package com.kairos.dto.planner.vrp.vrpPlanning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class VrpTaskPlanningDTO {
    private SolverConfigDTO solverConfig;
    private List<ShiftDTO> shifts;
    private List<EmployeeDTO> employees;
    private List<TaskDTO> tasks;
    private List<TaskDTO> drivingTimeList;
    private List<TaskDTO> escalatedTaskList = new ArrayList<>();
}
