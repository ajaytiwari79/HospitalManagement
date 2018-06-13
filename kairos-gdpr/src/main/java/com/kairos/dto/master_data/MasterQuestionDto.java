package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.QuestionType;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionDto {

    @NotNullOrEmpty(message = "name.cannot.be.empty.or.null")
    @Pattern(message = "numberic and Special character are not allowed in question title",regexp ="^[a-zA-Z\\s]+$" )
    private String question;

    @NotNullOrEmpty(message = "description.cannot.be.empty.or.null")
    private String description;

    private Boolean isRequired=false;

    @NotNullOrEmpty(message = "question type cannot be empty or null ,it should be text or either yes no maybe")
    private String questionType;

    private Boolean isNotSureAllowed=false;

    private Boolean isNotApplicableAllowed=false;

    public Boolean getNotApplicableAllowed() {
        return isNotApplicableAllowed;
    }

    public void setNotApplicableAllowed(Boolean notApplicableAllowed) {
        isNotApplicableAllowed = notApplicableAllowed;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public Boolean getNotSureAllowed() {
        return isNotSureAllowed;
    }

    public void setNotSureAllowed(Boolean notSureAllowed) {
        isNotSureAllowed = notSureAllowed;
    }
}
