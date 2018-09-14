package com.kairos.dto.user.country.day_type;

import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;

import java.util.List;

public class DayTypeEmploymentTypeWrapper {
    private List<DayType> dayTypes;
    private List<EmploymentTypeDTO> employmentTypes;

    public DayTypeEmploymentTypeWrapper() {
        //Default Constructor
    }

    public DayTypeEmploymentTypeWrapper(List<DayType> dayTypes, List<EmploymentTypeDTO> employmentTypes) {
        this.dayTypes = dayTypes;
        this.employmentTypes = employmentTypes;
    }

    public List<DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public List<EmploymentTypeDTO> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentTypeDTO> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }
}
