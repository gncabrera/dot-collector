package com.nookx.api.web.rest;

import com.nookx.api.domain.MegaPartType;
import com.nookx.api.repository.MegaPartTypeRepository;
import com.nookx.api.service.MegaPartTypeService;
import com.nookx.api.service.dto.MegaPartTypeDTO;
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
 * REST controller for managing {@link MegaPartType}.
 */
@RestController
@RequestMapping("/api/mega-part-types")
public class MegaPartTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaPartTypeResource.class);

    private static final String ENTITY_NAME = "megaPartType";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final MegaPartTypeService megaPartTypeService;

    private final MegaPartTypeRepository megaPartTypeRepository;

    public MegaPartTypeResource(MegaPartTypeService megaPartTypeService, MegaPartTypeRepository megaPartTypeRepository) {
        this.megaPartTypeService = megaPartTypeService;
        this.megaPartTypeRepository = megaPartTypeRepository;
    }

    /**
     * {@code POST  /mega-part-types} : Create a new megaPartType.
     *
     * @param megaPartTypeDTO the megaPartTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new megaPartTypeDTO, or with status {@code 400 (Bad Request)} if the megaPartType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MegaPartTypeDTO> createMegaPartType(@Valid @RequestBody MegaPartTypeDTO megaPartTypeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MegaPartType : {}", megaPartTypeDTO);
        if (megaPartTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaPartType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaPartTypeDTO = megaPartTypeService.save(megaPartTypeDTO);
        return ResponseEntity.created(new URI("/api/mega-part-types/" + megaPartTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaPartTypeDTO.getId().toString()))
            .body(megaPartTypeDTO);
    }

    /**
     * {@code PUT  /mega-part-types/:id} : Updates an existing megaPartType.
     *
     * @param id the id of the megaPartTypeDTO to save.
     * @param megaPartTypeDTO the megaPartTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaPartTypeDTO,
     * or with status {@code 400 (Bad Request)} if the megaPartTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the megaPartTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MegaPartTypeDTO> updateMegaPartType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MegaPartTypeDTO megaPartTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MegaPartType : {}, {}", id, megaPartTypeDTO);
        if (megaPartTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaPartTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaPartTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        megaPartTypeDTO = megaPartTypeService.update(megaPartTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaPartTypeDTO.getId().toString()))
            .body(megaPartTypeDTO);
    }

    /**
     * {@code PATCH  /mega-part-types/:id} : Partial updates given fields of an existing megaPartType, field will ignore if it is null
     *
     * @param id the id of the megaPartTypeDTO to save.
     * @param megaPartTypeDTO the megaPartTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaPartTypeDTO,
     * or with status {@code 400 (Bad Request)} if the megaPartTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the megaPartTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the megaPartTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MegaPartTypeDTO> partialUpdateMegaPartType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MegaPartTypeDTO megaPartTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MegaPartType partially : {}, {}", id, megaPartTypeDTO);
        if (megaPartTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaPartTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaPartTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MegaPartTypeDTO> result = megaPartTypeService.partialUpdate(megaPartTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaPartTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mega-part-types} : get all the Mega Part Types.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mega Part Types in body.
     */
    @GetMapping("")
    public List<MegaPartTypeDTO> getAllMegaPartTypes(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MegaPartTypes");
        return megaPartTypeService.findAll();
    }

    /**
     * {@code GET  /mega-part-types/:id} : get the "id" megaPartType.
     *
     * @param id the id of the megaPartTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the megaPartTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MegaPartTypeDTO> getMegaPartType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaPartType : {}", id);
        Optional<MegaPartTypeDTO> megaPartTypeDTO = megaPartTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaPartTypeDTO);
    }

    /**
     * {@code DELETE  /mega-part-types/:id} : delete the "id" megaPartType.
     *
     * @param id the id of the megaPartTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaPartType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaPartType : {}", id);
        megaPartTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
