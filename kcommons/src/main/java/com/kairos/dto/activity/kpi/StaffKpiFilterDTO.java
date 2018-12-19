package com.kairos.dto.activity.kpi;

import com.kairos.dto.activity.time_bank.UnitPositionWithCtaDetailsDTO;

import java.time.LocalDate;
import java.util.List;

public class StaffKpiFilterDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private List<UnitPositionWithCtaDetailsDTO> unitPositionWithCtaDetailsDTOS;


    public List<UnitPositionWithCtaDetailsDTO> getUnitPositionWithCtaDetailsDTOS() {
        return unitPositionWithCtaDetailsDTOS;
    }

    public void setUnitPositionWithCtaDetailsDTOS(List<UnitPositionWithCtaDetailsDTO> unitPositionWithCtaDetailsDTOS) {
        this.unitPositionWithCtaDetailsDTOS = unitPositionWithCtaDetailsDTOS;
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
}
