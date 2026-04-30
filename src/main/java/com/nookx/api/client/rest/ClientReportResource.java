package com.nookx.api.client.rest;

import com.nookx.api.client.dto.ClientReportDTO;
import com.nookx.api.client.service.ClientReportService;
import com.nookx.api.domain.enumeration.ReportCategory;
import com.nookx.api.domain.enumeration.ReportType;
import com.nookx.api.security.AuthoritiesConstants;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;

/**
 * REST controller for client {@link ClientReportDTO} operations.
 */
@RestController
@RequestMapping("/api/client/report")
public class ClientReportResource {

    private static final Logger LOG = LoggerFactory.getLogger(ClientReportResource.class);

    private static final String ENTITY_NAME = "clientReport";

    @Value("${jhipster.clientApp.name:nookx}")
    private String applicationName;

    private final ClientReportService clientReportService;

    public ClientReportResource(ClientReportService clientReportService) {
        this.clientReportService = clientReportService;
    }

    /**
     * {@code POST /api/client/report/{reportType}} : create a new Report (with optional image attachments).
     *
     * @param reportType the {@link ReportType} this report belongs to (must match {@code category.reportType}).
     * @param category the {@link ReportCategory} for the report.
     * @param description the report description (required).
     * @param files optional image attachments (max 10).
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and the created {@link ClientReportDTO} in body.
     */
    @PostMapping(value = "/{reportType}", consumes = "multipart/form-data")
    public ResponseEntity<ClientReportDTO> createReport(
        @PathVariable("reportType") ReportType reportType,
        @RequestParam("category") ReportCategory category,
        @RequestParam("description") String description,
        @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        LOG.debug("REST request to create Report : type={}, category={}", reportType, category);
        ClientReportDTO created = clientReportService.create(reportType, category, description, files);
        return ResponseEntity.status(HttpStatus.CREATED)
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, String.valueOf(created.getId())))
            .body(created);
    }

    /**
     * {@code GET /api/client/report/{reportType}} : get all reports of {@code reportType}. Admin only.
     *
     * @param reportType the {@link ReportType} to filter by.
     * @param pageable pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list in body.
     */
    @GetMapping("/{reportType}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<ClientReportDTO>> getAllReports(
        @PathVariable("reportType") ReportType reportType,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get all Reports of type {}", reportType);
        Page<ClientReportDTO> page = clientReportService.findAllByType(reportType, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
}
