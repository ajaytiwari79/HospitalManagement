package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.planninginfo.PlanningProblemDTO;
import com.kairos.dto.planner.solverconfig.ConstraintDTO;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import com.planner.component.exception.ExceptionService;
import com.planner.domain.query_results.organization_service.OrganizationServiceQueryResult;
import com.planner.domain.solverconfig.SolverConfig;
import com.planner.repository.planning_problem.PlanningProblemRepository;
import com.planner.repository.solver_config.SolverConfigRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.enums.constraint.ConstraintSubType.*;
import static com.kairos.enums.constraint.ConstraintType.*;

@Service
public class CountrySolverConfigService {

    @Inject
    private SolverConfigRepository solverConfigRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject private PlanningProblemRepository planningProblemRepository;

    public SolverConfigDTO createCountrySolverConfig(Long countryId,SolverConfigDTO countrySolverConfigDTO) {
        countrySolverConfigDTO.setCountryId(countryId);
        if (preValidateCountrySolverConfigDTO(countrySolverConfigDTO)) {
            SolverConfig countrySolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfigDTO, SolverConfig.class);
            solverConfigRepository.saveEntity(countrySolverConfig);
            copyUnitSolverConfigByOrganizationServiceAndSubService(countrySolverConfig);
            countrySolverConfigDTO.setId(countrySolverConfig.getId());
        }
        return countrySolverConfigDTO;
    }

    private void copyUnitSolverConfigByOrganizationServiceAndSubService(SolverConfig countrySolverConfig) {
        List<Long> applicableUnitIdForSolverConfig = null;//userNeo4jRepo.getUnitIdsByOrganizationSubServiceIds(countrySolverConfig.getOrganizationSubServiceIds());
        List<SolverConfig> unitSolverConfigList = new ArrayList<>();
        PhaseDTO phaseDTO = null;//activityMongoRepository.getOnePhaseById(new BigInteger(countrySolverConfig.getPhaseId().toString()));
        List<PhaseDTO> phaseDTOS = null;//activityMongoRepository.getPhaseByUnitIdAndPhaseEnum(applicableUnitIdForSolverConfig,phaseDTO.getPhaseEnum());
        Map<Long,PhaseDTO> phaseDTOMap = phaseDTOS.stream().collect(Collectors.toMap(k->k.getOrganizationId(),v->v));
        List<SolverConfig> unitSolverConfigs = solverConfigRepository.getAllSolverConfigByParentId(countrySolverConfig.getId());
        Map<Long,SolverConfig> unitSolverConfigMap = unitSolverConfigs.stream().collect(Collectors.toMap(SolverConfig::getUnitId,v->v));
        if (!applicableUnitIdForSolverConfig.isEmpty()) {
            updateUnitSolverConfig(countrySolverConfig, applicableUnitIdForSolverConfig, unitSolverConfigList, phaseDTOMap, unitSolverConfigMap);
            for (SolverConfig unitSolverConfig : unitSolverConfigMap.values()) {
                unitSolverConfig.setDeleted(true);
                unitSolverConfigList.add(unitSolverConfig);
            }
            if (isCollectionNotEmpty(unitSolverConfigList)) {
                solverConfigRepository.saveList(unitSolverConfigList);
            }
        }
    }

    private void updateUnitSolverConfig(SolverConfig countrySolverConfig, List<Long> applicableUnitIdForSolverConfig, List<SolverConfig> unitSolverConfigList, Map<Long, PhaseDTO> phaseDTOMap, Map<Long, SolverConfig> unitSolverConfigMap) {
        for (Long unitId : applicableUnitIdForSolverConfig) {
            if (!unitSolverConfigMap.containsKey(unitId) && phaseDTOMap.containsKey(unitId)) {
                SolverConfig unitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfig, SolverConfig.class);
                unitSolverConfig.setId(null);//Unset Id
                unitSolverConfig.setCountryId(null);
                unitSolverConfig.setUnitId(unitId);
                unitSolverConfig.setParentCountrySolverConfigId(countrySolverConfig.getId());
                unitSolverConfig.setPhaseId(phaseDTOMap.get(unitId).getId().longValue());
                unitSolverConfigList.add(unitSolverConfig);
            }else {
                unitSolverConfigMap.remove(unitId);
            }
        }
    }

    public void mapSolverConfigToOrganization(BigInteger solverConfigId,List<Long> organizationSubServiceIds){
        SolverConfig countrySolverConfig = solverConfigRepository.getSolverConfigById(solverConfigId);
        countrySolverConfig.setOrganizationSubServiceIds(organizationSubServiceIds);
        copyUnitSolverConfigByOrganizationServiceAndSubService(countrySolverConfig);
    }

    /**
     * copy(create) countrySolverConfig at country Level itself
     * Here TypeCasting is not required because coming DTO might get changed,so we require only
     * id field from previous saved solver-config.
     */
    public SolverConfigDTO copyCountrySolverConfig(Long countryId,SolverConfigDTO countrySolverConfigDTO) {
        SolverConfig solverConfig = solverConfigRepository.findByIdNotDeleted(countrySolverConfigDTO.getId());
        countrySolverConfigDTO.setCountryId(countryId);
        if (solverConfig != null && preValidateCountrySolverConfigDTO(countrySolverConfigDTO)) {
            SolverConfig countrySolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfigDTO, SolverConfig.class);
            countrySolverConfig.setId(null);//UnSet
            countrySolverConfig.setParentSolverConfigId(countrySolverConfigDTO.getId());
            solverConfigRepository.saveEntity(countrySolverConfig);
            copyUnitSolverConfigByOrganizationServiceAndSubService(countrySolverConfig);
            countrySolverConfigDTO.setId(countrySolverConfig.getId());
        }
        return countrySolverConfigDTO;
    }

    public List<SolverConfig> getAllCountrySolverConfigByCountryId(Long countryId) {
        return solverConfigRepository.getAllSolverConfigWithConstraintsByCountryId(countryId);
    }


    public SolverConfigDTO updateCountrySolverConfig(Long countryId,SolverConfigDTO countrySolverConfigDTO) {
        countrySolverConfigDTO.setCountryId(countryId);
        SolverConfig solverConfig = solverConfigRepository.findByIdNotDeleted(countrySolverConfigDTO.getId());
        if (solverConfig != null && preValidateCountrySolverConfigDTO(countrySolverConfigDTO)) {
            SolverConfig countrySolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfigDTO, SolverConfig.class);
            solverConfigRepository.saveEntity(countrySolverConfig);
            copyUnitSolverConfigByOrganizationServiceAndSubService(countrySolverConfig);
        }
        return countrySolverConfigDTO;
    }

    public boolean deleteCountrySolverConfig(BigInteger solverConfigId) {
        return solverConfigRepository.safeDeleteById(solverConfigId);
    }

    public DefaultDataDTO getDefaultData(Long countryId) {
        List<PlanningProblemDTO> planningProblemDTOS = ObjectMapperUtils.copyCollectionPropertiesByMapper(planningProblemRepository.findAll(),PlanningProblemDTO.class);
        return new DefaultDataDTO();/*
                .setOrganizationServicesBuilder(getOrganizationServicesAndItsSubServices(countryId))
                .setPhaseDTOSBuilder(getAllPhases(countryId)).setTimeTypeEnumSBuilder(newArrayList(PRESENCE,ABSENCE,PAID_BREAK,UNPAID_BREAK))
                .setConstraintTypesBuilder(getConstraintTypes()).setPlanningProblemsBuilder(planningProblemDTOS);*/
    }

    private ConstraintSubType constraintSubType;
    private Boolean  mandatory;
    private ScoreLevel scoreLevel;
    private int constraintWeight;
    private ConstraintType constraintType;

    public Map<ConstraintType, Set<ConstraintDTO>> getConstraintTypes(){
        Map<ConstraintType, Set<ConstraintDTO>> constraintTypeSetMap = new HashMap<>(ConstraintType.values().length);
        HashSet<ConstraintSubType> activityConstrainsts = newHashSet(ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH,
                MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF,
                ACTIVITY_VALID_DAYTYPE,
                ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS);
        Set<ConstraintDTO> activityConstraints = activityConstrainsts.stream().map(constraintSubType -> new ConstraintDTO(constraintSubType.toValue(),constraintSubType.toValue(),constraintSubType,true,ScoreLevel.SOFT,10,ACTIVITY)).collect(Collectors.toSet());
        constraintTypeSetMap.put(ACTIVITY,
                activityConstraints);
        HashSet<ConstraintSubType> wtaConstraintSubTypes = newHashSet(CONSECUTIVE_WORKING_PARTOFDAY,
                DAYS_OFF_IN_PERIOD,
                NUMBER_OF_PARTOFDAY,
                SHIFT_LENGTH,
                NUMBER_OF_SHIFTS_IN_INTERVAL,
                TIME_BANK,
                DAILY_RESTING_TIME,
                DURATION_BETWEEN_SHIFTS,
                REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS,
                WEEKLY_REST_PERIOD,
                NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD,
                SHORTEST_AND_AVERAGE_DAILY_REST,
                SENIOR_DAYS_PER_YEAR,
                CHILD_CARE_DAYS_CHECK,
                DAYS_OFF_AFTER_A_SERIES,
                NO_OF_SEQUENCE_SHIFT,
                EMPLOYEES_WITH_INCREASE_RISK,
                WTA_FOR_CARE_DAYS);
        Set<ConstraintDTO> wtaConstraints = wtaConstraintSubTypes.stream().map(constraintSubType -> new ConstraintDTO(constraintSubType.toValue(),constraintSubType.toValue(),constraintSubType,true,ScoreLevel.MEDIUM,10,WTA)).collect(Collectors.toSet());
        constraintTypeSetMap.put(WTA, wtaConstraints);
        constraintTypeSetMap.put(SHIFT,new HashSet<>());
        return constraintTypeSetMap;
    }

    private List<OrganizationServiceDTO> getOrganizationServicesAndItsSubServices(Long countryId) {
        List<OrganizationServiceQueryResult> organizationServiceQueryResults = null;//userNeo4jRepo.getAllOrganizationServices(countryId);
        return ObjectMapperUtils.copyCollectionPropertiesByMapper(organizationServiceQueryResults, OrganizationServiceDTO.class);
    }

    private List<PhaseDTO> getAllPhases(Long countryId) {
        return null;//activityMongoRepository.getAllPhasesByCountryId(countryId);
    }

    private boolean preValidateCountrySolverConfigDTO(SolverConfigDTO countrySolverConfigDTO) {
        if(solverConfigRepository.isNameExistsById(countrySolverConfigDTO.getName(), countrySolverConfigDTO.getId(), true, countrySolverConfigDTO.getCountryId())){
            exceptionService.dataNotFoundByIdException("message.name.alreadyExists");
            return false;
        }
        return true;
    }

}
