package com.nookx.api.web.rest;

import com.nookx.api.domain.ProfileRequestType;
import com.nookx.api.repository.ProfileRequestTypeRepository;
import com.nookx.api.service.ProfileRequestTypeService;
import com.nookx.api.service.dto.ProfileRequestTypeDTO;
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
 * REST controller for managing {@link ProfileRequestType}.
 */
@RestController
@RequestMapping("/api/profile-request-types")
public class ProfileRequestTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileRequestTypeResource.class);

    private static final String ENTITY_NAME = "profileRequestType";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final ProfileRequestTypeService profileRequestTypeService;

    private final ProfileRequestTypeRepository profileRequestTypeRepository;

    public ProfileRequestTypeResource(
        ProfileRequestTypeService profileRequestTypeService,
        ProfileRequestTypeRepository profileRequestTypeRepository
    ) {
        this.profileRequestTypeService = profileRequestTypeService;
        this.profileRequestTypeRepository = profileRequestTypeRepository;
    }

    /**
     * {@code POST  /profile-request-types} : Create a new profileRequestType.
     *
     * @param profileRequestTypeDTO the profileRequestTypeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new profileRequestTypeDTO, or with status {@code 400 (Bad Request)} if the profileRequestType has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ProfileRequestTypeDTO> createProfileRequestType(@Valid @RequestBody ProfileRequestTypeDTO profileRequestTypeDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ProfileRequestType : {}", profileRequestTypeDTO);
        if (profileRequestTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new profileRequestType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        profileRequestTypeDTO = profileRequestTypeService.save(profileRequestTypeDTO);
        return ResponseEntity.created(new URI("/api/profile-request-types/" + profileRequestTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, profileRequestTypeDTO.getId().toString()))
            .body(profileRequestTypeDTO);
    }

    /**
     * {@code PUT  /profile-request-types/:id} : Updates an existing profileRequestType.
     *
     * @param id the id of the profileRequestTypeDTO to save.
     * @param profileRequestTypeDTO the profileRequestTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileRequestTypeDTO,
     * or with status {@code 400 (Bad Request)} if the profileRequestTypeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the profileRequestTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProfileRequestTypeDTO> updateProfileRequestType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProfileRequestTypeDTO profileRequestTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProfileRequestType : {}, {}", id, profileRequestTypeDTO);
        if (profileRequestTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profileRequestTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileRequestTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        profileRequestTypeDTO = profileRequestTypeService.update(profileRequestTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profileRequestTypeDTO.getId().toString()))
            .body(profileRequestTypeDTO);
    }

    /**
     * {@code PATCH  /profile-request-types/:id} : Partial updates given fields of an existing profileRequestType, field will ignore if it is null
     *
     * @param id the id of the profileRequestTypeDTO to save.
     * @param profileRequestTypeDTO the profileRequestTypeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated profileRequestTypeDTO,
     * or with status {@code 400 (Bad Request)} if the profileRequestTypeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the profileRequestTypeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the profileRequestTypeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ProfileRequestTypeDTO> partialUpdateProfileRequestType(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProfileRequestTypeDTO profileRequestTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProfileRequestType partially : {}, {}", id, profileRequestTypeDTO);
        if (profileRequestTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, profileRequestTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!profileRequestTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ProfileRequestTypeDTO> result = profileRequestTypeService.partialUpdate(profileRequestTypeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, profileRequestTypeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /profile-request-types} : get all the Profile Request Types.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Profile Request Types in body.
     */
    @GetMapping("")
    public List<ProfileRequestTypeDTO> getAllProfileRequestTypes() {
        LOG.debug("REST request to get all ProfileRequestTypes");
        return profileRequestTypeService.findAll();
    }

    /**
     * {@code GET  /profile-request-types/:id} : get the "id" profileRequestType.
     *
     * @param id the id of the profileRequestTypeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the profileRequestTypeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProfileRequestTypeDTO> getProfileRequestType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProfileRequestType : {}", id);
        Optional<ProfileRequestTypeDTO> profileRequestTypeDTO = profileRequestTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(profileRequestTypeDTO);
    }

    /**
     * {@code DELETE  /profile-request-types/:id} : delete the "id" profileRequestType.
     *
     * @param id the id of the profileRequestTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfileRequestType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProfileRequestType : {}", id);
        profileRequestTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
