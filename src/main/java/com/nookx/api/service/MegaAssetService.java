package com.nookx.api.service;

import com.nookx.api.config.ApplicationProperties;
import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.User;
import com.nookx.api.domain.enumeration.AssetType;
import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.domain.enumeration.MegaAssetImageSize;
import com.nookx.api.repository.MegaAssetRepository;
import com.nookx.api.security.SecurityUtils;
import com.nookx.api.service.dto.MegaAssetDTO;
import com.nookx.api.service.mapper.MegaAssetMapper;
import com.nookx.api.service.upload.AssetUploadLinkContext;
import com.nookx.api.service.upload.AssetUploadLinkHandler;
import com.nookx.api.service.upload.AssetUploadLinkHandlerRegistry;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service Implementation for managing {@link MegaAsset}.
 */
@Service
@Transactional
public class MegaAssetService {

    private static final Logger LOG = LoggerFactory.getLogger(MegaAssetService.class);

    private static final String ENTITY_NAME = "megaAsset";

    private final MegaAssetRepository megaAssetRepository;

    private final MegaAssetMapper megaAssetMapper;

    private final ApplicationProperties applicationProperties;

    private final UserService userService;

    private final AssetUploadLinkHandlerRegistry assetUploadLinkHandlerRegistry;

    public MegaAssetService(
        MegaAssetRepository megaAssetRepository,
        MegaAssetMapper megaAssetMapper,
        ApplicationProperties applicationProperties,
        UserService userService,
        AssetUploadLinkHandlerRegistry assetUploadLinkHandlerRegistry
    ) {
        this.megaAssetRepository = megaAssetRepository;
        this.megaAssetMapper = megaAssetMapper;
        this.applicationProperties = applicationProperties;
        this.userService = userService;
        this.assetUploadLinkHandlerRegistry = assetUploadLinkHandlerRegistry;
    }

    public MegaAssetDTO upload(MultipartFile file, String description, boolean isPublic) {
        LOG.debug("Request to upload MegaAsset file");
        MegaAsset megaAsset = persistUploadedFile(file, description, isPublic);
        return megaAssetMapper.toDto(megaAsset);
    }

