package com.kairos.activity.cta;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CompensationTable {
    private int granularityLevel;
//    private CompensationMeasurementType compensationMeasurementType;
    private List<CompensationTableInterval> compensationTableInterval=new ArrayList<>();

    public CompensationTable() {
        //default
    }

    public CompensationTable(int granularityLevel) {
        this.granularityLevel = granularityLevel;
//        this.compensationMeasurementType = compensationMeasurementType;
    }

    public CompensationTable(int granularityLevel, List<CompensationTableInterval> compensationTableIntervals) {
        this.granularityLevel = granularityLevel;
        this.setCompensationTableInterval(compensationTableIntervals);
//        this.compensationMeasurementType = compensationMeasurementType;
    }

    public int getGranularityLevel() {
        return granularityLevel;
    }

    public void setGranularityLevel(int granularityLevel) {
        this.granularityLevel = granularityLevel;
    }

    /*public CompensationMeasurementType getCompensationMeasurementType() {
        return compensationMeasurementType;
    }*/

    /*public void setCompensationMeasurementType(CompensationMeasurementType compensationMeasurementType) {
        this.compensationMeasurementType = compensationMeasurementType;
    }*/

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
