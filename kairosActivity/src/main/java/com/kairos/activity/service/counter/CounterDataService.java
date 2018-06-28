package com.kairos.activity.service.counter;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

import com.kairos.activity.enums.CounterType;
import com.kairos.activity.enums.counter.ChartType;
import com.kairos.activity.enums.counter.CounterSize;
import com.kairos.activity.enums.counter.RepresentationUnit;
import com.kairos.activity.persistence.model.counter.KPI;
import com.kairos.activity.persistence.model.counter.chart.BaseChart;
import com.kairos.activity.persistence.model.counter.chart.GaugeChart;
import com.kairos.activity.response.dto.task.VRPTaskDTO;
import com.kairos.activity.service.planner.vrpPlanning.VRPPlanningService;
import com.kairos.activity.service.task_type.TaskService;
import com.kairos.response.dto.web.planning.vrpPlanning.VrpTaskPlanningDTO;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CounterDataService {

    @Inject
    VRPPlanningService vrpPlanningService;
    @Inject
    TaskService taskService;

    public void getCountersData(Long unitId, BigInteger solverConfigId){
        VrpTaskPlanningDTO vrpTaskPlanningDTO = vrpPlanningService.getSolverConfigurationForUnit(unitId, solverConfigId);
        List<Long> escalatedInstallationNumber = vrpTaskPlanningDTO.getEscalatedTaskList().parallelStream().map(task -> task.getInstallationNumber()).collect(Collectors.toList());
        List<VRPTaskDTO> tasks = taskService.getAllTask(unitId);
    }

    public KPI getTaskUnplannedKPI(Long unitId, VrpTaskPlanningDTO vrpTaskPlanningDTO, List<VRPTaskDTO> tasks){
        List<Long> escalatedInstallationNumber = vrpTaskPlanningDTO.getEscalatedTaskList().parallelStream().map(task -> task.getInstallationNumber()).collect(Collectors.toList());
        if(escalatedInstallationNumber.size() == 0){
            return prepareTaskUnplannedKPI(0, tasks.size());
        }
        List<VRPTaskDTO> escalatedtasks = tasks.stream().filter(task -> escalatedInstallationNumber.contains(task.getInstallationNumber())).collect(Collectors.toList());;
        return prepareTaskUnplannedKPI(escalatedtasks.size(), tasks.size());
    }

    private KPI prepareTaskUnplannedKPI(long tasksUnplanned, long totalTasks){
        BaseChart baseChart = new GaugeChart(0, totalTasks, tasksUnplanned, null, null, RepresentationUnit.NUMBER, "Task");
        KPI kpi = new KPI(CounterType.TASK_UNPLANNED.getName(), ChartType.GAUGE, baseChart, CounterSize.SIZE_1X1);
        kpi.setId(BigInteger.valueOf(2));
        kpi.setType(CounterType.TASK_UNPLANNED);
        return kpi;
    }

    //TODO: scope in future, for collecting counters metadata separatly
    public void getCounterMetadataForVRP(){
        //list of KPIs

    }

    //public void
}
