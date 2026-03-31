package com.dot.collector.api.service;

import com.dot.collector.api.domain.PartCategory;
import com.dot.collector.api.repository.PartCategoryRepository;
import com.dot.collector.api.service.dto.PartCategoryDTO;
import com.dot.collector.api.service.mapper.PartCategoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dot.collector.api.domain.PartCategory}.
 */
@Service
@Transactional
public class PartCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(PartCategoryService.class);

    private final PartCategoryRepository partCategoryRepository;

    private final PartCategoryMapper partCategoryMapper;

    public PartCategoryService(PartCategoryRepository partCategoryRepository, PartCategoryMapper partCategoryMapper) {
        this.partCategoryRepository = partCategoryRepository;
        this.partCategoryMapper = partCategoryMapper;
    }

    /**
     * Save a partCategory.
     *
     * @param partCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public PartCategoryDTO save(PartCategoryDTO partCategoryDTO) {
        LOG.debug("Request to save PartCategory : {}", partCategoryDTO);
        PartCategory partCategory = partCategoryMapper.toEntity(partCategoryDTO);
        partCategory = partCategoryRepository.save(partCategory);
        return partCategoryMapper.toDto(partCategory);
    }

    /**
     * Update a partCategory.
     *
     * @param partCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public PartCategoryDTO update(PartCategoryDTO partCategoryDTO) {
        LOG.debug("Request to update PartCategory : {}", partCategoryDTO);
        PartCategory partCategory = partCategoryMapper.toEntity(partCategoryDTO);
        partCategory = partCategoryRepository.save(partCategory);
        return partCategoryMapper.toDto(partCategory);
    }

    /**
     * Partially update a partCategory.
     *
     * @param partCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PartCategoryDTO> partialUpdate(PartCategoryDTO partCategoryDTO) {
        LOG.debug("Request to partially update PartCategory : {}", partCategoryDTO);

        return partCategoryRepository
            .findById(partCategoryDTO.getId())
            .map(existingPartCategory -> {
                partCategoryMapper.partialUpdate(existingPartCategory, partCategoryDTO);

                return existingPartCategory;
            })
            .map(partCategoryRepository::save)
            .map(partCategoryMapper::toDto);
    }

    /**
     * Get all the partCategories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PartCategoryDTO> findAll() {
        LOG.debug("Request to get all PartCategories");
        return partCategoryRepository.findAll().stream().map(partCategoryMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one partCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PartCategoryDTO> findOne(Long id) {
        LOG.debug("Request to get PartCategory : {}", id);
        return partCategoryRepository.findById(id).map(partCategoryMapper::toDto);
    }

    /**
     * Delete the partCategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PartCategory : {}", id);
        partCategoryRepository.deleteById(id);
    }
}
