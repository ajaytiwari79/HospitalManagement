package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.QuestionType;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionDto {


    @NotNullOrEmpty(message = "name.cannot.be.empty.or.null")
    private String name;

    @NotNullOrEmpty(message = "description.cannot.be.empty.or.null")
    private String description;

    private Boolean isRequired;

    @NotNull
    private QuestionType questionType;

    private Boolean isNotSureAllowed;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Boolean getNotSureAllowed() {
        return isNotSureAllowed;
    }

    public void setNotSureAllowed(Boolean notSureAllowed) {
        isNotSureAllowed = notSureAllowed;
    }
}
