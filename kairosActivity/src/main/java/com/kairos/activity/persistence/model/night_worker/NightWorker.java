package com.kairos.activity.persistence.model.night_worker;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.enums.PersonType;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by prerna on 8/5/18.
 */
public class NightWorker extends MongoBaseEntity{

    private boolean nightWorker;
    private LocalDate startDate;
    private PersonType personType;
    private int questionnaireFrequency;
    private Long staffId;
    private List<QuestionnaireAnswer> questionnaireAnswers;
    public NightWorker(){
        // default constructor
    }
}
