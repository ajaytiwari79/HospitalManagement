package com.kairos.activity.period;

import com.kairos.enums.phase.PhaseDefaultName;

import java.math.BigInteger;
import java.time.LocalDate;

public class PeriodPhaseDTO {
    private BigInteger id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String currentPhase;
    private String nextPhase;
    private String color;
    private PhaseDefaultName phaseEnum;

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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public PhaseDefaultName getPhaseEnum() {
        return phaseEnum;
    }

    public void setPhaseEnum(PhaseDefaultName phaseEnum) {
        this.phaseEnum = phaseEnum;
    }
}
