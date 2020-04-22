package com.kairos.shiftplanning.domain.staffing_level;

import com.kairos.commons.utils.DateTimeInterval;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@XStreamAlias("StaffingLevelInterval")
public class StaffingLevelInterval {
    private ZonedDateTime start;
    private ZonedDateTime end;
    private int minimumStaffRequired;
    private int maximumStaffRequired;
    private List<StaffingLevelSkill> skillLevels;
    private List<StaffingLevelActivityType> activityTypeLevels;

    public StaffingLevelInterval(ZonedDateTime start, ZonedDateTime end, int minimumStaffRequired, int maximumStaffRequired,
                                 List<StaffingLevelSkill> skillLevels, List<StaffingLevelActivityType> activityTypeLevels) {
        this.start = start;
        this.end = end;
        this.minimumStaffRequired = minimumStaffRequired;
        this.maximumStaffRequired = maximumStaffRequired;
        this.skillLevels = skillLevels;
        this.activityTypeLevels = activityTypeLevels;
    }
    public DateTimeInterval getInterval(){
        return new DateTimeInterval(start,end);
    }

}
