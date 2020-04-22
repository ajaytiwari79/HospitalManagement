package com.kairos.shiftplanning.domain.staffing_level;

import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@XStreamAlias("staffinglevel")
public class StaffingLevelPlannerEntity {
    private String id;
    private Long unitId;
    private LocalDate date;
    private List<StaffingLevelInterval> intervals;

    public Integer getStaffingLevelSatisfaction(Shift shift){
        return ShiftPlanningUtility.getStaffingLevelSatisfaction(this,shift);
    }
    public Integer getStaffingLevelSatisfaction(List<Shift> shifts){
        return ShiftPlanningUtility.getStaffingLevelSatisfaction(this);
    }
}
