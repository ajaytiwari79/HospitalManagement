package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.gdpr.AssessmentSchedulingFrequency;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentDTO {

    private BigInteger id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotNull(message = "error.message.due.date.not.Selected")
    private LocalDate endDate;
    private String comment;
    @NotNull(message = "error.message.assignee.not.selected")
    @Valid
    private  List<Staff> assigneeList;
    private boolean riskAssessment;
    private Staff approver;
    private LocalDate assessmentScheduledDate;
    private QuestionnaireTemplateType riskAssociatedEntity;
    @NotNull(message = "message.assessment.scheduling.frequency.not.Selected")
    private AssessmentSchedulingFrequency assessmentSchedulingFrequency;

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

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

    public LocalDate getAssessmentScheduledDate() { return assessmentScheduledDate; }

    public void setAssessmentScheduledDate(LocalDate assessmentScheduledDate) { this.assessmentScheduledDate = assessmentScheduledDate; }

    public AssessmentSchedulingFrequency getAssessmentSchedulingFrequency() { return assessmentSchedulingFrequency; }

    public void setAssessmentSchedulingFrequency(AssessmentSchedulingFrequency assessmentSchedulingFrequency) { this.assessmentSchedulingFrequency = assessmentSchedulingFrequency; }

    public boolean isRiskAssessment() { return riskAssessment; }

    public void setRiskAssessment(boolean riskAssessment) { this.riskAssessment = riskAssessment; }

    public QuestionnaireTemplateType getRiskAssociatedEntity() { return riskAssociatedEntity; }

    public void setRiskAssociatedEntity(QuestionnaireTemplateType riskAssociatedEntity) { this.riskAssociatedEntity = riskAssociatedEntity; }
}
