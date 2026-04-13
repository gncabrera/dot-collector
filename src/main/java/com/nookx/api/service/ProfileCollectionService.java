package com.nookx.api.service;

import com.nookx.api.domain.ProfileCollection;
import com.nookx.api.repository.ProfileCollectionRepository;
import com.nookx.api.service.dto.ProfileCollectionDTO;
import com.nookx.api.service.mapper.ProfileCollectionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProfileCollection}.
 */
@Service
@Transactional
public class ProfileCollectionService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileCollectionService.class);

    private final ProfileCollectionRepository profileCollectionRepository;

    private final ProfileCollectionMapper profileCollectionMapper;

    public ProfileCollectionService(
        ProfileCollectionRepository profileCollectionRepository,
        ProfileCollectionMapper profileCollectionMapper
    ) {
        this.profileCollectionRepository = profileCollectionRepository;
        this.profileCollectionMapper = profileCollectionMapper;
    }

    /**
     * Save a profileCollection.
     *
     * @param profileCollectionDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfileCollectionDTO save(ProfileCollectionDTO profileCollectionDTO) {
        LOG.debug("Request to save ProfileCollection : {}", profileCollectionDTO);
        ProfileCollection profileCollection = profileCollectionMapper.toEntity(profileCollectionDTO);
        profileCollection = profileCollectionRepository.save(profileCollection);
        return profileCollectionMapper.toDto(profileCollection);
    }

    /**
     * Update a profileCollection.
     *
     * @param profileCollectionDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfileCollectionDTO update(ProfileCollectionDTO profileCollectionDTO) {
        LOG.debug("Request to update ProfileCollection : {}", profileCollectionDTO);
        ProfileCollection profileCollection = profileCollectionMapper.toEntity(profileCollectionDTO);
        profileCollection = profileCollectionRepository.save(profileCollection);
        return profileCollectionMapper.toDto(profileCollection);
    }

    /**
     * Partially update a profileCollection.
     *
     * @param profileCollectionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProfileCollectionDTO> partialUpdate(ProfileCollectionDTO profileCollectionDTO) {
        LOG.debug("Request to partially update ProfileCollection : {}", profileCollectionDTO);

        return profileCollectionRepository
            .findById(profileCollectionDTO.getId())
            .map(existingProfileCollection -> {
                profileCollectionMapper.partialUpdate(existingProfileCollection, profileCollectionDTO);

                return existingProfileCollection;
            })
            .map(profileCollectionRepository::save)
            .map(profileCollectionMapper::toDto);
    }

    /**
     * Get all the profileCollections.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProfileCollectionDTO> findAll() {
        LOG.debug("Request to get all ProfileCollections");
        return profileCollectionRepository
            .findAll()
            .stream()
            .map(profileCollectionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one profileCollection by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProfileCollectionDTO> findOne(Long id) {
        LOG.debug("Request to get ProfileCollection : {}", id);
        return profileCollectionRepository.findById(id).map(profileCollectionMapper::toDto);
    }

    /**
     * Delete the profileCollection by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProfileCollection : {}", id);
        profileCollectionRepository.deleteById(id);
    }
}
