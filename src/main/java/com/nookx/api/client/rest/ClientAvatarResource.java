package com.nookx.api.client.rest;

import com.nookx.api.client.service.ClientAvatarService;
import com.nookx.api.service.dto.MegaAssetDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for client avatar (Dicebear) operations.
 */
@RestController
@RequestMapping("/api/client/avatar")
public class ClientAvatarResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientAvatarResource.class);

    private static final String ENTITY_NAME = "clientAvatar";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final ClientAvatarService clientAvatarService;

    public ClientAvatarResource(ClientAvatarService clientAvatarService) {
        this.clientAvatarService = clientAvatarService;
    }

    /**
     * {@code GET  /api/client/avatar/generate/{total}} : generate {@code total} random avatar seeds
     * (each one a UUID usable against {@code GET /api/client/avatar/{uuid}}).
     *
     * @param total the number of seeds to generate (must be in {@code [1, 19]}).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the seed list in body.
     */
    @GetMapping("/generate/{total}")
    public ResponseEntity<List<String>> generate(@PathVariable("total") int total) {
        LOG.debug("REST request to generate {} avatar seeds", total);
        return ResponseEntity.ok(clientAvatarService.generateSeeds(total));
    }

    /**
     * {@code GET  /api/client/avatar/{uuid}} : pass-through fetch of a Dicebear avatar JPEG by seed.
     * Nothing is persisted on the server filesystem.
     *
     * @param uuid the seed used against the Dicebear URL.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the JPEG bytes in body.
     */
    @GetMapping(value = "/{uuid}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getAvatar(@PathVariable("uuid") String uuid) {
        LOG.debug("REST request to pass-through Dicebear avatar : {}", uuid);
        byte[] bytes = clientAvatarService.fetchAvatarBytes(uuid);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(bytes);
    }

    /**
     * {@code PUT  /api/client/avatar/{uuid}} : download the Dicebear avatar for the given seed and
     * persist it as the current profile's image. Removes the previous avatar asset if one existed.
     *
     * @param uuid the seed used against the Dicebear URL.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the saved {@link MegaAssetDTO} in body.
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<MegaAssetDTO> saveAvatar(@PathVariable("uuid") String uuid) {
        LOG.debug("REST request to save Dicebear avatar to current profile : {}", uuid);
        MegaAssetDTO dto = clientAvatarService.saveAvatarForCurrentProfile(uuid);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, dto.getUuid().toString()))
            .body(dto);
    }
}
