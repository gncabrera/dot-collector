package com.nookx.api.service.upload;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.MegaSetImage;
import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.repository.MegaSetImageRepository;
import com.nookx.api.repository.MegaSetRepository;
import com.nookx.api.security.SecurityUtils;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class MegaSetAssetUploadLinkHandler implements AssetUploadLinkHandler {

    private static final String ENTITY_NAME = "megaSet";

    private final MegaSetRepository megaSetRepository;
    private final MegaSetImageRepository megaSetImageRepository;

    public MegaSetAssetUploadLinkHandler(MegaSetRepository megaSetRepository, MegaSetImageRepository megaSetImageRepository) {
        this.megaSetRepository = megaSetRepository;
        this.megaSetImageRepository = megaSetImageRepository;
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.SETS;
    }

    @Override
    public void assertCanUpload(Long entityId) {
        megaSetRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("MegaSet not found", ENTITY_NAME, "idnotfound"));
        if (!SecurityUtils.currentUserIsAdmin()) {
            throw new AccessDeniedException("Only administrators can attach images to MegaSet");
        }
    }

    @Override
    public void linkMegaAsset(MegaAsset megaAsset, Long entityId, AssetUploadLinkContext context) {
        MegaSet megaSet = megaSetRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("MegaSet not found", ENTITY_NAME, "idnotfound"));

        int sortOrder = resolveSortOrder(entityId, context != null ? context.sortOrder() : null);
        String label = context != null ? context.label() : null;
        boolean isPrimary = context != null && Boolean.TRUE.equals(context.isPrimary());

        if (isPrimary) {
            clearPrimaryForSet(entityId);
        }

        MegaSetImage row = new MegaSetImage();
        row.setMegaSet(megaSet);
        row.setAsset(megaAsset);
        row.setSortOrder(sortOrder);
        row.setLabel(label);
        row.setPrimary(isPrimary);
        megaSetImageRepository.save(row);
    }

    private void clearPrimaryForSet(Long setId) {
        List<MegaSetImage> images = megaSetImageRepository.findByMegaSet_IdOrderBySortOrderAsc(setId);
        for (MegaSetImage img : images) {
            if (img.isPrimary()) {
                img.setPrimary(false);
                megaSetImageRepository.save(img);
            }
        }
    }

    private int resolveSortOrder(Long setId, Integer requested) {
        if (requested != null) {
            return requested;
        }
        List<MegaSetImage> existing = megaSetImageRepository.findByMegaSet_IdOrderBySortOrderAsc(setId);
        return existing.stream().mapToInt(MegaSetImage::getSortOrder).max().orElse(-1) + 1;
    }
}
