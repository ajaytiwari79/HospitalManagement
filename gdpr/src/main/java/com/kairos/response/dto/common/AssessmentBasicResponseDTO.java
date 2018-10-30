package com.kairos.response.dto.common;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.gdpr.AssessmentStatus;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentBasicResponseDTO {

    private BigInteger id;
    private String name;
    private LocalDate endDate;
    private LocalDate completedDate;
    private String comment;
    private List<Staff> assigneeList;
    private Staff approver;
    private AssessmentStatus assessmentStatus;

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

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
}
