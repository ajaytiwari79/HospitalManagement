package com.planner.service.taskPlanningService;

import com.kairos.activity.util.ObjectMapperUtils;
import com.kairos.dto.planninginfo.PlanningSubmissionDTO;
import com.kairos.dto.planninginfo.PlanningSubmissonResponseDTO;
import com.kairos.planner.vrp.taskplanning.model.*;
import com.kairos.planner.vrp.taskplanning.solution.VrpTaskPlanningSolution;
import com.kairos.planner.vrp.taskplanning.solver.VrpTaskPlanningSolver;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.kairos.response.dto.web.planning.vrpPlanning.VrpTaskPlanningDTO;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.planner.domain.taskPlanning.PlanningProblem;
import com.planner.domain.tomtomResponse.Matrix;
import com.planner.domain.vrpPlanning.VRPPlanningSolution;
import com.planner.enums.PlanningStatus;
import com.planner.repository.config.SolverConfigRepository;
import com.planner.repository.taskPlanningRepository.PlanningRepository;
import com.planner.repository.vrpPlanning.VRPPlanningMongoRepository;
import com.planner.responseDto.PlanningDto.taskplanning.TaskPlanningDTO;
import com.planner.responseDto.config.SolverConfigDTO;
import com.planner.service.config.DroolsConfigService;
import com.planner.service.config.PathProvider;
import com.planner.service.config.SolverConfigService;
import com.planner.service.shiftPlanningService.ShiftPlanningService;
import com.planner.service.tomtomService.TomTomService;
import com.planner.service.vrpService.VRPGeneratorService;
import com.planner.util.wta.FileIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


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
        String drlFilePath = droolsConfigService.getDroolFilePath(solverConfigDTO);
        long [] hardLevel = new long[solverConfigDTO.getHardLevel()];
        long [] softLevel = new long[solverConfigDTO.getMediumLevel()+solverConfigDTO.getSoftLevel()];
        TaskPlanningSolution taskPlanningSolution = null;//taskPlanningSolutionService.getTaskPlanningSolutionByDate(taskPlanningDTO);
        taskPlanningSolution.setBendableScore(hardLevel,softLevel);
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
        VrpTaskPlanningSolver solver = new VrpTaskPlanningSolver(true);
        solution = solver.solveProblemOnRequest(solution);
        Object[] objects = getSolvedTasks(solution.getShifts());
        VRPPlanningSolution vrpPlanningSolution = new VRPPlanningSolution(solution.getSolverConfigId(),(List<Shift>) objects[0],solution.getEmployees(),(List<Task>) objects[1]);
        vrpPlanningMongoRepository.save(vrpPlanningSolution);

    }

    public VrpTaskPlanningDTO getSolutionBySolverConfigId(BigInteger solverConfigId){
        VRPPlanningSolution solution = vrpPlanningMongoRepository.getSolutionBySolverConfigId(solverConfigId);
        VrpTaskPlanningDTO vrpTaskPlanningDTO = new VrpTaskPlanningDTO();
        return ObjectMapperUtils.copyPropertiesByMapper(solution,VrpTaskPlanningDTO.class);
    }

    private Object[] getSolvedTasks(List<Shift> shifts){
        List<Task> tasks1 = new ArrayList<>();
        for (Shift shift:shifts){
            Task task = shift.getNextTask();
            while (true){
                tasks1.add(task);
                if(task.getNextTask()!=null){
                    task = task.getNextTask();
                    tasks1.add(task);
                }else {
                    break;
                }
            }
            shift.setNextTask(null);
        }
        return new Object[]{shifts,tasks1};
    }



}
