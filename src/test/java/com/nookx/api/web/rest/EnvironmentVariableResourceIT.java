package com.nookx.api.web.rest;

import static com.nookx.api.domain.EnvironmentVariableAsserts.*;
import static com.nookx.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookx.api.IntegrationTest;
import com.nookx.api.domain.EnvironmentVariable;
import com.nookx.api.repository.EnvironmentVariableRepository;
import com.nookx.api.service.dto.EnvironmentVariableDTO;
import com.nookx.api.service.mapper.EnvironmentVariableMapper;
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
 * Integration tests for the {@link EnvironmentVariableResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EnvironmentVariableResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/environment-variables";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EnvironmentVariableRepository environmentVariableRepository;

    @Autowired
    private EnvironmentVariableMapper environmentVariableMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEnvironmentVariableMockMvc;

    private EnvironmentVariable environmentVariable;

    private EnvironmentVariable insertedEnvironmentVariable;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnvironmentVariable createEntity() {
        return new EnvironmentVariable().key(DEFAULT_KEY).value(DEFAULT_VALUE).description(DEFAULT_DESCRIPTION).type(DEFAULT_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EnvironmentVariable createUpdatedEntity() {
        return new EnvironmentVariable().key(UPDATED_KEY).value(UPDATED_VALUE).description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);
    }

    @BeforeEach
    void initTest() {
        environmentVariable = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEnvironmentVariable != null) {
            environmentVariableRepository.delete(insertedEnvironmentVariable);
            insertedEnvironmentVariable = null;
        }
    }

    @Test
    @Transactional
    void createEnvironmentVariable() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EnvironmentVariable
        EnvironmentVariableDTO environmentVariableDTO = environmentVariableMapper.toDto(environmentVariable);
        var returnedEnvironmentVariableDTO = om.readValue(
            restEnvironmentVariableMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentVariableDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EnvironmentVariableDTO.class
        );

        // Validate the EnvironmentVariable in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEnvironmentVariable = environmentVariableMapper.toEntity(returnedEnvironmentVariableDTO);
        assertEnvironmentVariableUpdatableFieldsEquals(
            returnedEnvironmentVariable,
            getPersistedEnvironmentVariable(returnedEnvironmentVariable)
        );

        insertedEnvironmentVariable = returnedEnvironmentVariable;
    }

    @Test
    @Transactional
    void createEnvironmentVariableWithExistingId() throws Exception {
        // Create the EnvironmentVariable with an existing ID
        environmentVariable.setId(1L);
        EnvironmentVariableDTO environmentVariableDTO = environmentVariableMapper.toDto(environmentVariable);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEnvironmentVariableMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentVariableDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentVariable in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEnvironmentVariables() throws Exception {
        // Initialize the database
        insertedEnvironmentVariable = environmentVariableRepository.saveAndFlush(environmentVariable);

        // Get all the environmentVariableList
        restEnvironmentVariableMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(environmentVariable.getId().intValue())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)));
    }

    @Test
    @Transactional
    void getEnvironmentVariable() throws Exception {
        // Initialize the database
        insertedEnvironmentVariable = environmentVariableRepository.saveAndFlush(environmentVariable);

        // Get the environmentVariable
        restEnvironmentVariableMockMvc
            .perform(get(ENTITY_API_URL_ID, environmentVariable.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(environmentVariable.getId().intValue()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE));
    }

    @Test
    @Transactional
    void getNonExistingEnvironmentVariable() throws Exception {
        // Get the environmentVariable
        restEnvironmentVariableMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEnvironmentVariable() throws Exception {
        // Initialize the database
        insertedEnvironmentVariable = environmentVariableRepository.saveAndFlush(environmentVariable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the environmentVariable
        EnvironmentVariable updatedEnvironmentVariable = environmentVariableRepository.findById(environmentVariable.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEnvironmentVariable are not directly saved in db
        em.detach(updatedEnvironmentVariable);
        updatedEnvironmentVariable.key(UPDATED_KEY).value(UPDATED_VALUE).description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);
        EnvironmentVariableDTO environmentVariableDTO = environmentVariableMapper.toDto(updatedEnvironmentVariable);

        restEnvironmentVariableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, environmentVariableDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(environmentVariableDTO))
            )
            .andExpect(status().isOk());

        // Validate the EnvironmentVariable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEnvironmentVariableToMatchAllProperties(updatedEnvironmentVariable);
    }

    @Test
    @Transactional
    void putNonExistingEnvironmentVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentVariable.setId(longCount.incrementAndGet());

        // Create the EnvironmentVariable
        EnvironmentVariableDTO environmentVariableDTO = environmentVariableMapper.toDto(environmentVariable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentVariableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, environmentVariableDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(environmentVariableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentVariable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEnvironmentVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentVariable.setId(longCount.incrementAndGet());

        // Create the EnvironmentVariable
        EnvironmentVariableDTO environmentVariableDTO = environmentVariableMapper.toDto(environmentVariable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentVariableMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(environmentVariableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentVariable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEnvironmentVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentVariable.setId(longCount.incrementAndGet());

        // Create the EnvironmentVariable
        EnvironmentVariableDTO environmentVariableDTO = environmentVariableMapper.toDto(environmentVariable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentVariableMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(environmentVariableDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EnvironmentVariable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEnvironmentVariableWithPatch() throws Exception {
        // Initialize the database
        insertedEnvironmentVariable = environmentVariableRepository.saveAndFlush(environmentVariable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the environmentVariable using partial update
        EnvironmentVariable partialUpdatedEnvironmentVariable = new EnvironmentVariable();
        partialUpdatedEnvironmentVariable.setId(environmentVariable.getId());

        partialUpdatedEnvironmentVariable.description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);

        restEnvironmentVariableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvironmentVariable.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnvironmentVariable))
            )
            .andExpect(status().isOk());

        // Validate the EnvironmentVariable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvironmentVariableUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEnvironmentVariable, environmentVariable),
            getPersistedEnvironmentVariable(environmentVariable)
        );
    }

    @Test
    @Transactional
    void fullUpdateEnvironmentVariableWithPatch() throws Exception {
        // Initialize the database
        insertedEnvironmentVariable = environmentVariableRepository.saveAndFlush(environmentVariable);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the environmentVariable using partial update
        EnvironmentVariable partialUpdatedEnvironmentVariable = new EnvironmentVariable();
        partialUpdatedEnvironmentVariable.setId(environmentVariable.getId());

        partialUpdatedEnvironmentVariable.key(UPDATED_KEY).value(UPDATED_VALUE).description(UPDATED_DESCRIPTION).type(UPDATED_TYPE);

        restEnvironmentVariableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEnvironmentVariable.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEnvironmentVariable))
            )
            .andExpect(status().isOk());

        // Validate the EnvironmentVariable in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEnvironmentVariableUpdatableFieldsEquals(
            partialUpdatedEnvironmentVariable,
            getPersistedEnvironmentVariable(partialUpdatedEnvironmentVariable)
        );
    }

    @Test
    @Transactional
    void patchNonExistingEnvironmentVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentVariable.setId(longCount.incrementAndGet());

        // Create the EnvironmentVariable
        EnvironmentVariableDTO environmentVariableDTO = environmentVariableMapper.toDto(environmentVariable);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEnvironmentVariableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, environmentVariableDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(environmentVariableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentVariable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEnvironmentVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentVariable.setId(longCount.incrementAndGet());

        // Create the EnvironmentVariable
        EnvironmentVariableDTO environmentVariableDTO = environmentVariableMapper.toDto(environmentVariable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentVariableMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(environmentVariableDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the EnvironmentVariable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEnvironmentVariable() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        environmentVariable.setId(longCount.incrementAndGet());

        // Create the EnvironmentVariable
        EnvironmentVariableDTO environmentVariableDTO = environmentVariableMapper.toDto(environmentVariable);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEnvironmentVariableMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(environmentVariableDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the EnvironmentVariable in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEnvironmentVariable() throws Exception {
        // Initialize the database
        insertedEnvironmentVariable = environmentVariableRepository.saveAndFlush(environmentVariable);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the environmentVariable
        restEnvironmentVariableMockMvc
            .perform(delete(ENTITY_API_URL_ID, environmentVariable.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return environmentVariableRepository.count();
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

    protected EnvironmentVariable getPersistedEnvironmentVariable(EnvironmentVariable environmentVariable) {
        return environmentVariableRepository.findById(environmentVariable.getId()).orElseThrow();
    }

    protected void assertPersistedEnvironmentVariableToMatchAllProperties(EnvironmentVariable expectedEnvironmentVariable) {
        assertEnvironmentVariableAllPropertiesEquals(
            expectedEnvironmentVariable,
            getPersistedEnvironmentVariable(expectedEnvironmentVariable)
        );
    }

    protected void assertPersistedEnvironmentVariableToMatchUpdatableProperties(EnvironmentVariable expectedEnvironmentVariable) {
        assertEnvironmentVariableAllUpdatablePropertiesEquals(
            expectedEnvironmentVariable,
            getPersistedEnvironmentVariable(expectedEnvironmentVariable)
        );
    }
}
