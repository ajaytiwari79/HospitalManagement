package com.kairos.persistence.model.user.agreement.cta;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;

import java.util.ArrayList;
import java.util.List;
@NodeEntity
public class CompensationTable extends UserBaseEntity {
    private int granularityLevel;
    private CompensationMeasurementType compensationMeasurementType;
    private List<CompensationTableInterval>compensationTableInterval=new ArrayList<>();

    public int getGranularityLevel() {
        return granularityLevel;
    }

    public void setGranularityLevel(int granularityLevel) {
        this.granularityLevel = granularityLevel;
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
