package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomAssetTypeRepository {

    AssetType findByNameAndCountryId(Long countryId,String name);

    List<AssetTypeResponseDTO> getAllCountryAssetTypesWithSubAssetTypes(Long countryId);

    AssetTypeResponseDTO getCountryAssetTypesWithSubAssetTypes(Long countryId, BigInteger id);

    AssetType findByNameAndOrganizationId(Long organizationId,String name);

    List<AssetTypeResponseDTO> getAllOrganizationAssetTypesWithSubAssetTypes(Long organizationId);

    AssetTypeResponseDTO getOrganizationAssetTypesWithSubAssetTypes( Long organizationId, BigInteger id);



}
