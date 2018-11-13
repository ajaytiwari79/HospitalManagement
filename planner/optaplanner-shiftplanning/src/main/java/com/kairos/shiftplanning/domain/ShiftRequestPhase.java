package com.kairos.shiftplanning.domain;

import com.kairos.shiftplanning.domain.wta.listeners.ShiftStartTimeListener;
import com.kairos.shiftplanning.utils.JodaLocalDateConverter;
import com.kairos.shiftplanning.utils.JodaLocalTimeConverter;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@PlanningEntity
@XStreamAlias("ShiftRequestPhase")
public class ShiftRequestPhase implements Shift{
    private static Logger log= LoggerFactory.getLogger(ShiftRequestPhase.class);
    private UUID id;
    private Employee employee;
    //@CustomShadowVariable(variableListenerClass = ShiftIntervalListener.class,
      //      sources = @PlanningVariableReference(variableName = "shift",entityClass = ActivityLineInterval.class))
    @CustomShadowVariable(variableListenerClass = ShiftStartTimeListener.class,
          sources = @PlanningVariableReference(variableName = "activityLineIntervals"))
    @XStreamConverter(JodaLocalTimeConverter.class)
    private LocalTime startTime;
    @XStreamConverter(JodaLocalTimeConverter.class)
    //@CustomShadowVariable(variableListenerClass = ShiftIntervalListener.class,
     //       sources = @PlanningVariableReference(variableName = "shift",entityClass = ActivityLineInterval.class))
    private LocalTime endTime;
   // @CustomShadowVariable(variableListenerClass = ShiftBreakListener.class, sources = {
     //       @PlanningVariableReference(variableName = "startTime")/*,@PlanningVariableReference(variableName = "endTime")*/ })
   //@InverseRelationShadowVariable(sourceVariableName =  "breakShift")
    //These breaks are not useful while planner as those are realy planner entities
    private List<ShiftBreak> breaks= new ArrayList<>();
private java.time.LocalDate startDate;
private java.time.LocalDate endDate;

    //@CustomShadowVariable(variableListenerClass = ShiftIntervalListener.class, sources = {
    //        @PlanningVariableReference(variableName = "startTime"),@PlanningVariableReference(variableName = "endTime") })
    @InverseRelationShadowVariable(sourceVariableName =  "shift")
    private List<ActivityLineInterval> activityLineIntervals = new ArrayList<>();
    @XStreamConverter(JodaLocalDateConverter.class)
    private LocalDate date;

    private boolean isLocked;
    private boolean isCreatedByStaff;

    public ShiftRequestPhase(Employee employee, LocalDate date) {
        this.employee = employee;
        this.date = date;
    }

    public LocalDate getStartingDayOfThisWeek(){
        LocalDate startOfWeek = new LocalDate().withDayOfWeek(DateTimeConstants.MONDAY);
        return startOfWeek;
    }

    public boolean isCreatedByStaff() {
        return isCreatedByStaff;
    }

    public void setCreatedByStaff(boolean createdByStaff) {
        isCreatedByStaff = createdByStaff;
    }

    /*public List<ShiftBreak> getBreaks() {
        return breaks;
    }

    public void setBreaks(List<ShiftBreak> breaks) {
        this.breaks = breaks;
    }*/

