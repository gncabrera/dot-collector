package com.nookx.api.scraper.api.dto;

import com.nookx.api.scraper.domain.enumeration.SourceAssetKind;

/**
 * Source-neutral description of an asset (image/PDF/file) referenced by a set page.
 *
 * @param externalUrl absolute URL to download the binary from
 * @param kind        high-level kind, determines canonical linking
 * @param label       optional display label (e.g. "box front", "instructions")
 * @param sortOrder   optional ordering hint among sibling assets
 */
public record NormalizedAssetDto(String externalUrl, SourceAssetKind kind, String label, Integer sortOrder) {}
