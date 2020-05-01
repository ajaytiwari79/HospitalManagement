package com.planner.service.shift_planning;

import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
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
        new ShiftPlanningInitializer().initializeShiftPlanning(shiftPlanningProblemSubmitDTO);
        return null;
    }


    public SolverConfigDTO getSolverConfigDTO() {
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        constraintDTOS.add(new ConstraintDTO("Shortest duration for this activity, relative to shift length", "Shortest duration for this activity, relative to shift length", ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ScoreLevel.HARD, 5, 5l));
        constraintDTOS.add(new ConstraintDTO("Max number of allocations pr. shift for this activity per staff", "Max number of allocations pr. shift for this activity per staff", ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ScoreLevel.HARD, 5, 5l));
        return new SolverConfigDTO(constraintDTOS);
    }

}
