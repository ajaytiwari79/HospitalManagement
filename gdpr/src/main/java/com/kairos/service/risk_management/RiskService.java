package com.kairos.service.risk_management;


import com.kairos.persistence.repository.risk_management.RiskDaoImpl;
import com.kairos.response.dto.common.RiskResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class RiskService{

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private RiskDaoImpl riskDaoImpl;




    public List<RiskResponseDTO> getAllRiskByUnitId(Long unitId) {
        return riskDaoImpl.getAllRiskOfOrganizationId(unitId);
    }


}
