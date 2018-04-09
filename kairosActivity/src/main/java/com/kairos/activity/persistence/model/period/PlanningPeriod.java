package com.kairos.activity.persistence.model.period;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.client.dto.Phase.PhaseDTO;
import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Created by prerna on 6/4/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningPeriod extends MongoBaseEntity {

    private Date startDate;
    private Date endDate;
    private String name;
    @Indexed
    private Long unitId = -1L;
    private BigInteger currentPhaseId;
    private BigInteger nextPhaseId;
    private List<PeriodPhaseFlippingDate> phaseFlippingDate = new ArrayList<>();

    public PlanningPeriod(){
        // default constructor
    }

    public PlanningPeriod(String name, Date startDate, Date endDate, Long unitId, List<PeriodPhaseFlippingDate> phaseFlippingDate, List<PhaseDTO> applicablePhases) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unitId = unitId;
        this.phaseFlippingDate = phaseFlippingDate;
        if(Optional.ofNullable(applicablePhases).isPresent()){
            List<PeriodPhaseFlippingDate> tempPhaseFlippingDate = new ArrayList<>();
            Date tempEndDate = endDate;
            applicablePhases.forEach(phase->{
                // TODO Check if duration of period is enough to assign next flipping
                // TODO Calculate flipping date by duration
                PeriodPhaseFlippingDate periodPhaseFlippingDate = new PeriodPhaseFlippingDate(phase.getId(), endDate);

            });
        }
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
