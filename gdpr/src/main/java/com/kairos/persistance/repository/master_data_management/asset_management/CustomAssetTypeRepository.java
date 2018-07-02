package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.AssetType;

import java.util.List;
import java.util.Set;

public interface CustomAssetTypeRepository {

    AssetType findByName(Long countryId,Long organizationId,String name);


}
