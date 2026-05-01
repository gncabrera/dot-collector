package com.nookx.api.client.dto;

import java.util.List;
import lombok.Data;

@Data
public class ClientProfileDTO {

    private String username;
    private ClientImageDTO image;
    private String location;
    private Boolean publicProfile;
    private ClientCollectionsSummaryDTO collectionsSummary;
    private ClientContactLinksDTO contactLinks;
    private List<ClientInterestDTO> interests;
}
