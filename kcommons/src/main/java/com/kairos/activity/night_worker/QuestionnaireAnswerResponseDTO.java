package com.kairos.activity.night_worker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by prerna on 8/5/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionnaireAnswerResponseDTO {

//    private LocalDate dateOfAnswer;

    private BigInteger id;
    private String name;
    private List<QuestionAnswerDTO> questionAnswerPair;
    private boolean submitted;
    private LocalDate submittedOn;

    public QuestionnaireAnswerResponseDTO(){
        // default constructor
    }

   /* public QuestionnaireAnswerResponseDTO(String name, List<QuestionAnswerDTO> questionAnswerPair){
        this.name = name;
        this.questionAnswerPair = questionAnswerPair;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

    public LocalDate getSubmittedOn() {
        return submittedOn;
    }

    public void setSubmittedOn(LocalDate submittedOn) {
        this.submittedOn = submittedOn;
    }
}
