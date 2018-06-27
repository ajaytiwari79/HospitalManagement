package com.kairos.persistence.model.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prerna on 6/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningPeriod extends MongoBaseEntity {

    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    @Indexed
    private Long unitId = -1L;
    private BigInteger currentPhaseId;
    private BigInteger nextPhaseId;
    private List<PeriodPhaseFlippingDate> phaseFlippingDate = new ArrayList<>();

    public PlanningPeriod(){
        // default constructor
    }

    public PlanningPeriod(String name, LocalDate startDate, LocalDate endDate, Long unitId, List<PeriodPhaseFlippingDate> phaseFlippingDate, BigInteger currentPhaseId, BigInteger nextPhaseId) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unitId = unitId;
        this.phaseFlippingDate = phaseFlippingDate;
        this.currentPhaseId = currentPhaseId;
        this.nextPhaseId = nextPhaseId;
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

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public List<PeriodPhaseFlippingDate> getPhaseFlippingDate() {
        return phaseFlippingDate;
    }

    public void setPhaseFlippingDate(List<PeriodPhaseFlippingDate> phaseFlippingDate) {
        this.phaseFlippingDate = phaseFlippingDate;
    }

    public BigInteger getCurrentPhaseId() {
        return currentPhaseId;
    }

    public void setCurrentPhaseId(BigInteger currentPhaseId) {
        this.currentPhaseId = currentPhaseId;
    }

    public BigInteger getNextPhaseId() {
        return nextPhaseId;
    }

    public void setNextPhaseId(BigInteger nextPhaseId) {
        this.nextPhaseId = nextPhaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
