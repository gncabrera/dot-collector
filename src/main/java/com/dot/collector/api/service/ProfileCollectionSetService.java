package com.dot.collector.api.service;

import com.dot.collector.api.domain.ProfileCollectionSet;
import com.dot.collector.api.repository.ProfileCollectionSetRepository;
import com.dot.collector.api.service.dto.ProfileCollectionSetDTO;
import com.dot.collector.api.service.mapper.ProfileCollectionSetMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dot.collector.api.domain.ProfileCollectionSet}.
 */
@Service
@Transactional
public class ProfileCollectionSetService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileCollectionSetService.class);

    private final ProfileCollectionSetRepository profileCollectionSetRepository;

    private final ProfileCollectionSetMapper profileCollectionSetMapper;

    public ProfileCollectionSetService(
        ProfileCollectionSetRepository profileCollectionSetRepository,
        ProfileCollectionSetMapper profileCollectionSetMapper
    ) {
        this.profileCollectionSetRepository = profileCollectionSetRepository;
        this.profileCollectionSetMapper = profileCollectionSetMapper;
    }

    /**
     * Save a profileCollectionSet.
     *
     * @param profileCollectionSetDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfileCollectionSetDTO save(ProfileCollectionSetDTO profileCollectionSetDTO) {
        LOG.debug("Request to save ProfileCollectionSet : {}", profileCollectionSetDTO);
        ProfileCollectionSet profileCollectionSet = profileCollectionSetMapper.toEntity(profileCollectionSetDTO);
        profileCollectionSet = profileCollectionSetRepository.save(profileCollectionSet);
        return profileCollectionSetMapper.toDto(profileCollectionSet);
    }

    /**
     * Update a profileCollectionSet.
     *
     * @param profileCollectionSetDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfileCollectionSetDTO update(ProfileCollectionSetDTO profileCollectionSetDTO) {
        LOG.debug("Request to update ProfileCollectionSet : {}", profileCollectionSetDTO);
        ProfileCollectionSet profileCollectionSet = profileCollectionSetMapper.toEntity(profileCollectionSetDTO);
        profileCollectionSet = profileCollectionSetRepository.save(profileCollectionSet);
        return profileCollectionSetMapper.toDto(profileCollectionSet);
    }

    /**
     * Partially update a profileCollectionSet.
     *
     * @param profileCollectionSetDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProfileCollectionSetDTO> partialUpdate(ProfileCollectionSetDTO profileCollectionSetDTO) {
        LOG.debug("Request to partially update ProfileCollectionSet : {}", profileCollectionSetDTO);

        return profileCollectionSetRepository
            .findById(profileCollectionSetDTO.getId())
            .map(existingProfileCollectionSet -> {
                profileCollectionSetMapper.partialUpdate(existingProfileCollectionSet, profileCollectionSetDTO);

                return existingProfileCollectionSet;
            })
            .map(profileCollectionSetRepository::save)
            .map(profileCollectionSetMapper::toDto);
    }

    /**
     * Get all the profileCollectionSets.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProfileCollectionSetDTO> findAll() {
        LOG.debug("Request to get all ProfileCollectionSets");
        return profileCollectionSetRepository
            .findAll()
            .stream()
            .map(profileCollectionSetMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the profileCollectionSets with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<ProfileCollectionSetDTO> findAllWithEagerRelationships(Pageable pageable) {
        return profileCollectionSetRepository.findAllWithEagerRelationships(pageable).map(profileCollectionSetMapper::toDto);
    }

    /**
     * Get one profileCollectionSet by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProfileCollectionSetDTO> findOne(Long id) {
        LOG.debug("Request to get ProfileCollectionSet : {}", id);
        return profileCollectionSetRepository.findOneWithEagerRelationships(id).map(profileCollectionSetMapper::toDto);
    }

    /**
     * Delete the profileCollectionSet by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProfileCollectionSet : {}", id);
        profileCollectionSetRepository.deleteById(id);
    }
}
