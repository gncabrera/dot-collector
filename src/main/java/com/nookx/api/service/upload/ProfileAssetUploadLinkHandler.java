package com.nookx.api.service.upload;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.Profile;
import com.nookx.api.domain.ProfileImage;
import com.nookx.api.domain.User;
import com.nookx.api.domain.enumeration.AssetType;
import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.repository.ProfileImageRepository;
import com.nookx.api.repository.ProfileRepository;
import com.nookx.api.security.SecurityUtils;
import com.nookx.api.service.UserService;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class ProfileAssetUploadLinkHandler implements AssetUploadLinkHandler {

    private static final String ENTITY_NAME = "profile";

    private final ProfileRepository profileRepository;
    private final ProfileImageRepository profileImageRepository;
    private final UserService userService;

    public ProfileAssetUploadLinkHandler(
        ProfileRepository profileRepository,
        ProfileImageRepository profileImageRepository,
        UserService userService
    ) {
        this.profileRepository = profileRepository;
        this.profileImageRepository = profileImageRepository;
        this.userService = userService;
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.PROFILE;
    }

    @Override
    public boolean assetTypeIsValid(AssetType assetType) {
        return assetType == AssetType.IMAGE;
    }

    @Override
    public boolean canUpload(Long entityId) {
        Profile profile = profileRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("Profile not found", ENTITY_NAME, "idnotfound"));

        User currentUser = userService.getUserWithAuthorities().orElseThrow(() -> new AccessDeniedException("Not authenticated"));
        if (profile.getUser() == null) {
            return false;
        }
        return Objects.equals(profile.getUser().getId(), currentUser.getId());
    }

    @Override
    public void linkMegaAsset(MegaAsset megaAsset, Long entityId, AssetUploadLinkContext context) {
        Profile profile = profileRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("Profile not found", ENTITY_NAME, "idnotfound"));

        Optional<ProfileImage> existing = profileImageRepository.findByProfile_Id(entityId);

        if (existing.isPresent()) {
            ProfileImage row = existing.orElse(null);
            row.setAsset(megaAsset);
            profileImageRepository.save(row);
        } else {
            ProfileImage row = new ProfileImage();
            row.setProfile(profile);
            row.setAsset(megaAsset);
            profileImageRepository.save(row);
        }
    }
}
