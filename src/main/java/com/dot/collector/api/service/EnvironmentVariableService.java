package com.dot.collector.api.service;

import com.dot.collector.api.domain.EnvironmentVariable;
import com.dot.collector.api.repository.EnvironmentVariableRepository;
import com.dot.collector.api.service.dto.EnvironmentVariableDTO;
import com.dot.collector.api.service.mapper.EnvironmentVariableMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dot.collector.api.domain.EnvironmentVariable}.
 */
@Service
@Transactional
public class EnvironmentVariableService {

    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentVariableService.class);

    private final EnvironmentVariableRepository environmentVariableRepository;

    private final EnvironmentVariableMapper environmentVariableMapper;

    public EnvironmentVariableService(
        EnvironmentVariableRepository environmentVariableRepository,
        EnvironmentVariableMapper environmentVariableMapper
    ) {
        this.environmentVariableRepository = environmentVariableRepository;
        this.environmentVariableMapper = environmentVariableMapper;
    }

    /**
     * Save a environmentVariable.
     *
     * @param environmentVariableDTO the entity to save.
     * @return the persisted entity.
     */
    public EnvironmentVariableDTO save(EnvironmentVariableDTO environmentVariableDTO) {
        LOG.debug("Request to save EnvironmentVariable : {}", environmentVariableDTO);
        EnvironmentVariable environmentVariable = environmentVariableMapper.toEntity(environmentVariableDTO);
        environmentVariable = environmentVariableRepository.save(environmentVariable);
        return environmentVariableMapper.toDto(environmentVariable);
    }

    /**
     * Update a environmentVariable.
     *
     * @param environmentVariableDTO the entity to save.
     * @return the persisted entity.
     */
    public EnvironmentVariableDTO update(EnvironmentVariableDTO environmentVariableDTO) {
        LOG.debug("Request to update EnvironmentVariable : {}", environmentVariableDTO);
        EnvironmentVariable environmentVariable = environmentVariableMapper.toEntity(environmentVariableDTO);
        environmentVariable = environmentVariableRepository.save(environmentVariable);
        return environmentVariableMapper.toDto(environmentVariable);
    }

    /**
     * Partially update a environmentVariable.
     *
     * @param environmentVariableDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<EnvironmentVariableDTO> partialUpdate(EnvironmentVariableDTO environmentVariableDTO) {
        LOG.debug("Request to partially update EnvironmentVariable : {}", environmentVariableDTO);

        return environmentVariableRepository
            .findById(environmentVariableDTO.getId())
            .map(existingEnvironmentVariable -> {
                environmentVariableMapper.partialUpdate(existingEnvironmentVariable, environmentVariableDTO);

                return existingEnvironmentVariable;
            })
            .map(environmentVariableRepository::save)
            .map(environmentVariableMapper::toDto);
    }

    /**
     * Get all the environmentVariables.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<EnvironmentVariableDTO> findAll() {
        LOG.debug("Request to get all EnvironmentVariables");
        return environmentVariableRepository
            .findAll()
            .stream()
            .map(environmentVariableMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one environmentVariable by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EnvironmentVariableDTO> findOne(Long id) {
        LOG.debug("Request to get EnvironmentVariable : {}", id);
        return environmentVariableRepository.findById(id).map(environmentVariableMapper::toDto);
    }

    /**
     * Delete the environmentVariable by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete EnvironmentVariable : {}", id);
        environmentVariableRepository.deleteById(id);
    }
}
