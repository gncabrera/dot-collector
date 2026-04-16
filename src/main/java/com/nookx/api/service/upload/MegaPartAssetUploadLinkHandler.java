package com.nookx.api.service.upload;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.MegaPart;
import com.nookx.api.domain.MegaPartImage;
import com.nookx.api.domain.enumeration.AssetType;
import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.repository.MegaPartImageRepository;
import com.nookx.api.repository.MegaPartRepository;
import com.nookx.api.security.SecurityUtils;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class MegaPartAssetUploadLinkHandler implements AssetUploadLinkHandler {

    private static final String ENTITY_NAME = "megaPart";

    private final MegaPartRepository megaPartRepository;
    private final MegaPartImageRepository megaPartImageRepository;

    public MegaPartAssetUploadLinkHandler(MegaPartRepository megaPartRepository, MegaPartImageRepository megaPartImageRepository) {
        this.megaPartRepository = megaPartRepository;
        this.megaPartImageRepository = megaPartImageRepository;
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.PARTS;
    }

    @Override
    public boolean assetTypeIsValid(AssetType assetType) {
        return assetType == AssetType.IMAGE;
    }

    @Override
    public boolean canUpload(Long entityId) {
        megaPartRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("MegaPart not found", ENTITY_NAME, "idnotfound"));
        return SecurityUtils.currentUserIsAdmin();
    }

    @Override
    public void linkMegaAsset(MegaAsset megaAsset, Long entityId, AssetUploadLinkContext context) {
        MegaPart part = megaPartRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("MegaPart not found", ENTITY_NAME, "idnotfound"));

        int sortOrder = resolveSortOrder(entityId, context != null ? context.sortOrder() : null);
        String label = context != null ? context.label() : null;
        boolean isPrimary = context != null && Boolean.TRUE.equals(context.isPrimary());

        if (isPrimary) {
            clearPrimaryForPart(entityId);
        }

        MegaPartImage row = new MegaPartImage();
        row.setPart(part);
        row.setAsset(megaAsset);
        row.setSortOrder(sortOrder);
        row.setLabel(label);
        row.setPrimary(isPrimary);
        megaPartImageRepository.save(row);
    }

    private void clearPrimaryForPart(Long partId) {
        List<MegaPartImage> images = megaPartImageRepository.findByPart_IdOrderBySortOrderAsc(partId);
        for (MegaPartImage img : images) {
            if (img.isPrimary()) {
                img.setPrimary(false);
                megaPartImageRepository.save(img);
            }
        }
    }

    private int resolveSortOrder(Long partId, Integer requested) {
        if (requested != null) {
            return requested;
        }
        List<MegaPartImage> existing = megaPartImageRepository.findByPart_IdOrderBySortOrderAsc(partId);
        return existing.stream().mapToInt(MegaPartImage::getSortOrder).max().orElse(-1) + 1;
    }
}
