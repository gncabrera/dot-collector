package com.nookx.api.client.dto;

import java.util.List;
import lombok.Data;

@Data
public class ClientSearchResponseDTO {

    private List<ClientSearchTabDTO> tabs;
}
