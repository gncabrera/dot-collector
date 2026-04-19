package com.nookx.api.service;

import com.nookx.api.domain.MegaSetType;
import com.nookx.api.repository.MegaAttributeRepository;
import com.nookx.api.repository.MegaSetTypeRepository;
import com.nookx.api.service.dto.MegaSetTypeDTO;
import com.nookx.api.service.mapper.MegaSetTypeMapper;
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
 * Service Implementation for managing {@link MegaSetType}.
 */
@Service
@Transactional
public class MegaSetTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaSetTypeService.class);

    private final MegaSetTypeRepository megaSetTypeRepository;

    private final MegaSetTypeMapper megaSetTypeMapper;

    private final MegaAttributeRepository megaAttributeRepository;

    public MegaSetTypeService(
        MegaSetTypeRepository megaSetTypeRepository,
        MegaSetTypeMapper megaSetTypeMapper,
        MegaAttributeRepository megaAttributeRepository
    ) {
        this.megaSetTypeRepository = megaSetTypeRepository;
        this.megaSetTypeMapper = megaSetTypeMapper;
        this.megaAttributeRepository = megaAttributeRepository;
    }

    /**
     * Save a megaSetType.
     *
     * @param megaSetTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaSetTypeDTO save(MegaSetTypeDTO megaSetTypeDTO) {
        LOG.debug("Request to save MegaSetType : {}", megaSetTypeDTO);
        MegaSetType megaSetType = megaSetTypeMapper.toEntity(megaSetTypeDTO);
        megaSetType = megaSetTypeRepository.save(megaSetType);
        return megaSetTypeMapper.toDto(megaSetType);
    }

    /**
     * Update a megaSetType.
     *
     * @param megaSetTypeDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaSetTypeDTO update(MegaSetTypeDTO megaSetTypeDTO) {
        LOG.debug("Request to update MegaSetType : {}", megaSetTypeDTO);
        MegaSetType megaSetType = megaSetTypeMapper.toEntity(megaSetTypeDTO);
        megaSetType = megaSetTypeRepository.save(megaSetType);
        return megaSetTypeMapper.toDto(megaSetType);
    }

    /**
     * Partially update a megaSetType.
     *
     * @param megaSetTypeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MegaSetTypeDTO> partialUpdate(MegaSetTypeDTO megaSetTypeDTO) {
        LOG.debug("Request to partially update MegaSetType : {}", megaSetTypeDTO);

        return megaSetTypeRepository
            .findById(megaSetTypeDTO.getId())
            .map(existingMegaSetType -> {
                megaSetTypeMapper.partialUpdate(existingMegaSetType, megaSetTypeDTO);

                return existingMegaSetType;
            })
            .map(megaSetTypeRepository::save)
            .map(megaSetTypeMapper::toDto);
    }

    /**
     * Get all the megaSetTypes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MegaSetTypeDTO> findAll() {
        LOG.debug("Request to get all MegaSetTypes");
        return megaSetTypeRepository.findAll().stream().map(megaSetTypeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the megaSetTypes with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MegaSetTypeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return megaSetTypeRepository.findAllWithEagerRelationships(pageable).map(megaSetTypeMapper::toDto);
    }

    /**
     * Get one megaSetType by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MegaSetTypeDTO> findOne(Long id) {
        LOG.debug("Request to get MegaSetType : {}", id);
        return megaSetTypeRepository.findOneWithEagerRelationships(id).map(megaSetTypeMapper::toDto);
    }

    /**
     * Delete the megaSetType by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MegaSetType : {}", id);
        megaSetTypeRepository.deleteById(id);
    }
}
