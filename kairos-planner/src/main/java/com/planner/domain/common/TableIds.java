package com.planner.domain.common;


import org.springframework.data.cassandra.core.mapping.PrimaryKey;

public class TableIds {

    @PrimaryKey
    private long ids;
    private Long taskIds;
    private Long citizenIds;
    private Long staffIds;
    private Long shiftIds;
    private Long skillIds;
    private Long vehiclesIds;
    private Long planningIds;
    private Long planningConfigIds;
    private Long taskTypeIds;
    private Long locationIds;
    private Long locationDistanceIds;


    public long getIds() {
        return ids;
    }

    public void setIds(long ids) {
        this.ids = ids;
    }

    public Long getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(Long taskIds) {
        this.taskIds = taskIds;
    }

    public Long getCitizenIds() {
        return citizenIds;
    }

    public void setCitizenIds(Long citizenIds) {
        this.citizenIds = citizenIds;
    }

    public Long getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(Long staffIds) {
        this.staffIds = staffIds;
    }

    public Long getShiftIds() {
        return shiftIds;
    }

    public void setShiftIds(Long shiftIds) {
        this.shiftIds = shiftIds;
    }

    public Long getSkillIds() {
        return skillIds;
    }

    public void setSkillIds(Long skillIds) {
        this.skillIds = skillIds;
    }

    public Long getVehiclesIds() {
        return vehiclesIds;
    }

    public void setVehiclesIds(Long vehiclesIds) {
        this.vehiclesIds = vehiclesIds;
    }

    public Long getPlanningIds() {
        return planningIds;
    }

    public void setPlanningIds(Long planningIds) {
        this.planningIds = planningIds;
    }

    public Long getPlanningConfigIds() {
        return planningConfigIds;
    }

    public void setPlanningConfigIds(Long planningConfigIds) {
        this.planningConfigIds = planningConfigIds;
    }

    public Long getTaskTypeIds() {
        return taskTypeIds;
    }

    public void setTaskTypeIds(Long taskTypeIds) {
        this.taskTypeIds = taskTypeIds;
    }

    public Long getLocationIds() {
        return locationIds;
    }

    public void setLocationIds(Long locationIds) {
        this.locationIds = locationIds;
    }

    public Long getLocationDistanceIds() {
        return locationDistanceIds;
    }

    public void setLocationDistanceIds(Long locationDistanceIds) {
        this.locationDistanceIds = locationDistanceIds;
    }
}
