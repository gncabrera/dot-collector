package com.nookx.api.client.dto;

import com.nookx.api.service.dto.CurrencyDTO;
import lombok.Data;

@Data
public class ClientCollectionSettingsDTO {

    private boolean isPublic;
    private boolean showPrice;
    private boolean showCheckbox;
    private boolean showComment;
    private CurrencyDTO currency;
}
