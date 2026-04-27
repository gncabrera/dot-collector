package com.nookx.api.client.service;

import com.nookx.api.client.dto.ClientImageDTO;
import com.nookx.api.client.dto.ClientSearchResponseDTO;
import com.nookx.api.client.dto.ClientSearchTabDTO;
import com.nookx.api.client.dto.ClientSetSearchItemDTO;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.repository.MegaSetRepository;
import com.nookx.api.repository.projection.MegaSetSearchHitProjection;
import com.nookx.api.service.UserService;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ClientSearchService {

    private static final String ENTITY_NAME = "clientSearch";
    private static final String TAB_SETS = "SETS";

    private final MegaSetRepository megaSetRepository;
    private final ClientSetAssetService clientSetAssetService;
    private final UserService userService;

    public ClientSearchService(MegaSetRepository megaSetRepository, ClientSetAssetService clientSetAssetService, UserService userService) {
        this.megaSetRepository = megaSetRepository;
        this.clientSetAssetService = clientSetAssetService;
        this.userService = userService;
    }

    public ClientSearchResponseDTO searchSets(String query, int limit, String cursorToken) {
        String normalizedQuery = normalizeQuery(query);
        CursorPosition cursor = decodeCursor(cursorToken);
        Long currentUserId = userService
            .getUserWithAuthorities()
            .map(user -> user.getId())
            .orElse(null);
        List<MegaSetSearchHitProjection> hits = megaSetRepository.searchSetHits(
            normalizedQuery,
            currentUserId,
            cursor.score(),
            cursor.id(),
            limit + 1
        );

        boolean hasMore = hits.size() > limit;
        List<MegaSetSearchHitProjection> pageHits = hasMore ? hits.subList(0, limit) : hits;
        List<Long> ids = pageHits.stream().map(MegaSetSearchHitProjection::getId).toList();

        Map<Long, MegaSet> megaSetById = new HashMap<>();
        for (MegaSet megaSet : megaSetRepository.findAllById(ids)) {
            megaSetById.put(megaSet.getId(), megaSet);
        }

        List<ClientSetSearchItemDTO> items = pageHits
            .stream()
            .map(hit -> toSetSearchItem(megaSetById.get(hit.getId())))
            .toList();

        ClientSearchTabDTO tab = new ClientSearchTabDTO();
        tab.setType(TAB_SETS);
        tab.setItems(items);
        tab.setNextCursor(hasMore ? encodeCursor(pageHits.get(pageHits.size() - 1)) : null);

        ClientSearchResponseDTO response = new ClientSearchResponseDTO();
        response.setTabs(List.of(tab));
        return response;
    }

    private ClientSetSearchItemDTO toSetSearchItem(MegaSet megaSet) {
        if (megaSet == null) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        ClientSetSearchItemDTO item = new ClientSetSearchItemDTO();
        item.setId(megaSet.getId());
        item.setSetNumber(megaSet.getSetNumber());
        item.setName(megaSet.getName());
        item.setPublicItem(megaSet.isPublicItem());
        item.setImage(getPrimaryImage(megaSet.getId()));
        return item;
    }

    private ClientImageDTO getPrimaryImage(Long setId) {
        return clientSetAssetService.getImages(setId).stream().findFirst().orElse(null);
    }

    private String normalizeQuery(String query) {
        return query.trim();
    }

    private String encodeCursor(MegaSetSearchHitProjection hit) {
        String rawCursor = hit.getScore() + "|" + hit.getId();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawCursor.getBytes(StandardCharsets.UTF_8));
    }

    private CursorPosition decodeCursor(String token) {
        if (token == null || token.isBlank()) {
            return new CursorPosition(null, null);
        }
        try {
            String rawValue = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] cursorParts = rawValue.split("\\|");
            if (cursorParts.length != 2) {
                throw new BadRequestAlertException("Invalid cursor", ENTITY_NAME, "invalidcursor");
            }
            Float score = Float.parseFloat(cursorParts[0]);
            Long id = Long.parseLong(cursorParts[1]);
            return new CursorPosition(score, id);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestAlertException("Invalid cursor", ENTITY_NAME, "invalidcursor");
        }
    }

    private record CursorPosition(Float score, Long id) {}
}
