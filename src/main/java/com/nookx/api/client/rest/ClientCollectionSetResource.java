package com.nookx.api.client.rest;

import com.nookx.api.client.dto.ClientCollectionSetDTO;
import com.nookx.api.client.dto.ClientSetDTO;
import com.nookx.api.client.dto.ClientSetLiteDTO;
import com.nookx.api.client.service.ClientCollectionSetService;
import com.nookx.api.service.dto.ProfileCollectionSetDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that manages the sets contained in a client collection.
 */
@RestController
@RequestMapping("/api/client/collections/{collectionId}/sets")
public class ClientCollectionSetResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientCollectionSetResource.class);

    private final ClientCollectionSetService clientCollectionSetService;

    public ClientCollectionSetResource(ClientCollectionSetService clientCollectionSetService) {
        this.clientCollectionSetService = clientCollectionSetService;
    }

    /**
     * {@code PUT /api/client/collections/:collectionId/sets} : add the requested
     * sets to the collection.
     *
     * <p>The body is a list of {@link ClientCollectionSetDTO} carrying the
     * {@code setId} together with the {@code owned} flag and the sell-listing
     * fields ({@code userNotes}, {@code price}, {@code quantityToSell},
     * {@code status}) to persist for that link. Sets already present in the
     * collection are silently skipped (the operation is idempotent).</p>
     */
    @PutMapping("")
    public ResponseEntity<Void> addSetsToCollection(
        @PathVariable("collectionId") Long collectionId,
        @RequestBody List<ClientCollectionSetDTO> sets
    ) {
        LOG.debug("REST request to add sets to ClientCollection {} : {}", collectionId, sets);
        clientCollectionSetService.addSetsToCollection(collectionId, sets);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code DELETE /api/client/collections/:collectionId/sets/:setId} : remove the
     * link between the given collection and the given {@link com.nookx.api.domain.MegaSet}.
     */
    @DeleteMapping("/{setId}")
    public ResponseEntity<Void> removeSetFromCollection(
        @PathVariable("collectionId") Long collectionId,
        @PathVariable("setId") Long setId
    ) {
        LOG.debug("REST request to remove set {} from ClientCollection : {}", setId, collectionId);
        clientCollectionSetService.removeSetFromCollection(collectionId, setId);
        return ResponseEntity.noContent().build();
    }

    /**
     * {@code PUT /api/client/collections/:collectionId/sets/:id/details} : update the
     * editable details (owned, userNotes, price, quantityToSell, status) of the
     * {@link com.nookx.api.domain.ProfileCollectionSet} that links the given
     * {@link com.nookx.api.domain.MegaSet} ({@code id}) to the collection.
     *
     * <p>PUT semantics: {@code null} values in the body overwrite existing values.
     * The {@code id}, {@code dateAdded}, {@code collection} and {@code set} fields
     * of the body are ignored.</p>
     */
    @PutMapping("/{id}/details")
    public ResponseEntity<ProfileCollectionSetDTO> updateSetDetails(
        @PathVariable("collectionId") Long collectionId,
        @PathVariable("id") Long id,
        @RequestBody ProfileCollectionSetDTO dto
    ) {
        LOG.debug("REST request to update details of set {} in ClientCollection {} : {}", id, collectionId, dto);
        ProfileCollectionSetDTO result = clientCollectionSetService.updateSetDetails(collectionId, id, dto);
        return ResponseEntity.ok(result);
    }

    /**
     * {@code GET /api/client/collections/:collectionId/sets} : get every set
     * (fully enriched) that is part of the collection.
     */
    @GetMapping("")
    public List<ClientSetLiteDTO> getCollectionSets(@PathVariable("collectionId") Long collectionId) {
        LOG.debug("REST request to get sets of ClientCollection : {}", collectionId);
        return clientCollectionSetService.getSetsByCollection(collectionId);
    }
}
