package com.nookx.api.client.service;

import com.nookx.api.client.dto.ClientImageDTO;
import com.nookx.api.client.dto.ClientReportDTO;
import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileImage;
import com.nookx.api.domain.Report;
import com.nookx.api.domain.ReportImage;
import com.nookx.api.domain.User;
import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.domain.enumeration.ReportCategory;
import com.nookx.api.domain.enumeration.ReportType;
import com.nookx.api.repository.ProfileImageRepository;
import com.nookx.api.repository.ProfileRepository;
import com.nookx.api.repository.ReportImageRepository;
import com.nookx.api.repository.ReportRepository;
import com.nookx.api.service.MegaAssetService;
import com.nookx.api.service.UserService;
import com.nookx.api.service.upload.AssetUploadLinkContext;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
public class ClientReportService {

    private static final String ENTITY_NAME = "report";
    private static final int MAX_IMAGES = 10;

    private final ReportRepository reportRepository;
    private final ReportImageRepository reportImageRepository;
    private final MegaAssetService megaAssetService;
    private final UserService userService;
    private final ClientAssetUrlService clientAssetUrlService;
    private final ClientProfileService profileService;

    public ClientReportService(
        ReportRepository reportRepository,
        ReportImageRepository reportImageRepository,
        MegaAssetService megaAssetService,
        UserService userService,
        ClientAssetUrlService clientAssetUrlService,
        ClientProfileService profileService
    ) {
        this.reportRepository = reportRepository;
        this.reportImageRepository = reportImageRepository;
        this.megaAssetService = megaAssetService;
        this.userService = userService;
        this.clientAssetUrlService = clientAssetUrlService;
        this.profileService = profileService;
    }

    public ClientReportDTO create(ReportType reportType, ReportCategory category, String description, List<MultipartFile> files) {
        log.debug("Request to create Report: type={}, category={}", reportType, category);

        if (category == null) {
            throw new BadRequestAlertException("Category is required", ENTITY_NAME, "categoryrequired");
        }
        if (description == null || description.isBlank()) {
            throw new BadRequestAlertException("Description is required", ENTITY_NAME, "descriptionrequired");
        }
        if (category.getReportType() != reportType) {
            throw new BadRequestAlertException(
                "Category " + category.name() + " does not belong to reportType " + reportType,
                ENTITY_NAME,
                "categorytypemismatch"
            );
        }
        if (files != null && files.size() > MAX_IMAGES) {
            throw new BadRequestAlertException("Too many images (max " + MAX_IMAGES + ")", ENTITY_NAME, "toomanyimages");
        }

        User currentUser = userService.getUserWithAuthorities().orElseThrow(() -> new AccessDeniedException("Not authenticated"));

        Report report = new Report();
        report.setCategory(category);
        report.setDescription(description);
        report.setDate(Instant.now());
        report.setOwner(currentUser);
        report = reportRepository.save(report);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                megaAssetService.uploadAndLinkToEntity(
                    AttachmentType.REPORT,
                    report.getId(),
                    file,
                    "Report image",
                    false,
                    new AssetUploadLinkContext(null, null, null)
                );
            }
        }

        return toDto(report, reportImageRepository.findByReport_Id(report.getId()));
    }

    @Transactional(readOnly = true)
    public Page<ClientReportDTO> findAllByType(ReportType reportType, Pageable pageable) {
        log.debug("Request to get all Reports of type {}", reportType);
        List<ReportCategory> categories = categoriesOf(reportType);
        Page<Report> page = reportRepository.findByCategoryIn(categories, pageable);
        List<Report> reports = page.getContent();
        if (reports.isEmpty()) {
            return page.map(r -> toDto(r, List.of()));
        }

        List<Long> reportIds = reports.stream().map(Report::getId).toList();
        Map<Long, List<ReportImage>> imagesByReport = new HashMap<>();
        for (ReportImage ri : reportImageRepository.findByReport_IdIn(reportIds)) {
            imagesByReport.computeIfAbsent(ri.getReport().getId(), k -> new ArrayList<>()).add(ri);
        }

        return page.map(report -> toDto(report, imagesByReport.getOrDefault(report.getId(), List.of())));
    }

    private ClientReportDTO toDto(Report report, Collection<ReportImage> images) {
        ClientReportDTO dto = new ClientReportDTO();
        dto.setId(report.getId());
        dto.setCategory(report.getCategory());
        dto.setDescription(report.getDescription());
        dto.setDate(report.getDate());
        dto.setImages(clientAssetUrlService.toClientImageDtos(images.stream().map(ReportImage::getAsset).toList()));
        dto.setOwner(profileService.getProfileLite(report.getOwner(), false));
        return dto;
    }

    private static List<ReportCategory> categoriesOf(ReportType reportType) {
        return java.util.Arrays.stream(ReportCategory.values())
            .filter(c -> c.getReportType() == reportType)
            .toList();
    }
}
