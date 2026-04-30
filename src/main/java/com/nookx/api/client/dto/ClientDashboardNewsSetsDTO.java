package com.nookx.api.client.dto;

import java.util.List;
import lombok.Data;

/**
 * Response payload for the dashboard news-sets feed: a page of {@link ClientSetLiteDTO}
 * plus an opaque cursor token used to fetch the next page (null when no more pages exist).
 */
@Data
public class ClientDashboardNewsSetsDTO {

    private List<ClientSetLiteDTO> items;
    private String nextCursor;
}
