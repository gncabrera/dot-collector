package com.dot.collector.api.web.rest;

import static com.dot.collector.api.domain.MegaAttributeAsserts.*;
import static com.dot.collector.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dot.collector.api.IntegrationTest;
import com.dot.collector.api.domain.MegaAttribute;
import com.dot.collector.api.domain.enumeration.AttributeType;
import com.dot.collector.api.domain.enumeration.UIComponent;
import com.dot.collector.api.repository.MegaAttributeRepository;
import com.dot.collector.api.service.dto.MegaAttributeDTO;
import com.dot.collector.api.service.mapper.MegaAttributeMapper;
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
 * Integration tests for the {@link MegaAttributeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MegaAttributeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final UIComponent DEFAULT_UI_COMPONENT = UIComponent.INPUT;
    private static final UIComponent UPDATED_UI_COMPONENT = UIComponent.TEXTAREA;

    private static final AttributeType DEFAULT_TYPE = AttributeType.STRING;
    private static final AttributeType UPDATED_TYPE = AttributeType.NUMBER;

    private static final Boolean DEFAULT_REQUIRED = false;
    private static final Boolean UPDATED_REQUIRED = true;

    private static final Boolean DEFAULT_MULTIPLE = false;
    private static final Boolean UPDATED_MULTIPLE = true;

    private static final String DEFAULT_DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_DEFAULT_VALUE = "BBBBBBBBBB";

    private static final Double DEFAULT_MIN_NUMBER = 1D;
    private static final Double UPDATED_MIN_NUMBER = 2D;

    private static final Double DEFAULT_MAX_NUMBER = 1D;
    private static final Double UPDATED_MAX_NUMBER = 2D;

    private static final Integer DEFAULT_MIN_LENGTH = 1;
    private static final Integer UPDATED_MIN_LENGTH = 2;

    private static final Integer DEFAULT_MAX_LENGTH = 1;
    private static final Integer UPDATED_MAX_LENGTH = 2;

    private static final String DEFAULT_REGEX = "AAAAAAAAAA";
    private static final String UPDATED_REGEX = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;

    private static final String DEFAULT_ATTRIBUTE_GROUP = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTE_GROUP = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/mega-attributes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MegaAttributeRepository megaAttributeRepository;

    @Autowired
    private MegaAttributeMapper megaAttributeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMegaAttributeMockMvc;

    private MegaAttribute megaAttribute;

    private MegaAttribute insertedMegaAttribute;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaAttribute createEntity() {
        return new MegaAttribute()
            .name(DEFAULT_NAME)
            .label(DEFAULT_LABEL)
            .description(DEFAULT_DESCRIPTION)
            .uiComponent(DEFAULT_UI_COMPONENT)
            .type(DEFAULT_TYPE)
            .required(DEFAULT_REQUIRED)
            .multiple(DEFAULT_MULTIPLE)
            .defaultValue(DEFAULT_DEFAULT_VALUE)
            .minNumber(DEFAULT_MIN_NUMBER)
            .maxNumber(DEFAULT_MAX_NUMBER)
            .minLength(DEFAULT_MIN_LENGTH)
            .maxLength(DEFAULT_MAX_LENGTH)
            .regex(DEFAULT_REGEX)
            .order(DEFAULT_ORDER)
            .attributeGroup(DEFAULT_ATTRIBUTE_GROUP)
            .active(DEFAULT_ACTIVE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaAttribute createUpdatedEntity() {
        return new MegaAttribute()
            .name(UPDATED_NAME)
            .label(UPDATED_LABEL)
            .description(UPDATED_DESCRIPTION)
            .uiComponent(UPDATED_UI_COMPONENT)
            .type(UPDATED_TYPE)
            .required(UPDATED_REQUIRED)
            .multiple(UPDATED_MULTIPLE)
            .defaultValue(UPDATED_DEFAULT_VALUE)
            .minNumber(UPDATED_MIN_NUMBER)
            .maxNumber(UPDATED_MAX_NUMBER)
            .minLength(UPDATED_MIN_LENGTH)
            .maxLength(UPDATED_MAX_LENGTH)
            .regex(UPDATED_REGEX)
            .order(UPDATED_ORDER)
            .attributeGroup(UPDATED_ATTRIBUTE_GROUP)
            .active(UPDATED_ACTIVE);
    }

    @BeforeEach
    void initTest() {
        megaAttribute = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMegaAttribute != null) {
            megaAttributeRepository.delete(insertedMegaAttribute);
            insertedMegaAttribute = null;
        }
    }

    @Test
    @Transactional
    void createMegaAttribute() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MegaAttribute
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);
        var returnedMegaAttributeDTO = om.readValue(
            restMegaAttributeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaAttributeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MegaAttributeDTO.class
        );

        // Validate the MegaAttribute in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMegaAttribute = megaAttributeMapper.toEntity(returnedMegaAttributeDTO);
        assertMegaAttributeUpdatableFieldsEquals(returnedMegaAttribute, getPersistedMegaAttribute(returnedMegaAttribute));

        insertedMegaAttribute = returnedMegaAttribute;
    }

    @Test
    @Transactional
    void createMegaAttributeWithExistingId() throws Exception {
        // Create the MegaAttribute with an existing ID
        megaAttribute.setId(1L);
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMegaAttributeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaAttributeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MegaAttribute in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        megaAttribute.setName(null);

        // Create the MegaAttribute, which fails.
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);

        restMegaAttributeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaAttributeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLabelIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        megaAttribute.setLabel(null);

        // Create the MegaAttribute, which fails.
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);

        restMegaAttributeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaAttributeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        megaAttribute.setType(null);

        // Create the MegaAttribute, which fails.
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);

        restMegaAttributeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaAttributeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMegaAttributes() throws Exception {
        // Initialize the database
        insertedMegaAttribute = megaAttributeRepository.saveAndFlush(megaAttribute);

        // Get all the megaAttributeList
        restMegaAttributeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(megaAttribute.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].uiComponent").value(hasItem(DEFAULT_UI_COMPONENT.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].required").value(hasItem(DEFAULT_REQUIRED)))
            .andExpect(jsonPath("$.[*].multiple").value(hasItem(DEFAULT_MULTIPLE)))
            .andExpect(jsonPath("$.[*].defaultValue").value(hasItem(DEFAULT_DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].minNumber").value(hasItem(DEFAULT_MIN_NUMBER)))
            .andExpect(jsonPath("$.[*].maxNumber").value(hasItem(DEFAULT_MAX_NUMBER)))
            .andExpect(jsonPath("$.[*].minLength").value(hasItem(DEFAULT_MIN_LENGTH)))
            .andExpect(jsonPath("$.[*].maxLength").value(hasItem(DEFAULT_MAX_LENGTH)))
            .andExpect(jsonPath("$.[*].regex").value(hasItem(DEFAULT_REGEX)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)))
            .andExpect(jsonPath("$.[*].attributeGroup").value(hasItem(DEFAULT_ATTRIBUTE_GROUP)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)));
    }

    @Test
    @Transactional
    void getMegaAttribute() throws Exception {
        // Initialize the database
        insertedMegaAttribute = megaAttributeRepository.saveAndFlush(megaAttribute);

        // Get the megaAttribute
        restMegaAttributeMockMvc
            .perform(get(ENTITY_API_URL_ID, megaAttribute.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(megaAttribute.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.uiComponent").value(DEFAULT_UI_COMPONENT.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.required").value(DEFAULT_REQUIRED))
            .andExpect(jsonPath("$.multiple").value(DEFAULT_MULTIPLE))
            .andExpect(jsonPath("$.defaultValue").value(DEFAULT_DEFAULT_VALUE))
            .andExpect(jsonPath("$.minNumber").value(DEFAULT_MIN_NUMBER))
            .andExpect(jsonPath("$.maxNumber").value(DEFAULT_MAX_NUMBER))
            .andExpect(jsonPath("$.minLength").value(DEFAULT_MIN_LENGTH))
            .andExpect(jsonPath("$.maxLength").value(DEFAULT_MAX_LENGTH))
            .andExpect(jsonPath("$.regex").value(DEFAULT_REGEX))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER))
            .andExpect(jsonPath("$.attributeGroup").value(DEFAULT_ATTRIBUTE_GROUP))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE));
    }

    @Test
    @Transactional
    void getNonExistingMegaAttribute() throws Exception {
        // Get the megaAttribute
        restMegaAttributeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMegaAttribute() throws Exception {
        // Initialize the database
        insertedMegaAttribute = megaAttributeRepository.saveAndFlush(megaAttribute);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaAttribute
        MegaAttribute updatedMegaAttribute = megaAttributeRepository.findById(megaAttribute.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMegaAttribute are not directly saved in db
        em.detach(updatedMegaAttribute);
        updatedMegaAttribute
            .name(UPDATED_NAME)
            .label(UPDATED_LABEL)
            .description(UPDATED_DESCRIPTION)
            .uiComponent(UPDATED_UI_COMPONENT)
            .type(UPDATED_TYPE)
            .required(UPDATED_REQUIRED)
            .multiple(UPDATED_MULTIPLE)
            .defaultValue(UPDATED_DEFAULT_VALUE)
            .minNumber(UPDATED_MIN_NUMBER)
            .maxNumber(UPDATED_MAX_NUMBER)
            .minLength(UPDATED_MIN_LENGTH)
            .maxLength(UPDATED_MAX_LENGTH)
            .regex(UPDATED_REGEX)
            .order(UPDATED_ORDER)
            .attributeGroup(UPDATED_ATTRIBUTE_GROUP)
            .active(UPDATED_ACTIVE);
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(updatedMegaAttribute);

        restMegaAttributeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaAttributeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaAttributeDTO))
            )
            .andExpect(status().isOk());

        // Validate the MegaAttribute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMegaAttributeToMatchAllProperties(updatedMegaAttribute);
    }

    @Test
    @Transactional
    void putNonExistingMegaAttribute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttribute.setId(longCount.incrementAndGet());

        // Create the MegaAttribute
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaAttributeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaAttributeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaAttributeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaAttribute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMegaAttribute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttribute.setId(longCount.incrementAndGet());

        // Create the MegaAttribute
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaAttributeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaAttributeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaAttribute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMegaAttribute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttribute.setId(longCount.incrementAndGet());

        // Create the MegaAttribute
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaAttributeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaAttributeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaAttribute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMegaAttributeWithPatch() throws Exception {
        // Initialize the database
        insertedMegaAttribute = megaAttributeRepository.saveAndFlush(megaAttribute);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaAttribute using partial update
        MegaAttribute partialUpdatedMegaAttribute = new MegaAttribute();
        partialUpdatedMegaAttribute.setId(megaAttribute.getId());

        partialUpdatedMegaAttribute
            .name(UPDATED_NAME)
            .uiComponent(UPDATED_UI_COMPONENT)
            .type(UPDATED_TYPE)
            .required(UPDATED_REQUIRED)
            .multiple(UPDATED_MULTIPLE)
            .minNumber(UPDATED_MIN_NUMBER)
            .minLength(UPDATED_MIN_LENGTH)
            .attributeGroup(UPDATED_ATTRIBUTE_GROUP);

        restMegaAttributeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaAttribute.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaAttribute))
            )
            .andExpect(status().isOk());

        // Validate the MegaAttribute in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaAttributeUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMegaAttribute, megaAttribute),
            getPersistedMegaAttribute(megaAttribute)
        );
    }

    @Test
    @Transactional
    void fullUpdateMegaAttributeWithPatch() throws Exception {
        // Initialize the database
        insertedMegaAttribute = megaAttributeRepository.saveAndFlush(megaAttribute);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaAttribute using partial update
        MegaAttribute partialUpdatedMegaAttribute = new MegaAttribute();
        partialUpdatedMegaAttribute.setId(megaAttribute.getId());

        partialUpdatedMegaAttribute
            .name(UPDATED_NAME)
            .label(UPDATED_LABEL)
            .description(UPDATED_DESCRIPTION)
            .uiComponent(UPDATED_UI_COMPONENT)
            .type(UPDATED_TYPE)
            .required(UPDATED_REQUIRED)
            .multiple(UPDATED_MULTIPLE)
            .defaultValue(UPDATED_DEFAULT_VALUE)
            .minNumber(UPDATED_MIN_NUMBER)
            .maxNumber(UPDATED_MAX_NUMBER)
            .minLength(UPDATED_MIN_LENGTH)
            .maxLength(UPDATED_MAX_LENGTH)
            .regex(UPDATED_REGEX)
            .order(UPDATED_ORDER)
            .attributeGroup(UPDATED_ATTRIBUTE_GROUP)
            .active(UPDATED_ACTIVE);

        restMegaAttributeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaAttribute.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaAttribute))
            )
            .andExpect(status().isOk());

        // Validate the MegaAttribute in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaAttributeUpdatableFieldsEquals(partialUpdatedMegaAttribute, getPersistedMegaAttribute(partialUpdatedMegaAttribute));
    }

    @Test
    @Transactional
    void patchNonExistingMegaAttribute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttribute.setId(longCount.incrementAndGet());

        // Create the MegaAttribute
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaAttributeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, megaAttributeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaAttributeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaAttribute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMegaAttribute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttribute.setId(longCount.incrementAndGet());

        // Create the MegaAttribute
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaAttributeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaAttributeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaAttribute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMegaAttribute() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttribute.setId(longCount.incrementAndGet());

        // Create the MegaAttribute
        MegaAttributeDTO megaAttributeDTO = megaAttributeMapper.toDto(megaAttribute);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaAttributeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(megaAttributeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaAttribute in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMegaAttribute() throws Exception {
        // Initialize the database
        insertedMegaAttribute = megaAttributeRepository.saveAndFlush(megaAttribute);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the megaAttribute
        restMegaAttributeMockMvc
            .perform(delete(ENTITY_API_URL_ID, megaAttribute.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return megaAttributeRepository.count();
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

    protected MegaAttribute getPersistedMegaAttribute(MegaAttribute megaAttribute) {
        return megaAttributeRepository.findById(megaAttribute.getId()).orElseThrow();
    }

    protected void assertPersistedMegaAttributeToMatchAllProperties(MegaAttribute expectedMegaAttribute) {
        assertMegaAttributeAllPropertiesEquals(expectedMegaAttribute, getPersistedMegaAttribute(expectedMegaAttribute));
    }

    protected void assertPersistedMegaAttributeToMatchUpdatableProperties(MegaAttribute expectedMegaAttribute) {
        assertMegaAttributeAllUpdatablePropertiesEquals(expectedMegaAttribute, getPersistedMegaAttribute(expectedMegaAttribute));
    }
}
