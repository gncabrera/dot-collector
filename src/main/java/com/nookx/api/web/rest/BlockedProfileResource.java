package com.nookx.api.web.rest;

import com.nookx.api.domain.BlockedProfile;
import com.nookx.api.repository.BlockedProfileRepository;
import com.nookx.api.service.BlockedProfileService;
import com.nookx.api.service.dto.BlockedProfileDTO;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link BlockedProfile}.
 */
@RestController
@RequestMapping("/api/blocked-profiles")
public class BlockedProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(BlockedProfileResource.class);

    private static final String ENTITY_NAME = "blockedProfile";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final BlockedProfileService blockedProfileService;

    private final BlockedProfileRepository blockedProfileRepository;

    public BlockedProfileResource(BlockedProfileService blockedProfileService, BlockedProfileRepository blockedProfileRepository) {
        this.blockedProfileService = blockedProfileService;
        this.blockedProfileRepository = blockedProfileRepository;
    }

    /**
     * {@code POST  /blocked-profiles} : Create a new blockedProfile.
     *
     * @param blockedProfileDTO the blockedProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new blockedProfileDTO, or with status {@code 400 (Bad Request)} if the blockedProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<BlockedProfileDTO> createBlockedProfile(@RequestBody BlockedProfileDTO blockedProfileDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save BlockedProfile : {}", blockedProfileDTO);
        if (blockedProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new blockedProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        blockedProfileDTO = blockedProfileService.save(blockedProfileDTO);
        return ResponseEntity.created(new URI("/api/blocked-profiles/" + blockedProfileDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, blockedProfileDTO.getId().toString()))
            .body(blockedProfileDTO);
    }

    /**
     * {@code PUT  /blocked-profiles/:id} : Updates an existing blockedProfile.
     *
     * @param id the id of the blockedProfileDTO to save.
     * @param blockedProfileDTO the blockedProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated blockedProfileDTO,
     * or with status {@code 400 (Bad Request)} if the blockedProfileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the blockedProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<BlockedProfileDTO> updateBlockedProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BlockedProfileDTO blockedProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update BlockedProfile : {}, {}", id, blockedProfileDTO);
        if (blockedProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, blockedProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!blockedProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        blockedProfileDTO = blockedProfileService.update(blockedProfileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, blockedProfileDTO.getId().toString()))
            .body(blockedProfileDTO);
    }

    /**
     * {@code PATCH  /blocked-profiles/:id} : Partial updates given fields of an existing blockedProfile, field will ignore if it is null
     *
     * @param id the id of the blockedProfileDTO to save.
     * @param blockedProfileDTO the blockedProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated blockedProfileDTO,
     * or with status {@code 400 (Bad Request)} if the blockedProfileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the blockedProfileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the blockedProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<BlockedProfileDTO> partialUpdateBlockedProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody BlockedProfileDTO blockedProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update BlockedProfile partially : {}, {}", id, blockedProfileDTO);
        if (blockedProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, blockedProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!blockedProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BlockedProfileDTO> result = blockedProfileService.partialUpdate(blockedProfileDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, blockedProfileDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /blocked-profiles} : get all the Blocked Profiles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Blocked Profiles in body.
     */
    @GetMapping("")
    public List<BlockedProfileDTO> getAllBlockedProfiles() {
        LOG.debug("REST request to get all BlockedProfiles");
        return blockedProfileService.findAll();
    }

    /**
     * {@code GET  /blocked-profiles/:id} : get the "id" blockedProfile.
     *
     * @param id the id of the blockedProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the blockedProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BlockedProfileDTO> getBlockedProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get BlockedProfile : {}", id);
        Optional<BlockedProfileDTO> blockedProfileDTO = blockedProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(blockedProfileDTO);
    }

    /**
     * {@code DELETE  /blocked-profiles/:id} : delete the "id" blockedProfile.
     *
     * @param id the id of the blockedProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlockedProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete BlockedProfile : {}", id);
        blockedProfileService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
