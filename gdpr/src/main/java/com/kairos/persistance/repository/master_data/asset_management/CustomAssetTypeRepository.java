package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.response.dto.master_data.AssetTypeRiskResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomAssetTypeRepository {

    AssetType findByNameAndCountryId(Long countryId,String name);

    List<AssetTypeRiskResponseDTO> getAllAssetTypesByCountryId(Long countryId);

    List<AssetTypeRiskResponseDTO> getSubAssetTypesByAssetTypeIdAndCountryId(Long countryId,BigInteger assetTypeId);

    AssetTypeResponseDTO getCountryAssetTypesWithSubAssetTypes(Long countryId, BigInteger id);

    AssetType findByNameAndOrganizationId(Long organizationId,String name);

    AssetTypeResponseDTO getOrganizationAssetTypesWithSubAssetTypes( Long unitId, BigInteger id);

    List<AssetTypeRiskResponseDTO> getAllAssetTypesByUnitId(Long unitId);

    List<AssetTypeRiskResponseDTO> getSubAssetTypesByAssetTypeIdAndUnitId(Long unitId,BigInteger assetTypeId);





}
