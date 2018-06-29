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
import com.kairos.activity.persistence.model.counter.chart.PieChart;
import com.kairos.activity.persistence.model.counter.chart.PieDataUnit;
import com.kairos.activity.response.dto.task.VRPTaskDTO;
import com.kairos.activity.service.planner.vrpPlanning.VRPPlanningService;
import com.kairos.activity.service.task_type.TaskService;
import com.kairos.response.dto.web.planning.vrpPlanning.VrpTaskPlanningDTO;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CounterDataService {

    @Inject
    VRPPlanningService vrpPlanningService;
    @Inject
    TaskService taskService;

    public List<KPI> getCountersData(Long unitId, BigInteger solverConfigId){
        VrpTaskPlanningDTO vrpTaskPlanningDTO = vrpPlanningService.getSolverConfigurationForUnit(unitId, solverConfigId);
        List<Long> escalatedInstallationNumber = vrpTaskPlanningDTO.getEscalatedTaskList().parallelStream().map(task -> task.getInstallationNumber()).collect(Collectors.toList());
        List<VRPTaskDTO> tasks = taskService.getAllTask(unitId);

        ArrayList<KPI> kpiList = new ArrayList<>();
        kpiList.add(getTaskUnplannedKPI(vrpTaskPlanningDTO, tasks));
        kpiList.add(getTaskUnplannedHoursKPI(vrpTaskPlanningDTO));
        return kpiList;
    }


    //KPI for Task Unplanned
    public KPI getTaskUnplannedKPI(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<VRPTaskDTO> tasks){
        List<Long> escalatedInstallationNumber = vrpTaskPlanningDTO.getEscalatedTaskList().parallelStream().map(task -> task.getInstallationNumber()).collect(Collectors.toList());
        if(escalatedInstallationNumber.size() == 0){
            return prepareTaskUnplannedKPI(0, tasks.size());
        }
        List<VRPTaskDTO> escalatedtasks = tasks.stream().filter(task -> escalatedInstallationNumber.contains(task.getInstallationNumber())).collect(Collectors.toList());;
        return prepareTaskUnplannedKPI(escalatedtasks.size(), tasks.size());
    }

    private KPI prepareTaskUnplannedKPI(long tasksUnplanned, long totalTasks){
        BaseChart baseChart = new PieChart(RepresentationUnit.NUMBER, "Task", new ArrayList());
        ((PieChart) baseChart).getDataList().add(new PieDataUnit("Planned", String.valueOf(totalTasks-tasksUnplanned)));
        ((PieChart) baseChart).getDataList().add(new PieDataUnit("UnPlanned", String.valueOf(tasksUnplanned)));
        KPI kpi = new KPI(CounterType.TASK_UNPLANNED.getName(), ChartType.PIE, baseChart, CounterSize.SIZE_1X1);
        kpi.setType(CounterType.TASK_UNPLANNED);
        return kpi;
    }

    //Tasks Unplanned Hours KPI
    public KPI getTaskUnplannedHoursKPI(VrpTaskPlanningDTO vrpTaskPlanningDTO){
        double unplannedTaskHours = vrpTaskPlanningDTO.getEscalatedTaskList().parallelStream().mapToDouble(task -> task.getDuration()).sum();
        double plannedTaskHours = vrpTaskPlanningDTO.getTasks().parallelStream().mapToDouble(task -> task.getDuration()).sum();
        return prepareTaskUnplannedHours(unplannedTaskHours, plannedTaskHours);
    }

    private KPI prepareTaskUnplannedHours(double unplannedHours, double plannedHours){
        BaseChart baseChart = new PieChart(RepresentationUnit.DECIMAL, "Hours", new ArrayList());
        ((PieChart) baseChart).getDataList().add(new PieDataUnit("Planned Task", String.valueOf(plannedHours)));
        ((PieChart) baseChart).getDataList().add(new PieDataUnit("UnPlanned Task", String.valueOf(unplannedHours)));
        KPI kpi = new KPI(CounterType.TASK_UNPLANNED.getName(), ChartType.PIE, baseChart, CounterSize.SIZE_1X1);
        kpi.setType(CounterType.TASK_UNPLANNED);
        return kpi;
    }

    //KPI Task Per Staff
    public KPI getTasksPerStaff(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<VRPTaskDTO> tasks){
        Map staffTaskMap = vrpTaskPlanningDTO.getTasks().stream().collect(Collectors.groupingBy(task -> task.getStaffId()));

        return null;
    }

    //TODO: scope in future, for collecting counters metadata separatly
    public void getCounterMetadataForVRP(){
        //list of KPIs

    }

    //public void
}
