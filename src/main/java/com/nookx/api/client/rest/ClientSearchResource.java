package com.nookx.api.client.rest;

import com.nookx.api.client.dto.ClientSearchResponseDTO;
import com.nookx.api.client.service.ClientSearchService;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/client/search")
public class ClientSearchResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientSearchResource.class);
    private static final String ENTITY_NAME = "clientSearch";
    private static final String TAB_SETS = "SETS";
    private static final int MAX_LIMIT = 50;

    private final ClientSearchService clientSearchService;

    public ClientSearchResource(ClientSearchService clientSearchService) {
        this.clientSearchService = clientSearchService;
    }

    @GetMapping("")
    public ResponseEntity<ClientSearchResponseDTO> search(
        @RequestParam("q") String query,
        @RequestParam(value = "tab", required = false) String tab,
        @RequestParam(value = "limit", required = false, defaultValue = "20") int limit,
        @RequestParam(value = "cursor", required = false) String cursor
    ) {
        LOG.debug("REST request to search client resources query={}, tab={}, limit={}", query, tab, limit);

        if (query == null || query.isBlank()) {
            throw new BadRequestAlertException("Search query is required", ENTITY_NAME, "queryrequired");
        }
        if (tab != null && !tab.isBlank() && !TAB_SETS.equalsIgnoreCase(tab)) {
            throw new BadRequestAlertException("Unsupported search tab", ENTITY_NAME, "unsupportedtab");
        }
        int normalizedLimit = normalizeLimit(limit);

        return ResponseEntity.ok(clientSearchService.searchSets(query, normalizedLimit, cursor));
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            throw new BadRequestAlertException("Search limit must be positive", ENTITY_NAME, "invalidlimit");
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
