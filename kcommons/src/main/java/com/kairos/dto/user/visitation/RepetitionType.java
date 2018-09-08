package com.kairos.dto.user.visitation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.user.organization.Shifts;

import java.util.List;

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
