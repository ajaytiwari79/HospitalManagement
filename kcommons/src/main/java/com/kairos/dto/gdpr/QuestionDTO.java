package com.kairos.dto.gdpr;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionDTO {

    private BigInteger id;

    @NotBlank(message = "Question title  can't be  empty")
    private String question;

    @NotBlank(message = "Description  can't be  Empty")
    private String description;

    private Boolean isRequired=false;

    @NotBlank(message = "Question type Must be Text ,Yes no May")
    private String questionType;

    private String attributeName;

    private Boolean isNotSureAllowed=false;

    private Boolean isNotApplicableAllowed=false;

    public String getAttributeName() {
        return attributeName; }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName; }

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
        return question.trim();
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
