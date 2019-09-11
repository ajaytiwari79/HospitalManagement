package com.kairos.planner.vrp.taskplanning.solution;

import com.kairos.planner.vrp.taskplanning.model.*;
import com.kairos.planner.vrp.taskplanning.model.constraint.Constraint;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@PlanningSolution(lookUpStrategyType = LookUpStrategyType.EQUALITY)
public class VrpTaskPlanningSolution {
    private String id;
    private BigInteger solverConfigId;
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "shifts")
    private List<Shift> shifts;
    @ProblemFactCollectionProperty
    private List<Employee> employees;
    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "tasks")
    private List<Task> tasks;
    @ProblemFactProperty
    private LocationsDistanceMatrix locationsDistanceMatrix;
    @ProblemFactProperty
    private LocationsRouteMatrix locationsRouteMatrix;
    @ProblemFactProperty
    private Constraint constraint;

    @PlanningScore
    private HardMediumSoftLongScore hardMediumSoftScore;


    public Constraint getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint constraint) {
        this.constraint = constraint;
    }

    public BigInteger getSolverConfigId() {
        return solverConfigId;
    }

    public void setSolverConfigId(BigInteger solverConfigId) {
        this.solverConfigId = solverConfigId;
    }

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

    public VrpTaskPlanningSolution() {
        this.id=UUID.randomUUID().toString();
    }

    public HardMediumSoftLongScore getHardMediumSoftScore() {
        return hardMediumSoftScore;
    }

    public void setHardMediumSoftScore(HardMediumSoftLongScore hardMediumSoftScore) {
        this.hardMediumSoftScore = hardMediumSoftScore;
    }

    public LocationsDistanceMatrix getLocationsDistanceMatrix() {
        return locationsDistanceMatrix;
    }

    public void setLocationsDistanceMatrix(LocationsDistanceMatrix locationsDistanceMatrix) {
        this.locationsDistanceMatrix = locationsDistanceMatrix;
    }

    public LocationsRouteMatrix getLocationsRouteMatrix() {
        return locationsRouteMatrix;
    }

    public void setLocationsRouteMatrix(LocationsRouteMatrix locationsRouteMatrix) {
        this.locationsRouteMatrix = locationsRouteMatrix;
    }
}
