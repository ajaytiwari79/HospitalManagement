package com.kairos.persistence.model.questionnaire_template;


import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
public class QuestionnaireSectionMD extends BaseEntity {

    private String title;
    @OneToMany
    private List<QuestionMD> questions=new ArrayList<>();
    private Long countryId;


    public QuestionnaireSectionMD(String title, Long countryId) {
        this.title = title;
        this.countryId = countryId;
    }


    public QuestionnaireSectionMD(String title) {
        this.title = title;
    }
    public QuestionnaireSectionMD() {
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

    public List<QuestionMD> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionMD> questions) {
        this.questions = questions;
    }


}
