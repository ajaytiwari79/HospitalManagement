package com.planner.service.planning_problem;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.enums.planning_problem.PlanningProblemType;
import com.planner.domain.planning_problem.PlanningProblem;
import com.planner.repository.planning_problem.PlanningProblemRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
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
        return ObjectMapperUtils.copyPropertiesOfListByMapper(planningProblems, PlanningProblemDTO.class);
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
}
