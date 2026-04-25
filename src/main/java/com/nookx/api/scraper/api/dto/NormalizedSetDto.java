package com.nookx.api.scraper.api.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.util.List;

/**
 * Source-neutral representation of a set, produced by a parser and consumed by a mapper.
 * <p>
 * Every field is optional except {@code sourceCode} and {@code sourceExternalId}. Source-specific
 * metadata that does not fit the common columns goes into {@code rawAttributes} and will be merged
 * into the canonical {@code MegaSet.attributes} JSON.
 */
public record NormalizedSetDto(
    String sourceCode,
    String sourceExternalId,
    String setNumber,
    String name,
    String description,
    LocalDate releaseDate,
    String theme,
    JsonNode rawAttributes,
    List<NormalizedAssetDto> assets
) {
    public NormalizedSetDto {
        if (sourceCode == null || sourceCode.isBlank()) {
            throw new IllegalArgumentException("sourceCode is required");
        }
        if (sourceExternalId == null || sourceExternalId.isBlank()) {
            throw new IllegalArgumentException("sourceExternalId is required");
        }
        if (assets == null) {
            assets = List.of();
        }
    }
}
