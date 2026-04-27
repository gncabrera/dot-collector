package com.nookx.api.client.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.nookx.api.service.dto.MegaSetTypeDTO;
import java.util.List;
import lombok.Data;

@Data
public class ClientSetDTO {

    private Long id;
    private String setNumber;
    private String notes;
    private String name;
    private String description;
    private boolean publicItem;
    private JsonNode attributes;
    private MegaSetTypeDTO type;
    private ClientInterestDTO interest;
    private ClientSetCommunityDTO community;
    private List<ClientImageDTO> images;
    private List<String> files;
}
