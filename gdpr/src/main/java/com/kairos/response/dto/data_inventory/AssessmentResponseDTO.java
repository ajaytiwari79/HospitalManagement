package com.kairos.response.dto.data_inventory;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.Staff;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AssessmentResponseDTO {


    private BigInteger id;

    @NotBlank
    private String name;

    @NotNull
    private LocalDate endDate;

    private LocalDate completedDate;

    private String comment;

    private BigInteger assetId;

    private BigInteger processingActivityId;

    private String assessmentQuestionAnswersJsonString;

    @NotNull
    private Staff assignee;

    @NotNull
    private Staff approver;

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate;}


    public LocalDate getCompletedDate() { return completedDate; }

    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public String getComment() { return comment; }

    public void setComment(String comment) { this.comment = comment; }

    public BigInteger getAssetId() { return assetId; }

    public void setAssetId(BigInteger assetId) { this.assetId = assetId; }

    public BigInteger getProcessingActivityId() { return processingActivityId; }

    public void setProcessingActivityId(BigInteger processingActivityId) { this.processingActivityId = processingActivityId; }

    public String getAssessmentQuestionAnswersJsonString() { return assessmentQuestionAnswersJsonString; }

    public void setAssessmentQuestionAnswersJsonString(String assessmentQuestionAnswersJsonString) { this.assessmentQuestionAnswersJsonString = assessmentQuestionAnswersJsonString; }

    public Staff getAssignee() { return assignee; }

    public void setAssignee(Staff assignee) { this.assignee = assignee; }

    public Staff getApprover() { return approver; }

    public void setApprover(Staff approver) { this.approver = approver; }
}
