package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;

import java.math.BigInteger;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssessmentResponseDTO {


    private BigInteger id;
    private String name;
    private LocalDate endDate;
    private LocalDate completedDate;
    private String comment;
    private Staff assignee;
    private Staff approver;
    private AssessmentStatus assessmentStatus;
    private AssetBasicResponseDTO asset;
    private ProcessingActivityBasicResponseDTO processingActivity;

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getCompletedDate() { return completedDate; }

    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public String getComment() { return comment;}


    public void setComment(String comment) { this.comment = comment; }

    public Staff getAssignee() { return assignee; }

    public void setAssignee(Staff assignee) { this.assignee = assignee; }

    public Staff getApprover() { return approver; }

    public void setApprover(Staff approver) { this.approver = approver; }

    public AssessmentStatus getAssessmentStatus() { return assessmentStatus; }

    public void setAssessmentStatus(AssessmentStatus assessmentStatus) { this.assessmentStatus = assessmentStatus; }

    public AssetBasicResponseDTO getAsset() { return asset; }

    public void setAsset(AssetBasicResponseDTO asset) { this.asset = asset; }

    public ProcessingActivityBasicResponseDTO getProcessingActivity() { return processingActivity; }

    public void setProcessingActivity(ProcessingActivityBasicResponseDTO processingActivity) { this.processingActivity = processingActivity; }
}
