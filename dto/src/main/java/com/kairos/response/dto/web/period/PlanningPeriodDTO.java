package com.kairos.response.dto.web.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DurationFieldType;

import java.util.Date;

/**
 * Created by prerna on 6/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningPeriodDTO {
    private Long startDate;
    private Long endDate;
    private Long unitId = -1L;
    private int duration;
    private DurationFieldType durationType;
    private int recurringNumber;
    private String currentPhase;
    private String nextPhase;
    private Long requestToPuzzleDate;
    private Long puzzleToConstructionDate;
    private Long constructionToDraftDate;


    public PlanningPeriodDTO(){
        // default constructor
    }

    public Date getStartDate() {
        return new Date(startDate);
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
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

    public DurationFieldType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationFieldType durationType) {
        this.durationType = durationType;
    }

    public int getRecurringNumber() {
        return recurringNumber;
    }

    public void setRecurringNumber(int recurringNumber) {
        this.recurringNumber = recurringNumber;
    }

    public Date getEndDate() {
        return new Date(endDate);
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

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
}
