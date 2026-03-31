package com.dot.collector.api.web.rest;

import com.dot.collector.api.repository.MegaSetPartCountRepository;
import com.dot.collector.api.service.MegaSetPartCountService;
import com.dot.collector.api.service.dto.MegaSetPartCountDTO;
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
 * REST controller for managing {@link com.dot.collector.api.domain.MegaSetPartCount}.
 */
@RestController
@RequestMapping("/api/mega-set-part-counts")
public class MegaSetPartCountResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaSetPartCountResource.class);

    private static final String ENTITY_NAME = "megaSetPartCount";

    @Value("${jhipster.clientApp.name:dotCollector}")
    private String applicationName;

    private final MegaSetPartCountService megaSetPartCountService;

    private final MegaSetPartCountRepository megaSetPartCountRepository;

    public MegaSetPartCountResource(
        MegaSetPartCountService megaSetPartCountService,
        MegaSetPartCountRepository megaSetPartCountRepository
    ) {
        this.megaSetPartCountService = megaSetPartCountService;
        this.megaSetPartCountRepository = megaSetPartCountRepository;
    }

    /**
     * {@code POST  /mega-set-part-counts} : Create a new megaSetPartCount.
     *
     * @param megaSetPartCountDTO the megaSetPartCountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new megaSetPartCountDTO, or with status {@code 400 (Bad Request)} if the megaSetPartCount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MegaSetPartCountDTO> createMegaSetPartCount(@RequestBody MegaSetPartCountDTO megaSetPartCountDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MegaSetPartCount : {}", megaSetPartCountDTO);
        if (megaSetPartCountDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaSetPartCount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaSetPartCountDTO = megaSetPartCountService.save(megaSetPartCountDTO);
        return ResponseEntity.created(new URI("/api/mega-set-part-counts/" + megaSetPartCountDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaSetPartCountDTO.getId().toString()))
            .body(megaSetPartCountDTO);
    }

    /**
     * {@code PUT  /mega-set-part-counts/:id} : Updates an existing megaSetPartCount.
     *
     * @param id the id of the megaSetPartCountDTO to save.
     * @param megaSetPartCountDTO the megaSetPartCountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaSetPartCountDTO,
     * or with status {@code 400 (Bad Request)} if the megaSetPartCountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the megaSetPartCountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MegaSetPartCountDTO> updateMegaSetPartCount(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MegaSetPartCountDTO megaSetPartCountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MegaSetPartCount : {}, {}", id, megaSetPartCountDTO);
        if (megaSetPartCountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaSetPartCountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaSetPartCountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        megaSetPartCountDTO = megaSetPartCountService.update(megaSetPartCountDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaSetPartCountDTO.getId().toString()))
            .body(megaSetPartCountDTO);
    }

    /**
     * {@code PATCH  /mega-set-part-counts/:id} : Partial updates given fields of an existing megaSetPartCount, field will ignore if it is null
     *
     * @param id the id of the megaSetPartCountDTO to save.
     * @param megaSetPartCountDTO the megaSetPartCountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaSetPartCountDTO,
     * or with status {@code 400 (Bad Request)} if the megaSetPartCountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the megaSetPartCountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the megaSetPartCountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MegaSetPartCountDTO> partialUpdateMegaSetPartCount(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MegaSetPartCountDTO megaSetPartCountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MegaSetPartCount partially : {}, {}", id, megaSetPartCountDTO);
        if (megaSetPartCountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaSetPartCountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaSetPartCountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MegaSetPartCountDTO> result = megaSetPartCountService.partialUpdate(megaSetPartCountDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaSetPartCountDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mega-set-part-counts} : get all the Mega Set Part Counts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mega Set Part Counts in body.
     */
    @GetMapping("")
    public List<MegaSetPartCountDTO> getAllMegaSetPartCounts() {
        LOG.debug("REST request to get all MegaSetPartCounts");
        return megaSetPartCountService.findAll();
    }

    /**
     * {@code GET  /mega-set-part-counts/:id} : get the "id" megaSetPartCount.
     *
     * @param id the id of the megaSetPartCountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the megaSetPartCountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MegaSetPartCountDTO> getMegaSetPartCount(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaSetPartCount : {}", id);
        Optional<MegaSetPartCountDTO> megaSetPartCountDTO = megaSetPartCountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaSetPartCountDTO);
    }

    /**
     * {@code DELETE  /mega-set-part-counts/:id} : delete the "id" megaSetPartCount.
     *
     * @param id the id of the megaSetPartCountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaSetPartCount(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaSetPartCount : {}", id);
        megaSetPartCountService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
