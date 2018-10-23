package com.planner.domain.vrpPlanning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.planner.vrp.taskplanning.model.Employee;
import com.planner.domain.common.MongoBaseEntity;
import com.planner.domain.staff.PlanningShift;
import com.planner.domain.task.Task;

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
    private List<PlanningShift> shifts;
    private List<Employee> employees;
    private List<Task> tasks;
    private List<Task> drivingTimeList;
    private List<Task> escalatedTaskList;

    public VRPPlanningSolution() {
    }

    public VRPPlanningSolution(BigInteger solverConfigId, List<PlanningShift> shifts, List<Employee> employees, List<Task> tasks,List<Task> drivingTimeList,List<Task> escalatedTaskList) {
        this.solverConfigId = solverConfigId;
        this.shifts = shifts;
        this.employees = employees;
        this.tasks = tasks;
        this.drivingTimeList = drivingTimeList;
        this.escalatedTaskList = escalatedTaskList;
    }


    public List<Task> getEscalatedTaskList() {
        return escalatedTaskList;
    }

    public void setEscalatedTaskList(List<Task> escalatedTaskList) {
        this.escalatedTaskList = escalatedTaskList;
    }

    public List<Task> getDrivingTimeList() {
        return drivingTimeList;
    }

    public void setDrivingTimeList(List<Task> drivingTimeList) {
        this.drivingTimeList = drivingTimeList;
    }

    public BigInteger getSolverConfigId() {
        return solverConfigId;
    }

    public void setSolverConfigId(BigInteger solverConfigId) {
        this.solverConfigId = solverConfigId;
    }

    public List<PlanningShift> getShifts() {
        return shifts;
    }

    public void setShifts(List<PlanningShift> shifts) {
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
