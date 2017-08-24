package com.kairos.response.dto.web;

import com.kairos.persistence.model.user.position.Position;

/**
 * Created by pawanmandhan on 27/7/17.
 */
public class PositionDTO {


    private Long positionNameId;
    private Long expertiseId;
    private Long startDate;
    private Long endDate;
    private int totalWeeklyHours;
    private float avgMonthlyWorkingHours;
    private float hourlyWages;

    private float salary;
    private Position.EmploymentType employmentType;
    //private Long staffId;
    // private Long expiryDate;


    public PositionDTO() {

    }


    public PositionDTO( Long positionNameId, Long expertiseId) {
        this.positionNameId = positionNameId;
        this.expertiseId = expertiseId;
    }



    public PositionDTO(String name, String description, Long positionNameId, Long expertiseId,Long startDate, Long endDate, int totalWeeklyHours,
                       float avgMonthlyWorkingHours, float hourlyWages, float salary,Position.EmploymentType employmentType) {
        this.salary=salary;
        this.avgMonthlyWorkingHours=avgMonthlyWorkingHours;
        this.totalWeeklyHours=totalWeeklyHours;
        this.hourlyWages=hourlyWages;
        this.positionNameId = positionNameId;
        this.expertiseId = expertiseId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employmentType=employmentType;
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

    public int getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(int totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public float getAvgMonthlyWorkingHours() {
        return avgMonthlyWorkingHours;
    }

    public void setAvgMonthlyWorkingHours(float avgMonthlyWorkingHours) {
        this.avgMonthlyWorkingHours = avgMonthlyWorkingHours;
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

    public Position.EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(Position.EmploymentType employmentType) {
        this.employmentType = employmentType;
    }
}
