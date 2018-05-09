package com.kairos.response.dto.web.night_worker;

import com.kairos.persistence.model.enums.PersonType;

import java.math.BigInteger;
import java.time.LocalDate;

/**
 * Created by prerna on 8/5/18.
 */
public class NightWorkerGeneralResponseDTO {

    private BigInteger id;
    private boolean nightWorker;
    private LocalDate startDate;
    private PersonType personType;
    private int questionnaireFrequency;

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

    public int getQuestionnaireFrequency() {
        return questionnaireFrequency;
    }

    public void setQuestionnaireFrequency(int questionnaireFrequency) {
        this.questionnaireFrequency = questionnaireFrequency;
    }
}
