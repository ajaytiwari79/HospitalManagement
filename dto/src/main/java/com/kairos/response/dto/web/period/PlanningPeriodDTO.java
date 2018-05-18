package com.kairos.response.dto.web.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.enums.DurationType;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Created by prerna on 10/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningPeriodDTO {
    private BigInteger id;
    private String name;
    @NotNull(message = "error.startdate.notnull")
//    private Long startDateMillis;
//    private Long endDateMillis;
//    private Date startDate;
//    private Date endDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long unitId = -1L;
    private int duration;
    private DurationType durationType;
    private int recurringNumber;
    private String currentPhase;
    private String nextPhase;
    /*private Long requestToPuzzleDate;
    private Long puzzleToConstructionDate;
    private Long constructionToDraftDate;*/
    private LocalDate requestToPuzzleDate;
    private LocalDate puzzleToConstructionDate;
    private LocalDate constructionToDraftDate;
    private List<PeriodPhaseFlippingDateDTO> phaseFlippingDate;
    private String periodDuration;
//    private ZoneId zoneId;


    public PlanningPeriodDTO(){
        // default constructor
    }

    /*public PlanningPeriodDTO(Long startDateMillis, int duration, DurationType durationType, int recurringNumber, Date endDate, ZoneId zoneId){
        this.startDateMillis = startDateMillis;
        this.duration = duration;
        this.durationType = durationType;
        this.recurringNumber = recurringNumber;
        this.endDate = endDate;
        this.zoneId = zoneId;
    }*/

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

    /*public Long getRequestToPuzzleDate() {
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
    }*/

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    /*public Long getStartDateMillis() {
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
    }*/

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

    /*public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }*/

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
}

