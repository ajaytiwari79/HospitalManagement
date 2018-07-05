package com.planner.service.taskPlanningService;

import com.kairos.enums.IntegrationOperation;
import com.kairos.planner.solverconfig.ConstraintValueDTO;
import com.kairos.planner.solverconfig.SolverConfigDTO;
import com.kairos.util.DateUtils;
import com.kairos.planner.vrp.taskplanning.model.Shift;
import com.kairos.planner.vrp.taskplanning.model.Task;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import com.kairos.planner.vrp.taskplanning.solver.VrpTaskPlanningSolver;
import com.kairos.vrp.vrpPlanning.VrpTaskPlanningDTO;
import com.planner.appConfig.appConfig.AppConfig;
import com.planner.constants.AppConstants;
import com.planner.domain.staff.PlanningShift;
import com.planner.domain.vrpPlanning.VRPPlanningSolution;
import com.planner.repository.vrpPlanning.VRPPlanningMongoRepository;
import com.planner.service.Client.PlannerRestClient;
import com.planner.service.vrpService.VRPGeneratorService;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class VRPPlannerService {
    private static Logger log= LoggerFactory.getLogger(VRPPlannerService.class);
    @Autowired
    private VRPGeneratorService vrpGeneratorService;
    @Autowired private VRPPlanningMongoRepository vrpPlanningMongoRepository;
    @Autowired private PlannerRestClient plannerRestClient;
    //This must not be serialized and this is an active solvers runtimes
    private transient Map<String,VrpTaskPlanningSolver> runningSolversPerProblem=new ConcurrentHashMap<>();
    @Autowired private AppConfig appConfig;
    @Async
    public void startVRPPlanningSolverOnThisVM(VrpTaskPlanningDTO vrpTaskPlanningDTO){
        VrpTaskPlanningSolution solution = vrpGeneratorService.getVRPProblemSolution(vrpTaskPlanningDTO);
        List<File> drlFileList = getDrlFileList(vrpTaskPlanningDTO.getSolverConfig());
        VrpTaskPlanningSolver solver = new VrpTaskPlanningSolver(drlFileList,appConfig.getVrpXmlFilePath());
        runningSolversPerProblem.put(solution.getSolverConfigId().toString(),solver);
        Object[] solutionAndIndictment=solver.solveProblemOnRequest(solution);
        runningSolversPerProblem.remove(solution.getSolverConfigId().toString());
        solution = (VrpTaskPlanningSolution)solutionAndIndictment[0];
        Object[] solvedTasks = getSolvedTasks(solution.getShifts(), (Map<Task,Indictment>)solutionAndIndictment[1]);
        VRPPlanningSolution vrpPlanningSolution = new VRPPlanningSolution(solution.getSolverConfigId(),(List<PlanningShift>) solvedTasks[0],solution.getEmployees(),(List<com.planner.domain.task.Task>) solvedTasks[1],(List<com.planner.domain.task.Task>) solvedTasks[2],new ArrayList<>());
        vrpPlanningSolution.setId(solution.getId());
        vrpPlanningMongoRepository.save(vrpPlanningSolution);
        plannerRestClient.publish(null, vrpTaskPlanningDTO.getSolverConfig().getUnitId(), IntegrationOperation.CREATE, vrpTaskPlanningDTO.getSolverConfig().getId());
    }

    //TODO make this run on problemId(submissionId) rather than solverConfigId
    @Async
    public boolean terminateEarlyVrpPlanningSolver(String problemId){
        try{
            VrpTaskPlanningSolver solver;
            log.info("*****Terminating solver for problem id:{}",problemId);
            if((solver=runningSolversPerProblem.get(problemId))!=null){
                 solver.terminateEarly();
                //runningSolversPerProblem.remove(problemId);
                return true;
            }
        }catch (Exception e){
            log.error("exception stopping solver from different thread.",e);
            return false;
        }
        return true;
    }
    //TODO make this run on problemId(submissionId) rather than solverConfigId
    public boolean clearActiveVrpPlanningSolver(String problemId){
        try{
            runningSolversPerProblem.remove(problemId);
            return true;
        }catch (Exception e){
            log.error("exception stopping solver from different thread.",e);
            return false;
        }
    }

    private List<File> getDrlFileList(SolverConfigDTO solverConfigDTO){
        List<File> drlFiles = new ArrayList<>();
        try{
        Map<String, File> fileMap = Arrays.asList(new File(appConfig.getDroolFilePath()).listFiles()).stream().collect(Collectors.toMap(k -> k.getName().replace(AppConstants.DROOL_FILE_EXTENTION, ""), v -> v));
        drlFiles.add(fileMap.get(AppConstants.DROOL_BASE_FILE));
        for (ConstraintValueDTO constraintValueDTO : solverConfigDTO.getConstraints()) {
            if (fileMap.containsKey(constraintValueDTO.getName())) {
                drlFiles.add(fileMap.get(constraintValueDTO.getName()));
            }
            ;
        }
        }catch(Exception e){
            e.printStackTrace();
            log.error("Continuing with no drls.");
        }
        return drlFiles;
    }

    private Object[] getSolvedTasks(List<Shift> shifts, Map<Task, Indictment> indictmentMap) {
        List<com.planner.domain.task.Task> tasks = new ArrayList<>();
        List<com.planner.domain.task.Task> drivedTaskList = new ArrayList<>();
        List<PlanningShift> planningShifts = new ArrayList<>(shifts.size());
        Map<String, Indictment> taskIdAndIndictmentMap = getIndictmentMap(indictmentMap);
        for (Shift shift : shifts) {
            Task nextTask = shift.getNextTask();
            if(shift.getPlannedEndTime()!=null) {
                planningShifts.add(new PlanningShift(shift.getId(),shift.getEmployee().getId(), DateUtils.getDateByLocalDateAndLocalTime(shift.getLocalDate(), shift.getStartTime().toLocalTime()), Date.from(shift.getPlannedEndTime().atZone(ZoneId.systemDefault()).toInstant())));
                int i = 0;
                while (nextTask != null) {
                    LocalDateTime drivingTimeStart = null;
                    Indictment indictment = taskIdAndIndictmentMap.get(nextTask.getId());
                    if (nextTask.isShiftBreak()) {
                        com.planner.domain.task.Task breakTask = new com.planner.domain.task.Task("dt_" + i + "" + nextTask.getId().toString(), nextTask.getInstallationNo(), new Double(nextTask.getLatitude()), new Double(nextTask.getLongitude()), null, nextTask.getDuration(), nextTask.getStreetName(), new Integer(nextTask.getHouseNo()), nextTask.getBlock(), nextTask.getFloorNo(), nextTask.getPost(), nextTask.getCity());
                        breakTask.setPlannedStartTime(nextTask.getPlannedStartTime());
                        breakTask.setStaffId(new Long(shift.getEmployee().getId()));
                        breakTask.setShiftId(shift.getId());
                        breakTask.setBreakTime(true);
                        drivingTimeStart = nextTask.getPlannedStartTime().plusMinutes(nextTask.getDuration());
                        breakTask.setPlannedEndTime(drivingTimeStart);
                        drivedTaskList.add(breakTask);
                    } else {
                        com.planner.domain.task.Task task = new com.planner.domain.task.Task(nextTask.getId().toString(), nextTask.getInstallationNo(), new Double(nextTask.getLatitude()), new Double(nextTask.getLongitude()), null, nextTask.getPlannedDuration(), nextTask.getStreetName(), new Integer(nextTask.getHouseNo()), nextTask.getBlock(), nextTask.getFloorNo(), nextTask.getPost(), nextTask.getCity());
                        task.setPlannedStartTime(nextTask.getPlannedStartTime());
                        task.setDrivingTime(nextTask.getDrivingTime());
                        task.setShiftId(shift.getId());
                        drivingTimeStart = nextTask.getPlannedStartTime().plusMinutes((int) nextTask.getPlannedDuration());
                        task.setPlannedEndTime(drivingTimeStart);
                        task.setStaffId(new Long(shift.getEmployee().getId()));
                        if (indictment != null) {
                            HardMediumSoftLongScore score = ((HardMediumSoftLongScore) indictment.getScoreTotal());
                            if (score.getHardScore() < 0 || score.getMediumScore() < 0) {
                                //excalatedTaskList.add(task);
                                task.setEscalated(true);
                            }
                        }
                        tasks.add(task);
                    }
                    nextTask = nextTask.getNextTask();
                    if (nextTask != null) {
                        int drivingMin = nextTask.getDrivingTime();
                        com.planner.domain.task.Task drivedTask = new com.planner.domain.task.Task("dt_" + i + "" + nextTask.getId().toString(), nextTask.getInstallationNo(), new Double(nextTask.getLatitude()), new Double(nextTask.getLongitude()), null, drivingMin, nextTask.getStreetName(), new Integer(nextTask.getHouseNo()), nextTask.getBlock(), nextTask.getFloorNo(), nextTask.getPost(), nextTask.getCity());
                        drivedTask.setPlannedStartTime(drivingTimeStart);
                        drivedTask.setDrivingDistance(nextTask.getDrivingDistance());
                        drivedTask.setShiftId(shift.getId());
                        drivedTask.setStaffId(new Long(shift.getEmployee().getId()));
                        drivedTask.setPlannedEndTime(drivingTimeStart.plusMinutes(drivingMin));
                        drivedTaskList.add(drivedTask);
                    }
                    i++;
                }

            }
            shift.setNextTask(null);
        }
        return new Object[]{planningShifts, tasks, drivedTaskList};
    }


    private Map<String, Indictment> getIndictmentMap(Map<Task, Indictment> indictmentMap) {
        Map<String, Indictment> taskIndictmentMap = new HashMap<>(indictmentMap.size());
        indictmentMap.entrySet().forEach(i -> {
            if (i.getKey() instanceof Task) {
                taskIndictmentMap.put(i.getKey().getId(), i.getValue());
            }
        });
        return taskIndictmentMap;
    }

}
