package com.nookx.api.client.dto;

import java.util.List;
import lombok.Data;

@Data
public class ClientProfileLiteDTO {

    private String name;
    private ClientImageDTO image;
    private List<ClientInterestDTO> interests;
}
