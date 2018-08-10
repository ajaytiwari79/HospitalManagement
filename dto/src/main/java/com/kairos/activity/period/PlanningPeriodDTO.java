package com.kairos.activity.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.DurationType;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by prerna on 10/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningPeriodDTO {
    private BigInteger id;
    private String name;
    @NotNull(message = "error.startdate.notnull")
    private LocalDate startDate;
    private LocalDate endDate;
    private Long unitId = -1L;
    private int duration;
    private DurationType durationType;
    private int recurringNumber; // TODO HARISH rename
    private String currentPhase;
    private String nextPhase;
    private LocalDate requestToPuzzleDate;
    private LocalDate puzzleToConstructionDate;
    private LocalDate constructionToDraftDate;
    private List<PeriodPhaseFlippingDateDTO> phaseFlippingDate;
    private String periodDuration;
    private boolean active=true;

    public PlanningPeriodDTO(){
        // default constructor
    }

    public PlanningPeriodDTO( LocalDate startDate, int duration, DurationType durationType, int recurringNumber, LocalDate endDate){
        this.startDate = startDate;
        this.duration = duration;
        this.durationType = durationType;
        this.recurringNumber = recurringNumber;
        this.endDate = endDate;
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

    public LocalDate getRequestToPuzzleDate() {
        return requestToPuzzleDate;
    }

    public void setRequestToPuzzleDate(LocalDate requestToPuzzleDate) {
        this.requestToPuzzleDate = requestToPuzzleDate;
    }

    public LocalDate getPuzzleToConstructionDate() {
        return puzzleToConstructionDate;
    }

    public void setPuzzleToConstructionDate(LocalDate puzzleToConstructionDate) {
        this.puzzleToConstructionDate = puzzleToConstructionDate;
    }

    public LocalDate getConstructionToDraftDate() {
        return constructionToDraftDate;
    }

    public void setConstructionToDraftDate(LocalDate constructionToDraftDate) {
        this.constructionToDraftDate = constructionToDraftDate;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

