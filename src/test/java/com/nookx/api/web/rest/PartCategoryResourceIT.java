package com.nookx.api.web.rest;

import static com.nookx.api.domain.PartCategoryAsserts.*;
import static com.nookx.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookx.api.IntegrationTest;
import com.nookx.api.domain.PartCategory;
import com.nookx.api.repository.PartCategoryRepository;
import com.nookx.api.service.dto.PartCategoryDTO;
import com.nookx.api.service.mapper.PartCategoryMapper;
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
 * Integration tests for the {@link PartCategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PartCategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/part-categories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PartCategoryRepository partCategoryRepository;

    @Autowired
    private PartCategoryMapper partCategoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPartCategoryMockMvc;

    private PartCategory partCategory;

    private PartCategory insertedPartCategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartCategory createEntity() {
        return new PartCategory().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PartCategory createUpdatedEntity() {
        return new PartCategory().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        partCategory = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPartCategory != null) {
            partCategoryRepository.delete(insertedPartCategory);
            insertedPartCategory = null;
        }
    }

    @Test
    @Transactional
    void createPartCategory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PartCategory
        PartCategoryDTO partCategoryDTO = partCategoryMapper.toDto(partCategory);
        var returnedPartCategoryDTO = om.readValue(
            restPartCategoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partCategoryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PartCategoryDTO.class
        );

        // Validate the PartCategory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPartCategory = partCategoryMapper.toEntity(returnedPartCategoryDTO);
        assertPartCategoryUpdatableFieldsEquals(returnedPartCategory, getPersistedPartCategory(returnedPartCategory));

        insertedPartCategory = returnedPartCategory;
    }

    @Test
    @Transactional
    void createPartCategoryWithExistingId() throws Exception {
        // Create the PartCategory with an existing ID
        partCategory.setId(1L);
        PartCategoryDTO partCategoryDTO = partCategoryMapper.toDto(partCategory);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPartCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partCategoryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PartCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        partCategory.setName(null);

        // Create the PartCategory, which fails.
        PartCategoryDTO partCategoryDTO = partCategoryMapper.toDto(partCategory);

        restPartCategoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partCategoryDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPartCategories() throws Exception {
        // Initialize the database
        insertedPartCategory = partCategoryRepository.saveAndFlush(partCategory);

        // Get all the partCategoryList
        restPartCategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(partCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getPartCategory() throws Exception {
        // Initialize the database
        insertedPartCategory = partCategoryRepository.saveAndFlush(partCategory);

        // Get the partCategory
        restPartCategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, partCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(partCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingPartCategory() throws Exception {
        // Get the partCategory
        restPartCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPartCategory() throws Exception {
        // Initialize the database
        insertedPartCategory = partCategoryRepository.saveAndFlush(partCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partCategory
        PartCategory updatedPartCategory = partCategoryRepository.findById(partCategory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPartCategory are not directly saved in db
        em.detach(updatedPartCategory);
        updatedPartCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        PartCategoryDTO partCategoryDTO = partCategoryMapper.toDto(updatedPartCategory);

        restPartCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partCategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the PartCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPartCategoryToMatchAllProperties(updatedPartCategory);
    }

    @Test
    @Transactional
    void putNonExistingPartCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partCategory.setId(longCount.incrementAndGet());

        // Create the PartCategory
        PartCategoryDTO partCategoryDTO = partCategoryMapper.toDto(partCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, partCategoryDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPartCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partCategory.setId(longCount.incrementAndGet());

        // Create the PartCategory
        PartCategoryDTO partCategoryDTO = partCategoryMapper.toDto(partCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartCategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(partCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPartCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partCategory.setId(longCount.incrementAndGet());

        // Create the PartCategory
        PartCategoryDTO partCategoryDTO = partCategoryMapper.toDto(partCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartCategoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(partCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePartCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedPartCategory = partCategoryRepository.saveAndFlush(partCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partCategory using partial update
        PartCategory partialUpdatedPartCategory = new PartCategory();
        partialUpdatedPartCategory.setId(partCategory.getId());

        partialUpdatedPartCategory.description(UPDATED_DESCRIPTION);

        restPartCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartCategory))
            )
            .andExpect(status().isOk());

        // Validate the PartCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartCategoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPartCategory, partCategory),
            getPersistedPartCategory(partCategory)
        );
    }

    @Test
    @Transactional
    void fullUpdatePartCategoryWithPatch() throws Exception {
        // Initialize the database
        insertedPartCategory = partCategoryRepository.saveAndFlush(partCategory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the partCategory using partial update
        PartCategory partialUpdatedPartCategory = new PartCategory();
        partialUpdatedPartCategory.setId(partCategory.getId());

        partialUpdatedPartCategory.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restPartCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPartCategory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPartCategory))
            )
            .andExpect(status().isOk());

        // Validate the PartCategory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPartCategoryUpdatableFieldsEquals(partialUpdatedPartCategory, getPersistedPartCategory(partialUpdatedPartCategory));
    }

    @Test
    @Transactional
    void patchNonExistingPartCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partCategory.setId(longCount.incrementAndGet());

        // Create the PartCategory
        PartCategoryDTO partCategoryDTO = partCategoryMapper.toDto(partCategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPartCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partCategoryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPartCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partCategory.setId(longCount.incrementAndGet());

        // Create the PartCategory
        PartCategoryDTO partCategoryDTO = partCategoryMapper.toDto(partCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartCategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partCategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PartCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPartCategory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        partCategory.setId(longCount.incrementAndGet());

        // Create the PartCategory
        PartCategoryDTO partCategoryDTO = partCategoryMapper.toDto(partCategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPartCategoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(partCategoryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PartCategory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePartCategory() throws Exception {
        // Initialize the database
        insertedPartCategory = partCategoryRepository.saveAndFlush(partCategory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the partCategory
        restPartCategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, partCategory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return partCategoryRepository.count();
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

    protected PartCategory getPersistedPartCategory(PartCategory partCategory) {
        return partCategoryRepository.findById(partCategory.getId()).orElseThrow();
    }

    protected void assertPersistedPartCategoryToMatchAllProperties(PartCategory expectedPartCategory) {
        assertPartCategoryAllPropertiesEquals(expectedPartCategory, getPersistedPartCategory(expectedPartCategory));
    }

    protected void assertPersistedPartCategoryToMatchUpdatableProperties(PartCategory expectedPartCategory) {
        assertPartCategoryAllUpdatablePropertiesEquals(expectedPartCategory, getPersistedPartCategory(expectedPartCategory));
    }
}
