package com.kairos.response.dto.web.night_worker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 8/5/18.
 */
public class QuestionnaireAnswerResponseDTO {

//    private LocalDate dateOfAnswer;
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
}
