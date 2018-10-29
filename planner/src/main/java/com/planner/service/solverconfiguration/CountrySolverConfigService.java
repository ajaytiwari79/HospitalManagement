package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.planner.solverconfig.DefaultDataDTO;
import com.kairos.dto.planner.solverconfig.country.CountrySolverConfigDTO;
import com.kairos.dto.user.organization.OrganizationServiceDTO;
import com.planner.component.exception.ExceptionService;
import com.planner.domain.query_results.organization_service.OrganizationServiceQueryResult;
import com.planner.domain.solverconfig.common.SolverConfig;
import com.planner.domain.solverconfig.country.CountrySolverConfig;
import com.planner.domain.solverconfig.unit.UnitSolverConfig;
import com.planner.repository.shift_planning.ActivityMongoRepository;
import com.planner.repository.shift_planning.UserNeo4jRepo;
import com.planner.repository.solver_config.SolverConfigRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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

    //================================================================

    /**
     * @param countrySolverConfigDTO
     */
    public CountrySolverConfigDTO createCountrySolverConfig(CountrySolverConfigDTO countrySolverConfigDTO) {
        if (preValidateCountrySolverConfigDTO(countrySolverConfigDTO, true)) {
            CountrySolverConfig countrySolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfigDTO, CountrySolverConfig.class);
            solverConfigRepository.saveObject(countrySolverConfig);
            //Now copy same countrySolverConfig at {unit/s} associated with {organizationSubServiceId}
            copyUnitSolverConfigByOrganizationServiceAndSubService(countrySolverConfigDTO.getOrganizationServiceId(), countrySolverConfigDTO.getOrganizationSubServiceId(), countrySolverConfig);
            countrySolverConfigDTO.setId(countrySolverConfig.getId());
        }
        return countrySolverConfigDTO;
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
    private void copyUnitSolverConfigByOrganizationServiceAndSubService(Long organizationServiceId, Long organizationSubServiceId, CountrySolverConfig countrySolverConfig) {
        List<Long> applicableUnitIdForSolverConfig = userNeo4jRepo.getUnitIdsByOrganizationServiceAndSubServiceId(organizationServiceId, organizationSubServiceId);
        List<UnitSolverConfig> unitSolverConfigList = new ArrayList<>();
        if (!applicableUnitIdForSolverConfig.isEmpty()) {
            for (Long unitId : applicableUnitIdForSolverConfig) {
                UnitSolverConfig unitSolverConfig = new UnitSolverConfig();
                unitSolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfig, UnitSolverConfig.class);
                unitSolverConfig.setIdBuilder(null);//Unset Id
                unitSolverConfig.setUnitId(unitId);
                unitSolverConfig.setParentCountrySolverConfigId(countrySolverConfig.getId());
                unitSolverConfigList.add(unitSolverConfig);
            }
            if (unitSolverConfigList.size() > 0) {
                solverConfigRepository.saveObjectList(unitSolverConfigList);
            }
        }
    }


    //===========================================================================

    /**
     * copy(create) countrySolverConfig at country Level itself
     * Here TypeCasting is not required because coming DTO might get changed,so we require only
     * id field from previous saved solver-config.
     */
    public CountrySolverConfigDTO copyCountrySolverConfig(CountrySolverConfigDTO countrySolverConfigDTO) {
        SolverConfig solverConfig = solverConfigRepository.findByIdNotDeleted(countrySolverConfigDTO.getId());
        if (solverConfig != null && preValidateCountrySolverConfigDTO(countrySolverConfigDTO, true)) {
            CountrySolverConfig countrySolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfigDTO, CountrySolverConfig.class);
            countrySolverConfig.setId(null);//UnSet
            countrySolverConfig.setParentCountrySolverConfigId(countrySolverConfigDTO.getId());
            solverConfigRepository.saveObject(countrySolverConfig);
            //Now copy same countrySolverConfig at {unit/s} associated with {organizationSubServiceId}
            copyUnitSolverConfigByOrganizationServiceAndSubService(countrySolverConfigDTO.getOrganizationServiceId(), countrySolverConfigDTO.getOrganizationSubServiceId(), countrySolverConfig);
            countrySolverConfigDTO.setId(countrySolverConfig.getId());
        }
        return countrySolverConfigDTO;
    }

    //=============================================================================
    public List<CountrySolverConfigDTO> getAllCountrySolverConfigByCountryId(Long countryId) {
        List<SolverConfig> solverConfigList = solverConfigRepository.findAllObjectsNotDeletedById(true, countryId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(solverConfigList, CountrySolverConfig.class);
    }


    //=============================================================================
    //Only update if present
    public CountrySolverConfigDTO updateCountrySolverConfig(CountrySolverConfigDTO countrySolverConfigDTO) {
        SolverConfig solverConfig = solverConfigRepository.findByIdNotDeleted(countrySolverConfigDTO.getId());
        if (solverConfig != null && preValidateCountrySolverConfigDTO(countrySolverConfigDTO, false)) {
            CountrySolverConfig countrySolverConfig = ObjectMapperUtils.copyPropertiesByMapper(countrySolverConfigDTO, CountrySolverConfig.class);
            solverConfigRepository.saveObject(countrySolverConfig);
        }
        return countrySolverConfigDTO;
    }

    //============================================================================
    //Soft Delete
    public boolean deleteCountrySolverConfig(BigInteger solverConfigId) {
        boolean success = solverConfigRepository.safeDeleteById(solverConfigId);
        if (!success) {
            //TODO throw exception if required
        }
        return success;
    }

    /*=================================Country Default Data===============================================*/
    public DefaultDataDTO getDefaultData(Long countryId) {
        DefaultDataDTO defaultDataDTO = new DefaultDataDTO()
                //get all organizationServices by countryId
                .setOrganizationServiceDTOSBuilder(getOrganizationServicesAndItsSubServices(countryId))
                //get All Phases
                .setPhaseDTOSBuilder(getAllPhases(countryId));

        return defaultDataDTO;
    }

    /**
     * @param countryId
     * @return
     */
    private List<OrganizationServiceDTO> getOrganizationServicesAndItsSubServices(Long countryId) {
        List<OrganizationServiceQueryResult> organizationServiceQueryResults = userNeo4jRepo.getAllOrganizationServices(countryId);
        List<OrganizationServiceDTO> organizationServiceDTOS = ObjectMapperUtils.copyPropertiesOfListByMapper(organizationServiceQueryResults, OrganizationServiceDTO.class);

        return organizationServiceDTOS;
    }

    /**
     * @param countryId
     * @return
     */
    private List<PhaseDTO> getAllPhases(Long countryId) {
        List<PhaseDTO> phaseDTOS = activityMongoRepository.getAllPhasesByCountryId(countryId);
        return phaseDTOS;
    }

    //=====================common validation==============================================

    /**
     * Validation sequence should follow this ordering
     *
     * @param countrySolverConfigDTO
     * @return
     */
    private boolean preValidateCountrySolverConfigDTO(CountrySolverConfigDTO countrySolverConfigDTO, boolean isCurrentObjectIdNull) {
        String result = userNeo4jRepo.validateCountryOrganizationServiceAndSubService(countrySolverConfigDTO.getCountryId(), countrySolverConfigDTO.getOrganizationServiceId(), countrySolverConfigDTO.getOrganizationSubServiceId());

        if ("countryNotExists".equals(result)) {

            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Country", countrySolverConfigDTO.getCountryId());
        } else if (solverConfigRepository.isNameExistsById(countrySolverConfigDTO.getName(), isCurrentObjectIdNull ? null : countrySolverConfigDTO.getId(), true, countrySolverConfigDTO.getCountryId())) {
            exceptionService.dataNotFoundByIdException("message.name.alreadyExists");
        } else if ("organizationServiceNotExists".equals(result)) {

            exceptionService.dataNotFoundByIdException("message.dataNotFound", "OrganizationService", countrySolverConfigDTO.getOrganizationServiceId());
        } else if ("organizationSubServiceNotExists".equals(result)) {

            exceptionService.dataNotFoundByIdException("message.dataNotFound", "OrganizationSubService", countrySolverConfigDTO.getOrganizationSubServiceId());
        } else if ("relationShipNotValid".equals(result)) {

            exceptionService.relationShipNotValidException("message.relationship.notValid");
        }

        return true;
    }

}
