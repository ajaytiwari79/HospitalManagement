package com.planner.service.shift_planning;

import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.planner.solverconfig.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.shiftplanning.utils.ShiftPlanningInitializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.kairos.enums.constraint.ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH;
import static com.kairos.enums.constraint.ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF;

@Service
public class ShiftPlanningInitializationService {

    /**
     * ShiftRequestPhasePlanningSolution(Opta-planner planning Solution)
     */
    public ShiftRequestPhasePlanningSolution initializeShiftPlanning(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        shiftPlanningProblemSubmitDTO.setSolverConfig(getSolverConfigDTO());
        ShiftPlanningSolver.serverAddress = "http://localdev.kairosplanning.com/";
        new ShiftPlanningInitializer().initializeShiftPlanning(shiftPlanningProblemSubmitDTO);
        return null;
    }


    public SolverConfigDTO getSolverConfigDTO() {
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ScoreLevel.HARD, 5));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ScoreLevel.HARD, 5));
        return new SolverConfigDTO(constraintDTOS);
    }

}
