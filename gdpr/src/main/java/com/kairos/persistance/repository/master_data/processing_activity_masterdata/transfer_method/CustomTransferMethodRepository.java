package com.kairos.persistance.repository.master_data.processing_activity_masterdata.transfer_method;

import com.kairos.response.dto.common.TransferMethodResponseDTO;

import java.util.List;

public interface CustomTransferMethodRepository {

    List<TransferMethodResponseDTO> getAllNotInheritedTransferMethodFromParentOrgAndUnitTransferMethod(Long countryId, Long parentOrganizationId, Long organizationId);

}
