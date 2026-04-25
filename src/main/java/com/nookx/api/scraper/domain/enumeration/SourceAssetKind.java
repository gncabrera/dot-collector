package com.nookx.api.scraper.domain.enumeration;

/**
 * High-level kind of external asset referenced from a source page.
 * Drives which {@link com.nookx.api.domain.enumeration.AttachmentType} we use when
 * linking the downloaded asset to the canonical {@code MegaSet}.
 */
public enum SourceAssetKind {
    /** Product/box/in-use image. Mapped to {@code AttachmentType.SETS}. */
    IMAGE,
    /** Instructions manual in PDF. Mapped to {@code AttachmentType.SETS_FILE}. */
    INSTRUCTIONS_PDF,
    /** Any other downloadable file. Mapped to {@code AttachmentType.SETS_FILE}. */
    OTHER_FILE,
}
