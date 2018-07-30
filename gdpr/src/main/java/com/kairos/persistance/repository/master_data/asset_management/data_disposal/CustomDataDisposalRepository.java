package com.kairos.persistance.repository.master_data.asset_management.data_disposal;

import com.kairos.response.dto.metadata.DataDisposalResponseDTO;
import com.kairos.response.dto.metadata.DataSourceResponseDTO;

import java.util.List;

public interface CustomDataDisposalRepository {

    List<DataDisposalResponseDTO> getAllNotInheritedDataDisposalFromParentOrgAndUnitDataDisposal(Long countryId, Long parentOrganizationId, Long organizationId);

}
