package com.kairos.response.dto.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by oodles on 4/5/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShiftRepetition {
    private List<Shifts> shifts;

    public List<Shifts> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shifts> shifts) {
        this.shifts = shifts;
    }

}
