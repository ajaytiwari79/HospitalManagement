package com.kairos.persistance.repository.master_data.asset_management.storage_format;

import com.kairos.response.dto.common.StorageFormatResponseDTO;

import java.util.List;

public interface CustomStorageFormatRepository {

    List<StorageFormatResponseDTO> getAllNotInheritedStorageFormatFromParentOrgAndUnitStorageFormat(Long countryId, Long parentOrganizationId, Long organizationId);

}
