package com.kairos.persistance.repository.master_data.asset_management.tech_security_measure;

import com.kairos.response.dto.metadata.TechnicalSecurityMeasureReponseDTO;

import java.util.List;

public interface CustomTechnicalSecurityRepository {


    List<TechnicalSecurityMeasureReponseDTO> getAllNotInheritedTechnicalSecurityMeasureFromParentOrgAndUnitSecurityMeasure(Long countryId, Long parentOrganizationId, Long organizationId);

}
