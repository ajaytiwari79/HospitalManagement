package com.kairos.persistence.model.user.agreement.cta;

import java.util.ArrayList;
import java.util.List;

public class CompensationTable {
    private int GranularityLevel;
    private CompensationMeasurementType compensationMeasurementType;
    private List<CompensationTableInterval>compensationTableInterval=new ArrayList<>();

    public int getGranularityLevel() {
        return GranularityLevel;
    }

    public void setGranularityLevel(int granularityLevel) {
        GranularityLevel = granularityLevel;
    }

    public CompensationMeasurementType getCompensationMeasurementType() {
        return compensationMeasurementType;
    }

    public void setCompensationMeasurementType(CompensationMeasurementType compensationMeasurementType) {
        this.compensationMeasurementType = compensationMeasurementType;
    }

    public List<CompensationTableInterval> getCompensationTableInterval() {
        return compensationTableInterval;
    }

    public void setCompensationTableInterval(List<CompensationTableInterval> compensationTableInterval) {
        this.compensationTableInterval = compensationTableInterval;
    }

    public void addCompensationTableInterval(CompensationTableInterval compensationTableInterval) {
        if (compensationTableInterval == null)
            throw new NullPointerException("Can't add null compensationTableInterval");
         getCompensationTableInterval().add(compensationTableInterval);
    }
}
