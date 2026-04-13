package com.nookx.api.web.rest;

import static com.nookx.api.domain.MegaSetPartCountAsserts.*;
import static com.nookx.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookx.api.IntegrationTest;
import com.nookx.api.domain.MegaSetPartCount;
import com.nookx.api.repository.MegaSetPartCountRepository;
import com.nookx.api.service.dto.MegaSetPartCountDTO;
import com.nookx.api.service.mapper.MegaSetPartCountMapper;
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
 * Integration tests for the {@link MegaSetPartCountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MegaSetPartCountResourceIT {

    private static final Integer DEFAULT_COUNT = 1;
    private static final Integer UPDATED_COUNT = 2;

    private static final String ENTITY_API_URL = "/api/mega-set-part-counts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MegaSetPartCountRepository megaSetPartCountRepository;

    @Autowired
    private MegaSetPartCountMapper megaSetPartCountMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMegaSetPartCountMockMvc;

    private MegaSetPartCount megaSetPartCount;

    private MegaSetPartCount insertedMegaSetPartCount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaSetPartCount createEntity() {
        return new MegaSetPartCount().count(DEFAULT_COUNT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaSetPartCount createUpdatedEntity() {
        return new MegaSetPartCount().count(UPDATED_COUNT);
    }

    @BeforeEach
    void initTest() {
        megaSetPartCount = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMegaSetPartCount != null) {
            megaSetPartCountRepository.delete(insertedMegaSetPartCount);
            insertedMegaSetPartCount = null;
        }
    }

    @Test
    @Transactional
    void createMegaSetPartCount() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MegaSetPartCount
        MegaSetPartCountDTO megaSetPartCountDTO = megaSetPartCountMapper.toDto(megaSetPartCount);
        var returnedMegaSetPartCountDTO = om.readValue(
            restMegaSetPartCountMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaSetPartCountDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MegaSetPartCountDTO.class
        );

        // Validate the MegaSetPartCount in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMegaSetPartCount = megaSetPartCountMapper.toEntity(returnedMegaSetPartCountDTO);
        assertMegaSetPartCountUpdatableFieldsEquals(returnedMegaSetPartCount, getPersistedMegaSetPartCount(returnedMegaSetPartCount));

        insertedMegaSetPartCount = returnedMegaSetPartCount;
    }

    @Test
    @Transactional
    void createMegaSetPartCountWithExistingId() throws Exception {
        // Create the MegaSetPartCount with an existing ID
        megaSetPartCount.setId(1L);
        MegaSetPartCountDTO megaSetPartCountDTO = megaSetPartCountMapper.toDto(megaSetPartCount);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMegaSetPartCountMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaSetPartCountDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MegaSetPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMegaSetPartCounts() throws Exception {
        // Initialize the database
        insertedMegaSetPartCount = megaSetPartCountRepository.saveAndFlush(megaSetPartCount);

        // Get all the megaSetPartCountList
        restMegaSetPartCountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(megaSetPartCount.getId().intValue())))
            .andExpect(jsonPath("$.[*].count").value(hasItem(DEFAULT_COUNT)));
    }

    @Test
    @Transactional
    void getMegaSetPartCount() throws Exception {
        // Initialize the database
        insertedMegaSetPartCount = megaSetPartCountRepository.saveAndFlush(megaSetPartCount);

        // Get the megaSetPartCount
        restMegaSetPartCountMockMvc
            .perform(get(ENTITY_API_URL_ID, megaSetPartCount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(megaSetPartCount.getId().intValue()))
            .andExpect(jsonPath("$.count").value(DEFAULT_COUNT));
    }

    @Test
    @Transactional
    void getNonExistingMegaSetPartCount() throws Exception {
        // Get the megaSetPartCount
        restMegaSetPartCountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMegaSetPartCount() throws Exception {
        // Initialize the database
        insertedMegaSetPartCount = megaSetPartCountRepository.saveAndFlush(megaSetPartCount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaSetPartCount
        MegaSetPartCount updatedMegaSetPartCount = megaSetPartCountRepository.findById(megaSetPartCount.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMegaSetPartCount are not directly saved in db
        em.detach(updatedMegaSetPartCount);
        updatedMegaSetPartCount.count(UPDATED_COUNT);
        MegaSetPartCountDTO megaSetPartCountDTO = megaSetPartCountMapper.toDto(updatedMegaSetPartCount);

        restMegaSetPartCountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaSetPartCountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaSetPartCountDTO))
            )
            .andExpect(status().isOk());

        // Validate the MegaSetPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMegaSetPartCountToMatchAllProperties(updatedMegaSetPartCount);
    }

    @Test
    @Transactional
    void putNonExistingMegaSetPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetPartCount.setId(longCount.incrementAndGet());

        // Create the MegaSetPartCount
        MegaSetPartCountDTO megaSetPartCountDTO = megaSetPartCountMapper.toDto(megaSetPartCount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaSetPartCountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaSetPartCountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaSetPartCountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaSetPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMegaSetPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetPartCount.setId(longCount.incrementAndGet());

        // Create the MegaSetPartCount
        MegaSetPartCountDTO megaSetPartCountDTO = megaSetPartCountMapper.toDto(megaSetPartCount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaSetPartCountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaSetPartCountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaSetPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMegaSetPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetPartCount.setId(longCount.incrementAndGet());

        // Create the MegaSetPartCount
        MegaSetPartCountDTO megaSetPartCountDTO = megaSetPartCountMapper.toDto(megaSetPartCount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaSetPartCountMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaSetPartCountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaSetPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMegaSetPartCountWithPatch() throws Exception {
        // Initialize the database
        insertedMegaSetPartCount = megaSetPartCountRepository.saveAndFlush(megaSetPartCount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaSetPartCount using partial update
        MegaSetPartCount partialUpdatedMegaSetPartCount = new MegaSetPartCount();
        partialUpdatedMegaSetPartCount.setId(megaSetPartCount.getId());

        restMegaSetPartCountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaSetPartCount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaSetPartCount))
            )
            .andExpect(status().isOk());

        // Validate the MegaSetPartCount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaSetPartCountUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMegaSetPartCount, megaSetPartCount),
            getPersistedMegaSetPartCount(megaSetPartCount)
        );
    }

    @Test
    @Transactional
    void fullUpdateMegaSetPartCountWithPatch() throws Exception {
        // Initialize the database
        insertedMegaSetPartCount = megaSetPartCountRepository.saveAndFlush(megaSetPartCount);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaSetPartCount using partial update
        MegaSetPartCount partialUpdatedMegaSetPartCount = new MegaSetPartCount();
        partialUpdatedMegaSetPartCount.setId(megaSetPartCount.getId());

        partialUpdatedMegaSetPartCount.count(UPDATED_COUNT);

        restMegaSetPartCountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaSetPartCount.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaSetPartCount))
            )
            .andExpect(status().isOk());

        // Validate the MegaSetPartCount in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaSetPartCountUpdatableFieldsEquals(
            partialUpdatedMegaSetPartCount,
            getPersistedMegaSetPartCount(partialUpdatedMegaSetPartCount)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMegaSetPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetPartCount.setId(longCount.incrementAndGet());

        // Create the MegaSetPartCount
        MegaSetPartCountDTO megaSetPartCountDTO = megaSetPartCountMapper.toDto(megaSetPartCount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaSetPartCountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, megaSetPartCountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaSetPartCountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaSetPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMegaSetPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetPartCount.setId(longCount.incrementAndGet());

        // Create the MegaSetPartCount
        MegaSetPartCountDTO megaSetPartCountDTO = megaSetPartCountMapper.toDto(megaSetPartCount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaSetPartCountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaSetPartCountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaSetPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMegaSetPartCount() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetPartCount.setId(longCount.incrementAndGet());

        // Create the MegaSetPartCount
        MegaSetPartCountDTO megaSetPartCountDTO = megaSetPartCountMapper.toDto(megaSetPartCount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaSetPartCountMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(megaSetPartCountDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaSetPartCount in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMegaSetPartCount() throws Exception {
        // Initialize the database
        insertedMegaSetPartCount = megaSetPartCountRepository.saveAndFlush(megaSetPartCount);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the megaSetPartCount
        restMegaSetPartCountMockMvc
            .perform(delete(ENTITY_API_URL_ID, megaSetPartCount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return megaSetPartCountRepository.count();
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

    protected MegaSetPartCount getPersistedMegaSetPartCount(MegaSetPartCount megaSetPartCount) {
        return megaSetPartCountRepository.findById(megaSetPartCount.getId()).orElseThrow();
    }

    protected void assertPersistedMegaSetPartCountToMatchAllProperties(MegaSetPartCount expectedMegaSetPartCount) {
        assertMegaSetPartCountAllPropertiesEquals(expectedMegaSetPartCount, getPersistedMegaSetPartCount(expectedMegaSetPartCount));
    }

    protected void assertPersistedMegaSetPartCountToMatchUpdatableProperties(MegaSetPartCount expectedMegaSetPartCount) {
        assertMegaSetPartCountAllUpdatablePropertiesEquals(
            expectedMegaSetPartCount,
            getPersistedMegaSetPartCount(expectedMegaSetPartCount)
        );
    }
}
