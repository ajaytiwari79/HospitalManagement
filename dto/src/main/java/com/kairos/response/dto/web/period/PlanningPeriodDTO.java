package com.kairos.response.dto.web.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.enums.DurationType;
import org.joda.time.DurationFieldType;

import java.math.BigInteger;
import java.util.Date;

/**
 * Created by prerna on 10/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningPeriodDTO {
    private BigInteger id;
    private Long startDateInMillis;
    private Long endDateInMillis;
    private Date startDate;
    private Date endDate;
    private Long unitId = -1L;
    private int duration;
    private DurationType durationType;
    private int recurringNumber;
    private String currentPhase;
    private String nextPhase;
    private Long requestToPuzzleDate;
    private Long puzzleToConstructionDate;
    private Long constructionToDraftDate;


    public PlanningPeriodDTO(){
        // default constructor
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }

    public int getRecurringNumber() {
        return recurringNumber;
    }

    public void setRecurringNumber(int recurringNumber) {
        this.recurringNumber = recurringNumber;
    }

    /*public Date getEndDate() {
        return new Date(endDate);
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }*/

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    public String getNextPhase() {
        return nextPhase;
    }

    public void setNextPhase(String nextPhase) {
        this.nextPhase = nextPhase;
    }

    public Long getRequestToPuzzleDate() {
        return requestToPuzzleDate;
    }

    public void setRequestToPuzzleDate(Long requestToPuzzleDate) {
        this.requestToPuzzleDate = requestToPuzzleDate;
    }

    public Long getPuzzleToConstructionDate() {
        return puzzleToConstructionDate;
    }

    public void setPuzzleToConstructionDate(Long puzzleToConstructionDate) {
        this.puzzleToConstructionDate = puzzleToConstructionDate;
    }

    public Long getConstructionToDraftDate() {
        return constructionToDraftDate;
    }

    public void setConstructionToDraftDate(Long constructionToDraftDate) {
        this.constructionToDraftDate = constructionToDraftDate;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public Long getStartDateInMillis() {
        return startDateInMillis;
    }

    public void setStartDateInMillis(Long startDateInMillis) {
        this.startDateInMillis = startDateInMillis;
    }

    public Long getEndDateInMillis() {
        return endDateInMillis;
    }

    public void setEndDateInMillis(Long endDateInMillis) {
        this.endDateInMillis = endDateInMillis;
    }

    public Date getStartDate() {
        return new Date(startDateInMillis);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }



}

