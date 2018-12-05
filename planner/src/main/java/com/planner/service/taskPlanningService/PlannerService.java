package com.planner.service.taskPlanningService;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.task.TaskDTO;
import com.kairos.dto.planner.planninginfo.PlanningSubmissionDTO;
import com.kairos.dto.planner.planninginfo.PlanningSubmissonResponseDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.dto.planner.vrp.vrpPlanning.VRPIndictmentDTO;
import com.kairos.dto.planner.vrp.vrpPlanning.VrpTaskPlanningDTO;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.planner.domain.taskPlanning.PlanningProblem;
import com.planner.domain.vrpPlanning.VRPPlanningSolution;
import com.planner.enums.PlanningStatus;
import com.planner.repository.solver_config.SolverConfigRepository;
import com.planner.repository.taskPlanningRepository.PlanningRepository;
import com.planner.repository.vrpPlanning.IndictmentMongoRepository;
import com.planner.repository.vrpPlanning.VRPPlanningMongoRepository;
import com.planner.service.config.DroolsConfigService;
import com.planner.service.config.PathProvider;
import com.planner.service.rest_client.PlannerRestClient;
import com.planner.service.shift_planning.ShiftPlanningInitializationService;
import com.planner.service.solverconfiguration.UnitSolverConfigService;
import com.planner.service.tomtomService.TomTomService;
import com.planner.service.vrpService.VRPGeneratorService;
import com.planner.util.wta.FileIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;


@Service
public class PlannerService {

    private static Logger log= LoggerFactory.getLogger(PlannerService.class);

    @Autowired
    private PlanningRepository planningRepository;
    @Autowired private DroolsConfigService droolsConfigService;
    @Autowired private SolverConfigRepository solverConfigRepository;
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
    @Inject private ShiftPlanningInitializationService shiftPlanningInitializationService;
    @Inject private UnitSolverConfigService unitSolverConfigService;


    public TaskDTO getPlanningProblemByid(String id){
        PlanningProblem planningProblem = (PlanningProblem) planningRepository.findById(id,PlanningProblem.class);
        TaskDTO taskPlanningDTO = new TaskDTO();
        /*taskPlanningDTO.setUnitId(planningProblem.getUnitId());
        taskPlanningDTO.setOptaPlannerId(planningProblem.getId());
        taskPlanningDTO.setPlanningProblemStatus(planningProblem.getStatus().toValue());*/
        return taskPlanningDTO;
    }


    public TaskDTO submitTaskPlanningProblem(TaskDTO taskPlanningDTO){
        SolverConfigDTO solverConfigDTO =null;// solverConfigService.getOneForPlanning(taskPlanningDTO.getSolverConfigId());
        boolean initializedTaskPlanner = initializeTaskPlanner(solverConfigDTO);
        if(initializedTaskPlanner){
            PlanningProblem planningProblem = new PlanningProblem();
            //planningProblem.setUnitId(taskPlanningDTO.getUnitId());
            planningProblem.setStatus(PlanningStatus.UNSOLVED);
            //planningProblem.setProblemXml(getStringBySolutionObject(taskPlanningSolution));
            planningProblem = (PlanningProblem) planningRepository.save(planningProblem);
            //taskPlanningDTO.setOptaPlannerId(planningProblem.getId());
            //taskPlanningDTO.setPlanningProblemStatus(planningProblem.getStatus().toValue());
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
        ShiftRequestPhasePlanningSolution problem=null;//shiftPlanningInitializationService.initializeShiftPlanning(unitId,null,null,null);
        FileIOUtil.writeShiftPlanningXMLToFile(problem,pathProvider.getProblemXmlpath());
        SolverConfigDTO solverConfig=unitSolverConfigService.getSolverConfigWithConstraints(planningSubmissionDTO.getSolverConfigId());
        //FileIOUtil.writeXMLDocumentToFile(solverConfig,pathProvider.getProblemXmlpath());
        try {
            startShiftPlanningSolverOnThisVM(problem,solverConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PlanningSubmissonResponseDTO();
    }
    public boolean startShiftPlanningSolverOnThisVM(ShiftRequestPhasePlanningSolution problem,SolverConfigDTO solverConfig){

        ShiftPlanningSolver shiftPlanningSolver=new ShiftPlanningSolver(solverConfig);
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
