package com.kairos.response.dto.web.night_worker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 8/5/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireAnswerResponseDTO {

//    private LocalDate dateOfAnswer;

    private BigInteger id;
    private List<QuestionAnswerDTO> questionAnswerPair;

    public QuestionnaireAnswerResponseDTO(){
        // default constructor
    }

    /*public LocalDate getDateOfAnswer() {
        return dateOfAnswer;
    }

    public void setDateOfAnswer(LocalDate dateOfAnswer) {
        this.dateOfAnswer = dateOfAnswer;
    }*/

    public List<QuestionAnswerDTO> getQuestionAnswerPair() {
        return questionAnswerPair;
    }

    public void setQuestionAnswerPair(List<QuestionAnswerDTO> questionAnswerPair) {
        this.questionAnswerPair = questionAnswerPair;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
