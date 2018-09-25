package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.planner.domain.query_results.organization_service.OrganizationServiceQueryResult;
import com.planner.domain.solverconfig.SolverConfig;
import com.planner.repository.config.SolverConfigRepository;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.repository.shift_planning.UserNeo4jRepo;
import com.planner.service.shift_planning.ActivityMongoService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Service
public class CountrySolverConfigService {

    @Inject
    private SolverConfigRepository solverConfigRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Inject
    private UserNeo4jRepo userNeo4jRepo;

    /**
     * @param solverConfigDTO
     */
    public void createCountrySolverConfig(SolverConfigDTO solverConfigDTO) {
        boolean nameExists = solverConfigRepository.isNameExists(solverConfigDTO.getName());
        if (!nameExists) {
            SolverConfig solverConfig = ObjectMapperUtils.copyPropertiesByMapper(solverConfigDTO, SolverConfig.class);
            solverConfigRepository.saveObject(solverConfig);
            if (solverConfigDTO.getOrganizationSubServiceId() != null) {
                //Now copy same solverConfig at {unit/s} associated with {organizationSubServiceId}
                copyUnitSolverConfigByOrganizationSubService(solverConfigDTO.getOrganizationSubServiceId(), solverConfig);
            }
        }
    }

    /**
     * copy(create) solverConfig at Unit Level
     * (which is OrganizationSubServices at country Level)
     * by organizationSubServiceId.
     * we need just to find all unitId/s associated with all{OrganizationSubServices}.
     * Then for corresponding unitId/s create this same {@link SolverConfig}
     *
     * @param organizationSubServiceId
     * @param solverConfig
     */
    private void copyUnitSolverConfigByOrganizationSubService(Long organizationSubServiceId, SolverConfig solverConfig) {
        List<Long> applicableUnitIdForSolverConfig = userNeo4jRepo.getUnitIdsByOrganizationSubServiceId(organizationSubServiceId);
        for (Long unitId : applicableUnitIdForSolverConfig) {
            solverConfig.setUnitIdBuilder(unitId).setIdBuilder(null);//Unset Id
            solverConfigRepository.saveObject(solverConfig);
        }
    }


    /***********************************************************************************************************************/
    //copy(create) solverConfig at country Level itself
    public void copyCountrySolverConfig(SolverConfigDTO solverConfigDTO) {
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(solverConfigDTO.getId() + "");
        if (solverConfigOptional.isPresent()) {
            SolverConfig solverConfig = ObjectMapperUtils.copyPropertiesByMapper(solverConfigDTO, SolverConfig.class)
                    .setIdBuilder(null)//UnSet
                    .setParentIdBuilder(solverConfigDTO.getId() + "");
            solverConfigRepository.saveObject(solverConfig);
        }
    }

    /***************************************************************/
    public SolverConfigDTO getCountrySolverConfig(String solverConfigId) {
        SolverConfigDTO solverConfigDTO = null;
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(solverConfigId);
        if (solverConfigOptional.isPresent()) {
            SolverConfig solverConfig = solverConfigOptional.get();
            solverConfigDTO = ObjectMapperUtils.copyPropertiesByMapper(solverConfig, SolverConfigDTO.class);
        }
        return solverConfigDTO;
    }

    /***************************************************************/
    public List<SolverConfigDTO> getAllCountrySolverConfig() {
        List<SolverConfig> solverConfigList = solverConfigRepository.findAllNotDeleted();
        return ObjectMapperUtils.copyPropertiesOfListByMapper(solverConfigList, SolverConfigDTO.class);
    }

    /***************************************************************/
    //Only update if present
    public SolverConfigDTO updateCountrySolverConfig(SolverConfigDTO solverConfigDTO) {
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(solverConfigDTO.getId() + "");
        boolean nameExists = solverConfigRepository.isNameExists(solverConfigDTO.getName());

        if (solverConfigOptional.isPresent() && !nameExists) {
            SolverConfig solverConfig = ObjectMapperUtils.copyPropertiesByMapper(solverConfigDTO, SolverConfig.class);
            solverConfigRepository.save(solverConfig);
        }
        return solverConfigDTO;
    }

    /***************************************************************/
    //Soft Delete
    public boolean deleteCountrySolverConfig(String solverConfigId) {
        boolean isPresent = solverConfigRepository.findById(solverConfigId).isPresent();
        if (isPresent) {
            solverConfigRepository.safeDeleteById(solverConfigId);
        }
        return isPresent;
    }

    /******************************Country Default Data***********************************************/
    public DefaultDataDTO getDefaultData(Long countryId) {
        DefaultDataDTO defaultDataDTO=new DefaultDataDTO()
                //get all organizationServices by countryId
                .setOrganizationServiceDTOSBuilder(getOrganizationServicesAndItsSubServices(countryId))
                //get All Phases
                .setPhaseDTOSBuilder(getAllPhases(countryId));

        return defaultDataDTO;
    }

    /**
     *
     * @param countryId
     * @return
     */
    private List<OrganizationServiceDTO> getOrganizationServicesAndItsSubServices(Long countryId) {
        List<OrganizationServiceQueryResult> organizationServiceQueryResults=userNeo4jRepo.getAllOrganizationServices(countryId);
        List<OrganizationServiceDTO> organizationServiceDTOS=ObjectMapperUtils.copyPropertiesOfListByMapper(organizationServiceQueryResults,OrganizationServiceDTO.class);

        return organizationServiceDTOS;
    }

    /**
     *@param countryId
     * @return
     */
    private List<PhaseDTO> getAllPhases(Long countryId)
    {
        List<PhaseDTO> phaseDTOS=activityMongoRepository.getAllPhasesByCountryId(countryId);;
      return phaseDTOS;
    }

}
