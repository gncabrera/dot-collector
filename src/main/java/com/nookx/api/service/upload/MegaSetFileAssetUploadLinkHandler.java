package com.nookx.api.service.upload;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.MegaSet;
import com.nookx.api.domain.MegaSetFile;
import com.nookx.api.domain.enumeration.AssetType;
import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.repository.MegaSetFileRepository;
import com.nookx.api.repository.MegaSetRepository;
import com.nookx.api.security.SecurityUtils;
import com.nookx.api.service.MegaSetService;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MegaSetFileAssetUploadLinkHandler implements AssetUploadLinkHandler {

    private static final String ENTITY_NAME = "megaSet";

    private final MegaSetRepository megaSetRepository;
    private final MegaSetFileRepository megaSetFileRepository;
    private final MegaSetService megaSetService;

    public MegaSetFileAssetUploadLinkHandler(
        MegaSetRepository megaSetRepository,
        MegaSetFileRepository megaSetFileRepository,
        MegaSetService megaSetService
    ) {
        this.megaSetRepository = megaSetRepository;
        this.megaSetFileRepository = megaSetFileRepository;
        this.megaSetService = megaSetService;
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.SETS_FILE;
    }

    @Override
    public boolean assetTypeIsValid(AssetType assetType) {
        return assetType == AssetType.FILE;
    }

    @Override
    public boolean canUpload(Long entityId) {
        MegaSet megaSet = megaSetRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("MegaSet not found", ENTITY_NAME, "idnotfound"));
        return megaSetService.isOwner(megaSet);
    }

    @Override
    public void linkMegaAsset(MegaAsset megaAsset, Long entityId, AssetUploadLinkContext context) {
        MegaSet megaSet = megaSetRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("MegaSet not found", ENTITY_NAME, "idnotfound"));

        MegaSetFile row = new MegaSetFile();
        row.setMegaSet(megaSet);
        row.setAsset(megaAsset);
        megaSetFileRepository.save(row);
    }
}
