package com.kairos.dto.activity.kpi;

import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;

import java.util.List;

public class StaffKpiFilterDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private List<Long> unitIds;
    private Long unitId;
    private String unitName;
    private List<EmploymentWithCtaDetailsDTO> employment;
    private List<DayTypeDTO> dayTypeDTOS;


    public List<EmploymentWithCtaDetailsDTO> getEmployment() {
        return employment;
    }

    public void setEmployment(List<EmploymentWithCtaDetailsDTO> employment) {
        this.employment = employment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName(){
        return this.firstName+" "+this.getLastName();
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public List<Long> getUnitIds() {
        return unitIds;
    }

    public void setUnitIds(List<Long> unitIds) {
        this.unitIds = unitIds;
    }

    public List<DayTypeDTO> getDayTypeDTOS() {
        return dayTypeDTOS;
    }

    public void setDayTypeDTOS(List<DayTypeDTO> dayTypeDTOS) {
        this.dayTypeDTOS = dayTypeDTOS;
    }
}
