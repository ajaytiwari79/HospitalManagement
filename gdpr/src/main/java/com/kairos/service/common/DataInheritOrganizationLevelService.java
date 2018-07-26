package com.kairos.service.common;


import com.kairos.dto.data_inventory.OrganizationMetaDataDTO;
import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistance.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistance.repository.master_data.asset_management.MasterAssetMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataInheritOrganizationLevelService extends MongoBaseService {


    @Inject
    private MasterAssetMongoRepository masterAssetMongoRepository;

    @Inject
    private AssetMongoRepository assetMongoRepository;


    public Boolean inheritDataFromParentOrganization(Long countryId, Long parentOrganizationId, Long unitId, OrganizationMetaDataDTO organizationMetaData) {

        List<MasterAsset> masterAssetList = masterAssetMongoRepository.getMasterAssetByOrgTypeSubTypeCategoryAndSubCategory(countryId, parentOrganizationId, organizationMetaData);

        if (!masterAssetList.isEmpty()) {
            Boolean activeStatus = false;
            List<Asset> organizationAssetList = new ArrayList<>();
            masterAssetList.forEach(masterAsset -> {

                Asset asset = new Asset(masterAsset.getName(), masterAsset.getDescription(), countryId, activeStatus);
                asset.setOrganizationId(unitId);
                organizationAssetList.add(asset);
            });

            assetMongoRepository.saveAll(sequenceGenerator(organizationAssetList));
        }

        return true;

    }


}
