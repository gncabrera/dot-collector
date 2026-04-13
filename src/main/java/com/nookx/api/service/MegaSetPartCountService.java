package com.nookx.api.service;

import com.nookx.api.domain.MegaSetPartCount;
import com.nookx.api.repository.MegaSetPartCountRepository;
import com.nookx.api.service.dto.MegaSetPartCountDTO;
import com.nookx.api.service.mapper.MegaSetPartCountMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MegaSetPartCount}.
 */
@Service
@Transactional
public class MegaSetPartCountService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaSetPartCountService.class);

    private final MegaSetPartCountRepository megaSetPartCountRepository;

    private final MegaSetPartCountMapper megaSetPartCountMapper;

    public MegaSetPartCountService(MegaSetPartCountRepository megaSetPartCountRepository, MegaSetPartCountMapper megaSetPartCountMapper) {
        this.megaSetPartCountRepository = megaSetPartCountRepository;
        this.megaSetPartCountMapper = megaSetPartCountMapper;
    }

    /**
     * Save a megaSetPartCount.
     *
     * @param megaSetPartCountDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaSetPartCountDTO save(MegaSetPartCountDTO megaSetPartCountDTO) {
        LOG.debug("Request to save MegaSetPartCount : {}", megaSetPartCountDTO);
        MegaSetPartCount megaSetPartCount = megaSetPartCountMapper.toEntity(megaSetPartCountDTO);
        megaSetPartCount = megaSetPartCountRepository.save(megaSetPartCount);
        return megaSetPartCountMapper.toDto(megaSetPartCount);
    }

    /**
     * Update a megaSetPartCount.
     *
     * @param megaSetPartCountDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaSetPartCountDTO update(MegaSetPartCountDTO megaSetPartCountDTO) {
        LOG.debug("Request to update MegaSetPartCount : {}", megaSetPartCountDTO);
        MegaSetPartCount megaSetPartCount = megaSetPartCountMapper.toEntity(megaSetPartCountDTO);
        megaSetPartCount = megaSetPartCountRepository.save(megaSetPartCount);
        return megaSetPartCountMapper.toDto(megaSetPartCount);
    }

    /**
     * Partially update a megaSetPartCount.
     *
     * @param megaSetPartCountDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MegaSetPartCountDTO> partialUpdate(MegaSetPartCountDTO megaSetPartCountDTO) {
        LOG.debug("Request to partially update MegaSetPartCount : {}", megaSetPartCountDTO);

        return megaSetPartCountRepository
            .findById(megaSetPartCountDTO.getId())
            .map(existingMegaSetPartCount -> {
                megaSetPartCountMapper.partialUpdate(existingMegaSetPartCount, megaSetPartCountDTO);

                return existingMegaSetPartCount;
            })
            .map(megaSetPartCountRepository::save)
            .map(megaSetPartCountMapper::toDto);
    }

    /**
     * Get all the megaSetPartCounts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MegaSetPartCountDTO> findAll() {
        LOG.debug("Request to get all MegaSetPartCounts");
        return megaSetPartCountRepository
            .findAll()
            .stream()
            .map(megaSetPartCountMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one megaSetPartCount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MegaSetPartCountDTO> findOne(Long id) {
        LOG.debug("Request to get MegaSetPartCount : {}", id);
        return megaSetPartCountRepository.findById(id).map(megaSetPartCountMapper::toDto);
    }

    /**
     * Delete the megaSetPartCount by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MegaSetPartCount : {}", id);
        megaSetPartCountRepository.deleteById(id);
    }
}
