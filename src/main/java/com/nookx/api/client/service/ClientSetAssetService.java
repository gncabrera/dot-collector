package com.nookx.api.client.service;

import com.nookx.api.client.dto.ClientImageDTO;
import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.MegaSetFile;
import com.nookx.api.domain.MegaSetImage;
import com.nookx.api.repository.MegaSetFileRepository;
import com.nookx.api.repository.MegaSetImageRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads auxiliary assets (images and files) attached to a {@link com.nookx.api.domain.MegaSet}.
 *
 * <p>All URL building (image size variants and file download links) is delegated to the
 * shared {@link ClientAssetUrlService}.</p>
 */
@Service
@Transactional(readOnly = true)
public class ClientSetAssetService {

    private final MegaSetImageRepository megaSetImageRepository;
    private final MegaSetFileRepository megaSetFileRepository;
    private final ClientAssetUrlService clientAssetUrlService;

    public ClientSetAssetService(
        MegaSetImageRepository megaSetImageRepository,
        MegaSetFileRepository megaSetFileRepository,
        ClientAssetUrlService clientAssetUrlService
    ) {
        this.megaSetImageRepository = megaSetImageRepository;
        this.megaSetFileRepository = megaSetFileRepository;
        this.clientAssetUrlService = clientAssetUrlService;
    }

    public ClientImageDTO getPrimaryImage(Long setId) {
        List<MegaAsset> assets = megaSetImageRepository
            .findByMegaSet_IdOrderBySortOrderAsc(setId)
            .stream()
            .filter(MegaSetImage::isPrimary)
            .map(MegaSetImage::getAsset)
            .toList();
        return clientAssetUrlService.toClientImageDtos(assets).stream().findFirst().orElse(null);
    }

    public List<ClientImageDTO> getImages(Long setId) {
        List<MegaAsset> assets = megaSetImageRepository
            .findByMegaSet_IdOrderBySortOrderAsc(setId)
            .stream()
            .map(MegaSetImage::getAsset)
            .toList();
        return clientAssetUrlService.toClientImageDtos(assets);
    }

    public List<String> getFiles(Long setId) {
        List<MegaAsset> assets = megaSetFileRepository
            .findByMegaSet_IdOrderBySortOrderAsc(setId)
            .stream()
            .map(MegaSetFile::getAsset)
            .toList();
        return clientAssetUrlService.toFileUrls(assets);
    }
}
