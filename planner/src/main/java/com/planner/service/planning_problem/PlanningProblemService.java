package com.planner.service.planning_problem;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.enums.planning_problem.PlanningProblemType;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.planner.domain.planning_problem.PlanningProblem;
import com.planner.enums.PlanningProblemStatus;
import com.planner.repository.planning_problem.PlanningProblemRepository;
import com.planner.util.wta.FileIOUtil;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PlanningProblemService {
    @Inject
    private PlanningProblemRepository planningProblemRepository;

    public void createPlanningProblem(PlanningProblemDTO planningProblemDTO) {
        PlanningProblem planningProblem = ObjectMapperUtils.copyPropertiesByMapper(planningProblemDTO, PlanningProblem.class);
        planningProblemRepository.saveEntity(planningProblem);
    }

    public PlanningProblemDTO getPlanningProblem(String planningProblemDTOId) {
        Optional<PlanningProblem> planningProblemOptional = planningProblemRepository.findById(planningProblemDTOId);
        PlanningProblemDTO planningProblemDTO = null;
        if (planningProblemOptional.isPresent()) {
            planningProblemDTO = ObjectMapperUtils.copyPropertiesByMapper(planningProblemOptional.get(), PlanningProblemDTO.class);
        }
        return planningProblemDTO;
    }

    public List<PlanningProblemDTO> getAllPlanningProblem() {
        List<PlanningProblem> planningProblems = planningProblemRepository.findAll();
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(planningProblems, PlanningProblemDTO.class);
    }

    public void updatePlanningProblem(PlanningProblemDTO planningProblemDTO) {
        Optional<PlanningProblem> planningProblemOptional = planningProblemRepository.findById(planningProblemDTO.getId() + "");
        PlanningProblem planningProblem;
        if (planningProblemOptional.isPresent()) {
            planningProblem = planningProblemOptional.get();
            planningProblemRepository.saveEntity(planningProblem);
        }

    }

    public void deletePlanningProblem(String planningProblemDTOId) {
        Optional<PlanningProblem> planningProblemOptional = planningProblemRepository.findById(planningProblemDTOId);
        PlanningProblem planningProblem;
        if (planningProblemOptional.isPresent()) {
            planningProblem = planningProblemOptional.get();
            planningProblem.setDeleted(true);
            planningProblemRepository.saveEntity(planningProblem);
        }
    }

    public PlanningProblemDTO createDefaultPlanningProblem() {
        PlanningProblem defaultPlanningProblem = new PlanningProblem();
        defaultPlanningProblem.setName("ShiftPlanning");
        defaultPlanningProblem.setDescription("This is for ShiftPlanning");
        defaultPlanningProblem.setType(PlanningProblemType.SHIFT_PLANNING);
        planningProblemRepository.saveEntity(defaultPlanningProblem);
        PlanningProblemDTO planningProblemDTO = ObjectMapperUtils.copyPropertiesByMapper(defaultPlanningProblem, PlanningProblemDTO.class);
        return planningProblemDTO;
    }

    public BigInteger addProblemFileAndGetPlanningProblemID(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO, Date fromPlanningDate, Date toPlanningDate, ShiftRequestPhasePlanningSolution shiftRequestPhasePlanningSolution){
        PlanningProblem planningProblem;
        if(shiftPlanningProblemSubmitDTO.getPlanningProblemId() != null){
            planningProblem = planningProblemRepository.findByIdNotDeleted(shiftPlanningProblemSubmitDTO.getPlanningPeriodId());
        }else{
            planningProblem = new PlanningProblem();
        }
        planningProblem.setPlanningStartDate(fromPlanningDate);
        planningProblem.setPlanningEndDate(toPlanningDate);
        planningProblem.setType(PlanningProblemType.SHIFT_PLANNING);
        planningProblemRepository.save(planningProblem);
        BigInteger  planningProblemId = planningProblem.getId();
        String problemFile = planningProblemId+"_PROBLEM";
        FileIOUtil.writeShiftPlanningXMLToFile(shiftRequestPhasePlanningSolution,problemFile);
        planningProblem.setStatus(PlanningProblemStatus.IN_PROGRESS);
        planningProblem.setProblemFileName(problemFile);
        planningProblemRepository.save(planningProblem);
        return planningProblemId;
    }


    public void addSolutionFile( ShiftRequestPhasePlanningSolution planningSolution,BigInteger  planningProblemId){
      PlanningProblem planningProblem = planningProblemRepository.findByIdNotDeleted(planningProblemId);
        String solutionFile = planningProblemId+"_SOLUTION";
        FileIOUtil.writeShiftPlanningXMLToFile(planningSolution,solutionFile);
        planningProblem.setStatus(PlanningProblemStatus.SOLVED);
        planningProblem.setSolutionFileName(solutionFile);
        planningProblemRepository.save(planningProblem);
    }


}
