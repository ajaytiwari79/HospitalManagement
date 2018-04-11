package com.kairos.persistence.model.user.staff;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.enums.EmploymentStatus;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.BELONGS_TO;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_UNIT_PERMISSIONS;

/**
 * Created by prabjot on 3/12/16.
 */
@NodeEntity
public class Employment extends UserBaseEntity {

    private String name;

    @Relationship(type = HAS_UNIT_PERMISSIONS)
    private List<UnitPermission> unitPermissions = new ArrayList<>();

    @Relationship(type = BELONGS_TO)
    private Staff staff;

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    private Long endDateMillis;
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

    public List<UnitPermission> getUnitPermissions() {
        return Optional.ofNullable(unitPermissions).orElse(new ArrayList<>());
    }

    public void setUnitPermissions(List<UnitPermission> unitPermissions) {
        this.unitPermissions = unitPermissions;
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
