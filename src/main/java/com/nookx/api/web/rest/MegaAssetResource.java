package com.nookx.api.web.rest;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.repository.MegaAssetRepository;
import com.nookx.api.service.MegaAssetService;
import com.nookx.api.service.dto.MegaAssetDTO;
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
 * REST controller for managing {@link MegaAsset}.
 */
@RestController
@RequestMapping("/api/mega-assets")
public class MegaAssetResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaAssetResource.class);

    private static final String ENTITY_NAME = "megaAsset";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final MegaAssetService megaAssetService;

    private final MegaAssetRepository megaAssetRepository;

    public MegaAssetResource(MegaAssetService megaAssetService, MegaAssetRepository megaAssetRepository) {
        this.megaAssetService = megaAssetService;
        this.megaAssetRepository = megaAssetRepository;
    }

    @PostMapping("")
    public ResponseEntity<MegaAssetDTO> createMegaAsset(@Valid @RequestBody MegaAssetDTO megaAssetDTO) throws URISyntaxException {
        LOG.debug("REST request to save MegaAsset : {}", megaAssetDTO);
        if (megaAssetDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaAsset cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaAssetDTO = megaAssetService.save(megaAssetDTO);
        return ResponseEntity.created(new URI("/api/mega-assets/" + megaAssetDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaAssetDTO.getId().toString()))
            .body(megaAssetDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MegaAssetDTO> getMegaAsset(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaAsset : {}", id);
        Optional<MegaAssetDTO> megaAssetDTO = megaAssetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaAssetDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaAsset(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaAsset : {}", id);
        megaAssetService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
