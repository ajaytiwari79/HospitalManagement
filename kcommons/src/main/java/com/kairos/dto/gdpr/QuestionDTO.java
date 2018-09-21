package com.kairos.dto.gdpr;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionDTO {

    private BigInteger id;

    @NotBlank(message = "Question title  can't be  empty")
    private String question;

    @NotBlank(message = "Description  can't be  Empty")
    private String description;

    private boolean required;

    @NotNull(message = "Question type Must be Text ,Yes no May")
    private QuestionType questionType;

    private String attributeName;

    private boolean notSureAllowed;

    public String getAttributeName() { return attributeName; }

    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public QuestionType getQuestionType() { return questionType; }

    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    public boolean isRequired() { return required; }

    public void setRequired(boolean required) { this.required = required; }

    public boolean isNotSureAllowed() { return notSureAllowed; }

    public void setNotSureAllowed(boolean notSureAllowed) { this.notSureAllowed = notSureAllowed; }

}
