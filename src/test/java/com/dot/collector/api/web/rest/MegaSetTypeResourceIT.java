package com.dot.collector.api.web.rest;

import static com.dot.collector.api.domain.MegaSetTypeAsserts.*;
import static com.dot.collector.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dot.collector.api.IntegrationTest;
import com.dot.collector.api.domain.MegaSetType;
import com.dot.collector.api.repository.MegaSetTypeRepository;
import com.dot.collector.api.service.MegaSetTypeService;
import com.dot.collector.api.service.dto.MegaSetTypeDTO;
import com.dot.collector.api.service.mapper.MegaSetTypeMapper;
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
 * Integration tests for the {@link MegaSetTypeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MegaSetTypeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Boolean DEFAULT_IS_LATEST = false;
    private static final Boolean UPDATED_IS_LATEST = true;

    private static final String ENTITY_API_URL = "/api/mega-set-types";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MegaSetTypeRepository megaSetTypeRepository;

    @Mock
    private MegaSetTypeRepository megaSetTypeRepositoryMock;

    @Autowired
    private MegaSetTypeMapper megaSetTypeMapper;

    @Mock
    private MegaSetTypeService megaSetTypeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMegaSetTypeMockMvc;

    private MegaSetType megaSetType;

    private MegaSetType insertedMegaSetType;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaSetType createEntity() {
        return new MegaSetType().name(DEFAULT_NAME).version(DEFAULT_VERSION).active(DEFAULT_ACTIVE).isLatest(DEFAULT_IS_LATEST);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaSetType createUpdatedEntity() {
        return new MegaSetType().name(UPDATED_NAME).version(UPDATED_VERSION).active(UPDATED_ACTIVE).isLatest(UPDATED_IS_LATEST);
    }

    @BeforeEach
    void initTest() {
        megaSetType = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMegaSetType != null) {
            megaSetTypeRepository.delete(insertedMegaSetType);
            insertedMegaSetType = null;
        }
    }

    @Test
    @Transactional
    void createMegaSetType() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MegaSetType
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(megaSetType);
        var returnedMegaSetTypeDTO = om.readValue(
            restMegaSetTypeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaSetTypeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MegaSetTypeDTO.class
        );

        // Validate the MegaSetType in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMegaSetType = megaSetTypeMapper.toEntity(returnedMegaSetTypeDTO);
        assertMegaSetTypeUpdatableFieldsEquals(returnedMegaSetType, getPersistedMegaSetType(returnedMegaSetType));

        insertedMegaSetType = returnedMegaSetType;
    }

    @Test
    @Transactional
    void createMegaSetTypeWithExistingId() throws Exception {
        // Create the MegaSetType with an existing ID
        megaSetType.setId(1L);
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(megaSetType);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMegaSetTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaSetTypeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MegaSetType in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        megaSetType.setName(null);

        // Create the MegaSetType, which fails.
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(megaSetType);

        restMegaSetTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaSetTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVersionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        megaSetType.setVersion(null);

        // Create the MegaSetType, which fails.
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(megaSetType);

        restMegaSetTypeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaSetTypeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMegaSetTypes() throws Exception {
        // Initialize the database
        insertedMegaSetType = megaSetTypeRepository.saveAndFlush(megaSetType);

        // Get all the megaSetTypeList
        restMegaSetTypeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(megaSetType.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].isLatest").value(hasItem(DEFAULT_IS_LATEST)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMegaSetTypesWithEagerRelationshipsIsEnabled() throws Exception {
        when(megaSetTypeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMegaSetTypeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(megaSetTypeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMegaSetTypesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(megaSetTypeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMegaSetTypeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(megaSetTypeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMegaSetType() throws Exception {
        // Initialize the database
        insertedMegaSetType = megaSetTypeRepository.saveAndFlush(megaSetType);

        // Get the megaSetType
        restMegaSetTypeMockMvc
            .perform(get(ENTITY_API_URL_ID, megaSetType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(megaSetType.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.isLatest").value(DEFAULT_IS_LATEST));
    }

    @Test
    @Transactional
    void getNonExistingMegaSetType() throws Exception {
        // Get the megaSetType
        restMegaSetTypeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMegaSetType() throws Exception {
        // Initialize the database
        insertedMegaSetType = megaSetTypeRepository.saveAndFlush(megaSetType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaSetType
        MegaSetType updatedMegaSetType = megaSetTypeRepository.findById(megaSetType.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMegaSetType are not directly saved in db
        em.detach(updatedMegaSetType);
        updatedMegaSetType.name(UPDATED_NAME).version(UPDATED_VERSION).active(UPDATED_ACTIVE).isLatest(UPDATED_IS_LATEST);
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(updatedMegaSetType);

        restMegaSetTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaSetTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaSetTypeDTO))
            )
            .andExpect(status().isOk());

        // Validate the MegaSetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMegaSetTypeToMatchAllProperties(updatedMegaSetType);
    }

    @Test
    @Transactional
    void putNonExistingMegaSetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetType.setId(longCount.incrementAndGet());

        // Create the MegaSetType
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(megaSetType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaSetTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaSetTypeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaSetTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaSetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMegaSetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetType.setId(longCount.incrementAndGet());

        // Create the MegaSetType
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(megaSetType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaSetTypeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaSetTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaSetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMegaSetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetType.setId(longCount.incrementAndGet());

        // Create the MegaSetType
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(megaSetType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaSetTypeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaSetTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaSetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMegaSetTypeWithPatch() throws Exception {
        // Initialize the database
        insertedMegaSetType = megaSetTypeRepository.saveAndFlush(megaSetType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaSetType using partial update
        MegaSetType partialUpdatedMegaSetType = new MegaSetType();
        partialUpdatedMegaSetType.setId(megaSetType.getId());

        partialUpdatedMegaSetType.version(UPDATED_VERSION).active(UPDATED_ACTIVE);

        restMegaSetTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaSetType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaSetType))
            )
            .andExpect(status().isOk());

        // Validate the MegaSetType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaSetTypeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMegaSetType, megaSetType),
            getPersistedMegaSetType(megaSetType)
        );
    }

    @Test
    @Transactional
    void fullUpdateMegaSetTypeWithPatch() throws Exception {
        // Initialize the database
        insertedMegaSetType = megaSetTypeRepository.saveAndFlush(megaSetType);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaSetType using partial update
        MegaSetType partialUpdatedMegaSetType = new MegaSetType();
        partialUpdatedMegaSetType.setId(megaSetType.getId());

        partialUpdatedMegaSetType.name(UPDATED_NAME).version(UPDATED_VERSION).active(UPDATED_ACTIVE).isLatest(UPDATED_IS_LATEST);

        restMegaSetTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaSetType.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaSetType))
            )
            .andExpect(status().isOk());

        // Validate the MegaSetType in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaSetTypeUpdatableFieldsEquals(partialUpdatedMegaSetType, getPersistedMegaSetType(partialUpdatedMegaSetType));
    }

    @Test
    @Transactional
    void patchNonExistingMegaSetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetType.setId(longCount.incrementAndGet());

        // Create the MegaSetType
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(megaSetType);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaSetTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, megaSetTypeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaSetTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaSetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMegaSetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetType.setId(longCount.incrementAndGet());

        // Create the MegaSetType
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(megaSetType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaSetTypeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaSetTypeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaSetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMegaSetType() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaSetType.setId(longCount.incrementAndGet());

        // Create the MegaSetType
        MegaSetTypeDTO megaSetTypeDTO = megaSetTypeMapper.toDto(megaSetType);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaSetTypeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(megaSetTypeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaSetType in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMegaSetType() throws Exception {
        // Initialize the database
        insertedMegaSetType = megaSetTypeRepository.saveAndFlush(megaSetType);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the megaSetType
        restMegaSetTypeMockMvc
            .perform(delete(ENTITY_API_URL_ID, megaSetType.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return megaSetTypeRepository.count();
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

    protected MegaSetType getPersistedMegaSetType(MegaSetType megaSetType) {
        return megaSetTypeRepository.findById(megaSetType.getId()).orElseThrow();
    }

    protected void assertPersistedMegaSetTypeToMatchAllProperties(MegaSetType expectedMegaSetType) {
        assertMegaSetTypeAllPropertiesEquals(expectedMegaSetType, getPersistedMegaSetType(expectedMegaSetType));
    }

    protected void assertPersistedMegaSetTypeToMatchUpdatableProperties(MegaSetType expectedMegaSetType) {
        assertMegaSetTypeAllUpdatablePropertiesEquals(expectedMegaSetType, getPersistedMegaSetType(expectedMegaSetType));
    }
}
