package com.kairos.response.dto.web.day_type;

import com.kairos.response.dto.web.cta.EmploymentTypeDTO;

import java.util.List;

public class DayTypeEmploymentWrapper {
    private List<DayTypesDTO> dayTypes;
    private List<EmploymentTypeDTO> employmentTypes;

    public DayTypeEmploymentWrapper() {
        //Default Constructor
    }

    public DayTypeEmploymentWrapper(List<DayTypesDTO> dayTypes, List<EmploymentTypeDTO> employmentTypes) {
        this.dayTypes = dayTypes;
        this.employmentTypes = employmentTypes;
    }

    public List<DayTypesDTO> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayTypesDTO> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public List<EmploymentTypeDTO> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentTypeDTO> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }
}
