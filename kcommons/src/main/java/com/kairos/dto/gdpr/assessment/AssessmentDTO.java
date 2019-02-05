package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.DurationType;
import com.kairos.enums.gdpr.AssessmentSchedulingFrequency;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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
    private int relativeDeadlineDuration;
    private DurationType relativeDeadlineType;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name.trim(); }

    public void setName(String name) { this.name = name; }

    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getComment() { return comment.trim(); }

    public void setComment(String comment) { this.comment = comment; }

    public List<Staff> getAssigneeList() { return assigneeList; }

    public void setAssigneeList(List<Staff> assigneeList) { this.assigneeList = assigneeList; }

    public Staff getApprover() { return approver; }

    public void setApprover(Staff approver) { this.approver = approver; }

    public LocalDate getAssessmentLaunchedDate() { return assessmentLaunchedDate; }

    public void setAssessmentLaunchedDate(LocalDate assessmentLaunchedDate) { this.assessmentLaunchedDate = assessmentLaunchedDate; }

    public AssessmentSchedulingFrequency getAssessmentSchedulingFrequency() { return assessmentSchedulingFrequency; }

    public void setAssessmentSchedulingFrequency(AssessmentSchedulingFrequency assessmentSchedulingFrequency) { this.assessmentSchedulingFrequency = assessmentSchedulingFrequency; }

    public boolean isRiskAssessment() { return isRiskAssessment; }

    public void setRiskAssessment(boolean riskAssessment) { this.isRiskAssessment = riskAssessment; }

    public QuestionnaireTemplateType getRiskAssociatedEntity() { return riskAssociatedEntity; }

    public void setRiskAssociatedEntity(QuestionnaireTemplateType riskAssociatedEntity) { this.riskAssociatedEntity = riskAssociatedEntity; }

    public LocalDate getStartDate() { return startDate; }

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public int getRelativeDeadlineDuration() {
        return relativeDeadlineDuration;
    }

    public void setRelativeDeadlineDuration(int relativeDeadlineDuration) {
        this.relativeDeadlineDuration = relativeDeadlineDuration;
    }

    public DurationType getRelativeDeadlineType() {
        return relativeDeadlineType;
    }

    public void setRelativeDeadlineType(DurationType relativeDeadlineType) {
        this.relativeDeadlineType = relativeDeadlineType;
    }

}
