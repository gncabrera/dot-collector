package com.nookx.api.service.upload;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.ProfileCollection;
import com.nookx.api.domain.ProfileCollectionImage;
import com.nookx.api.domain.User;
import com.nookx.api.domain.enumeration.AttachmentType;
import com.nookx.api.repository.ProfileCollectionImageRepository;
import com.nookx.api.repository.ProfileCollectionRepository;
import com.nookx.api.security.SecurityUtils;
import com.nookx.api.service.UserService;
import com.nookx.api.web.rest.errors.BadRequestAlertException;
import java.util.Objects;
import java.util.Optional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class ProfileCollectionAssetUploadLinkHandler implements AssetUploadLinkHandler {

    private static final String ENTITY_NAME = "profileCollection";

    private final ProfileCollectionRepository profileCollectionRepository;
    private final ProfileCollectionImageRepository profileCollectionImageRepository;
    private final UserService userService;

    public ProfileCollectionAssetUploadLinkHandler(
        ProfileCollectionRepository profileCollectionRepository,
        ProfileCollectionImageRepository profileCollectionImageRepository,
        UserService userService
    ) {
        this.profileCollectionRepository = profileCollectionRepository;
        this.profileCollectionImageRepository = profileCollectionImageRepository;
        this.userService = userService;
    }

    @Override
    public AttachmentType getAttachmentType() {
        return AttachmentType.COLLECTIONS;
    }

    @Override
    public void assertCanUpload(Long entityId) {
        ProfileCollection collection = profileCollectionRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("ProfileCollection not found", ENTITY_NAME, "idnotfound"));

        if (SecurityUtils.currentUserIsAdmin()) {
            return;
        }

        User currentUser = userService.getUserWithAuthorities().orElseThrow(() -> new AccessDeniedException("Not authenticated"));
        if (collection.getProfile() == null || collection.getProfile().getUser() == null) {
            throw new AccessDeniedException("ProfileCollection has no owning user");
        }
        if (!Objects.equals(collection.getProfile().getUser().getId(), currentUser.getId())) {
            throw new AccessDeniedException("Not allowed to modify this profile collection");
        }
    }

    @Override
    public void linkMegaAsset(MegaAsset megaAsset, Long entityId, AssetUploadLinkContext context) {
        ProfileCollection collection = profileCollectionRepository
            .findById(entityId)
            .orElseThrow(() -> new BadRequestAlertException("ProfileCollection not found", ENTITY_NAME, "idnotfound"));

        Optional<ProfileCollectionImage> byProfileCollectionId = profileCollectionImageRepository.findByProfileCollection_Id(entityId);

        if (byProfileCollectionId.isPresent()) {
            ProfileCollectionImage existing = byProfileCollectionId.get();
            existing.setAsset(megaAsset);
            profileCollectionImageRepository.save(existing);
        } else {
            ProfileCollectionImage row = new ProfileCollectionImage();
            row.setProfileCollection(collection);
            row.setAsset(megaAsset);
            profileCollectionImageRepository.save(row);
        }
    }
}
