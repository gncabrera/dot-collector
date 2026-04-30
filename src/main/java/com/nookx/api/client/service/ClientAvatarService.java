package com.nookx.api.client.service;

import com.nookx.api.config.ApplicationProperties;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.repository.ProfileImageRepository;
import com.nookx.api.service.MegaAssetService;
import com.nookx.api.service.ProfileService;
import com.nookx.api.service.dto.MegaAssetDTO;
import com.nookx.api.service.upload.AssetUploadLinkContext;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

/**
 * Avatar generation, fetching (Dicebear pass-through), and persisting via {@link MegaAssetService}.
 */
@Service
@Transactional
public class ClientAvatarService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientAvatarService.class);

    private static final String ENTITY_NAME = "clientAvatar";

    private static final int MAX_GENERATE = 20;

    private static final String AVATAR_CONTENT_TYPE = "image/jpeg";

    private static final String AVATAR_EXTENSION = ".jpg";

    private final MegaAssetService megaAssetService;
    private final ProfileService profileService;
    private final ProfileImageRepository profileImageRepository;
    private final ApplicationProperties applicationProperties;
    private final RestClient restClient;

    public ClientAvatarService(
        MegaAssetService megaAssetService,
        ProfileService profileService,
        ProfileImageRepository profileImageRepository,
        ApplicationProperties applicationProperties
    ) {
        this.megaAssetService = megaAssetService;
        this.profileService = profileService;
        this.profileImageRepository = profileImageRepository;
        this.applicationProperties = applicationProperties;
        this.restClient = RestClient.create();
    }

    /**
     * Generate {@code total} random seeds for Dicebear avatars. Nothing is stored.
     */
    @Transactional(readOnly = true)
    public List<String> generateSeeds(int total) {
        if (total <= 0 || total >= MAX_GENERATE) {
            throw new BadRequestAlertException("total must be between 1 and " + (MAX_GENERATE - 1), ENTITY_NAME, "invalidtotal");
        }
        return Stream.generate(() -> UUID.randomUUID().toString())
            .limit(total)
            .toList();
    }

    /**
     * Fetch the JPEG bytes of a Dicebear avatar for the given seed without persisting anything.
     */
    @Transactional(readOnly = true)
    public byte[] fetchAvatarBytes(String seed) {
        validateUuid(seed);
        String url = buildAvatarUrl(seed);
        LOG.debug("Fetching avatar from Dicebear: {}", url);
        byte[] body = restClient.get().uri(url).retrieve().body(byte[].class);
        if (body == null || body.length == 0) {
            throw new BadRequestAlertException("Empty avatar response from Dicebear", ENTITY_NAME, "fetchempty");
        }
        return body;
    }

    /**
     * Download the avatar from Dicebear and persist it as the profile image of the current user.
     * Removes the previous avatar asset (if any) once the new one is linked.
     */
    public MegaAssetDTO saveAvatarForCurrentProfile(String seed) {
        validateUuid(seed);
        Profile profile = profileService.getCurrentProfile();
        if (profile == null) {
            throw new BadRequestAlertException("Current profile not found", ENTITY_NAME, "profilenotfound");
        }

        String previousUuid = profileImageRepository
            .findByProfile_Id(profile.getId())
            .map(pi -> pi.getAsset() != null ? pi.getAsset().getUuid() : null)
            .orElse(null);

        byte[] bytes = fetchAvatarBytes(seed);
        MultipartFile file = new ByteArrayMultipartFile("file", seed + AVATAR_EXTENSION, AVATAR_CONTENT_TYPE, bytes);

        MegaAssetDTO dto = megaAssetService.uploadAndLinkToEntity(
            AttachmentType.PROFILE,
            profile.getId(),
            file,
            "Avatar",
            true,
            new AssetUploadLinkContext(null, null, null)
        );

        if (previousUuid != null && !Objects.equals(previousUuid, asString(dto.getUuid()))) {
            boolean removed = megaAssetService.deleteByUuid(previousUuid);
            if (!removed) {
                LOG.warn("Previous avatar asset {} could not be removed", previousUuid);
            }
        }

        return dto;
    }

    private String buildAvatarUrl(String seed) {
        ApplicationProperties.Dicebear dicebear = applicationProperties.getDicebear();
        return dicebear.getUrl() + "/" + dicebear.getType() + "/jpg?seed=" + seed;
    }

    private static void validateUuid(String raw) {
        if (raw == null) {
            throw new BadRequestAlertException("uuid is required", ENTITY_NAME, "uuidrequired");
        }
        try {
            UUID.fromString(raw.trim());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestAlertException("Invalid uuid: " + raw, ENTITY_NAME, "invaliduuid");
        }
    }

    private static String asString(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    /**
     * Minimal {@link MultipartFile} backed by an in-memory byte array, used to feed
     * {@link MegaAssetService#uploadAndLinkToEntity} from the Dicebear download.
     */
    private static final class ByteArrayMultipartFile implements MultipartFile {

        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        private ByteArrayMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content != null ? content : new byte[0];
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() {
            return content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
            try (OutputStream out = Files.newOutputStream(Path.of(dest.toURI()))) {
                out.write(content);
            }
        }
    }
}
