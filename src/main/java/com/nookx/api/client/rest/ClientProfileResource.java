package com.nookx.api.client.rest;

import com.nookx.api.client.dto.ClientBlockedProfileDTO;
import com.nookx.api.client.dto.ClientProfileDTO;
import com.nookx.api.client.dto.ClientProfileLiteDTO;
import com.nookx.api.client.service.ClientProfileService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     * {@code GET  /client/profile/:id} : get a profile by id, scoped to the current user.
     *
     * <p>If the target is not public and is not the current user, a limited view (username + image) is returned.
     * If the target is mutually blocked, {@code 404 (Not Found)} is returned.
     *
     * @param id Profile id.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the profile in body, or {@code 404}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientProfileDTO> getProfileById(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ClientProfile : {}", id);
        return ResponseEntity.ok(clientProfileService.getProfileById(id));
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

    /**
     * {@code PUT  /client/profile/:id/follow} : follow a profile on behalf of the current user.
     *
     * <p>Idempotent: returns {@code 204} even if already following.
     *
     * @param id id of the profile to follow.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @PutMapping("/{id}/follow")
    public ResponseEntity<Void> follow(@PathVariable("id") Long id) {
        LOG.debug("REST request to follow Profile : {}", id);
        clientProfileService.follow(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code DELETE  /client/profile/:id/follow} : unfollow a profile on behalf of the current user.
     *
     * <p>Idempotent: returns {@code 204} even if not following.
     *
     * @param id id of the profile to unfollow.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/{id}/follow")
    public ResponseEntity<Void> unfollow(@PathVariable("id") Long id) {
        LOG.debug("REST request to unfollow Profile : {}", id);
        clientProfileService.unfollow(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PUT  /client/profile/:id/block} : block a profile on behalf of the current user.
     *
     * <p>Idempotent: returns {@code 204} even if already blocked (reason is updated).
     * Auto-removes follow rows in both directions between the two profiles.
     *
     * @param id id of the profile to block.
     * @param dto block payload (optional reason).
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @PutMapping("/{id}/block")
    public ResponseEntity<Void> block(@PathVariable("id") Long id, @Valid @RequestBody(required = false) ClientBlockedProfileDTO dto) {
        LOG.debug("REST request to block Profile : {}", id);
        clientProfileService.block(id, dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code DELETE  /client/profile/:id/block} : unblock a profile on behalf of the current user.
     *
     * <p>Idempotent: returns {@code 204} even if not blocked.
     *
     * @param id id of the profile to unblock.
     * @return the {@link ResponseEntity} with status {@code 204 (No Content)}.
     */
    @DeleteMapping("/{id}/block")
    public ResponseEntity<Void> unblock(@PathVariable("id") Long id) {
        LOG.debug("REST request to unblock Profile : {}", id);
        clientProfileService.unblock(id);
        return ResponseEntity.noContent().build();
    }
}
