package com.dot.collector.api.client.dto;

import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ClientCollectionLiteDTO {

    private Long id;
    private String title;
    private String description;
    private String collectionType;
    private List<String> sets;
    private String createdBy;
    private ClientCollectionCommunityDTO community;
}
