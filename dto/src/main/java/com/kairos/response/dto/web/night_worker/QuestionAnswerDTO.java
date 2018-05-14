package com.kairos.response.dto.web.night_worker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;

/**
 * Created by prerna on 8/5/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionAnswerDTO {
    private BigInteger questionId;
    private String question;
    private boolean answer;

    public QuestionAnswerDTO(){
        // default constructor
    }

    public BigInteger getQuestionId() {
        return questionId;
    }

    public void setQuestionId(BigInteger questionId) {
        this.questionId = questionId;
    }

    public boolean isAnswer() {
        return answer;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
