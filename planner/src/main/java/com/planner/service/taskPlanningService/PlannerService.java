package com.planner.service.taskPlanningService;

import com.kairos.enums.IntegrationOperation;
import com.kairos.util.DateUtils;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.planner.planninginfo.PlanningSubmissionDTO;
import com.kairos.planner.planninginfo.PlanningSubmissonResponseDTO;
import com.kairos.planner.solverconfig.ConstraintValueDTO;
import com.kairos.planner.solverconfig.SolverConfigDTO;
import com.kairos.planner.vrp.taskplanning.model.*;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import com.kairos.planner.vrp.taskplanning.solver.VrpTaskPlanningSolver;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.kairos.vrp.vrpPlanning.VrpTaskPlanningDTO;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.planner.domain.staff.PlanningShift;
import com.planner.domain.taskPlanning.PlanningProblem;
import com.planner.domain.vrpPlanning.VRPPlanningSolution;
import com.planner.enums.PlanningStatus;
import com.planner.repository.config.SolverConfigRepository;
import com.planner.repository.taskPlanningRepository.PlanningRepository;
import com.planner.repository.vrpPlanning.VRPPlanningMongoRepository;
import com.planner.responseDto.PlanningDto.taskplanning.TaskPlanningDTO;
import com.planner.service.Client.PlannerRestClient;
import com.planner.service.config.DroolsConfigService;
import com.planner.service.config.PathProvider;
import com.planner.service.config.SolverConfigService;
import com.planner.service.shiftPlanningService.ShiftPlanningService;
import com.planner.service.tomtomService.TomTomService;
import com.planner.service.vrpService.VRPGeneratorService;
import com.planner.util.wta.FileIOUtil;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PlannerService {

    private static Logger log= LoggerFactory.getLogger(PlannerService.class);

    @Autowired
    private PlanningRepository planningRepository;
    @Autowired private TaskPlanningSolutionService taskPlanningSolutionService;
    @Autowired private DroolsConfigService droolsConfigService;
    @Autowired private SolverConfigService solverConfigService;
    @Autowired private SolverConfigRepository solverConfigRepository;
    @Autowired private ShiftPlanningService shiftPlanningService;
    @Autowired
    private PathProvider pathProvider;
    @Autowired
    private PlannerLauncherService plannerLauncherService;
    @Autowired private TomTomService tomTomService;
    @Autowired private VRPGeneratorService vrpGeneratorService;
    @Autowired private VRPPlanningMongoRepository vrpPlanningMongoRepository;
    @Autowired private PlannerRestClient plannerRestClient;

    public TaskPlanningDTO getPlanningProblemByid(String id){
        PlanningProblem planningProblem = (PlanningProblem) planningRepository.findById(id,PlanningProblem.class);
        TaskPlanningDTO taskPlanningDTO = new TaskPlanningDTO();
        taskPlanningDTO.setUnitId(planningProblem.getUnitId());
        taskPlanningDTO.setOptaPlannerId(planningProblem.getId());
        taskPlanningDTO.setPlanningProblemStatus(planningProblem.getStatus().toValue());
        return taskPlanningDTO;
    }


    public TaskPlanningDTO submitTaskPlanningProblem(TaskPlanningDTO taskPlanningDTO){
        SolverConfigDTO solverConfigDTO =null;// solverConfigService.getOneForPlanning(taskPlanningDTO.getSolverConfigId());
        boolean initializedTaskPlanner = initializeTaskPlanner(solverConfigDTO);
        if(initializedTaskPlanner){
            PlanningProblem planningProblem = new PlanningProblem();
            planningProblem.setUnitId(taskPlanningDTO.getUnitId());
            planningProblem.setStatus(PlanningStatus.UNSOLVED);
            //planningProblem.setProblemXml(getStringBySolutionObject(taskPlanningSolution));
            planningProblem = (PlanningProblem) planningRepository.save(planningProblem);
            taskPlanningDTO.setOptaPlannerId(planningProblem.getId());
            taskPlanningDTO.setPlanningProblemStatus(planningProblem.getStatus().toValue());
        }
        return taskPlanningDTO;
    }


    private boolean initializeTaskPlanner(SolverConfigDTO solverConfigDTO){
        //String drlFilePath = droolsConfigService.getDroolFilePath(solverConfigDTO);
        /*long [] hardLevel = new long[solverConfigDTO.getHardLevel()];
        long [] softLevel = new long[solverConfigDTO.getMediumLevel()+solverConfigDTO.getSoftLevel()];*/
        TaskPlanningSolution taskPlanningSolution = null;//taskPlanningSolutionService.getTaskPlanningSolutionByDate(taskPlanningDTO);
        //taskPlanningSolution.setBendableScore(hardLevel,softLevel);
        return true;
    }





    private String getObjectOutputStream(TaskPlanningSolution taskPlanningSolution){
        /*try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(""));
            outputStream.writeObject(taskPlanningSolution);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        return null;
    }


    public PlanningSubmissonResponseDTO submitShiftPlanningProblem(Long unitId, PlanningSubmissionDTO planningSubmissionDTO) {
        ShiftRequestPhasePlanningSolution problem=shiftPlanningService.createShiftPlanningProblem(unitId,planningSubmissionDTO.getDates());
        FileIOUtil.writeShiftPlanningXMLToFile(problem,pathProvider.getProblemXmlpath());
        Document solverConfig=solverConfigService.createShiftPlanningSolverConfig(planningSubmissionDTO.getSolverConfigId());
        FileIOUtil.writeXMLDocumentToFile(solverConfig,pathProvider.getProblemXmlpath());
        try {
            startShiftPlanningSolverOnThisVM(problem,pathProvider.getProblemXmlpath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PlanningSubmissonResponseDTO();
    }
    public boolean startShiftPlanningSolverOnThisVM(ShiftRequestPhasePlanningSolution problem,String solverConfigPath){
        ShiftPlanningSolver shiftPlanningSolver=new ShiftPlanningSolver(solverConfigPath);
        ShiftRequestPhasePlanningSolution solution=shiftPlanningSolver.runSolverOnRequest(problem);
        return true;
    }

    public boolean submitVRPPlanning(VrpTaskPlanningDTO vrpTaskPlanningDTO){
        VRPPlanningSolution solution = vrpPlanningMongoRepository.getSolutionBySolverConfigId(vrpTaskPlanningDTO.getSolverConfig().getId());
        if(solution!=null){
            vrpPlanningMongoRepository.delete(solution);
        }
        startVRPPlanningSolverOnThisVM(vrpTaskPlanningDTO);
        return true;
    }

    @Async
    public void startVRPPlanningSolverOnThisVM(VrpTaskPlanningDTO vrpTaskPlanningDTO){
        VrpTaskPlanningSolution solution = vrpGeneratorService.getVRPProblemSolution(vrpTaskPlanningDTO);
        List<File> drlFileList = getDrlFileList(vrpTaskPlanningDTO.getSolverConfig());
        VrpTaskPlanningSolver solver = new VrpTaskPlanningSolver(drlFileList);
        Object[] objects = solver.solveProblemOnRequest(solution);
        solution = (VrpTaskPlanningSolution)objects[0];
        Map<Task,Indictment> indictmentMap = (Map<Task,Indictment>)objects[1];
        Object[] solvedTasks = getSolvedTasks(solution.getShifts());
        VRPPlanningSolution vrpPlanningSolution = new VRPPlanningSolution(solution.getSolverConfigId(),(List<PlanningShift>) solvedTasks[0],solution.getEmployees(),(List<com.planner.domain.task.Task>) solvedTasks[1],(List<com.planner.domain.task.Task>) solvedTasks[2]);
        vrpPlanningMongoRepository.save(vrpPlanningSolution);
        plannerRestClient.publish(null,vrpTaskPlanningDTO.getSolverConfig().getUnitId(), IntegrationOperation.CREATE,vrpTaskPlanningDTO.getSolverConfig().getId());
    }

    private List<File> getDrlFileList(SolverConfigDTO solverConfigDTO){
        Map<String,File> fileMap = Arrays.asList(new File("src/main/resources/droolsFile").listFiles()).stream().collect(Collectors.toMap(k->k.getName().replace(".drl",""),v->v));
        List<File> drlFiles = new ArrayList<>();
        for (ConstraintValueDTO constraintValueDTO : solverConfigDTO.getConstraints()) {
            if(fileMap.containsKey(constraintValueDTO.getName())){
                drlFiles.add(fileMap.get(constraintValueDTO.getName()));
            };
        }
        return drlFiles;
    }

    public VrpTaskPlanningDTO getSolutionBySolverConfigId(BigInteger solverConfigId){
        /*VrpTaskPlanningSolution solution = (VrpTaskPlanningSolution)new VrpTaskPlanningSolver().getxStream().fromXML(new File("optaplanner-vrp-taskplanning/src/main/resources/solution-2594soft.xml"));

        Object[] solvedTasks = getSolvedTasks(solution.getShifts());
        VRPPlanningSolution vrpPlanningSolution = new VRPPlanningSolution(solution.getSolverConfigId(),(List<PlanningShift>) solvedTasks[0],solution.getEmployees(),(List<com.planner.domain.task.Task>) solvedTasks[1],(List<com.planner.domain.task.Task>) solvedTasks[2]);
        */VRPPlanningSolution solution = vrpPlanningMongoRepository.getSolutionBySolverConfigId(solverConfigId);
        //vrpPlanningSolution.setSolverConfigId(solverConfigId);
        //vrpPlanningMongoRepository.save(vrpPlanningSolution);
/*
        */return ObjectMapperUtils.copyPropertiesByMapper(solution,VrpTaskPlanningDTO.class);
    }

    private Object[] getSolvedTasks(List<Shift> shifts){
        List<com.planner.domain.task.Task> tasks = new ArrayList<>();
        List<com.planner.domain.task.Task> drivedTaskList = new ArrayList<>();
        List<PlanningShift> planningShifts = new ArrayList<>(shifts.size());
        for (Shift shift:shifts){
            Task nextTask = shift.getNextTask();
            if(shift.getEndTime()!=null) {
                planningShifts.add(new PlanningShift(shift.getEmployee().getId(), DateUtils.getDateByLocalDateAndLocalTime(shift.getLocalDate(), Shift.getDefaultShiftStart()), Date.from(shift.getEndTime().atZone(ZoneId.systemDefault()).toInstant())));
                int i = 0;
                if (nextTask != null) {
                    while (true) {
                        LocalDateTime drivingTimeStart = null;
                        if(nextTask.isShiftBreak()){
                            com.planner.domain.task.Task drivedTask = new com.planner.domain.task.Task("dt_"+i+""+ nextTask.getId().toString(), nextTask.getInstallationNo(), new Double(nextTask.getLatitude()), new Double(nextTask.getLongitude()), null, nextTask.getDrivingTimeSeconds(), nextTask.getStreetName(), new Integer(nextTask.getHouseNo()), nextTask.getBlock(), nextTask.getFloorNo(), nextTask.getPost(), nextTask.getCity());
                            drivedTask.setPlannedStartTime(nextTask.getPlannedStartTime());
                            drivedTask.setStaffId(new Long(shift.getEmployee().getId()));
                            drivedTask.setBreakTime(true);
                            drivingTimeStart = nextTask.getPlannedStartTime().plusMinutes(nextTask.getDuration());
                            drivedTask.setPlannedEndTime(drivingTimeStart);
                            drivedTaskList.add(drivedTask);
                        }else {
                            com.planner.domain.task.Task task = new com.planner.domain.task.Task(nextTask.getId().toString(), nextTask.getInstallationNo(), new Double(nextTask.getLatitude()), new Double(nextTask.getLongitude()), null, nextTask.getDuration(), nextTask.getStreetName(), new Integer(nextTask.getHouseNo()), nextTask.getBlock(), nextTask.getFloorNo(), nextTask.getPost(), nextTask.getCity());
                            task.setPlannedStartTime(nextTask.getPlannedStartTime());
                            drivingTimeStart = nextTask.getPlannedStartTime().plusMinutes(nextTask.getDuration());
                            task.setPlannedEndTime(drivingTimeStart);
                            task.setStaffId(new Long(shift.getEmployee().getId()));
                            tasks.add(task);
                        }
                        if (nextTask.getNextTask() != null) {
                            nextTask = nextTask.getNextTask();
                            com.planner.domain.task.Task drivedTask = new com.planner.domain.task.Task("dt_"+i+"" + nextTask.getId().toString(), nextTask.getInstallationNo(), new Double(nextTask.getLatitude()), new Double(nextTask.getLongitude()), null, nextTask.getDrivingTimeSeconds(), nextTask.getStreetName(), new Integer(nextTask.getHouseNo()), nextTask.getBlock(), nextTask.getFloorNo(), nextTask.getPost(), nextTask.getCity());
                            drivedTask.setPlannedStartTime(drivingTimeStart);
                            drivedTask.setStaffId(new Long(shift.getEmployee().getId()));
                            drivedTask.setPlannedEndTime(drivingTimeStart.plusSeconds(nextTask.getDrivingTimeSeconds()));
                            drivedTaskList.add(drivedTask);
                        } else if(nextTask.getNextTask() == null){
                            break;
                        }
                        i++;
                    }
                }
            }
            shift.setNextTask(null);
        }
        return new Object[]{planningShifts,tasks,drivedTaskList};
    }



}
