package com.nookx.api.client.rest;

import com.nookx.api.client.dto.ClientCollectionDTO;
import com.nookx.api.client.dto.ClientCollectionLiteDTO;
import com.nookx.api.client.dto.ClientCollectionUpdateDTO;
import com.nookx.api.client.service.ClientCollectionService;
import com.nookx.api.domain.enumeration.ProfileCollectionType;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for client {@link ClientCollectionDTO} operations.
 */
@RestController
@RequestMapping("/api/client/collections")
public class ClientCollectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientCollectionResource.class);

    private static final String ENTITY_NAME = "clientCollection";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final ClientCollectionService clientCollectionService;

    public ClientCollectionResource(ClientCollectionService clientCollectionService) {
        this.clientCollectionService = clientCollectionService;
    }

    /**
     * {@code GET  /client-collections} : get collections for the current user.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list in body.
     */
    @GetMapping("")
    public List<ClientCollectionLiteDTO> getUserCollections() {
        LOG.debug("REST request to get user ClientCollections");
        return clientCollectionService.getUserCollections();
    }

    /**
     * {@code GET  /collections/types} : get all {@link ProfileCollectionType} values as strings.
     *
     * @return the list of enum constant names.
     */
    @GetMapping("/types")
    public List<String> getCollectionTypes() {
        LOG.debug("REST request to get ProfileCollectionType values");
        return Arrays.stream(ProfileCollectionType.values()).map(Enum::name).toList();
    }

    /**
     * {@code GET  /client-collections/:id} : get the collection by id.
     *
     * @param id the id of the collection to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the DTO, or {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientCollectionDTO> getCollectionById(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClientCollection : {}", id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(clientCollectionService.getCollectionById(id)));
    }

    /**
     * {@code POST  /client-collections} : create a new client collection.
     *
     * @param dto the collection to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new DTO,
     * or with status {@code 400 (Bad Request)} if the collection already has an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<ClientCollectionDTO> create(@Valid @RequestBody ClientCollectionDTO dto) throws URISyntaxException {
        LOG.debug("REST request to save ClientCollection : {}", dto);
        if (dto.getId() != null) {
            throw new BadRequestAlertException("A new clientCollection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClientCollectionDTO result = clientCollectionService.create(dto);
        return ResponseEntity.created(new URI("/api/client/collections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /client-collections/:id} : update an existing client collection.
     *
     * @param id the id of the collection to update.
     * @param dto the update payload containing fields to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated DTO,
     * or with status {@code 400 (Bad Request)} if the collection does not exist.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientCollectionDTO> update(@PathVariable("id") Long id, @Valid @RequestBody ClientCollectionUpdateDTO dto) {
        LOG.debug("REST request to update ClientCollection : {}, {}", id, dto);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ClientCollectionDTO result = clientCollectionService.update(id, dto);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /client-collections/:sourceCollectionId/clone} : clone a collection.
     *
     * @param sourceCollectionId id of the collection to clone from.
     * @param dto clone payload.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new DTO,
     * or with status {@code 400 (Bad Request)} if the DTO already has an ID,
     * or {@code 404 (Not Found)} if the clone could not be produced.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/{sourceCollectionId}/clone")
    public ResponseEntity<ClientCollectionDTO> cloneCollection(
        @PathVariable("sourceCollectionId") Long sourceCollectionId,
        @Valid @RequestBody ClientCollectionDTO dto
    ) throws URISyntaxException {
        LOG.debug("REST request to clone ClientCollection from {} : {}", sourceCollectionId, dto);
        ClientCollectionDTO result = clientCollectionService.cloneCollection(sourceCollectionId, dto);
        if (result == null || result.getId() == null) {
            return ResponseUtil.wrapOrNotFound(Optional.empty());
        }
        return ResponseEntity.created(new URI("/api/client/collections/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code DELETE  /client-collections/delete/:id} : delete a client collection along with its dependents.
     *
     * <p>Cleans up the dependents of the collection:
     * <ul>
     *   <li>image: removed</li>
     *   <li>interests: relations removed (interests themselves are kept)</li>
     *   <li>profile: untouched</li>
     *   <li>currency: untouched</li>
     *   <li>clone information: source references nulled out, owned clone information deleted</li>
     *   <li>profile collection sets: deleted</li>
     * </ul>
     *
     * @param id the id of the collection to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClientCollection : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        clientCollectionService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code PUT  /client-collections/:id/star} : star a public collection on behalf of the current profile.
     *
     * <p>Idempotent: returns {@code 200} with the current total stars even if the collection was already starred.
     *
     * @param id the id of the collection to star.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the current total stars in the body.
     */
    @PutMapping("/{id}/star")
    public ResponseEntity<Long> star(@PathVariable("id") Long id) {
        LOG.debug("REST request to star ClientCollection : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        long totalStars = clientCollectionService.star(id);
        return ResponseEntity.ok(totalStars);
    }

    /**
     * {@code DELETE  /client-collections/:id/star} : remove the current profile's star from a collection.
     *
     * <p>Idempotent: returns {@code 204} even if the collection was not starred.
     *
     * @param id the id of the collection to unstar.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/{id}/star")
    public ResponseEntity<Void> unstar(@PathVariable("id") Long id) {
        LOG.debug("REST request to unstar ClientCollection : {}", id);
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        clientCollectionService.unstar(id);
        return ResponseEntity.noContent().build();
    }
}
