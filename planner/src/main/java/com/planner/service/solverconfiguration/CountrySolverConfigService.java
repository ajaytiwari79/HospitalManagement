package com.planner.service.solverconfiguration;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.planner.domain.solverconfig.SolverConfig;
import com.planner.repository.config.SolverConfigRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class CountrySolverConfigService {

    @Inject
    private SolverConfigRepository solverConfigRepository;

    public void createCountrySolverConfig(SolverConfigDTO solverConfigDTO) {
        boolean nameExists = solverConfigRepository.isNameExists(solverConfigDTO.getName(), SolverConfig.class);
        boolean isInstanceOfProxy=false;
        if (!nameExists) {
            SolverConfig solverConfig = ObjectMapperUtils.copyPropertiesByMapper(solverConfigDTO, SolverConfig.class);
            solverConfigRepository.saveObject(solverConfig);
        }

    }

    public SolverConfigDTO getCountrySolverConfig(String solverConfigId) {
        SolverConfigDTO solverConfigDTO = null;
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(solverConfigId);
        if (solverConfigOptional.isPresent()) {
            SolverConfig solverConfig = solverConfigOptional.get();
            solverConfigDTO = ObjectMapperUtils.copyPropertiesByMapper(solverConfig, SolverConfigDTO.class);
        }
        return solverConfigDTO;
    }

    public List<SolverConfigDTO> getAllCountrySolverConfig() {
        List<SolverConfig> solverConfigList = solverConfigRepository.findAllNotDeleted(SolverConfig.class);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(solverConfigList, SolverConfigDTO.class);
    }

    //Only update if present
    public SolverConfigDTO updateCountrySolverConfig(SolverConfigDTO solverConfigDTO) {
        Optional<SolverConfig> solverConfigOptional = solverConfigRepository.findById(solverConfigDTO.getId()+"");
        boolean nameExists = solverConfigRepository.isNameExists(solverConfigDTO.getName(), SolverConfig.class);

        if (solverConfigOptional.isPresent() && !nameExists) {
            SolverConfig solverConfig = ObjectMapperUtils.copyPropertiesByMapper(solverConfigDTO, SolverConfig.class);
            solverConfigRepository.save(solverConfig);
        }
        return solverConfigDTO;
    }

    //Soft Delete
    public boolean deleteCountrySolverConfig(String solverConfigId) {
        boolean isPresent = solverConfigRepository.findById(solverConfigId).isPresent();
        if (isPresent) {
            solverConfigRepository.safeDeleteById(solverConfigId, SolverConfig.class);
        }
        return isPresent;
    }


}
