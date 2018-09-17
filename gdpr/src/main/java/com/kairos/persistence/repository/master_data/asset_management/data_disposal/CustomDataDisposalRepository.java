package com.kairos.persistence.repository.master_data.asset_management.data_disposal;

import com.kairos.response.dto.common.DataDisposalResponseDTO;

import java.util.List;

public interface CustomDataDisposalRepository {

    List<DataDisposalResponseDTO> getAllNotInheritedDataDisposalFromParentOrgAndUnitDataDisposal(Long countryId, Long parentOrganizationId, Long organizationId);

}
