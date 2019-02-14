package com.kairos.persistence.model.data_inventory.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class AssessmentAnswer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Question id can't be null for Assessment Answer")
    private Long questionId;
    private String attributeName;
    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    private SelectedChoice value;
    private QuestionType questionType;


    public AssessmentAnswer() {
    }

    public AssessmentAnswer(Long questionId, String attributeName, SelectedChoice value, QuestionType questionType) {

        this.questionId = questionId;
        this.attributeName = attributeName;
        this.value = value;
        this.questionType = questionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() { return questionId; }

    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getAttributeName() { return attributeName.trim(); }

    public void setAttributeName(String attributeName){ this.attributeName = attributeName; }

    public SelectedChoice getValue() { return value; }

    public void setValue(SelectedChoice value) { this.value = value; }

    public QuestionType getQuestionType() { return questionType; }

    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }
}
