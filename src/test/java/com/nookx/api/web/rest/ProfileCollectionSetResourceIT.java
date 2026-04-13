package com.nookx.api.web.rest;

import static com.nookx.api.domain.ProfileCollectionSetAsserts.*;
import static com.nookx.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nookx.api.IntegrationTest;
import com.nookx.api.domain.ProfileCollectionSet;
import com.nookx.api.repository.ProfileCollectionSetRepository;
import com.nookx.api.service.ProfileCollectionSetService;
import com.nookx.api.service.dto.ProfileCollectionSetDTO;
import com.nookx.api.service.mapper.ProfileCollectionSetMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link ProfileCollectionSetResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ProfileCollectionSetResourceIT {

    private static final Boolean DEFAULT_OWNED = false;
    private static final Boolean UPDATED_OWNED = true;

    private static final Boolean DEFAULT_WANTED = false;
    private static final Boolean UPDATED_WANTED = true;

    private static final LocalDate DEFAULT_DATE_ADDED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ADDED = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/profile-collection-sets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProfileCollectionSetRepository profileCollectionSetRepository;

    @Mock
    private ProfileCollectionSetRepository profileCollectionSetRepositoryMock;

    @Autowired
    private ProfileCollectionSetMapper profileCollectionSetMapper;

    @Mock
    private ProfileCollectionSetService profileCollectionSetServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfileCollectionSetMockMvc;

    private ProfileCollectionSet profileCollectionSet;

    private ProfileCollectionSet insertedProfileCollectionSet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileCollectionSet createEntity() {
        return new ProfileCollectionSet().owned(DEFAULT_OWNED).wanted(DEFAULT_WANTED).dateAdded(DEFAULT_DATE_ADDED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileCollectionSet createUpdatedEntity() {
        return new ProfileCollectionSet().owned(UPDATED_OWNED).wanted(UPDATED_WANTED).dateAdded(UPDATED_DATE_ADDED);
    }

    @BeforeEach
    void initTest() {
        profileCollectionSet = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProfileCollectionSet != null) {
            profileCollectionSetRepository.delete(insertedProfileCollectionSet);
            insertedProfileCollectionSet = null;
        }
    }

    @Test
    @Transactional
    void createProfileCollectionSet() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProfileCollectionSet
        ProfileCollectionSetDTO profileCollectionSetDTO = profileCollectionSetMapper.toDto(profileCollectionSet);
        var returnedProfileCollectionSetDTO = om.readValue(
            restProfileCollectionSetMockMvc
                .perform(
                    post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileCollectionSetDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProfileCollectionSetDTO.class
        );

        // Validate the ProfileCollectionSet in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProfileCollectionSet = profileCollectionSetMapper.toEntity(returnedProfileCollectionSetDTO);
        assertProfileCollectionSetUpdatableFieldsEquals(
            returnedProfileCollectionSet,
            getPersistedProfileCollectionSet(returnedProfileCollectionSet)
        );

        insertedProfileCollectionSet = returnedProfileCollectionSet;
    }

    @Test
    @Transactional
    void createProfileCollectionSetWithExistingId() throws Exception {
        // Create the ProfileCollectionSet with an existing ID
        profileCollectionSet.setId(1L);
        ProfileCollectionSetDTO profileCollectionSetDTO = profileCollectionSetMapper.toDto(profileCollectionSet);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfileCollectionSetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileCollectionSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProfileCollectionSet in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProfileCollectionSets() throws Exception {
        // Initialize the database
        insertedProfileCollectionSet = profileCollectionSetRepository.saveAndFlush(profileCollectionSet);

        // Get all the profileCollectionSetList
        restProfileCollectionSetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profileCollectionSet.getId().intValue())))
            .andExpect(jsonPath("$.[*].owned").value(hasItem(DEFAULT_OWNED)))
            .andExpect(jsonPath("$.[*].wanted").value(hasItem(DEFAULT_WANTED)))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfileCollectionSetsWithEagerRelationshipsIsEnabled() throws Exception {
        when(profileCollectionSetServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProfileCollectionSetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(profileCollectionSetServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProfileCollectionSetsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(profileCollectionSetServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restProfileCollectionSetMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(profileCollectionSetRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getProfileCollectionSet() throws Exception {
        // Initialize the database
        insertedProfileCollectionSet = profileCollectionSetRepository.saveAndFlush(profileCollectionSet);

        // Get the profileCollectionSet
        restProfileCollectionSetMockMvc
            .perform(get(ENTITY_API_URL_ID, profileCollectionSet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(profileCollectionSet.getId().intValue()))
            .andExpect(jsonPath("$.owned").value(DEFAULT_OWNED))
            .andExpect(jsonPath("$.wanted").value(DEFAULT_WANTED))
            .andExpect(jsonPath("$.dateAdded").value(DEFAULT_DATE_ADDED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingProfileCollectionSet() throws Exception {
        // Get the profileCollectionSet
        restProfileCollectionSetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProfileCollectionSet() throws Exception {
        // Initialize the database
        insertedProfileCollectionSet = profileCollectionSetRepository.saveAndFlush(profileCollectionSet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileCollectionSet
        ProfileCollectionSet updatedProfileCollectionSet = profileCollectionSetRepository
            .findById(profileCollectionSet.getId())
            .orElseThrow();
        // Disconnect from session so that the updates on updatedProfileCollectionSet are not directly saved in db
        em.detach(updatedProfileCollectionSet);
        updatedProfileCollectionSet.owned(UPDATED_OWNED).wanted(UPDATED_WANTED).dateAdded(UPDATED_DATE_ADDED);
        ProfileCollectionSetDTO profileCollectionSetDTO = profileCollectionSetMapper.toDto(updatedProfileCollectionSet);

        restProfileCollectionSetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileCollectionSetDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileCollectionSetDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProfileCollectionSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProfileCollectionSetToMatchAllProperties(updatedProfileCollectionSet);
    }

    @Test
    @Transactional
    void putNonExistingProfileCollectionSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollectionSet.setId(longCount.incrementAndGet());

        // Create the ProfileCollectionSet
        ProfileCollectionSetDTO profileCollectionSetDTO = profileCollectionSetMapper.toDto(profileCollectionSet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileCollectionSetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileCollectionSetDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileCollectionSetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileCollectionSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfileCollectionSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollectionSet.setId(longCount.incrementAndGet());

        // Create the ProfileCollectionSet
        ProfileCollectionSetDTO profileCollectionSetDTO = profileCollectionSetMapper.toDto(profileCollectionSet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileCollectionSetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileCollectionSetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileCollectionSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfileCollectionSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollectionSet.setId(longCount.incrementAndGet());

        // Create the ProfileCollectionSet
        ProfileCollectionSetDTO profileCollectionSetDTO = profileCollectionSetMapper.toDto(profileCollectionSet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileCollectionSetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileCollectionSetDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfileCollectionSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfileCollectionSetWithPatch() throws Exception {
        // Initialize the database
        insertedProfileCollectionSet = profileCollectionSetRepository.saveAndFlush(profileCollectionSet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileCollectionSet using partial update
        ProfileCollectionSet partialUpdatedProfileCollectionSet = new ProfileCollectionSet();
        partialUpdatedProfileCollectionSet.setId(profileCollectionSet.getId());

        partialUpdatedProfileCollectionSet.wanted(UPDATED_WANTED);

        restProfileCollectionSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfileCollectionSet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfileCollectionSet))
            )
            .andExpect(status().isOk());

        // Validate the ProfileCollectionSet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileCollectionSetUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProfileCollectionSet, profileCollectionSet),
            getPersistedProfileCollectionSet(profileCollectionSet)
        );
    }

    @Test
    @Transactional
    void fullUpdateProfileCollectionSetWithPatch() throws Exception {
        // Initialize the database
        insertedProfileCollectionSet = profileCollectionSetRepository.saveAndFlush(profileCollectionSet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileCollectionSet using partial update
        ProfileCollectionSet partialUpdatedProfileCollectionSet = new ProfileCollectionSet();
        partialUpdatedProfileCollectionSet.setId(profileCollectionSet.getId());

        partialUpdatedProfileCollectionSet.owned(UPDATED_OWNED).wanted(UPDATED_WANTED).dateAdded(UPDATED_DATE_ADDED);

        restProfileCollectionSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfileCollectionSet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfileCollectionSet))
            )
            .andExpect(status().isOk());

        // Validate the ProfileCollectionSet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileCollectionSetUpdatableFieldsEquals(
            partialUpdatedProfileCollectionSet,
            getPersistedProfileCollectionSet(partialUpdatedProfileCollectionSet)
        );
    }

    @Test
    @Transactional
    void patchNonExistingProfileCollectionSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollectionSet.setId(longCount.incrementAndGet());

        // Create the ProfileCollectionSet
        ProfileCollectionSetDTO profileCollectionSetDTO = profileCollectionSetMapper.toDto(profileCollectionSet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileCollectionSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, profileCollectionSetDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileCollectionSetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileCollectionSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfileCollectionSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollectionSet.setId(longCount.incrementAndGet());

        // Create the ProfileCollectionSet
        ProfileCollectionSetDTO profileCollectionSetDTO = profileCollectionSetMapper.toDto(profileCollectionSet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileCollectionSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileCollectionSetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileCollectionSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfileCollectionSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollectionSet.setId(longCount.incrementAndGet());

        // Create the ProfileCollectionSet
        ProfileCollectionSetDTO profileCollectionSetDTO = profileCollectionSetMapper.toDto(profileCollectionSet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileCollectionSetMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(profileCollectionSetDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfileCollectionSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfileCollectionSet() throws Exception {
        // Initialize the database
        insertedProfileCollectionSet = profileCollectionSetRepository.saveAndFlush(profileCollectionSet);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the profileCollectionSet
        restProfileCollectionSetMockMvc
            .perform(delete(ENTITY_API_URL_ID, profileCollectionSet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return profileCollectionSetRepository.count();
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

    protected ProfileCollectionSet getPersistedProfileCollectionSet(ProfileCollectionSet profileCollectionSet) {
        return profileCollectionSetRepository.findById(profileCollectionSet.getId()).orElseThrow();
    }

    protected void assertPersistedProfileCollectionSetToMatchAllProperties(ProfileCollectionSet expectedProfileCollectionSet) {
        assertProfileCollectionSetAllPropertiesEquals(
            expectedProfileCollectionSet,
            getPersistedProfileCollectionSet(expectedProfileCollectionSet)
        );
    }

    protected void assertPersistedProfileCollectionSetToMatchUpdatableProperties(ProfileCollectionSet expectedProfileCollectionSet) {
        assertProfileCollectionSetAllUpdatablePropertiesEquals(
            expectedProfileCollectionSet,
            getPersistedProfileCollectionSet(expectedProfileCollectionSet)
        );
    }
}
