package com.kairos.activity.persistence.model.night_worker;

import java.math.BigInteger;

/**
 * Created by prerna on 8/5/18.
 */
public class QuestionAnswerPair {

    private BigInteger questionId;
    private Boolean answer;

    public QuestionAnswerPair(){
        // default constructor
    }

    public BigInteger getQuestionId() {
        return questionId;
    }

    public void setQuestionId(BigInteger questionId) {
        this.questionId = questionId;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }
}
