package com.kairos.persistance.repository.data_inventory.asset;

import com.kairos.persistance.model.data_inventory.asset.Asset;

public interface CustomAssetRepository {

Asset findByName(Long countryid,Long organizationId,String name);

}
