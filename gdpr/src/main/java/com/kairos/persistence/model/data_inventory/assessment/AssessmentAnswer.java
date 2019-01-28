package com.kairos.persistence.model.data_inventory.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.QuestionType;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class AssessmentAnswer {


    @NotNull(message = "Question id can't be null for Assessment Answer")
    private Long questionId;
    private String attributeName;
    private Object value;
    private QuestionType questionType;



    public AssessmentAnswer() {
    }

    public AssessmentAnswer(Long questionId, String attributeName, Object value, QuestionType questionType) {
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

    public Object getValue() { return value; }

    public void setValue(Object value) { this.value = value; }
}
