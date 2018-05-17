package com.kairos.activity.persistence.model.night_worker;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.activity.util.timeCareShift.Person;
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
    private List<BigInteger> staffQuestionnairesId;

    public NightWorker(){
        // default constructor
    }

    public NightWorker(boolean nightWorker, LocalDate startDate, PersonType personType, int questionnaireFrequency, Long staffId){
        this.nightWorker = nightWorker;
        this.startDate = startDate;
        this.personType = personType;
        this.questionnaireFrequency = questionnaireFrequency;
        this.staffId = staffId;
    }

    public boolean isNightWorker() {
        return nightWorker;
    }

    public void setNightWorker(boolean nightWorker) {
        this.nightWorker = nightWorker;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    public int getQuestionnaireFrequency() {
        return questionnaireFrequency;
    }

    public void setQuestionnaireFrequency(int questionnaireFrequency) {
        this.questionnaireFrequency = questionnaireFrequency;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public List<BigInteger> getStaffQuestionnaires() {
        return staffQuestionnairesId;
    }

    public void setStaffQuestionnaires(List<BigInteger> staffQuestionnairesId) {
        this.staffQuestionnairesId = staffQuestionnairesId;
    }
}
