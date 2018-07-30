package com.kairos.persistance.repository.master_data.asset_management.org_security_measure;

import com.kairos.response.dto.metadata.OrganizationalSecurityMeasureResponseDTO;

import java.util.List;

public interface CustomOrganizationalSecurityRepository {


    List<OrganizationalSecurityMeasureResponseDTO> getAllNotInheritedFromParentOrgAndUnitOrgSecurityMeasure(Long countryId, Long parentOrganizationId, Long organizationId);

}
