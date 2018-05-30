package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.dto.FilterSelectionDto;
import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.response.dto.filter.FilterQueryResult;

import java.util.List;

public interface CustomMasterAssetRepository {


    FilterQueryResult getMasterAssetFilter(Long countryId);

    List<MasterAsset> getMasterAssetListWithFilterData(Long countryId,FilterSelectionDto filterSelectionDto);



}
