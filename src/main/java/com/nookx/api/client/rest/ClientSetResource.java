package com.nookx.api.client.rest;

import com.nookx.api.client.dto.ClientSetDTO;
import com.nookx.api.client.service.ClientSetService;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
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
 * REST controller for client {@link ClientSetDTO} operations.
 */
@RestController
@RequestMapping("/api/client/sets")
public class ClientSetResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientSetResource.class);

    private static final String ENTITY_NAME = "clientSet";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final ClientSetService clientSetService;

    public ClientSetResource(ClientSetService clientSetService) {
        this.clientSetService = clientSetService;
    }

    /**
     * {@code GET /api/client/sets/:id} : get a set by id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientSetDTO> getById(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClientSet : {}", id);
        return ResponseUtil.wrapOrNotFound(clientSetService.getById(id));
    }

    /**
     * {@code POST /api/client/sets} : create a new set.
     */
    @PostMapping("")
    public ResponseEntity<ClientSetDTO> create(@Valid @RequestBody ClientSetDTO dto) throws URISyntaxException {
        LOG.debug("REST request to save ClientSet : {}", dto);
        if (dto.getId() != null) {
            throw new BadRequestAlertException("A new clientSet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ClientSetDTO result = clientSetService.create(dto);
        return ResponseEntity.created(new URI("/api/client/sets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT /api/client/sets/:id} : update an existing set.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientSetDTO> update(@PathVariable("id") Long id, @Valid @RequestBody ClientSetDTO dto) {
        LOG.debug("REST request to update ClientSet : {}, {}", id, dto);
        if (dto.getId() != null && !id.equals(dto.getId())) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idmismatch");
        }
        ClientSetDTO result = clientSetService.update(id, dto);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code DELETE /api/client/sets/:id} : delete a set.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ClientSet : {}", id);
        clientSetService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
