package com.kairos.persistence.repository.data_inventory.asset;

import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomAssetRepository {

    Asset findByName( Long organizationId, String name);

    AssetResponseDTO findAssetWithMetaDataById( Long organizationId, BigInteger id);

    List<AssetResponseDTO> findAllAssetWithMetaData( Long organizationId);

}
