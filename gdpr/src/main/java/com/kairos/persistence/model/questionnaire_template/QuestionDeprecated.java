package com.kairos.persistence.model.questionnaire_template;


import com.kairos.enums.gdpr.QuestionType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class QuestionDeprecated {

    @NotBlank(message = "Question cant'be empty")
    private String question;
    @NotBlank(message = "Description can't be empty")
    private String description;
    private boolean required;
    @NotNull
    private QuestionType questionType;
    private boolean notSureAllowed;
    private String attributeName;
    private Long countryId;

    public QuestionDeprecated(String question, String description, boolean required, QuestionType questionType, boolean notSureAllowed, Long countryId) {
        this.question = question;
        this.description = description;
        this.questionType = questionType;
        this.countryId = countryId;
    }

    public QuestionDeprecated(String question, String description, boolean required, QuestionType questionType, boolean notSureAllowed) {
        this.question = question;
        this.description = description;
        this.required = required;
        this.questionType = questionType;
        this.notSureAllowed = notSureAllowed;
    }

    public String getAttributeName() { return attributeName; }

    public QuestionDeprecated setAttributeName(String attributeName) { this.attributeName = attributeName;  return this;}

    public Long getCountryId() {
        return countryId;
    }

    public void  setCountryId(Long countryId) { this.countryId = countryId; }

    public String getQuestion() {
        return question;
    }

    public QuestionDeprecated setQuestion(String question) { this.question = question; return this;}

    public String getDescription() {
        return description;
    }

    public QuestionDeprecated setDescription(String description) { this.description = description; return this; }

    public QuestionType getQuestionType() { return questionType; }

    public QuestionDeprecated setQuestionType(QuestionType questionType) { this.questionType = questionType; return this; }

    public boolean isRequired() { return required; }

    public QuestionDeprecated setRequired(boolean required) { this.required = required; return this; }

    public boolean isNotSureAllowed() { return notSureAllowed;}

    public QuestionDeprecated setNotSureAllowed(boolean notSureAllowed) { this.notSureAllowed = notSureAllowed;  return this;}

    public QuestionDeprecated() {
    }
}
