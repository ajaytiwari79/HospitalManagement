package com.kairos.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ColumnResource {
    private List<ImportShiftDTO> shifts;

    public List<ImportShiftDTO> getShifts() {
        return shifts;
    }

    public void setShifts(List<ImportShiftDTO> shifts) {
        this.shifts = shifts;
    }
}