package com.nookx.api.web.rest;

import com.nookx.api.client.dto.ClientSetDTO;
import com.nookx.api.client.service.ClientSetService;
import com.nookx.api.config.ApplicationProperties;
import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.service.IngestIdempotencyService;
import com.nookx.api.service.MegaAssetService;
import com.nookx.api.service.dto.MegaAssetDTO;
import com.nookx.api.service.upload.AssetUploadLinkContext;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Internal ingest endpoints used by the external scraper integration.
 */
@RestController
@RequestMapping("/api/admin/ingest")
public class IngestResource {

    private static final Logger LOG = LoggerFactory.getLogger(IngestResource.class);

    private static final String ENTITY_NAME = "ingest";
    private static final String SETS_SCOPE = "POST:/api/admin/ingest/sets";
    private static final String ASSETS_SCOPE = "POST:/api/admin/ingest/assets";

    private final ClientSetService clientSetService;
    private final MegaAssetService megaAssetService;
    private final IngestIdempotencyService ingestIdempotencyService;
    private final ApplicationProperties applicationProperties;

    public IngestResource(
        ClientSetService clientSetService,
        MegaAssetService megaAssetService,
        IngestIdempotencyService ingestIdempotencyService,
        ApplicationProperties applicationProperties
    ) {
        this.clientSetService = clientSetService;
        this.megaAssetService = megaAssetService;
        this.ingestIdempotencyService = ingestIdempotencyService;
        this.applicationProperties = applicationProperties;
    }

    @PostMapping("/sets")
    public ResponseEntity<SetsBatchResponse> ingestSetsBatch(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @Valid @RequestBody SetsBatchRequest request
    ) {
        ensureIdempotencyKey(idempotencyKey);
        validateBatchSize("sets", request.sets().size());
        LOG.debug("REST request to ingest set batch, size {}", request.sets().size());

        IngestIdempotencyService.CachedResponse<SetsBatchResponse> response = ingestIdempotencyService.getOrCompute(
            SETS_SCOPE,
            idempotencyKey,
            () -> buildSetsBatchResponse(request)
        );

        return ResponseEntity.status(response.status()).body(response.body());
    }

    @PostMapping(value = "/assets", consumes = "multipart/form-data")
    public ResponseEntity<AssetsBatchResponse> ingestSetAssetsBatch(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestParam("setId") Long setId,
        @RequestParam("files") List<MultipartFile> files,
        @RequestParam(value = "description", required = false) String description,
        @RequestParam(value = "isPublic", required = false, defaultValue = "false") boolean isPublic,
        @RequestParam(value = "sortOrderStart", required = false) Integer sortOrderStart,
        @RequestParam(value = "label", required = false) String label,
        @RequestParam(value = "primaryIndex", required = false) Integer primaryIndex
    ) {
        ensureIdempotencyKey(idempotencyKey);
        if (files == null || files.isEmpty()) {
            throw new BadRequestAlertException("At least one file is required", ENTITY_NAME, "filerequired");
        }
        validateBatchSize("assets", files.size());
        LOG.debug("REST request to ingest asset batch, setId {}, size {}", setId, files.size());

        IngestIdempotencyService.CachedResponse<AssetsBatchResponse> response = ingestIdempotencyService.getOrCompute(
            ASSETS_SCOPE,
            idempotencyKey,
            () -> buildAssetsBatchResponse(setId, files, description, isPublic, sortOrderStart, label, primaryIndex)
        );

        return ResponseEntity.status(response.status()).body(response.body());
    }

    private IngestIdempotencyService.CachedResponse<SetsBatchResponse> buildSetsBatchResponse(SetsBatchRequest request) {
        List<SetBatchItemResult> results = new ArrayList<>();
        int created = 0;

        for (int index = 0; index < request.sets().size(); index++) {
            ClientSetDTO dto = request.sets().get(index);
            try {
                if (dto.getId() != null) {
                    throw new BadRequestAlertException("A new clientSet cannot already have an ID", ENTITY_NAME, "idexists");
                }
                ClientSetDTO createdSet = clientSetService.create(dto);
                created++;
                results.add(new SetBatchItemResult(index, HttpStatus.CREATED.value(), createdSet, null));
            } catch (RuntimeException ex) {
                results.add(new SetBatchItemResult(index, HttpStatus.BAD_REQUEST.value(), null, safeMessage(ex)));
            }
        }

        int failed = request.sets().size() - created;
        HttpStatus status = failed == 0 ? HttpStatus.CREATED : HttpStatus.MULTI_STATUS;
        return new IngestIdempotencyService.CachedResponse<>(
            status,
            new SetsBatchResponse(request.sets().size(), created, failed, List.copyOf(results))
        );
    }

