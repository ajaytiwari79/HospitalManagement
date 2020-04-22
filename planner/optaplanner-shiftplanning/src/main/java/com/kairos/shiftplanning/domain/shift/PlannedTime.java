package com.kairos.shiftplanning.domain.shift;

import com.kairos.commons.utils.DateTimeInterval;
import lombok.*;

import java.math.BigInteger;
import java.time.ZonedDateTime;

import static com.kairos.shiftplanning.utils.ShiftPlanningUtility.getIntervalAsString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PlannedTime {
    private BigInteger plannedTimeId;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public DateTimeInterval getInterval() {
        return new DateTimeInterval(startDate,endDate);
    }

    @Override
    public String toString() {
        return "PlannedTime{" +
                "plannedTimeId=" + plannedTimeId +
                getIntervalAsString(getInterval()) +
                '}';
    }
}
