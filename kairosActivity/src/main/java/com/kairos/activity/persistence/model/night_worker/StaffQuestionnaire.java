package com.kairos.activity.persistence.model.night_worker;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by prerna on 8/5/18.
 */
public class StaffQuestionnaire extends MongoBaseEntity{

    private List<QuestionAnswerPair> questionAnswerPair;

    public StaffQuestionnaire(){
        // default constructor
    }

    public StaffQuestionnaire(List<QuestionAnswerPair> questionAnswerPair){
        this.questionAnswerPair = questionAnswerPair;
    }

    public List<QuestionAnswerPair> getQuestionAnswerPair() {
        return questionAnswerPair;
    }

    public void setQuestionAnswerPair(List<QuestionAnswerPair> questionAnswerPair) {
        this.questionAnswerPair = questionAnswerPair;
    }
}
