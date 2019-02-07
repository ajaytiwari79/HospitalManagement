package com.kairos.persistence.model.questionnaire_template;


import com.kairos.persistence.model.common.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class QuestionnaireSection extends BaseEntity {

    private String title;

    @OneToMany(cascade = CascadeType.ALL ,orphanRemoval = true)
    @JoinColumn(name = "questionnaire_section_id")
    private List<Question> questions=new ArrayList<>();
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

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }


    @Override
    public void delete() {
        super.delete();
        this.questions.forEach(question -> {
            question.delete();
        });
    }
}
