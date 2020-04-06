package com.kairos.shiftplanning.domain.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.staff.Employee;
import com.kairos.shiftplanning.domain.staff.IndirectActivity;
import com.kairos.shiftplanning.listeners.ShiftStartTimeListener;
import com.kairos.shiftplanning.utils.LocalDateConverter;
import com.kairos.shiftplanning.utils.LocalTimeConverter;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.asZonedDateTime;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;


@Getter
@Setter
@NoArgsConstructor
@PlanningEntity
@XStreamAlias("ShiftImp")
public class ShiftImp implements Shift{
    private static Logger log= LoggerFactory.getLogger(ShiftImp.class);
    private BigInteger id;
    private Employee employee;
    @CustomShadowVariable(variableListenerClass = ShiftStartTimeListener.class,
          sources = @PlanningVariableReference(variableName = "activityLineIntervals"))
    @XStreamConverter(LocalTimeConverter.class)
    private LocalTime startTime;
    @XStreamConverter(LocalTimeConverter.class)
    private LocalTime endTime;
    //These breaks are not useful while planner as those are realy planner entities
    private List<ShiftBreak> breaks= new ArrayList<>();
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;

    @InverseRelationShadowVariable(sourceVariableName =  "shift")
    private List<ActivityLineInterval> activityLineIntervals = new ArrayList<>();
    @XStreamConverter(LocalDateConverter.class)
    private LocalDate date;
    private int scheduledMinutes;
    private int durationMinutes;
    private int plannedMinutesOfTimebank;
    private boolean isLocked;
    private boolean isCreatedByStaff;
    private List<ShiftActivity> shiftActivities;
    private Set<BigInteger> activitiesTimeTypeIds = new HashSet<>();
    private Set<BigInteger> activityIds = new HashSet<>();
    private Set<BigInteger> activitiesPlannedTimeIds = new HashSet<>();
    private int restingMinutes;

    public ShiftImp(Employee employee, LocalDate date) {
        this.employee = employee;
        this.date = date;
    }

    public int getBreaksDuration(List<ShiftBreak> breaks){
        return breaks.stream().mapToInt(b->b.getMinutes()).sum();
    }

    public boolean containsActivity(IndirectActivity indirectActivity){
        return availableThisInterval(indirectActivity.getInterval());
    }
    public boolean overlapsInterval(DateTimeInterval interval){
        return interval!=null && this.getInterval()!=null && this.getInterval().overlaps(interval);
    }

    @Override
    public ZonedDateTime getStart() {
        return startTime!=null? asZonedDateTime(date,startTime):null;
    }

    @Override
    public ZonedDateTime getEnd() {
        return endTime!=null? endTime.isAfter(startTime) ? asZonedDateTime(date,endTime) : asZonedDateTime(date.plusDays(1),endTime) :null;
    }
    @Override
    public Integer getMinutes(){
        return Shift.super.getMinutes();
    }

