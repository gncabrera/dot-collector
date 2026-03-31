package com.dot.collector.api.web.rest;

import com.dot.collector.api.repository.MegaAttributeOptionRepository;
import com.dot.collector.api.service.MegaAttributeOptionService;
import com.dot.collector.api.service.dto.MegaAttributeOptionDTO;
import com.dot.collector.api.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.dot.collector.api.domain.MegaAttributeOption}.
 */
@RestController
@RequestMapping("/api/mega-attribute-options")
public class MegaAttributeOptionResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaAttributeOptionResource.class);

    private static final String ENTITY_NAME = "megaAttributeOption";

    @Value("${jhipster.clientApp.name:dotCollector}")
    private String applicationName;

    private final MegaAttributeOptionService megaAttributeOptionService;

    private final MegaAttributeOptionRepository megaAttributeOptionRepository;

    public MegaAttributeOptionResource(
        MegaAttributeOptionService megaAttributeOptionService,
        MegaAttributeOptionRepository megaAttributeOptionRepository
    ) {
        this.megaAttributeOptionService = megaAttributeOptionService;
        this.megaAttributeOptionRepository = megaAttributeOptionRepository;
    }

    /**
     * {@code POST  /mega-attribute-options} : Create a new megaAttributeOption.
     *
     * @param megaAttributeOptionDTO the megaAttributeOptionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new megaAttributeOptionDTO, or with status {@code 400 (Bad Request)} if the megaAttributeOption has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MegaAttributeOptionDTO> createMegaAttributeOption(@RequestBody MegaAttributeOptionDTO megaAttributeOptionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MegaAttributeOption : {}", megaAttributeOptionDTO);
        if (megaAttributeOptionDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaAttributeOption cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaAttributeOptionDTO = megaAttributeOptionService.save(megaAttributeOptionDTO);
        return ResponseEntity.created(new URI("/api/mega-attribute-options/" + megaAttributeOptionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaAttributeOptionDTO.getId().toString()))
            .body(megaAttributeOptionDTO);
    }

    /**
     * {@code PUT  /mega-attribute-options/:id} : Updates an existing megaAttributeOption.
     *
     * @param id the id of the megaAttributeOptionDTO to save.
     * @param megaAttributeOptionDTO the megaAttributeOptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaAttributeOptionDTO,
     * or with status {@code 400 (Bad Request)} if the megaAttributeOptionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the megaAttributeOptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MegaAttributeOptionDTO> updateMegaAttributeOption(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MegaAttributeOptionDTO megaAttributeOptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MegaAttributeOption : {}, {}", id, megaAttributeOptionDTO);
        if (megaAttributeOptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaAttributeOptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaAttributeOptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        megaAttributeOptionDTO = megaAttributeOptionService.update(megaAttributeOptionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaAttributeOptionDTO.getId().toString()))
            .body(megaAttributeOptionDTO);
    }

    /**
     * {@code PATCH  /mega-attribute-options/:id} : Partial updates given fields of an existing megaAttributeOption, field will ignore if it is null
     *
     * @param id the id of the megaAttributeOptionDTO to save.
     * @param megaAttributeOptionDTO the megaAttributeOptionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaAttributeOptionDTO,
     * or with status {@code 400 (Bad Request)} if the megaAttributeOptionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the megaAttributeOptionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the megaAttributeOptionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MegaAttributeOptionDTO> partialUpdateMegaAttributeOption(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MegaAttributeOptionDTO megaAttributeOptionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MegaAttributeOption partially : {}, {}", id, megaAttributeOptionDTO);
        if (megaAttributeOptionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaAttributeOptionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaAttributeOptionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MegaAttributeOptionDTO> result = megaAttributeOptionService.partialUpdate(megaAttributeOptionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaAttributeOptionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mega-attribute-options} : get all the Mega Attribute Options.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mega Attribute Options in body.
     */
    @GetMapping("")
    public List<MegaAttributeOptionDTO> getAllMegaAttributeOptions() {
        LOG.debug("REST request to get all MegaAttributeOptions");
        return megaAttributeOptionService.findAll();
    }

    /**
     * {@code GET  /mega-attribute-options/:id} : get the "id" megaAttributeOption.
     *
     * @param id the id of the megaAttributeOptionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the megaAttributeOptionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MegaAttributeOptionDTO> getMegaAttributeOption(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaAttributeOption : {}", id);
        Optional<MegaAttributeOptionDTO> megaAttributeOptionDTO = megaAttributeOptionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaAttributeOptionDTO);
    }

    /**
     * {@code DELETE  /mega-attribute-options/:id} : delete the "id" megaAttributeOption.
     *
     * @param id the id of the megaAttributeOptionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaAttributeOption(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaAttributeOption : {}", id);
        megaAttributeOptionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
