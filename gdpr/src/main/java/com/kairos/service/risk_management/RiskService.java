package com.kairos.service.risk_management;


import com.kairos.dto.gdpr.BasicRiskDTO;
import com.kairos.persistence.repository.risk_management.RiskDaoImpl;
import com.kairos.persistence.repository.risk_management.RiskRepository;
import com.kairos.response.dto.common.RiskResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class RiskService{

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private RiskRepository riskRepository;

    @Inject
    private RiskDaoImpl riskDaoImpl;


    private <E extends BasicRiskDTO> void checkForDuplicateNames(List<E> riskDTOS) {

        List<String> riskNames = new ArrayList<>();
        for (E riskDTO : riskDTOS) {
            if (riskNames.contains(riskDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "RISK", riskDTO.getName());
            }
            riskNames.add(riskDTO.getName().toLowerCase());
        }
    }


    public List<RiskResponseDTO> getAllRiskByUnitId(Long organizationId) {
        return riskDaoImpl.getAllRiskOfOrganizationId(organizationId);
    }


}
