package com.kairos.persistance.repository.master_data.processing_activity_masterdata.accessor_party;


import com.kairos.response.dto.metadata.AccessorPartyReponseDTO;

import java.util.List;

public interface CustomAccessorPartyRepository {


    List<AccessorPartyReponseDTO> getAllNotInheritedAccesorPartyFromParentOrgAndUnitAccesorParty(Long countryId,Long parentOrganizationId,Long organizationId);
}
