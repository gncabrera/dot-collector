package com.nookx.api.service.upload;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.Report;
import com.nookx.api.domain.ReportImage;
import com.nookx.api.domain.User;
import com.nookx.api.domain.enumeration.AssetType;
import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.repository.ReportImageRepository;
import com.nookx.api.repository.ReportRepository;
import com.nookx.api.service.UserService;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.util.Objects;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class ReportAssetUploadLinkHandler implements AssetUploadLinkHandler {

    private static final String ENTITY_NAME = "report";

    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;
    private final UserService userService;

    public ReportAssetUploadLinkHandler(
        ReportRepository reportRepository,
        ReportImageRepository reportImageRepository,
        UserService userService
    ) {
        this.reportRepository = reportRepository;
        this.reportImageRepository = reportImageRepository;
        this.userService = userService;
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.REPORT;
    }

    @Override
    public boolean assetTypeIsValid(AssetType assetType) {
        return assetType == AssetType.IMAGE;
    }

    @Override
    public boolean canUpload(Long entityId) {
        Report report = reportRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("Report not found", ENTITY_NAME, "idnotfound"));
        User currentUser = userService.getUserWithAuthorities().orElseThrow(() -> new AccessDeniedException("Not authenticated"));
        if (report.getOwner() == null) {
            return false;
        }
        return Objects.equals(report.getOwner().getId(), currentUser.getId());
    }

    @Override
    public void linkMegaAsset(MegaAsset megaAsset, Long entityId, AssetUploadLinkContext context) {
        Report report = reportRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("Report not found", ENTITY_NAME, "idnotfound"));

        ReportImage row = new ReportImage();
        row.setReport(report);
        row.setAsset(megaAsset);
        reportImageRepository.save(row);
    }
}
