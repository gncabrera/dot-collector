package com.nookx.api.service;

import com.nookx.api.domain.ProfileRequest;
import com.nookx.api.repository.ProfileRequestRepository;
import com.nookx.api.service.dto.ProfileRequestDTO;
import com.nookx.api.service.mapper.ProfileRequestMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProfileRequest}.
 */
@Service
@Transactional
public class ProfileRequestService {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileRequestService.class);

    private final ProfileRequestRepository profileRequestRepository;

    private final ProfileRequestMapper profileRequestMapper;

    public ProfileRequestService(ProfileRequestRepository profileRequestRepository, ProfileRequestMapper profileRequestMapper) {
        this.profileRequestRepository = profileRequestRepository;
        this.profileRequestMapper = profileRequestMapper;
    }

    /**
     * Save a profileRequest.
     *
     * @param profileRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfileRequestDTO save(ProfileRequestDTO profileRequestDTO) {
        LOG.debug("Request to save ProfileRequest : {}", profileRequestDTO);
        ProfileRequest profileRequest = profileRequestMapper.toEntity(profileRequestDTO);
        profileRequest = profileRequestRepository.save(profileRequest);
        return profileRequestMapper.toDto(profileRequest);
    }

    /**
     * Update a profileRequest.
     *
     * @param profileRequestDTO the entity to save.
     * @return the persisted entity.
     */
    public ProfileRequestDTO update(ProfileRequestDTO profileRequestDTO) {
        LOG.debug("Request to update ProfileRequest : {}", profileRequestDTO);
        ProfileRequest profileRequest = profileRequestMapper.toEntity(profileRequestDTO);
        profileRequest = profileRequestRepository.save(profileRequest);
        return profileRequestMapper.toDto(profileRequest);
    }

    /**
     * Partially update a profileRequest.
     *
     * @param profileRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProfileRequestDTO> partialUpdate(ProfileRequestDTO profileRequestDTO) {
        LOG.debug("Request to partially update ProfileRequest : {}", profileRequestDTO);

        return profileRequestRepository
            .findById(profileRequestDTO.getId())
            .map(existingProfileRequest -> {
                profileRequestMapper.partialUpdate(existingProfileRequest, profileRequestDTO);

                return existingProfileRequest;
            })
            .map(profileRequestRepository::save)
            .map(profileRequestMapper::toDto);
    }

    /**
     * Get all the profileRequests.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProfileRequestDTO> findAll() {
        LOG.debug("Request to get all ProfileRequests");
        return profileRequestRepository
            .findAll()
            .stream()
            .map(profileRequestMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one profileRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProfileRequestDTO> findOne(Long id) {
        LOG.debug("Request to get ProfileRequest : {}", id);
        return profileRequestRepository.findById(id).map(profileRequestMapper::toDto);
    }

    /**
     * Delete the profileRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete ProfileRequest : {}", id);
        profileRequestRepository.deleteById(id);
    }
}
