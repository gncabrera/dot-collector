package com.nookx.api.web.rest;

import static com.nookx.api.domain.ProfileRequestAsserts.*;
import static com.nookx.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookx.api.IntegrationTest;
import com.nookx.api.domain.ProfileRequest;
import com.nookx.api.repository.ProfileRequestRepository;
import com.nookx.api.service.dto.ProfileRequestDTO;
import com.nookx.api.service.mapper.ProfileRequestMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ProfileRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProfileRequestResourceIT {

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/profile-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProfileRequestRepository profileRequestRepository;

    @Autowired
    private ProfileRequestMapper profileRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfileRequestMockMvc;

    private ProfileRequest profileRequest;

    private ProfileRequest insertedProfileRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileRequest createEntity() {
        return new ProfileRequest().message(DEFAULT_MESSAGE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileRequest createUpdatedEntity() {
        return new ProfileRequest().message(UPDATED_MESSAGE);
    }

    @BeforeEach
    void initTest() {
        profileRequest = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProfileRequest != null) {
            profileRequestRepository.delete(insertedProfileRequest);
            insertedProfileRequest = null;
        }
    }

    @Test
    @Transactional
    void createProfileRequest() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProfileRequest
        ProfileRequestDTO profileRequestDTO = profileRequestMapper.toDto(profileRequest);
        var returnedProfileRequestDTO = om.readValue(
            restProfileRequestMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileRequestDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProfileRequestDTO.class
        );

        // Validate the ProfileRequest in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProfileRequest = profileRequestMapper.toEntity(returnedProfileRequestDTO);
        assertProfileRequestUpdatableFieldsEquals(returnedProfileRequest, getPersistedProfileRequest(returnedProfileRequest));

        insertedProfileRequest = returnedProfileRequest;
    }

    @Test
    @Transactional
    void createProfileRequestWithExistingId() throws Exception {
        // Create the ProfileRequest with an existing ID
        profileRequest.setId(1L);
        ProfileRequestDTO profileRequestDTO = profileRequestMapper.toDto(profileRequest);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfileRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProfileRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProfileRequests() throws Exception {
        // Initialize the database
        insertedProfileRequest = profileRequestRepository.saveAndFlush(profileRequest);

        // Get all the profileRequestList
        restProfileRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profileRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)));
    }

    @Test
    @Transactional
    void getProfileRequest() throws Exception {
        // Initialize the database
        insertedProfileRequest = profileRequestRepository.saveAndFlush(profileRequest);

        // Get the profileRequest
        restProfileRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, profileRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(profileRequest.getId().intValue()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE));
    }

    @Test
    @Transactional
    void getNonExistingProfileRequest() throws Exception {
        // Get the profileRequest
        restProfileRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProfileRequest() throws Exception {
        // Initialize the database
        insertedProfileRequest = profileRequestRepository.saveAndFlush(profileRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileRequest
        ProfileRequest updatedProfileRequest = profileRequestRepository.findById(profileRequest.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProfileRequest are not directly saved in db
        em.detach(updatedProfileRequest);
        updatedProfileRequest.message(UPDATED_MESSAGE);
        ProfileRequestDTO profileRequestDTO = profileRequestMapper.toDto(updatedProfileRequest);

        restProfileRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProfileRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProfileRequestToMatchAllProperties(updatedProfileRequest);
    }

    @Test
    @Transactional
    void putNonExistingProfileRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequest.setId(longCount.incrementAndGet());

        // Create the ProfileRequest
        ProfileRequestDTO profileRequestDTO = profileRequestMapper.toDto(profileRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfileRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequest.setId(longCount.incrementAndGet());

        // Create the ProfileRequest
        ProfileRequestDTO profileRequestDTO = profileRequestMapper.toDto(profileRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfileRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequest.setId(longCount.incrementAndGet());

        // Create the ProfileRequest
        ProfileRequestDTO profileRequestDTO = profileRequestMapper.toDto(profileRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfileRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfileRequestWithPatch() throws Exception {
        // Initialize the database
        insertedProfileRequest = profileRequestRepository.saveAndFlush(profileRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileRequest using partial update
        ProfileRequest partialUpdatedProfileRequest = new ProfileRequest();
        partialUpdatedProfileRequest.setId(profileRequest.getId());

        restProfileRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfileRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfileRequest))
            )
            .andExpect(status().isOk());

        // Validate the ProfileRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileRequestUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProfileRequest, profileRequest),
            getPersistedProfileRequest(profileRequest)
        );
    }

    @Test
    @Transactional
    void fullUpdateProfileRequestWithPatch() throws Exception {
        // Initialize the database
        insertedProfileRequest = profileRequestRepository.saveAndFlush(profileRequest);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileRequest using partial update
        ProfileRequest partialUpdatedProfileRequest = new ProfileRequest();
        partialUpdatedProfileRequest.setId(profileRequest.getId());

        partialUpdatedProfileRequest.message(UPDATED_MESSAGE);

        restProfileRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfileRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfileRequest))
            )
            .andExpect(status().isOk());

        // Validate the ProfileRequest in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileRequestUpdatableFieldsEquals(partialUpdatedProfileRequest, getPersistedProfileRequest(partialUpdatedProfileRequest));
    }

    @Test
    @Transactional
    void patchNonExistingProfileRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequest.setId(longCount.incrementAndGet());

        // Create the ProfileRequest
        ProfileRequestDTO profileRequestDTO = profileRequestMapper.toDto(profileRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, profileRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfileRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequest.setId(longCount.incrementAndGet());

        // Create the ProfileRequest
        ProfileRequestDTO profileRequestDTO = profileRequestMapper.toDto(profileRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfileRequest() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequest.setId(longCount.incrementAndGet());

        // Create the ProfileRequest
        ProfileRequestDTO profileRequestDTO = profileRequestMapper.toDto(profileRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileRequestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(profileRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfileRequest in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfileRequest() throws Exception {
        // Initialize the database
        insertedProfileRequest = profileRequestRepository.saveAndFlush(profileRequest);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the profileRequest
        restProfileRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, profileRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return profileRequestRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ProfileRequest getPersistedProfileRequest(ProfileRequest profileRequest) {
        return profileRequestRepository.findById(profileRequest.getId()).orElseThrow();
    }

    protected void assertPersistedProfileRequestToMatchAllProperties(ProfileRequest expectedProfileRequest) {
        assertProfileRequestAllPropertiesEquals(expectedProfileRequest, getPersistedProfileRequest(expectedProfileRequest));
    }

    protected void assertPersistedProfileRequestToMatchUpdatableProperties(ProfileRequest expectedProfileRequest) {
        assertProfileRequestAllUpdatablePropertiesEquals(expectedProfileRequest, getPersistedProfileRequest(expectedProfileRequest));
    }
}
