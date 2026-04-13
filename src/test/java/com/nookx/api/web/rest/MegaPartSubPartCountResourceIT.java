package com.nookx.api.web.rest;

import static com.nookx.api.domain.MegaPartSubPartCountAsserts.*;
import static com.nookx.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookx.api.IntegrationTest;
import com.nookx.api.domain.MegaPartSubPartCount;
import com.nookx.api.repository.MegaPartSubPartCountRepository;
import com.nookx.api.service.dto.MegaPartSubPartCountDTO;
import com.nookx.api.service.mapper.MegaPartSubPartCountMapper;
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
 * Integration tests for the {@link MegaPartSubPartCountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MegaPartSubPartCountResourceIT {

    private static final Integer DEFAULT_COUNT = 1;
    private static final Integer UPDATED_COUNT = 2;

    private static final String ENTITY_API_URL = "/api/mega-part-sub-part-counts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MegaPartSubPartCountRepository megaPartSubPartCountRepository;

    @Autowired
    private MegaPartSubPartCountMapper megaPartSubPartCountMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMegaPartSubPartCountMockMvc;

    private MegaPartSubPartCount megaPartSubPartCount;

    private MegaPartSubPartCount insertedMegaPartSubPartCount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaPartSubPartCount createEntity() {
        return new MegaPartSubPartCount().count(DEFAULT_COUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaPartSubPartCount createUpdatedEntity() {
        return new MegaPartSubPartCount().count(UPDATED_COUNT);
    }

    @BeforeEach
    void initTest() {
        megaPartSubPartCount = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMegaPartSubPartCount != null) {
            megaPartSubPartCountRepository.delete(insertedMegaPartSubPartCount);
            insertedMegaPartSubPartCount = null;
        }
    }

    @Test
    @Transactional
    void createMegaPartSubPartCount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MegaPartSubPartCount
        MegaPartSubPartCountDTO megaPartSubPartCountDTO = megaPartSubPartCountMapper.toDto(megaPartSubPartCount);
        var returnedMegaPartSubPartCountDTO = om.readValue(
            restMegaPartSubPartCountMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaPartSubPartCountDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MegaPartSubPartCountDTO.class
        );

        // Validate the MegaPartSubPartCount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMegaPartSubPartCount = megaPartSubPartCountMapper.toEntity(returnedMegaPartSubPartCountDTO);
        assertMegaPartSubPartCountUpdatableFieldsEquals(
            returnedMegaPartSubPartCount,
            getPersistedMegaPartSubPartCount(returnedMegaPartSubPartCount)
        );

        insertedMegaPartSubPartCount = returnedMegaPartSubPartCount;
    }

    @Test
    @Transactional
    void createMegaPartSubPartCountWithExistingId() throws Exception {
        // Create the MegaPartSubPartCount with an existing ID
        megaPartSubPartCount.setId(1L);
        MegaPartSubPartCountDTO megaPartSubPartCountDTO = megaPartSubPartCountMapper.toDto(megaPartSubPartCount);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMegaPartSubPartCountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaPartSubPartCountDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MegaPartSubPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMegaPartSubPartCounts() throws Exception {
        // Initialize the database
        insertedMegaPartSubPartCount = megaPartSubPartCountRepository.saveAndFlush(megaPartSubPartCount);

        // Get all the megaPartSubPartCountList
        restMegaPartSubPartCountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(megaPartSubPartCount.getId().intValue())))
            .andExpect(jsonPath("$.[*].count").value(hasItem(DEFAULT_COUNT)));
    }

    @Test
    @Transactional
    void getMegaPartSubPartCount() throws Exception {
        // Initialize the database
        insertedMegaPartSubPartCount = megaPartSubPartCountRepository.saveAndFlush(megaPartSubPartCount);

        // Get the megaPartSubPartCount
        restMegaPartSubPartCountMockMvc
            .perform(get(ENTITY_API_URL_ID, megaPartSubPartCount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(megaPartSubPartCount.getId().intValue()))
            .andExpect(jsonPath("$.count").value(DEFAULT_COUNT));
    }

    @Test
    @Transactional
    void getNonExistingMegaPartSubPartCount() throws Exception {
        // Get the megaPartSubPartCount
        restMegaPartSubPartCountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMegaPartSubPartCount() throws Exception {
        // Initialize the database
        insertedMegaPartSubPartCount = megaPartSubPartCountRepository.saveAndFlush(megaPartSubPartCount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaPartSubPartCount
        MegaPartSubPartCount updatedMegaPartSubPartCount = megaPartSubPartCountRepository
            .findById(megaPartSubPartCount.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedMegaPartSubPartCount are not directly saved in db
        em.detach(updatedMegaPartSubPartCount);
        updatedMegaPartSubPartCount.count(UPDATED_COUNT);
        MegaPartSubPartCountDTO megaPartSubPartCountDTO = megaPartSubPartCountMapper.toDto(updatedMegaPartSubPartCount);

        restMegaPartSubPartCountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaPartSubPartCountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaPartSubPartCountDTO))
            )
            .andExpect(status().isOk());

        // Validate the MegaPartSubPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMegaPartSubPartCountToMatchAllProperties(updatedMegaPartSubPartCount);
    }

    @Test
    @Transactional
    void putNonExistingMegaPartSubPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartSubPartCount.setId(longCount.incrementAndGet());

        // Create the MegaPartSubPartCount
        MegaPartSubPartCountDTO megaPartSubPartCountDTO = megaPartSubPartCountMapper.toDto(megaPartSubPartCount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaPartSubPartCountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaPartSubPartCountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaPartSubPartCountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaPartSubPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMegaPartSubPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartSubPartCount.setId(longCount.incrementAndGet());

        // Create the MegaPartSubPartCount
        MegaPartSubPartCountDTO megaPartSubPartCountDTO = megaPartSubPartCountMapper.toDto(megaPartSubPartCount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaPartSubPartCountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaPartSubPartCountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaPartSubPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMegaPartSubPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartSubPartCount.setId(longCount.incrementAndGet());

        // Create the MegaPartSubPartCount
        MegaPartSubPartCountDTO megaPartSubPartCountDTO = megaPartSubPartCountMapper.toDto(megaPartSubPartCount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaPartSubPartCountMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaPartSubPartCountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaPartSubPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMegaPartSubPartCountWithPatch() throws Exception {
        // Initialize the database
        insertedMegaPartSubPartCount = megaPartSubPartCountRepository.saveAndFlush(megaPartSubPartCount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaPartSubPartCount using partial update
        MegaPartSubPartCount partialUpdatedMegaPartSubPartCount = new MegaPartSubPartCount();
        partialUpdatedMegaPartSubPartCount.setId(megaPartSubPartCount.getId());

        partialUpdatedMegaPartSubPartCount.count(UPDATED_COUNT);

        restMegaPartSubPartCountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaPartSubPartCount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaPartSubPartCount))
            )
            .andExpect(status().isOk());

        // Validate the MegaPartSubPartCount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaPartSubPartCountUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMegaPartSubPartCount, megaPartSubPartCount),
            getPersistedMegaPartSubPartCount(megaPartSubPartCount)
        );
    }

    @Test
    @Transactional
    void fullUpdateMegaPartSubPartCountWithPatch() throws Exception {
        // Initialize the database
        insertedMegaPartSubPartCount = megaPartSubPartCountRepository.saveAndFlush(megaPartSubPartCount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaPartSubPartCount using partial update
        MegaPartSubPartCount partialUpdatedMegaPartSubPartCount = new MegaPartSubPartCount();
        partialUpdatedMegaPartSubPartCount.setId(megaPartSubPartCount.getId());

        partialUpdatedMegaPartSubPartCount.count(UPDATED_COUNT);

        restMegaPartSubPartCountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaPartSubPartCount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaPartSubPartCount))
            )
            .andExpect(status().isOk());

        // Validate the MegaPartSubPartCount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaPartSubPartCountUpdatableFieldsEquals(
            partialUpdatedMegaPartSubPartCount,
            getPersistedMegaPartSubPartCount(partialUpdatedMegaPartSubPartCount)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMegaPartSubPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartSubPartCount.setId(longCount.incrementAndGet());

        // Create the MegaPartSubPartCount
        MegaPartSubPartCountDTO megaPartSubPartCountDTO = megaPartSubPartCountMapper.toDto(megaPartSubPartCount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaPartSubPartCountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, megaPartSubPartCountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaPartSubPartCountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaPartSubPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMegaPartSubPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartSubPartCount.setId(longCount.incrementAndGet());

        // Create the MegaPartSubPartCount
        MegaPartSubPartCountDTO megaPartSubPartCountDTO = megaPartSubPartCountMapper.toDto(megaPartSubPartCount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaPartSubPartCountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaPartSubPartCountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaPartSubPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMegaPartSubPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartSubPartCount.setId(longCount.incrementAndGet());

        // Create the MegaPartSubPartCount
        MegaPartSubPartCountDTO megaPartSubPartCountDTO = megaPartSubPartCountMapper.toDto(megaPartSubPartCount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaPartSubPartCountMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(megaPartSubPartCountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaPartSubPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMegaPartSubPartCount() throws Exception {
        // Initialize the database
        insertedMegaPartSubPartCount = megaPartSubPartCountRepository.saveAndFlush(megaPartSubPartCount);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the megaPartSubPartCount
        restMegaPartSubPartCountMockMvc
            .perform(delete(ENTITY_API_URL_ID, megaPartSubPartCount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return megaPartSubPartCountRepository.count();
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

    protected MegaPartSubPartCount getPersistedMegaPartSubPartCount(MegaPartSubPartCount megaPartSubPartCount) {
        return megaPartSubPartCountRepository.findById(megaPartSubPartCount.getId()).orElseThrow();
    }

    protected void assertPersistedMegaPartSubPartCountToMatchAllProperties(MegaPartSubPartCount expectedMegaPartSubPartCount) {
        assertMegaPartSubPartCountAllPropertiesEquals(
            expectedMegaPartSubPartCount,
            getPersistedMegaPartSubPartCount(expectedMegaPartSubPartCount)
        );
    }

    protected void assertPersistedMegaPartSubPartCountToMatchUpdatableProperties(MegaPartSubPartCount expectedMegaPartSubPartCount) {
        assertMegaPartSubPartCountAllUpdatablePropertiesEquals(
            expectedMegaPartSubPartCount,
            getPersistedMegaPartSubPartCount(expectedMegaPartSubPartCount)
        );
    }
}
