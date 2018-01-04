package com.kairos.response.dto.web;

import com.kairos.persistence.model.user.position.Position;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Created by pawanmandhan on 27/7/17.
 */
public class PositionDTO {

    @NotNull(message = "Position Name  is required for position") @Range(min = 0,message = "Position Name is required for position")
    private Long positionNameId;
    @NotNull(message = "expertise is required for position")
    @Range(min = 0,message = "expertise is required for position")
    private Long expertiseId;

    private Long startDate;
    private Long endDate;
    private float totalWeeklyHours;
    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyWages;
    private float salary;
    private Long employmentTypeId;
//    private Position.EmploymentType employmentType;

    @NotNull(message = "staffId is missing")
    @Range(min = 0, message = "staffId is missing")
    private Long staffId;
    // private Long expiryDate;


    public PositionDTO() {
        //default cons
    }


    public PositionDTO(Long positionNameId, Long expertiseId) {
        this.positionNameId = positionNameId;
        this.expertiseId = expertiseId;
    }



    public PositionDTO(String name, String description, Long positionNameId, Long expertiseId, Long startDate, Long endDate, int totalWeeklyHours,
                       float avgDailyWorkingHours, float hourlyWages, float salary, Long employmentTypeId) {
        this.salary=salary;
        this.avgDailyWorkingHours =avgDailyWorkingHours;
        this.totalWeeklyHours=totalWeeklyHours;
        this.hourlyWages=hourlyWages;
        this.positionNameId = positionNameId;
        this.expertiseId = expertiseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employmentTypeId=employmentTypeId;
    }

    public PositionDTO(Long positionNameId, Long expertiseId, Long startDate, Long endDate, int totalWeeklyHours, float avgDailyWorkingHours, int workingDaysInWeek, float hourlyWages, float salary, Long employmentTypeId, Long staffId) {
        this.positionNameId = positionNameId;
        this.expertiseId = expertiseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalWeeklyHours = totalWeeklyHours;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.workingDaysInWeek = workingDaysInWeek;
        this.hourlyWages = hourlyWages;
        this.salary = salary;
        this.employmentTypeId = employmentTypeId;
        this.staffId = staffId;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public Long getPositionNameId() {
        return positionNameId;
    }

    public void setPositionNameId(Long positionNameId) {
        this.positionNameId = positionNameId;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
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

    /*public Position.EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(Position.EmploymentType employmentType) {
        this.employmentType = employmentType;
    }*/

    public long getEmploymentTypeId() {
        return employmentTypeId;
    }

    public void setEmploymentTypeId(long employmentTypeId) {
        this.employmentTypeId = employmentTypeId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }
}
