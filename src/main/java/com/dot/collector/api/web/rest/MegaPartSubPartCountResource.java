package com.dot.collector.api.web.rest;

import com.dot.collector.api.repository.MegaPartSubPartCountRepository;
import com.dot.collector.api.service.MegaPartSubPartCountService;
import com.dot.collector.api.service.dto.MegaPartSubPartCountDTO;
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
 * REST controller for managing {@link com.dot.collector.api.domain.MegaPartSubPartCount}.
 */
@RestController
@RequestMapping("/api/mega-part-sub-part-counts")
public class MegaPartSubPartCountResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaPartSubPartCountResource.class);

    private static final String ENTITY_NAME = "megaPartSubPartCount";

    @Value("${jhipster.clientApp.name:dotCollector}")
    private String applicationName;

    private final MegaPartSubPartCountService megaPartSubPartCountService;

    private final MegaPartSubPartCountRepository megaPartSubPartCountRepository;

    public MegaPartSubPartCountResource(
        MegaPartSubPartCountService megaPartSubPartCountService,
        MegaPartSubPartCountRepository megaPartSubPartCountRepository
    ) {
        this.megaPartSubPartCountService = megaPartSubPartCountService;
        this.megaPartSubPartCountRepository = megaPartSubPartCountRepository;
    }

    /**
     * {@code POST  /mega-part-sub-part-counts} : Create a new megaPartSubPartCount.
     *
     * @param megaPartSubPartCountDTO the megaPartSubPartCountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new megaPartSubPartCountDTO, or with status {@code 400 (Bad Request)} if the megaPartSubPartCount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MegaPartSubPartCountDTO> createMegaPartSubPartCount(@RequestBody MegaPartSubPartCountDTO megaPartSubPartCountDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MegaPartSubPartCount : {}", megaPartSubPartCountDTO);
        if (megaPartSubPartCountDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaPartSubPartCount cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaPartSubPartCountDTO = megaPartSubPartCountService.save(megaPartSubPartCountDTO);
        return ResponseEntity.created(new URI("/api/mega-part-sub-part-counts/" + megaPartSubPartCountDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaPartSubPartCountDTO.getId().toString()))
            .body(megaPartSubPartCountDTO);
    }

    /**
     * {@code PUT  /mega-part-sub-part-counts/:id} : Updates an existing megaPartSubPartCount.
     *
     * @param id the id of the megaPartSubPartCountDTO to save.
     * @param megaPartSubPartCountDTO the megaPartSubPartCountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaPartSubPartCountDTO,
     * or with status {@code 400 (Bad Request)} if the megaPartSubPartCountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the megaPartSubPartCountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MegaPartSubPartCountDTO> updateMegaPartSubPartCount(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MegaPartSubPartCountDTO megaPartSubPartCountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MegaPartSubPartCount : {}, {}", id, megaPartSubPartCountDTO);
        if (megaPartSubPartCountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaPartSubPartCountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaPartSubPartCountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        megaPartSubPartCountDTO = megaPartSubPartCountService.update(megaPartSubPartCountDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaPartSubPartCountDTO.getId().toString()))
            .body(megaPartSubPartCountDTO);
    }

    /**
     * {@code PATCH  /mega-part-sub-part-counts/:id} : Partial updates given fields of an existing megaPartSubPartCount, field will ignore if it is null
     *
     * @param id the id of the megaPartSubPartCountDTO to save.
     * @param megaPartSubPartCountDTO the megaPartSubPartCountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaPartSubPartCountDTO,
     * or with status {@code 400 (Bad Request)} if the megaPartSubPartCountDTO is not valid,
     * or with status {@code 404 (Not Found)} if the megaPartSubPartCountDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the megaPartSubPartCountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MegaPartSubPartCountDTO> partialUpdateMegaPartSubPartCount(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MegaPartSubPartCountDTO megaPartSubPartCountDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MegaPartSubPartCount partially : {}, {}", id, megaPartSubPartCountDTO);
        if (megaPartSubPartCountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaPartSubPartCountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaPartSubPartCountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MegaPartSubPartCountDTO> result = megaPartSubPartCountService.partialUpdate(megaPartSubPartCountDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaPartSubPartCountDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mega-part-sub-part-counts} : get all the Mega Part Sub Part Counts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mega Part Sub Part Counts in body.
     */
    @GetMapping("")
    public List<MegaPartSubPartCountDTO> getAllMegaPartSubPartCounts() {
        LOG.debug("REST request to get all MegaPartSubPartCounts");
        return megaPartSubPartCountService.findAll();
    }

    /**
     * {@code GET  /mega-part-sub-part-counts/:id} : get the "id" megaPartSubPartCount.
     *
     * @param id the id of the megaPartSubPartCountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the megaPartSubPartCountDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MegaPartSubPartCountDTO> getMegaPartSubPartCount(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaPartSubPartCount : {}", id);
        Optional<MegaPartSubPartCountDTO> megaPartSubPartCountDTO = megaPartSubPartCountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaPartSubPartCountDTO);
    }

    /**
     * {@code DELETE  /mega-part-sub-part-counts/:id} : delete the "id" megaPartSubPartCount.
     *
     * @param id the id of the megaPartSubPartCountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaPartSubPartCount(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaPartSubPartCount : {}", id);
        megaPartSubPartCountService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
