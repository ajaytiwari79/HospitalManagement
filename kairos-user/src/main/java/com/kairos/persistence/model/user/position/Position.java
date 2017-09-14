package com.kairos.persistence.model.user.position;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.user.agreement.cta.CostTimeAgreement;
import com.kairos.persistence.model.user.agreement.wta.WorkingTimeAgreement;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;


/**
 * Created by pawanmandhan on 24/7/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NodeEntity
public class Position extends UserBaseEntity {

    @Relationship(type = HAS_EXPERTISE_IN)
    private Expertise expertise;

    @Relationship(type = HAS_CTA)
    private CostTimeAgreement cta;

    @Relationship(type = HAS_WTA)
    private WorkingTimeAgreement wta;

    @Relationship(type = HAS_POSITION_NAME)
    private PositionName positionName;

    private boolean isEnabled = true;
    private Long startDate;
    private Long endDate;
    private int totalWeeklyHours;
    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyWages;
    public enum EmploymentType{
        FULL_TIME,PART_TIME
    };

    private EmploymentType employmentType;
    private float salary;

    public Position() {
    }

    public Position( Expertise expertise, CostTimeAgreement cta, WorkingTimeAgreement wta,
                    PositionName positionName, String description, Long startDate, Long endDate, Long expiryDate
                    ,int totalWeeklyHours ,float avgDailyWorkingHours,float hourlyWages,float salary,int workingDaysInWeek) {

        this.expertise = expertise;
        this.cta = cta;
        this.wta = wta;
        this.positionName = positionName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalWeeklyHours=totalWeeklyHours;
        this.avgDailyWorkingHours =avgDailyWorkingHours;
        this.salary=salary;
        this.hourlyWages=hourlyWages;
        this.workingDaysInWeek=workingDaysInWeek;
    }


    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public int getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(int totalWeeklyHours) {
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

    public PositionName getPositionName() {
        return positionName;
    }

    public void setPositionName(PositionName positionName) {
        this.positionName = positionName;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
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

    public WorkingTimeAgreement getWta() {
        return wta;
    }

    public void setWta(WorkingTimeAgreement wta) {
        this.wta = wta;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(EmploymentType employmentType) {
        this.employmentType = employmentType;
    }
}
