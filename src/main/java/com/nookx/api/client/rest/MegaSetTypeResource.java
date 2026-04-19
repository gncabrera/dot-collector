package com.nookx.api.client.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.nookx.api.domain.MegaSetType;
import com.nookx.api.service.MegaSetTypeService;
import com.nookx.api.service.dto.MegaAttributeDTO;
import com.nookx.api.service.dto.MegaSetTypeDTO;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing the versioned dynamic schemas backed by
 * {@link MegaSetType}.
 *
 * <h2>Design</h2>
 * <ul>
 *   <li>A type version is <b>immutable</b>: there is intentionally no PUT/PATCH endpoint.</li>
 *   <li>To evolve a schema, call {@code POST /api/set-types/{id}/versions} with the
 *       desired attribute list. Existing data keeps pointing at the previous version, so
 *       no migration is required.</li>
 *   <li>{@code GET /api/set-types?isLatest=true} returns the current schemas the UI
 *       should use for new entries.</li>
 *   <li>{@code GET /api/set-types/{id}/schema} returns the attribute definitions to
 *       drive a dynamic form on the frontend.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/client/set-types")
public class MegaSetTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaSetTypeResource.class);

    private static final String ENTITY_NAME = "megaSetType";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final MegaSetTypeService megaSetTypeService;

    public MegaSetTypeResource(MegaSetTypeService megaSetTypeService) {
        this.megaSetTypeService = megaSetTypeService;
    }

    /**
     * {@code POST  /set-types} : create a brand new MegaSetType (version 1).
     * Use {@link #createNewVersion(Long, List)} to evolve an existing schema.
     */
    @PostMapping("")
    public ResponseEntity<MegaSetTypeDTO> create(@Valid @RequestBody MegaSetTypeDTO dto) throws URISyntaxException {
        LOG.debug("REST request to create MegaSetType : {}", dto);
        if (dto.getId() != null) {
            throw new BadRequestAlertException("A new megaSetType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MegaSetTypeDTO result = megaSetTypeService.create(dto);
        return ResponseEntity.created(new URI("/api/set-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code POST  /set-types/{id}/versions} : publish a new version of an existing schema.
     * The body is the full attribute list for the new version. Attributes are matched by name:
     * new ones get added, missing ones are dropped, matching ones are patched.
     */
    @PostMapping("/{id}/versions")
    public ResponseEntity<MegaSetTypeDTO> createNewVersion(
        @PathVariable("id") Long id,
        @Valid @RequestBody List<MegaAttributeDTO> attributes
    ) throws URISyntaxException {
        LOG.debug("REST request to create a new version of MegaSetType : {}", id);
        MegaSetTypeDTO result = megaSetTypeService.createNewVersion(id, attributes);
        return ResponseEntity.created(new URI("/api/set-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /set-types} : list MegaSetTypes with optional filters.
     *
     * @param name      optional filter by exact type name (e.g. "BOOK")
     * @param isLatest  optional filter on the latest version flag
     * @param active    optional filter on the active flag
     */
    @GetMapping("")
    public List<MegaSetTypeDTO> list(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "isLatest", required = false) Boolean isLatest,
        @RequestParam(value = "active", required = false) Boolean active
    ) {
        LOG.debug("REST request to list MegaSetTypes name={}, isLatest={}, active={}", name, isLatest, active);
        return megaSetTypeService.search(name, isLatest, active);
    }

    /**
     * {@code GET  /set-types/{id}} : fetch a single MegaSetType (with attributes).
     */
    @GetMapping("/{id}")
    public ResponseEntity<MegaSetTypeDTO> getById(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaSetType : {}", id);
        return ResponseUtil.wrapOrNotFound(megaSetTypeService.findOne(id));
    }

    /**
     * {@code GET  /set-types/{id}/schema} : fetch the attribute schema for a type version.
     * Convenience wrapper used by the dynamic-form UI – it just returns the attribute list
     * of the requested type version.
     */
    @GetMapping("/{id}/schema")
    public ResponseEntity<List<MegaAttributeDTO>> getSchema(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaSetType schema : {}", id);
        return megaSetTypeService
            .findOne(id)
            .map(t -> ResponseEntity.ok(List.copyOf(t.getAttributes())))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * {@code GET  /set-types/by-name/{name}/latest} : fetch the latest version of a
     * named schema. Useful when the UI only knows the type name (e.g. "BOOK") and wants
     * to render the freshest form.
     */
    @GetMapping("/by-name/{name}/latest")
    public ResponseEntity<MegaSetTypeDTO> getLatestByName(@PathVariable("name") String name) {
        LOG.debug("REST request to get latest MegaSetType : {}", name);
        return ResponseUtil.wrapOrNotFound(megaSetTypeService.findLatestByName(name));
    }

    /**
     * {@code GET  /set-types/by-name/{name}/versions} : full version history of a schema.
     */
    @GetMapping("/by-name/{name}/versions")
    public List<MegaSetTypeDTO> getVersionsByName(@PathVariable("name") String name) {
        LOG.debug("REST request to get MegaSetType versions : {}", name);
        return megaSetTypeService.findVersionsByName(name);
    }

    /**
     * {@code POST  /set-types/{id}/validate} : validate a JSON attributes payload against
     * a specific schema version. Returns 204 on success or 400 with the first violation.
     */
    @PostMapping("/{id}/validate")
    public ResponseEntity<Void> validate(@PathVariable("id") Long id, @RequestBody JsonNode attributes) {
        LOG.debug("REST request to validate attributes against MegaSetType : {}", id);
        megaSetTypeService.validateAttributes(id, attributes);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code DELETE  /set-types/{id}} : delete a MegaSetType.
     * Will be rejected by the database if any MegaSet still references it.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaSetType : {}", id);
        megaSetTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
