package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.response.dto.master_data.AssetTypeResponseDto;

import java.math.BigInteger;
import java.util.List;

public interface CustomAssetTypeRepository {

    AssetType findByName(Long countryId,Long organizationId,String name);

    List<AssetTypeResponseDto> getAllAssetTypesWithSubAssetTypes(Long countryId,Long organizationId);

    AssetTypeResponseDto getAssetTypesWithSubAssetTypes(Long countryId, Long organizationId, BigInteger id);

}
