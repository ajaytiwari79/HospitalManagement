package com.kairos.persistance.repository.master_data.processing_activity_masterdata.responsibility_type;

import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;

import java.util.List;

public interface CustomResponsibilityTypeRepository {

    List<ResponsibilityTypeResponseDTO> getAllNotInheritedResponsibilityTypesFromParentOrgAndUnitResponsibilityType(Long countryId, Long parentOrganizationId, Long organizationId);


}
