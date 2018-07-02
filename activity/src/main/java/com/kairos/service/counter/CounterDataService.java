package com.kairos.service.counter;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

import com.kairos.activity.enums.counter.ChartType;
import com.kairos.activity.enums.counter.CounterSize;
import com.kairos.activity.enums.counter.RepresentationUnit;
import com.kairos.enums.CounterType;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.counter.chart.BaseChart;
import com.kairos.persistence.model.counter.chart.PieChart;
import com.kairos.persistence.model.counter.chart.PieDataUnit;
import com.kairos.service.planner.vrpPlanning.VRPPlanningService;
import com.kairos.service.task_type.TaskService;
import com.kairos.vrp.task.VRPTaskDTO;
import com.kairos.vrp.vrpPlanning.TaskDTO;
import com.kairos.vrp.vrpPlanning.VrpTaskPlanningDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class CounterDataService {
    private final static Logger logger = LoggerFactory.getLogger(CounterDataService.class);
    @Inject
    VRPPlanningService vrpPlanningService;
    @Inject
    TaskService taskService;

    public List<KPI> getCountersData(Long unitId, BigInteger solverConfigId){
        VrpTaskPlanningDTO vrpTaskPlanningDTO = vrpPlanningService.getSolverConfigurationForUnit(unitId, solverConfigId);
        List<Long> escalatedInstallationNumber = vrpTaskPlanningDTO.getEscalatedTaskList().parallelStream().map(task -> task.getInstallationNumber()).collect(toList());
        List<VRPTaskDTO> tasks = taskService.getAllTask(unitId);

        ArrayList<KPI> kpiList = new ArrayList<>();
        kpiList.add(getTaskUnplannedKPI(vrpTaskPlanningDTO, tasks));
        kpiList.add(getTaskUnplannedHoursKPI(vrpTaskPlanningDTO));
        kpiList.add(getTasksPerStaff(vrpTaskPlanningDTO, tasks));
        return kpiList;
    }


    //KPI for Task Unplanned
    public KPI getTaskUnplannedKPI(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<VRPTaskDTO> tasks){
        List<Long> escalatedInstallationNumber = vrpTaskPlanningDTO.getEscalatedTaskList().parallelStream().map(task -> task.getInstallationNumber()).collect(toList());
        if(escalatedInstallationNumber.size() == 0){
            return prepareTaskUnplannedKPI(0, tasks.size());
        }
        List<VRPTaskDTO> escalatedtasks = tasks.stream().filter(task -> escalatedInstallationNumber.contains(task.getInstallationNumber())).collect(toList());;
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
        double unplannedTaskMinutes = vrpTaskPlanningDTO.getEscalatedTaskList().parallelStream().mapToDouble(task -> task.getDuration()).sum();
        double plannedTaskMinutes = vrpTaskPlanningDTO.getTasks().parallelStream().mapToDouble(task -> task.getDuration()).sum();
        return prepareTaskUnplannedHours(unplannedTaskMinutes, plannedTaskMinutes);
    }

    private KPI prepareTaskUnplannedHours(double unplannedMinutes, double plannedMinutes){
        BaseChart baseChart = new PieChart(RepresentationUnit.DECIMAL, "Hours", new ArrayList());
        ((PieChart) baseChart).getDataList().add(new PieDataUnit("Planned Task", String.valueOf(plannedMinutes/60.0)));
        ((PieChart) baseChart).getDataList().add(new PieDataUnit("UnPlanned Task", String.valueOf(unplannedMinutes/60.0)));
        KPI kpi = new KPI(CounterType.TASK_UNPLANNED_HOURS.getName(), ChartType.PIE, baseChart, CounterSize.SIZE_1X1);
        kpi.setType(CounterType.TASK_UNPLANNED_HOURS);
        return kpi;
    }

    //KPI Task Per Staff
    //TODO: staffIds to be replaced with staff name.
    public KPI getTasksPerStaff(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<VRPTaskDTO> tasks){
        Map<String, Long> staffTaskCountMap = new HashMap<>();

        Map<Long, List<VRPTaskDTO>> installationNumberTaskMap =tasks.stream().collect(Collectors.groupingBy(VRPTaskDTO::getInstallationNumber, toList()));
        vrpTaskPlanningDTO.getTasks().stream().collect(Collectors.groupingBy(TaskDTO::getStaffId, toList()))
                .forEach((staffId, taskList) -> {
                    long taskCount = taskList.stream().mapToLong(taskDTO -> (installationNumberTaskMap.get(taskDTO.getInstallationNumber())!=null)?installationNumberTaskMap.get(taskDTO.getInstallationNumber()).size():0).sum();
                    staffTaskCountMap.put(String.valueOf(staffId), taskCount);
                });
        return prepareTasksPerStaffKPI(staffTaskCountMap);
    }

    public KPI prepareTasksPerStaffKPI(Map<String, Long> staffTaskData){
        BaseChart baseChart = new PieChart(RepresentationUnit.NUMBER, "Tasks", new ArrayList());
        staffTaskData.forEach((staffName, taskCount) -> {
            ((PieChart) baseChart).getDataList().add(new PieDataUnit(String.valueOf(staffName), String.valueOf(taskCount)));
        });
        KPI kpi = new KPI(CounterType.TASKS_PER_STAFF.getName(), ChartType.PIE, baseChart, CounterSize.SIZE_1X1);
        kpi.setType(CounterType.TASKS_PER_STAFF);
        return kpi;
    }

    //

    //TODO: scope in future, for collecting counters metadata separatly
    public void getCounterMetadataForVRP(){
        //list of KPIs

    }

    //public void
}
