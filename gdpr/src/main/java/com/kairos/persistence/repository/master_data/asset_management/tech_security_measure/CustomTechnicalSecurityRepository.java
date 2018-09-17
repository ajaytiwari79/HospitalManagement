package com.kairos.persistence.repository.master_data.asset_management.tech_security_measure;

import com.kairos.response.dto.common.TechnicalSecurityMeasureResponseDTO;

import java.util.List;

public interface CustomTechnicalSecurityRepository {


    List<TechnicalSecurityMeasureResponseDTO> getAllNotInheritedTechnicalSecurityMeasureFromParentOrgAndUnitSecurityMeasure(Long countryId, Long parentOrganizationId, Long organizationId);

}
