package com.kairos.persistence.model.agreement.cta;

import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_COMPENSATION_TABLE_INTERVAL;

@NodeEntity
public class CompensationTable extends UserBaseEntity {
    private int granularityLevel;
//    private CompensationMeasurementType compensationMeasurementType;
    @Relationship(type = HAS_COMPENSATION_TABLE_INTERVAL)
    private List<CompensationTableInterval>compensationTableInterval=new ArrayList<>();

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
