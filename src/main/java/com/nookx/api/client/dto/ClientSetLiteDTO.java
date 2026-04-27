package com.nookx.api.client.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.nookx.api.service.dto.MegaSetTypeDTO;
import java.util.List;
import lombok.Data;

@Data
public class ClientSetLiteDTO {

    private Long id;
    private String setNumber;
    private String name;
    private ClientImageDTO image;
}
