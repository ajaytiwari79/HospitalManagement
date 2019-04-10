package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.dto.planner.solverconfig.unit.UnitSolverConfigDTO;
import com.planner.component.exception.ExceptionService;
import com.planner.domain.solverconfig.common.SolverConfig;
import com.planner.domain.solverconfig.unit.UnitSolverConfig;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.repository.shift_planning.UserNeo4jRepo;
import com.planner.repository.solver_config.SolverConfigRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.enums.TimeTypeEnum.*;
import static com.kairos.enums.TimeTypeEnum.UNPAID_BREAK;

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


    //===================================================================================
    public UnitSolverConfigDTO createUnitSolverConfig(UnitSolverConfigDTO unitSolverConfigDTO) {
        if (preValidateUnitSolverConfigDTO(unitSolverConfigDTO, true)) {
            UnitSolverConfig unitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(unitSolverConfigDTO, UnitSolverConfig.class);
            solverConfigRepository.saveEntity(unitSolverConfig);
            unitSolverConfigDTO.setId(unitSolverConfig.getId());
        }
        return unitSolverConfigDTO;
    }

    //========================================================================================
    public UnitSolverConfigDTO copyUnitSolverConfig(UnitSolverConfigDTO unitSolverConfigDTO) {
        SolverConfig constraint = solverConfigRepository.findByIdNotDeleted(unitSolverConfigDTO.getId());
        if (constraint != null && preValidateUnitSolverConfigDTO(unitSolverConfigDTO, true)) {
            UnitSolverConfig unitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(unitSolverConfigDTO, UnitSolverConfig.class);
            unitSolverConfig.setParentSolverConfigId(unitSolverConfigDTO.getId());
            unitSolverConfig.setId(null);//Unset Id
            solverConfigRepository.saveEntity(unitSolverConfig);
            unitSolverConfigDTO.setId(unitSolverConfig.getId());
        }
        return unitSolverConfigDTO;
    }


    //=============================================================================
    public List<UnitSolverConfigDTO> getAllUnitSolverConfigByUnitId(Long unitId) {
        List<SolverConfig> solverConfigList = solverConfigRepository.findAllObjectsNotDeletedById(true, unitId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(solverConfigList, UnitSolverConfig.class);
    }

    //============================================================================
    //Only update if present
    public UnitSolverConfigDTO updateUnitSolverConfig(UnitSolverConfigDTO unitSolverConfigDTO) {
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(unitSolverConfigDTO.getId());
        boolean nameExists = solverConfigRepository.isNameExistsById(unitSolverConfigDTO.getName(), unitSolverConfigDTO.getId(), false, unitSolverConfigDTO.getUnitId());
        if (solverConfigOptional.isPresent() && !nameExists) {
            UnitSolverConfig unitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(unitSolverConfigDTO, UnitSolverConfig.class);
            solverConfigRepository.saveEntity(unitSolverConfig);
        }
        return unitSolverConfigDTO;
    }

    //============================================================================
    //Soft Delete
    public boolean deleteUnitSolverConfig(BigInteger solverConfigId) {
        boolean isPresent = solverConfigRepository.findById(solverConfigId).isPresent();
        if (isPresent) {
            solverConfigRepository.safeDeleteById(solverConfigId);
        }
        return isPresent;
    }

    /*==============================Country Default Data==================================*/
    public DefaultDataDTO getDefaultData(Long unitId) {
        DefaultDataDTO defaultDataDTO = new DefaultDataDTO()
                //get All Phases
                .setPhaseDTOSBuilder(getAllPhases(unitId))
                //getAllPlanningPeriod
                .setPlanningPeriodBuilder(getAllPlanningPeriods(unitId)).setTimeTypeEnumSBuilder(newArrayList(PRESENCE,ABSENCE,PAID_BREAK,UNPAID_BREAK))
                .setConstraintTypesBuilder(countrySolverConfigService.getConstraintTypes());


        return defaultDataDTO;
    }

    /**
     * @param unitId
     * @return
     */
    private List<PhaseDTO> getAllPhases(Long unitId) {
        List<PhaseDTO> phaseDTOS = activityMongoRepository.getAllPhasesByUnitId(unitId);
        ;
        return phaseDTOS;
    }

    /**
     * @param unitId
     * @return
     */
    private List<PlanningPeriodDTO> getAllPlanningPeriods(Long unitId) {
        return activityMongoRepository.getAllPlanningPeriodByUnitId(unitId);
    }

    //======================================common validation

    /**
     * Validation sequence should follow this ordering
     *
     * @param unitSolverConfigDTO
     * @return
     */
    private boolean preValidateUnitSolverConfigDTO(UnitSolverConfigDTO unitSolverConfigDTO, boolean isCurrentObjectIdNull) {
        String result = userNeo4jRepo.validateUnit(unitSolverConfigDTO.getUnitId());
        if ("unitNotExists".equals(result)) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Unit", unitSolverConfigDTO.getUnitId());
        } else if (solverConfigRepository.isNameExistsById(unitSolverConfigDTO.getName(), isCurrentObjectIdNull ? null : unitSolverConfigDTO.getId(), false, unitSolverConfigDTO.getUnitId())) {
            exceptionService.dataNotFoundByIdException("message.name.alreadyExists");
        }
        return true;
    }

    public SolverConfigDTO getSolverConfigWithConstraints(BigInteger solverConfigId){
        return solverConfigRepository.getSolverConfigWithConstraints(solverConfigId);
    }


}
