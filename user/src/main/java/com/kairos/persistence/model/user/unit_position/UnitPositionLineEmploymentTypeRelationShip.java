package com.kairos.persistence.model.user.unit_position;

import com.kairos.enums.employment_type.EmploymentCategory;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.employment_type.EmploymentType;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_EMPLOYMENT_TYPE;


/**
 * Created by vipul on 6/4/18.
 */
@RelationshipEntity(type = HAS_EMPLOYMENT_TYPE)
public class UnitPositionLineEmploymentTypeRelationShip extends UserBaseEntity {


    @StartNode
    private UnitPositionLine unitPositionLine;
    @EndNode
    private EmploymentType employmentType;
    @Property
    private EmploymentCategory employmentTypeCategory;

    public UnitPositionLine getUnitPositionLine() {
        return unitPositionLine;
    }

    public void setUnitPositionLine(UnitPositionLine unitPositionLine) {
        this.unitPositionLine = unitPositionLine;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public EmploymentCategory getEmploymentTypeCategory() {
        return employmentTypeCategory;
    }

    public void setEmploymentTypeCategory(EmploymentCategory employmentTypeCategory) {
        this.employmentTypeCategory = employmentTypeCategory;
    }

    public UnitPositionLineEmploymentTypeRelationShip() {

    }

    public UnitPositionLineEmploymentTypeRelationShip(UnitPositionLine unitPositionLine, EmploymentType employmentType, EmploymentCategory employmentTypeCategory) {
        this.unitPositionLine = unitPositionLine;
        this.employmentType = employmentType;
        this.employmentTypeCategory = employmentTypeCategory;
    }
}