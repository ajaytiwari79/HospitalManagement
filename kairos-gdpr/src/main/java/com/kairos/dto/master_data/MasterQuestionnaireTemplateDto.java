package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.QuestionnaireType;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterQuestionnaireTemplateDto {

    @NotNullOrEmpty(message = "name.cannot.be.empty.or.null")
    private String name;

    @NotNull(message = "list.cannot.be.null")
    @NotEmpty(message = "list.cannot.be.empty")
    private List<BigInteger> sections;

    private Long countryId;

    private QuestionnaireType questionnaireType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QuestionnaireType getQuestionnaireType() {
        return questionnaireType;
    }

    public void setQuestionnaireType(QuestionnaireType questionnaireType) {
        this.questionnaireType = questionnaireType;
    }

    public List<BigInteger> getSections() {
        return sections;
    }

    public void setSections(List<BigInteger> sections) {
        this.sections = sections;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }


}
