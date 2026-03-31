package com.dot.collector.api.service;

import com.dot.collector.api.domain.PartSubCategory;
import com.dot.collector.api.repository.PartSubCategoryRepository;
import com.dot.collector.api.service.dto.PartSubCategoryDTO;
import com.dot.collector.api.service.mapper.PartSubCategoryMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dot.collector.api.domain.PartSubCategory}.
 */
@Service
@Transactional
public class PartSubCategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(PartSubCategoryService.class);

    private final PartSubCategoryRepository partSubCategoryRepository;

    private final PartSubCategoryMapper partSubCategoryMapper;

    public PartSubCategoryService(PartSubCategoryRepository partSubCategoryRepository, PartSubCategoryMapper partSubCategoryMapper) {
        this.partSubCategoryRepository = partSubCategoryRepository;
        this.partSubCategoryMapper = partSubCategoryMapper;
    }

    /**
     * Save a partSubCategory.
     *
     * @param partSubCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public PartSubCategoryDTO save(PartSubCategoryDTO partSubCategoryDTO) {
        LOG.debug("Request to save PartSubCategory : {}", partSubCategoryDTO);
        PartSubCategory partSubCategory = partSubCategoryMapper.toEntity(partSubCategoryDTO);
        partSubCategory = partSubCategoryRepository.save(partSubCategory);
        return partSubCategoryMapper.toDto(partSubCategory);
    }

    /**
     * Update a partSubCategory.
     *
     * @param partSubCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    public PartSubCategoryDTO update(PartSubCategoryDTO partSubCategoryDTO) {
        LOG.debug("Request to update PartSubCategory : {}", partSubCategoryDTO);
        PartSubCategory partSubCategory = partSubCategoryMapper.toEntity(partSubCategoryDTO);
        partSubCategory = partSubCategoryRepository.save(partSubCategory);
        return partSubCategoryMapper.toDto(partSubCategory);
    }

    /**
     * Partially update a partSubCategory.
     *
     * @param partSubCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PartSubCategoryDTO> partialUpdate(PartSubCategoryDTO partSubCategoryDTO) {
        LOG.debug("Request to partially update PartSubCategory : {}", partSubCategoryDTO);

        return partSubCategoryRepository
            .findById(partSubCategoryDTO.getId())
            .map(existingPartSubCategory -> {
                partSubCategoryMapper.partialUpdate(existingPartSubCategory, partSubCategoryDTO);

                return existingPartSubCategory;
            })
            .map(partSubCategoryRepository::save)
            .map(partSubCategoryMapper::toDto);
    }

    /**
     * Get all the partSubCategories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PartSubCategoryDTO> findAll() {
        LOG.debug("Request to get all PartSubCategories");
        return partSubCategoryRepository
            .findAll()
            .stream()
            .map(partSubCategoryMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one partSubCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PartSubCategoryDTO> findOne(Long id) {
        LOG.debug("Request to get PartSubCategory : {}", id);
        return partSubCategoryRepository.findById(id).map(partSubCategoryMapper::toDto);
    }

    /**
     * Delete the partSubCategory by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PartSubCategory : {}", id);
        partSubCategoryRepository.deleteById(id);
    }
}
