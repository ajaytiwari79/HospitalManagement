package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.dto.FilterSelection;
import com.kairos.dto.FilterSelectionDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistance.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.response.dto.filter.FilterQueryResult;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigInteger;
import java.util.List;

public interface CustomMasterAssetRepository {


    MasterAsset findByName(Long countryId, Long organizationId, String name);


    List<MasterAssetResponseDTO> getMasterAssetDataWithFilterSelection(Long countryId, Long organizationId, FilterSelectionDTO filterSelectionDto);

    Criteria buildMatchCriteria(FilterSelection filterSelection, FilterType filterType);

    List<MasterAssetResponseDTO> getAllMasterAssetWithAssetTypeAndSubAssetType(Long  countryId,Long organizationId);

    MasterAssetResponseDTO getMasterAssetWithAssetTypeAndSubAssetTypeById(Long  countryId, Long organizationId, BigInteger id);



}
