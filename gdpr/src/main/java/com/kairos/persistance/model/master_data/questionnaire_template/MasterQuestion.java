package com.kairos.persistance.model.master_data.questionnaire_template;


import com.kairos.enums.QuestionType;
import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Document(collection = "question")
public class MasterQuestion extends MongoBaseEntity {

    @NotBlank(message = "Question cant'be empty")
    private String question;

    @NotBlank(message = "Description can't be empty")
    private String description;

    private Boolean isRequired=false;

    @NotNull
    private QuestionType questionType;

    private Boolean isNotSureAllowed=false;

    private String attributeName;

    private Long countryId;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

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

    public Boolean getRequired() {
        return isRequired;
    }

    public void setRequired(Boolean required) {
        isRequired = required;
    }

    public Boolean getNotSureAllowed() {
        return isNotSureAllowed;
    }

    public void setNotSureAllowed(Boolean notSureAllowed) {
        isNotSureAllowed = notSureAllowed;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public MasterQuestion(String question, String description, QuestionType questionType, Long countryId) {
        this.question = question;
        this.description = description;
        this.questionType = questionType;
        this.countryId = countryId;
    }

    public MasterQuestion() {
    }
}
