package com.kairos.shiftplanning.domain.staffing_level;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@XStreamAlias("StaffingLevelInterval")
@Deprecated
public class StaffingLevelInterval {
    private DateTime start;
    private DateTime end;
    private int minimumStaffRequired;
    private int maximumStaffRequired;
    private List<StaffingLevelSkill> skillLevels;
    private List<StaffingLevelActivityType> activityTypeLevels;

    public StaffingLevelInterval(DateTime start, DateTime end, int minimumStaffRequired, int maximumStaffRequired,
                                 List<StaffingLevelSkill> skillLevels, List<StaffingLevelActivityType> activityTypeLevels) {
        this.start = start;
        this.end = end;
        this.minimumStaffRequired = minimumStaffRequired;
        this.maximumStaffRequired = maximumStaffRequired;
        this.skillLevels = skillLevels;
        this.activityTypeLevels = activityTypeLevels;
    }
    public Interval getInterval(){
        return new Interval(start,end);
    }

}
