package com.kairos.response.dto.master_data.questionnaire_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionBasicResponseDTO {

    private BigInteger id;

    @NotNullOrEmpty(message = "name.cannot.be.empty.or.null")
    private String question;

    private String description;

    private String questionType;

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
