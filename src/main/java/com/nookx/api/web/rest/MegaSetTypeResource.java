package com.nookx.api.web.rest;

import com.nookx.api.domain.MegaSetType;
import com.nookx.api.repository.MegaSetTypeRepository;
import com.nookx.api.service.MegaSetTypeService;
import com.nookx.api.service.dto.MegaSetTypeDTO;
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
 * REST controller for managing {@link MegaSetType}.
 */
@RestController
@RequestMapping("/api/mega-set-types")
public class MegaSetTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaSetTypeResource.class);

    private static final String ENTITY_NAME = "megaSetType";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final MegaSetTypeService megaSetTypeService;

    private final MegaSetTypeRepository megaSetTypeRepository;

    public MegaSetTypeResource(MegaSetTypeService megaSetTypeService, MegaSetTypeRepository megaSetTypeRepository) {
        this.megaSetTypeService = megaSetTypeService;
        this.megaSetTypeRepository = megaSetTypeRepository;
    }

    /**
     * {@code POST  /mega-set-types} : Create a new megaSetType.
     *
     * @param megaSetTypeDTO the megaSetTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new megaSetTypeDTO, or with status {@code 400 (Bad Request)} if the megaSetType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MegaSetTypeDTO> createMegaSetType(@Valid @RequestBody MegaSetTypeDTO megaSetTypeDTO) throws URISyntaxException {
        LOG.debug("REST request to save MegaSetType : {}", megaSetTypeDTO);
        if (megaSetTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaSetType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaSetTypeDTO = megaSetTypeService.save(megaSetTypeDTO);
        return ResponseEntity.created(new URI("/api/mega-set-types/" + megaSetTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaSetTypeDTO.getId().toString()))
            .body(megaSetTypeDTO);
    }

    /**
     * {@code PUT  /mega-set-types/:id} : Updates an existing megaSetType.
     *
     * @param id the id of the megaSetTypeDTO to save.
     * @param megaSetTypeDTO the megaSetTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaSetTypeDTO,
     * or with status {@code 400 (Bad Request)} if the megaSetTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the megaSetTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MegaSetTypeDTO> updateMegaSetType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MegaSetTypeDTO megaSetTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MegaSetType : {}, {}", id, megaSetTypeDTO);
        if (megaSetTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaSetTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaSetTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        megaSetTypeDTO = megaSetTypeService.update(megaSetTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaSetTypeDTO.getId().toString()))
            .body(megaSetTypeDTO);
    }

    /**
     * {@code PATCH  /mega-set-types/:id} : Partial updates given fields of an existing megaSetType, field will ignore if it is null
     *
     * @param id the id of the megaSetTypeDTO to save.
     * @param megaSetTypeDTO the megaSetTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaSetTypeDTO,
     * or with status {@code 400 (Bad Request)} if the megaSetTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the megaSetTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the megaSetTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MegaSetTypeDTO> partialUpdateMegaSetType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MegaSetTypeDTO megaSetTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MegaSetType partially : {}, {}", id, megaSetTypeDTO);
        if (megaSetTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaSetTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaSetTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MegaSetTypeDTO> result = megaSetTypeService.partialUpdate(megaSetTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaSetTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mega-set-types} : get all the Mega Set Types.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mega Set Types in body.
     */
    @GetMapping("")
    public List<MegaSetTypeDTO> getAllMegaSetTypes(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all MegaSetTypes");
        return megaSetTypeService.findAll();
    }

    /**
     * {@code GET  /mega-set-types/:id} : get the "id" megaSetType.
     *
     * @param id the id of the megaSetTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the megaSetTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MegaSetTypeDTO> getMegaSetType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaSetType : {}", id);
        Optional<MegaSetTypeDTO> megaSetTypeDTO = megaSetTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaSetTypeDTO);
    }

    /**
     * {@code DELETE  /mega-set-types/:id} : delete the "id" megaSetType.
     *
     * @param id the id of the megaSetTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaSetType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaSetType : {}", id);
        megaSetTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
