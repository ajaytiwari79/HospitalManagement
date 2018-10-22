package com.kairos.persistence.model.data_inventory.assessment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.gdpr.AssetAttributeName;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssessmentAnswerValueObject {


    @NotNull(message = "Question id can't be null for Assessment Answer")
    private BigInteger questionId;
    @NotBlank(message ="Attribute name can't be empty" )
    private String attributeName;
    private Object value;


    public AssessmentAnswerValueObject() {
    }

    public AssessmentAnswerValueObject(BigInteger questionId, String attributeName, Object value) {
        this.questionId = questionId;
        this.attributeName = attributeName;
        this.value = value;
    }

    public BigInteger getQuestionId() { return questionId; }

    public void setQuestionId(BigInteger questionId) { this.questionId = questionId; }

    public String getAttributeName() { return attributeName; }

    public void setAttributeName(String attributeName) { this.attributeName = attributeName; }

    public Object getValue() { return value; }

    public void setValue(Object value) { this.value = value; }
}
