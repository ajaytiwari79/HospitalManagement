package com.kairos.response.dto.master_data.questionnaire_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.gdpr.QuestionType;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class QuestionBasicResponseDTO {

    private BigInteger id;

    @NotBlank(message = "Name can't be empty")
    private String question;
    private String description;
    private Object assessmentQuestionValues;
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

    public QuestionType getQuestionType() { return questionType; }

    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    public Object getAssessmentQuestionValues() { return assessmentQuestionValues; }

    public void setAssessmentQuestionValues(Object assessmentQuestionValues) { this.assessmentQuestionValues = assessmentQuestionValues; }

    public Object getAssessmentAnswerChoices() { return assessmentAnswerChoices; }

    public void setAssessmentAnswerChoices(Object assessmentAnswerChoices) { this.assessmentAnswerChoices = assessmentAnswerChoices; }

    public QuestionBasicResponseDTO() {
    }
}
