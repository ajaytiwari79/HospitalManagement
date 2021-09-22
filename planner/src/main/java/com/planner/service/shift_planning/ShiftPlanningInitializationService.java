package com.planner.service.shift_planning;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.planner.solverconfig.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftPlanningSolution;
import com.planner.component.rest_client.IntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.enums.constraint.ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH;
import static com.kairos.enums.constraint.ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF;

@Service
public class ShiftPlanningInitializationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftPlanningInitializationService.class);

    @Inject
    private IntegrationService integrationService;

    /**
     * ShiftRequestPhasePlanningSolution(Opta-planner planning Solution)
     */
    public ShiftPlanningSolution initializeShiftPlanning(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        shiftPlanningProblemSubmitDTO.setSolverConfig(getSolverConfigDTO());
        integrationService.updateDataOfShiftForPlanningFromUserService(shiftPlanningProblemSubmitDTO);
        integrationService.updateDataOfShiftForPlanningFromActivityService(shiftPlanningProblemSubmitDTO);
        String objectString = ObjectMapperUtils.objectToJsonString(shiftPlanningProblemSubmitDTO);
        new Thread(()->writeProblemToTheFile(objectString,shiftPlanningProblemSubmitDTO.getPlanningProblemId().toString())).start();
        writeProblemToTheFile(objectString,"problem.json");
        try {
            new ShiftPlanningSolver().run();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }


    public SolverConfigDTO getSolverConfigDTO() {
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ScoreLevel.HARD, 5));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ScoreLevel.HARD, 5));
        return new SolverConfigDTO(constraintDTOS);
    }

    void writeProblemToTheFile(String objectString, String fileName){
        try {
            String filePath = System.getProperty("user.home") + "/" + fileName;
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter printWriter = new PrintWriter(filePath);
            printWriter.write(objectString);
            printWriter.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

}
