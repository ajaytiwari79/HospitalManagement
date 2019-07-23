package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.dto.planner.solverconfig.unit.UnitSolverConfigDTO;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.planner.component.exception.ExceptionService;
import com.planner.domain.constraint.unit.UnitConstraint;
import com.planner.domain.solverconfig.common.SolverConfig;
import com.planner.domain.solverconfig.unit.UnitSolverConfig;
import com.planner.repository.constraint.ConstraintsRepository;
import com.planner.repository.planning_problem.PlanningProblemRepository;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.repository.shift_planning.UserNeo4jRepo;
import com.planner.repository.solver_config.SolverConfigRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.TimeTypeEnum.*;

@Service
public class UnitSolverConfigService {
    @Inject
    private SolverConfigRepository solverConfigRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UserNeo4jRepo userNeo4jRepo;
    @Inject
    private ExceptionService exceptionService;
    @Inject private CountrySolverConfigService countrySolverConfigService;
    @Inject private PlanningProblemRepository planningProblemRepository;
    @Inject private ConstraintsRepository constraintsRepository;


    public UnitSolverConfigDTO createUnitSolverConfig(UnitSolverConfigDTO unitSolverConfigDTO,Long unitId) {
        unitSolverConfigDTO.setUnitId(unitId);
        if (preValidateUnitSolverConfigDTO(unitSolverConfigDTO)) {
            UnitSolverConfig unitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(unitSolverConfigDTO, UnitSolverConfig.class);
            List<BigInteger> countraintids = getConstraintsIds(unitSolverConfigDTO, null);
            unitSolverConfig.setConstraintIds(countraintids);
            solverConfigRepository.saveEntity(unitSolverConfig);
            unitSolverConfigDTO.setId(unitSolverConfig.getId());
        }
        return unitSolverConfigDTO;
    }

    public UnitSolverConfigDTO copyUnitSolverConfig(UnitSolverConfigDTO unitSolverConfigDTO) {
        SolverConfig constraint = solverConfigRepository.findByIdNotDeleted(unitSolverConfigDTO.getId());
        if (constraint != null && preValidateUnitSolverConfigDTO(unitSolverConfigDTO)) {
            UnitSolverConfig unitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(unitSolverConfigDTO, UnitSolverConfig.class);
            unitSolverConfig.setParentSolverConfigId(unitSolverConfigDTO.getId());
            unitSolverConfig.setId(null);//Unset Id
            List<BigInteger> countraintids = getConstraintsIds(unitSolverConfigDTO, null);
            unitSolverConfig.setConstraintIds(countraintids);
            solverConfigRepository.saveEntity(unitSolverConfig);
            unitSolverConfigDTO.setId(unitSolverConfig.getId());
        }
        return unitSolverConfigDTO;
    }


    public List<UnitSolverConfigDTO> getAllUnitSolverConfigByUnitId(Long unitId) {
        List<SolverConfigDTO> solverConfigList = solverConfigRepository.getAllSolverConfigWithConstraints(false, unitId);
        return solverConfigList.stream().map(solverConfigDTO -> (UnitSolverConfigDTO)solverConfigDTO).collect(Collectors.toList());
    }

    public UnitSolverConfigDTO updateUnitSolverConfig(Long unitId,UnitSolverConfigDTO unitSolverConfigDTO) {
        unitSolverConfigDTO.setUnitId(unitId);
        SolverConfig solverConfig = solverConfigRepository.findByIdNotDeleted(unitSolverConfigDTO.getId());
        boolean nameExists = solverConfigRepository.isNameExistsById(unitSolverConfigDTO.getName(), unitSolverConfigDTO.getId(), false, unitSolverConfigDTO.getUnitId());
        if (isNotNull(solverConfig) && !nameExists) {
            UnitSolverConfig unitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(unitSolverConfigDTO, UnitSolverConfig.class);
            List<BigInteger> countraintids = getConstraintsIds(unitSolverConfigDTO, solverConfig);
            unitSolverConfig.setConstraintIds(countraintids);
            solverConfigRepository.saveEntity(unitSolverConfig);
        }
        return unitSolverConfigDTO;
    }

