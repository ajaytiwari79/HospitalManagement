package com.kairos.dto.user.organization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by anil on 28/7/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSlot {

    private String name;
    private Long id;
    private boolean systemGeneratedTimeSlots;

    public boolean isSystemGeneratedTimeSlots() {
        return systemGeneratedTimeSlots;
    }

    public void setSystemGeneratedTimeSlots(boolean systemGeneratedTimeSlots) {
        this.systemGeneratedTimeSlots = systemGeneratedTimeSlots;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
