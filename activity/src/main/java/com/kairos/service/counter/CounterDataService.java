package com.kairos.service.counter;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/27/2018
 */

import com.kairos.counter.CounterServiceMapping;
import com.kairos.dto.activity.counter.data.FilterCriteriaDTO;
import com.kairos.dto.activity.counter.data.CommonRepresentationData;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.repository.counter.CounterRepository;
import com.kairos.persistence.repository.time_bank.TimeBankRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.planner.vrpPlanning.VRPPlanningService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.task_type.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class CounterDataService {
    private final static Logger logger = LoggerFactory.getLogger(CounterDataService.class);
    @Inject
    private VRPPlanningService vrpPlanningService;
    @Inject
    private TaskService taskService;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private CounterRepository counterRepository;
    @Inject
    private CounterServiceMapping counterServiceMapping;
    @Inject
    private ExecutorService executorService;
    @Inject
    private TimeBankRepository timeBankRepository;

    //FIXME: DO NOT REMOVE will be uncommented once representation model confirmed.
    public List<KPI> getCountersData(Long unitId, BigInteger solverConfigId){
//        VrpTaskPlanningDTO vrpTaskPlanningDTO = vrpPlanningService.getSolutionBySubmition(unitId, solverConfigId);
//        List<VRPTaskDTO> tasks = taskService.getAllTask(unitId);
//        Set<String> shiftIds = vrpTaskPlanningDTO.getTasks().stream().map(task -> task.getShiftId()).collect(Collectors.toSet());
//        Map<String, EmployeeDTO> employeeDataIdMap = vrpTaskPlanningDTO.getEmployees().stream().collect(Collectors.toMap(employee -> employee.getId(), employee->employee));
//
//        if(shiftIds == null || shiftIds.isEmpty()){
//            exceptionService.dataNotFoundByIdException("error.kpi.vrp.shift.availability", shiftIds);
//        }
//        List<Shift> shifts = shiftService.getAllShiftByIds(new ArrayList<>(shiftIds));
        ArrayList<KPI> kpiList = new ArrayList<>();
        //kpiList
//        kpiList.add(getTaskUnplannedKPI(vrpTaskPlanningDTO, tasks));
//        kpiList.add(getTaskUnplannedHoursKPI(vrpTaskPlanningDTO));
//        kpiList.add(getTasksPerStaff(vrpTaskPlanningDTO, tasks, employeeDataIdMap));
//        kpiList.add(getTotalTaskTimeVsWorkingTime(vrpTaskPlanningDTO, shifts));
//        kpiList.add(getRoadTimePercentKPI(vrpTaskPlanningDTO, shifts));
//        kpiList.add(getCompletedTaskWithinTimeWindowKPI(vrpTaskPlanningDTO, shifts, tasks));
//        kpiList.add(getPercentOfBreaksIn11and13KPI(vrpTaskPlanningDTO));
//        kpiList.add(getFlexiTimePercentKPI(vrpTaskPlanningDTO,shifts));
//        kpiList.add(getFlexiTimeTaskPercentKPI(vrpTaskPlanningDTO, shifts, tasks));
//        kpiList.add(getTotalKMsDrivenByStaff(vrpTaskPlanningDTO, employeeDataIdMap));
//        kpiList.add(getTotalTaskEfficiencyKPI(vrpTaskPlanningDTO, tasks));
        return kpiList;
    }


    //KPI for Task Unplanned
//    public KPI getTaskUnplannedKPI(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<VRPTaskDTO> tasks){
//        List<Long> escalatedInstallationNumber = vrpTaskPlanningDTO.getEscalatedTaskList().parallelStream().map(task -> task.getInstallationNumber()).collect(toList());
//        if(escalatedInstallationNumber.size() == 0){
//            return prepareTaskUnplannedKPI(0, tasks.size());
//        }
//        List<VRPTaskDTO> escalatedtasks = tasks.stream().filter(task -> escalatedInstallationNumber.contains(task.getInstallationNumber())).collect(toList());;
//        return prepareTaskUnplannedKPI(escalatedtasks.size(), tasks.size());
//    }
//
//    private KPI prepareTaskUnplannedKPI(long tasksUnplanned, long totalTasks){
//        BaseChart baseChart = new PieChart(RepresentationUnit.NUMBER, "Task", new ArrayList());
//        ((PieChart) baseChart).getDataList().add(new CommonKpiDataUnit("Planned", null, decimalSpecification(totalTasks-tasksUnplanned)));
//        ((PieChart) baseChart).getDataList().add(new CommonKpiDataUnit("UnPlanned", null, decimalSpecification(tasksUnplanned)));
//        KPI kpi = new KPI(CounterType.TASK_UNPLANNED.getName(), ChartType.PIE, CounterSize.SIZE_1X1, CounterType.TASK_UNPLANNED, false,null);
//        kpi.setId(new BigInteger("1"));
//        return kpi;
//    }
//
//    //Tasks Unplanned Hours KPI
//    public KPI getTaskUnplannedHoursKPI(VrpTaskPlanningDTO vrpTaskPlanningDTO){
//        double unplannedTaskMinutes = vrpTaskPlanningDTO.getEscalatedTaskList().parallelStream().mapToDouble(task -> task.getDuration()).sum();
//        double plannedTaskMinutes = vrpTaskPlanningDTO.getTasks().parallelStream().mapToDouble(task -> task.getDuration()).sum();
//        return prepareTaskUnplannedHours(unplannedTaskMinutes, plannedTaskMinutes);
//    }
//
//    private KPI prepareTaskUnplannedHours(double unplannedMinutes, double plannedMinutes){
//        BaseChart baseChart = new PieChart(RepresentationUnit.DECIMAL, "Hours", new ArrayList());
//        ((PieChart) baseChart).getDataList().add(new CommonKpiDataUnit("Planned Task", null, decimalSpecification(plannedMinutes/60.0)));
//        ((PieChart) baseChart).getDataList().add(new CommonKpiDataUnit("UnPlanned Task", null, decimalSpecification(unplannedMinutes/60.0)));
//        KPI kpi = new KPI(CounterType.TASK_UNPLANNED_HOURS.getName(), baseChart, CounterSize.SIZE_1X1, CounterType.TASK_UNPLANNED_HOURS, false,null);
//        kpi.setId(new BigInteger("2"));
//        return kpi;
//    }
//
//    //KPI Task Per Staff
//    //TODO: staffIds to be replaced with staff name.
//    public KPI getTasksPerStaff(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<VRPTaskDTO> tasks, Map<String, EmployeeDTO> employeeDataIdMap){
//        Map<String, Long> staffTaskCountMap = new HashMap<>();
//
//        Map<Long, List<VRPTaskDTO>> installationNumberTaskMap =tasks.stream().collect(Collectors.groupingBy(VRPTaskDTO::getInstallationNumber, toList()));
//        vrpTaskPlanningDTO.getTasks().stream().collect(Collectors.groupingBy(TaskDTO::getStaffId, toList()))
//                .forEach((staffId, taskList) -> {
//                    long taskCount = taskList.stream().mapToLong(taskDTO -> (installationNumberTaskMap.get(taskDTO.getInstallationNumber())!=null)?installationNumberTaskMap.get(taskDTO.getInstallationNumber()).size():0).sum();
//                    EmployeeDTO employee = employeeDataIdMap.get(String.valueOf(staffId));
//                    staffTaskCountMap.put((employee != null)?employee.getName():"NA", taskCount);
//                });
//        return prepareTasksPerStaffKPI(staffTaskCountMap);
//    }
//
//    private KPI prepareTasksPerStaffKPI(Map<String, Long> staffTaskData){
//        BaseChart baseChart = new PieChart(RepresentationUnit.NUMBER, "Tasks", new ArrayList());
//        staffTaskData.forEach((staffName, taskCount) -> {
//            ((PieChart) baseChart).getDataList().add(new CommonKpiDataUnit(staffName, null, decimalSpecification(taskCount)));
//        });
//        KPI kpi = new KPI(CounterType.TASKS_PER_STAFF.getName(), baseChart, CounterSize.SIZE_1X1, CounterType.TASKS_PER_STAFF, false, null);
//        kpi.setId(new BigInteger("3"));
//        return kpi;
//    }
//
//    //getting total working time
//    //excluding break times and driving time for totalTaskTime
//    public KPI getTotalTaskTimeVsWorkingTime(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<Shift> includedShifts){
//        double workingTimeMinutes = includedShifts.parallelStream().mapToLong(shift -> Long.sum(shift.getEndDate().getTime(), -shift.getStartDate().getTime())).sum()/(1000*60);
//        double totalTaskTimeMinutes = vrpTaskPlanningDTO.getTasks().parallelStream().mapToLong(task -> task.getDuration()).sum();
//        return prepareTaskTimeVsWorkingTime(workingTimeMinutes, totalTaskTimeMinutes);
//    }
//
//    private KPI prepareTaskTimeVsWorkingTime(double workingTime, double totalTaskTime){
//        BaseChart baseChart = new SingleNumberChart(decimalSpecification(totalTaskTime*100.0/workingTime), RepresentationUnit.PERCENT, "Hours");
//        KPI kpi = new KPI(CounterType.TOTAL_TASK_TIME_PERCENT.getName(), baseChart, CounterSize.SIZE_1X1, CounterType.TOTAL_TASK_TIME_PERCENT, false,null);
//        kpi.setId(new BigInteger("4"));
//        return kpi;
//    }
//
//    //KPI: ROAD_TIME_PERCENT
//    public KPI getRoadTimePercentKPI(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<Shift> includedShifts){
//        double workingTimeMinutes = includedShifts.parallelStream().mapToLong(shift -> Long.sum(shift.getEndDate().getTime(), -shift.getStartDate().getTime())).sum()/(1000*60);
//        double totalRoadTimeMinutes=vrpTaskPlanningDTO.getDrivingTimeList().stream().filter(task -> !task.isBreakTime()).mapToLong(task -> task.getDuration()).sum();
//        double roadTimePercent = totalRoadTimeMinutes*100.0/workingTimeMinutes;
//        return prepareRoadTimePercentKPI(roadTimePercent);
//    }
//
//    private KPI prepareRoadTimePercentKPI(double roadTimePercent){
//        BaseChart baseChart = new SingleNumberChart(decimalSpecification(roadTimePercent), RepresentationUnit.PERCENT, "Hours");
//        KPI kpi = new KPI(CounterType.ROAD_TIME_PERCENT.getName(), baseChart, CounterSize.SIZE_1X1, CounterType.ROAD_TIME_PERCENT, false,null);
//        kpi.setId(new BigInteger("5"));
//        return kpi;
//    }
//
//    //KPI: Total tasks Completed
//    public KPI getCompletedTaskWithinTimeWindowKPI(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<Shift> shifts, List<VRPTaskDTO> tasks){
//        Map<String, Shift> shiftMap = shifts.stream().collect(Collectors.toMap(shift-> shift.getId().toString(), shift-> shift));
//        Map<Long, List<VRPTaskDTO>> installationNumberTaskMap =tasks.stream().collect(Collectors.groupingBy(VRPTaskDTO::getInstallationNumber, toList()));
//        List<VRPTaskDTO> completedTasks= new ArrayList<>();
//        vrpTaskPlanningDTO.getTasks().forEach(task -> {
//            Shift shift = shiftMap.get(task.getShiftId());
//            if(shift.getStartDate().getTime()<=task.getPlannedStartTime().toInstant(ZoneOffset.UTC).toEpochMilli()
//                    && shift.getEndDate().getTime()>=task.getPlannedEndTime().toInstant(ZoneOffset.UTC).toEpochMilli()){
//                completedTasks.addAll(installationNumberTaskMap.get(task.getInstallationNumber()));
//            }
//        });
//        return prepareCompletedTaskWithinTimeWindow(completedTasks.size());
//    }
//
//    private KPI prepareCompletedTaskWithinTimeWindow(long completedTasksCount){
//        BaseChart baseChart = new SingleNumberChart(decimalSpecification(completedTasksCount), RepresentationUnit.NUMBER, "Tasks");
//        KPI kpi = new KPI(CounterType.TASKS_COMPLETED_WITHIN_TIME.getName(), baseChart, CounterSize.SIZE_1X1, CounterType.TASKS_COMPLETED_WITHIN_TIME, false,null);
//        kpi.setId(new BigInteger("6"));
//        return kpi;
//    }
//
//    //KPI:Percent of breaks
//
//    public KPI getPercentOfBreaksIn11and13KPI(VrpTaskPlanningDTO vrpTaskPlanningDTO) {
//        List<TaskDTO> allBreaks = vrpTaskPlanningDTO.getDrivingTimeList().stream().filter(task -> task.isBreakTime()).collect(toList());
//        List<DayOfWeek> days = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY);
//        List<TaskDTO> validBreaks = allBreaks.stream().filter(task -> (days.contains(task.getPlannedStartTime().getDayOfWeek()) && task.getPlannedStartTime().getHour() >=11 && task.getPlannedEndTime().getHour() <=13)).collect(toList());
//        double validBreakPercentage = validBreaks.size()*100.0/allBreaks.size();
//        return preparePercentOfBreaksIn11and13KPI(validBreakPercentage);
//    }
//
//    private KPI preparePercentOfBreaksIn11and13KPI(double validBreakPercentage){
//        BaseChart baseChart = new SingleNumberChart(decimalSpecification(validBreakPercentage), RepresentationUnit.PERCENT, "Breaks");
//        KPI kpi = new KPI(CounterType.VALID_BREAK_PERCENT.getName(), baseChart, CounterSize.SIZE_1X1, CounterType.VALID_BREAK_PERCENT, false,null);
//        kpi.setId(new BigInteger("7"));
//        return kpi;
//    }
//
//    //KPI:Flexi Time Time Percent
//    public KPI getFlexiTimePercentKPI(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<Shift> shifts){
//        long baseShiftWorkingTime = shifts.stream().mapToLong(baseShift -> Long.sum(baseShift.getEndDate().getTime(), -baseShift.getStartDate().getTime())).sum();
//        Map<String, Shift> shiftsIdMap = shifts.parallelStream().collect(Collectors.toMap(shift -> shift.getId().toString(), shift-> shift));
//        long flexiWorkingTime = vrpTaskPlanningDTO.getShifts().parallelStream().mapToLong(
//                plannedShift -> ((plannedShift.getEndTime() - shiftsIdMap.get(plannedShift.getKairosShiftId()).getEndDate().getTime())>=0)
//                        ?(plannedShift.getEndTime() - shiftsIdMap.get(plannedShift.getKairosShiftId()).getEndDate().getTime())
//                        :0)
//                .sum();
//        double flexiTimePercent = flexiWorkingTime*100.0/baseShiftWorkingTime;
//        return prepareFlexiTimePercentKPI(flexiTimePercent);
//    }
//
//    private KPI prepareFlexiTimePercentKPI(double flexiTimePercent){
//        BaseChart baseChart = new SingleNumberChart(decimalSpecification(flexiTimePercent), RepresentationUnit.PERCENT, "Hours");
//        KPI kpi = new KPI(CounterType.FLEXI_TIME_PERCENT.getName(), baseChart, CounterSize.SIZE_1X1, CounterType.FLEXI_TIME_PERCENT, false,null);
//        kpi.setId(new BigInteger("8"));
//        return kpi;
//    }
//
//    //KPI: flexi time tasks
//    public KPI getFlexiTimeTaskPercentKPI(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<Shift> shifts, List<VRPTaskDTO> taskDTOs){
//        Map<Long, List<VRPTaskDTO>> installationNumberTaskMap = taskDTOs.stream().collect(Collectors.groupingBy(task -> task.getInstallationNumber(), Collectors.toList()));
//        Map<String, Shift> shiftIdMap = shifts.parallelStream().collect(Collectors.toMap(shift->shift.getId().toString(), shift->shift));
//        List<TaskDTO> eligibleTaskGroups = vrpTaskPlanningDTO.getTasks().parallelStream().filter(task ->  shiftIdMap.get(task.getShiftId())!=null && task.getPlannedEndTime().toInstant(ZoneOffset.UTC).toEpochMilli() > shiftIdMap.get(task.getShiftId()).getEndDate().getTime()).collect(toList());
//        List<Long> taskCounts = new ArrayList<>();
//        eligibleTaskGroups.parallelStream().forEach(taskGroup -> {
//            long shiftEndTime = shiftIdMap.get(taskGroup.getShiftId()).getEndDate().getTime();
//            long taskDurationWithinShift = shiftEndTime - taskGroup.getPlannedStartTime().toInstant(ZoneOffset.UTC).toEpochMilli();
//            long taskCount = 0;
//            if(taskDurationWithinShift <= 0){
//                taskCount = installationNumberTaskMap.get(taskGroup.getInstallationNumber()).size();
//            }else {
//                List<VRPTaskDTO> tasks = installationNumberTaskMap.get(taskGroup.getInstallationNumber()).stream().sorted(new Comparator<VRPTaskDTO>() {
//                    @Override
//                    public int compare(VRPTaskDTO o1, VRPTaskDTO o2) {
//                        return o2.getDuration() - o1.getDuration();
//                    }
//                }).collect(toList());
//                long availableDuration = taskDurationWithinShift;
//                for(VRPTaskDTO task : tasks){
//                    availableDuration-=task.getDuration();
//                    taskCount = (availableDuration>=0)?taskCount:++taskCount;
//                }
//            }
//            taskCounts.add(taskCount);
//        });
//        long totalFlexiTaskCount = taskCounts.stream().mapToLong(t->t).sum();
//        double totalFlexiTaskCountPercent = totalFlexiTaskCount*100.0/taskDTOs.size();
//        return prepareFlexiTimeTaskPercent(totalFlexiTaskCountPercent);
//    }
//
//    private KPI prepareFlexiTimeTaskPercent(double flexiTimeTaskPercent){
//        BaseChart baseChart = new SingleNumberChart(decimalSpecification(flexiTimeTaskPercent), RepresentationUnit.PERCENT, "Tasks");
//        KPI kpi = new KPI(CounterType.FLEXI_TIME_TASK_PERCENT.getName(), baseChart, CounterSize.SIZE_1X1, CounterType.FLEXI_TIME_PERCENT, false,null);
//        kpi.setId(new BigInteger("9"));
//        return kpi;
//    }
//
//    //KPI: total KM driven by per staff
//    public KPI getTotalKMsDrivenByStaff(VrpTaskPlanningDTO vrpTaskPlanningDTO, Map<String, EmployeeDTO> employeeDTOMap){
//        Map<String, Double> staffAndKMsData = new HashedMap();
//        vrpTaskPlanningDTO.getDrivingTimeList().stream().collect(Collectors.groupingBy(task -> task.getStaffId(), Collectors.toList())).forEach((staffId, drivingTimeList) -> {
//            EmployeeDTO employee = employeeDTOMap.get(String.valueOf(staffId));
//            staffAndKMsData.put((employee != null)?employee.getName():"NA", drivingTimeList.stream().mapToDouble(e -> e.getDrivingDistance()).sum()/1000.0);
//        });
//        return prepareTotalKMDrivenByStaff(staffAndKMsData);
//    }
//
//    private KPI prepareTotalKMDrivenByStaff(Map<String, Double> staffAndKMDetails){
//        BaseChart baseChart = new PieChart(RepresentationUnit.NUMBER, "KMs", new ArrayList());
//        staffAndKMDetails.forEach((staffName, kmDriven) -> {
//            ((PieChart) baseChart).getDataList().add(new CommonKpiDataUnit(staffName, null, decimalSpecification(kmDriven)));
//        });
//        KPI kpi = new KPI(CounterType.TOTAL_KM_DRIVEN_PER_STAFF.getName(), baseChart, CounterSize.SIZE_1X1, CounterType.TOTAL_KM_DRIVEN_PER_STAFF, false,null);
//        kpi.setId(new BigInteger("10"));
//        return kpi;
//    }
//
//    //KPI: task Efficiency
//    public KPI getTotalTaskEfficiencyKPI(VrpTaskPlanningDTO vrpTaskPlanningDTO, List<VRPTaskDTO> tasks){
//        long totalTaskDuration = tasks.parallelStream().mapToLong(task -> task.getDuration()).sum();
//        long totalPlannedTaskDuration = vrpTaskPlanningDTO.getTasks().stream().mapToLong(task -> task.getDuration()).sum();
//        double efficiency = totalTaskDuration*1.0/totalPlannedTaskDuration;
//        return prepareTaskEfficiencyKPI(efficiency);
//    }
//
//    public KPI prepareTaskEfficiencyKPI(double efficiency){
//        BaseChart baseChart = new SingleNumberChart(decimalSpecification(efficiency), RepresentationUnit.DECIMAL, "Tasks");
//        KPI kpi = new KPI(CounterType.TASK_EFFICIENCY.getName(), baseChart, CounterSize.SIZE_1X1, CounterType.TASK_EFFICIENCY, false,null);
//        kpi.setId(new BigInteger("11"));
//        return kpi;
//    }
//
//    //KPI: Yellow Time Percent:
//    //public KPI
//
//    private double decimalSpecification(double value){
//        return Math.round(value*100)/100.0;
//    }
//
//    //TODO: scope in future, for collecting counters common separately
//    public void getCounterMetadataForVRP(){//list of KPIs
//
//    }

    public Map generateKPIData(FilterCriteriaDTO filters,Long organizationId){
        List<BigInteger> kpiIds = filters.getKpiIds();
        List<KPI> kpis = counterRepository.getKPIsByIds(kpiIds);
        Map<BigInteger, KPI> kpiMap = kpis.stream().collect(Collectors.toMap(kpi->kpi.getId(), kpi -> kpi));
        List<Future<CommonRepresentationData>> kpiResults = new ArrayList<>();
        Map<FilterType, List> filterBasedCriteria = new HashMap<>();
        if(filters.getFilters() != null)
        filters.getFilters().forEach(filter -> {
            filterBasedCriteria.put(filter.getType(), filter.getValues());
        });
        for(BigInteger kpiId : filters.getKpiIds()){
            Callable<CommonRepresentationData> data = () ->{
                return counterServiceMapping.getService(kpiMap.get(kpiId).getType()).getCalculatedKPI(filterBasedCriteria, organizationId, kpiMap.get(kpiId));
            };
            Future<CommonRepresentationData> responseData = executorService.submit(data);
            kpiResults.add(responseData);
        }
        List<CommonRepresentationData> kpisData = new ArrayList();
        for(Future<CommonRepresentationData> data : kpiResults){
            try {
                kpisData.add(data.get());
            } catch(InterruptedException ex){
                ex.printStackTrace();
            } catch(ExecutionException ex){
                ex.printStackTrace();
            }
        }

//      TODO: to be used with counter data processing.
//        List<Future<CommonRepresentationData>> counterResults = new ArrayList<>();  //will be used for counter data collections.
//        Map<BigInteger, Counter> counterMap = new HashedMap();
//        for(BigInteger counterId : filters.getCounterIds()){
//            Callable<CommonRepresentationData> data = () ->{
//                return counterServiceMapping.getService(kpiMap.get(counterId).getType()).getCalculatedCounter(filterBasedCriteria, countryId, counterMap.get(counterId));
//            };
//            Future<CommonRepresentationData> responseData = executorService.submit(data);
//            counterResults.add(responseData);
//        }

        return kpisData.stream().collect(Collectors.toMap(kpiData -> kpiData.getCounterId(), kpiData -> kpiData));
    }

//    public Map<Long,Long> calculatePlannedHour(Set<Long> staffIds, Long unitId, LocalDate startDate, LocalDate endDate ){
//        List<DailyTimeBankEntry> dailyTimeBankEntries = timeBankRepository.findAllByStaffIdsAndDate(staffIds, DateUtils.asDate(startDate),DateUtils.asDate(endDate));
//        Map<Long,Long> staffPlannedHours = dailyTimeBankEntries.stream().collect(Collectors.groupingBy(DailyTimeBankEntry::getStaffId,Collectors.summingLong(d->d.getTotalTimeBankMin()+d.getContractualMin())));
//        return staffPlannedHours;
//    }
}
