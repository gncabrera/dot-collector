package com.dot.collector.api.web.rest;

import static com.dot.collector.api.domain.ProfileCollectionAsserts.*;
import static com.dot.collector.api.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.dot.collector.api.IntegrationTest;
import com.dot.collector.api.domain.ProfileCollection;
import com.dot.collector.api.repository.ProfileCollectionRepository;
import com.dot.collector.api.service.dto.ProfileCollectionDTO;
import com.dot.collector.api.service.mapper.ProfileCollectionMapper;
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
 * Integration tests for the {@link ProfileCollectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProfileCollectionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_IS_PUBLIC = false;
    private static final Boolean UPDATED_IS_PUBLIC = true;

    private static final String ENTITY_API_URL = "/api/profile-collections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProfileCollectionRepository profileCollectionRepository;

    @Autowired
    private ProfileCollectionMapper profileCollectionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restProfileCollectionMockMvc;

    private ProfileCollection profileCollection;

    private ProfileCollection insertedProfileCollection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileCollection createEntity() {
        return new ProfileCollection().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION).isPublic(DEFAULT_IS_PUBLIC);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProfileCollection createUpdatedEntity() {
        return new ProfileCollection().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).isPublic(UPDATED_IS_PUBLIC);
    }

    @BeforeEach
    void initTest() {
        profileCollection = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedProfileCollection != null) {
            profileCollectionRepository.delete(insertedProfileCollection);
            insertedProfileCollection = null;
        }
    }

    @Test
    @Transactional
    void createProfileCollection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProfileCollection
        ProfileCollectionDTO profileCollectionDTO = profileCollectionMapper.toDto(profileCollection);
        var returnedProfileCollectionDTO = om.readValue(
            restProfileCollectionMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileCollectionDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ProfileCollectionDTO.class
        );

        // Validate the ProfileCollection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProfileCollection = profileCollectionMapper.toEntity(returnedProfileCollectionDTO);
        assertProfileCollectionUpdatableFieldsEquals(returnedProfileCollection, getPersistedProfileCollection(returnedProfileCollection));

        insertedProfileCollection = returnedProfileCollection;
    }

    @Test
    @Transactional
    void createProfileCollectionWithExistingId() throws Exception {
        // Create the ProfileCollection with an existing ID
        profileCollection.setId(1L);
        ProfileCollectionDTO profileCollectionDTO = profileCollectionMapper.toDto(profileCollection);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restProfileCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileCollectionDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ProfileCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllProfileCollections() throws Exception {
        // Initialize the database
        insertedProfileCollection = profileCollectionRepository.saveAndFlush(profileCollection);

        // Get all the profileCollectionList
        restProfileCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(profileCollection.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].isPublic").value(hasItem(DEFAULT_IS_PUBLIC)));
    }

    @Test
    @Transactional
    void getProfileCollection() throws Exception {
        // Initialize the database
        insertedProfileCollection = profileCollectionRepository.saveAndFlush(profileCollection);

        // Get the profileCollection
        restProfileCollectionMockMvc
            .perform(get(ENTITY_API_URL_ID, profileCollection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(profileCollection.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.isPublic").value(DEFAULT_IS_PUBLIC));
    }

    @Test
    @Transactional
    void getNonExistingProfileCollection() throws Exception {
        // Get the profileCollection
        restProfileCollectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingProfileCollection() throws Exception {
        // Initialize the database
        insertedProfileCollection = profileCollectionRepository.saveAndFlush(profileCollection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileCollection
        ProfileCollection updatedProfileCollection = profileCollectionRepository.findById(profileCollection.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedProfileCollection are not directly saved in db
        em.detach(updatedProfileCollection);
        updatedProfileCollection.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).isPublic(UPDATED_IS_PUBLIC);
        ProfileCollectionDTO profileCollectionDTO = profileCollectionMapper.toDto(updatedProfileCollection);

        restProfileCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileCollectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileCollectionDTO))
            )
            .andExpect(status().isOk());

        // Validate the ProfileCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProfileCollectionToMatchAllProperties(updatedProfileCollection);
    }

    @Test
    @Transactional
    void putNonExistingProfileCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollection.setId(longCount.incrementAndGet());

        // Create the ProfileCollection
        ProfileCollectionDTO profileCollectionDTO = profileCollectionMapper.toDto(profileCollection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, profileCollectionDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchProfileCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollection.setId(longCount.incrementAndGet());

        // Create the ProfileCollection
        ProfileCollectionDTO profileCollectionDTO = profileCollectionMapper.toDto(profileCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(profileCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamProfileCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollection.setId(longCount.incrementAndGet());

        // Create the ProfileCollection
        ProfileCollectionDTO profileCollectionDTO = profileCollectionMapper.toDto(profileCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileCollectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(profileCollectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfileCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateProfileCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedProfileCollection = profileCollectionRepository.saveAndFlush(profileCollection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileCollection using partial update
        ProfileCollection partialUpdatedProfileCollection = new ProfileCollection();
        partialUpdatedProfileCollection.setId(profileCollection.getId());

        partialUpdatedProfileCollection.title(UPDATED_TITLE).isPublic(UPDATED_IS_PUBLIC);

        restProfileCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfileCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfileCollection))
            )
            .andExpect(status().isOk());

        // Validate the ProfileCollection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileCollectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProfileCollection, profileCollection),
            getPersistedProfileCollection(profileCollection)
        );
    }

    @Test
    @Transactional
    void fullUpdateProfileCollectionWithPatch() throws Exception {
        // Initialize the database
        insertedProfileCollection = profileCollectionRepository.saveAndFlush(profileCollection);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the profileCollection using partial update
        ProfileCollection partialUpdatedProfileCollection = new ProfileCollection();
        partialUpdatedProfileCollection.setId(profileCollection.getId());

        partialUpdatedProfileCollection.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION).isPublic(UPDATED_IS_PUBLIC);

        restProfileCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedProfileCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedProfileCollection))
            )
            .andExpect(status().isOk());

        // Validate the ProfileCollection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProfileCollectionUpdatableFieldsEquals(
            partialUpdatedProfileCollection,
            getPersistedProfileCollection(partialUpdatedProfileCollection)
        );
    }

    @Test
    @Transactional
    void patchNonExistingProfileCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollection.setId(longCount.incrementAndGet());

        // Create the ProfileCollection
        ProfileCollectionDTO profileCollectionDTO = profileCollectionMapper.toDto(profileCollection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProfileCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, profileCollectionDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchProfileCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollection.setId(longCount.incrementAndGet());

        // Create the ProfileCollection
        ProfileCollectionDTO profileCollectionDTO = profileCollectionMapper.toDto(profileCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(profileCollectionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ProfileCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamProfileCollection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        profileCollection.setId(longCount.incrementAndGet());

        // Create the ProfileCollection
        ProfileCollectionDTO profileCollectionDTO = profileCollectionMapper.toDto(profileCollection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProfileCollectionMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(profileCollectionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ProfileCollection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteProfileCollection() throws Exception {
        // Initialize the database
        insertedProfileCollection = profileCollectionRepository.saveAndFlush(profileCollection);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the profileCollection
        restProfileCollectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, profileCollection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return profileCollectionRepository.count();
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

    protected ProfileCollection getPersistedProfileCollection(ProfileCollection profileCollection) {
        return profileCollectionRepository.findById(profileCollection.getId()).orElseThrow();
    }

    protected void assertPersistedProfileCollectionToMatchAllProperties(ProfileCollection expectedProfileCollection) {
        assertProfileCollectionAllPropertiesEquals(expectedProfileCollection, getPersistedProfileCollection(expectedProfileCollection));
    }

    protected void assertPersistedProfileCollectionToMatchUpdatableProperties(ProfileCollection expectedProfileCollection) {
        assertProfileCollectionAllUpdatablePropertiesEquals(
            expectedProfileCollection,
            getPersistedProfileCollection(expectedProfileCollection)
        );
    }
}
