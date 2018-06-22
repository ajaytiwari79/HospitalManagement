package com.kairos.response.dto.web.planning.vrpPlanning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.solverconfig.SolverConfigDTO;

import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VrpTaskPlanningDTO {
    private SolverConfigDTO solverConfig;
    private List<ShiftDTO> shifts;
    private List<EmployeeDTO> employees;
    private List<TaskDTO> tasks;

    public VrpTaskPlanningDTO() {
    }

    public VrpTaskPlanningDTO(SolverConfigDTO solverConfig, List<ShiftDTO> shifts, List<EmployeeDTO> employees, List<TaskDTO> tasks) {
        this.solverConfig = solverConfig;
        this.shifts = shifts;
        this.employees = employees;
        this.tasks = tasks;
    }

    public List<ShiftDTO> getShifts() {
        return shifts;
    }

    public void setShifts(List<ShiftDTO> shifts) {
        this.shifts = shifts;
    }

    public List<EmployeeDTO> getEmployees() {
        return employees;
    }

    public void setEmployees(List<EmployeeDTO> employees) {
        this.employees = employees;
    }

    public List<TaskDTO> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDTO> tasks) {
        this.tasks = tasks;
    }

    public SolverConfigDTO getSolverConfig() {
        return solverConfig;
    }

    public void setSolverConfig(SolverConfigDTO solverConfig) {
        this.solverConfig = solverConfig;
    }
}
