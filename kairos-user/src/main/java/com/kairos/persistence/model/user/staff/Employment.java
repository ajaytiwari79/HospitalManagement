package com.kairos.persistence.model.user.staff;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.kairos.persistence.EmploymentStatus;
import com.kairos.persistence.model.common.UserBaseEntity;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 3/12/16.
 */
@NodeEntity
public class Employment extends UserBaseEntity {

    private String name;

    @Relationship(type = HAS_UNIT_EMPLOYMENTS)
    private List<UnitEmployment> unitEmployments = new ArrayList<>();

    @Relationship(type = BELONGS_TO)
    private Staff staff;

    private EmploymentStatus employmentStatus = EmploymentStatus.PENDING;

    public Employment(){}

    public String getName() {
        return name;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public List<UnitEmployment> getUnitEmployments() {
        return unitEmployments;
    }

    public void setUnitEmployments(List<UnitEmployment> unitEmployments) {
        this.unitEmployments = unitEmployments;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public Employment(String name, Staff staff) {
        this.name = name;
        this.staff = staff;
    }
}
