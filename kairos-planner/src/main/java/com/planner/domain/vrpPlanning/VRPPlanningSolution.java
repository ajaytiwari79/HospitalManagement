package com.planner.domain.vrpPlanning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.planner.vrp.taskplanning.model.Employee;
import com.kairos.planner.vrp.taskplanning.model.Shift;
import com.kairos.planner.vrp.taskplanning.model.Task;
import com.planner.domain.MongoBaseEntity;

import java.math.BigInteger;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class VRPPlanningSolution extends MongoBaseEntity {

    private BigInteger solverConfigId;
    private List<Shift> shifts;
    private List<Employee> employees;
    private List<Task> tasks;

    public VRPPlanningSolution() {
    }

    public VRPPlanningSolution(BigInteger solverConfigId, List<Shift> shifts, List<Employee> employees, List<Task> tasks) {
        this.solverConfigId = solverConfigId;
        this.shifts = shifts;
        this.employees = employees;
        this.tasks = tasks;
    }

    public BigInteger getSolverConfigId() {
        return solverConfigId;
    }

    public void setSolverConfigId(BigInteger solverConfigId) {
        this.solverConfigId = solverConfigId;
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
}
