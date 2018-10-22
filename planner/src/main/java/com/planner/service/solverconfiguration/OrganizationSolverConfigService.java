package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.organization.solverconfig.OrganizationSolverConfigDTO;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.planner.domain.common.solverconfig.SolverConfig;
import com.planner.domain.organization.solverconfig.OrganizationSolverConfig;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.repository.solver_config.SolverConfigRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class OrganizationSolverConfigService {
    @Inject
    private SolverConfigRepository solverConfigRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;

    public void createOrganizationSolverConfig(OrganizationSolverConfigDTO organizationSolverConfigDTO) {
        boolean nameExists = solverConfigRepository.isNameExists(organizationSolverConfigDTO.getName(),null,false);
        if (!nameExists) {
            OrganizationSolverConfig organizationSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(organizationSolverConfigDTO, OrganizationSolverConfig.class);
            solverConfigRepository.saveObject(organizationSolverConfig);
        }

    }


    public void copyOrganizationSolverConfig(OrganizationSolverConfigDTO organizationSolverConfigDTO) {
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(organizationSolverConfigDTO.getId());
        if (solverConfigOptional.isPresent()) {
            OrganizationSolverConfig organizationSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(organizationSolverConfigDTO, OrganizationSolverConfig.class);
            organizationSolverConfig.setIdBuilder(null);//UnSet
            organizationSolverConfig.setParentOrganizationSolverConfigId(organizationSolverConfigDTO.getId());
            solverConfigRepository.saveObject(organizationSolverConfig);
        }
    }
/*************************************************************************/
    public OrganizationSolverConfigDTO getOrganizationSolverConfig(BigInteger solverConfigId) {
        OrganizationSolverConfigDTO OrganizationSolverConfigDTO = null;
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(solverConfigId);
        if (solverConfigOptional.isPresent()) {
            OrganizationSolverConfig OrganizationSolverConfig =(OrganizationSolverConfig) solverConfigOptional.get();
            OrganizationSolverConfigDTO = ObjectMapperUtils.copyPropertiesByMapper(OrganizationSolverConfig, OrganizationSolverConfigDTO.class);
        }
        return OrganizationSolverConfigDTO;
    }
    /*************************************************************************/
    public List<OrganizationSolverConfigDTO> getAllOrganizationSolverConfig() {
        List<SolverConfig> organizationSolverConfigList = solverConfigRepository.findAllSolverConfigNotDeletedByType("organization");
        return ObjectMapperUtils.copyPropertiesOfListByMapper(organizationSolverConfigList, OrganizationSolverConfigDTO.class);
    }
    /*************************************************************************/
    //Only update if present
    public OrganizationSolverConfigDTO updateOrganizationSolverConfig(OrganizationSolverConfigDTO OrganizationSolverConfigDTO) {
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(OrganizationSolverConfigDTO.getId());
        boolean nameExists = solverConfigRepository.isNameExists(OrganizationSolverConfigDTO.getName(),OrganizationSolverConfigDTO.getId(),false);
        if (solverConfigOptional.isPresent() && !nameExists) {
            OrganizationSolverConfig OrganizationSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(OrganizationSolverConfigDTO, OrganizationSolverConfig.class);
            solverConfigRepository.saveObject(OrganizationSolverConfig);
        }
        return OrganizationSolverConfigDTO;
    }
    /*************************************************************************/
    //Soft Delete
    public boolean deleteOrganizationSolverConfig(BigInteger solverConfigId) {
        boolean isPresent = solverConfigRepository.findById(solverConfigId).isPresent();
        if (isPresent) {
            solverConfigRepository.safeDeleteById(solverConfigId);
        }
        return isPresent;
    }

    /******************************Country Default Data***********************************************/
    public DefaultDataDTO getDefaultData(Long organizationId) {
        DefaultDataDTO defaultDataDTO=new DefaultDataDTO()
                //get All Phases
                .setPhaseDTOSBuilder(getAllPhases(organizationId))
                //getAllPlanningPeriod
                .setPlanningPeriodDTOSBuilder(getAllPlanningPeriods(organizationId));


        return defaultDataDTO;
    }

    /**
     *@param organizationId
     * @return
     */
    private List<PhaseDTO> getAllPhases(Long organizationId)
    {
        List<PhaseDTO> phaseDTOS=activityMongoRepository.getAllPhasesByOrganizationId(organizationId);;
        return phaseDTOS;
    }

    /**
     *
     * @param organizationId
     * @return
     */
    private List<PlanningPeriodDTO> getAllPlanningPeriods(Long organizationId) {
        return activityMongoRepository.getAllPlanningPeriodByOrganizationId(organizationId);
    }


}
