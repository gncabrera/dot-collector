package com.nookx.api.web.rest;

import com.nookx.api.domain.MegaPart;
import com.nookx.api.repository.MegaPartRepository;
import com.nookx.api.service.MegaPartService;
import com.nookx.api.service.dto.MegaPartDTO;
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
 * REST controller for managing {@link MegaPart}.
 */
@RestController
@RequestMapping("/api/mega-parts")
public class MegaPartResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaPartResource.class);

    private static final String ENTITY_NAME = "megaPart";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final MegaPartService megaPartService;

    private final MegaPartRepository megaPartRepository;

    public MegaPartResource(MegaPartService megaPartService, MegaPartRepository megaPartRepository) {
        this.megaPartService = megaPartService;
        this.megaPartRepository = megaPartRepository;
    }

    /**
     * {@code POST  /mega-parts} : Create a new megaPart.
     *
     * @param megaPartDTO the megaPartDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new megaPartDTO, or with status {@code 400 (Bad Request)} if the megaPart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MegaPartDTO> createMegaPart(@Valid @RequestBody MegaPartDTO megaPartDTO) throws URISyntaxException {
        LOG.debug("REST request to save MegaPart : {}", megaPartDTO);
        if (megaPartDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaPart cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaPartDTO = megaPartService.save(megaPartDTO);
        return ResponseEntity.created(new URI("/api/mega-parts/" + megaPartDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaPartDTO.getId().toString()))
            .body(megaPartDTO);
    }

    /**
     * {@code PUT  /mega-parts/:id} : Updates an existing megaPart.
     *
     * @param id the id of the megaPartDTO to save.
     * @param megaPartDTO the megaPartDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaPartDTO,
     * or with status {@code 400 (Bad Request)} if the megaPartDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the megaPartDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MegaPartDTO> updateMegaPart(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MegaPartDTO megaPartDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MegaPart : {}, {}", id, megaPartDTO);
        if (megaPartDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaPartDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaPartRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        megaPartDTO = megaPartService.update(megaPartDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaPartDTO.getId().toString()))
            .body(megaPartDTO);
    }

    /**
     * {@code PATCH  /mega-parts/:id} : Partial updates given fields of an existing megaPart, field will ignore if it is null
     *
     * @param id the id of the megaPartDTO to save.
     * @param megaPartDTO the megaPartDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaPartDTO,
     * or with status {@code 400 (Bad Request)} if the megaPartDTO is not valid,
     * or with status {@code 404 (Not Found)} if the megaPartDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the megaPartDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MegaPartDTO> partialUpdateMegaPart(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MegaPartDTO megaPartDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MegaPart partially : {}, {}", id, megaPartDTO);
        if (megaPartDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaPartDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaPartRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MegaPartDTO> result = megaPartService.partialUpdate(megaPartDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaPartDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mega-parts} : get all the Mega Parts.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mega Parts in body.
     */
    @GetMapping("")
    public List<MegaPartDTO> getAllMegaParts(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all MegaParts");
        return megaPartService.findAll();
    }

    /**
     * {@code GET  /mega-parts/:id} : get the "id" megaPart.
     *
     * @param id the id of the megaPartDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the megaPartDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MegaPartDTO> getMegaPart(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaPart : {}", id);
        Optional<MegaPartDTO> megaPartDTO = megaPartService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaPartDTO);
    }

    /**
     * {@code DELETE  /mega-parts/:id} : delete the "id" megaPart.
     *
     * @param id the id of the megaPartDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaPart(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaPart : {}", id);
        megaPartService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
