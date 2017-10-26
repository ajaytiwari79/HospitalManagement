package com.kairos.persistence.model.user.resources;

import java.util.List;

/**
 * Created by prabjot on 25/10/17.
 */
public class ResourceUnavailabilityDTO {

    private List<String> unavailabilityDates;
    private String startTime;
    private String endTime;
    private boolean fullDay;

    public List<String> getUnavailabilityDates() {
        return unavailabilityDates;
    }

    public void setUnavailabilityDates(List<String> unavailabilityDates) {
        this.unavailabilityDates = unavailabilityDates;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isFullDay() {
        return fullDay;
    }

    public void setFullDay(boolean fullDay) {
        this.fullDay = fullDay;
    }
}
