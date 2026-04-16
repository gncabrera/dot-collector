package com.nookx.api.service.upload;

import java.io.Serializable;

/**
 * Optional metadata for linking an uploaded asset (used by MegaPart / MegaSet images).
 */
public record AssetUploadLinkContext(Integer sortOrder, String label, Boolean isPrimary) implements Serializable {}
