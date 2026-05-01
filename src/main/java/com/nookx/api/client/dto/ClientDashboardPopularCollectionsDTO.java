package com.nookx.api.client.dto;

import java.util.List;
import lombok.Data;

/**
 * Response payload for the dashboard popular-collections feed: a page of
 * {@link ClientPopularCollectionDTO} plus an opaque cursor token used to fetch the
 * next page (null when no more pages exist).
 */
@Data
public class ClientDashboardPopularCollectionsDTO {

    private List<ClientPopularCollectionDTO> items;
    private String nextCursor;
}
