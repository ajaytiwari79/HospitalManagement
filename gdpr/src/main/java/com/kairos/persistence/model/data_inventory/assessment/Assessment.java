package com.kairos.persistence.model.data_inventory.assessment;


import com.kairos.enums.DurationType;
import com.kairos.enums.gdpr.AssessmentSchedulingFrequency;
import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.model.risk_management.Risk;
import org.javers.core.metamodel.annotation.Entity;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Assessment extends BaseEntity {

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotNull(message = "error.message.due.date.not.Selected")
    private LocalDate endDate;
    private LocalDate completedDate;
    private String comment;
    @OneToOne
    private Asset asset;
    private boolean isRiskAssessment;
    @OneToOne
    private ProcessingActivity processingActivity;
    @OneToMany
    private List<Risk> risks = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    private List<AssessmentAnswer> assessmentAnswers = new ArrayList<>();
    @NotNull
    @Valid
    @ElementCollection
    private List<Staff> assigneeList = new ArrayList<>();
    @NotNull
    private Staff approver;
    private AssessmentStatus  assessmentStatus=AssessmentStatus.NEW;
    @OneToOne
    private QuestionnaireTemplate questionnaireTemplate;
    @Embedded
    private UserVO assessmentLastAssistBy;
    private LocalDate assessmentLaunchedDate;
    @NotNull(message = "error.message.start.date.not.Selected")
    private LocalDate startDate;
    private AssessmentSchedulingFrequency assessmentSchedulingFrequency;
    private int relativeDeadlineDuration;
    private DurationType relativeDeadlineType;
    @NotNull
    private Long organizationId;




    public Assessment(@NotBlank String name, @NotNull(message = "error.message.start.date.not.Selected") LocalDate startDate,@NotNull LocalDate endDate, String comment, @NotNull List<Staff> assigneeList, @NotNull Staff approver,@NotNull Long organizationId) {
        this.name = name;
        this.endDate = endDate;
        this.assigneeList = assigneeList;
        this.approver = approver;
        this.comment=comment;
        this.startDate=startDate;
        this.organizationId=organizationId;
    }




    public Assessment() {
    }


    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }




    public AssessmentSchedulingFrequency getAssessmentSchedulingFrequency() { return assessmentSchedulingFrequency; }

    public void setAssessmentSchedulingFrequency(AssessmentSchedulingFrequency assessmentSchedulingFrequency) { this.assessmentSchedulingFrequency = assessmentSchedulingFrequency; }

    public UserVO getAssessmentLastAssistBy() { return assessmentLastAssistBy; }

    public void setAssessmentLastAssistBy(UserVO assessmentLastAssistBy) { this.assessmentLastAssistBy = assessmentLastAssistBy; }

    public LocalDate getAssessmentLaunchedDate() { return assessmentLaunchedDate; }

    public void setAssessmentLaunchedDate(LocalDate assessmentLaunchedDate) { this.assessmentLaunchedDate = assessmentLaunchedDate; }

    public boolean isRiskAssessment() { return isRiskAssessment; }

    public void setRiskAssessment(boolean riskAssessment) { this.isRiskAssessment = riskAssessment; }

    public List<Risk> getRisks() { return risks; }

    public void setRisks(List<Risk> risks) { this.risks = risks; }

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

    public QuestionnaireTemplate getQuestionnaireTemplate() { return questionnaireTemplate; }

    public void setQuestionnaireTemplate(QuestionnaireTemplate questionnaireTemplate) { this.questionnaireTemplate = questionnaireTemplate; }

    public Asset getAsset() { return asset; }

    public void setAsset(Asset asset) { this.asset = asset; }

    public ProcessingActivity getProcessingActivity() { return processingActivity; }

    public void setProcessingActivity(ProcessingActivity processingActivity) { this.processingActivity = processingActivity; }

    public List<AssessmentAnswer> getAssessmentAnswers() { return assessmentAnswers; }

    public void setAssessmentAnswers(List<AssessmentAnswer> assessmentAnswers) { this.assessmentAnswers = assessmentAnswers; }
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
