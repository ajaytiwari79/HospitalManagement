package com.planner.service.taskPlanningService;

import com.kairos.planner.planninginfo.PlanningSubmissionDTO;
import com.kairos.planner.planninginfo.PlanningSubmissonResponseDTO;
import com.kairos.planner.solverconfig.SolverConfigDTO;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.util.ObjectMapperUtils;
import com.kairos.vrp.vrpPlanning.VRPIndictmentDTO;
import com.kairos.vrp.vrpPlanning.VrpTaskPlanningDTO;
import com.planner.appConfig.appConfig.AppConfig;
import com.planner.domain.taskPlanning.PlanningProblem;
import com.planner.domain.vrpPlanning.VRPPlanningSolution;
import com.planner.enums.PlanningStatus;
import com.planner.repository.config.SolverConfigRepository;
import com.planner.repository.taskPlanningRepository.PlanningRepository;
import com.planner.repository.vrpPlanning.IndictmentMongoRepository;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
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
    @Autowired private PlannerRestClient plannerRestClient;
    @Autowired private VRPPlannerService vrpPlannerService;
    @Autowired private IndictmentMongoRepository indictmentMongoRepository;


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
        VRPPlanningSolution solution = vrpPlanningMongoRepository.getSolutionBySolverConfigId(vrpTaskPlanningDTO.getSolverConfig().getId());
        if(solution!=null){
            vrpPlanningMongoRepository.delete(solution);
        }
        vrpPlannerService.startVRPPlanningSolverOnThisVM(vrpTaskPlanningDTO);
        return true;
    }

    public boolean stopVRPPlanning(BigInteger solverConfigId){
        vrpPlannerService.terminateEarlyVrpPlanningSolver(solverConfigId.toString());
        return true;
    }





    public VrpTaskPlanningDTO getSolutionBySolverConfigId(BigInteger solverConfigId){
        /*VrpTaskPlanningSolution solution = (VrpTaskPlanningSolution)new VrpTaskPlanningSolver(null).getxStream().fromXML(new File("optaplanner-vrp-taskplanning/src/main/resources/solution.xml"));

        Object[] solvedTasks = getSolvedTasks(solution.getShifts());
        VRPPlanningSolution vrpPlanningSolution = new VRPPlanningSolution(solution.getSolverConfigId(),(List<PlanningShift>) solvedTasks[0],solution.getEmployees(),(List<com.planner.domain.task.Task>) solvedTasks[1],(List<com.planner.domain.task.Task>) solvedTasks[2]);*/
        VRPPlanningSolution solution = vrpPlanningMongoRepository.getSolutionBySolverConfigId(solverConfigId);
       /* vrpPlanningSolution.setSolverConfigId(solverConfigId);
        vrpPlanningMongoRepository.save(vrpPlanningSolution);*/
/*
        */return ObjectMapperUtils.copyPropertiesByMapper(solution,VrpTaskPlanningDTO.class);
    }

    public VRPIndictmentDTO getIndictmentBySolverConfigId(BigInteger solverConfigId){
        VRPIndictmentDTO vrpIndictmentDTO = indictmentMongoRepository.getIndictmentBySolverConfigId(solverConfigId);
        return vrpIndictmentDTO;
    }





}
