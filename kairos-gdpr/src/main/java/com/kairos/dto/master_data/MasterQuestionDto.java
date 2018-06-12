package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.QuestionType;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionDto {

    private BigInteger id;

    @NotNullOrEmpty(message = "name.cannot.be.empty.or.null")
    private String question;

    @NotNullOrEmpty(message = "description.cannot.be.empty.or.null")
    private String description;

    private Boolean isRequired;

    @NotNull
    private String questionType;

    private Boolean isNotSureAllowed;

    private Boolean isNotApplicableAllowed;


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

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
