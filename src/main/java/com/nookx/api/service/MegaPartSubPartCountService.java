package com.nookx.api.service;

import com.nookx.api.domain.MegaPartSubPartCount;
import com.nookx.api.repository.MegaPartSubPartCountRepository;
import com.nookx.api.service.dto.MegaPartSubPartCountDTO;
import com.nookx.api.service.mapper.MegaPartSubPartCountMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MegaPartSubPartCount}.
 */
@Service
@Transactional
public class MegaPartSubPartCountService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaPartSubPartCountService.class);

    private final MegaPartSubPartCountRepository megaPartSubPartCountRepository;

    private final MegaPartSubPartCountMapper megaPartSubPartCountMapper;

    public MegaPartSubPartCountService(
        MegaPartSubPartCountRepository megaPartSubPartCountRepository,
        MegaPartSubPartCountMapper megaPartSubPartCountMapper
    ) {
        this.megaPartSubPartCountRepository = megaPartSubPartCountRepository;
        this.megaPartSubPartCountMapper = megaPartSubPartCountMapper;
    }

    /**
     * Save a megaPartSubPartCount.
     *
     * @param megaPartSubPartCountDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaPartSubPartCountDTO save(MegaPartSubPartCountDTO megaPartSubPartCountDTO) {
        LOG.debug("Request to save MegaPartSubPartCount : {}", megaPartSubPartCountDTO);
        MegaPartSubPartCount megaPartSubPartCount = megaPartSubPartCountMapper.toEntity(megaPartSubPartCountDTO);
        megaPartSubPartCount = megaPartSubPartCountRepository.save(megaPartSubPartCount);
        return megaPartSubPartCountMapper.toDto(megaPartSubPartCount);
    }

    /**
     * Update a megaPartSubPartCount.
     *
     * @param megaPartSubPartCountDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaPartSubPartCountDTO update(MegaPartSubPartCountDTO megaPartSubPartCountDTO) {
        LOG.debug("Request to update MegaPartSubPartCount : {}", megaPartSubPartCountDTO);
        MegaPartSubPartCount megaPartSubPartCount = megaPartSubPartCountMapper.toEntity(megaPartSubPartCountDTO);
        megaPartSubPartCount = megaPartSubPartCountRepository.save(megaPartSubPartCount);
        return megaPartSubPartCountMapper.toDto(megaPartSubPartCount);
    }

    /**
     * Partially update a megaPartSubPartCount.
     *
     * @param megaPartSubPartCountDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MegaPartSubPartCountDTO> partialUpdate(MegaPartSubPartCountDTO megaPartSubPartCountDTO) {
        LOG.debug("Request to partially update MegaPartSubPartCount : {}", megaPartSubPartCountDTO);

        return megaPartSubPartCountRepository
            .findById(megaPartSubPartCountDTO.getId())
            .map(existingMegaPartSubPartCount -> {
                megaPartSubPartCountMapper.partialUpdate(existingMegaPartSubPartCount, megaPartSubPartCountDTO);

                return existingMegaPartSubPartCount;
            })
            .map(megaPartSubPartCountRepository::save)
            .map(megaPartSubPartCountMapper::toDto);
    }

    /**
     * Get all the megaPartSubPartCounts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MegaPartSubPartCountDTO> findAll() {
        LOG.debug("Request to get all MegaPartSubPartCounts");
        return megaPartSubPartCountRepository
            .findAll()
            .stream()
            .map(megaPartSubPartCountMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one megaPartSubPartCount by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MegaPartSubPartCountDTO> findOne(Long id) {
        LOG.debug("Request to get MegaPartSubPartCount : {}", id);
        return megaPartSubPartCountRepository.findById(id).map(megaPartSubPartCountMapper::toDto);
    }

    /**
     * Delete the megaPartSubPartCount by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MegaPartSubPartCount : {}", id);
        megaPartSubPartCountRepository.deleteById(id);
    }
}
