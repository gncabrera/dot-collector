package com.dot.collector.api.web.rest;

import static com.dot.collector.api.domain.MegaAttributeOptionAsserts.*;
import static com.dot.collector.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dot.collector.api.IntegrationTest;
import com.dot.collector.api.domain.MegaAttributeOption;
import com.dot.collector.api.repository.MegaAttributeOptionRepository;
import com.dot.collector.api.service.dto.MegaAttributeOptionDTO;
import com.dot.collector.api.service.mapper.MegaAttributeOptionMapper;
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
 * Integration tests for the {@link MegaAttributeOptionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MegaAttributeOptionResourceIT {

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/mega-attribute-options";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MegaAttributeOptionRepository megaAttributeOptionRepository;

    @Autowired
    private MegaAttributeOptionMapper megaAttributeOptionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMegaAttributeOptionMockMvc;

    private MegaAttributeOption megaAttributeOption;

    private MegaAttributeOption insertedMegaAttributeOption;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaAttributeOption createEntity() {
        return new MegaAttributeOption().label(DEFAULT_LABEL).value(DEFAULT_VALUE).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MegaAttributeOption createUpdatedEntity() {
        return new MegaAttributeOption().label(UPDATED_LABEL).value(UPDATED_VALUE).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    void initTest() {
        megaAttributeOption = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMegaAttributeOption != null) {
            megaAttributeOptionRepository.delete(insertedMegaAttributeOption);
            insertedMegaAttributeOption = null;
        }
    }

    @Test
    @Transactional
    void createMegaAttributeOption() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MegaAttributeOption
        MegaAttributeOptionDTO megaAttributeOptionDTO = megaAttributeOptionMapper.toDto(megaAttributeOption);
        var returnedMegaAttributeOptionDTO = om.readValue(
            restMegaAttributeOptionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaAttributeOptionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MegaAttributeOptionDTO.class
        );

        // Validate the MegaAttributeOption in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMegaAttributeOption = megaAttributeOptionMapper.toEntity(returnedMegaAttributeOptionDTO);
        assertMegaAttributeOptionUpdatableFieldsEquals(
            returnedMegaAttributeOption,
            getPersistedMegaAttributeOption(returnedMegaAttributeOption)
        );

        insertedMegaAttributeOption = returnedMegaAttributeOption;
    }

    @Test
    @Transactional
    void createMegaAttributeOptionWithExistingId() throws Exception {
        // Create the MegaAttributeOption with an existing ID
        megaAttributeOption.setId(1L);
        MegaAttributeOptionDTO megaAttributeOptionDTO = megaAttributeOptionMapper.toDto(megaAttributeOption);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMegaAttributeOptionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaAttributeOptionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MegaAttributeOption in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllMegaAttributeOptions() throws Exception {
        // Initialize the database
        insertedMegaAttributeOption = megaAttributeOptionRepository.saveAndFlush(megaAttributeOption);

        // Get all the megaAttributeOptionList
        restMegaAttributeOptionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(megaAttributeOption.getId().intValue())))
            .andExpect(jsonPath("$.[*].label").value(hasItem(DEFAULT_LABEL)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getMegaAttributeOption() throws Exception {
        // Initialize the database
        insertedMegaAttributeOption = megaAttributeOptionRepository.saveAndFlush(megaAttributeOption);

        // Get the megaAttributeOption
        restMegaAttributeOptionMockMvc
            .perform(get(ENTITY_API_URL_ID, megaAttributeOption.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(megaAttributeOption.getId().intValue()))
            .andExpect(jsonPath("$.label").value(DEFAULT_LABEL))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingMegaAttributeOption() throws Exception {
        // Get the megaAttributeOption
        restMegaAttributeOptionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMegaAttributeOption() throws Exception {
        // Initialize the database
        insertedMegaAttributeOption = megaAttributeOptionRepository.saveAndFlush(megaAttributeOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaAttributeOption
        MegaAttributeOption updatedMegaAttributeOption = megaAttributeOptionRepository.findById(megaAttributeOption.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMegaAttributeOption are not directly saved in db
        em.detach(updatedMegaAttributeOption);
        updatedMegaAttributeOption.label(UPDATED_LABEL).value(UPDATED_VALUE).description(UPDATED_DESCRIPTION);
        MegaAttributeOptionDTO megaAttributeOptionDTO = megaAttributeOptionMapper.toDto(updatedMegaAttributeOption);

        restMegaAttributeOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaAttributeOptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaAttributeOptionDTO))
            )
            .andExpect(status().isOk());

        // Validate the MegaAttributeOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMegaAttributeOptionToMatchAllProperties(updatedMegaAttributeOption);
    }

    @Test
    @Transactional
    void putNonExistingMegaAttributeOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttributeOption.setId(longCount.incrementAndGet());

        // Create the MegaAttributeOption
        MegaAttributeOptionDTO megaAttributeOptionDTO = megaAttributeOptionMapper.toDto(megaAttributeOption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaAttributeOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, megaAttributeOptionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaAttributeOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaAttributeOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMegaAttributeOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttributeOption.setId(longCount.incrementAndGet());

        // Create the MegaAttributeOption
        MegaAttributeOptionDTO megaAttributeOptionDTO = megaAttributeOptionMapper.toDto(megaAttributeOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaAttributeOptionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(megaAttributeOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaAttributeOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMegaAttributeOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttributeOption.setId(longCount.incrementAndGet());

        // Create the MegaAttributeOption
        MegaAttributeOptionDTO megaAttributeOptionDTO = megaAttributeOptionMapper.toDto(megaAttributeOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaAttributeOptionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(megaAttributeOptionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaAttributeOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMegaAttributeOptionWithPatch() throws Exception {
        // Initialize the database
        insertedMegaAttributeOption = megaAttributeOptionRepository.saveAndFlush(megaAttributeOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaAttributeOption using partial update
        MegaAttributeOption partialUpdatedMegaAttributeOption = new MegaAttributeOption();
        partialUpdatedMegaAttributeOption.setId(megaAttributeOption.getId());

        partialUpdatedMegaAttributeOption.label(UPDATED_LABEL).value(UPDATED_VALUE);

        restMegaAttributeOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaAttributeOption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaAttributeOption))
            )
            .andExpect(status().isOk());

        // Validate the MegaAttributeOption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaAttributeOptionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMegaAttributeOption, megaAttributeOption),
            getPersistedMegaAttributeOption(megaAttributeOption)
        );
    }

    @Test
    @Transactional
    void fullUpdateMegaAttributeOptionWithPatch() throws Exception {
        // Initialize the database
        insertedMegaAttributeOption = megaAttributeOptionRepository.saveAndFlush(megaAttributeOption);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the megaAttributeOption using partial update
        MegaAttributeOption partialUpdatedMegaAttributeOption = new MegaAttributeOption();
        partialUpdatedMegaAttributeOption.setId(megaAttributeOption.getId());

        partialUpdatedMegaAttributeOption.label(UPDATED_LABEL).value(UPDATED_VALUE).description(UPDATED_DESCRIPTION);

        restMegaAttributeOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMegaAttributeOption.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMegaAttributeOption))
            )
            .andExpect(status().isOk());

        // Validate the MegaAttributeOption in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMegaAttributeOptionUpdatableFieldsEquals(
            partialUpdatedMegaAttributeOption,
            getPersistedMegaAttributeOption(partialUpdatedMegaAttributeOption)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMegaAttributeOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttributeOption.setId(longCount.incrementAndGet());

        // Create the MegaAttributeOption
        MegaAttributeOptionDTO megaAttributeOptionDTO = megaAttributeOptionMapper.toDto(megaAttributeOption);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMegaAttributeOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, megaAttributeOptionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaAttributeOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaAttributeOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMegaAttributeOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttributeOption.setId(longCount.incrementAndGet());

        // Create the MegaAttributeOption
        MegaAttributeOptionDTO megaAttributeOptionDTO = megaAttributeOptionMapper.toDto(megaAttributeOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaAttributeOptionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(megaAttributeOptionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MegaAttributeOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMegaAttributeOption() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        megaAttributeOption.setId(longCount.incrementAndGet());

        // Create the MegaAttributeOption
        MegaAttributeOptionDTO megaAttributeOptionDTO = megaAttributeOptionMapper.toDto(megaAttributeOption);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMegaAttributeOptionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(megaAttributeOptionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the MegaAttributeOption in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMegaAttributeOption() throws Exception {
        // Initialize the database
        insertedMegaAttributeOption = megaAttributeOptionRepository.saveAndFlush(megaAttributeOption);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the megaAttributeOption
        restMegaAttributeOptionMockMvc
            .perform(delete(ENTITY_API_URL_ID, megaAttributeOption.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return megaAttributeOptionRepository.count();
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

    protected MegaAttributeOption getPersistedMegaAttributeOption(MegaAttributeOption megaAttributeOption) {
        return megaAttributeOptionRepository.findById(megaAttributeOption.getId()).orElseThrow();
    }

    protected void assertPersistedMegaAttributeOptionToMatchAllProperties(MegaAttributeOption expectedMegaAttributeOption) {
        assertMegaAttributeOptionAllPropertiesEquals(
            expectedMegaAttributeOption,
            getPersistedMegaAttributeOption(expectedMegaAttributeOption)
        );
    }

    protected void assertPersistedMegaAttributeOptionToMatchUpdatableProperties(MegaAttributeOption expectedMegaAttributeOption) {
        assertMegaAttributeOptionAllUpdatablePropertiesEquals(
            expectedMegaAttributeOption,
            getPersistedMegaAttributeOption(expectedMegaAttributeOption)
        );
    }
}
