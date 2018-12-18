package com.kairos.dto.activity.period;

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
    private BigInteger currentPhaseId;
    private String nextPhase;
    private FlippingDateDTO requestToPuzzleDate;
    private FlippingDateDTO puzzleToConstructionDate;
    private FlippingDateDTO constructionToDraftDate;
    private List<PeriodPhaseDTO> phaseFlippingDate;
    private String periodDuration;
    private boolean active=true;
    private String color;
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

    public FlippingDateDTO getRequestToPuzzleDate() {
        return requestToPuzzleDate;
    }

    public void setRequestToPuzzleDate(FlippingDateDTO requestToPuzzleDate) {
        this.requestToPuzzleDate = requestToPuzzleDate;
    }

    public FlippingDateDTO getPuzzleToConstructionDate() {
        return puzzleToConstructionDate;
    }

    public void setPuzzleToConstructionDate(FlippingDateDTO puzzleToConstructionDate) {
        this.puzzleToConstructionDate = puzzleToConstructionDate;
    }

    public FlippingDateDTO getConstructionToDraftDate() {
        return constructionToDraftDate;
    }

    public void setConstructionToDraftDate(FlippingDateDTO constructionToDraftDate) {
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

    public List<PeriodPhaseDTO> getPhaseFlippingDate() {
        return phaseFlippingDate;
    }

    public void setPhaseFlippingDate(List<PeriodPhaseDTO> phaseFlippingDate) {
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigInteger getCurrentPhaseId() {
        return currentPhaseId;
    }

    public void setCurrentPhaseId(BigInteger currentPhaseId) {
        this.currentPhaseId = currentPhaseId;
    }
}

