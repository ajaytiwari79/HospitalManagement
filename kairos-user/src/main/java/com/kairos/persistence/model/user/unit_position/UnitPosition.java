package com.kairos.persistence.model.user.unit_position;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.position_code.PositionCode;
import com.kairos.persistence.model.user.staff.Staff;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by pawanmandhan on 24/7/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class UnitPosition extends UserBaseEntity {

    @Relationship(type = HAS_EXPERTISE_IN)
    private Expertise expertise;

    @Relationship(type = HAS_CTA)
    private CostTimeAgreement cta;

    @Relationship(type = HAS_WTA)
    private WorkingTimeAgreement workingTimeAgreement;

    @Relationship(type = HAS_POSITION_CODE)
    private PositionCode positionCode;

    @Relationship(type = BELONGS_TO_STAFF, direction = "INCOMING")
    private Staff staff;


    @Relationship(type = HAS_EMPLOYMENT_TYPE)
    private EmploymentType employmentType;

    @Relationship(type = STAFF_BELONGS_TO_UNION)
    private Organization union;

    @Relationship(type = UNIT_POSITION_BELONGS_TO_UNIT)
    private Organization unit;


    private Long startDateMillis;
    private Long endDateMillis;
    private Long lastWorkingDateMillis;
    private int totalWeeklyMinutes;

    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyWages;

    private float salary;

    public UnitPosition() {
    }


    public int getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public float getAvgDailyWorkingHours() {
        return avgDailyWorkingHours;
    }

    public void setAvgDailyWorkingHours(float avgDailyWorkingHours) {
        this.avgDailyWorkingHours = avgDailyWorkingHours;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public float getHourlyWages() {
        return hourlyWages;
    }

    public void setHourlyWages(float hourlyWages) {
        this.hourlyWages = hourlyWages;
    }

    public float getSalary() {
        return salary;
    }

    public void setSalary(float salary) {
        this.salary = salary;
    }

    public PositionCode getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(PositionCode positionCode) {
        this.positionCode = positionCode;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public CostTimeAgreement getCta() {
        return cta;
    }

    public void setCta(CostTimeAgreement cta) {
        this.cta = cta;
    }

    public WorkingTimeAgreement getWorkingTimeAgreement() {
        return workingTimeAgreement;
    }

    public void setWorkingTimeAgreement(WorkingTimeAgreement workingTimeAgreement) {
        this.workingTimeAgreement = workingTimeAgreement;
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

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }


    public UnitPositionQueryResult getBasicDetails() {
        UnitPositionQueryResult result = null;
        result = new UnitPositionQueryResult(this.expertise.retrieveBasicDetails(), this.startDateMillis, this.workingDaysInWeek,
                this.endDateMillis, this.totalWeeklyMinutes,
                this.avgDailyWorkingHours, this.hourlyWages, this.id, this.employmentType, this.salary, this.positionCode, this.union,this.lastWorkingDateMillis);
        return result;
    }

    public UnitPosition(Long startDateMillis, Long endDateMillis, int totalWeeklyMinutes, float avgDailyWorkingHours, int workingDaysInWeek, float hourlyWages, float salary) {
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.workingDaysInWeek = workingDaysInWeek;
        this.hourlyWages = hourlyWages;
        this.salary = salary;
    }

    public Organization getUnion() {
        return union;
    }

    public void setUnion(Organization union) {
        this.union = union;
    }

    public Long getLastWorkingDateMillis() {
        return lastWorkingDateMillis;
    }

    public void setLastWorkingDateMillis(Long lastWorkingDateMillis) {
        this.lastWorkingDateMillis = lastWorkingDateMillis;
    }

    public Organization getUnit() {
        return unit;
    }

    public void setUnit(Organization unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("UnitEmploymentPosition{");
        sb.append("startDateMillis=").append(startDateMillis);
        sb.append(", endDateMillis=").append(endDateMillis);
        sb.append(", totalWeeklyMinutes=").append(totalWeeklyMinutes);
        sb.append('}');
        return sb.toString();
    }
}
