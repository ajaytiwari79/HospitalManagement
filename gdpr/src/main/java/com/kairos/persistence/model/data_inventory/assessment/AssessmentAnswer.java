package com.kairos.persistence.model.data_inventory.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
public class AssessmentAnswer {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private
    Long id;

    @NotNull(message = "Question id can't be null for Assessment Answer")
    private Long questionId;
    private String attributeName;
    private String value;
    private QuestionType questionType;



    public AssessmentAnswer() {
    }

    public AssessmentAnswer(Long questionId, String attributeName, String value, QuestionType questionType) {
        this.questionId = questionId;
        this.attributeName = attributeName;
        this.value = value;
        this.questionType= questionType;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Long getQuestionId() { return questionId; }

    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getAttributeName() { return attributeName; }

    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
