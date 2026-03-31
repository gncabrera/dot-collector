package com.dot.collector.api.web.rest;

import static com.dot.collector.api.domain.PartSubCategoryAsserts.*;
import static com.dot.collector.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dot.collector.api.IntegrationTest;
import com.dot.collector.api.domain.PartSubCategory;
import com.dot.collector.api.repository.PartSubCategoryRepository;
import com.dot.collector.api.service.dto.PartSubCategoryDTO;
import com.dot.collector.api.service.mapper.PartSubCategoryMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link PartSubCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PartSubCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/part-sub-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartSubCategoryRepository partSubCategoryRepository;

    @Autowired
    private PartSubCategoryMapper partSubCategoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartSubCategoryMockMvc;

    private PartSubCategory partSubCategory;

    private PartSubCategory insertedPartSubCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartSubCategory createEntity() {
        return new PartSubCategory().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartSubCategory createUpdatedEntity() {
        return new PartSubCategory().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        partSubCategory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPartSubCategory != null) {
            partSubCategoryRepository.delete(insertedPartSubCategory);
            insertedPartSubCategory = null;
        }
    }

    @Test
    @Transactional
    void createPartSubCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PartSubCategory
        PartSubCategoryDTO partSubCategoryDTO = partSubCategoryMapper.toDto(partSubCategory);
        var returnedPartSubCategoryDTO = om.readValue(
            restPartSubCategoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partSubCategoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PartSubCategoryDTO.class
        );

        // Validate the PartSubCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPartSubCategory = partSubCategoryMapper.toEntity(returnedPartSubCategoryDTO);
        assertPartSubCategoryUpdatableFieldsEquals(returnedPartSubCategory, getPersistedPartSubCategory(returnedPartSubCategory));

        insertedPartSubCategory = returnedPartSubCategory;
    }

    @Test
    @Transactional
    void createPartSubCategoryWithExistingId() throws Exception {
        // Create the PartSubCategory with an existing ID
        partSubCategory.setId(1L);
        PartSubCategoryDTO partSubCategoryDTO = partSubCategoryMapper.toDto(partSubCategory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartSubCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partSubCategoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PartSubCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partSubCategory.setName(null);

        // Create the PartSubCategory, which fails.
        PartSubCategoryDTO partSubCategoryDTO = partSubCategoryMapper.toDto(partSubCategory);

        restPartSubCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partSubCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPartSubCategories() throws Exception {
        // Initialize the database
        insertedPartSubCategory = partSubCategoryRepository.saveAndFlush(partSubCategory);

        // Get all the partSubCategoryList
        restPartSubCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partSubCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getPartSubCategory() throws Exception {
        // Initialize the database
        insertedPartSubCategory = partSubCategoryRepository.saveAndFlush(partSubCategory);

        // Get the partSubCategory
        restPartSubCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, partSubCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(partSubCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingPartSubCategory() throws Exception {
        // Get the partSubCategory
        restPartSubCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartSubCategory() throws Exception {
        // Initialize the database
        insertedPartSubCategory = partSubCategoryRepository.saveAndFlush(partSubCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partSubCategory
        PartSubCategory updatedPartSubCategory = partSubCategoryRepository.findById(partSubCategory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPartSubCategory are not directly saved in db
        em.detach(updatedPartSubCategory);
        updatedPartSubCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        PartSubCategoryDTO partSubCategoryDTO = partSubCategoryMapper.toDto(updatedPartSubCategory);

        restPartSubCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partSubCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partSubCategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the PartSubCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartSubCategoryToMatchAllProperties(updatedPartSubCategory);
    }

    @Test
    @Transactional
    void putNonExistingPartSubCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partSubCategory.setId(longCount.incrementAndGet());

        // Create the PartSubCategory
        PartSubCategoryDTO partSubCategoryDTO = partSubCategoryMapper.toDto(partSubCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartSubCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partSubCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partSubCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartSubCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartSubCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partSubCategory.setId(longCount.incrementAndGet());

        // Create the PartSubCategory
        PartSubCategoryDTO partSubCategoryDTO = partSubCategoryMapper.toDto(partSubCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartSubCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partSubCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartSubCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartSubCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partSubCategory.setId(longCount.incrementAndGet());

        // Create the PartSubCategory
        PartSubCategoryDTO partSubCategoryDTO = partSubCategoryMapper.toDto(partSubCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartSubCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partSubCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartSubCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartSubCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedPartSubCategory = partSubCategoryRepository.saveAndFlush(partSubCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partSubCategory using partial update
        PartSubCategory partialUpdatedPartSubCategory = new PartSubCategory();
        partialUpdatedPartSubCategory.setId(partSubCategory.getId());

        partialUpdatedPartSubCategory.name(UPDATED_NAME);

        restPartSubCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartSubCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartSubCategory))
            )
            .andExpect(status().isOk());

        // Validate the PartSubCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartSubCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPartSubCategory, partSubCategory),
            getPersistedPartSubCategory(partSubCategory)
        );
    }

    @Test
    @Transactional
    void fullUpdatePartSubCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedPartSubCategory = partSubCategoryRepository.saveAndFlush(partSubCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partSubCategory using partial update
        PartSubCategory partialUpdatedPartSubCategory = new PartSubCategory();
        partialUpdatedPartSubCategory.setId(partSubCategory.getId());

        partialUpdatedPartSubCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restPartSubCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartSubCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartSubCategory))
            )
            .andExpect(status().isOk());

        // Validate the PartSubCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartSubCategoryUpdatableFieldsEquals(
            partialUpdatedPartSubCategory,
            getPersistedPartSubCategory(partialUpdatedPartSubCategory)
        );
    }

    @Test
    @Transactional
    void patchNonExistingPartSubCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partSubCategory.setId(longCount.incrementAndGet());

        // Create the PartSubCategory
        PartSubCategoryDTO partSubCategoryDTO = partSubCategoryMapper.toDto(partSubCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartSubCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partSubCategoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partSubCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartSubCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartSubCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partSubCategory.setId(longCount.incrementAndGet());

        // Create the PartSubCategory
        PartSubCategoryDTO partSubCategoryDTO = partSubCategoryMapper.toDto(partSubCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartSubCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partSubCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartSubCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartSubCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partSubCategory.setId(longCount.incrementAndGet());

        // Create the PartSubCategory
        PartSubCategoryDTO partSubCategoryDTO = partSubCategoryMapper.toDto(partSubCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartSubCategoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(partSubCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartSubCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartSubCategory() throws Exception {
        // Initialize the database
        insertedPartSubCategory = partSubCategoryRepository.saveAndFlush(partSubCategory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the partSubCategory
        restPartSubCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, partSubCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partSubCategoryRepository.count();
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

    protected PartSubCategory getPersistedPartSubCategory(PartSubCategory partSubCategory) {
        return partSubCategoryRepository.findById(partSubCategory.getId()).orElseThrow();
    }

    protected void assertPersistedPartSubCategoryToMatchAllProperties(PartSubCategory expectedPartSubCategory) {
        assertPartSubCategoryAllPropertiesEquals(expectedPartSubCategory, getPersistedPartSubCategory(expectedPartSubCategory));
    }

    protected void assertPersistedPartSubCategoryToMatchUpdatableProperties(PartSubCategory expectedPartSubCategory) {
        assertPartSubCategoryAllUpdatablePropertiesEquals(expectedPartSubCategory, getPersistedPartSubCategory(expectedPartSubCategory));
    }
}
