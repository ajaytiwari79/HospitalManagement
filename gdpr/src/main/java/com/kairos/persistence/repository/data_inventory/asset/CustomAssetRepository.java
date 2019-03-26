package com.kairos.persistence.repository.data_inventory.asset;

import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;

import java.util.List;

public interface CustomAssetRepository {

     List<AssetBasicResponseDTO> getAllAssetRelatedProcessingActivityByOrgId(Long orgId);
}
