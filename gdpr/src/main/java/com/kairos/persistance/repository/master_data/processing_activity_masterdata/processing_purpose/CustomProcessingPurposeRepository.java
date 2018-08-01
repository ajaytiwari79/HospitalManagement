package com.kairos.persistance.repository.master_data.processing_activity_masterdata.processing_purpose;

import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;

import java.util.List;

public interface CustomProcessingPurposeRepository {


    List<ProcessingPurposeResponseDTO> getAllNotInheritedProcessingPurposesFromParentOrgAndUnitProcessingPurpose(Long countryId, Long parentOrganizationId, Long organizationId);

}
