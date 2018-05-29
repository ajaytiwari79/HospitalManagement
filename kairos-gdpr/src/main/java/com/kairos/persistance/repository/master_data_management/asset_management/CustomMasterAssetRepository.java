package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.response.dto.filter.FilterQueryResult;

public interface CustomMasterAssetRepository {


    FilterQueryResult getMasterAssetFilter(Long countryId);



}