    //List<ShiftBreak> breaks = new ArrayList<>();




    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }


    /*public Integer getBreakMinutes() {
        return breaks.stream().mapToInt(brk -> brk.getMinutes()).sum();
    }*/

    /*public Integer getWorkMinutes() {
        return getMinutes() == 0 ? 0 : getMinutes() - getBreakMinutes();
    }*/



    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    /*public void addBreak(ShiftBreak brk) {
        this.breaks.add(brk);
    }*/
    /*
    This considers only shift's total hours.
     */

    /*public boolean checkActivityContraints(ActivityLineInterval activityLineInterval,ShiftRequestPhase shift,List<ActivityLineInterval> activityLineIntervals,int index){
        return activityLineInterval.checkConstraints(activityLineInterval,shift,activityLineIntervals,index);
    }*/

   /* public void breakActivityContraints(ActivityLineInterval activityLineInterval,HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext,int index){
        activityLineInterval.getActivity().breakActivityContraints(scoreHolder,kContext,index);
    }*/

    /*public int checkConstraints(List<Shift> shifts, int index){
        return this.getEmployee().getWorkingTimeConstraints().checkConstraint(shifts, index);

    }

    public int checkConstraints( int index){
        return this.getEmployee().getWorkingTimeConstraints().checkConstraint(this, index);
    }

    public void breakLevelConstraints(HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext,int index,int contraintPenality){
        this.getEmployee().getWorkingTimeConstraints().breakLevelConstraints(scoreHolder,kContext,index,contraintPenality);
    }
    */

    public int getBreaksDuration(List<ShiftBreak> breaks){
        return breaks.stream().mapToInt(b->b.getMinutes()).sum();
    }

    public List<ShiftBreak> getBreaks() {
        return breaks;
    }

    public void setBreaks(List<ShiftBreak> breaks) {
        this.breaks = breaks;
    }
    public boolean containsActivity(IndirectActivity indirectActivity){
        return availableThisInterval(indirectActivity.getInterval());
    }
    public boolean overlapsInterval(Interval interval){
        return interval!=null && this.getInterval()!=null && this.getInterval().overlaps(interval);
    }


    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    @Override
    public DateTime getStart() {
        return startTime!=null?date.toDateTime(startTime):null;
    }

    @Override
    public DateTime getEnd() {
        return endTime!=null?endTime.isAfter(startTime)?date.toDateTime(endTime):date.plusDays(1).toDateTime(endTime):null;
    }
    @Override
    public Integer getMinutes(){
        return Shift.super.getMinutes();
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    public LocalDate getDate() {
        return  date;
    }

    public List<ActivityLineInterval> getActivityLineIntervals() {
        return activityLineIntervals;
    }
   /* public List<ActivityLineInterval> getActivityLineIntervalsList() {
        return new ArrayList<>(activityLineIntervals);
    }*/

    public java.time.LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(java.time.LocalDate startDate) {
        this.startDate = startDate;
    }

    public java.time.LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(java.time.LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setActivityLineIntervals(List<ActivityLineInterval> activityLineIntervals) {
        this.activityLineIntervals = activityLineIntervals;
    }

    /*public int getShiftCostInInt(){
        return Math.round(getShiftCost());
    }

    public float getShiftCost(){
        return getEmployee().getCollectiveTimeAgreement().getTotalCostOfShift(this).floatValue();
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ShiftRequestPhase that = (ShiftRequestPhase) o;

        /*return new EqualsBuilder()
                .append(isLocked, that.isLocked)
                .append(isCreatedByStaff, that.isCreatedByStaff)
                .append(id, that.id)
                .append(employee, that.employee)
                .append(startTime, that.startTime)
                .append(endTime, that.endTime)
                .append(breaks, that.breaks)
                .append(activityLineIntervals, that.activityLineIntervals)
                .append(date, that.date)
                .isEquals();*/
        return id.equals(that.getId());
    }

    public ShiftRequestPhase() {
    }

    @Override
    public int hashCode() {
        /*int hashcode=new HashCodeBuilder(17, 37)
                .append(id)
                .append(employee)
                .append(startTime)
                .append(endTime)
                .append(breaks)
                .append(activityLineIntervals)
                .append(date)

                .append(isLocked)
                .append(isCreatedByStaff)
                .toHashCode();*/
        int hashcode=id.hashCode();
        //log.info("Shift hashcode:"+date.toString("MM/dd")+":"+hashcode);
        return hashcode;
    }

    /**
     * This method tells missing intervals in shif. Note this does not tell which activityintervals are overlapped multiple times in same interval.
     * @return totalMissing
     */
    public int missingIntervals(){
        //System.out.println(assignedActivityLineIntervals.size());
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
            if(breaks!=null && breaksIndices.contains(i)) continue;
            if(!intervalEntry.containsKey(i)) totalMissing++;
        }
        /*if(totalMissing>0){
            log.info("misssing");
        }*/
        return totalMissing;

    }
    public int missingIntervals(List<ActivityLineInterval> assignedActivityLineIntervals){
        //System.out.println(assignedActivityLineIntervals.size());
        if(startTime==null) return  0;
        Collections.sort(assignedActivityLineIntervals);
        int intervalMins=15;
        Map<Integer,Integer> intervalEntry= new HashMap<>();
        //if(assignedActivityLineIntervals.size()==0){
            //Well, the reason it(empty assignedActivityLineIntervals) happens even when startTime is not null is because when removed a shift from activityLineinterval,
            //it does not update the listener which wreaks havoc.
        //}
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
            if(breaks!=null && breaksIndices.contains(i)) continue;
            if(!intervalEntry.containsKey(i)) totalMissing++;
        }
        return totalMissing;

    }
    private Set<Integer> getBreaksIndices(int intervalMins){
        if(breaks==null) return null;
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
    private int getBreaksIntervals(int intervalMins) {
        int breakIntervals=0;
        if(breaks!=null && !breaks.isEmpty()){
            for (ShiftBreak shiftBreak:breaks) {
                if(shiftBreak.getMinutes()!=null){
                    breakIntervals+=shiftBreak.getMinutes()/intervalMins;
                }
            }
        }
        return breakIntervals;
    }
    @Override
    public String toString() {
        return "Shift{"+id.toString().substring(0,6)+":"+
                date.toString("[MM/dd]")+"" + (Optional.ofNullable(startTime).isPresent()?startTime.toString("HH:mm"):"") +
                "," +  (Optional.ofNullable(endTime).isPresent()?endTime.toString("HH:mm"):"") +
                '}';
    }

    public ShiftRequestPhase getShiftByActivity(Activity activity, List<ShiftRequestPhase> shifts){
        for (ShiftRequestPhase shift:shifts) {

        }
        return null;
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
    public int getMissingBreakTimes(){
        int totalMins=getMinutes();
        if(totalMins<300) return 0;
        /*int assignedMins= activityLineIntervals.stream().mapToInt(ActivityLineInterval::getDuration).sum();
        int diff= totalMins-assignedMins;*/
        int missing=0;
        List<Interval> mergedIntervals= ShiftPlanningUtility.getMergedIntervals(activityLineIntervals,false);
        int max=0;
        for (int i = 1; i < mergedIntervals.size()-1; i++) {
            max=mergedIntervals.get(i).getStart().isAfter(mergedIntervals.get(i-1).getEnd()) && ShiftPlanningUtility.getMinutes(mergedIntervals.get(i-1).getEnd(),mergedIntervals.get(i).getStart())>max?ShiftPlanningUtility.getMinutes(mergedIntervals.get(i-1).getEnd(),mergedIntervals.get(i).getStart()):max;
        }
        //check for 5 hours break
        if(totalMins>300 && max<30){
            missing=(30-max)/15;
        }
        //log.info("**************************{}************",missing);
        return  missing;
    }
    public boolean isAvailableThisInterval(Interval interval,List<ShiftBreak> breaks){
        //log.info(this.getPrettyId());
        if(isAbsenceActivityApplied() || !getInterval().contains(interval)){
            //log.info("false");
            return false;
        }

        for(ShiftBreak sb:breaks){
            if(sb.getShift().getId().equals(this.id) && sb.getInterval()!=null && sb.getInterval().overlaps(interval)){
                    //log.info("false");
                return false;
            }
        }
        //log.info("true");
        return true;
    }
    public boolean hasAnyEmployee(List<Employee> emps){
        //log.info("E"+this.getPrettyId()+":::"+emps.contains(this.employee));
        return emps.contains(this.employee);
    }
}