    private IngestIdempotencyService.CachedResponse<AssetsBatchResponse> buildAssetsBatchResponse(
        Long setId,
        List<MultipartFile> files,
        String description,
        boolean isPublic,
        Integer sortOrderStart,
        String label,
        Integer primaryIndex
    ) {
        List<AssetBatchItemResult> results = new ArrayList<>();
        int uploaded = 0;

        for (int index = 0; index < files.size(); index++) {
            MultipartFile file = files.get(index);
            try {
                Integer sortOrder = sortOrderStart != null ? sortOrderStart + index : null;
                boolean isPrimary = primaryIndex != null && primaryIndex == index;
                AssetUploadLinkContext linkContext = new AssetUploadLinkContext(sortOrder, label, isPrimary);
                MegaAssetDTO uploadedAsset = megaAssetService.uploadAndLinkToEntity(
                    AttachmentType.SETS,
                    setId,
                    file,
                    description,
                    isPublic,
                    linkContext
                );
                uploaded++;
                results.add(new AssetBatchItemResult(index, file.getOriginalFilename(), HttpStatus.CREATED.value(), uploadedAsset, null));
            } catch (RuntimeException ex) {
                results.add(
                    new AssetBatchItemResult(index, file.getOriginalFilename(), HttpStatus.BAD_REQUEST.value(), null, safeMessage(ex))
                );
            }
        }

        int failed = files.size() - uploaded;
        HttpStatus status = failed == 0 ? HttpStatus.CREATED : HttpStatus.MULTI_STATUS;
        return new IngestIdempotencyService.CachedResponse<>(
            status,
            new AssetsBatchResponse(files.size(), uploaded, failed, List.copyOf(results))
        );
    }

    private void validateBatchSize(String batchName, int size) {
        int maxBatchSize = applicationProperties.getScraper().getMaxBatchSize();
        if (size > maxBatchSize) {
            throw new BadRequestAlertException(
                "Batch size for " + batchName + " exceeds configured max " + maxBatchSize,
                ENTITY_NAME,
                "batchtoolarge"
            );
        }
    }

    private static String safeMessage(RuntimeException ex) {
        Throwable rootCause = ex;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }

        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            ex.printStackTrace(pw);
        }

        StringBuilder detail = new StringBuilder();
        detail.append("exception=").append(ex.getClass().getName());
        if (StringUtils.hasText(ex.getMessage())) {
            detail.append(", message=").append(ex.getMessage());
        }
        if (rootCause != ex) {
            detail.append(", rootCause=").append(rootCause.getClass().getName());
            if (StringUtils.hasText(rootCause.getMessage())) {
                detail.append(", rootMessage=").append(rootCause.getMessage());
            }
        }
        detail.append(", stackTrace=").append(sw);
        return detail.toString();
    }

    private void ensureIdempotencyKey(String idempotencyKey) {
        if (!StringUtils.hasText(idempotencyKey)) {
            throw new BadRequestAlertException("Idempotency-Key header is required", ENTITY_NAME, "idempotencykeyrequired");
        }
    }

    public record SetsBatchRequest(@NotEmpty List<@Valid ClientSetDTO> sets) {}

    public record SetBatchItemResult(int index, int status, ClientSetDTO set, String error) {}

    public record SetsBatchResponse(int total, int created, int failed, List<SetBatchItemResult> results) {}

    public record AssetBatchItemResult(int index, String filename, int status, MegaAssetDTO asset, String error) {}

    public record AssetsBatchResponse(int total, int uploaded, int failed, List<AssetBatchItemResult> results) {}
}
