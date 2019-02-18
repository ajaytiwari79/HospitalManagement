package com.kairos.persistence.model.questionnaire_template;


import com.kairos.persistence.model.common.BaseEntity;

import javax.persistence.*;
import java.util.List;

@Entity
public class QuestionnaireSection extends BaseEntity {

    private String title;

    @OneToMany(cascade = CascadeType.ALL ,fetch = FetchType.EAGER)
    @JoinColumn(name = "questionnaire_section_id")
    private List<Question> questions;
    private Long countryId;
    private Long organizationId;

    public QuestionnaireSection() {
    }


    public QuestionnaireSection(String title,  Long countryId, Long organizationId) {
        this.title = title;
        this.countryId = countryId;
        this.organizationId = organizationId;
    }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public List<Question> getQuestions() { return questions; }

    public void setQuestions(List<Question> questions) { this.questions = questions; }


    @Override
    public void delete() {
        super.delete();
        this.questions.forEach(BaseEntity::delete);
    }
}
