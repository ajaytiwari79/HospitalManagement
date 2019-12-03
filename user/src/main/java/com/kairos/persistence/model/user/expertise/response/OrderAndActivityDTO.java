package com.kairos.persistence.model.user.expertise.response;


import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.open_shift.OrderResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class OrderAndActivityDTO {
    private List<OrderResponseDTO> orders;
    private List<ActivityDTO> activities;
    private Integer minOpenShiftHours;
    private List<CounterDTO> counters;
}
