package com.kairos.persistance.model.master_data_management.questionnaire_template;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.enums.QuestionnaireType;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "questionnaire_template")
public class MasterQuestionnaireTemplate extends MongoBaseEntity {

    @NotNullOrEmpty(message = "name.cannot.be.empty.or.null")
    private String name;

    @NotNull(message = "list.cannot.be.null")
    @NotEmpty(message = "list.cannot.be.empty")
    private List<BigInteger> sections;

    @NotNull
    private Long countryId;

    private String questionnaireType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuestionnaireType() {
        return questionnaireType;
    }

    public void setQuestionnaireType(String questionnaireType) {
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

    public MasterQuestionnaireTemplate(String name, List<BigInteger> sections, Long countryId, String questionnaireType) {
        this.name = name;
        this.sections = sections;
        this.countryId = countryId;
        this.questionnaireType = questionnaireType;
    }

    public MasterQuestionnaireTemplate() {
    }
}
