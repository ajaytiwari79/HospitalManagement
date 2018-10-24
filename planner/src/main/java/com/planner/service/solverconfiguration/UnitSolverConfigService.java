package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.kairos.dto.planner.solverconfig.unit.UnitSolverConfigDTO;
import com.planner.domain.solverconfig.common.SolverConfig;
import com.planner.domain.solverconfig.unit.UnitSolverConfig;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.repository.solver_config.SolverConfigRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class UnitSolverConfigService {
    @Inject
    private SolverConfigRepository solverConfigRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;

    public void createUnitSolverConfig(UnitSolverConfigDTO unitSolverConfigDTO) {
        boolean nameExists = solverConfigRepository.isNameExists(unitSolverConfigDTO.getName(),null,false);
        if (!nameExists) {
            UnitSolverConfig unitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(unitSolverConfigDTO, UnitSolverConfig.class);
            solverConfigRepository.saveObject(unitSolverConfig);
        }

    }


    public void copyUnitSolverConfig(UnitSolverConfigDTO unitSolverConfigDTO) {
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(unitSolverConfigDTO.getId());
        if (solverConfigOptional.isPresent()) {
            UnitSolverConfig unitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(unitSolverConfigDTO, UnitSolverConfig.class);
            unitSolverConfig.setIdBuilder(null);//UnSet
            unitSolverConfig.setParentUnitSolverConfigId(unitSolverConfigDTO.getId());
            solverConfigRepository.saveObject(unitSolverConfig);
        }
    }
/*************************************************************************/
    public UnitSolverConfigDTO getUnitSolverConfig(BigInteger solverConfigId) {
        UnitSolverConfigDTO UnitSolverConfigDTO = null;
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(solverConfigId);
        if (solverConfigOptional.isPresent()) {
            UnitSolverConfig unitSolverConfig =(UnitSolverConfig) solverConfigOptional.get();
            UnitSolverConfigDTO = ObjectMapperUtils.copyPropertiesByMapper(unitSolverConfig, UnitSolverConfigDTO.class);
        }
        return UnitSolverConfigDTO;
    }
    /*************************************************************************/
    public List<UnitSolverConfigDTO> getAllUnitSolverConfig() {
        List<SolverConfig> unitSolverConfigList = solverConfigRepository.findAllSolverConfigNotDeletedByType("unit");
        return ObjectMapperUtils.copyPropertiesOfListByMapper(unitSolverConfigList, UnitSolverConfigDTO.class);
    }
    /*************************************************************************/
    //Only update if present
    public UnitSolverConfigDTO updateUnitSolverConfig(UnitSolverConfigDTO UnitSolverConfigDTO) {
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(UnitSolverConfigDTO.getId());
        boolean nameExists = solverConfigRepository.isNameExists(UnitSolverConfigDTO.getName(), UnitSolverConfigDTO.getId(),false);
        if (solverConfigOptional.isPresent() && !nameExists) {
            UnitSolverConfig UnitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(UnitSolverConfigDTO, UnitSolverConfig.class);
            solverConfigRepository.saveObject(UnitSolverConfig);
        }
        return UnitSolverConfigDTO;
    }
    /*************************************************************************/
    //Soft Delete
    public boolean deleteUnitSolverConfig(BigInteger solverConfigId) {
        boolean isPresent = solverConfigRepository.findById(solverConfigId).isPresent();
        if (isPresent) {
            solverConfigRepository.safeDeleteById(solverConfigId);
        }
        return isPresent;
    }

    /******************************Country Default Data***********************************************/
    public DefaultDataDTO getDefaultData(Long unitId) {
        DefaultDataDTO defaultDataDTO=new DefaultDataDTO()
                //get All Phases
                .setPhaseDTOSBuilder(getAllPhases(unitId))
                //getAllPlanningPeriod
                .setPlanningPeriodDTOSBuilder(getAllPlanningPeriods(unitId));


        return defaultDataDTO;
    }

    /**
     *@param unitId
     * @return
     */
    private List<PhaseDTO> getAllPhases(Long unitId)
    {
        List<PhaseDTO> phaseDTOS=activityMongoRepository.getAllPhasesByUnitId(unitId);;
        return phaseDTOS;
    }

    /**
     *
     * @param unitId
     * @return
     */
    private List<PlanningPeriodDTO> getAllPlanningPeriods(Long unitId) {
        return activityMongoRepository.getAllPlanningPeriodByUnitId(unitId);
    }


}
