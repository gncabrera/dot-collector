package com.nookx.api.client.dto;

import java.util.List;
import lombok.Data;

@Data
public class ClientSearchTabDTO {

    private String type;
    private List<?> items;
    private String nextCursor;
}
