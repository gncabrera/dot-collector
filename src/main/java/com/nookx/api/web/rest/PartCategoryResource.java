package com.nookx.api.web.rest;

import com.nookx.api.domain.PartCategory;
import com.nookx.api.repository.PartCategoryRepository;
import com.nookx.api.service.PartCategoryService;
import com.nookx.api.service.dto.PartCategoryDTO;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link PartCategory}.
 */
@RestController
@RequestMapping("/api/part-categories")
public class PartCategoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(PartCategoryResource.class);

    private static final String ENTITY_NAME = "partCategory";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final PartCategoryService partCategoryService;

    private final PartCategoryRepository partCategoryRepository;

    public PartCategoryResource(PartCategoryService partCategoryService, PartCategoryRepository partCategoryRepository) {
        this.partCategoryService = partCategoryService;
        this.partCategoryRepository = partCategoryRepository;
    }

    /**
     * {@code POST  /part-categories} : Create a new partCategory.
     *
     * @param partCategoryDTO the partCategoryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new partCategoryDTO, or with status {@code 400 (Bad Request)} if the partCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PartCategoryDTO> createPartCategory(@Valid @RequestBody PartCategoryDTO partCategoryDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PartCategory : {}", partCategoryDTO);
        if (partCategoryDTO.getId() != null) {
            throw new BadRequestAlertException("A new partCategory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        partCategoryDTO = partCategoryService.save(partCategoryDTO);
        return ResponseEntity.created(new URI("/api/part-categories/" + partCategoryDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, partCategoryDTO.getId().toString()))
            .body(partCategoryDTO);
    }

    /**
     * {@code PUT  /part-categories/:id} : Updates an existing partCategory.
     *
     * @param id the id of the partCategoryDTO to save.
     * @param partCategoryDTO the partCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the partCategoryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the partCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PartCategoryDTO> updatePartCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PartCategoryDTO partCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PartCategory : {}, {}", id, partCategoryDTO);
        if (partCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        partCategoryDTO = partCategoryService.update(partCategoryDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partCategoryDTO.getId().toString()))
            .body(partCategoryDTO);
    }

    /**
     * {@code PATCH  /part-categories/:id} : Partial updates given fields of an existing partCategory, field will ignore if it is null
     *
     * @param id the id of the partCategoryDTO to save.
     * @param partCategoryDTO the partCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated partCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the partCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the partCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the partCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PartCategoryDTO> partialUpdatePartCategory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PartCategoryDTO partCategoryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PartCategory partially : {}, {}", id, partCategoryDTO);
        if (partCategoryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, partCategoryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!partCategoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PartCategoryDTO> result = partCategoryService.partialUpdate(partCategoryDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, partCategoryDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /part-categories} : get all the Part Categories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Part Categories in body.
     */
    @GetMapping("")
    public List<PartCategoryDTO> getAllPartCategories() {
        LOG.debug("REST request to get all PartCategories");
        return partCategoryService.findAll();
    }

    /**
     * {@code GET  /part-categories/:id} : get the "id" partCategory.
     *
     * @param id the id of the partCategoryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the partCategoryDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PartCategoryDTO> getPartCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PartCategory : {}", id);
        Optional<PartCategoryDTO> partCategoryDTO = partCategoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(partCategoryDTO);
    }

    /**
     * {@code DELETE  /part-categories/:id} : delete the "id" partCategory.
     *
     * @param id the id of the partCategoryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePartCategory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PartCategory : {}", id);
        partCategoryService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
