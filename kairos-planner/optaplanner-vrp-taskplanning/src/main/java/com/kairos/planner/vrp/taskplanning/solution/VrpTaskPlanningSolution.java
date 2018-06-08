package com.kairos.planner.vrp.taskplanning.solution;

import com.kairos.planner.vrp.taskplanning.model.Employee;
import com.kairos.planner.vrp.taskplanning.model.LocationsDistanceMatrix;
import com.kairos.planner.vrp.taskplanning.model.Shift;
import com.kairos.planner.vrp.taskplanning.model.Task;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;

import java.util.List;
import java.util.UUID;

@PlanningSolution
public class VrpTaskPlanningSolution {
    private String id;
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
    @PlanningScore
    private HardMediumSoftLongScore hardMediumSoftScore;

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
}
