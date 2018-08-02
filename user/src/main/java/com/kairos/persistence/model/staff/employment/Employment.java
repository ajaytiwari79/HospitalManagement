package com.kairos.persistence.model.staff.employment;

import com.kairos.config.neo4j.converter.LocalDateConverter;
import com.kairos.enums.EmploymentStatus;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.country.reason_code.ReasonCode;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.permission.UnitPermission;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

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
    private Long endDateMillis;
    private Long startDateMillis;
    @Convert(LocalDateConverter.class)
    private LocalDate mainEmploymentStartDate;
    @Convert(LocalDateConverter.class)
    private LocalDate mainEmploymentEndDate;
    private boolean mainEmployment;
    public Long getAccessGroupIdOnEmploymentEnd() {
        return accessGroupIdOnEmploymentEnd;
    }

    public void setAccessGroupIdOnEmploymentEnd(Long accessGroupIdOnEmploymentEnd) {
        this.accessGroupIdOnEmploymentEnd = accessGroupIdOnEmploymentEnd;
    }

    private Long accessGroupIdOnEmploymentEnd;
    public ReasonCode getReasonCode() {
        return reasonCode;
    }

    public void setReasonCode(ReasonCode reasonCode) {
        this.reasonCode = reasonCode;
    }

    @Relationship(type = HAS_REASON_CODE)
    private ReasonCode reasonCode;

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

    public LocalDate getMainEmploymentStartDate() {
        return mainEmploymentStartDate;
    }

    public void setMainEmploymentStartDate(LocalDate mainEmploymentStartDate) {
        this.mainEmploymentStartDate = mainEmploymentStartDate;
    }

    public LocalDate getMainEmploymentEndDate() {
        return mainEmploymentEndDate;
    }

    public void setMainEmploymentEndDate(LocalDate mainEmploymentEndDate) {
        this.mainEmploymentEndDate = mainEmploymentEndDate;
    }

    public boolean isMainEmployment() {
        return mainEmployment;
    }

    public void setMainEmployment(boolean mainEmployment) {
        this.mainEmployment = mainEmployment;
    }

}
