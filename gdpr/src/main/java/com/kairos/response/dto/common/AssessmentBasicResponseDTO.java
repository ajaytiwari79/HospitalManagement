package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.gdpr.AssessmentSchedulingFrequency;
import com.kairos.enums.gdpr.AssessmentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class AssessmentBasicResponseDTO {

    private Long id;
    private String name;
    private LocalDate endDate;
    private LocalDate completedDate;
    private LocalDate startDate;
    private String comment;
    private List<Staff> assigneeList;
    private Staff approver;
    private AssessmentStatus assessmentStatus;
    private List<RiskBasicResponseDTO> risks=new ArrayList<>();
    private LocalDate assessmentLaunchedDate;
    private AssessmentSchedulingFrequency assessmentSchedulingFrequency;

    public AssessmentBasicResponseDTO(Long id, String name, LocalDate endDate, LocalDate completedDate, LocalDate startDate, String comment, AssessmentStatus assessmentStatus, LocalDate assessmentLaunchedDate, AssessmentSchedulingFrequency assessmentSchedulingFrequency) {
        this.id = id;
        this.name = name;
        this.endDate = endDate;
        this.completedDate = completedDate;
        this.startDate = startDate;
        this.comment = comment;
        this.assessmentStatus = assessmentStatus;
        this.assessmentLaunchedDate = assessmentLaunchedDate;
        this.assessmentSchedulingFrequency = assessmentSchedulingFrequency;
    }
}
