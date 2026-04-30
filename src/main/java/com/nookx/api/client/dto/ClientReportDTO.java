package com.nookx.api.client.dto;

import com.nookx.api.domain.enumeration.ReportCategory;
import java.time.Instant;
import java.util.List;
import lombok.Data;

@Data
public class ClientReportDTO {

    private Long id;
    private ReportCategory category;
    private String description;
    private List<ClientImageDTO> images;
    private ClientProfileLiteDTO owner;
    private Instant date;
}
