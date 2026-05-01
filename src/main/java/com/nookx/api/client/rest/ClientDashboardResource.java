package com.nookx.api.client.rest;

import static com.nookx.api.client.cursor.CursorCodec.normalizeLimit;

import com.nookx.api.client.dto.ClientDashboardNewsSetsDTO;
import com.nookx.api.client.dto.ClientDashboardPopularCollectionsDTO;
import com.nookx.api.client.service.ClientDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for the authenticated client's dashboard feeds.
 */
@RestController
@RequestMapping("/api/client/dashboard")
public class ClientDashboardResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientDashboardResource.class);
    private static final String ENTITY_NAME = "clientDashboard";
    private static final int DEFAULT_LIMIT = 10;

    private final ClientDashboardService clientDashboardService;

    public ClientDashboardResource(ClientDashboardService clientDashboardService) {
        this.clientDashboardService = clientDashboardService;
    }

    /**
     * {@code GET /api/client/dashboard/news/sets} : latest released public sets for the dashboard,
     * boosting sets whose interest the current profile is subscribed to.
     *
     * @param limit  page size, capped at {@value #MAX_LIMIT} (defaults to {@value #DEFAULT_LIMIT}).
     * @param cursor opaque cursor returned by the previous call ({@code null} for the first page).
     */
    @GetMapping("/news/sets")
    public ResponseEntity<ClientDashboardNewsSetsDTO> getNewsSets(
        @RequestParam(value = "limit", required = false, defaultValue = "" + DEFAULT_LIMIT) int limit,
        @RequestParam(value = "cursor", required = false) String cursor
    ) {
        LOG.debug("REST request to get dashboard news sets limit={}, cursor={}", limit, cursor);
        int normalizedLimit = normalizeLimit(limit);
        return ResponseEntity.ok(clientDashboardService.getNewsSets(normalizedLimit, cursor));
    }

    /**
     * {@code GET /api/client/dashboard/collections/popular} : public collections ranked by a
     * weighted sum of recent vs older interactions (each star or clone counts {@code 3} when it
     * happened in the last two days, {@code 1} otherwise). Collections without any interaction
     * are excluded from the result.
     *
     * @param limit  page size, capped at the {@code normalizeLimit} cap (defaults to {@value #DEFAULT_LIMIT}).
     * @param cursor opaque cursor returned by the previous call ({@code null} for the first page).
     */
    @GetMapping("/collections/popular")
    public ResponseEntity<ClientDashboardPopularCollectionsDTO> getPopularCollections(
        @RequestParam(value = "limit", required = false, defaultValue = "" + DEFAULT_LIMIT) int limit,
        @RequestParam(value = "cursor", required = false) String cursor
    ) {
        LOG.debug("REST request to get dashboard popular collections limit={}, cursor={}", limit, cursor);
        int normalizedLimit = normalizeLimit(limit);
        return ResponseEntity.ok(clientDashboardService.getPopularCollections(normalizedLimit, cursor));
    }
}
