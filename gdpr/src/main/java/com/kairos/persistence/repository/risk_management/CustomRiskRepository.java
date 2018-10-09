package com.kairos.persistence.repository.risk_management;

import com.kairos.response.dto.common.RiskResponseDTO;

import java.util.List;

public interface CustomRiskRepository {


    List<RiskResponseDTO> getAllRiskByUnitId(Long unitId);
}
