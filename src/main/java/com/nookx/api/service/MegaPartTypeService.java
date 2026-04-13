package com.nookx.api.service;

import com.nookx.api.domain.MegaPartType;
import com.nookx.api.repository.MegaPartTypeRepository;
import com.nookx.api.service.dto.MegaPartTypeDTO;
import com.nookx.api.service.mapper.MegaPartTypeMapper;
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
 * Service Implementation for managing {@link MegaPartType}.
 */
@Service
@Transactional
public class MegaPartTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaPartTypeService.class);

    private final MegaPartTypeRepository megaPartTypeRepository;

    private final MegaPartTypeMapper megaPartTypeMapper;

    public MegaPartTypeService(MegaPartTypeRepository megaPartTypeRepository, MegaPartTypeMapper megaPartTypeMapper) {
        this.megaPartTypeRepository = megaPartTypeRepository;
        this.megaPartTypeMapper = megaPartTypeMapper;
    }

    /**
     * Save a megaPartType.
     *
     * @param megaPartTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaPartTypeDTO save(MegaPartTypeDTO megaPartTypeDTO) {
        LOG.debug("Request to save MegaPartType : {}", megaPartTypeDTO);
        MegaPartType megaPartType = megaPartTypeMapper.toEntity(megaPartTypeDTO);
        megaPartType = megaPartTypeRepository.save(megaPartType);
        return megaPartTypeMapper.toDto(megaPartType);
    }

    /**
     * Update a megaPartType.
     *
     * @param megaPartTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaPartTypeDTO update(MegaPartTypeDTO megaPartTypeDTO) {
        LOG.debug("Request to update MegaPartType : {}", megaPartTypeDTO);
        MegaPartType megaPartType = megaPartTypeMapper.toEntity(megaPartTypeDTO);
        megaPartType = megaPartTypeRepository.save(megaPartType);
        return megaPartTypeMapper.toDto(megaPartType);
    }

    /**
     * Partially update a megaPartType.
     *
     * @param megaPartTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MegaPartTypeDTO> partialUpdate(MegaPartTypeDTO megaPartTypeDTO) {
        LOG.debug("Request to partially update MegaPartType : {}", megaPartTypeDTO);

        return megaPartTypeRepository
            .findById(megaPartTypeDTO.getId())
            .map(existingMegaPartType -> {
                megaPartTypeMapper.partialUpdate(existingMegaPartType, megaPartTypeDTO);

                return existingMegaPartType;
            })
            .map(megaPartTypeRepository::save)
            .map(megaPartTypeMapper::toDto);
    }

    /**
     * Get all the megaPartTypes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MegaPartTypeDTO> findAll() {
        LOG.debug("Request to get all MegaPartTypes");
        return megaPartTypeRepository.findAll().stream().map(megaPartTypeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the megaPartTypes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MegaPartTypeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return megaPartTypeRepository.findAllWithEagerRelationships(pageable).map(megaPartTypeMapper::toDto);
    }

    /**
     * Get one megaPartType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MegaPartTypeDTO> findOne(Long id) {
        LOG.debug("Request to get MegaPartType : {}", id);
        return megaPartTypeRepository.findOneWithEagerRelationships(id).map(megaPartTypeMapper::toDto);
    }

    /**
     * Delete the megaPartType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MegaPartType : {}", id);
        megaPartTypeRepository.deleteById(id);
    }
}
