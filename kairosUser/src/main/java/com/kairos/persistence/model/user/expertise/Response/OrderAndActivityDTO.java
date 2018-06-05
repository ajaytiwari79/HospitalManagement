package com.kairos.persistence.model.user.expertise.Response;

import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.response.dto.web.open_shift.OrderResponseDTO;

import java.util.List;

public class OrderAndActivityDTO {
    private List<OrderResponseDTO> orders;
    private List<ActivityDTO> activities;

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
}
