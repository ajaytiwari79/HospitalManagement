package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.enums.planning_problem.PlanningProblemType;
import com.planner.component.exception.ExceptionService;
import com.planner.component.rest_client.IntegrationService;
import com.planner.domain.solverconfig.SolverConfig;
import com.planner.repository.planning_problem.PlanningProblemRepository;
import com.planner.repository.solver_config.SolverConfigRepository;
import com.planner.service.planning_problem.PlanningProblemService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.TimeTypeEnum.*;

@Service
public class UnitSolverConfigService {
    @Inject
    private SolverConfigRepository solverConfigRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject private CountrySolverConfigService countrySolverConfigService;
    @Inject private PlanningProblemRepository planningProblemRepository;
    @Inject private IntegrationService integrationService;
    @Inject private PlanningProblemService planningProblemService;


    public SolverConfigDTO createUnitSolverConfig(SolverConfigDTO solverConfigDTO,Long unitId) {
        solverConfigDTO.setUnitId(unitId);
        if (preValidateUnitSolverConfigDTO(solverConfigDTO)) {
            SolverConfig solverConfig = ObjectMapperUtils.copyPropertiesByMapper(solverConfigDTO, SolverConfig.class);
            solverConfigRepository.saveEntity(solverConfig);
            solverConfigDTO.setId(solverConfig.getId());
        }
        return solverConfigDTO;
    }

    public SolverConfigDTO copyUnitSolverConfig(SolverConfigDTO solverConfigDTO) {
        SolverConfig constraint = solverConfigRepository.findByIdNotDeleted(solverConfigDTO.getId());
        if (constraint != null && preValidateUnitSolverConfigDTO(solverConfigDTO)) {
            SolverConfig solverConfig = ObjectMapperUtils.copyPropertiesByMapper(solverConfigDTO, SolverConfig.class);
            solverConfig.setParentSolverConfigId(solverConfigDTO.getId());
            solverConfig.setId(null);
            solverConfigRepository.saveEntity(solverConfig);
            solverConfigDTO.setId(solverConfig.getId());
        }
        return solverConfigDTO;
    }


    public List<SolverConfig> getAllUnitSolverConfigByUnitId(Long unitId) {
        return solverConfigRepository.getAllSolverConfigWithConstraintsByUnitId(unitId);
    }

    public SolverConfigDTO updateUnitSolverConfig(Long unitId,SolverConfigDTO unitSolverConfigDTO) {
        unitSolverConfigDTO.setUnitId(unitId);
        SolverConfig solverConfig = solverConfigRepository.findByIdNotDeleted(unitSolverConfigDTO.getId());
        boolean nameExists = solverConfigRepository.isNameExistsById(unitSolverConfigDTO.getName(), unitSolverConfigDTO.getId(), false, unitSolverConfigDTO.getUnitId());
        if (isNotNull(solverConfig) && !nameExists) {
            solverConfig = ObjectMapperUtils.copyPropertiesByMapper(unitSolverConfigDTO, SolverConfig.class);
            solverConfigRepository.saveEntity(solverConfig);
        }
        return unitSolverConfigDTO;
    }

    public boolean deleteUnitSolverConfig(BigInteger solverConfigId) {
        boolean isPresent = solverConfigRepository.findById(solverConfigId).isPresent();
        if (isPresent) {
            solverConfigRepository.safeDeleteById(solverConfigId);
        }
        return isPresent;
    }

    public DefaultDataDTO getDefaultData(Long unitId) {
        DefaultDataDTO defaultDataDTO = integrationService.getDefaultDataForSolverConfig(unitId);
        defaultDataDTO.setTimeTypeEnums(newArrayList(PRESENCE,ABSENCE,PAID_BREAK,UNPAID_BREAK));
        defaultDataDTO.setOrganizationServices(integrationService.getOrganisationServiceByunitId(unitId));
        defaultDataDTO.setConstraintTypes(countrySolverConfigService.getConstraintTypes());
        PlanningProblemDTO planningProblemDTO = planningProblemRepository.findPlanningProblemByType(PlanningProblemType.SHIFT_PLANNING);
        if(isNull(planningProblemDTO)){
            planningProblemDTO = planningProblemService.createDefaultPlanningProblem();
        }
        defaultDataDTO.setPlanningProblems(newArrayList(planningProblemDTO));
        return defaultDataDTO;
    }

    private boolean preValidateUnitSolverConfigDTO(SolverConfigDTO unitSolverConfigDTO) {
        String result = null;//userNeo4jRepo.validateUnit(unitSolverConfigDTO.getUnitId());
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
