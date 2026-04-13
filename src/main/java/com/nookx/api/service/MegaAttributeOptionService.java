package com.nookx.api.service;

import com.nookx.api.domain.MegaAttributeOption;
import com.nookx.api.repository.MegaAttributeOptionRepository;
import com.nookx.api.service.dto.MegaAttributeOptionDTO;
import com.nookx.api.service.mapper.MegaAttributeOptionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MegaAttributeOption}.
 */
@Service
@Transactional
public class MegaAttributeOptionService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaAttributeOptionService.class);

    private final MegaAttributeOptionRepository megaAttributeOptionRepository;

    private final MegaAttributeOptionMapper megaAttributeOptionMapper;

    public MegaAttributeOptionService(
        MegaAttributeOptionRepository megaAttributeOptionRepository,
        MegaAttributeOptionMapper megaAttributeOptionMapper
    ) {
        this.megaAttributeOptionRepository = megaAttributeOptionRepository;
        this.megaAttributeOptionMapper = megaAttributeOptionMapper;
    }

    /**
     * Save a megaAttributeOption.
     *
     * @param megaAttributeOptionDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaAttributeOptionDTO save(MegaAttributeOptionDTO megaAttributeOptionDTO) {
        LOG.debug("Request to save MegaAttributeOption : {}", megaAttributeOptionDTO);
        MegaAttributeOption megaAttributeOption = megaAttributeOptionMapper.toEntity(megaAttributeOptionDTO);
        megaAttributeOption = megaAttributeOptionRepository.save(megaAttributeOption);
        return megaAttributeOptionMapper.toDto(megaAttributeOption);
    }

    /**
     * Update a megaAttributeOption.
     *
     * @param megaAttributeOptionDTO the entity to save.
     * @return the persisted entity.
     */
    public MegaAttributeOptionDTO update(MegaAttributeOptionDTO megaAttributeOptionDTO) {
        LOG.debug("Request to update MegaAttributeOption : {}", megaAttributeOptionDTO);
        MegaAttributeOption megaAttributeOption = megaAttributeOptionMapper.toEntity(megaAttributeOptionDTO);
        megaAttributeOption = megaAttributeOptionRepository.save(megaAttributeOption);
        return megaAttributeOptionMapper.toDto(megaAttributeOption);
    }

    /**
     * Partially update a megaAttributeOption.
     *
     * @param megaAttributeOptionDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MegaAttributeOptionDTO> partialUpdate(MegaAttributeOptionDTO megaAttributeOptionDTO) {
        LOG.debug("Request to partially update MegaAttributeOption : {}", megaAttributeOptionDTO);

        return megaAttributeOptionRepository
            .findById(megaAttributeOptionDTO.getId())
            .map(existingMegaAttributeOption -> {
                megaAttributeOptionMapper.partialUpdate(existingMegaAttributeOption, megaAttributeOptionDTO);

                return existingMegaAttributeOption;
            })
            .map(megaAttributeOptionRepository::save)
            .map(megaAttributeOptionMapper::toDto);
    }

    /**
     * Get all the megaAttributeOptions.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<MegaAttributeOptionDTO> findAll() {
        LOG.debug("Request to get all MegaAttributeOptions");
        return megaAttributeOptionRepository
            .findAll()
            .stream()
            .map(megaAttributeOptionMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one megaAttributeOption by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MegaAttributeOptionDTO> findOne(Long id) {
        LOG.debug("Request to get MegaAttributeOption : {}", id);
        return megaAttributeOptionRepository.findById(id).map(megaAttributeOptionMapper::toDto);
    }

    /**
     * Delete the megaAttributeOption by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MegaAttributeOption : {}", id);
        megaAttributeOptionRepository.deleteById(id);
    }
}
