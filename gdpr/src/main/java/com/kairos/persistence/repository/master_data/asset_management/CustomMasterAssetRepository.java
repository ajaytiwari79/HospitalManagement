package com.kairos.persistence.repository.master_data.asset_management;

import com.kairos.dto.gdpr.FilterSelection;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationMetaDataDTO;
import com.kairos.enums.gdpr.FilterType;
import com.kairos.persistence.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
import org.springframework.data.mongodb.core.query.Criteria;

import java.math.BigInteger;
import java.util.List;

public interface CustomMasterAssetRepository {


    MasterAsset findByName(Long countryId, String name);


    List<MasterAssetResponseDTO> getMasterAssetDataWithFilterSelection(Long countryId, FilterSelectionDTO filterSelectionDto);

    Criteria buildMatchCriteria(FilterSelection filterSelection, FilterType filterType);

    List<MasterAssetResponseDTO> getAllMasterAssetWithAssetTypeAndSubAssetType(Long  countryId);

    MasterAssetResponseDTO getMasterAssetWithAssetTypeAndSubAssetTypeById(Long  countryId, BigInteger id);

    List<MasterAsset> getMasterAssetByOrgTypeSubTypeCategoryAndSubCategory(Long  countryId, OrganizationMetaDataDTO organizationMetaDataDTO);


}
