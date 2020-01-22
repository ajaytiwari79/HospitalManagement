package com.kairos.shiftplanning.domain.staff;

import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.utils.ShiftPlanningUtility;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.List;
import java.util.UUID;

@PlanningEntity
@XStreamAlias("IndirectActivity")
public class IndirectActivity {
    private UUID id;
    //To be used later if we predefine any time.
    private DateTime preferredStartTime;
    @PlanningVariable(valueRangeProviderRefs = "possibleStartDateTimes",nullable = true)
    private DateTime startTime;
    private int duration;
    private boolean canBeMoved;
    private List<Employee> employees;
    private String type;
    private boolean locked;
    public IndirectActivity(UUID id, int duration, boolean canBeMoved, List<Employee> employees, String type, boolean locked) {
        this.id = id;
        this.duration = duration;
        this.canBeMoved = canBeMoved;
        this.employees = employees;
        this.type = type;
        this.locked=locked;
    }

    public IndirectActivity() {
    }

    public LocalDate getPlannedStartDate() {
        return startTime ==null?null: startTime.toLocalDate();
    }

    public Interval getInterval(){
        return startTime ==null? null: new Interval(startTime, startTime.plusMinutes(duration));
    }
    public boolean hasEmployee(Employee employee){
        return employees !=null && employees.contains(employee);
    }
    public boolean overlapsInterval(Interval interval){
        return this.getInterval()!=null && interval!=null && interval.overlaps(this.getInterval());
    }

    public boolean canBePlanned(List<ShiftImp> shifts) {
        return ShiftPlanningUtility.checkEmployeesAvailability(shifts, employees, startTime);
    }
}
