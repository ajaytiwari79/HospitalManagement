package com.kairos.persistence.model.data_inventory.assessment;


import com.kairos.enums.gdpr.AssetAttributeName;
import org.javers.core.metamodel.annotation.ValueObject;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@ValueObject
public class AssetAssessmentAnswerVO {

    @NotNull(message = "Question id can't be null for Assessment Answer")
    private BigInteger questionId;
    @NotNull(message ="Field Not Match with Asset" )
    private AssetAttributeName assetField;
    private Object value;


    public AssetAssessmentAnswerVO( BigInteger questionId,  AssetAttributeName assetField, Object value) {
        this.questionId = questionId;
        this.assetField = assetField;
        this.value = value;
    }

    public BigInteger getQuestionId() { return questionId; }

    public void setQuestionId(BigInteger questionId) { this.questionId = questionId; }

    public AssetAttributeName getAssetField() { return assetField; }

    public void setAssetField(AssetAttributeName assetField) { this.assetField = assetField; }

    public Object getValue() { return value; }

    public void setValue(Object value) { this.value = value; }
}
