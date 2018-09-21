package com.kairos.persistence.model.questionnaire_template;


import com.kairos.enums.gdpr.QuestionType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document(collection = "question")
public class Question extends MongoBaseEntity {

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

    public Question(String question, String description,  boolean required,QuestionType questionType, boolean notSureAllowed, Long countryId) {
        this.question = question;
        this.description = description;
        this.questionType = questionType;
        this.countryId = countryId;
    }

    public Question( String question, String description, boolean required, QuestionType questionType, boolean notSureAllowed) {
        this.question = question;
        this.description = description;
        this.required = required;
        this.questionType = questionType;
        this.notSureAllowed = notSureAllowed;
    }

    public String getAttributeName() { return attributeName; }

    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public boolean isRequired() { return required; }

    public void setRequired(boolean required) { this.required = required; }

    public boolean isNotSureAllowed() { return notSureAllowed; }

    public void setNotSureAllowed(boolean notSureAllowed) { this.notSureAllowed = notSureAllowed; }

    public Question() {
    }
}
