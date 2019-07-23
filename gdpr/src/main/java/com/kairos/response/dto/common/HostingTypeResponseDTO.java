package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import lombok.*;

import java.time.LocalDate;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class HostingTypeResponseDTO {

    private Long id;

    private String name;

    private Long organizationId;

    private SuggestedDataStatus suggestedDataStatus;

    private LocalDate suggestedDate;

    public HostingTypeResponseDTO(Long id, String name, Long organizationId, SuggestedDataStatus suggestedDataStatus, LocalDate suggestedDate) {
        this.id = id;
        this.name = name;
        this.organizationId = organizationId;
        this.suggestedDataStatus = suggestedDataStatus;
        this.suggestedDate = suggestedDate;
    }
}
