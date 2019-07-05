package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.DurationType;
import com.kairos.enums.gdpr.AssessmentSchedulingFrequency;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter @Setter @NoArgsConstructor
public class AssessmentDTO {

    private Long id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotNull(message = "error.message.due.date.not.Selected")
    private LocalDate endDate;
    private String comment;
    @NotNull(message = "error.message.assignee.not.selected")
    @Valid
    private  List<Staff> assigneeList;
    private boolean isRiskAssessment;
    private Staff approver;
    private LocalDate assessmentLaunchedDate;
    private QuestionnaireTemplateType riskAssociatedEntity;
    @NotNull(message = "message.assessment.scheduling.frequency.not.Selected")
    private AssessmentSchedulingFrequency assessmentSchedulingFrequency;
    @NotNull(message = "error.message.start.date.not.Selected")
    private LocalDate startDate;
    @Min(value = 1,message = "message.relativeDeadLine.value.invalid")
    private Integer relativeDeadlineDuration;
    @NotNull(message = "message.durationType.null")
    private DurationType relativeDeadlineType;

}
