package com.planner.service.taskPlanningService;

import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.dto.planninginfo.PlanningSubmissionDTO;
import com.kairos.dto.planninginfo.PlanningSubmissonResponseDTO;
import com.kairos.dto.solverconfig.ConstraintValueDTO;
import com.kairos.dto.solverconfig.SolverConfigDTO;
import com.kairos.planner.vrp.taskplanning.model.*;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import com.kairos.planner.vrp.taskplanning.solver.VrpTaskPlanningSolver;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.kairos.response.dto.web.planning.vrpPlanning.TaskDTO;
import com.kairos.response.dto.web.planning.vrpPlanning.VrpTaskPlanningDTO;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.planner.domain.staff.PlanningShift;
import com.planner.domain.taskPlanning.PlanningProblem;
import com.planner.domain.tomtomResponse.Matrix;
import com.planner.domain.vrpPlanning.VRPPlanningSolution;
import com.planner.enums.PlanningStatus;
import com.planner.repository.config.SolverConfigRepository;
import com.planner.repository.taskPlanningRepository.PlanningRepository;
import com.planner.repository.vrpPlanning.VRPPlanningMongoRepository;
import com.planner.responseDto.PlanningDto.taskplanning.TaskPlanningDTO;
import com.planner.service.config.DroolsConfigService;
import com.planner.service.config.PathProvider;
import com.planner.service.config.SolverConfigService;
import com.planner.service.shiftPlanningService.ShiftPlanningService;
import com.planner.service.tomtomService.TomTomService;
import com.planner.service.vrpService.VRPGeneratorService;
import com.planner.util.wta.FileIOUtil;
import com.thoughtworks.xstream.XStream;
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
   // @Autowired private

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
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(""));
            outputStream.writeObject(taskPlanningSolution);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        /*VrpTaskPlanningSolution solution = (VrpTaskPlanningSolution)new VrpTaskPlanningSolver().getxStream().fromXML(new File("optaplanner-vrp-taskplanning/src/main/resources/solution.xml"));

        Object[] solvedTasks = getSolvedTasks(solution.getShifts());
        VRPPlanningSolution vrpPlanningSolution = new VRPPlanningSolution(solution.getSolverConfigId(),(List<PlanningShift>) solvedTasks[0],solution.getEmployees(),(List<com.planner.domain.task.Task>) solvedTasks[1],(List<com.planner.domain.task.Task>) solvedTasks[2]);
        */VRPPlanningSolution solution = vrpPlanningMongoRepository.getSolutionBySolverConfigId(solverConfigId);
        /*vrpPlanningSolution.setSolverConfigId(solverConfigId);
        vrpPlanningMongoRepository.save(vrpPlanningSolution);
        */return ObjectMapperUtils.copyPropertiesByMapper(solution,VrpTaskPlanningDTO.class);
    }

    private Object[] getSolvedTasks(List<Shift> shifts){
        List<com.planner.domain.task.Task> taskDTOS = new ArrayList<>();
        List<com.planner.domain.task.Task> drivedTaskList = new ArrayList<>();
        List<PlanningShift> planningShifts = new ArrayList<>(shifts.size());
        for (Shift shift:shifts){
            Task nextTask = shift.getNextTask();
            planningShifts.add(new PlanningShift(shift.getEmployee().getId(), Date.from(shift.getStartTime().atZone(ZoneId.systemDefault()).toInstant()),Date.from(shift.getEndTime().atZone(ZoneId.systemDefault()).toInstant())));
            if(nextTask!=null){
                while (true){
                    com.planner.domain.task.Task task = new com.planner.domain.task.Task(nextTask.getId().toString(),nextTask.getInstallationNo(),new Double(nextTask.getLatitude()),new Double(nextTask.getLongitude()),null,nextTask.getDuration(),nextTask.getStreetName(),new Integer(nextTask.getHouseNo()),nextTask.getBlock(),nextTask.getFloorNo(),nextTask.getPost(),nextTask.getCity());
                    task.setPlannedStartTime(nextTask.getPlannedStartTime());
                    LocalDateTime drivingTimeStart = nextTask.getPlannedStartTime().plusMinutes(nextTask.getDuration());
                    task.setPlannedEndTime(drivingTimeStart);
                    task.setStaffId(new Long(shift.getEmployee().getId()));
                    taskDTOS.add(task);
                    if(nextTask.getNextTask()!=null){
                        nextTask = nextTask.getNextTask();
                        com.planner.domain.task.Task drivedTask = new com.planner.domain.task.Task(nextTask.getId().toString(),nextTask.getInstallationNo(),new Double(nextTask.getLatitude()),new Double(nextTask.getLongitude()),null,nextTask.getDrivingTimeSeconds(),nextTask.getStreetName(),new Integer(nextTask.getHouseNo()),nextTask.getBlock(),nextTask.getFloorNo(),nextTask.getPost(),nextTask.getCity());
                        drivedTask.setPlannedStartTime(drivingTimeStart);
                        drivedTask.setStaffId(new Long(shift.getEmployee().getId()));
                        drivedTask.setPlannedEndTime(drivingTimeStart.plusSeconds(nextTask.getDrivingTimeSeconds()));
                        drivedTaskList.add(drivedTask);
                    }else {
                        break;
                    }
                }
            }
            shift.setNextTask(null);
        }
        return new Object[]{planningShifts,taskDTOS,drivedTaskList};
    }



}
