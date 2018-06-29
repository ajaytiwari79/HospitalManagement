package com.kairos.persistence.model.open_shift;


import com.kairos.activity.activity.ActivityDTO;

import java.util.List;

public class OrderAndActivityDTO {
    private List<ActivityDTO> activities;
    private List<Order> orders;
    private Integer minOpenShiftHours;

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
}
