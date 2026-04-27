package com.nookx.api.web.rest;

import com.nookx.api.domain.ProfileCollectionSet;
import com.nookx.api.repository.ProfileCollectionSetRepository;
import com.nookx.api.service.ProfileCollectionSetService;
import com.nookx.api.service.dto.ProfileCollectionSetDTO;
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
 * REST controller for managing {@link ProfileCollectionSet}.
 */
@RestController
@RequestMapping("/api/profile-collection-sets")
public class ProfileCollectionSetResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileCollectionSetResource.class);

    private static final String ENTITY_NAME = "profileCollectionSet";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final ProfileCollectionSetService profileCollectionSetService;

    private final ProfileCollectionSetRepository profileCollectionSetRepository;

    public ProfileCollectionSetResource(
        ProfileCollectionSetService profileCollectionSetService,
        ProfileCollectionSetRepository profileCollectionSetRepository
    ) {
        this.profileCollectionSetService = profileCollectionSetService;
        this.profileCollectionSetRepository = profileCollectionSetRepository;
    }

    /**
     * {@code POST  /profile-collection-sets} : Create a new profileCollectionSet.
     *
     * @param profileCollectionSetDTO the profileCollectionSetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profileCollectionSetDTO, or with status {@code 400 (Bad Request)} if the profileCollectionSet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProfileCollectionSetDTO> createProfileCollectionSet(@RequestBody ProfileCollectionSetDTO profileCollectionSetDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProfileCollectionSet : {}", profileCollectionSetDTO);
        if (profileCollectionSetDTO.getId() != null) {
            throw new BadRequestAlertException("A new profileCollectionSet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        profileCollectionSetDTO = profileCollectionSetService.save(profileCollectionSetDTO);
        return ResponseEntity.created(new URI("/api/profile-collection-sets/" + profileCollectionSetDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, profileCollectionSetDTO.getId().toString()))
            .body(profileCollectionSetDTO);
    }

    /**
     * {@code PUT  /profile-collection-sets/:id} : Updates an existing profileCollectionSet.
     *
     * @param id the id of the profileCollectionSetDTO to save.
     * @param profileCollectionSetDTO the profileCollectionSetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileCollectionSetDTO,
     * or with status {@code 400 (Bad Request)} if the profileCollectionSetDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profileCollectionSetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProfileCollectionSetDTO> updateProfileCollectionSet(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProfileCollectionSetDTO profileCollectionSetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProfileCollectionSet : {}, {}", id, profileCollectionSetDTO);
        if (profileCollectionSetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profileCollectionSetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileCollectionSetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        profileCollectionSetDTO = profileCollectionSetService.update(profileCollectionSetDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profileCollectionSetDTO.getId().toString()))
            .body(profileCollectionSetDTO);
    }

    /**
     * {@code PATCH  /profile-collection-sets/:id} : Partial updates given fields of an existing profileCollectionSet, field will ignore if it is null
     *
     * @param id the id of the profileCollectionSetDTO to save.
     * @param profileCollectionSetDTO the profileCollectionSetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileCollectionSetDTO,
     * or with status {@code 400 (Bad Request)} if the profileCollectionSetDTO is not valid,
     * or with status {@code 404 (Not Found)} if the profileCollectionSetDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the profileCollectionSetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProfileCollectionSetDTO> partialUpdateProfileCollectionSet(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProfileCollectionSetDTO profileCollectionSetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProfileCollectionSet partially : {}, {}", id, profileCollectionSetDTO);
        if (profileCollectionSetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profileCollectionSetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileCollectionSetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProfileCollectionSetDTO> result = profileCollectionSetService.partialUpdate(profileCollectionSetDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profileCollectionSetDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /profile-collection-sets} : get all the Profile Collection Sets.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Profile Collection Sets in body.
     */
    @GetMapping("")
    public List<ProfileCollectionSetDTO> getAllProfileCollectionSets() {
        LOG.debug("REST request to get all ProfileCollectionSets");
        return profileCollectionSetService.findAll();
    }

    /**
     * {@code GET  /profile-collection-sets/:id} : get the "id" profileCollectionSet.
     *
     * @param id the id of the profileCollectionSetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profileCollectionSetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileCollectionSetDTO> getProfileCollectionSet(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProfileCollectionSet : {}", id);
        Optional<ProfileCollectionSetDTO> profileCollectionSetDTO = profileCollectionSetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profileCollectionSetDTO);
    }

    /**
     * {@code DELETE  /profile-collection-sets/:id} : delete the "id" profileCollectionSet.
     *
     * @param id the id of the profileCollectionSetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfileCollectionSet(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProfileCollectionSet : {}", id);
        profileCollectionSetService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
