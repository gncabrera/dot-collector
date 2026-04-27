package com.nookx.api.client.service;

import com.nookx.api.client.dto.ClientImageDTO;
import com.nookx.api.config.ApplicationProperties;
import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.enumeration.MegaAssetImageSize;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Generic builder for {@link MegaAsset} client URLs.
 *
 * <p>Exposes the two shapes every client service needs:</p>
 * <ul>
 *   <li>{@link #toClientImageDto(MegaAsset)} — a {@link ClientImageDTO} with the three
 *       size-variant image URLs served by {@code GET /api/client/assets/image/{size}/{uuid}}.</li>
 *   <li>{@link #toFileUrl(MegaAsset)} — a single download URL served by
 *       {@code GET /api/client/assets/file/{uuid}}.</li>
 * </ul>
 */
@Service
public class ClientAssetUrlService {

    private static final String IMAGE_URL_TEMPLATE = "/api/client/assets/image/_SIZE_/";
    private static final String FILE_URL_TEMPLATE = "/api/client/assets/file/";

    private final ApplicationProperties applicationProperties;

    public ClientAssetUrlService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    // -----------------------------------------------------------------
    //  Images
    // -----------------------------------------------------------------

    /**
     * Build a {@link ClientImageDTO} with all size variants for the given asset,
     * or {@code null} if the asset (or its uuid) is not available.
     */
    public ClientImageDTO toClientImageDto(MegaAsset asset) {
        if (asset == null || asset.getUuid() == null) {
            return null;
        }
        String url = applicationProperties.getBaseUrl() + IMAGE_URL_TEMPLATE + asset.getUuid();
        ClientImageDTO dto = new ClientImageDTO();
        dto.setOriginal(url.replace("_SIZE_", MegaAssetImageSize.ORIGINAL.suffix()));
        dto.setThumb(url.replace("_SIZE_", MegaAssetImageSize.THUMB.suffix()));
        dto.setMedium(url.replace("_SIZE_", MegaAssetImageSize.MEDIUM.suffix()));
        return dto;
    }

    /** Map a list of assets to non-null {@link ClientImageDTO}s preserving the input order. */
    public List<ClientImageDTO> toClientImageDtos(List<MegaAsset> assets) {
        if (assets == null || assets.isEmpty()) {
            return List.of();
        }
        return assets.stream().map(this::toClientImageDto).filter(Objects::nonNull).toList();
    }

    // -----------------------------------------------------------------
    //  Files
    // -----------------------------------------------------------------

    /**
     * Build the download URL for a file asset, or {@code null} if the asset (or its uuid)
     * is not available.
     */
    public String toFileUrl(MegaAsset asset) {
        if (asset == null || asset.getUuid() == null) {
            return null;
        }
        return applicationProperties.getBaseUrl() + FILE_URL_TEMPLATE + asset.getUuid();
    }

    /** Map a list of assets to non-null download URLs preserving the input order. */
    public List<String> toFileUrls(List<MegaAsset> assets) {
        if (assets == null || assets.isEmpty()) {
            return List.of();
        }
        return assets.stream().map(this::toFileUrl).filter(Objects::nonNull).toList();
    }
}
