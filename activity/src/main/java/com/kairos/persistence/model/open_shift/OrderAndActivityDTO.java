package com.kairos.persistence.model.open_shift;


import com.kairos.activity.activity.ActivityDTO;
import com.kairos.activity.counter.CounterDTO;

import java.util.List;

public class OrderAndActivityDTO {
    private List<ActivityDTO> activities;
    private List<Order> orders;
    private Integer minOpenShiftHours;
    private List<CounterDTO> counters;

    public OrderAndActivityDTO() {
        //Default Constructor
    }

    public List<ActivityDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
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
