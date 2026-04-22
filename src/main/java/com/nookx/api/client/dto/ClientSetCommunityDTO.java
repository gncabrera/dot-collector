package com.nookx.api.client.dto;

import lombok.Data;

@Data
public class ClientSetCommunityDTO {

    private long totalOwned;
    private long totalWanted;
    private long totalCollectionsContaining;
}
