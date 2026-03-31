package com.dot.collector.api.web.rest;

import static com.dot.collector.api.domain.BlockedProfileAsserts.*;
import static com.dot.collector.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dot.collector.api.IntegrationTest;
import com.dot.collector.api.domain.BlockedProfile;
import com.dot.collector.api.repository.BlockedProfileRepository;
import com.dot.collector.api.service.dto.BlockedProfileDTO;
import com.dot.collector.api.service.mapper.BlockedProfileMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link BlockedProfileResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BlockedProfileResourceIT {

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_BLOCKED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_BLOCKED = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/blocked-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BlockedProfileRepository blockedProfileRepository;

    @Autowired
    private BlockedProfileMapper blockedProfileMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBlockedProfileMockMvc;

    private BlockedProfile blockedProfile;

    private BlockedProfile insertedBlockedProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlockedProfile createEntity() {
        return new BlockedProfile().reason(DEFAULT_REASON).dateBlocked(DEFAULT_DATE_BLOCKED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BlockedProfile createUpdatedEntity() {
        return new BlockedProfile().reason(UPDATED_REASON).dateBlocked(UPDATED_DATE_BLOCKED);
    }

    @BeforeEach
    void initTest() {
        blockedProfile = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedBlockedProfile != null) {
            blockedProfileRepository.delete(insertedBlockedProfile);
            insertedBlockedProfile = null;
        }
    }

    @Test
    @Transactional
    void createBlockedProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BlockedProfile
        BlockedProfileDTO blockedProfileDTO = blockedProfileMapper.toDto(blockedProfile);
        var returnedBlockedProfileDTO = om.readValue(
            restBlockedProfileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blockedProfileDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BlockedProfileDTO.class
        );

        // Validate the BlockedProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBlockedProfile = blockedProfileMapper.toEntity(returnedBlockedProfileDTO);
        assertBlockedProfileUpdatableFieldsEquals(returnedBlockedProfile, getPersistedBlockedProfile(returnedBlockedProfile));

        insertedBlockedProfile = returnedBlockedProfile;
    }

    @Test
    @Transactional
    void createBlockedProfileWithExistingId() throws Exception {
        // Create the BlockedProfile with an existing ID
        blockedProfile.setId(1L);
        BlockedProfileDTO blockedProfileDTO = blockedProfileMapper.toDto(blockedProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlockedProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blockedProfileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BlockedProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBlockedProfiles() throws Exception {
        // Initialize the database
        insertedBlockedProfile = blockedProfileRepository.saveAndFlush(blockedProfile);

        // Get all the blockedProfileList
        restBlockedProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blockedProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].dateBlocked").value(hasItem(DEFAULT_DATE_BLOCKED.toString())));
    }

    @Test
    @Transactional
    void getBlockedProfile() throws Exception {
        // Initialize the database
        insertedBlockedProfile = blockedProfileRepository.saveAndFlush(blockedProfile);

        // Get the blockedProfile
        restBlockedProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, blockedProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(blockedProfile.getId().intValue()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.dateBlocked").value(DEFAULT_DATE_BLOCKED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBlockedProfile() throws Exception {
        // Get the blockedProfile
        restBlockedProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBlockedProfile() throws Exception {
        // Initialize the database
        insertedBlockedProfile = blockedProfileRepository.saveAndFlush(blockedProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the blockedProfile
        BlockedProfile updatedBlockedProfile = blockedProfileRepository.findById(blockedProfile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBlockedProfile are not directly saved in db
        em.detach(updatedBlockedProfile);
        updatedBlockedProfile.reason(UPDATED_REASON).dateBlocked(UPDATED_DATE_BLOCKED);
        BlockedProfileDTO blockedProfileDTO = blockedProfileMapper.toDto(updatedBlockedProfile);

        restBlockedProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blockedProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(blockedProfileDTO))
            )
            .andExpect(status().isOk());

        // Validate the BlockedProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBlockedProfileToMatchAllProperties(updatedBlockedProfile);
    }

    @Test
    @Transactional
    void putNonExistingBlockedProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blockedProfile.setId(longCount.incrementAndGet());

        // Create the BlockedProfile
        BlockedProfileDTO blockedProfileDTO = blockedProfileMapper.toDto(blockedProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlockedProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, blockedProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(blockedProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlockedProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBlockedProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blockedProfile.setId(longCount.incrementAndGet());

        // Create the BlockedProfile
        BlockedProfileDTO blockedProfileDTO = blockedProfileMapper.toDto(blockedProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockedProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(blockedProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlockedProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBlockedProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blockedProfile.setId(longCount.incrementAndGet());

        // Create the BlockedProfile
        BlockedProfileDTO blockedProfileDTO = blockedProfileMapper.toDto(blockedProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockedProfileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(blockedProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BlockedProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBlockedProfileWithPatch() throws Exception {
        // Initialize the database
        insertedBlockedProfile = blockedProfileRepository.saveAndFlush(blockedProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the blockedProfile using partial update
        BlockedProfile partialUpdatedBlockedProfile = new BlockedProfile();
        partialUpdatedBlockedProfile.setId(blockedProfile.getId());

        partialUpdatedBlockedProfile.reason(UPDATED_REASON).dateBlocked(UPDATED_DATE_BLOCKED);

        restBlockedProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlockedProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBlockedProfile))
            )
            .andExpect(status().isOk());

        // Validate the BlockedProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBlockedProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBlockedProfile, blockedProfile),
            getPersistedBlockedProfile(blockedProfile)
        );
    }

    @Test
    @Transactional
    void fullUpdateBlockedProfileWithPatch() throws Exception {
        // Initialize the database
        insertedBlockedProfile = blockedProfileRepository.saveAndFlush(blockedProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the blockedProfile using partial update
        BlockedProfile partialUpdatedBlockedProfile = new BlockedProfile();
        partialUpdatedBlockedProfile.setId(blockedProfile.getId());

        partialUpdatedBlockedProfile.reason(UPDATED_REASON).dateBlocked(UPDATED_DATE_BLOCKED);

        restBlockedProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBlockedProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBlockedProfile))
            )
            .andExpect(status().isOk());

        // Validate the BlockedProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBlockedProfileUpdatableFieldsEquals(partialUpdatedBlockedProfile, getPersistedBlockedProfile(partialUpdatedBlockedProfile));
    }

    @Test
    @Transactional
    void patchNonExistingBlockedProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blockedProfile.setId(longCount.incrementAndGet());

        // Create the BlockedProfile
        BlockedProfileDTO blockedProfileDTO = blockedProfileMapper.toDto(blockedProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBlockedProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, blockedProfileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(blockedProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlockedProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBlockedProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blockedProfile.setId(longCount.incrementAndGet());

        // Create the BlockedProfile
        BlockedProfileDTO blockedProfileDTO = blockedProfileMapper.toDto(blockedProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockedProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(blockedProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BlockedProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBlockedProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        blockedProfile.setId(longCount.incrementAndGet());

        // Create the BlockedProfile
        BlockedProfileDTO blockedProfileDTO = blockedProfileMapper.toDto(blockedProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBlockedProfileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(blockedProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BlockedProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBlockedProfile() throws Exception {
        // Initialize the database
        insertedBlockedProfile = blockedProfileRepository.saveAndFlush(blockedProfile);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the blockedProfile
        restBlockedProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, blockedProfile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return blockedProfileRepository.count();
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

    protected BlockedProfile getPersistedBlockedProfile(BlockedProfile blockedProfile) {
        return blockedProfileRepository.findById(blockedProfile.getId()).orElseThrow();
    }

    protected void assertPersistedBlockedProfileToMatchAllProperties(BlockedProfile expectedBlockedProfile) {
        assertBlockedProfileAllPropertiesEquals(expectedBlockedProfile, getPersistedBlockedProfile(expectedBlockedProfile));
    }

    protected void assertPersistedBlockedProfileToMatchUpdatableProperties(BlockedProfile expectedBlockedProfile) {
        assertBlockedProfileAllUpdatablePropertiesEquals(expectedBlockedProfile, getPersistedBlockedProfile(expectedBlockedProfile));
    }
}