    public Activity getActivityById(BigInteger activityId) {
        for (ShiftActivity shiftActivity : shiftActivities) {
            if(shiftActivity.getActivity().getId().equals(activityId)){
                return shiftActivity.getActivity();
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ShiftImp that = (ShiftImp) o;
        return id.equals(that.getId());
    }

    public boolean isChanged(ShiftImp shiftImp){
        if (shiftImp == null || getClass() != shiftImp.getClass()) return true;
        return (!id.equals(shiftImp.id) || !this.startTime.equals(endTime) || !this.getEndTime().equals(shiftImp.getEndTime()) || !this.getActivityLineIntervals().equals(shiftImp.getActivityLineIntervals()) || ! this.employee.equals(shiftImp.employee));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * This method tells missing intervals in shift. Note this does not tell which activityintervals are overlapped multiple times in same interval.
     * @return totalMissing
     */
    public int missingIntervals(){
        if(startTime==null) return  0;
        int intervalMins=15;
        Map<Integer,Integer> intervalEntry= new HashMap<>();
        for (ActivityLineInterval activityLineInterval:activityLineIntervals) {
            ZonedDateTime start = activityLineInterval.getStart();
            int a=(start.get(ChronoField.HOUR_OF_DAY)*(60/intervalMins)) + (start.get(ChronoField.MINUTE_OF_HOUR)/intervalMins);
            if(!intervalEntry.containsKey(a)){
                intervalEntry.put(a,1);
            }
        }
        int startI=(startTime.get(ChronoField.HOUR_OF_DAY)*(60/intervalMins)) + (startTime.get(ChronoField.MINUTE_OF_HOUR)/intervalMins);
        int endI=(endTime.get(ChronoField.HOUR_OF_DAY)*(60/intervalMins)) + (endTime.get(ChronoField.MINUTE_OF_HOUR)/intervalMins);
        int totalMissing=0;
        Set<Integer> breaksIndices= getBreaksIndices(intervalMins);
        for(int i=startI;i<endI;i++){
            if(breaks!=null && isNotNull(breaksIndices) && breaksIndices.contains(i)) continue;
            if(!intervalEntry.containsKey(i)) totalMissing++;
        }
        return totalMissing;

    }
    public int missingIntervals(List<ActivityLineInterval> assignedActivityLineIntervals){
        if(startTime==null) return  0;
        Collections.sort(assignedActivityLineIntervals);
        int intervalMins=15;
        Map<Integer,Integer> intervalEntry= new HashMap<>();
        for (ActivityLineInterval activityLineInterval:assignedActivityLineIntervals) {
            ZonedDateTime start = activityLineInterval.getStart();
            int a=(start.get(ChronoField.HOUR_OF_DAY)*(60/intervalMins)) + (start.get(ChronoField.MINUTE_OF_HOUR)/intervalMins);
            if(!intervalEntry.containsKey(a)){
                intervalEntry.put(a,1);
            }
        }
        //TODO life aint sorted.. so are these intervals.. use above
        int startForFirstInterval=(assignedActivityLineIntervals.get(0).getStart().get(ChronoField.HOUR_OF_DAY)*(60/intervalMins)) + (assignedActivityLineIntervals.get(0).getStart().get(ChronoField.MINUTE_OF_HOUR)/intervalMins);
        int startForLastInterval=(assignedActivityLineIntervals.get(assignedActivityLineIntervals.size()-1).getStart().get(ChronoField.HOUR_OF_DAY)*(60/intervalMins)) + (assignedActivityLineIntervals.get(assignedActivityLineIntervals.size()-1).getStart().get(ChronoField.MINUTE_OF_HOUR)/intervalMins);
        int totalMissing=0;
        Set<Integer> breaksIndices= getBreaksIndices(intervalMins);
        for(int i=startForFirstInterval;i<=startForLastInterval;i++){
            if(breaks!=null && isNotNull(breaksIndices) && breaksIndices.contains(i)) continue;
            if(!intervalEntry.containsKey(i)) totalMissing++;
        }
        return totalMissing;

    }
    private Set<Integer> getBreaksIndices(int intervalMins){
        if(breaks==null) return new HashSet<>();
        Set<Integer> breaksIndices=new HashSet<>();
        for (ShiftBreak shiftBreak: breaks) {
            int i=0;
            int breakIntervals=shiftBreak.getMinutes()/intervalMins;
            while (i<breakIntervals){
                breaksIndices.add(getIndexForTime(shiftBreak.getStartTime().toLocalTime(),intervalMins) + i++);
            }
        }
        return breaksIndices;
    }
    private int getIndexForTime(LocalTime time, int intervalMins){
        return (time.get(ChronoField.HOUR_OF_DAY)*(60/intervalMins)) + (time.get(ChronoField.MINUTE_OF_HOUR)/intervalMins);
    }

    @Override
    public String toString() {
        return "Shift{"+id.toString().substring(0,6)+":"+
                date.format(DateTimeFormatter.ofPattern("[MM/dd]"))+"" + (Optional.ofNullable(startTime).isPresent()?startTime.format(DateTimeFormatter.ofPattern("HH:mm")):"") +
                "," +  (Optional.ofNullable(endTime).isPresent()?endTime.format(DateTimeFormatter.ofPattern("HH:mm")):"") +
                "}";
    }

    public boolean hasIntervalsForActivity(Activity activity){
        for(ActivityLineInterval ali:activityLineIntervals){
            if(ali.getActivity().getId().equals(activity.getId())){
                return true;
            }
        }
        return false;
    }
    public String getPrettyId(){
        return id.toString().substring(0,6);
    }
    public boolean isAbsenceActivityApplied(){
        return CollectionUtils.isNotEmpty(activityLineIntervals) && new ArrayList<>(activityLineIntervals).get(0).getActivity().isTypeAbsence();
    }

    public boolean isAbsenceActivity(){
        return shiftActivities.stream().anyMatch(shiftActivity -> shiftActivity.getActivity().isTypeAbsence());
    }

    public boolean isPresenceActivity(){
        return shiftActivities.stream().anyMatch(shiftActivity -> shiftActivity.getActivity().isTypePresence());
    }

    public int getMissingBreakTimes(){
        int totalMins=getMinutes();
        if(totalMins<300) return 0;
        int missing=0;
        List<DateTimeInterval> mergedIntervals= ShiftPlanningUtility.getMergedIntervals(activityLineIntervals,false);
        int max=0;
        for (int i = 1; i < mergedIntervals.size()-1; i++) {
            max=mergedIntervals.get(i).getStart().isAfter(mergedIntervals.get(i-1).getEnd()) && ShiftPlanningUtility.getMinutes(mergedIntervals.get(i-1).getEnd(),mergedIntervals.get(i).getStart())>max?ShiftPlanningUtility.getMinutes(mergedIntervals.get(i-1).getEnd(),mergedIntervals.get(i).getStart()):max;
        }
        if(totalMins>300 && max<30){
            missing=(30-max)/15;
        }
        return  missing;
    }
    public boolean isAvailableThisInterval(DateTimeInterval interval,List<ShiftBreak> breaks){
        if(isAbsenceActivityApplied() || !getInterval().contains(interval)){
            return false;
        }

        for(ShiftBreak sb:breaks){
            if(sb.getShift().getId().equals(this.id) && sb.getInterval()!=null && sb.getInterval().overlaps(interval)){
                return false;
            }
        }
        return true;
    }

    public ShiftActivity firstShiftActivity(){
        return shiftActivities.get(0);
    }

    public ShiftActivity lastShiftActivity(){
        return shiftActivities.get(shiftActivities.size()-1);
    }

    public boolean hasAnyEmployee(List<Employee> emps){
        return emps.contains(this.employee);
    }
}
