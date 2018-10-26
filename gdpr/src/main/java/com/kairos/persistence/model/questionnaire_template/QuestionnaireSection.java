package com.kairos.persistence.model.questionnaire_template;


import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Document
public class QuestionnaireSection extends MongoBaseEntity {

    private String title;
    private List<BigInteger> questions=new ArrayList<>();
    private Long countryId;


    public QuestionnaireSection(String title, Long countryId) {
        this.title = title;
        this.countryId = countryId;
    }


    public QuestionnaireSection(String title) {
        this.title = title;
    }
    public QuestionnaireSection() {
    }

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



}
