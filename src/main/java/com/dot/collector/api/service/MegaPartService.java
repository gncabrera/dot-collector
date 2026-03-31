package com.dot.collector.api.service;

import com.dot.collector.api.domain.MegaPart;
import com.dot.collector.api.repository.MegaPartRepository;
import com.dot.collector.api.service.dto.MegaPartDTO;
import com.dot.collector.api.service.mapper.MegaPartMapper;
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
 * Service Implementation for managing {@link com.dot.collector.api.domain.MegaPart}.
 */
@Service
@Transactional
public class MegaPartService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaPartService.class);

    private final MegaPartRepository megaPartRepository;

    private final MegaPartMapper megaPartMapper;

    public MegaPartService(MegaPartRepository megaPartRepository, MegaPartMapper megaPartMapper) {
        this.megaPartRepository = megaPartRepository;
        this.megaPartMapper = megaPartMapper;
    }

    /**
     * Save a megaPart.
     *
     * @param megaPartDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaPartDTO save(MegaPartDTO megaPartDTO) {
        LOG.debug("Request to save MegaPart : {}", megaPartDTO);
        MegaPart megaPart = megaPartMapper.toEntity(megaPartDTO);
        megaPart = megaPartRepository.save(megaPart);
        return megaPartMapper.toDto(megaPart);
    }

    /**
     * Update a megaPart.
     *
     * @param megaPartDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaPartDTO update(MegaPartDTO megaPartDTO) {
        LOG.debug("Request to update MegaPart : {}", megaPartDTO);
        MegaPart megaPart = megaPartMapper.toEntity(megaPartDTO);
        megaPart = megaPartRepository.save(megaPart);
        return megaPartMapper.toDto(megaPart);
    }

    /**
     * Partially update a megaPart.
     *
     * @param megaPartDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MegaPartDTO> partialUpdate(MegaPartDTO megaPartDTO) {
        LOG.debug("Request to partially update MegaPart : {}", megaPartDTO);

        return megaPartRepository
            .findById(megaPartDTO.getId())
            .map(existingMegaPart -> {
                megaPartMapper.partialUpdate(existingMegaPart, megaPartDTO);

                return existingMegaPart;
            })
            .map(megaPartRepository::save)
            .map(megaPartMapper::toDto);
    }

    /**
     * Get all the megaParts.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MegaPartDTO> findAll() {
        LOG.debug("Request to get all MegaParts");
        return megaPartRepository.findAll().stream().map(megaPartMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the megaParts with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MegaPartDTO> findAllWithEagerRelationships(Pageable pageable) {
        return megaPartRepository.findAllWithEagerRelationships(pageable).map(megaPartMapper::toDto);
    }

    /**
     * Get one megaPart by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MegaPartDTO> findOne(Long id) {
        LOG.debug("Request to get MegaPart : {}", id);
        return megaPartRepository.findOneWithEagerRelationships(id).map(megaPartMapper::toDto);
    }

    /**
     * Delete the megaPart by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MegaPart : {}", id);
        megaPartRepository.deleteById(id);
    }
}
