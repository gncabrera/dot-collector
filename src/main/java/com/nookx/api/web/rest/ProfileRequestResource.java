package com.nookx.api.web.rest;

import com.nookx.api.domain.ProfileRequest;
import com.nookx.api.repository.ProfileRequestRepository;
import com.nookx.api.service.ProfileRequestService;
import com.nookx.api.service.dto.ProfileRequestDTO;
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
 * REST controller for managing {@link ProfileRequest}.
 */
@RestController
@RequestMapping("/api/profile-requests")
public class ProfileRequestResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileRequestResource.class);

    private static final String ENTITY_NAME = "profileRequest";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final ProfileRequestService profileRequestService;

    private final ProfileRequestRepository profileRequestRepository;

    public ProfileRequestResource(ProfileRequestService profileRequestService, ProfileRequestRepository profileRequestRepository) {
        this.profileRequestService = profileRequestService;
        this.profileRequestRepository = profileRequestRepository;
    }

    /**
     * {@code POST  /profile-requests} : Create a new profileRequest.
     *
     * @param profileRequestDTO the profileRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profileRequestDTO, or with status {@code 400 (Bad Request)} if the profileRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProfileRequestDTO> createProfileRequest(@RequestBody ProfileRequestDTO profileRequestDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProfileRequest : {}", profileRequestDTO);
        if (profileRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new profileRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        profileRequestDTO = profileRequestService.save(profileRequestDTO);
        return ResponseEntity.created(new URI("/api/profile-requests/" + profileRequestDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, profileRequestDTO.getId().toString()))
            .body(profileRequestDTO);
    }

    /**
     * {@code PUT  /profile-requests/:id} : Updates an existing profileRequest.
     *
     * @param id the id of the profileRequestDTO to save.
     * @param profileRequestDTO the profileRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileRequestDTO,
     * or with status {@code 400 (Bad Request)} if the profileRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profileRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProfileRequestDTO> updateProfileRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProfileRequestDTO profileRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProfileRequest : {}, {}", id, profileRequestDTO);
        if (profileRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profileRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        profileRequestDTO = profileRequestService.update(profileRequestDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profileRequestDTO.getId().toString()))
            .body(profileRequestDTO);
    }

    /**
     * {@code PATCH  /profile-requests/:id} : Partial updates given fields of an existing profileRequest, field will ignore if it is null
     *
     * @param id the id of the profileRequestDTO to save.
     * @param profileRequestDTO the profileRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileRequestDTO,
     * or with status {@code 400 (Bad Request)} if the profileRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the profileRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the profileRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProfileRequestDTO> partialUpdateProfileRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ProfileRequestDTO profileRequestDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProfileRequest partially : {}, {}", id, profileRequestDTO);
        if (profileRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profileRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProfileRequestDTO> result = profileRequestService.partialUpdate(profileRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profileRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /profile-requests} : get all the Profile Requests.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Profile Requests in body.
     */
    @GetMapping("")
    public List<ProfileRequestDTO> getAllProfileRequests() {
        LOG.debug("REST request to get all ProfileRequests");
        return profileRequestService.findAll();
    }

    /**
     * {@code GET  /profile-requests/:id} : get the "id" profileRequest.
     *
     * @param id the id of the profileRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profileRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileRequestDTO> getProfileRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProfileRequest : {}", id);
        Optional<ProfileRequestDTO> profileRequestDTO = profileRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profileRequestDTO);
    }

    /**
     * {@code DELETE  /profile-requests/:id} : delete the "id" profileRequest.
     *
     * @param id the id of the profileRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfileRequest(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProfileRequest : {}", id);
        profileRequestService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
