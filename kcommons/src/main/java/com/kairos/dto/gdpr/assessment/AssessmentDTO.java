package com.kairos.dto.gdpr.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentDTO {

    protected BigInteger id;

    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    protected String name;

    @NotNull(message = "error.message.due.date.not.Selected")
    protected LocalDate endDate;

    protected String comment;

    @NotNull(message = "error.message.assignee.not.selected")
    protected Staff assignee;

    protected Staff approver;

    public BigInteger getId() { return id; }

    public void setId(BigInteger id) { this.id = id; }

    public String getName() { return name.trim(); }

    public void setName(String name) { this.name = name; }

    public LocalDate getEndDate() { return endDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getComment() { return comment.trim(); }

    public void setComment(String comment) { this.comment = comment; }

    public Staff getAssignee() { return assignee; }

    public void setAssignee(Staff assignee) { this.assignee = assignee; }

    public Staff getApprover() { return approver; }

    public void setApprover(Staff approver) { this.approver = approver; }

}
