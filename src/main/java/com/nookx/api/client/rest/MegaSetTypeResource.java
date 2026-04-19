package com.nookx.api.client.rest;

import com.nookx.api.domain.MegaSetType;
import com.nookx.api.repository.MegaSetTypeRepository;
import com.nookx.api.service.MegaSetTypeService;
import com.nookx.api.service.dto.MegaSetTypeDTO;
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
 * REST controller for managing {@link MegaSetType}.
 */
@RestController
@RequestMapping("/api/mega-set-types")
public class MegaSetTypeResource {

    private static final Logger LOG = LoggerFactory.getLogger(MegaSetTypeResource.class);

    private static final String ENTITY_NAME = "megaSetType";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final MegaSetTypeService megaSetTypeService;

    private final MegaSetTypeRepository megaSetTypeRepository;

    public MegaSetTypeResource(MegaSetTypeService megaSetTypeService, MegaSetTypeRepository megaSetTypeRepository) {
        this.megaSetTypeService = megaSetTypeService;
        this.megaSetTypeRepository = megaSetTypeRepository;
    }

    @PostMapping("")
    public ResponseEntity<MegaSetTypeDTO> createMegaSetType(@Valid @RequestBody MegaSetTypeDTO megaSetTypeDTO) throws URISyntaxException {
        LOG.debug("REST request to save MegaSetType : {}", megaSetTypeDTO);
        if (megaSetTypeDTO.getId() != null) {
            throw new BadRequestAlertException("A new megaSetType cannot already have an ID", ENTITY_NAME, "idexists");
        }
        megaSetTypeDTO = megaSetTypeService.save(megaSetTypeDTO);
        return ResponseEntity.created(new URI("/api/mega-set-types/" + megaSetTypeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, megaSetTypeDTO.getId().toString()))
            .body(megaSetTypeDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MegaSetTypeDTO> updateMegaSetType(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MegaSetTypeDTO megaSetTypeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MegaSetType : {}, {}", id, megaSetTypeDTO);
        if (megaSetTypeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, megaSetTypeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!megaSetTypeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        megaSetTypeDTO = megaSetTypeService.update(megaSetTypeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, megaSetTypeDTO.getId().toString()))
            .body(megaSetTypeDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MegaSetTypeDTO> getMegaSetType(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MegaSetType : {}", id);
        Optional<MegaSetTypeDTO> megaSetTypeDTO = megaSetTypeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(megaSetTypeDTO);
    }

    /**
     * {@code DELETE  /mega-set-types/:id} : delete the "id" megaSetType.
     *
     * @param id the id of the megaSetTypeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMegaSetType(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MegaSetType : {}", id);
        megaSetTypeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
