package com.nookx.api.client.dto;

import java.util.List;
import lombok.Data;

@Data
public class ClientProfileDTO {

    private Long id;
    private String username;
    private ClientImageDTO image;
    private String location;
    private Boolean publicProfile;
    private ClientCollectionsSummaryDTO collectionsSummary;
    private ClientContactLinksDTO contactLinks;
    private ClientProfileCommunityDTO communityDTO;
    private List<ClientInterestDTO> interests;
}
