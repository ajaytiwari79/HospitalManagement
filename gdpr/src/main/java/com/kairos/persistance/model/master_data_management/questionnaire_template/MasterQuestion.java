package com.kairos.persistance.model.master_data_management.questionnaire_template;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotNull;

@Document(collection = "question")
public class MasterQuestion extends MongoBaseEntity {

    @NotNullOrEmpty(message = "name.cannot.be.empty.or.null")
    private String question;

    @NotNullOrEmpty(message = "description.cannot.be.empty.or.null")
    private String description;

    private Boolean isRequired=false;

    @NotNull
    private String questionType;

    private Boolean isNotSureAllowed=false;

    private Long countryId;

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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public MasterQuestion(String question, String description, String questionType, Long countryId) {
        this.question = question;
        this.description = description;
        this.questionType = questionType;
        this.countryId = countryId;
    }

    public MasterQuestion() {
    }
}
