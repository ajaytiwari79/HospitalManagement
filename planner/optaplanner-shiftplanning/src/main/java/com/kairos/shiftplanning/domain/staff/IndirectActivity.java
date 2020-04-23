package com.kairos.shiftplanning.domain.staff;

import com.kairos.commons.utils.DateTimeInterval;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@PlanningEntity
@XStreamAlias("IndirectActivity")
public class IndirectActivity {
    private UUID id;
    //To be used later if we predefine any time.
    private ZonedDateTime preferredStartTime;
    @PlanningVariable(valueRangeProviderRefs = "possibleStartDateTimes",nullable = true)
    private ZonedDateTime startTime;
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

    public LocalDate getPlannedStartDate() {
        return startTime ==null?null: startTime.toLocalDate();
    }

    public DateTimeInterval getInterval(){
        return startTime ==null? null: new DateTimeInterval(startTime, startTime.plusMinutes(duration));
    }
    public boolean hasEmployee(Employee employee){
        return employees !=null && employees.contains(employee);
    }
    public boolean overlapsInterval(DateTimeInterval interval){
        return this.getInterval()!=null && interval!=null && interval.overlaps(this.getInterval());
    }
}
