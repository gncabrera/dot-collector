package com.nookx.api.web.rest;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.repository.MegaAssetRepository;
import com.nookx.api.service.MegaAssetService;
import com.nookx.api.service.dto.MegaAssetDTO;
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
 * REST controller for managing {@link MegaAsset}.
 */
@RestController
@RequestMapping("/api/mega-assets")
public class MegaAssetResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaAssetResource.class);

    private static final String ENTITY_NAME = "megaAsset";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final MegaAssetService megaAssetService;

    private final MegaAssetRepository megaAssetRepository;

    public MegaAssetResource(MegaAssetService megaAssetService, MegaAssetRepository megaAssetRepository) {
        this.megaAssetService = megaAssetService;
        this.megaAssetRepository = megaAssetRepository;
    }

    /**
     * {@code POST  /mega-assets} : Create a new megaAsset.
     *
     * @param megaAssetDTO the megaAssetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new megaAssetDTO, or with status {@code 400 (Bad Request)} if the megaAsset has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MegaAssetDTO> createMegaAsset(@Valid @RequestBody MegaAssetDTO megaAssetDTO) throws URISyntaxException {
        LOG.debug("REST request to save MegaAsset : {}", megaAssetDTO);
        if (megaAssetDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaAsset cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaAssetDTO = megaAssetService.save(megaAssetDTO);
        return ResponseEntity.created(new URI("/api/mega-assets/" + megaAssetDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaAssetDTO.getId().toString()))
            .body(megaAssetDTO);
    }

    /**
     * {@code PUT  /mega-assets/:id} : Updates an existing megaAsset.
     *
     * @param id the id of the megaAssetDTO to save.
     * @param megaAssetDTO the megaAssetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaAssetDTO,
     * or with status {@code 400 (Bad Request)} if the megaAssetDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the megaAssetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MegaAssetDTO> updateMegaAsset(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MegaAssetDTO megaAssetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MegaAsset : {}, {}", id, megaAssetDTO);
        if (megaAssetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaAssetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaAssetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        megaAssetDTO = megaAssetService.update(megaAssetDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaAssetDTO.getId().toString()))
            .body(megaAssetDTO);
    }

    /**
     * {@code PATCH  /mega-assets/:id} : Partial updates given fields of an existing megaAsset, field will ignore if it is null
     *
     * @param id the id of the megaAssetDTO to save.
     * @param megaAssetDTO the megaAssetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated megaAssetDTO,
     * or with status {@code 400 (Bad Request)} if the megaAssetDTO is not valid,
     * or with status {@code 404 (Not Found)} if the megaAssetDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the megaAssetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MegaAssetDTO> partialUpdateMegaAsset(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MegaAssetDTO megaAssetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MegaAsset partially : {}, {}", id, megaAssetDTO);
        if (megaAssetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaAssetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaAssetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MegaAssetDTO> result = megaAssetService.partialUpdate(megaAssetDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaAssetDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /mega-assets} : get all the Mega Assets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Mega Assets in body.
     */
    @GetMapping("")
    public List<MegaAssetDTO> getAllMegaAssets() {
        LOG.debug("REST request to get all MegaAssets");
        return megaAssetService.findAll();
    }

    /**
     * {@code GET  /mega-assets/:id} : get the "id" megaAsset.
     *
     * @param id the id of the megaAssetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the megaAssetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MegaAssetDTO> getMegaAsset(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaAsset : {}", id);
        Optional<MegaAssetDTO> megaAssetDTO = megaAssetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaAssetDTO);
    }

    /**
     * {@code DELETE  /mega-assets/:id} : delete the "id" megaAsset.
     *
     * @param id the id of the megaAssetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaAsset(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaAsset : {}", id);
        megaAssetService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
