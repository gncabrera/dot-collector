package com.dot.collector.api.web.rest;

import com.dot.collector.api.repository.PartSubCategoryRepository;
import com.dot.collector.api.service.PartSubCategoryService;
import com.dot.collector.api.service.dto.PartSubCategoryDTO;
import com.dot.collector.api.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.dot.collector.api.domain.PartSubCategory}.
 */
@RestController
@RequestMapping("/api/part-sub-categories")
public class PartSubCategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(PartSubCategoryResource.class);

    private static final String ENTITY_NAME = "partSubCategory";

    @Value("${jhipster.clientApp.name:dotCollector}")
    private String applicationName;

    private final PartSubCategoryService partSubCategoryService;

    private final PartSubCategoryRepository partSubCategoryRepository;

    public PartSubCategoryResource(PartSubCategoryService partSubCategoryService, PartSubCategoryRepository partSubCategoryRepository) {
        this.partSubCategoryService = partSubCategoryService;
        this.partSubCategoryRepository = partSubCategoryRepository;
    }

    /**
     * {@code POST  /part-sub-categories} : Create a new partSubCategory.
     *
     * @param partSubCategoryDTO the partSubCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partSubCategoryDTO, or with status {@code 400 (Bad Request)} if the partSubCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PartSubCategoryDTO> createPartSubCategory(@Valid @RequestBody PartSubCategoryDTO partSubCategoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PartSubCategory : {}", partSubCategoryDTO);
        if (partSubCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new partSubCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        partSubCategoryDTO = partSubCategoryService.save(partSubCategoryDTO);
        return ResponseEntity.created(new URI("/api/part-sub-categories/" + partSubCategoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, partSubCategoryDTO.getId().toString()))
            .body(partSubCategoryDTO);
    }

    /**
     * {@code PUT  /part-sub-categories/:id} : Updates an existing partSubCategory.
     *
     * @param id the id of the partSubCategoryDTO to save.
     * @param partSubCategoryDTO the partSubCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partSubCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the partSubCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partSubCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PartSubCategoryDTO> updatePartSubCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PartSubCategoryDTO partSubCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PartSubCategory : {}, {}", id, partSubCategoryDTO);
        if (partSubCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partSubCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partSubCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        partSubCategoryDTO = partSubCategoryService.update(partSubCategoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partSubCategoryDTO.getId().toString()))
            .body(partSubCategoryDTO);
    }

    /**
     * {@code PATCH  /part-sub-categories/:id} : Partial updates given fields of an existing partSubCategory, field will ignore if it is null
     *
     * @param id the id of the partSubCategoryDTO to save.
     * @param partSubCategoryDTO the partSubCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partSubCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the partSubCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the partSubCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the partSubCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PartSubCategoryDTO> partialUpdatePartSubCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PartSubCategoryDTO partSubCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PartSubCategory partially : {}, {}", id, partSubCategoryDTO);
        if (partSubCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partSubCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partSubCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PartSubCategoryDTO> result = partSubCategoryService.partialUpdate(partSubCategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partSubCategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /part-sub-categories} : get all the Part Sub Categories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Part Sub Categories in body.
     */
    @GetMapping("")
    public List<PartSubCategoryDTO> getAllPartSubCategories() {
        LOG.debug("REST request to get all PartSubCategories");
        return partSubCategoryService.findAll();
    }

    /**
     * {@code GET  /part-sub-categories/:id} : get the "id" partSubCategory.
     *
     * @param id the id of the partSubCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partSubCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartSubCategoryDTO> getPartSubCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PartSubCategory : {}", id);
        Optional<PartSubCategoryDTO> partSubCategoryDTO = partSubCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partSubCategoryDTO);
    }

    /**
     * {@code DELETE  /part-sub-categories/:id} : delete the "id" partSubCategory.
     *
     * @param id the id of the partSubCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartSubCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PartSubCategory : {}", id);
        partSubCategoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
