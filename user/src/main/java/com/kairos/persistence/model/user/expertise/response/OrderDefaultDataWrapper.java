package com.kairos.persistence.model.user.expertise.response;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.open_shift.OrderResponseDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailQueryResult;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.skill.Skill;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderDefaultDataWrapper {
    private List<OrderResponseDTO> orders;
    private List<ActivityDTO> activities;
    private List<Skill> skills;
    private List<Expertise> expertise;
    private List<StaffPersonalDetailQueryResult> staffList;
    private List<PresenceTypeDTO> plannedTime;
    private List<FunctionDTO> functions;
    private List<ReasonCodeDTO> reasonCodes;
    private List<DayTypeDTO> dayTypes;
    private Integer minOpenShiftHours;
    private List<CounterDTO> counters;

    public OrderDefaultDataWrapper(List<OrderResponseDTO> orders, List<ActivityDTO> activities, List<Skill> skills, List<Expertise> expertise, List<StaffPersonalDetailQueryResult> staffList,
                                   List<PresenceTypeDTO> plannedTime, List<FunctionDTO> functions, List<ReasonCodeDTO> reasonCodes,
                                    Integer minOpenShiftHours, List<CounterDTO> counters) {
        this.orders = orders;
        this.activities = activities;
        this.skills = skills;
        this.expertise = expertise;
        this.staffList = staffList;
        this.plannedTime = plannedTime;
        this.functions = functions;
        this.reasonCodes = reasonCodes;
        this.minOpenShiftHours=minOpenShiftHours;
        this.counters=counters;
    }

}
