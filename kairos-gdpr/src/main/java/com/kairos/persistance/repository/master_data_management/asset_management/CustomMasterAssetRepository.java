package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDto;
import com.kairos.persistance.model.enums.FilterType;
import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.response.dto.filter.FilterQueryResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface CustomMasterAssetRepository {


    FilterQueryResult getMasterAssetFilter(Long countryId);

    List<MasterAsset> getMasterAssetDataWithFilterSelection(Long countryId, FilterSelectionDto filterSelectionDto);

     Criteria buildQuery(FilterSelection filterSelection,FilterType filterType, Query query);



}
