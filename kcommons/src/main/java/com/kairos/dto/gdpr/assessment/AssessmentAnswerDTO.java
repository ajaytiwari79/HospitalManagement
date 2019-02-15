package com.kairos.dto.gdpr.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentAnswerDTO {



    private Long id;
    @NotNull(message = "Question id can't be null for Assessment Answer")
    private Long questionId;
    @NotBlank(message = "error.message.attribute.name.null")
    private String attributeName;
    private SelectedChoiceDTO value;
    @NotNull(message = "error.message.questionType.name.null")
    private QuestionType questionType;

    public AssessmentAnswerDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public SelectedChoiceDTO getValue() {
        return value;
    }

    public void setValue(SelectedChoiceDTO value) {
        this.value = value;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }
}
