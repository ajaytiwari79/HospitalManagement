package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.api.domain.entity.PlanningEntity;

import java.time.LocalDate;
import java.time.LocalTime;

@PlanningEntity
public class Shift {
    private String id;
    private Employee employee;
    private LocalDate localDate;
    private LocalTime start;
    private LocalTime end;

    public Shift(String id, Employee employee, LocalDate localDate, LocalTime start, LocalTime end) {
        this.id = id;
        this.employee = employee;
        this.localDate = localDate;
        this.start = start;
        this.end = end;
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
}
