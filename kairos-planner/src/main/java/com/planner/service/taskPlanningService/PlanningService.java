package com.planner.service.taskPlanningService;

import com.kairos.dto.planninginfo.PlanningSubmissionDTO;
import com.kairos.dto.planninginfo.PlanningSubmissonResponseDTO;
import com.kairos.planning.solution.TaskPlanningSolution;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.planner.domain.solverconfig.SolverConfig;
import com.planner.domain.taskPlanning.PlanningProblem;
import com.planner.enums.PlanningStatus;
import com.planner.repository.config.SolverConfigRepository;
import com.planner.repository.taskPlanningRepository.PlanningRepository;
import com.planner.responseDto.PlanningDto.taskplanning.TaskPlanningDTO;
import com.planner.responseDto.config.SolverConfigDTO;
import com.planner.service.config.DroolsConfigService;
import com.planner.service.config.PathProvider;
import com.planner.service.config.SolverConfigService;
import com.planner.service.shiftPlanningService.ShiftPlanningService;
import com.planner.util.wta.ShiftPlanningUtil;
import com.thoughtworks.xstream.XStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


@Service
public class PlanningService {

    private static Logger log= LoggerFactory.getLogger(PlanningService.class);

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


    private String getStringBySolutionObject(Object object){
        XStream xStream = new XStream();
        xStream.setMode(XStream.ID_REFERENCES);
        String xml = xStream.toXML(object);
       /* try {
            PrintWriter out = new PrintWriter("/media/pradeep/bak/Kairos/kairosCore/task/task.xml");
            out.println(xml);
            out.close();
            System.out.println("file complete");
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }*/
        return xml;
    }


    public PlanningSubmissonResponseDTO submitShiftPlanningProblem(Long unitId, PlanningSubmissionDTO planningSubmissionDTO) {
        ShiftRequestPhasePlanningSolution problem=shiftPlanningService.createShiftPlanningProblem(unitId,planningSubmissionDTO.getDates());
        ShiftPlanningUtil.toXml(problem,pathProvider.getProblemXmlpath());
        solverConfigService.createShiftPlanningSolverConfig(planningSubmissionDTO.getSolverConfigId());
        try {
            plannerLauncherService.instantiateNewSolver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new PlanningSubmissonResponseDTO();
    }
}
