package com.kairos.service.solver_config;

import com.kairos.enums.solver_config.ConstraintCategory;
import com.kairos.enums.solver_config.PlanningType;
import com.kairos.enums.solver_config.SolverConfigStatus;
import com.kairos.persistence.model.solver_config.Constraint;
import com.kairos.persistence.model.solver_config.ConstraintValue;
import com.kairos.persistence.model.solver_config.SolverConfig;
import com.kairos.persistence.repository.solver_config.ConstraintRepository;
import com.kairos.persistence.repository.solver_config.SolverConfigRepository;
import com.kairos.planner.solverconfig.ConstraintDTO;
import com.kairos.planner.solverconfig.DefaultContraintsDTO;
import com.kairos.planner.solverconfig.SolverConfigConstraintWrapper;
import com.kairos.planner.solverconfig.SolverConfigDTO;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author pradeep
 * @date - 20/6/18
 */
@Service
public class SolverConfigService extends MongoBaseService {

    @Inject
    private SolverConfigRepository solverConfigRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ConstraintRepository constraintRepository;

    public SolverConfigDTO createSolverConfig(Long unitId, SolverConfigDTO solverConfigDTO) {
        Boolean exists = solverConfigRepository.existsSolverConfigByNameAndUnitId(unitId, solverConfigDTO.getName());
        if (exists) {
            exceptionService.duplicateDataException("message.solverConfig.exists", solverConfigDTO.getName());
        }
        SolverConfig solverConfig = ObjectMapperUtils.copyPropertiesByMapper(solverConfigDTO, SolverConfig.class);
        solverConfig.setUnitId(unitId);
        save(solverConfig);
        solverConfigDTO.setId(solverConfig.getId());
        return solverConfigDTO;
    }


    public SolverConfigDTO updateSolverConfig(Long unitId,BigInteger solverConfigId, SolverConfigDTO solverConfigDTO) {
        /*Boolean exists = solverConfigRepository.existsSolverConfigByNameAndUnitIdAndSolverConfigId(unitId,solverConfigDTO.getName(),solverConfigDTO.getId());
        if(exists){
            exceptionService.duplicateDataException("message.solverConfig.exists",solverConfigDTO.getName());
        }*/
        if (solverConfigDTO.isDefault()) {
            exceptionService.invalidRequestException("message.solverConfig.default.update");
        }
        SolverConfig solverConfig = solverConfigRepository.findOne(solverConfigId);
        List<ConstraintValue> constraintValues = ObjectMapperUtils.copyPropertiesOfListByMapper(solverConfigDTO.getConstraints(), ConstraintValue.class);
        solverConfig.setConstraints(constraintValues);
        save(solverConfig);
        return solverConfigDTO;
    }


    public SolverConfigConstraintWrapper getAllVRPSolverConfig(Long unitId) {
        List<ConstraintDTO> constraints = constraintRepository.getAllVRPPlanningConstraints(unitId, PlanningType.VRPPLANNING);
        Map<BigInteger,ConstraintDTO> constraintDTOMap = constraints.stream().collect(Collectors.toMap(k->k.getId(), v->v));
        List<SolverConfigDTO> solverConfigs = solverConfigRepository.getAllByUnitId(unitId);
        solverConfigs.forEach(s->{
            s.getConstraints().forEach(c->{
                ConstraintDTO constraintDTO = constraintDTOMap.get(c.getId());
                c.setCategory(constraintDTO.getCategory());
                c.setName(constraintDTO.getName());
                c.setDescription(constraintDTO.getDescription());
            });
        });
        List<DefaultContraintsDTO> defaultContraints = constraints.stream().collect(Collectors.groupingBy(ConstraintDTO::getCategory,Collectors.toList())).entrySet().stream().map(c->new DefaultContraintsDTO(c.getKey().toValue(),c.getValue())).collect(Collectors.toList());
        return new SolverConfigConstraintWrapper(defaultContraints, solverConfigs);
    }



