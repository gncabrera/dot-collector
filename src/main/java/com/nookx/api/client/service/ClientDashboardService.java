package com.nookx.api.client.service;

import com.nookx.api.client.cursor.CursorCodec;
import com.nookx.api.client.cursor.DashboardLatestReleasedCursorPosition;
import com.nookx.api.client.dto.ClientDashboardNewsSetsDTO;
import com.nookx.api.client.dto.ClientSetLiteDTO;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.Profile;
import com.nookx.api.repository.MegaSetRepository;
import com.nookx.api.repository.ProfileInterestRepository;
import com.nookx.api.repository.projection.MegaSetNewsHitProjection;
import com.nookx.api.service.ProfileService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Client-facing facade for the user dashboard. Every endpoint requires an authenticated
 * profile – callers that are not signed in receive {@link AccessDeniedException} (HTTP 403).
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ClientDashboardService {

    private static final String ENTITY_NAME = "clientDashboard";
    /** Sentinel used when the user has no subscribed interests, so the native {@code IN} clause stays valid. */
    private static final Long NO_INTEREST_SENTINEL = -1L;

    private final MegaSetRepository megaSetRepository;
    private final ProfileInterestRepository profileInterestRepository;
    private final ProfileService profileService;
    private final ClientSetService clientSetService;

    public ClientDashboardService(
        MegaSetRepository megaSetRepository,
        ProfileInterestRepository profileInterestRepository,
        ProfileService profileService,
        ClientSetService clientSetService
    ) {
        this.megaSetRepository = megaSetRepository;
        this.profileInterestRepository = profileInterestRepository;
        this.profileService = profileService;
        this.clientSetService = clientSetService;
    }

    /**
     * Returns the latest released public {@link MegaSet}s ordered by release date desc,
     * boosting sets whose interest the current profile is subscribed to so they surface
     * first within the same release window.
     *
     * @param limit       page size (caller-normalized; must be &gt; 0).
     * @param cursorToken opaque cursor from the previous page, or {@code null} for the first page.
     * @return page of {@link ClientSetLiteDTO} + next cursor (null when no more pages).
     * @throws AccessDeniedException when no authenticated profile is present.
     */
    public ClientDashboardNewsSetsDTO getNewsSets(int limit, String cursorToken) {
        Profile currentProfile = profileService.getCurrentProfile();
        if (currentProfile == null) {
            throw new AccessDeniedException("Not authenticated");
        }

        DashboardLatestReleasedCursorPosition cursor = CursorCodec.decodeCursor(cursorToken);
        List<Long> interestIds = profileInterestRepository.findInterestIdsByProfileId(currentProfile.getId());
        if (interestIds.isEmpty()) {
            interestIds = List.of(NO_INTEREST_SENTINEL);
        }

        List<MegaSetNewsHitProjection> hits = megaSetRepository.findDashboardNewsSets(
            interestIds,
            cursor.priority(),
            cursor.releaseDate(),
            cursor.id(),
            limit + 1
        );

        boolean hasMore = hits.size() > limit;
        List<MegaSetNewsHitProjection> pageHits = hasMore ? hits.subList(0, limit) : hits;

        List<Long> ids = pageHits.stream().map(MegaSetNewsHitProjection::getId).toList();
        Map<Long, MegaSet> megaSetById = new HashMap<>();
        for (MegaSet megaSet : megaSetRepository.findAllById(ids)) {
            megaSetById.put(megaSet.getId(), megaSet);
        }

        List<ClientSetLiteDTO> items = pageHits
            .stream()
            .map(hit -> clientSetService.toLiteDto(megaSetById.get(hit.getId())))
            .toList();

        ClientDashboardNewsSetsDTO response = new ClientDashboardNewsSetsDTO();
        response.setItems(items);
        response.setNextCursor(hasMore ? CursorCodec.encodeCursor(pageHits.get(pageHits.size() - 1)) : null);
        return response;
    }
}
