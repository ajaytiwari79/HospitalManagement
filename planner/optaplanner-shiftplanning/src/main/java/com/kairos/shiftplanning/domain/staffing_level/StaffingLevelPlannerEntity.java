package com.kairos.shiftplanning.domain.staffing_level;

import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.staff.IndirectActivity;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("staffinglevel")
public class StaffingLevelPlannerEntity {
    private String id;
    private Long unitId;
    private LocalDate date;
    private List<StaffingLevelInterval> intervals;

    public Integer getStaffingLevelSatisfaction(Shift shift, List<IndirectActivity> indirectActivityList){
        return ShiftPlanningUtility.getStaffingLevelSatisfaction(this,shift,indirectActivityList);
    }
    public Integer getStaffingLevelSatisfaction(List<Shift> shifts,List<IndirectActivity> indirectActivityList){
        return ShiftPlanningUtility.getStaffingLevelSatisfaction(this,shifts,indirectActivityList);
    }
}
