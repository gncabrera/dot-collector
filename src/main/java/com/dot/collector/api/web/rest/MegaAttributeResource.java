package com.dot.collector.api.web.rest;

import com.dot.collector.api.repository.MegaAttributeRepository;
import com.dot.collector.api.service.MegaAttributeService;
import com.dot.collector.api.service.dto.MegaAttributeDTO;
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
 * REST controller for managing {@link com.dot.collector.api.domain.MegaAttribute}.
 */
@RestController
@RequestMapping("/api/mega-attributes")
public class MegaAttributeResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaAttributeResource.class);

    private static final String ENTITY_NAME = "megaAttribute";

    @Value("${jhipster.clientApp.name:dotCollector}")
    private String applicationName;

    private final MegaAttributeService megaAttributeService;

    private final MegaAttributeRepository megaAttributeRepository;

    public MegaAttributeResource(MegaAttributeService megaAttributeService, MegaAttributeRepository megaAttributeRepository) {
        this.megaAttributeService = megaAttributeService;
        this.megaAttributeRepository = megaAttributeRepository;
    }

    /**
     * {@code POST  /mega-attributes} : Create a new megaAttribute.
     *
     * @param megaAttributeDTO the megaAttributeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new megaAttributeDTO, or with status {@code 400 (Bad Request)} if the megaAttribute has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MegaAttributeDTO> createMegaAttribute(@Valid @RequestBody MegaAttributeDTO megaAttributeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MegaAttribute : {}", megaAttributeDTO);
        if (megaAttributeDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaAttribute cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaAttributeDTO = megaAttributeService.save(megaAttributeDTO);
        return ResponseEntity.created(new URI("/api/mega-attributes/" + megaAttributeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaAttributeDTO.getId().toString()))
            .body(megaAttributeDTO);
    }

    /**
     * {@code PUT  /mega-attributes/:id} : Updates an existing megaAttribute.
     *
     * @param id the id of the megaAttributeDTO to save.
     * @param megaAttributeDTO the megaAttributeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaAttributeDTO,
     * or with status {@code 400 (Bad Request)} if the megaAttributeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the megaAttributeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MegaAttributeDTO> updateMegaAttribute(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MegaAttributeDTO megaAttributeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MegaAttribute : {}, {}", id, megaAttributeDTO);
        if (megaAttributeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaAttributeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaAttributeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        megaAttributeDTO = megaAttributeService.update(megaAttributeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaAttributeDTO.getId().toString()))
            .body(megaAttributeDTO);
    }

    /**
     * {@code PATCH  /mega-attributes/:id} : Partial updates given fields of an existing megaAttribute, field will ignore if it is null
     *
     * @param id the id of the megaAttributeDTO to save.
     * @param megaAttributeDTO the megaAttributeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaAttributeDTO,
     * or with status {@code 400 (Bad Request)} if the megaAttributeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the megaAttributeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the megaAttributeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MegaAttributeDTO> partialUpdateMegaAttribute(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MegaAttributeDTO megaAttributeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MegaAttribute partially : {}, {}", id, megaAttributeDTO);
        if (megaAttributeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaAttributeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaAttributeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MegaAttributeDTO> result = megaAttributeService.partialUpdate(megaAttributeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaAttributeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mega-attributes} : get all the Mega Attributes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mega Attributes in body.
     */
    @GetMapping("")
    public List<MegaAttributeDTO> getAllMegaAttributes() {
        LOG.debug("REST request to get all MegaAttributes");
        return megaAttributeService.findAll();
    }

    /**
     * {@code GET  /mega-attributes/:id} : get the "id" megaAttribute.
     *
     * @param id the id of the megaAttributeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the megaAttributeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MegaAttributeDTO> getMegaAttribute(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaAttribute : {}", id);
        Optional<MegaAttributeDTO> megaAttributeDTO = megaAttributeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaAttributeDTO);
    }

    /**
     * {@code DELETE  /mega-attributes/:id} : delete the "id" megaAttribute.
     *
     * @param id the id of the megaAttributeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaAttribute(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaAttribute : {}", id);
        megaAttributeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
