package com.nookx.api.client.dto;

import com.nookx.api.domain.enumeration.ProfileCollectionSetStatus;
import lombok.Data;

/**
 * DTO used to add a {@link com.nookx.api.domain.MegaSet} to a
 * {@link com.nookx.api.domain.ProfileCollection} via {@code ProfileCollectionSet}.
 */
@Data
public class ClientCollectionSetDTO {

    private Long setId;
    private Boolean owned;
    private String userNotes;
    private Float price;
    private Integer quantityToSell;
    private ProfileCollectionSetStatus status;
}
