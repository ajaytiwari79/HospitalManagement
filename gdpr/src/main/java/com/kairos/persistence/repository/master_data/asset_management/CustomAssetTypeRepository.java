package com.kairos.persistence.repository.master_data.asset_management;

import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.response.dto.master_data.AssetTypeRiskResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomAssetTypeRepository {

    AssetType findByNameAndCountryId(Long countryId,String name);

    List<AssetTypeRiskResponseDTO> getAllAssetTypeWithSubAssetTypeAndRiskByCountryId(Long countryId);

    AssetTypeResponseDTO getAssetTypesWithSubAssetTypesByIdAndCountryId(Long countryId, BigInteger id);

    AssetType findByNameAndUnitId(Long organizationId, String name);

    AssetTypeResponseDTO getAssetTypesWithSubAssetTypesByIdAndUnitId(Long unitId, BigInteger id);

    List<AssetTypeRiskResponseDTO> getAllAssetTypeWithSubAssetTypeAndRiskByUnitId(Long unitId);

    List<AssetTypeResponseDTO> getAllAssetTypeWithSubAssetTypeByUnitId(Long unitId);



}
