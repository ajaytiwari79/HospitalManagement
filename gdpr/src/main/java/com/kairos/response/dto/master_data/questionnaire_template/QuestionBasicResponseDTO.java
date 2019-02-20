package com.kairos.response.dto.master_data.questionnaire_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.gdpr.QuestionType;

import javax.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionBasicResponseDTO {

    private Long id;
    @NotBlank(message = "Name can't be empty")
    private String question;
    private String description;
    private Object value;
    private Object assessmentAnswerChoices;
    private QuestionType questionType;
    private String attributeName;
    private boolean required;
    private boolean notSureAllowed;


    public boolean isNotSureAllowed() { return notSureAllowed; }

    public void setNotSureAllowed(boolean notSureAllowed) { this.notSureAllowed = notSureAllowed; }

    public String getAttributeName() { return attributeName; }

    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public boolean isRequired() { return required; }

    public void setRequired(boolean required) { this.required = required; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public QuestionType getQuestionType() { return questionType; }

    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    public Object getValue() { return value; }

    public void setValue(Object value) { this.value = value; }

    public Object getAssessmentAnswerChoices() { return assessmentAnswerChoices; }

    public void setAssessmentAnswerChoices(Object assessmentAnswerChoices) { this.assessmentAnswerChoices = assessmentAnswerChoices; }

    public QuestionBasicResponseDTO() {
    }
}
