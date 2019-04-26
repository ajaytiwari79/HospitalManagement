package com.kairos.persistence.model.night_worker;

import com.kairos.persistence.model.common.MongoBaseEntity;

/**
 * Created by prerna on 8/5/18.
 */
public class NightWorkerQuestion extends MongoBaseEntity{

    private String question;

    public NightWorkerQuestion(){
        // default constructor
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
