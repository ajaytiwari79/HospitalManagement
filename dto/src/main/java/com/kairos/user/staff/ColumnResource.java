package com.kairos.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ColumnResource {
    private List<KMDShift> shifts;

    public List<KMDShift> getShifts() {
        return shifts;
    }

    public void setShifts(List<KMDShift> shifts) {
        this.shifts = shifts;
    }
}