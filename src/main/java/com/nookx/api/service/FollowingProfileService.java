package com.nookx.api.service;

import com.nookx.api.domain.FollowingProfile;
import com.nookx.api.repository.FollowingProfileRepository;
import com.nookx.api.service.dto.FollowingProfileDTO;
import com.nookx.api.service.mapper.FollowingProfileMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link FollowingProfile}.
 */
@Service
@Transactional
public class FollowingProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(FollowingProfileService.class);

    private final FollowingProfileRepository followingProfileRepository;

    private final FollowingProfileMapper followingProfileMapper;

    public FollowingProfileService(FollowingProfileRepository followingProfileRepository, FollowingProfileMapper followingProfileMapper) {
        this.followingProfileRepository = followingProfileRepository;
        this.followingProfileMapper = followingProfileMapper;
    }

    /**
     * Save a followingProfile.
     *
     * @param followingProfileDTO the entity to save.
     * @return the persisted entity.
     */
    public FollowingProfileDTO save(FollowingProfileDTO followingProfileDTO) {
        LOG.debug("Request to save FollowingProfile : {}", followingProfileDTO);
        FollowingProfile followingProfile = followingProfileMapper.toEntity(followingProfileDTO);
        followingProfile = followingProfileRepository.save(followingProfile);
        return followingProfileMapper.toDto(followingProfile);
    }

    /**
     * Update a followingProfile.
     *
     * @param followingProfileDTO the entity to save.
     * @return the persisted entity.
     */
    public FollowingProfileDTO update(FollowingProfileDTO followingProfileDTO) {
        LOG.debug("Request to update FollowingProfile : {}", followingProfileDTO);
        FollowingProfile followingProfile = followingProfileMapper.toEntity(followingProfileDTO);
        followingProfile = followingProfileRepository.save(followingProfile);
        return followingProfileMapper.toDto(followingProfile);
    }

    /**
     * Partially update a followingProfile.
     *
     * @param followingProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FollowingProfileDTO> partialUpdate(FollowingProfileDTO followingProfileDTO) {
        LOG.debug("Request to partially update FollowingProfile : {}", followingProfileDTO);

        return followingProfileRepository
            .findById(followingProfileDTO.getId())
            .map(existingFollowingProfile -> {
                followingProfileMapper.partialUpdate(existingFollowingProfile, followingProfileDTO);

                return existingFollowingProfile;
            })
            .map(followingProfileRepository::save)
            .map(followingProfileMapper::toDto);
    }

    /**
     * Get all the followingProfiles.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<FollowingProfileDTO> findAll() {
        LOG.debug("Request to get all FollowingProfiles");
        return followingProfileRepository
            .findAll()
            .stream()
            .map(followingProfileMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one followingProfile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<FollowingProfileDTO> findOne(Long id) {
        LOG.debug("Request to get FollowingProfile : {}", id);
        return followingProfileRepository.findById(id).map(followingProfileMapper::toDto);
    }

    /**
     * Delete the followingProfile by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete FollowingProfile : {}", id);
        followingProfileRepository.deleteById(id);
    }
}
