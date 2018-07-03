package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.AssetType;
import com.kairos.response.dto.master_data.AssetTypeResponseDto;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface CustomAssetTypeRepository {

    AssetType findByName(Long countryId,Long organizationId,String name);

    List<AssetTypeResponseDto> getAllAssetTypesWithSubAssetTypes(Long countryId,Long organizationId);

    AssetTypeResponseDto getAssetTypesWithSubAssetTypes(Long countryId, Long organizationId, BigInteger id);

}
