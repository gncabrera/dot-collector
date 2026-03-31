package com.dot.collector.api.web.rest;

import com.dot.collector.api.repository.FollowingProfileRepository;
import com.dot.collector.api.service.FollowingProfileService;
import com.dot.collector.api.service.dto.FollowingProfileDTO;
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
 * REST controller for managing {@link com.dot.collector.api.domain.FollowingProfile}.
 */
@RestController
@RequestMapping("/api/following-profiles")
public class FollowingProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(FollowingProfileResource.class);

    private static final String ENTITY_NAME = "followingProfile";

    @Value("${jhipster.clientApp.name:dotCollector}")
    private String applicationName;

    private final FollowingProfileService followingProfileService;

    private final FollowingProfileRepository followingProfileRepository;

    public FollowingProfileResource(
        FollowingProfileService followingProfileService,
        FollowingProfileRepository followingProfileRepository
    ) {
        this.followingProfileService = followingProfileService;
        this.followingProfileRepository = followingProfileRepository;
    }

    /**
     * {@code POST  /following-profiles} : Create a new followingProfile.
     *
     * @param followingProfileDTO the followingProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new followingProfileDTO, or with status {@code 400 (Bad Request)} if the followingProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FollowingProfileDTO> createFollowingProfile(@RequestBody FollowingProfileDTO followingProfileDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save FollowingProfile : {}", followingProfileDTO);
        if (followingProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new followingProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        followingProfileDTO = followingProfileService.save(followingProfileDTO);
        return ResponseEntity.created(new URI("/api/following-profiles/" + followingProfileDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, followingProfileDTO.getId().toString()))
            .body(followingProfileDTO);
    }

    /**
     * {@code PUT  /following-profiles/:id} : Updates an existing followingProfile.
     *
     * @param id the id of the followingProfileDTO to save.
     * @param followingProfileDTO the followingProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated followingProfileDTO,
     * or with status {@code 400 (Bad Request)} if the followingProfileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the followingProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FollowingProfileDTO> updateFollowingProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FollowingProfileDTO followingProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FollowingProfile : {}, {}", id, followingProfileDTO);
        if (followingProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, followingProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!followingProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        followingProfileDTO = followingProfileService.update(followingProfileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, followingProfileDTO.getId().toString()))
            .body(followingProfileDTO);
    }

    /**
     * {@code PATCH  /following-profiles/:id} : Partial updates given fields of an existing followingProfile, field will ignore if it is null
     *
     * @param id the id of the followingProfileDTO to save.
     * @param followingProfileDTO the followingProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated followingProfileDTO,
     * or with status {@code 400 (Bad Request)} if the followingProfileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the followingProfileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the followingProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FollowingProfileDTO> partialUpdateFollowingProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody FollowingProfileDTO followingProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FollowingProfile partially : {}, {}", id, followingProfileDTO);
        if (followingProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, followingProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!followingProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FollowingProfileDTO> result = followingProfileService.partialUpdate(followingProfileDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, followingProfileDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /following-profiles} : get all the Following Profiles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Following Profiles in body.
     */
    @GetMapping("")
    public List<FollowingProfileDTO> getAllFollowingProfiles() {
        LOG.debug("REST request to get all FollowingProfiles");
        return followingProfileService.findAll();
    }

    /**
     * {@code GET  /following-profiles/:id} : get the "id" followingProfile.
     *
     * @param id the id of the followingProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the followingProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FollowingProfileDTO> getFollowingProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FollowingProfile : {}", id);
        Optional<FollowingProfileDTO> followingProfileDTO = followingProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(followingProfileDTO);
    }

    /**
     * {@code DELETE  /following-profiles/:id} : delete the "id" followingProfile.
     *
     * @param id the id of the followingProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollowingProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FollowingProfile : {}", id);
        followingProfileService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
