package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.country.solverconfig.CountrySolverConfigDTO;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.planner.domain.common.solverconfig.SolverConfig;
import com.planner.domain.country.solverconfig.CountrySolverConfig;
import com.planner.domain.query_results.organization_service.OrganizationServiceQueryResult;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.repository.shift_planning.UserNeo4jRepo;
import com.planner.repository.solver_config.SolverConfigRepository;
import com.planner.service.commons.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
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
    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countrySolverConfigDTO
     */
    public void createCountrySolverConfig(CountrySolverConfigDTO countrySolverConfigDTO) {
        boolean nameExists = solverConfigRepository.isNameExists(countrySolverConfigDTO.getName());
        if (!nameExists) {
            CountrySolverConfig countrySolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfigDTO, CountrySolverConfig.class);
            solverConfigRepository.saveObject(countrySolverConfig);
            if (countrySolverConfigDTO.getOrganizationSubServiceId() != null) {
                //Now copy same countrySolverConfig at {unit/s} associated with {organizationSubServiceId}
                copyUnitSolverConfigByOrganizationSubService(countrySolverConfigDTO.getOrganizationServiceId(),countrySolverConfigDTO.getOrganizationSubServiceId(), countrySolverConfig);
            }
        }
        else
        {
            exceptionService.fieldAlreadyExistsException("message.name.alreadyExists");
        }
    }

    /**
     * copy(create) countrySolverConfig at Unit Level
     * (which is OrganizationSubServices at country Level)
     * by organizationSubServiceId.
     * we need just to find all unitId/s associated with all{OrganizationSubServices}.
     * Then for corresponding unitId/s create this same {@link CountrySolverConfig}
     *
     * @param organizationServiceId
     * @param organizationSubServiceId
     * @param countrySolverConfig
     */
    private void copyUnitSolverConfigByOrganizationSubService(Long organizationServiceId, Long organizationSubServiceId, CountrySolverConfig countrySolverConfig) {
        List<Long> applicableUnitIdForSolverConfig = userNeo4jRepo.getUnitIdsByOrganizationServiceAndSubServiceId(organizationServiceId,organizationSubServiceId);
        for (Long countryId : applicableUnitIdForSolverConfig) {
            countrySolverConfig.setIdBuilder(null);//Unset Id
            solverConfigRepository.saveObject(countrySolverConfig);
        }
    }


    /***********************************************************************************************************************/
    /**
     *  copy(create) countrySolverConfig at country Level itself
     *  Here TypeCasting is not required because coming DTO might get changed,so we require only
     *  id field from previous saved solver-config.
     */
    public void copyCountrySolverConfig(CountrySolverConfigDTO countrySolverConfigDTO) {
        Optional<SolverConfig> countrySolverConfigOptional = solverConfigRepository.findById(countrySolverConfigDTO.getId());
        if (countrySolverConfigOptional.isPresent()) {
            CountrySolverConfig countrySolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfigDTO, CountrySolverConfig.class);
            countrySolverConfig.setIdBuilder(null);//UnSet
            countrySolverConfig.setParentCountrySolverConfigId(countrySolverConfigOptional.get().getId());
            solverConfigRepository.saveObject(countrySolverConfig);
        }
    }

    /***************************************************************/
    public CountrySolverConfigDTO getCountrySolverConfig(BigInteger solverConfigId) {
        CountrySolverConfigDTO countrySolverConfigDTO = null;
        Optional<SolverConfig> countrySolverConfigOptional = solverConfigRepository.findById(solverConfigId);
        if (countrySolverConfigOptional.isPresent()) {
            CountrySolverConfig countrySolverConfig =(CountrySolverConfig) countrySolverConfigOptional.get();
            countrySolverConfigDTO = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfig, CountrySolverConfigDTO.class);
        }
        return countrySolverConfigDTO;
    }

    /*******************************get All ********************************/
    public List<CountrySolverConfigDTO> getAllCountrySolverConfig() {
        List<SolverConfig> solverConfigList = solverConfigRepository.findAllSolverConfigNotDeletedByType("country");
        return ObjectMapperUtils.copyPropertiesOfListByMapper(solverConfigList, CountrySolverConfigDTO.class);
    }

    /***************************************************************/
    //Only update if present
    public CountrySolverConfigDTO updateCountrySolverConfig(CountrySolverConfigDTO countrySolverConfigDTO) {
        Optional<SolverConfig> countrySolverConfigOptional = solverConfigRepository.findById(countrySolverConfigDTO.getId());
        boolean nameExists = solverConfigRepository.isNameExists(countrySolverConfigDTO.getName());
        if (countrySolverConfigOptional.isPresent() && !nameExists) {
            CountrySolverConfig countrySolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfigDTO, CountrySolverConfig.class);
            solverConfigRepository.saveObject(countrySolverConfig);
        }
        return countrySolverConfigDTO;
    }

    /***************************************************************/
    //Soft Delete
    public boolean deleteCountrySolverConfig(BigInteger solverConfigId) {
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
