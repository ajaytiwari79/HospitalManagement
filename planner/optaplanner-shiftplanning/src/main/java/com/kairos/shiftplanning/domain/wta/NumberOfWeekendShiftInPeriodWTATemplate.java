package com.kairos.shiftplanning.domain.wta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.shiftplanning.constraints.ScoreLevel;
import com.kairos.shiftplanning.domain.shift.Shift;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.List;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE13
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NumberOfWeekendShiftInPeriodWTATemplate implements ConstraintHandler {

    private int numberShiftsPerPeriod;
    private long numberOfWeeks;
    private int fromDayOfWeek; //(day of week)
    private LocalTime fromTime;
    private boolean proportional;
    private LocalTime toTime;
    private int toDayOfWeek;
    private int weight;
    private ScoreLevel level;
    private String templateType;
    private Interval interval;

    public NumberOfWeekendShiftInPeriodWTATemplate(int numberShiftsPerPeriod, int fromDayOfWeek, LocalTime fromTime,int toDayOfWeek, LocalTime toTime, boolean proportional,  int weight, ScoreLevel level,LocalDate planningWeekStart) {
        this.numberShiftsPerPeriod = numberShiftsPerPeriod;
        this.fromDayOfWeek = fromDayOfWeek;
        this.fromTime = fromTime;
        this.proportional = proportional;
        this.toTime = toTime;
        this.toDayOfWeek = toDayOfWeek;
        this.weight = weight;
        this.level = level;
        interval=getInterval(planningWeekStart);
    }
    private Interval getInterval(LocalDate planningWeekStart){
        DateTime start=planningWeekStart.plusDays(fromDayOfWeek).toDateTime(fromTime);
        DateTime end=toDayOfWeek<fromDayOfWeek?planningWeekStart.plusWeeks(1).plusDays(toDayOfWeek).toDateTime(toTime):planningWeekStart.plusDays(toDayOfWeek).toDateTime(toTime);
        return new Interval(start,end);
    }
    public int checkConstraints(List<Shift> shifts){
        int weekendShifts=(int) shifts.stream().filter(s->interval.contains(s.getStart())).count();
        return weekendShifts>numberShiftsPerPeriod?weekendShifts-numberShiftsPerPeriod:0;
    }

    @Override
    public ScoreLevel getLevel() {
        return level;
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
