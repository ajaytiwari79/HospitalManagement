package com.kairos.persistence.model.access_permission;
/*
 *Created By Pavan on 30/8/18
 *
 */

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class StaffAccessGroupQueryResult {

    private Long staffId;
    private Long countryId;
    private boolean isCountryAdmin;
    private List<Long> accessGroupIds;
    private boolean staff;
    private boolean management;
    private Long unitId;
    private boolean hasPermission;

    public StaffAccessGroupQueryResult() {
        //Default Constructor
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public boolean isCountryAdmin() {
        return isCountryAdmin;
    }

    public void setCountryAdmin(boolean countryAdmin) {
        isCountryAdmin = countryAdmin;
    }

    public boolean isStaff() {
        return staff;
    }

    public void setStaff(boolean staff) {
        this.staff = staff;
    }

    public boolean isManagement() {
        return management;
    }

    public void setManagement(boolean management) {
        this.management = management;
    }

    public List<Long> getAccessGroupIds() {
        return accessGroupIds;
    }

    public void setAccessGroupIds(List<Long> accessGroupIds) {
        this.accessGroupIds = accessGroupIds;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public boolean isHasPermission() {
        return hasPermission;
    }

    public void setHasPermission(boolean hasPermission) {
        this.hasPermission = hasPermission;
    }
}
