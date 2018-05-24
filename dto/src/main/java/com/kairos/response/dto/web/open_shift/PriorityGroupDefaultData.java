package com.kairos.response.dto.web.open_shift;

import com.kairos.response.dto.web.cta.EmploymentTypeDTO;

import java.util.List;

public class PriorityGroupDefaultData {
    private List<EmploymentTypeDTO> employmentTypes;

    public PriorityGroupDefaultData() {
        //Default Constructor
    }

    public PriorityGroupDefaultData(List<EmploymentTypeDTO> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<EmploymentTypeDTO> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentTypeDTO> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }
}
