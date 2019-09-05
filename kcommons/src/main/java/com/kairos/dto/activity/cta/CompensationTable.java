package com.kairos.dto.activity.cta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompensationTable {
    private int granularityLevel;
    private List<CompensationTableInterval> compensationTableInterval=new ArrayList<>();

    public CompensationTable() {
    }

    public CompensationTable(int granularityLevel) {
        this.granularityLevel = granularityLevel;
    }

    public CompensationTable(int granularityLevel, List<CompensationTableInterval> compensationTableIntervals) {
        this.granularityLevel = granularityLevel;
        this.setCompensationTableInterval(compensationTableIntervals);
    }

    public int getGranularityLevel() {
        return granularityLevel;
    }

    public void setGranularityLevel(int granularityLevel) {
        this.granularityLevel = granularityLevel;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompensationTable)) return false;
        CompensationTable that = (CompensationTable) o;
        return granularityLevel == that.granularityLevel &&
                Objects.equals(compensationTableInterval, that.compensationTableInterval);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(granularityLevel, compensationTableInterval);
    }
}
