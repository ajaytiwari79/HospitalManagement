package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.wta.IntervalUnit;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.utils.JodaIntervalConverter;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.Interval;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE11
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaximumAverageScheduledTimeWTATemplate implements ConstraintHandler {

    //Total Average Working Time in a period

    private List<String> balanceType;//multiple check boxes
    private int intervalLength;
    private IntervalUnit intervalUnit;
    private long validationStartDateMillis;
    private boolean balanceAdjustment;
    private boolean useShiftTimes;
    private long maximumAvgTime;
    private int weight;
    private ScoreLevel level;
    private String templateType;
    @XStreamConverter(JodaIntervalConverter.class)
    private Interval interval;

    public MaximumAverageScheduledTimeWTATemplate(long maximumAvgTime, int intervalLength, int weight, ScoreLevel level, LocalDate weekStart){
        this.weight = weight;
        this.maximumAvgTime = maximumAvgTime;
        this.level = level;
        intervalUnit=IntervalUnit.WEEKS;
        this.intervalLength=intervalLength;
        this.interval=initializeInterval(weekStart);
    }

    private Interval initializeInterval(LocalDate weekStart) {
        return ShiftPlanningUtility.createInterval(weekStart,intervalLength,intervalUnit);
    }


    public int checkConstraints(List<Shift> shifts){
        int totalScheduledTime = 0;//(int) shifts.get(0).getEmployee().getPrevShiftsInfo().getMaximumAverageScheduledTimeInfo();
        for (Shift shift:shifts) {
            if(interval.overlaps(shift.getInterval())){
                totalScheduledTime+=interval.overlap(shift.getInterval()).toPeriod().getMinutes();
            }
        }
        return totalScheduledTime>maximumAvgTime?totalScheduledTime-(int)maximumAvgTime:0;
    }
}
