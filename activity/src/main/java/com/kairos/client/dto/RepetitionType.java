package com.kairos.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by oodles on 26/4/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepetitionType {

    private List<Shifts> shifts;

    private String type;

    private int visits;

    public List<Shifts> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shifts> shifts) {
        this.shifts = shifts;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }


}
