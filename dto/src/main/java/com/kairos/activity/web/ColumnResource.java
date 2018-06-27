package com.kairos.activity.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by oodles on 20/7/17.
 */
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
