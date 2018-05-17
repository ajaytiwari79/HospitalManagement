package com.kairos.activity.persistence.model.night_worker;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by prerna on 8/5/18.
 */
public class StaffQuestionnaire extends MongoBaseEntity{

    private String name;
    private List<QuestionAnswerPair> questionAnswerPair;
    private boolean submitted;
    private LocalDate submittedOn;

    public StaffQuestionnaire(){
        // default constructor
    }

    public StaffQuestionnaire(String name, List<QuestionAnswerPair> questionAnswerPair){
        this.name = name;
        this.questionAnswerPair = questionAnswerPair;
    }

    public List<QuestionAnswerPair> getQuestionAnswerPair() {
        return questionAnswerPair;
    }

    public void setQuestionAnswerPair(List<QuestionAnswerPair> questionAnswerPair) {
        this.questionAnswerPair = questionAnswerPair;
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