    public void createDefaultConfig(Long unitId) {
        List<ConstraintDTO> constraintDTOS = constraintRepository.getAllVRPPlanningConstraints(unitId,PlanningType.VRPPLANNING);
        if(!constraintDTOS.isEmpty()){
            exceptionService.duplicateDataException("message.constraints.exists");
        }
        List<Constraint> constraints = new ArrayList<>(40);
        constraints.add(new Constraint("Start in Time window 1", "Longest tasks starts in first time interval", ConstraintCategory.LONGEST_TASK, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("First Task in Time window 1 or 2", "Longest tasks should start at first in any interval", ConstraintCategory.LONGEST_TASK, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Starts as First task", "Longest task starts as first task of the day", ConstraintCategory.LONGEST_TASK, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Plan in decending order of duration", "Longest tasks always before shorter on same installation", ConstraintCategory.LONGEST_TASK, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Add switching Time", "If distance between multiple tasks are less than X meters, then add x min to duration as walking time", ConstraintCategory.DURATION_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Plan in same time window", "Staff should not perform meter installations at different intervals for a citizen(i.e. A citizen should ideally only be disturbed once.)", ConstraintCategory.DURATION_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Plan all tasks same day", "Staff should perform all task for a citizen in one day.", ConstraintCategory.DURATION_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
       /* constraints.add(new Constraint("","Staff should perform all task for a citizen in one go without any break.", ConstraintCategory.BREAK_CONSTRAINTS,PlanningType.VRPPLANNING,unitId));*/
        constraints.add(new Constraint("Do not plan break within same installation number", "Breaks should not be planned between two tasks of same installation number", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Address with even number", "House with even number is always on right while moving in ascending order.", ConstraintCategory.LOCATION_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Address with odd number", "House with odd number is always on left while moving in ascending order.", ConstraintCategory.LOCATION_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Plan inside Time Window", "Break of 30 min is allowed between 11:00 to 1:00 p.m. it means that break has latest time of 12:30 p.m.", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Must Be Planned", "Break should be planned.", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Allow Zig-zag", "For one way street, zig-zag planning is most optimized.(irrespective of even odd)", ConstraintCategory.LOCATION_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Must Be Planned", "Task should be done in time slots window- Morning and afternoon", ConstraintCategory.DURATION_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Mut be planned inside Time window", "Task should be done in one of the two time slots - Morning and afternoon", ConstraintCategory.DURATION_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Minimize Flexi Time", "There is 1 hour of flexible time but that would cost 100% extra. So opta-planner must work on minimizing it.", ConstraintCategory.DURATION_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Optimize plan based on Skill", "If there are two staffs, one with 3 skills and other with one skill and both can perform a task, so opta must assign that task to the staff with one skill", ConstraintCategory.EFFICIENCY_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("How many metres a person can carry", "How many metres a person can carry", ConstraintCategory.EFFICIENCY_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Maximize Task Performed", "Maximum tasks performed by Staff is X per day(or hour)", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Task with nearby location", "Plan task from same floor/building/street together.", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Minimize task time", "Minimize on overall calculated task time", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Minimize driving time", "Minimize on overall calculated driving Time", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Reduce Duration", "Minimize on difference between fixed time and actual time(efficiency factor)", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Maximize flexitime utilization", "Maximize capacity inside overtime", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Minimize overtime", "Minimize using overtime window", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Do not allow zig-zag in same time window", "The rule for zig-zag applies to same time window.", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        constraints.add(new Constraint("Task from same installation number", "Same place's installations should happen consecutively", ConstraintCategory.BREAK_CONSTRAINTS, PlanningType.VRPPLANNING, unitId));
        save(constraints);
        List<ConstraintValue> constraintValues = constraints.stream().map(c -> new ConstraintValue(c.getId(),null)).collect(Collectors.toList());
        SolverConfig solverConfig = new SolverConfig(unitId, "Default Configuration", false, PlanningType.VRPPLANNING, null, 300, constraintValues, true, SolverConfigStatus.READY);
        save(solverConfig);
    }


}
