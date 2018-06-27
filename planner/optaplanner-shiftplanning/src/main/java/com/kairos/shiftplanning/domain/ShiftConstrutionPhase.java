package com.kairos.shiftplanning.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.joda.time.*;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;

import java.util.*;

//@PlanningEntity(movableEntitySelectionFilter = MoveableEntitySelectionFilter.class)
@XStreamAlias("ShiftConstrutionPhase")
public class ShiftConstrutionPhase implements Shift{
    private UUID id;
    private Employee employee;
    @PlanningVariable(valueRangeProviderRefs = "possibleStartTimes")
    private DateTime start;
    @PlanningVariable(valueRangeProviderRefs = "possibleEndTimes")
    private DateTime end;
    @CustomShadowVariable(variableListenerClass = ShiftBreakListener.class, sources = {
            @PlanningVariableReference(variableName = "start"),@PlanningVariableReference(variableName = "end") })
    private List<ShiftBreak> breaks;

    private boolean isLocked;
    private boolean isCreatedByStaff;




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

    public Long getStartInMillis(){
        return this.getStart().getMillis();
    }

    public DateTime getStart() {
        return start;
    }


    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public Interval getInterval() {
        if (start == null || end == null || start.isAfter(end)) return null;
        return new Interval(start, end);
    }

    public Integer getMinutes() {
        return getInterval() == null ? 0 : getInterval().toDuration().toStandardMinutes().getMinutes();
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
    public int checkConstraints(List<Shift> shifts, int index){
        if(!this.isLocked() && this.getStart()!=null && this.getEnd()!=null) {
           return this.getEmployee().getWorkingTimeConstraints().checkConstraint(shifts, index);
        }
        return 0;
    }

    public void breakLevelConstraints(HardMediumSoftLongScoreHolder scoreHolder, RuleContext kContext,int index,int contraintPenality){
        this.getEmployee().getWorkingTimeConstraints().breakLevelConstraints(scoreHolder,kContext,index,contraintPenality);
    }

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
        return avialableThisInterval(indirectActivity.getInterval());
    }
    public boolean overlapsInterval(Interval interval){
        return interval!=null && this.getInterval()!=null && this.getInterval().overlaps(interval);
    }
    public boolean avialableThisInterval(Interval interval){
        return this.getInterval()!=null && interval!=null
                && this.getInterval().contains(interval) &&
                (breaks==null || breaks.stream().filter(brk->brk.getInterval().overlaps(interval)).findFirst()!=null);
    }
    public void info(Object obj){
        System.out.println(obj);
    }
}
