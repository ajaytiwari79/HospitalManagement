package com.kairos.persistance.repository.master_data.asset_management.hosting_type;


import com.kairos.response.dto.common.HostingTypeResponseDTO;

import java.util.List;

public interface CustomHostingTypeRepository {

    List<HostingTypeResponseDTO> getAllNotInheritedHostingTypeFromParentOrgAndUnitHostingType(Long countryId, Long parentOrganizationId, Long organizationId);

}
