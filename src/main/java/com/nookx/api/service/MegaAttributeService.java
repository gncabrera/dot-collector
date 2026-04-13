package com.nookx.api.service;

import com.nookx.api.domain.MegaAttribute;
import com.nookx.api.repository.MegaAttributeRepository;
import com.nookx.api.service.dto.MegaAttributeDTO;
import com.nookx.api.service.mapper.MegaAttributeMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MegaAttribute}.
 */
@Service
@Transactional
public class MegaAttributeService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaAttributeService.class);

    private final MegaAttributeRepository megaAttributeRepository;

    private final MegaAttributeMapper megaAttributeMapper;

    public MegaAttributeService(MegaAttributeRepository megaAttributeRepository, MegaAttributeMapper megaAttributeMapper) {
        this.megaAttributeRepository = megaAttributeRepository;
        this.megaAttributeMapper = megaAttributeMapper;
    }

    /**
     * Save a megaAttribute.
     *
     * @param megaAttributeDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaAttributeDTO save(MegaAttributeDTO megaAttributeDTO) {
        LOG.debug("Request to save MegaAttribute : {}", megaAttributeDTO);
        MegaAttribute megaAttribute = megaAttributeMapper.toEntity(megaAttributeDTO);
        megaAttribute = megaAttributeRepository.save(megaAttribute);
        return megaAttributeMapper.toDto(megaAttribute);
    }

    /**
     * Update a megaAttribute.
     *
     * @param megaAttributeDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaAttributeDTO update(MegaAttributeDTO megaAttributeDTO) {
        LOG.debug("Request to update MegaAttribute : {}", megaAttributeDTO);
        MegaAttribute megaAttribute = megaAttributeMapper.toEntity(megaAttributeDTO);
        megaAttribute = megaAttributeRepository.save(megaAttribute);
        return megaAttributeMapper.toDto(megaAttribute);
    }

    /**
     * Partially update a megaAttribute.
     *
     * @param megaAttributeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MegaAttributeDTO> partialUpdate(MegaAttributeDTO megaAttributeDTO) {
        LOG.debug("Request to partially update MegaAttribute : {}", megaAttributeDTO);

        return megaAttributeRepository
            .findById(megaAttributeDTO.getId())
            .map(existingMegaAttribute -> {
                megaAttributeMapper.partialUpdate(existingMegaAttribute, megaAttributeDTO);

                return existingMegaAttribute;
            })
            .map(megaAttributeRepository::save)
            .map(megaAttributeMapper::toDto);
    }

    /**
     * Get all the megaAttributes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MegaAttributeDTO> findAll() {
        LOG.debug("Request to get all MegaAttributes");
        return megaAttributeRepository.findAll().stream().map(megaAttributeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one megaAttribute by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MegaAttributeDTO> findOne(Long id) {
        LOG.debug("Request to get MegaAttribute : {}", id);
        return megaAttributeRepository.findById(id).map(megaAttributeMapper::toDto);
    }

    /**
     * Delete the megaAttribute by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MegaAttribute : {}", id);
        megaAttributeRepository.deleteById(id);
    }
}
