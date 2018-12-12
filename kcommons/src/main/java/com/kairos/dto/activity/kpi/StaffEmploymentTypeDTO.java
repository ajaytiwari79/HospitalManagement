package com.kairos.dto.activity.kpi;

import java.util.List;

public class StaffEmploymentTypeDTO {
    private List<Long> staffIds;
 private List<Long> unitIds;
    private List<Long> employmentTypeIds;
    private Long organizationId;
    private String startDate;
    private String endDate;

    public StaffEmploymentTypeDTO() {
    }

    public StaffEmploymentTypeDTO(List<Long> unitIds, List<Long> employmentTypeIds) {
        this.unitIds = unitIds;
        this.employmentTypeIds = employmentTypeIds;

    }

    public StaffEmploymentTypeDTO(List<Long> staffIds,List<Long> unitIds, List<Long> employmentTypeIds, Long organizationId, String startDate,String endDate) {
        this.staffIds=staffIds;
        this.unitIds = unitIds;
        this.employmentTypeIds = employmentTypeIds;
        this.organizationId=organizationId;
        this.startDate=startDate;
        this.endDate=endDate;

    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }

    public List<Long> getEmploymentTypeIds() {
        return employmentTypeIds;
    }

    public void setEmploymentTypeIds(List<Long> employmentTypeIds) {
        this.employmentTypeIds = employmentTypeIds;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Long> getStaffIds() {
        return staffIds;
    }

    public void setStaffIds(List<Long> staffIds) {
        this.staffIds = staffIds;
    }
}
