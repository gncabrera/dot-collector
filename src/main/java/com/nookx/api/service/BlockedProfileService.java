package com.nookx.api.service;

import com.nookx.api.domain.BlockedProfile;
import com.nookx.api.repository.BlockedProfileRepository;
import com.nookx.api.service.dto.BlockedProfileDTO;
import com.nookx.api.service.mapper.BlockedProfileMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link BlockedProfile}.
 */
@Service
@Transactional
public class BlockedProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(BlockedProfileService.class);

    private final BlockedProfileRepository blockedProfileRepository;

    private final BlockedProfileMapper blockedProfileMapper;

    public BlockedProfileService(BlockedProfileRepository blockedProfileRepository, BlockedProfileMapper blockedProfileMapper) {
        this.blockedProfileRepository = blockedProfileRepository;
        this.blockedProfileMapper = blockedProfileMapper;
    }

    /**
     * Save a blockedProfile.
     *
     * @param blockedProfileDTO the entity to save.
     * @return the persisted entity.
     */
    public BlockedProfileDTO save(BlockedProfileDTO blockedProfileDTO) {
        LOG.debug("Request to save BlockedProfile : {}", blockedProfileDTO);
        BlockedProfile blockedProfile = blockedProfileMapper.toEntity(blockedProfileDTO);
        blockedProfile = blockedProfileRepository.save(blockedProfile);
        return blockedProfileMapper.toDto(blockedProfile);
    }

    /**
     * Update a blockedProfile.
     *
     * @param blockedProfileDTO the entity to save.
     * @return the persisted entity.
     */
    public BlockedProfileDTO update(BlockedProfileDTO blockedProfileDTO) {
        LOG.debug("Request to update BlockedProfile : {}", blockedProfileDTO);
        BlockedProfile blockedProfile = blockedProfileMapper.toEntity(blockedProfileDTO);
        blockedProfile = blockedProfileRepository.save(blockedProfile);
        return blockedProfileMapper.toDto(blockedProfile);
    }

    /**
     * Partially update a blockedProfile.
     *
     * @param blockedProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<BlockedProfileDTO> partialUpdate(BlockedProfileDTO blockedProfileDTO) {
        LOG.debug("Request to partially update BlockedProfile : {}", blockedProfileDTO);

        return blockedProfileRepository
            .findById(blockedProfileDTO.getId())
            .map(existingBlockedProfile -> {
                blockedProfileMapper.partialUpdate(existingBlockedProfile, blockedProfileDTO);

                return existingBlockedProfile;
            })
            .map(blockedProfileRepository::save)
            .map(blockedProfileMapper::toDto);
    }

    /**
     * Get all the blockedProfiles.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<BlockedProfileDTO> findAll() {
        LOG.debug("Request to get all BlockedProfiles");
        return blockedProfileRepository
            .findAll()
            .stream()
            .map(blockedProfileMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one blockedProfile by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<BlockedProfileDTO> findOne(Long id) {
        LOG.debug("Request to get BlockedProfile : {}", id);
        return blockedProfileRepository.findById(id).map(blockedProfileMapper::toDto);
    }

    /**
     * Delete the blockedProfile by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete BlockedProfile : {}", id);
        blockedProfileRepository.deleteById(id);
    }
}
