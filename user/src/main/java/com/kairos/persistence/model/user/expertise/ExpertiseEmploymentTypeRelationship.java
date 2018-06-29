package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import java.math.BigInteger;

import static com.kairos.persistence.model.constants.RelationshipConstants.EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT;

@RelationshipEntity(type = EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT)
public class ExpertiseEmploymentTypeRelationship extends UserBaseEntity {
    @StartNode
    private Expertise expertise;
    @EndNode
    private EmploymentType employmentType;
    public BigInteger includedPlannedTime;
    public BigInteger excludedPlannedTime;

    public ExpertiseEmploymentTypeRelationship() {
        //DC
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public BigInteger getIncludedPlannedTime() {
        return includedPlannedTime;
    }

    public void setIncludedPlannedTime(BigInteger includedPlannedTime) {
        this.includedPlannedTime = includedPlannedTime;
    }

    public BigInteger getExcludedPlannedTime() {
        return excludedPlannedTime;
    }

    public void setExcludedPlannedTime(BigInteger excludedPlannedTime) {
        this.excludedPlannedTime = excludedPlannedTime;
    }

    public ExpertiseEmploymentTypeRelationship(Expertise expertise, EmploymentType employmentType, BigInteger includedPlannedTime, BigInteger excludedPlannedTime) {
        this.expertise = expertise;
        this.employmentType = employmentType;
        this.includedPlannedTime = includedPlannedTime;
        this.excludedPlannedTime = excludedPlannedTime;
    }
}
