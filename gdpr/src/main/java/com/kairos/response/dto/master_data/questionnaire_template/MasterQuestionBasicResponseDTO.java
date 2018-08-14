package com.kairos.response.dto.master_data.questionnaire_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionBasicResponseDTO {

    private BigInteger id;

    @NotBlank(message = "Name can't be empty")
    private String question;

    private String description;

    private String questionType;

    private String attributeName;

    private boolean isRequired;

    public String getAttributeName() { return attributeName; }

    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public boolean isRequired() { return isRequired; }

    public void setRequired(boolean required) { isRequired = required; }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public MasterQuestionBasicResponseDTO() {
    }
}
