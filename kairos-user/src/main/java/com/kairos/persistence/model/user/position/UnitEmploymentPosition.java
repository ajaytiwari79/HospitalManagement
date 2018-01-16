package com.kairos.persistence.model.user.position;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.country.EmploymentType;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.staff.Staff;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by pawanmandhan on 24/7/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class UnitEmploymentPosition extends UserBaseEntity {

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

    private Long startDateMillis;
    private Long endDateMillis;
    private float totalWeeklyHours;
    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyWages;

    private float salary;

    public UnitEmploymentPosition() {
    }


    public UnitEmploymentPosition(Expertise expertise, CostTimeAgreement cta, WorkingTimeAgreement wta,
                                  PositionCode positionCode, String description, Long startDateMillis, Long endDateMillis, Long expiryDate
            , int totalWeeklyHours, float avgDailyWorkingHours, float hourlyWages, float salary, int workingDaysInWeek) {


        this.expertise = expertise;
        this.cta = cta;
        this.workingTimeAgreement = workingTimeAgreement;
        this.positionCode = positionCode;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.totalWeeklyHours = totalWeeklyHours;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.salary = salary;
        this.hourlyWages = hourlyWages;
        this.workingDaysInWeek = workingDaysInWeek;
    }


    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public float getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(float totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public float getAvgDailyWorkingHours() {
        return avgDailyWorkingHours;
    }

    public void setAvgDailyWorkingHours(float avgDailyWorkingHours) {
        this.avgDailyWorkingHours = avgDailyWorkingHours;
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


    public UnitEmploymentPosition(Expertise expertise, CostTimeAgreement cta, WorkingTimeAgreement wta, PositionCode positionCode, Staff staff, boolean deleted, Long startDateMillis, Long endDateMillis, int totalWeeklyHours, float avgDailyWorkingHours, int workingDaysInWeek, float hourlyWages, EmploymentType employmentType, float salary) {
        this.expertise = expertise;
        this.cta = cta;
        this.workingTimeAgreement = workingTimeAgreement;
        this.positionCode = positionCode;
        this.staff = staff;
        this.deleted = deleted;
        this.startDateMillis = startDateMillis;
        this.endDateMillis = endDateMillis;
        this.totalWeeklyHours = totalWeeklyHours;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.workingDaysInWeek = workingDaysInWeek;
        this.hourlyWages = hourlyWages;
        this.employmentType = employmentType;
        this.salary = salary;
    }


    public UnitEmploymentPositionQueryResult getBasicDetails() {
        UnitEmploymentPositionQueryResult result = new UnitEmploymentPositionQueryResult(this.expertise.retrieveBasicDetails(), this.startDateMillis, this.workingDaysInWeek, this.endDateMillis, this.totalWeeklyHours,
                this.avgDailyWorkingHours, this.hourlyWages, this.id, this.employmentType, this.salary, this.positionCode, this.workingTimeAgreement.basicDetails(), this.cta);
        return result;
    }
}