    /**
     * Persists a binary payload captured outside of an HTTP request (e.g. by the scraper).
     * <p>
     * The caller is responsible for deciding ownership: when invoked from a background job with no
     * authenticated principal, pass {@code null} (the {@code owner_id} column is nullable). Image
     * variants (thumb / medium) are generated the same way as for uploaded files.
     *
     * @param bytes            binary payload, must be non-empty
     * @param originalFilename original file name, used only to extract the extension
     * @param contentType      MIME type; drives {@link AssetType} and image variant generation
     * @param description      optional description persisted on the {@link MegaAsset}
     * @param isPublic         whether the asset should be publicly accessible via {@code /api/client/assets/file/*}
     * @param owner            explicit owner, or {@code null} for system-owned assets
     * @return the persisted {@link MegaAsset}
     */
    public MegaAsset storeFromBytes(
        byte[] bytes,
        String originalFilename,
        String contentType,
        String description,
        boolean isPublic,
        User owner
    ) {
        if (bytes == null || bytes.length == 0) {
            throw new BadRequestAlertException("Bytes payload is required", ENTITY_NAME, "filerequired");
        }

        String safeName = buildSafeName(originalFilename);
        String extensionPart = extractExtension(safeName);

        boolean isImage = getAssetType(contentType) == AssetType.IMAGE;
        UUID assetUuid = UUID.randomUUID();
        String storedFilename = isImage
            ? buildImageFilename(assetUuid, extensionPart, MegaAssetImageSize.ORIGINAL)
            : assetUuid + extensionPart;

        Path baseDir = getBaseDir();
        try {
            Files.createDirectories(baseDir);
            Path target = baseDir.resolve(storedFilename).normalize();
            if (!target.startsWith(baseDir)) {
                throw new BadRequestAlertException("Invalid file path", ENTITY_NAME, "invalidpath");
            }
            Files.write(target, bytes);
            if (isImage) {
                writeImageVariants(baseDir, assetUuid, extensionPart);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store binary payload", e);
        }

        String displayName = safeName.length() > 255 ? safeName.substring(0, 255) : safeName;
        AssetType assetType = getAssetType(contentType);

        MegaAsset megaAsset = new MegaAsset()
            .name(displayName)
            .description(description)
            .uuid(assetUuid.toString())
            .extension(extensionPart)
            .type(assetType)
            .contentType(contentType)
            .sizeBytes((long) bytes.length);
        megaAsset.setOwner(owner);
        megaAsset.setPublic(isPublic);
        return megaAssetRepository.save(megaAsset);
    }

    private static String buildSafeName(String originalFilename) {
        String cleaned = StringUtils.cleanPath(originalFilename != null ? originalFilename : "upload");
        String safe = Path.of(cleaned).getFileName().toString();
        return safe.isBlank() ? "upload" : safe;
    }

    private static String extractExtension(String safeName) {
        int dot = safeName.lastIndexOf('.');
        if (dot < 0 || dot >= safeName.length() - 1) {
            return "";
        }
        String ext = safeName.substring(dot);
        if (ext.length() > 32) {
            return "";
        }
        return ext.toLowerCase(Locale.ROOT);
    }

    /**
     * Stores the file, persists {@link MegaAsset}, and links it to the target entity in one transaction.
     * If linking fails after the file was written, the asset row and file on disk are removed.
     */
    public MegaAssetDTO uploadAndLinkToEntity(
        AttachmentType attachmentType,
        Long entityId,
        MultipartFile file,
        String description,
        boolean isPublic,
        AssetUploadLinkContext linkContext
    ) {
        LOG.debug("Request to upload MegaAsset and link to {} id {}", attachmentType, entityId);
        AssetUploadLinkHandler handler = assetUploadLinkHandlerRegistry.getHandler(attachmentType);
        if (!handler.canUpload(entityId)) {
            throw new AccessDeniedException("Access denied");
        }
        AssetType assetType = getAssetType(file.getContentType());
        if (!handler.assetTypeIsValid(assetType)) {
            throw new BadRequestAlertException("File type not allowed for this attachment", ENTITY_NAME, "invalidassettype");
        }

        MegaAsset megaAsset = null;
        try {
            megaAsset = persistUploadedFile(file, description, isPublic);
            handler.linkMegaAsset(megaAsset, entityId, linkContext);
            return megaAssetMapper.toDto(megaAsset);
        } catch (RuntimeException ex) {
            if (megaAsset != null && megaAsset.getId() != null) {
                megaAssetRepository.delete(megaAsset);
                deleteStoredFile(megaAsset);
            }
            throw ex;
        }
    }

    private MegaAsset persistUploadedFile(MultipartFile file, String description, boolean isPublic) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestAlertException("A file is required", ENTITY_NAME, "filerequired");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload");
        String safeName = Path.of(originalFilename).getFileName().toString();
        if (safeName.isBlank()) {
            safeName = "upload";
        }

        String extensionPart = "";
        int dot = safeName.lastIndexOf('.');
        if (dot >= 0 && dot < safeName.length() - 1) {
            extensionPart = safeName.substring(dot);
            if (extensionPart.length() > 32) {
                extensionPart = "";
            }
        }
        extensionPart = extensionPart.toLowerCase(Locale.ROOT);

        boolean isImage = getAssetType(file.getContentType()) == AssetType.IMAGE;
        UUID assetUuid = UUID.randomUUID();
        String storedFilename = assetUuid + extensionPart;

        if (isImage) {
            storedFilename = buildImageFilename(assetUuid, extensionPart, MegaAssetImageSize.ORIGINAL);
        }

        Path baseDir = getBaseDir();

        try {
            Files.createDirectories(baseDir);
            Path target = baseDir.resolve(storedFilename).normalize();
            if (!target.startsWith(baseDir)) {
                throw new BadRequestAlertException("Invalid file path", ENTITY_NAME, "invalidpath");
            }
            file.transferTo(target.toFile());
            if (isImage) {
                writeImageVariants(baseDir, assetUuid, extensionPart);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store uploaded file", e);
        }

        String displayName = safeName.length() > 255 ? safeName.substring(0, 255) : safeName;
        String contentType = file.getContentType(); //TODO: find content-type from file directly
        AssetType assetType = getAssetType(contentType);

        User owner = userService
            .getUserWithAuthorities()
            .orElseThrow(() -> new BadRequestAlertException("Current user could not be resolved", ENTITY_NAME, "usernotfound"));

        MegaAsset megaAsset = new MegaAsset()
            .name(displayName)
            .description(description)
            .uuid(assetUuid.toString())
            .extension(extensionPart)
            .type(assetType)
            .contentType(contentType)
            .sizeBytes(file.getSize());
        megaAsset.setOwner(owner);
        megaAsset.setPublic(isPublic);
        return megaAssetRepository.save(megaAsset);
    }

    private String buildImageFilename(UUID assetUuid, String extensionPart, MegaAssetImageSize size) {
        return buildImageFilename(assetUuid.toString(), extensionPart, size);
    }

    private String buildImageFilename(String assetUuid, String extensionPart, MegaAssetImageSize size) {
        return assetUuid + size.filenameSuffix() + extensionPart;
    }

    private AssetType getAssetType(String contentType) {
        return StringUtils.hasText(contentType) && contentType.toLowerCase().startsWith("image/") ? AssetType.IMAGE : AssetType.FILE;
    }

    /**
     * Resolves the stored file for download if the entity exists and the file is on disk.
     */
    @Transactional(readOnly = true)
    public Optional<MegaAssetFileDownload> findFileForDownload(String uuid) {
        LOG.debug("Request to get MegaAsset file stream : {}", uuid);
        Optional<MegaAsset> dtoOpt = parseAssetUuid(uuid).flatMap(megaAssetRepository::findByUuid);
        if (dtoOpt.isEmpty()) {
            return Optional.empty();
        }

        MegaAsset entity = dtoOpt.orElse(null);

        if (!canAccessMegaAsset(entity)) {
            return Optional.empty();
        }
        String storedName = storedObjectName(entity);
        if (!StringUtils.hasText(storedName)) {
            return Optional.empty();
        }
        Path baseDir = getBaseDir();
        Path filePath = baseDir.resolve(storedName).normalize();
        if (!filePath.startsWith(baseDir) || !Files.isRegularFile(filePath)) {
            LOG.warn("Asset id {} file not found at {}", uuid, filePath);
            return Optional.empty();
        }
        Resource resource = new FileSystemResource(filePath);
        return Optional.of(new MegaAssetFileDownload(megaAssetMapper.toDto(entity), resource));
    }

    /**
     * Resolves an image file (original, thumb, or medium) by asset UUID and variant.
     */
    @Transactional(readOnly = true)
    public Optional<MegaAssetFileDownload> findImageFileForDownload(String uuid, MegaAssetImageSize size) {
        LOG.debug("Request to get MegaAsset image stream : {} size {}", uuid, size);
        Optional<MegaAsset> dtoOpt = parseAssetUuid(uuid).flatMap(megaAssetRepository::findByUuid);
        if (dtoOpt.isEmpty()) {
            return Optional.empty();
        }

        MegaAsset entity = dtoOpt.orElse(null);

        if (!canAccessMegaAsset(entity)) {
            return Optional.empty();
        }
        if (entity.getType() != AssetType.IMAGE) {
            return Optional.empty();
        }
        String storedName = storedObjectName(entity);
        if (!StringUtils.hasText(storedName)) {
            return Optional.empty();
        }

        String relative = buildImageFilename(entity.getUuid(), entity.getExtension(), size);
        Path baseDir = getBaseDir();
        Path filePath = baseDir.resolve(relative).normalize();
        if (!filePath.startsWith(baseDir) || !Files.isRegularFile(filePath)) {
            LOG.warn("Asset {} size {} file not found at {}", uuid, size, filePath);
            return Optional.empty();
        }
        Resource resource = new FileSystemResource(filePath);
        return Optional.of(new MegaAssetFileDownload(megaAssetMapper.toDto(entity), resource));
    }

    private Path getBaseDir() {
        return Path.of(applicationProperties.getMegaAsset().getUploadDirectory()).toAbsolutePath().normalize();
    }

    /**
     * Deletes a mega asset by UUID (same value as in {@code /dl/{uuid}}).
     * Removes the row and the file on disk when permitted.
     *
     * @return true if deleted, false if not found or access denied
     */
    @Transactional
    public boolean deleteByUuid(String uuid) {
        LOG.debug("Request to delete MegaAsset : {}", uuid);
        Optional<MegaAsset> opt = parseAssetUuid(uuid).flatMap(megaAssetRepository::findByUuid);
        if (opt.isEmpty()) {
            return false;
        }
        MegaAsset entity = opt.orElse(null);
        if (!canAccessMegaAsset(entity)) {
            return false;
        }
        megaAssetRepository.delete(entity);
        deleteStoredFile(entity);
        return true;
    }

    private boolean canAccessMegaAsset(MegaAsset entity) {
        boolean isAdmin = SecurityUtils.currentUserIsAdmin();
        if (entity.isPublic() || isAdmin) {
            return true;
        }
        Optional<User> currentUser = userService.getUserWithAuthorities();
        if (currentUser.isEmpty()) {
            return false;
        }
        if (entity.getOwner() != null && !Objects.equals(entity.getOwner().getId(), currentUser.orElse(null).getId())) {
            return false;
        }
        return true;
    }

    private void deleteStoredFile(MegaAsset entity) {
        String storedName = storedObjectName(entity);
        if (!StringUtils.hasText(storedName)) {
            return;
        }
        boolean isImage = entity.getType() == AssetType.IMAGE;

        if (isImage) {
            storedName = buildImageFilename(entity.getUuid(), entity.getExtension(), MegaAssetImageSize.ORIGINAL);
        }
        Path baseDir = getBaseDir();
        deleteFileIfUnderBase(baseDir, storedName);
        if (isImage) {
            deleteFileIfUnderBase(baseDir, buildImageFilename(entity.getUuid(), entity.getExtension(), MegaAssetImageSize.THUMB));
            deleteFileIfUnderBase(baseDir, buildImageFilename(entity.getUuid(), entity.getExtension(), MegaAssetImageSize.MEDIUM));
        }
    }

    private static Optional<String> parseAssetUuid(String raw) {
        if (!StringUtils.hasText(raw)) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(raw.trim()).toString());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static String storedObjectName(MegaAsset entity) {
        if (entity.getUuid() == null) {
            return "";
        }
        String ext = entity.getExtension() != null ? entity.getExtension() : "";
        return entity.getUuid().toString() + ext;
    }

    private void deleteFileIfUnderBase(Path baseDir, String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            return;
        }
        Path filePath = baseDir.resolve(relativePath).normalize();
        if (!filePath.startsWith(baseDir)) {
            LOG.warn("Skipping delete outside base dir: {}", filePath);
            return;
        }
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            LOG.warn("Failed to delete file for asset path {}: {}", relativePath, e.getMessage());
        }
    }

    private void writeImageVariants(Path baseDir, UUID assetUuid, String extension) {
        String originalFilename = buildImageFilename(assetUuid, extension, MegaAssetImageSize.ORIGINAL);

        Path source = baseDir.resolve(originalFilename).normalize();
        if (!source.startsWith(baseDir) || !Files.isRegularFile(source)) {
            LOG.warn("Skipping variants, source missing: {}", source);
            return;
        }
        BufferedImage src;
        try {
            src = ImageIO.read(source.toFile());
        } catch (IOException e) {
            LOG.warn("Could not read image for variants {}: {}", originalFilename, e.getMessage());
            return;
        }
        if (src == null) {
            LOG.warn("ImageIO could not decode image for variants: {}", originalFilename);
            return;
        }
        try {
            writeScaledVariant(src, baseDir, assetUuid, extension, MegaAssetImageSize.THUMB);
        } catch (IOException e) {
            LOG.warn("Failed to write THUMB variant for {}: {}", originalFilename, e.getMessage());
        }
        try {
            writeScaledVariant(src, baseDir, assetUuid, extension, MegaAssetImageSize.MEDIUM);
        } catch (IOException e) {
            LOG.warn("Failed to write MEDIUM variant for {}: {}", originalFilename, e.getMessage());
        }
    }

    private void writeScaledVariant(BufferedImage src, Path baseDir, UUID assetUuid, String extension, MegaAssetImageSize variant)
        throws IOException {
        String variantFilename = buildImageFilename(assetUuid, extension, variant);
        String originalFilename = buildImageFilename(assetUuid, extension, MegaAssetImageSize.ORIGINAL);
        Path dest = baseDir.resolve(variantFilename).normalize();
        if (!dest.startsWith(baseDir)) {
            throw new IOException("Invalid variant path");
        }
        int w = src.getWidth();
        int h = src.getHeight();
        Dimension dim;
        if (variant == MegaAssetImageSize.THUMB) {
            dim = computeThumbDimensions(w, h, variant.getMaxWidth(), variant.getMaxHeight());
        } else if (variant == MegaAssetImageSize.MEDIUM) {
            dim = computeMediumDimensions(w, h, variant.getMaxWidth());
        } else {
            throw new IllegalArgumentException("variant must be THUMB or MEDIUM");
        }
        BufferedImage scaled = scaleImage(src, dim.width, dim.height);
        try {
            writeRasterImage(dest, scaled, originalFilename);
        } finally {
            scaled.flush();
        }
    }

    private static Dimension computeThumbDimensions(int w, int h, int maxW, int maxH) {
        double scale = Math.min((double) maxW / w, (double) maxH / h);
        if (scale > 1.0) {
            scale = 1.0;
        }
        int nw = Math.max(1, (int) Math.round(w * scale));
        int nh = Math.max(1, (int) Math.round(h * scale));
        return new Dimension(nw, nh);
    }

    private static Dimension computeMediumDimensions(int w, int h, int maxW) {
        if (w <= maxW) {
            return new Dimension(w, h);
        }
        int nw = maxW;
        int nh = Math.max(1, (int) Math.round(h * ((double) maxW / w)));
        return new Dimension(nw, nh);
    }

    private static BufferedImage scaleImage(BufferedImage src, int targetW, int targetH) {
        int type = src.getColorModel().hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage scaled = new BufferedImage(targetW, targetH, type);
        Graphics2D g = scaled.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(src, 0, 0, targetW, targetH, null);
        } finally {
            g.dispose();
        }
        return scaled;
    }

    private static void writeRasterImage(Path dest, BufferedImage image, String originalFilename) throws IOException {
        String ext = "";
        int dot = originalFilename.lastIndexOf('.');
        if (dot >= 0) {
            ext = originalFilename.substring(dot).toLowerCase(Locale.ROOT);
        }
        if (dest.getParent() != null) {
            Files.createDirectories(dest.getParent());
        }
        if (".jpg".equals(ext) || ".jpeg".equals(ext)) {
            BufferedImage rgb = image.getColorModel().hasAlpha() ? copyToRgbWhiteBackground(image) : image;
            writeJpeg(dest, rgb, 0.85f);
        } else {
            String format = formatNameForExtension(ext);
            if (!ImageIO.write(image, format, dest.toFile())) {
                throw new IOException("No writer for format " + format);
            }
        }
    }

    private static BufferedImage copyToRgbWhiteBackground(BufferedImage src) {
        BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = rgb.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, src.getWidth(), src.getHeight());
            g.drawImage(src, 0, 0, null);
        } finally {
            g.dispose();
        }
        return rgb;
    }

    private static void writeJpeg(Path dest, BufferedImage rgb, float quality) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            if (!ImageIO.write(rgb, "jpg", dest.toFile())) {
                throw new IOException("JPEG writer not available");
            }
            return;
        }
        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(dest.toFile())) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality);
            }
            writer.write(null, new IIOImage(rgb, null, null), param);
        } finally {
            writer.dispose();
        }
    }

    private static String formatNameForExtension(String extLower) {
        return switch (extLower) {
            case ".png" -> "png";
            case ".gif" -> "gif";
            case ".bmp" -> "bmp";
            default -> "png";
        };
    }

    public record MegaAssetFileDownload(MegaAssetDTO asset, Resource resource) {}
}
