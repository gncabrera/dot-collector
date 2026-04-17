package com.nookx.api.client.rest;

import com.nookx.api.client.dto.ClientProfileDTO;
import com.nookx.api.client.dto.ClientProfileLiteDTO;
import com.nookx.api.client.service.ClientProfileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for client {@link ClientProfileDTO} operations.
 */
@RestController
@RequestMapping("/api/client/profile")
public class ClientProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientProfileResource.class);

    private static final String ENTITY_NAME = "clientProfile";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final ClientProfileService clientProfileService;

    public ClientProfileResource(ClientProfileService clientProfileService) {
        this.clientProfileService = clientProfileService;
    }

    /**
     * {@code GET  /client/profile} : get the current user's profile.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the profile in body.
     */
    @GetMapping("")
    public ResponseEntity<ClientProfileDTO> getProfile() {
        LOG.debug("REST request to get current ClientProfile");
        return ResponseEntity.ok(clientProfileService.getProfile());
    }

    /**
     * {@code GET  /client/profile/lite} : get a lite view of the current user's profile.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the lite profile in body.
     */
    @GetMapping("/lite")
    public ResponseEntity<ClientProfileLiteDTO> getProfileLite() {
        LOG.debug("REST request to get current ClientProfileLite");
        return ResponseEntity.ok(clientProfileService.getProfileLite());
    }

    /**
     * {@code PUT  /client/profile} : update the current user's profile.
     * Interests, image and collections are not updated through this endpoint.
     *
     * @param clientProfileDTO the profile to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the updated profile in body.
     */
    @PutMapping("")
    public ResponseEntity<ClientProfileDTO> updateProfile(@Valid @RequestBody ClientProfileDTO clientProfileDTO) {
        LOG.debug("REST request to update current ClientProfile : {}", clientProfileDTO);
        ClientProfileDTO result = clientProfileService.update(clientProfileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, ENTITY_NAME))
            .body(result);
    }
}
