package com.planner.service.shift_planning;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.planner.solverconfig.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.integration.ActivityIntegration;
import com.kairos.shiftplanning.integration.UserIntegration;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;
import com.kairos.shiftplanning.utils.ShiftPlanningInitializer;
import com.planner.component.rest_client.GenericRestClient;
import com.planner.component.rest_client.IntegrationService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.enums.constraint.ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH;
import static com.kairos.enums.constraint.ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF;

@Service
public class ShiftPlanningInitializationService {

    @Inject
    private IntegrationService integrationService;

    /**
     * ShiftRequestPhasePlanningSolution(Opta-planner planning Solution)
     */
    public ShiftRequestPhasePlanningSolution initializeShiftPlanning(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        shiftPlanningProblemSubmitDTO.setSolverConfig(getSolverConfigDTO());
        Long unitId = shiftPlanningProblemSubmitDTO.getUnitId();
        integrationService.updateDataOfShiftForPlanningFromUserService(shiftPlanningProblemSubmitDTO);
        integrationService.updateDataOfShiftForPlanningFromActivityService(shiftPlanningProblemSubmitDTO);
        String objectString = ObjectMapperUtils.objectToJsonString(shiftPlanningProblemSubmitDTO);
        writeProblemToTheFile(objectString,shiftPlanningProblemSubmitDTO.getPlanningProblemId());
        try (PrintWriter printWriter = new PrintWriter(new File(System.getProperty("user.home")+"/problem.json"))){
            printWriter.write(objectString);
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public SolverConfigDTO getSolverConfigDTO() {
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ScoreLevel.HARD, 5));
        constraintDTOS.add(new ConstraintDTO(ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ScoreLevel.HARD, 5));
        return new SolverConfigDTO(constraintDTOS);
    }

    void writeProblemToTheFile(String objectString, BigInteger planningProblemId){
        new Thread(()->{

            try {
                String filePath = System.getProperty("user.home") + "/" + planningProblemId;
                File file = new File(filePath);
                if(!file.exists()){
                    file.createNewFile();
                }
                PrintWriter printWriter = new PrintWriter(filePath);
                printWriter.write(objectString);
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

}
