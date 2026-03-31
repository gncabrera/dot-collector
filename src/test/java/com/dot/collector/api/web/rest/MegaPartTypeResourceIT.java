package com.dot.collector.api.web.rest;

import static com.dot.collector.api.domain.MegaPartTypeAsserts.*;
import static com.dot.collector.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dot.collector.api.IntegrationTest;
import com.dot.collector.api.domain.MegaPartType;
import com.dot.collector.api.repository.MegaPartTypeRepository;
import com.dot.collector.api.service.MegaPartTypeService;
import com.dot.collector.api.service.dto.MegaPartTypeDTO;
import com.dot.collector.api.service.mapper.MegaPartTypeMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link MegaPartTypeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MegaPartTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Boolean DEFAULT_IS_LATEST = false;
    private static final Boolean UPDATED_IS_LATEST = true;

    private static final String ENTITY_API_URL = "/api/mega-part-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MegaPartTypeRepository megaPartTypeRepository;

    @Mock
    private MegaPartTypeRepository megaPartTypeRepositoryMock;

    @Autowired
    private MegaPartTypeMapper megaPartTypeMapper;

    @Mock
    private MegaPartTypeService megaPartTypeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMegaPartTypeMockMvc;

    private MegaPartType megaPartType;

    private MegaPartType insertedMegaPartType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaPartType createEntity() {
        return new MegaPartType().name(DEFAULT_NAME).version(DEFAULT_VERSION).active(DEFAULT_ACTIVE).isLatest(DEFAULT_IS_LATEST);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaPartType createUpdatedEntity() {
        return new MegaPartType().name(UPDATED_NAME).version(UPDATED_VERSION).active(UPDATED_ACTIVE).isLatest(UPDATED_IS_LATEST);
    }

    @BeforeEach
    void initTest() {
        megaPartType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMegaPartType != null) {
            megaPartTypeRepository.delete(insertedMegaPartType);
            insertedMegaPartType = null;
        }
    }

    @Test
    @Transactional
    void createMegaPartType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MegaPartType
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(megaPartType);
        var returnedMegaPartTypeDTO = om.readValue(
            restMegaPartTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaPartTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MegaPartTypeDTO.class
        );

        // Validate the MegaPartType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMegaPartType = megaPartTypeMapper.toEntity(returnedMegaPartTypeDTO);
        assertMegaPartTypeUpdatableFieldsEquals(returnedMegaPartType, getPersistedMegaPartType(returnedMegaPartType));

        insertedMegaPartType = returnedMegaPartType;
    }

    @Test
    @Transactional
    void createMegaPartTypeWithExistingId() throws Exception {
        // Create the MegaPartType with an existing ID
        megaPartType.setId(1L);
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(megaPartType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMegaPartTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaPartTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MegaPartType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        megaPartType.setName(null);

        // Create the MegaPartType, which fails.
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(megaPartType);

        restMegaPartTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaPartTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        megaPartType.setVersion(null);

        // Create the MegaPartType, which fails.
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(megaPartType);

        restMegaPartTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaPartTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMegaPartTypes() throws Exception {
        // Initialize the database
        insertedMegaPartType = megaPartTypeRepository.saveAndFlush(megaPartType);

        // Get all the megaPartTypeList
        restMegaPartTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(megaPartType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].isLatest").value(hasItem(DEFAULT_IS_LATEST)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMegaPartTypesWithEagerRelationshipsIsEnabled() throws Exception {
        when(megaPartTypeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMegaPartTypeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(megaPartTypeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMegaPartTypesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(megaPartTypeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMegaPartTypeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(megaPartTypeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMegaPartType() throws Exception {
        // Initialize the database
        insertedMegaPartType = megaPartTypeRepository.saveAndFlush(megaPartType);

        // Get the megaPartType
        restMegaPartTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, megaPartType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(megaPartType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.isLatest").value(DEFAULT_IS_LATEST));
    }

    @Test
    @Transactional
    void getNonExistingMegaPartType() throws Exception {
        // Get the megaPartType
        restMegaPartTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMegaPartType() throws Exception {
        // Initialize the database
        insertedMegaPartType = megaPartTypeRepository.saveAndFlush(megaPartType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaPartType
        MegaPartType updatedMegaPartType = megaPartTypeRepository.findById(megaPartType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMegaPartType are not directly saved in db
        em.detach(updatedMegaPartType);
        updatedMegaPartType.name(UPDATED_NAME).version(UPDATED_VERSION).active(UPDATED_ACTIVE).isLatest(UPDATED_IS_LATEST);
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(updatedMegaPartType);

        restMegaPartTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaPartTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaPartTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the MegaPartType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMegaPartTypeToMatchAllProperties(updatedMegaPartType);
    }

    @Test
    @Transactional
    void putNonExistingMegaPartType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartType.setId(longCount.incrementAndGet());

        // Create the MegaPartType
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(megaPartType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaPartTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaPartTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaPartTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaPartType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMegaPartType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartType.setId(longCount.incrementAndGet());

        // Create the MegaPartType
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(megaPartType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaPartTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaPartTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaPartType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMegaPartType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartType.setId(longCount.incrementAndGet());

        // Create the MegaPartType
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(megaPartType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaPartTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaPartTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaPartType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMegaPartTypeWithPatch() throws Exception {
        // Initialize the database
        insertedMegaPartType = megaPartTypeRepository.saveAndFlush(megaPartType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaPartType using partial update
        MegaPartType partialUpdatedMegaPartType = new MegaPartType();
        partialUpdatedMegaPartType.setId(megaPartType.getId());

        partialUpdatedMegaPartType.name(UPDATED_NAME).version(UPDATED_VERSION).active(UPDATED_ACTIVE);

        restMegaPartTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaPartType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaPartType))
            )
            .andExpect(status().isOk());

        // Validate the MegaPartType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaPartTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMegaPartType, megaPartType),
            getPersistedMegaPartType(megaPartType)
        );
    }

    @Test
    @Transactional
    void fullUpdateMegaPartTypeWithPatch() throws Exception {
        // Initialize the database
        insertedMegaPartType = megaPartTypeRepository.saveAndFlush(megaPartType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaPartType using partial update
        MegaPartType partialUpdatedMegaPartType = new MegaPartType();
        partialUpdatedMegaPartType.setId(megaPartType.getId());

        partialUpdatedMegaPartType.name(UPDATED_NAME).version(UPDATED_VERSION).active(UPDATED_ACTIVE).isLatest(UPDATED_IS_LATEST);

        restMegaPartTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaPartType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaPartType))
            )
            .andExpect(status().isOk());

        // Validate the MegaPartType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaPartTypeUpdatableFieldsEquals(partialUpdatedMegaPartType, getPersistedMegaPartType(partialUpdatedMegaPartType));
    }

    @Test
    @Transactional
    void patchNonExistingMegaPartType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartType.setId(longCount.incrementAndGet());

        // Create the MegaPartType
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(megaPartType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaPartTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, megaPartTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaPartTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaPartType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMegaPartType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartType.setId(longCount.incrementAndGet());

        // Create the MegaPartType
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(megaPartType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaPartTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaPartTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaPartType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMegaPartType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaPartType.setId(longCount.incrementAndGet());

        // Create the MegaPartType
        MegaPartTypeDTO megaPartTypeDTO = megaPartTypeMapper.toDto(megaPartType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaPartTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(megaPartTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaPartType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMegaPartType() throws Exception {
        // Initialize the database
        insertedMegaPartType = megaPartTypeRepository.saveAndFlush(megaPartType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the megaPartType
        restMegaPartTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, megaPartType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return megaPartTypeRepository.count();
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

    protected MegaPartType getPersistedMegaPartType(MegaPartType megaPartType) {
        return megaPartTypeRepository.findById(megaPartType.getId()).orElseThrow();
    }

    protected void assertPersistedMegaPartTypeToMatchAllProperties(MegaPartType expectedMegaPartType) {
        assertMegaPartTypeAllPropertiesEquals(expectedMegaPartType, getPersistedMegaPartType(expectedMegaPartType));
    }

    protected void assertPersistedMegaPartTypeToMatchUpdatableProperties(MegaPartType expectedMegaPartType) {
        assertMegaPartTypeAllUpdatablePropertiesEquals(expectedMegaPartType, getPersistedMegaPartType(expectedMegaPartType));
    }
}
