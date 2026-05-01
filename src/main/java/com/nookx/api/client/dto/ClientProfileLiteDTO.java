package com.nookx.api.client.dto;

import java.util.List;
import lombok.Data;

@Data
public class ClientProfileLiteDTO {

    private Long id;
    private String name;
    private ClientImageDTO image;
    private Boolean publicProfile;
    private List<ClientInterestDTO> interests;
}
