package com.dot.collector.api.web.rest;

import com.dot.collector.api.repository.MegaSetRepository;
import com.dot.collector.api.service.MegaSetService;
import com.dot.collector.api.service.dto.MegaSetDTO;
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
 * REST controller for managing {@link com.dot.collector.api.domain.MegaSet}.
 */
@RestController
@RequestMapping("/api/mega-sets")
public class MegaSetResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaSetResource.class);

    private static final String ENTITY_NAME = "megaSet";

    @Value("${jhipster.clientApp.name:dotCollector}")
    private String applicationName;

    private final MegaSetService megaSetService;

    private final MegaSetRepository megaSetRepository;

    public MegaSetResource(MegaSetService megaSetService, MegaSetRepository megaSetRepository) {
        this.megaSetService = megaSetService;
        this.megaSetRepository = megaSetRepository;
    }

    /**
     * {@code POST  /mega-sets} : Create a new megaSet.
     *
     * @param megaSetDTO the megaSetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new megaSetDTO, or with status {@code 400 (Bad Request)} if the megaSet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MegaSetDTO> createMegaSet(@Valid @RequestBody MegaSetDTO megaSetDTO) throws URISyntaxException {
        LOG.debug("REST request to save MegaSet : {}", megaSetDTO);
        if (megaSetDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaSet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaSetDTO = megaSetService.save(megaSetDTO);
        return ResponseEntity.created(new URI("/api/mega-sets/" + megaSetDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaSetDTO.getId().toString()))
            .body(megaSetDTO);
    }

    /**
     * {@code PUT  /mega-sets/:id} : Updates an existing megaSet.
     *
     * @param id the id of the megaSetDTO to save.
     * @param megaSetDTO the megaSetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaSetDTO,
     * or with status {@code 400 (Bad Request)} if the megaSetDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the megaSetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MegaSetDTO> updateMegaSet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MegaSetDTO megaSetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MegaSet : {}, {}", id, megaSetDTO);
        if (megaSetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaSetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaSetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        megaSetDTO = megaSetService.update(megaSetDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaSetDTO.getId().toString()))
            .body(megaSetDTO);
    }

    /**
     * {@code PATCH  /mega-sets/:id} : Partial updates given fields of an existing megaSet, field will ignore if it is null
     *
     * @param id the id of the megaSetDTO to save.
     * @param megaSetDTO the megaSetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaSetDTO,
     * or with status {@code 400 (Bad Request)} if the megaSetDTO is not valid,
     * or with status {@code 404 (Not Found)} if the megaSetDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the megaSetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MegaSetDTO> partialUpdateMegaSet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MegaSetDTO megaSetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MegaSet partially : {}, {}", id, megaSetDTO);
        if (megaSetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaSetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaSetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MegaSetDTO> result = megaSetService.partialUpdate(megaSetDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaSetDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mega-sets} : get all the Mega Sets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mega Sets in body.
     */
    @GetMapping("")
    public List<MegaSetDTO> getAllMegaSets() {
        LOG.debug("REST request to get all MegaSets");
        return megaSetService.findAll();
    }

    /**
     * {@code GET  /mega-sets/:id} : get the "id" megaSet.
     *
     * @param id the id of the megaSetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the megaSetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MegaSetDTO> getMegaSet(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaSet : {}", id);
        Optional<MegaSetDTO> megaSetDTO = megaSetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaSetDTO);
    }

    /**
     * {@code DELETE  /mega-sets/:id} : delete the "id" megaSet.
     *
     * @param id the id of the megaSetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaSet(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaSet : {}", id);
        megaSetService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
