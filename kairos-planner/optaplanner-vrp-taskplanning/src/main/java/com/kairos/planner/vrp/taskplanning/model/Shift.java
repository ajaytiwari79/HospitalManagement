package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
//@PlanningEntity
public class Shift extends TaskOrShift{
    private String id;
    private Employee employee;
    private LocalDate localDate;
   // @CustomShadowVariable(variableListenerClass = ShiftStartTimeListener.class,
   //         sources = @PlanningVariableReference(variableName = "activityLineIntervals"))
    private LocalTime start;
    private LocalTime end;

    private List<Task> tasks;

    public Shift(String id, Employee employee, LocalDate localDate, LocalTime start, LocalTime end) {
        this.id = id;
        this.employee = employee;
        this.localDate = localDate;
        this.start = start;
        this.end = end;
    }

    public Shift() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "" + employee.getName() +
                "[" + localDate +
                ":" + start +
                "-" + end +
                '}';
    }
}
