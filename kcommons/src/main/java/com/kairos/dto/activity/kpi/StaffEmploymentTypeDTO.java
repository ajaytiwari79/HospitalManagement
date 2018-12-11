package com.kairos.dto.activity.kpi;

import java.util.List;

public class StaffEmploymentTypeDTO {
 public List<Long> unitIds;
 public List<Long> employmentTypeIds;
 public String date;

    public StaffEmploymentTypeDTO() {
    }

    public StaffEmploymentTypeDTO(List<Long> unitIds, List<Long> employmentTypeIds, String date) {
        this.unitIds = unitIds;
        this.employmentTypeIds = employmentTypeIds;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
