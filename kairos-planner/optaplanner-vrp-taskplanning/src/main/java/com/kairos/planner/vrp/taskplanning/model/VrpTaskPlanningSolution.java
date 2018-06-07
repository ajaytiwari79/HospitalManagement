package com.kairos.planner.vrp.taskplanning.model;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;

import java.util.List;
@PlanningSolution
public class VrpTaskPlanningSolution {
    private String id;
    @ProblemFactCollectionProperty
    private List<Shift> shifts;
    @ProblemFactCollectionProperty
    private List<Employee> employees;
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "tasks")
    private List<Task> tasks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = shifts;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public VrpTaskPlanningSolution(String id, List<Shift> shifts, List<Employee> employees, List<Task> tasks) {
        this.id = id;
        this.shifts = shifts;
        this.employees = employees;
        this.tasks = tasks;
    }
}
