package com.kairos.persistance.model.master_data_management.questionnaire_template;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Document(collection = "questionnaire_section")
public class MasterQuestionnaireSection extends MongoBaseEntity {

    @NotNullOrEmpty(message = "name.cannot.be.empty.or.null")
    private String title;

    @NotNull(message = "list.cannot.be.null")
    @NotEmpty(message = "list.cannot.be.empty")
    private List<BigInteger> questions=new ArrayList<>();

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<BigInteger> getQuestions() {
        return questions;
    }

    public void setQuestions(List<BigInteger> questions) {
        this.questions = questions;
    }

    public MasterQuestionnaireSection(String title, Long countryId) {
        this.title = title;
        this.countryId = countryId;
    }

    public MasterQuestionnaireSection() {
    }
}
