package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class DataDisposalResponseDTO {

    private Long id;

    private String name;

    private Long organizationId;

    private SuggestedDataStatus suggestedDataStatus;

    private LocalDate suggestedDate;

    public DataDisposalResponseDTO(Long id, String name, Long organizationId, SuggestedDataStatus suggestedDataStatus, LocalDate suggestedDate) {
        this.id = id;
        this.name = name;
        this.organizationId = organizationId;
        this.suggestedDataStatus = suggestedDataStatus;
        this.suggestedDate = suggestedDate;
    }
}
