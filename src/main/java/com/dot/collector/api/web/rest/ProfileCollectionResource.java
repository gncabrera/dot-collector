package com.dot.collector.api.web.rest;

import com.dot.collector.api.repository.ProfileCollectionRepository;
import com.dot.collector.api.service.ProfileCollectionService;
import com.dot.collector.api.service.dto.ProfileCollectionDTO;
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
 * REST controller for managing {@link com.dot.collector.api.domain.ProfileCollection}.
 */
@RestController
@RequestMapping("/api/profile-collections")
public class ProfileCollectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileCollectionResource.class);

    private static final String ENTITY_NAME = "profileCollection";

    @Value("${jhipster.clientApp.name:dotCollector}")
    private String applicationName;

    private final ProfileCollectionService profileCollectionService;

    private final ProfileCollectionRepository profileCollectionRepository;

    public ProfileCollectionResource(
        ProfileCollectionService profileCollectionService,
        ProfileCollectionRepository profileCollectionRepository
    ) {
        this.profileCollectionService = profileCollectionService;
        this.profileCollectionRepository = profileCollectionRepository;
    }

    /**
     * {@code POST  /profile-collections} : Create a new profileCollection.
     *
     * @param profileCollectionDTO the profileCollectionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profileCollectionDTO, or with status {@code 400 (Bad Request)} if the profileCollection has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProfileCollectionDTO> createProfileCollection(@RequestBody ProfileCollectionDTO profileCollectionDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProfileCollection : {}", profileCollectionDTO);
        if (profileCollectionDTO.getId() != null) {
            throw new BadRequestAlertException("A new profileCollection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        profileCollectionDTO = profileCollectionService.save(profileCollectionDTO);
        return ResponseEntity.created(new URI("/api/profile-collections/" + profileCollectionDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, profileCollectionDTO.getId().toString()))
            .body(profileCollectionDTO);
    }

    /**
     * {@code PUT  /profile-collections/:id} : Updates an existing profileCollection.
     *
     * @param id the id of the profileCollectionDTO to save.
     * @param profileCollectionDTO the profileCollectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileCollectionDTO,
     * or with status {@code 400 (Bad Request)} if the profileCollectionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profileCollectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProfileCollectionDTO> updateProfileCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProfileCollectionDTO profileCollectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProfileCollection : {}, {}", id, profileCollectionDTO);
        if (profileCollectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profileCollectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileCollectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        profileCollectionDTO = profileCollectionService.update(profileCollectionDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profileCollectionDTO.getId().toString()))
            .body(profileCollectionDTO);
    }

    /**
     * {@code PATCH  /profile-collections/:id} : Partial updates given fields of an existing profileCollection, field will ignore if it is null
     *
     * @param id the id of the profileCollectionDTO to save.
     * @param profileCollectionDTO the profileCollectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileCollectionDTO,
     * or with status {@code 400 (Bad Request)} if the profileCollectionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the profileCollectionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the profileCollectionDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProfileCollectionDTO> partialUpdateProfileCollection(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProfileCollectionDTO profileCollectionDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProfileCollection partially : {}, {}", id, profileCollectionDTO);
        if (profileCollectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profileCollectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileCollectionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProfileCollectionDTO> result = profileCollectionService.partialUpdate(profileCollectionDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profileCollectionDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /profile-collections} : get all the Profile Collections.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Profile Collections in body.
     */
    @GetMapping("")
    public List<ProfileCollectionDTO> getAllProfileCollections() {
        LOG.debug("REST request to get all ProfileCollections");
        return profileCollectionService.findAll();
    }

    /**
     * {@code GET  /profile-collections/:id} : get the "id" profileCollection.
     *
     * @param id the id of the profileCollectionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profileCollectionDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileCollectionDTO> getProfileCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProfileCollection : {}", id);
        Optional<ProfileCollectionDTO> profileCollectionDTO = profileCollectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profileCollectionDTO);
    }

    /**
     * {@code DELETE  /profile-collections/:id} : delete the "id" profileCollection.
     *
     * @param id the id of the profileCollectionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfileCollection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProfileCollection : {}", id);
        profileCollectionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
