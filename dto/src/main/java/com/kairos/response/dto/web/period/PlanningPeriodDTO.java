package com.kairos.response.dto.web.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.enums.DurationType;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 10/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningPeriodDTO {
    private BigInteger id;
    private String name;
    private Long startDateMillis;
    private Long endDateMillis;
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
    private List<PeriodPhaseFlippingDateDTO> phaseFlippingDate;
    private String periodDuration;


    public PlanningPeriodDTO(){
        // default constructor
    }

    public PlanningPeriodDTO(Long startDateMillis, int duration, DurationType durationType, int recurringNumber){
        this.startDateMillis = startDateMillis;
        this.duration = duration;
        this.durationType = durationType;
        this.recurringNumber = recurringNumber;
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

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    public Date getStartDate() {
        return startDate;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PeriodPhaseFlippingDateDTO> getPhaseFlippingDate() {
        return phaseFlippingDate;
    }

    public void setPhaseFlippingDate(List<PeriodPhaseFlippingDateDTO> phaseFlippingDate) {
        this.phaseFlippingDate = phaseFlippingDate;
    }

    public String getPeriodDuration() {
        return periodDuration;
    }

    public void setPeriodDuration(String periodDuration) {
        this.periodDuration = periodDuration;
    }
}

