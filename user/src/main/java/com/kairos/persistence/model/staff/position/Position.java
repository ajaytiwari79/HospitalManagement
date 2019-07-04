package com.kairos.persistence.model.staff.position;

import com.kairos.enums.employment_type.EmploymentStatus;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import com.kairos.persistence.model.staff.personal_details.Staff;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.*;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

/**
 * Created by prabjot on 3/12/16.
 */
@NodeEntity
public class Position extends UserBaseEntity {

    private String name;

    @Relationship(type = HAS_UNIT_PERMISSIONS)
    private List<UnitPermission> unitPermissions = new ArrayList<>();

    @Relationship(type = BELONGS_TO)
    private Staff staff;
    private Long endDateMillis;
    private Long startDateMillis;
    private Long accessGroupIdOnPositionEnd;
    @Relationship(type = HAS_REASON_CODE)
    private ReasonCode reasonCode;

    public Position(){}

    public Position(String name, Staff staff) {
        this.name = name;
        this.staff = staff;
    }


    public Long getAccessGroupIdOnPositionEnd() {
        return accessGroupIdOnPositionEnd;
    }

    public void setAccessGroupIdOnPositionEnd(Long accessGroupIdOnPositionEnd) {
        this.accessGroupIdOnPositionEnd = accessGroupIdOnPositionEnd;
    }

    public ReasonCode getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(ReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }

    public Long getStartDateMillis() {
        return startDateMillis;
    }

    public void setStartDateMillis(Long startDateMillis) {
        this.startDateMillis = startDateMillis;
    }

    public Long getEndDateMillis() {
        return endDateMillis;
    }

    public void setEndDateMillis(Long endDateMillis) {
        this.endDateMillis = endDateMillis;
    }

    private EmploymentStatus employmentStatus = EmploymentStatus.PENDING;

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
}
