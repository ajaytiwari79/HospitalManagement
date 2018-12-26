package com.kairos.dto.activity.kpi;

import com.kairos.dto.activity.time_bank.UnitPositionWithCtaDetailsDTO;

import java.util.List;

public class StaffKpiFilterDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private List<UnitPositionWithCtaDetailsDTO> unitPosition;


    public List<UnitPositionWithCtaDetailsDTO> getUnitPosition() {
        return unitPosition;
    }

    public void setUnitPosition(List<UnitPositionWithCtaDetailsDTO> unitPosition) {
        this.unitPosition = unitPosition;
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