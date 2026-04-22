package com.nookx.api.client.dto;

import com.nookx.api.domain.enumeration.ProfileCollectionType;
import java.util.List;
import lombok.Data;

@Data
public class ClientCollectionUpdateDTO {

    private String title;
    private String description;
    private ProfileCollectionType collectionType;
    private ClientCollectionSettingsDTO settings;
    private List<ClientInterestDTO> interests;
}
