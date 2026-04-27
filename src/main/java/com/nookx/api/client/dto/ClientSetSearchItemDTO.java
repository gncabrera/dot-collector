package com.nookx.api.client.dto;

import lombok.Data;

@Data
public class ClientSetSearchItemDTO {

    private Long id;
    private String setNumber;
    private String name;
    private boolean publicItem;
    private ClientImageDTO image;
}
