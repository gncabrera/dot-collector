package com.nookx.api.client.dto;

import lombok.Data;

@Data
public class ClientCollectionCommunityDTO {

    private int totalStars;
    private int totalComments;
    private int totalCloned;
    private ClientCloneInformationDTO clone;
}
