package com.kairos.service.common;


import com.kairos.gdpr.data_inventory.OrganizationMetaDataDTO;
import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistance.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.persistance.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistance.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistance.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.MasterProcessingActivityRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataInheritOrganizationLevelService extends MongoBaseService {


    @Inject
    private MasterAssetMongoRepository masterAssetMongoRepository;

    @Inject
    private MasterProcessingActivityRepository masterProcessingActivityRepository;

    @Inject
    private AssetMongoRepository assetMongoRepository;

    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;


    /**
     *
     * @param countryId
     * @param parentOrganizationId - id of parent organization from which unit inherit data
     * @param unitId - id of the organization which inherit data from from
     * @param organizationMetaData - contain meta data about child organization, on the basis of meta data (org type ,sub type ,service category and sub service) unit
     *                             inherit data from parent
     * @return
     */
    public Boolean inheritDataFromParentOrganization(Long countryId, Long parentOrganizationId, Long unitId, OrganizationMetaDataDTO organizationMetaData) {

        List<MasterAsset> masterAssetList = masterAssetMongoRepository.getMasterAssetByOrgTypeSubTypeCategoryAndSubCategory(countryId, parentOrganizationId, organizationMetaData);

        if (!masterAssetList.isEmpty()) {
            Boolean activeStatus = false;
            List<Asset> organizationAssetList = new ArrayList<>();
            masterAssetList.forEach(masterAsset -> {

                Asset asset = new Asset(masterAsset.getName(), masterAsset.getDescription(), activeStatus);
                asset.setOrganizationId(unitId);
                organizationAssetList.add(asset);
            });

            assetMongoRepository.saveAll(organizationAssetList);
        }

        List<MasterProcessingActivity> masterProcessingActivityList = masterProcessingActivityRepository.getMasterProcessingActivityByOrgTypeSubTypeCategoryAndSubCategory(countryId, parentOrganizationId, organizationMetaData);


        if (!masterProcessingActivityList.isEmpty()) {
            Boolean activeStatus = false;
            List<ProcessingActivity> organizationProcessingActivityList = new ArrayList<>();
            masterProcessingActivityList.forEach(masterProcessingActivity -> {

                ProcessingActivity processingActivity = new ProcessingActivity(masterProcessingActivity.getName(), masterProcessingActivity.getDescription(), activeStatus);
                processingActivity.setOrganizationId(unitId);
                organizationProcessingActivityList.add(processingActivity);
            });

            processingActivityMongoRepository.saveAll(organizationProcessingActivityList);
        }


        return true;

    }


}
