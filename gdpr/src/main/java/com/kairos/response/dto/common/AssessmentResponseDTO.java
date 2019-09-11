package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.gdpr.AssessmentSchedulingFrequency;
import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class AssessmentResponseDTO {


    private Long id;
    private String name;
    private LocalDate endDate;
    private LocalDate completedDate;
    private String comment;
    private List<Staff> assigneeList;
    private Staff approver;
    private AssessmentStatus assessmentStatus;
    private AssetBasicResponseDTO asset;
    private ProcessingActivityBasicDTO processingActivity;
    private List<RiskBasicResponseDTO> risks;
    private LocalDate assessmentLaunchedDate;
    private AssessmentSchedulingFrequency assessmentSchedulingFrequency;
    private LocalDate startDate;

}
