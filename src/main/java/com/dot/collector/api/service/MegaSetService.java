package com.dot.collector.api.service;

import com.dot.collector.api.domain.MegaSet;
import com.dot.collector.api.repository.MegaSetRepository;
import com.dot.collector.api.service.dto.MegaSetDTO;
import com.dot.collector.api.service.mapper.MegaSetMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dot.collector.api.domain.MegaSet}.
 */
@Service
@Transactional
public class MegaSetService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaSetService.class);

    private final MegaSetRepository megaSetRepository;

    private final MegaSetMapper megaSetMapper;

    public MegaSetService(MegaSetRepository megaSetRepository, MegaSetMapper megaSetMapper) {
        this.megaSetRepository = megaSetRepository;
        this.megaSetMapper = megaSetMapper;
    }

    /**
     * Save a megaSet.
     *
     * @param megaSetDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaSetDTO save(MegaSetDTO megaSetDTO) {
        LOG.debug("Request to save MegaSet : {}", megaSetDTO);
        MegaSet megaSet = megaSetMapper.toEntity(megaSetDTO);
        megaSet = megaSetRepository.save(megaSet);
        return megaSetMapper.toDto(megaSet);
    }

    /**
     * Update a megaSet.
     *
     * @param megaSetDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaSetDTO update(MegaSetDTO megaSetDTO) {
        LOG.debug("Request to update MegaSet : {}", megaSetDTO);
        MegaSet megaSet = megaSetMapper.toEntity(megaSetDTO);
        megaSet = megaSetRepository.save(megaSet);
        return megaSetMapper.toDto(megaSet);
    }

    /**
     * Partially update a megaSet.
     *
     * @param megaSetDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MegaSetDTO> partialUpdate(MegaSetDTO megaSetDTO) {
        LOG.debug("Request to partially update MegaSet : {}", megaSetDTO);

        return megaSetRepository
            .findById(megaSetDTO.getId())
            .map(existingMegaSet -> {
                megaSetMapper.partialUpdate(existingMegaSet, megaSetDTO);

                return existingMegaSet;
            })
            .map(megaSetRepository::save)
            .map(megaSetMapper::toDto);
    }

    /**
     * Get all the megaSets.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MegaSetDTO> findAll() {
        LOG.debug("Request to get all MegaSets");
        return megaSetRepository.findAll().stream().map(megaSetMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one megaSet by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MegaSetDTO> findOne(Long id) {
        LOG.debug("Request to get MegaSet : {}", id);
        return megaSetRepository.findById(id).map(megaSetMapper::toDto);
    }

    /**
     * Delete the megaSet by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MegaSet : {}", id);
        megaSetRepository.deleteById(id);
    }
}
