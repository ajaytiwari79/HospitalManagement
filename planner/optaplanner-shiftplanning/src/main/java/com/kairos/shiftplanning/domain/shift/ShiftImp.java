package com.kairos.shiftplanning.domain.shift;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.staff.Employee;
import com.kairos.shiftplanning.domain.staff.IndirectActivity;
import com.kairos.shiftplanning.listeners.ShiftStartTimeListener;
import com.kairos.shiftplanning.utils.JodaLocalDateConverter;
import com.kairos.shiftplanning.utils.JodaLocalTimeConverter;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;


@Getter
@Setter
@PlanningEntity
@XStreamAlias("ShiftImp")
public class ShiftImp implements Shift{
    private static Logger log= LoggerFactory.getLogger(ShiftImp.class);
    private BigInteger id;
    private Employee employee;
    @CustomShadowVariable(variableListenerClass = ShiftStartTimeListener.class,
          sources = @PlanningVariableReference(variableName = "activityLineIntervals"))
    @XStreamConverter(JodaLocalTimeConverter.class)
    private LocalTime startTime;
    @XStreamConverter(JodaLocalTimeConverter.class)
    private LocalTime endTime;
    //These breaks are not useful while planner as those are realy planner entities
    private List<ShiftBreak> breaks= new ArrayList<>();
    private java.time.LocalDate startDate;
    private java.time.LocalDate endDate;

    @InverseRelationShadowVariable(sourceVariableName =  "shift")
    private List<ActivityLineInterval> activityLineIntervals = new ArrayList<>();
    @XStreamConverter(JodaLocalDateConverter.class)
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
    public ShiftImp(Employee employee, LocalDate date) {
        this.employee = employee;
        this.date = date;
    }

    public LocalDate getStartingDayOfThisWeek(){
        LocalDate startOfWeek = new LocalDate().withDayOfWeek(DateTimeConstants.MONDAY);
        return startOfWeek;
    }

    public int getBreaksDuration(List<ShiftBreak> breaks){
        return breaks.stream().mapToInt(b->b.getMinutes()).sum();
    }

    public boolean containsActivity(IndirectActivity indirectActivity){
        return availableThisInterval(indirectActivity.getInterval());
    }
    public boolean overlapsInterval(Interval interval){
        return interval!=null && this.getInterval()!=null && this.getInterval().overlaps(interval);
    }

    @Override
    public DateTime getStart() {
        return startTime!=null?date.toDateTime(startTime):null;
    }

    @Override
    public DateTime getEnd() {
        return endTime!=null? endTime.isAfter(startTime) ? date.toDateTime(endTime) : date.plusDays(1).toDateTime(endTime) :null;
    }
    @Override
    public Integer getMinutes(){
        return Shift.super.getMinutes();
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

    public ShiftImp() {
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
            DateTime start = activityLineInterval.getStart();
            int a=(start.getHourOfDay()*(60/intervalMins)) + (start.getMinuteOfHour()/intervalMins);
            if(!intervalEntry.containsKey(a)){
                intervalEntry.put(a,1);
            }
        }
        int startI=(startTime.getHourOfDay()*(60/intervalMins)) + (startTime.getMinuteOfHour()/intervalMins);
        int endI=(endTime.getHourOfDay()*(60/intervalMins)) + (endTime.getMinuteOfHour()/intervalMins);
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
            DateTime start = activityLineInterval.getStart();
            int a=(start.getHourOfDay()*(60/intervalMins)) + (start.getMinuteOfHour()/intervalMins);
            if(!intervalEntry.containsKey(a)){
                intervalEntry.put(a,1);
            }
        }
        //TODO life aint sorted.. so are these intervals.. use above
        int startForFirstInterval=(assignedActivityLineIntervals.get(0).getStart().getHourOfDay()*(60/intervalMins)) + (assignedActivityLineIntervals.get(0).getStart().getMinuteOfHour()/intervalMins);
        int startForLastInterval=(assignedActivityLineIntervals.get(assignedActivityLineIntervals.size()-1).getStart().getHourOfDay()*(60/intervalMins)) + (assignedActivityLineIntervals.get(assignedActivityLineIntervals.size()-1).getStart().getMinuteOfHour()/intervalMins);
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
        return (time.getHourOfDay()*(60/intervalMins)) + (time.getMinuteOfHour()/intervalMins);
    }

    @Override
    public String toString() {
        return "Shift{"+id.toString().substring(0,6)+":"+
                date.toString("[MM/dd]")+"" + (Optional.ofNullable(startTime).isPresent()?startTime.toString("HH:mm"):"") +
                "," +  (Optional.ofNullable(endTime).isPresent()?endTime.toString("HH:mm"):"") +
                '}';
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
        List<Interval> mergedIntervals= ShiftPlanningUtility.getMergedIntervals(activityLineIntervals,false);
        int max=0;
        for (int i = 1; i < mergedIntervals.size()-1; i++) {
            max=mergedIntervals.get(i).getStart().isAfter(mergedIntervals.get(i-1).getEnd()) && ShiftPlanningUtility.getMinutes(mergedIntervals.get(i-1).getEnd(),mergedIntervals.get(i).getStart())>max?ShiftPlanningUtility.getMinutes(mergedIntervals.get(i-1).getEnd(),mergedIntervals.get(i).getStart()):max;
        }
        if(totalMins>300 && max<30){
            missing=(30-max)/15;
        }
        return  missing;
    }
    public boolean isAvailableThisInterval(Interval interval,List<ShiftBreak> breaks){
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
    public boolean hasAnyEmployee(List<Employee> emps){
        return emps.contains(this.employee);
    }
}