    private List<BigInteger> getConstraintsIds(UnitSolverConfigDTO unitSolverConfigDTO, SolverConfig solverConfig) {
        Map<BigInteger, UnitConstraint> countryConstraintDTOMap = new HashMap<>();
        if(isNotNull(solverConfig)){
            List<UnitConstraint> solverConfigConstraints = constraintsRepository.findAllUnitConstraintByIds(solverConfig.getConstraintIds());
            countryConstraintDTOMap = solverConfigConstraints.stream().collect(Collectors.toMap(k->k.getId(), v->v));
        }
        List<UnitConstraint> unitConstraints = new ArrayList<>();
        for (ConstraintDTO constraintDTO : unitSolverConfigDTO.getConstraints()) {
            if(countryConstraintDTOMap.containsKey(constraintDTO.getId())) {
                UnitConstraint unitConstraint = countryConstraintDTOMap.get(constraintDTO.getId());
                unitConstraint.setConstraintLevel(constraintDTO.getConstraintLevel());
                unitConstraint.setPenalty(constraintDTO.getPenalty());
                unitConstraints.add(unitConstraint);
            }
            else {
                unitConstraints.add(new UnitConstraint(constraintDTO.getConstraintLevel(),constraintDTO.getPenalty(),constraintDTO.getName()));
            }
        }
        if(isCollectionNotEmpty(unitConstraints)) {
            constraintsRepository.saveList(unitConstraints);
        }
        return unitConstraints.stream().map(countryConstraint -> countryConstraint.getId()).collect(Collectors.toList());
    }

    public boolean deleteUnitSolverConfig(BigInteger solverConfigId) {
        boolean isPresent = solverConfigRepository.findById(solverConfigId).isPresent();
        if (isPresent) {
            solverConfigRepository.safeDeleteById(solverConfigId);
        }
        return isPresent;
    }

    public DefaultDataDTO getDefaultData(Long unitId) {
        List<PlanningProblemDTO> planningProblemDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(planningProblemRepository.findAll(),PlanningProblemDTO.class);
      //  Long countryId=userNeo4jRepo.getCountryIdByUnitId(unitId);
        DefaultDataDTO defaultDataDTO = new DefaultDataDTO()
                .setOrganizationServicesBuilder(ObjectMapperUtils.copyPropertiesOfListByMapper(userNeo4jRepo.getAllOrganizationServicesByUnitId(unitId),OrganizationServiceDTO.class))

                //get All Phases
                .setPhaseDTOSBuilder(getAllPhases(unitId))
                //getAllPlanningPeriod
                .setPlanningPeriodBuilder(getAllPlanningPeriods(unitId)).setTimeTypeEnumSBuilder(newArrayList(PRESENCE,ABSENCE,PAID_BREAK,UNPAID_BREAK))
                .setConstraintTypesBuilder(countrySolverConfigService.getConstraintTypes()).setPlanningProblemsBuilder(planningProblemDTOS);


        return defaultDataDTO;
    }

    private List<PhaseDTO> getAllPhases(Long unitId) {
        List<PhaseDTO> phaseDTOS = activityMongoRepository.getAllPhasesByUnitId(unitId);
        ;
        return phaseDTOS;
    }

    private List<PlanningPeriodDTO> getAllPlanningPeriods(Long unitId) {
        return activityMongoRepository.getAllPlanningPeriodByUnitId(unitId);
    }

    private boolean preValidateUnitSolverConfigDTO(UnitSolverConfigDTO unitSolverConfigDTO) {
        String result = userNeo4jRepo.validateUnit(unitSolverConfigDTO.getUnitId());
        if ("unitNotExists".equals(result)) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Unit", unitSolverConfigDTO.getUnitId());
        } else if (solverConfigRepository.isNameExistsById(unitSolverConfigDTO.getName(), unitSolverConfigDTO.getId(), false, unitSolverConfigDTO.getUnitId())) {
            exceptionService.dataNotFoundByIdException("message.name.alreadyExists");
        }
        return true;
    }

    public SolverConfigDTO getSolverConfigWithConstraints(BigInteger solverConfigId){
        return solverConfigRepository.getSolverConfigWithConstraints(solverConfigId);
    }


}
