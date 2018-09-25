package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.planner.domain.query_results.organization_service.OrganizationServiceQueryResult;
import com.planner.domain.solverconfig.SolverConfig;
import com.planner.repository.config.SolverConfigRepository;
import com.planner.repository.shift_planning.UserNeo4jRepo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Service
public class CountrySolverConfigService {

    @Inject
    private SolverConfigRepository solverConfigRepository;
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
            if (solverConfigDTO.getOrganizationServiceCategoryId() != null) {
                //Now copy same solverConfig at {unit/s} associated with {organizationServiceCategoryId}
                copyUnitSolverConfigByOrganizationServiceCategory(solverConfigDTO.getOrganizationServiceCategoryId());
            }
        }
    }

    /**
     * copy(create) solverConfig at Unit Level By organizationServiceCategoryId
     */
    private void copyUnitSolverConfigByOrganizationServiceCategory(Long organizationServiceCategoryId) {


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
    public List<OrganizationServiceQueryResult> getDefaultData(Long countryId) {
        //get all organizationServices by countryId
        return getOrganizationServicesAndItsSubServices(countryId);
    }

    private List<OrganizationServiceQueryResult> getOrganizationServicesAndItsSubServices(Long countryId) {
        return userNeo4jRepo.getAllOrganizationServices(countryId);
    }
}
