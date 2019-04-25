package com.kairos.persistence.model.night_worker;

import com.kairos.enums.PersonType;
import com.kairos.persistence.model.common.MongoBaseEntity;

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
    private int questionnaireFrequencyInMonths;
    private Long staffId;
    private Long unitId;
    private List<BigInteger> staffQuestionnairesId;
    private boolean eligibleNightWorker;

    public NightWorker(){
        // default constructor
    }

    public NightWorker(boolean nightWorker, LocalDate startDate, PersonType personType, int questionnaireFrequencyInMonths, Long staffId, Long unitId, boolean eligibleNightWorker){
        this.nightWorker = nightWorker;
        this.startDate = startDate;
        this.personType = personType;
        this.questionnaireFrequencyInMonths = questionnaireFrequencyInMonths;
        this.staffId = staffId;
        this.unitId = unitId;
        this.eligibleNightWorker = eligibleNightWorker;
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

    public int getQuestionnaireFrequencyInMonths() {
        return questionnaireFrequencyInMonths;
    }

    public void setQuestionnaireFrequencyInMonths(int questionnaireFrequencyInMonths) {
        this.questionnaireFrequencyInMonths = questionnaireFrequencyInMonths;
    }

    public List<BigInteger> getStaffQuestionnairesId() {
        return staffQuestionnairesId;
    }

    public void setStaffQuestionnairesId(List<BigInteger> staffQuestionnairesId) {
        this.staffQuestionnairesId = staffQuestionnairesId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public boolean isEligibleNightWorker() {
        return eligibleNightWorker;
    }

    public void setEligibleNightWorker(boolean eligibleNightWorker) {
        this.eligibleNightWorker = eligibleNightWorker;
    }
}
