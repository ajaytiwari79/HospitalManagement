package com.kairos.response.dto.master_data.questionnaire_template;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionBasicResponse {

    private BigInteger id;

    @NotNullOrEmpty(message = "name.cannot.be.empty.or.null")
    private String name;

    private String description;

    private String questionType;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

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

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }
}
