package com.kairos.dto.activity.common;


import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.open_shift.OrderResponseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
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
    private List<ReasonCodeDTO> reasonCodeDTOS;
    private List<PresenceTypeDTO> plannedTypes;
}
