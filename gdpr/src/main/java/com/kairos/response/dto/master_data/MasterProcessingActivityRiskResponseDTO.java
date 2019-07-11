package com.kairos.response.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.response.dto.common.RiskBasicResponseDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class MasterProcessingActivityRiskResponseDTO {


    private Long id;
    private String name;
    private Boolean mainParent;
    private List<RiskBasicResponseDTO> risks=new ArrayList<>();
    private LocalDate suggestedDate;
    private SuggestedDataStatus suggestedDataStatus;

    private List<MasterProcessingActivityRiskResponseDTO>  processingActivities=new ArrayList<>();

    public MasterProcessingActivityRiskResponseDTO(Long id, String name, boolean mainParent, List<RiskBasicResponseDTO> risks, LocalDate suggestedDate, SuggestedDataStatus suggestedDataStatus) {
        this.id = id;
        this.name = name;
        this.mainParent = mainParent;
        this.risks=risks;
       this.suggestedDate= suggestedDate;
       this.suggestedDataStatus=suggestedDataStatus;
    }
}
