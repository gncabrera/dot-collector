package com.nookx.api.web.rest;

import static com.nookx.api.domain.FollowingProfileAsserts.*;
import static com.nookx.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookx.api.IntegrationTest;
import com.nookx.api.domain.FollowingProfile;
import com.nookx.api.repository.FollowingProfileRepository;
import com.nookx.api.service.dto.FollowingProfileDTO;
import com.nookx.api.service.mapper.FollowingProfileMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link FollowingProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FollowingProfileResourceIT {

    private static final LocalDate DEFAULT_DATE_FOLLOWING = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_FOLLOWING = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/following-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FollowingProfileRepository followingProfileRepository;

    @Autowired
    private FollowingProfileMapper followingProfileMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFollowingProfileMockMvc;

    private FollowingProfile followingProfile;

    private FollowingProfile insertedFollowingProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FollowingProfile createEntity() {
        return new FollowingProfile().dateFollowing(DEFAULT_DATE_FOLLOWING);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FollowingProfile createUpdatedEntity() {
        return new FollowingProfile().dateFollowing(UPDATED_DATE_FOLLOWING);
    }

    @BeforeEach
    void initTest() {
        followingProfile = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFollowingProfile != null) {
            followingProfileRepository.delete(insertedFollowingProfile);
            insertedFollowingProfile = null;
        }
    }

    @Test
    @Transactional
    void createFollowingProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FollowingProfile
        FollowingProfileDTO followingProfileDTO = followingProfileMapper.toDto(followingProfile);
        var returnedFollowingProfileDTO = om.readValue(
            restFollowingProfileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followingProfileDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FollowingProfileDTO.class
        );

        // Validate the FollowingProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFollowingProfile = followingProfileMapper.toEntity(returnedFollowingProfileDTO);
        assertFollowingProfileUpdatableFieldsEquals(returnedFollowingProfile, getPersistedFollowingProfile(returnedFollowingProfile));

        insertedFollowingProfile = returnedFollowingProfile;
    }

    @Test
    @Transactional
    void createFollowingProfileWithExistingId() throws Exception {
        // Create the FollowingProfile with an existing ID
        followingProfile.setId(1L);
        FollowingProfileDTO followingProfileDTO = followingProfileMapper.toDto(followingProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFollowingProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followingProfileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FollowingProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFollowingProfiles() throws Exception {
        // Initialize the database
        insertedFollowingProfile = followingProfileRepository.saveAndFlush(followingProfile);

        // Get all the followingProfileList
        restFollowingProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(followingProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].dateFollowing").value(hasItem(DEFAULT_DATE_FOLLOWING.toString())));
    }

    @Test
    @Transactional
    void getFollowingProfile() throws Exception {
        // Initialize the database
        insertedFollowingProfile = followingProfileRepository.saveAndFlush(followingProfile);

        // Get the followingProfile
        restFollowingProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, followingProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(followingProfile.getId().intValue()))
            .andExpect(jsonPath("$.dateFollowing").value(DEFAULT_DATE_FOLLOWING.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFollowingProfile() throws Exception {
        // Get the followingProfile
        restFollowingProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFollowingProfile() throws Exception {
        // Initialize the database
        insertedFollowingProfile = followingProfileRepository.saveAndFlush(followingProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the followingProfile
        FollowingProfile updatedFollowingProfile = followingProfileRepository.findById(followingProfile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFollowingProfile are not directly saved in db
        em.detach(updatedFollowingProfile);
        updatedFollowingProfile.dateFollowing(UPDATED_DATE_FOLLOWING);
        FollowingProfileDTO followingProfileDTO = followingProfileMapper.toDto(updatedFollowingProfile);

        restFollowingProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followingProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(followingProfileDTO))
            )
            .andExpect(status().isOk());

        // Validate the FollowingProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFollowingProfileToMatchAllProperties(updatedFollowingProfile);
    }

    @Test
    @Transactional
    void putNonExistingFollowingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followingProfile.setId(longCount.incrementAndGet());

        // Create the FollowingProfile
        FollowingProfileDTO followingProfileDTO = followingProfileMapper.toDto(followingProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowingProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, followingProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(followingProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowingProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFollowingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followingProfile.setId(longCount.incrementAndGet());

        // Create the FollowingProfile
        FollowingProfileDTO followingProfileDTO = followingProfileMapper.toDto(followingProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowingProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(followingProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowingProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFollowingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followingProfile.setId(longCount.incrementAndGet());

        // Create the FollowingProfile
        FollowingProfileDTO followingProfileDTO = followingProfileMapper.toDto(followingProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowingProfileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(followingProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FollowingProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFollowingProfileWithPatch() throws Exception {
        // Initialize the database
        insertedFollowingProfile = followingProfileRepository.saveAndFlush(followingProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the followingProfile using partial update
        FollowingProfile partialUpdatedFollowingProfile = new FollowingProfile();
        partialUpdatedFollowingProfile.setId(followingProfile.getId());

        partialUpdatedFollowingProfile.dateFollowing(UPDATED_DATE_FOLLOWING);

        restFollowingProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollowingProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFollowingProfile))
            )
            .andExpect(status().isOk());

        // Validate the FollowingProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFollowingProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFollowingProfile, followingProfile),
            getPersistedFollowingProfile(followingProfile)
        );
    }

    @Test
    @Transactional
    void fullUpdateFollowingProfileWithPatch() throws Exception {
        // Initialize the database
        insertedFollowingProfile = followingProfileRepository.saveAndFlush(followingProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the followingProfile using partial update
        FollowingProfile partialUpdatedFollowingProfile = new FollowingProfile();
        partialUpdatedFollowingProfile.setId(followingProfile.getId());

        partialUpdatedFollowingProfile.dateFollowing(UPDATED_DATE_FOLLOWING);

        restFollowingProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFollowingProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFollowingProfile))
            )
            .andExpect(status().isOk());

        // Validate the FollowingProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFollowingProfileUpdatableFieldsEquals(
            partialUpdatedFollowingProfile,
            getPersistedFollowingProfile(partialUpdatedFollowingProfile)
        );
    }

    @Test
    @Transactional
    void patchNonExistingFollowingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followingProfile.setId(longCount.incrementAndGet());

        // Create the FollowingProfile
        FollowingProfileDTO followingProfileDTO = followingProfileMapper.toDto(followingProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFollowingProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, followingProfileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(followingProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowingProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFollowingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followingProfile.setId(longCount.incrementAndGet());

        // Create the FollowingProfile
        FollowingProfileDTO followingProfileDTO = followingProfileMapper.toDto(followingProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowingProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(followingProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FollowingProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFollowingProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        followingProfile.setId(longCount.incrementAndGet());

        // Create the FollowingProfile
        FollowingProfileDTO followingProfileDTO = followingProfileMapper.toDto(followingProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFollowingProfileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(followingProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FollowingProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFollowingProfile() throws Exception {
        // Initialize the database
        insertedFollowingProfile = followingProfileRepository.saveAndFlush(followingProfile);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the followingProfile
        restFollowingProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, followingProfile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return followingProfileRepository.count();
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

    protected FollowingProfile getPersistedFollowingProfile(FollowingProfile followingProfile) {
        return followingProfileRepository.findById(followingProfile.getId()).orElseThrow();
    }

    protected void assertPersistedFollowingProfileToMatchAllProperties(FollowingProfile expectedFollowingProfile) {
        assertFollowingProfileAllPropertiesEquals(expectedFollowingProfile, getPersistedFollowingProfile(expectedFollowingProfile));
    }

    protected void assertPersistedFollowingProfileToMatchUpdatableProperties(FollowingProfile expectedFollowingProfile) {
        assertFollowingProfileAllUpdatablePropertiesEquals(
            expectedFollowingProfile,
            getPersistedFollowingProfile(expectedFollowingProfile)
        );
    }
}
