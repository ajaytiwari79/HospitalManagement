package com.kairos.persistence.model.user.unit_position;

import com.kairos.enums.EmploymentCategory;
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
public class UnitPositionEmploymentTypeRelationShip extends UserBaseEntity {


    @StartNode
    private PositionLine positionLine;
    @EndNode
    private EmploymentType employmentType;
    @Property
    private EmploymentCategory employmentTypeCategory;

    public PositionLine getPositionLine() {
        return positionLine;
    }

    public void setPositionLine(PositionLine positionLine) {
        this.positionLine = positionLine;
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

    public UnitPositionEmploymentTypeRelationShip() {

    }

    public UnitPositionEmploymentTypeRelationShip(PositionLine positionLine, EmploymentType employmentType, EmploymentCategory employmentTypeCategory) {
        this.positionLine=positionLine;
        this.employmentType = employmentType;
        this.employmentTypeCategory = employmentTypeCategory;
    }
}