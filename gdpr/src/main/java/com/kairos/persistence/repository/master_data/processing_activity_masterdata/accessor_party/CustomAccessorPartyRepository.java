package com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party;


import com.kairos.response.dto.common.AccessorPartyResponseDTO;

import java.util.List;

public interface CustomAccessorPartyRepository {


    List<AccessorPartyResponseDTO> getAllNotInheritedAccessorPartyFromParentOrgAndUnitAccessorParty(Long countryId, Long parentOrganizationId, Long organizationId);

}
