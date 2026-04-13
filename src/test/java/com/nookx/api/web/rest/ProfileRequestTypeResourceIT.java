package com.nookx.api.web.rest;

import static com.nookx.api.domain.ProfileRequestTypeAsserts.*;
import static com.nookx.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookx.api.IntegrationTest;
import com.nookx.api.domain.ProfileRequestType;
import com.nookx.api.repository.ProfileRequestTypeRepository;
import com.nookx.api.service.dto.ProfileRequestTypeDTO;
import com.nookx.api.service.mapper.ProfileRequestTypeMapper;
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
 * Integration tests for the {@link ProfileRequestTypeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProfileRequestTypeResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/profile-request-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProfileRequestTypeRepository profileRequestTypeRepository;

    @Autowired
    private ProfileRequestTypeMapper profileRequestTypeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfileRequestTypeMockMvc;

    private ProfileRequestType profileRequestType;

    private ProfileRequestType insertedProfileRequestType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileRequestType createEntity() {
        return new ProfileRequestType().key(DEFAULT_KEY).name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileRequestType createUpdatedEntity() {
        return new ProfileRequestType().key(UPDATED_KEY).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        profileRequestType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProfileRequestType != null) {
            profileRequestTypeRepository.delete(insertedProfileRequestType);
            insertedProfileRequestType = null;
        }
    }

    @Test
    @Transactional
    void createProfileRequestType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProfileRequestType
        ProfileRequestTypeDTO profileRequestTypeDTO = profileRequestTypeMapper.toDto(profileRequestType);
        var returnedProfileRequestTypeDTO = om.readValue(
            restProfileRequestTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileRequestTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProfileRequestTypeDTO.class
        );

        // Validate the ProfileRequestType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProfileRequestType = profileRequestTypeMapper.toEntity(returnedProfileRequestTypeDTO);
        assertProfileRequestTypeUpdatableFieldsEquals(
            returnedProfileRequestType,
            getPersistedProfileRequestType(returnedProfileRequestType)
        );

        insertedProfileRequestType = returnedProfileRequestType;
    }

    @Test
    @Transactional
    void createProfileRequestTypeWithExistingId() throws Exception {
        // Create the ProfileRequestType with an existing ID
        profileRequestType.setId(1L);
        ProfileRequestTypeDTO profileRequestTypeDTO = profileRequestTypeMapper.toDto(profileRequestType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfileRequestTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileRequestTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProfileRequestType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProfileRequestTypes() throws Exception {
        // Initialize the database
        insertedProfileRequestType = profileRequestTypeRepository.saveAndFlush(profileRequestType);

        // Get all the profileRequestTypeList
        restProfileRequestTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profileRequestType.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getProfileRequestType() throws Exception {
        // Initialize the database
        insertedProfileRequestType = profileRequestTypeRepository.saveAndFlush(profileRequestType);

        // Get the profileRequestType
        restProfileRequestTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, profileRequestType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(profileRequestType.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingProfileRequestType() throws Exception {
        // Get the profileRequestType
        restProfileRequestTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProfileRequestType() throws Exception {
        // Initialize the database
        insertedProfileRequestType = profileRequestTypeRepository.saveAndFlush(profileRequestType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileRequestType
        ProfileRequestType updatedProfileRequestType = profileRequestTypeRepository.findById(profileRequestType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProfileRequestType are not directly saved in db
        em.detach(updatedProfileRequestType);
        updatedProfileRequestType.key(UPDATED_KEY).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        ProfileRequestTypeDTO profileRequestTypeDTO = profileRequestTypeMapper.toDto(updatedProfileRequestType);

        restProfileRequestTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileRequestTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileRequestTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProfileRequestType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProfileRequestTypeToMatchAllProperties(updatedProfileRequestType);
    }

    @Test
    @Transactional
    void putNonExistingProfileRequestType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequestType.setId(longCount.incrementAndGet());

        // Create the ProfileRequestType
        ProfileRequestTypeDTO profileRequestTypeDTO = profileRequestTypeMapper.toDto(profileRequestType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileRequestTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileRequestTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileRequestTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileRequestType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfileRequestType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequestType.setId(longCount.incrementAndGet());

        // Create the ProfileRequestType
        ProfileRequestTypeDTO profileRequestTypeDTO = profileRequestTypeMapper.toDto(profileRequestType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileRequestTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileRequestTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileRequestType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfileRequestType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequestType.setId(longCount.incrementAndGet());

        // Create the ProfileRequestType
        ProfileRequestTypeDTO profileRequestTypeDTO = profileRequestTypeMapper.toDto(profileRequestType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileRequestTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileRequestTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfileRequestType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfileRequestTypeWithPatch() throws Exception {
        // Initialize the database
        insertedProfileRequestType = profileRequestTypeRepository.saveAndFlush(profileRequestType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileRequestType using partial update
        ProfileRequestType partialUpdatedProfileRequestType = new ProfileRequestType();
        partialUpdatedProfileRequestType.setId(profileRequestType.getId());

        partialUpdatedProfileRequestType.description(UPDATED_DESCRIPTION);

        restProfileRequestTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfileRequestType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfileRequestType))
            )
            .andExpect(status().isOk());

        // Validate the ProfileRequestType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileRequestTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProfileRequestType, profileRequestType),
            getPersistedProfileRequestType(profileRequestType)
        );
    }

    @Test
    @Transactional
    void fullUpdateProfileRequestTypeWithPatch() throws Exception {
        // Initialize the database
        insertedProfileRequestType = profileRequestTypeRepository.saveAndFlush(profileRequestType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileRequestType using partial update
        ProfileRequestType partialUpdatedProfileRequestType = new ProfileRequestType();
        partialUpdatedProfileRequestType.setId(profileRequestType.getId());

        partialUpdatedProfileRequestType.key(UPDATED_KEY).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restProfileRequestTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfileRequestType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfileRequestType))
            )
            .andExpect(status().isOk());

        // Validate the ProfileRequestType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileRequestTypeUpdatableFieldsEquals(
            partialUpdatedProfileRequestType,
            getPersistedProfileRequestType(partialUpdatedProfileRequestType)
        );
    }

    @Test
    @Transactional
    void patchNonExistingProfileRequestType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequestType.setId(longCount.incrementAndGet());

        // Create the ProfileRequestType
        ProfileRequestTypeDTO profileRequestTypeDTO = profileRequestTypeMapper.toDto(profileRequestType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileRequestTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, profileRequestTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileRequestTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileRequestType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfileRequestType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequestType.setId(longCount.incrementAndGet());

        // Create the ProfileRequestType
        ProfileRequestTypeDTO profileRequestTypeDTO = profileRequestTypeMapper.toDto(profileRequestType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileRequestTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileRequestTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileRequestType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfileRequestType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileRequestType.setId(longCount.incrementAndGet());

        // Create the ProfileRequestType
        ProfileRequestTypeDTO profileRequestTypeDTO = profileRequestTypeMapper.toDto(profileRequestType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileRequestTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(profileRequestTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfileRequestType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfileRequestType() throws Exception {
        // Initialize the database
        insertedProfileRequestType = profileRequestTypeRepository.saveAndFlush(profileRequestType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the profileRequestType
        restProfileRequestTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, profileRequestType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return profileRequestTypeRepository.count();
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

    protected ProfileRequestType getPersistedProfileRequestType(ProfileRequestType profileRequestType) {
        return profileRequestTypeRepository.findById(profileRequestType.getId()).orElseThrow();
    }

    protected void assertPersistedProfileRequestTypeToMatchAllProperties(ProfileRequestType expectedProfileRequestType) {
        assertProfileRequestTypeAllPropertiesEquals(expectedProfileRequestType, getPersistedProfileRequestType(expectedProfileRequestType));
    }

    protected void assertPersistedProfileRequestTypeToMatchUpdatableProperties(ProfileRequestType expectedProfileRequestType) {
        assertProfileRequestTypeAllUpdatablePropertiesEquals(
            expectedProfileRequestType,
            getPersistedProfileRequestType(expectedProfileRequestType)
        );
    }
}
