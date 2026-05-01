package com.nookx.api.client.dto;

import com.nookx.api.domain.enumeration.ProfileCollectionType;
import java.util.List;
import lombok.Data;

/**
 * Lightweight DTO for the dashboard "popular collections" feed: the basic collection
 * descriptors plus pre-rendered set thumbnails, the {@code owned/total} progress label
 * and the community counters used to rank the collection.
 */
@Data
public class ClientPopularCollectionDTO {

    private Long id;
    private String title;
    private String description;
    private ClientCollectionCommunityDTO community;
    /** Up to 5 thumb URLs of the most recently added sets in the collection (newest first). */
    private List<String> sets;
    private String createdBy;
    /** Progress label as {@code "<owned>/<total>"} sets in the collection. */
    private String items;
    /** Names of the interests linked to the collection. */
    private List<String> interests;
    private ProfileCollectionType collectionType;
}
