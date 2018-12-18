package com.kairos.persistence.model.data_inventory.assessment;


import com.kairos.enums.DurationType;
import com.kairos.enums.gdpr.AssessmentSchedulingFrequency;
import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.dto.gdpr.Staff;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Document
public class Assessment extends MongoBaseEntity {

    @NotBlank
    private String name;
    @NotNull
    private LocalDate endDate;
    private LocalDate completedDate;
    private String comment;
    private BigInteger assetId;
    private boolean riskAssessment;
    private BigInteger processingActivityId;
    private Set<BigInteger> riskIds;
    private List<AssessmentAnswerValueObject> assessmentAnswers;
    @NotNull
    @Valid
    private List<Staff> assigneeList;
    @NotNull
    private Staff approver;
    private AssessmentStatus  assessmentStatus=AssessmentStatus.NEW;
    private BigInteger questionnaireTemplateId;
    private UserVO assessmentLastAssistBy;
    private LocalDate assessmentScheduledDate;
    @NotNull(message = "error.message.start.date.not.Selected")
    private LocalDate startDate;

    private AssessmentSchedulingFrequency assessmentSchedulingFrequency;
    private int relativeDeadlineDuration;
    private DurationType relativeDeadlineType;


    public Assessment(@NotBlank String name, @NotNull LocalDate endDate, @NotNull List<Staff> assigneeList, @NotNull Staff approver,String comment,@NotNull(message = "error.message.start.date.not.Selected") LocalDate startDate) {
        this.name = name;
        this.endDate = endDate;
        this.assigneeList = assigneeList;
        this.approver = approver;
        this.comment=comment;
        this.startDate=startDate;
    }

    public AssessmentSchedulingFrequency getAssessmentSchedulingFrequency() { return assessmentSchedulingFrequency; }

    public void setAssessmentSchedulingFrequency(AssessmentSchedulingFrequency assessmentSchedulingFrequency) { this.assessmentSchedulingFrequency = assessmentSchedulingFrequency; }

    public UserVO getAssessmentLastAssistBy() { return assessmentLastAssistBy; }

    public void setAssessmentLastAssistBy(UserVO assessmentLastAssistBy) { this.assessmentLastAssistBy = assessmentLastAssistBy; }

    public LocalDate getAssessmentScheduledDate() { return assessmentScheduledDate; }

    public void setAssessmentScheduledDate(LocalDate assessmentScheduledDate) { this.assessmentScheduledDate = assessmentScheduledDate; }

    public boolean isRiskAssessment() { return riskAssessment; }

    public void setRiskAssessment(boolean riskAssessment) { this.riskAssessment = riskAssessment; }

    public Set<BigInteger> getRiskIds() { return riskIds; }

    public void setRiskIds(Set<BigInteger> riskIds) { this.riskIds = riskIds; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getCompletedDate() { return completedDate; }

    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public List<Staff> getAssigneeList() { return assigneeList; }

    public void setAssigneeList(List<Staff> assigneeList) { this.assigneeList = assigneeList; }

    public Staff getApprover() { return approver; }

    public void setApprover(Staff approver) { this.approver = approver; }

    public AssessmentStatus getAssessmentStatus() { return assessmentStatus; }

    public void setAssessmentStatus(AssessmentStatus assessmentStatus) { this.assessmentStatus = assessmentStatus; }

    public BigInteger getQuestionnaireTemplateId() { return questionnaireTemplateId; }

    public void setQuestionnaireTemplateId(BigInteger questionnaireTemplateId) { this.questionnaireTemplateId = questionnaireTemplateId; }

    public BigInteger getAssetId() { return assetId; }

    public void setAssetId(BigInteger assetId) { this.assetId = assetId; }

    public BigInteger getProcessingActivityId() { return processingActivityId; }

    public void setProcessingActivityId(BigInteger processingActivityId) { this.processingActivityId = processingActivityId; }

    public List<AssessmentAnswerValueObject> getAssessmentAnswers() { return assessmentAnswers; }

    public void setAssessmentAnswers(List<AssessmentAnswerValueObject> assessmentAnswers) { this.assessmentAnswers = assessmentAnswers; }

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
