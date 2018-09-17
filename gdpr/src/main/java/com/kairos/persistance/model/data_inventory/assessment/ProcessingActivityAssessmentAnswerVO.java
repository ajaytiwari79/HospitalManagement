package com.kairos.persistance.model.data_inventory.assessment;


import com.kairos.enums.ProcessingActivityAttributeName;
import org.javers.core.metamodel.annotation.ValueObject;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@ValueObject
public class ProcessingActivityAssessmentAnswerVO {

    @NotNull(message = "Question id can't be null for Assessment Answer")
    private BigInteger questionId;

    @NotNull(message ="Field Not Match with Asset" )
    private ProcessingActivityAttributeName processingActivityField;

    private Object value;

    public BigInteger getQuestionId() { return questionId; }

    public void setQuestionId(BigInteger questionId) { this.questionId = questionId; }

    public ProcessingActivityAttributeName getProcessingActivityField() { return processingActivityField; }

    public void setProcessingActivityField(ProcessingActivityAttributeName processingActivityField) { this.processingActivityField = processingActivityField; }

    public Object getValue() { return value; }

    public void setValue(Object value) { this.value = value; }


}
