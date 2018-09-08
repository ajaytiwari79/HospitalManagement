package com.kairos.persistence.model.user.expertise.Response;


import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.counter.CounterDTO;
import com.kairos.dto.activity.open_shift.OrderResponseDTO;

import java.util.List;

public class OrderAndActivityDTO {
    private List<OrderResponseDTO> orders;
    private List<ActivityDTO> activities;
    private Integer minOpenShiftHours;
    private List<CounterDTO> counters;


    public OrderAndActivityDTO() {
        //Default Constructor
    }

    public List<OrderResponseDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponseDTO> orders) {
        this.orders = orders;
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

    public Integer getMinOpenShiftHours() {
        return minOpenShiftHours;
    }

    public void setMinOpenShiftHours(Integer minOpenShiftHours) {
        this.minOpenShiftHours = minOpenShiftHours;
    }

    public List<CounterDTO> getCounters() {
        return counters;
    }

    public void setCounters(List<CounterDTO> counters) {
        this.counters = counters;
    }
}
