package com.nookx.api.client.dto;

import lombok.Data;

/**
 * DTO used to add a {@link com.nookx.api.domain.MegaSet} to a
 * {@link com.nookx.api.domain.ProfileCollection} via {@code ProfileCollectionSet}.
 */
@Data
public class ClientCollectionSetDTO {

    private Long setId;
    private Boolean owned;
    private Boolean wanted;
}
