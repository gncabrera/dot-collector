package com.nookx.api.service;

import com.nookx.api.domain.ProfileRequestType;
import com.nookx.api.repository.ProfileRequestTypeRepository;
import com.nookx.api.service.dto.ProfileRequestTypeDTO;
import com.nookx.api.service.mapper.ProfileRequestTypeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProfileRequestType}.
 */
@Service
@Transactional
public class ProfileRequestTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileRequestTypeService.class);

    private final ProfileRequestTypeRepository profileRequestTypeRepository;

    private final ProfileRequestTypeMapper profileRequestTypeMapper;

    public ProfileRequestTypeService(
        ProfileRequestTypeRepository profileRequestTypeRepository,
        ProfileRequestTypeMapper profileRequestTypeMapper
    ) {
        this.profileRequestTypeRepository = profileRequestTypeRepository;
        this.profileRequestTypeMapper = profileRequestTypeMapper;
    }

    /**
     * Save a profileRequestType.
     *
     * @param profileRequestTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfileRequestTypeDTO save(ProfileRequestTypeDTO profileRequestTypeDTO) {
        LOG.debug("Request to save ProfileRequestType : {}", profileRequestTypeDTO);
        ProfileRequestType profileRequestType = profileRequestTypeMapper.toEntity(profileRequestTypeDTO);
        profileRequestType = profileRequestTypeRepository.save(profileRequestType);
        return profileRequestTypeMapper.toDto(profileRequestType);
    }

    /**
     * Update a profileRequestType.
     *
     * @param profileRequestTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfileRequestTypeDTO update(ProfileRequestTypeDTO profileRequestTypeDTO) {
        LOG.debug("Request to update ProfileRequestType : {}", profileRequestTypeDTO);
        ProfileRequestType profileRequestType = profileRequestTypeMapper.toEntity(profileRequestTypeDTO);
        profileRequestType = profileRequestTypeRepository.save(profileRequestType);
        return profileRequestTypeMapper.toDto(profileRequestType);
    }

    /**
     * Partially update a profileRequestType.
     *
     * @param profileRequestTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProfileRequestTypeDTO> partialUpdate(ProfileRequestTypeDTO profileRequestTypeDTO) {
        LOG.debug("Request to partially update ProfileRequestType : {}", profileRequestTypeDTO);

        return profileRequestTypeRepository
            .findById(profileRequestTypeDTO.getId())
            .map(existingProfileRequestType -> {
                profileRequestTypeMapper.partialUpdate(existingProfileRequestType, profileRequestTypeDTO);

                return existingProfileRequestType;
            })
            .map(profileRequestTypeRepository::save)
            .map(profileRequestTypeMapper::toDto);
    }

    /**
     * Get all the profileRequestTypes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProfileRequestTypeDTO> findAll() {
        LOG.debug("Request to get all ProfileRequestTypes");
        return profileRequestTypeRepository
            .findAll()
            .stream()
            .map(profileRequestTypeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one profileRequestType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProfileRequestTypeDTO> findOne(Long id) {
        LOG.debug("Request to get ProfileRequestType : {}", id);
        return profileRequestTypeRepository.findById(id).map(profileRequestTypeMapper::toDto);
    }

    /**
     * Delete the profileRequestType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProfileRequestType : {}", id);
        profileRequestTypeRepository.deleteById(id);
    }
}
