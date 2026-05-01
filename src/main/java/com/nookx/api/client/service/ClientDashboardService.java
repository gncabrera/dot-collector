package com.nookx.api.client.service;

import com.nookx.api.client.cursor.CursorCodec;
import com.nookx.api.client.cursor.DashboardLatestReleasedCursorPosition;
import com.nookx.api.client.cursor.DashboardPopularCollectionsCursorPosition;
import com.nookx.api.client.dto.ClientDashboardNewsSetsDTO;
import com.nookx.api.client.dto.ClientDashboardPopularCollectionsDTO;
import com.nookx.api.client.dto.ClientImageDTO;
import com.nookx.api.client.dto.ClientPopularCollectionDTO;
import com.nookx.api.client.dto.ClientSetLiteDTO;
import com.nookx.api.domain.Interest;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileCollection;
import com.nookx.api.repository.MegaSetRepository;
import com.nookx.api.repository.ProfileCollectionRepository;
import com.nookx.api.repository.ProfileCollectionSetRepository;
import com.nookx.api.repository.ProfileInterestRepository;
import com.nookx.api.repository.projection.MegaSetNewsHitProjection;
import com.nookx.api.repository.projection.PopularCollectionHitProjection;
import com.nookx.api.service.ProfileService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    /** How many set thumbnails to surface per popular collection. */
    private static final int POPULAR_COLLECTION_PREVIEW_SETS = 5;

    private final MegaSetRepository megaSetRepository;
    private final ProfileInterestRepository profileInterestRepository;
    private final ProfileService profileService;
    private final ClientSetService clientSetService;
    private final ProfileCollectionRepository profileCollectionRepository;
    private final ProfileCollectionSetRepository profileCollectionSetRepository;
    private final ClientCollectionService clientCollectionService;
    private final ClientSetAssetService clientSetAssetService;

    public ClientDashboardService(
        MegaSetRepository megaSetRepository,
        ProfileInterestRepository profileInterestRepository,
        ProfileService profileService,
        ClientSetService clientSetService,
        ProfileCollectionRepository profileCollectionRepository,
        ProfileCollectionSetRepository profileCollectionSetRepository,
        ClientCollectionService clientCollectionService,
        ClientSetAssetService clientSetAssetService
    ) {
        this.megaSetRepository = megaSetRepository;
        this.profileInterestRepository = profileInterestRepository;
        this.profileService = profileService;
        this.clientSetService = clientSetService;
        this.profileCollectionRepository = profileCollectionRepository;
        this.profileCollectionSetRepository = profileCollectionSetRepository;
        this.clientCollectionService = clientCollectionService;
        this.clientSetAssetService = clientSetAssetService;
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

        DashboardLatestReleasedCursorPosition cursor = CursorCodec.decode(cursorToken, DashboardLatestReleasedCursorPosition.PARSER);
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
        response.setNextCursor(
            hasMore
                ? CursorCodec.encode(
                      new DashboardLatestReleasedCursorPosition(
                          pageHits.getLast().getPriority(),
                          pageHits.getLast().getReleaseDate(),
                          pageHits.getLast().getId()
                      )
                  )
                : null
        );
        return response;
    }

    /**
     * Returns the most popular {@link ProfileCollection}s ranked by a weighted sum of recent vs
     * older interactions: each star or clone counts {@code 3} when it happened in the last two
     * days and {@code 1} otherwise. Collections without any interaction are excluded.
     *
     * @param limit       page size (caller-normalized; must be &gt; 0).
     * @param cursorToken opaque cursor from the previous page, or {@code null} for the first page.
     * @return page of {@link ClientPopularCollectionDTO} + next cursor (null when no more pages).
     * @throws AccessDeniedException when no authenticated profile is present.
     */
    public ClientDashboardPopularCollectionsDTO getPopularCollections(int limit, String cursorToken) {
        Profile currentProfile = profileService.getCurrentProfile();
        if (currentProfile == null) {
            throw new AccessDeniedException("Not authenticated");
        }

        DashboardPopularCollectionsCursorPosition cursor = CursorCodec.decode(
            cursorToken,
            DashboardPopularCollectionsCursorPosition.PARSER
        );

        List<PopularCollectionHitProjection> hits = profileCollectionRepository.findPopularCollections(
            cursor.score(),
            cursor.id(),
            limit + 1
        );

        boolean hasMore = hits.size() > limit;
        List<PopularCollectionHitProjection> pageHits = hasMore ? hits.subList(0, limit) : hits;

        List<Long> ids = pageHits.stream().map(PopularCollectionHitProjection::getId).toList();
        Map<Long, ProfileCollection> collectionById = new HashMap<>();
        for (ProfileCollection collection : profileCollectionRepository.findAllById(ids)) {
            collectionById.put(collection.getId(), collection);
        }

        List<ClientPopularCollectionDTO> items = pageHits
            .stream()
            .map(hit -> collectionById.get(hit.getId()))
            .filter(Objects::nonNull)
            .map(this::toPopularDto)
            .toList();

        ClientDashboardPopularCollectionsDTO response = new ClientDashboardPopularCollectionsDTO();
        response.setItems(items);
        response.setNextCursor(
            hasMore
                ? CursorCodec.encode(
                      new DashboardPopularCollectionsCursorPosition(pageHits.getLast().getScore(), pageHits.getLast().getId())
                  )
                : null
        );
        return response;
    }

    private ClientPopularCollectionDTO toPopularDto(ProfileCollection collection) {
        ClientPopularCollectionDTO dto = new ClientPopularCollectionDTO();
        dto.setId(collection.getId());
        dto.setTitle(collection.getTitle());
        dto.setDescription(collection.getDescription());
        dto.setCollectionType(collection.getType());

        if (collection.getProfile() != null) {
            Profile profile = collection.getProfile();
            if (profile.getUsername() != null) {
                dto.setCreatedBy(profile.getUsername());
            } else if (profile.getFullName() != null) {
                dto.setCreatedBy(profile.getFullName());
            }
        }

        dto.setCommunity(clientCollectionService.getClientCollectionCommunityDTO(collection));

        if (collection.getInterests() != null && !collection.getInterests().isEmpty()) {
            dto.setInterests(collection.getInterests().stream().map(Interest::getName).filter(Objects::nonNull).toList());
        } else {
            dto.setInterests(List.of());
        }

        Long collectionId = collection.getId();
        long ownedCount = collectionId != null ? profileCollectionSetRepository.countByCollection_IdAndOwnedTrue(collectionId) : 0L;
        long totalCount = collectionId != null ? profileCollectionSetRepository.countByCollection_Id(collectionId) : 0L;
        dto.setItems(ownedCount + "/" + totalCount);

        dto.setSets(collectionId != null ? recentSetThumbs(collectionId) : List.of());
        return dto;
    }

    private List<String> recentSetThumbs(Long collectionId) {
        Pageable topN = PageRequest.of(0, POPULAR_COLLECTION_PREVIEW_SETS);
        return profileCollectionSetRepository
            .findRecentSetIdsByCollectionId(collectionId, topN)
            .stream()
            .map(clientSetAssetService::getPrimaryImage)
            .filter(Objects::nonNull)
            .map(ClientImageDTO::getThumb)
            .filter(Objects::nonNull)
            .toList();
    }
}
