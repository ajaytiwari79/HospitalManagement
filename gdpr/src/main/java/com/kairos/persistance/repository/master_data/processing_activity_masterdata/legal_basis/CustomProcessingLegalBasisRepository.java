package com.kairos.persistance.repository.master_data.processing_activity_masterdata.legal_basis;

import com.kairos.response.dto.common.ProcessingLegalBasisResponseDTO;

import java.util.List;

public interface CustomProcessingLegalBasisRepository {

    List<ProcessingLegalBasisResponseDTO> getAllNotInheritedLegalBasisFromParentOrgAndUnitProcessingLegalBasis(Long countryId, Long parentOrganizationId, Long organizationId);

}
