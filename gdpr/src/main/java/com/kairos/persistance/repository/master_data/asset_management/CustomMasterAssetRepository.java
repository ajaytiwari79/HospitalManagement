package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistance.model.master_data.asset_management.MasterAsset;
import com.kairos.response.dto.filter.FilterQueryResult;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public interface CustomMasterAssetRepository {


    MasterAsset findByName(Long countryId, Long organizationId, String name);

    FilterQueryResult getMasterAssetFilter(Long countryId);

    List<MasterAsset> getMasterAssetDataWithFilterSelection(Long countryId, Long organizationId, FilterSelectionDTO filterSelectionDto);

    Criteria buildQuery(FilterSelection filterSelection, FilterType filterType, Query query);


}
