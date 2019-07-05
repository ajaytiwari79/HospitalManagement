package com.kairos.response.dto.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.response.dto.common.RiskBasicResponseDTO;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class ProcessingActivityRiskResponseDTO {

    private Long id;
    private String name;
    private Boolean mainParent;
    private List<RiskBasicResponseDTO> risks;
    private List<ProcessingActivityRiskResponseDTO> processingActivities;

    public ProcessingActivityRiskResponseDTO(Long id, String name, Boolean mainParent, List<RiskBasicResponseDTO> risks) {
        this.id = id;
        this.name = name;
        this.mainParent = mainParent;
        this.risks = risks;
    }
}
