package com.kairos.dto.activity.cta;

import com.kairos.dto.user.country.agreement.cta.CompensationMeasurementType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Getter
@Setter
@NoArgsConstructor
public class CompensationTable {
    private int granularityLevel;
    private List<CompensationTableInterval> compensationTableInterval=new ArrayList<>();
    //use for protected days off calculation
    private CompensationMeasurementType unusedDaysOffType;
    private float unusedDaysOffvalue;



    public CompensationTable(int granularityLevel) {
        this.granularityLevel = granularityLevel;
    }

    public CompensationTable(int granularityLevel, List<CompensationTableInterval> compensationTableIntervals) {
        this.granularityLevel = granularityLevel;
        this.setCompensationTableInterval(compensationTableIntervals);
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
