package com.nookx.api.service;

import com.nookx.api.client.rest.MegaSetTypeResource;
import com.nookx.api.domain.Interest;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.repository.InterestRepository;
import com.nookx.api.repository.MegaSetRepository;
import com.nookx.api.service.dto.MegaSetDTO;
import com.nookx.api.service.mapper.MegaSetMapper;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link MegaSet}.
 *
 * <p>Every write path validates the {@code attributes} JSON payload against the schema of the
 * {@link com.nookx.api.domain.MegaSetType} that the row points to. This guarantees that
 * what hits the JSONB column always matches the (versioned) dynamic schema, while old rows
 * keep being valid against their own (older) version of the type.</p>
 */
@Service
@Transactional
public class MegaSetService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaSetService.class);

    private static final String ENTITY_NAME = "megaSet";

    private final MegaSetRepository megaSetRepository;

    private final MegaSetMapper megaSetMapper;

    private final MegaSetTypeService megaSetTypeService;

    private final InterestRepository interestRepository;

    private final MegaSetTypeResource megaSetTypeResource;

    public MegaSetService(
        MegaSetRepository megaSetRepository,
        MegaSetMapper megaSetMapper,
        MegaSetTypeService megaSetTypeService,
        InterestRepository interestRepository,
        MegaSetTypeResource megaSetTypeResource
    ) {
        this.megaSetRepository = megaSetRepository;
        this.megaSetMapper = megaSetMapper;
        this.megaSetTypeService = megaSetTypeService;
        this.interestRepository = interestRepository;
        this.megaSetTypeResource = megaSetTypeResource;
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

        Interest interest = interestRepository.findById(megaSetDTO.getInterest().getId()).orElse(null);
        if (interest != null) {
            megaSet.setInterest(interest);
            megaSet.setType(interest.getSetType());
        }
        validateAgainstType(megaSet);

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
        validateAgainstType(megaSet);
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
                validateAgainstType(existingMegaSet);
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

    /**
     * Validate the JSON {@code attributes} payload of a {@link MegaSet} against the schema
     * declared by its {@link com.nookx.api.domain.MegaSetType}. The validation is delegated
     * to {@link MegaSetTypeService#validateAttributes} so the same rules apply in every
     * write path (save, update, partial update) and in the standalone validate endpoint.
     */
    private void validateAgainstType(MegaSet megaSet) {
        if (megaSet.getType() == null || megaSet.getType().getId() == null) {
            throw new BadRequestAlertException("MegaSet must reference a MegaSetType", ENTITY_NAME, "typerequired");
        }
        megaSetTypeService.validateAttributes(megaSet.getType().getId(), megaSet.getAttributes());
    }
}
