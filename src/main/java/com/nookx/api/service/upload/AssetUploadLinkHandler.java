package com.nookx.api.service.upload;

import com.nookx.api.domain.MegaAsset;
import com.nookx.api.domain.enumeration.AttachmentType;

/**
 * Links a persisted {@link MegaAsset} to a domain entity after upload.
 * Authorization for the given {@code entityId} must be enforced in {@link #assertCanUpload(Long)} (before the file is stored).
 */
public interface AssetUploadLinkHandler {
    AttachmentType getAttachmentType();

    /**
     * Validates that the target entity exists and the current user may attach an asset to it.
     * Invoked before the file is written and before the {@link MegaAsset} row is created.
     */
    void assertCanUpload(Long entityId);

    /**
     * Persists the link between the saved asset and the target entity.
     */
    void linkMegaAsset(MegaAsset megaAsset, Long entityId, AssetUploadLinkContext context);
}
