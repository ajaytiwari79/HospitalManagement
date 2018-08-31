package com.kairos.activity.period;

import java.math.BigInteger;
import java.time.LocalDate;

public class PeriodDTO {
    private BigInteger id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigInteger phaseId;
    private String currentPhaseName;
    private String nextPhaseName;
    private String phaseColor;
    private String phaseEnum;

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

    public String getCurrentPhaseName() {
        return currentPhaseName;
    }

    public void setCurrentPhaseName(String currentPhaseName) {
        this.currentPhaseName = currentPhaseName;
    }

    public String getNextPhaseName() {
        return nextPhaseName;
    }

    public void setNextPhaseName(String nextPhaseName) {
        this.nextPhaseName = nextPhaseName;
    }

    public String getPhaseColor() {
        return phaseColor;
    }

    public BigInteger getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(BigInteger phaseId) {
        this.phaseId = phaseId;
    }

    public void setPhaseColor(String phaseColor) {
        this.phaseColor = phaseColor;
    }

    public String getPhaseEnum() {
        return phaseEnum;
    }

    public void setPhaseEnum(String phaseEnum) {
        this.phaseEnum = phaseEnum;
    }
}
