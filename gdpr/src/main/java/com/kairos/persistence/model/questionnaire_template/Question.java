package com.kairos.persistence.model.questionnaire_template;


import com.kairos.enums.gdpr.QuestionType;
import com.kairos.persistence.model.common.BaseEntity;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class Question extends BaseEntity {

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
    private Long organizationId;


    public Question(@NotBlank(message = "Question cant'be empty") String question, @NotBlank(message = "Description can't be empty") String description, boolean required, @NotNull QuestionType questionType, boolean notSureAllowed, Long countryId, Long organizationId) {
        this.question = question;
        this.description = description;
        this.required = required;
        this.questionType = questionType;
        this.notSureAllowed = notSureAllowed;
        this.countryId = countryId;
        this.organizationId = organizationId;
    }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }


    public String getAttributeName() { return attributeName; }

    public Question setAttributeName(String attributeName) { this.attributeName = attributeName;  return this;}

    public Long getCountryId() {
        return countryId;
    }

    public void  setCountryId(Long countryId) { this.countryId = countryId; }

    public String getQuestion() {
        return question;
    }

    public Question setQuestion(String question) { this.question = question; return this;}

    public String getDescription() {
        return description;
    }

    public Question setDescription(String description) { this.description = description; return this; }

    public QuestionType getQuestionType() { return questionType; }

    public Question setQuestionType(QuestionType questionType) { this.questionType = questionType; return this; }

    public boolean isRequired() { return required; }

    public Question setRequired(boolean required) { this.required = required; return this; }

    public boolean isNotSureAllowed() { return notSureAllowed;}

    public Question setNotSureAllowed(boolean notSureAllowed) { this.notSureAllowed = notSureAllowed;  return this;}

    public Question() {
    }
}
