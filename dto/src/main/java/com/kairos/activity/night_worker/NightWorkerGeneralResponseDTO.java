package com.kairos.activity.night_worker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.PersonType;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * Created by prerna on 8/5/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NightWorkerGeneralResponseDTO {

    private BigInteger id;
    private boolean nightWorker;
    private LocalDate startDate;
    private PersonType personType;
    private int questionnaireFrequencyInMonths;
    private boolean eligibleNightWorker;


    public NightWorkerGeneralResponseDTO(){
        // default constructor
    }

    public NightWorkerGeneralResponseDTO(boolean nightWorker){
        this.nightWorker = nightWorker;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public boolean isEligibleNightWorker() {
        return eligibleNightWorker;
    }

    public void setEligibleNightWorker(boolean eligibleNightWorker) {
        this.eligibleNightWorker = eligibleNightWorker;
    }
}
