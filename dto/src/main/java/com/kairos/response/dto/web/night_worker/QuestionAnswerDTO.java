package com.kairos.response.dto.web.night_worker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;

/**
 * Created by prerna on 8/5/18.
 */
public class QuestionAnswerDTO {
    private BigInteger questionId;
//    private String question;
    private Boolean answer;

    public QuestionAnswerDTO(){
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
