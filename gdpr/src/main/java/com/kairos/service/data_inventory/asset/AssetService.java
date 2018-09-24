package com.kairos.service.data_inventory.asset;

import com.kairos.dto.gdpr.data_inventory.AssetDTO;
import com.kairos.dto.gdpr.data_inventory.AssetRelateProcessingActivityDTO;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentMongoRepository;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.service.master_data.asset_management.MasterAssetService;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.IS_SUCCESS;


@Service
public class AssetService extends MongoBaseService {


    @Inject
    private AssetMongoRepository assetMongoRepository;

    @Inject
    private Javers javers;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private JaversCommonService javersCommonService;


    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;

    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;

    @Inject
    private AssessmentMongoRepository assessmentMongoRepository;

    @Inject
    private QuestionnaireTemplateMongoRepository questionnaireTemplateMongoRepository;

    @Inject
    private MasterAssetService masterAssetService;


    public AssetDTO createAssetWithBasicDetail(Long organizationId, AssetDTO assetDTO) {
        Asset previousAsset = assetMongoRepository.findByName(organizationId, assetDTO.getName());
        if (Optional.ofNullable(previousAsset).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", " Asset ", assetDTO.getName());
        }
        AssetType assetType = assetTypeMongoRepository.findByIdAndUnitId(organizationId, assetDTO.getAssetType());
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset  type", assetDTO.getAssetType());
        } else {
            if (Optional.ofNullable(assetType.getSubAssetTypes()).isPresent()) {
                if (!assetType.getSubAssetTypes().containsAll(assetDTO.getAssetSubTypes())) {
                    exceptionService.invalidRequestException("message.invalid.request", " invalid Sub Asset is Selected ");
                }
            }
        }
        Asset asset = new Asset(assetDTO.getName(), assetDTO.getDescription(), assetDTO.getHostingLocation(),
                assetDTO.getAssetType(), assetDTO.getAssetSubTypes(), assetDTO.getManagingDepartment(), assetDTO.getAssetOwner());
        asset.setOrganizationId(organizationId);
        asset.setHostingProvider(assetDTO.getHostingProvider());
        asset.setHostingType(assetDTO.getHostingType());
        asset.setOrgSecurityMeasures(assetDTO.getOrgSecurityMeasures());
        asset.setTechnicalSecurityMeasures(assetDTO.getTechnicalSecurityMeasures());
        asset.setStorageFormats(assetDTO.getStorageFormats());
        asset.setDataDisposal(assetDTO.getDataDisposal());
        asset.setDataRetentionPeriod(assetDTO.getDataRetentionPeriod());
        asset.setMaxDataSubjectVolume(assetDTO.getMaxDataSubjectVolume());
        asset.setMinDataSubjectVolume(assetDTO.getMinDataSubjectVolume());
        assetMongoRepository.save(asset);
        assetDTO.setId(asset.getId());
        return assetDTO;
    }


    public Map<String, Object> deleteAssetById(Long organizationId, BigInteger assetId) {
        Asset asset = assetMongoRepository.findByIdAndNonDeleted(organizationId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset " + assetId);
        }
        List<ProcessingActivityBasicResponseDTO> linkedProcessingActivities = processingActivityMongoRepository.findAllProcessingActivityLinkWithAssetById(organizationId, assetId);
        Map<String, Object> result = new HashMap<>();
        if (!linkedProcessingActivities.isEmpty()) {
            result.put(IS_SUCCESS, false);
            result.put("data", linkedProcessingActivities);
            result.put("message", "Asset is linked with Processing Activities");
        } else {
            delete(asset);
            result.put(IS_SUCCESS, true);
        }
        return result;
    }


    /**
     * @param unitId
     * @param assetId asset id
     * @param active  status of Asset
     * @return
     * @description method updated active status of Asset
     */
    public boolean updateStatusOfAsset(Long unitId, BigInteger assetId, boolean active) {
        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assetId);
        }
        asset.setActive(active);
        assetMongoRepository.save(asset);
        return true;
    }


    /**
     * @param
     * @param organizationId
     * @param id
     * @return method return Asset with Meta Data (storage format ,data Disposal, hosting type and etc)
     */
    public AssetResponseDTO getAssetWithMetadataById(Long organizationId, BigInteger id) {
        AssetResponseDTO asset = assetMongoRepository.findAssetWithMetaDataById(organizationId, id);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset " + id);
        }
        return asset;
    }


    /**
     * @param
     * @param organizationId
     * @return return list Of Asset With Meta Data
     */
    public List<AssetResponseDTO> getAllAssetWithMetadata(Long organizationId) {
        return assetMongoRepository.findAllAssetWithMetaData(organizationId);
    }


    /**
     * @param assetId
     * @return
     * @description method return audit history of asset , old Object list and latest version also.
     * return object contain  changed field with key fields and values with key Values in return list of map
     */
    public List<Map<String, Object>> getAssetActivitiesHistory(BigInteger assetId) {

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(assetId, Asset.class);
        List<CdoSnapshot> changes = javers.findSnapshots(jqlQuery.build());
        changes.sort((o1, o2) -> -1 * (int) o1.getVersion() - (int) o2.getVersion());
        return javersCommonService.getHistoryMap(changes, assetId, Asset.class);


    }


    public List<AssetBasicResponseDTO> getAllActiveAsset(Long unitId) {
        return assetMongoRepository.getAllAssetWithBasicDetailByStatus(unitId, true);
    }

    /**
     * @param
     * @param organizationId
     * @param assetId        - asset id
     * @param assetDTO       - asset dto contain meta data about asset
     * @return - updated Asset
     */
    public AssetDTO updateAssetData(Long organizationId, BigInteger assetId, AssetDTO assetDTO) {

        Asset asset = assetMongoRepository.findByName(organizationId, assetDTO.getName());
        if (Optional.ofNullable(asset).isPresent() && !assetId.equals(asset.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "Asset", assetDTO.getName());
        }
        asset = assetMongoRepository.findOne(assetId);
        if (!asset.isActive()) {
            exceptionService.invalidRequestException("message.asset.inactive");
        }
        AssetType assetType = assetTypeMongoRepository.findByIdAndUnitId(organizationId, assetDTO.getAssetType());
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset Type", assetDTO.getAssetType());

        } else {
            if (Optional.ofNullable(assetType.getSubAssetTypes()).isPresent()) {
                if (!assetType.getSubAssetTypes().containsAll(assetDTO.getAssetSubTypes())) {
                    exceptionService.invalidRequestException("message.invalid.request", " invalid Sub Asset is Selected ");
                }
            }
        }
        ObjectMapperUtils.copyProperties(assetDTO, asset);
        assetMongoRepository.save(asset);
        return assetDTO;
    }


    /**
     * @param unitId
     * @param assetId
     * @param assetRelateProcessingActivityDTO
     * @return
     * @description map asset with Processing activity
     */
    public Asset addProcessingActivitiesAndSubProcessingActivitiesToAsset(Long unitId, BigInteger assetId, AssetRelateProcessingActivityDTO assetRelateProcessingActivityDTO) {
        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assetId);
        }
        asset.setProcessingActivities(assetRelateProcessingActivityDTO.getProcessingActivities());
        asset.setSubProcessingActivities(assetRelateProcessingActivityDTO.getSubProcessingActivities());
        assetMongoRepository.save(asset);
        return asset;
    }


    /**
     * @param unitId
     * @param assetId              - asset Id
     * @param processingActivityId Processing Activity id link with Asset
     * @return
     */
    public boolean unLinkProcessingActivityFromAsset(Long unitId, BigInteger assetId, BigInteger processingActivityId) {
        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assetId);
        }
        asset.getProcessingActivities().remove(processingActivityId);
        assetMongoRepository.save(asset);
        return true;

    }


    /**
     * @param unitId
     * @param assetId                 -Asset Id
     * @param subProcessingActivityId - Sub Processing Activity Id Link with Asset
     * @return
     */
    public boolean unLinkSubProcessingActivityFromAsset(Long unitId, BigInteger assetId, BigInteger subProcessingActivityId) {
        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assetId);
        }
        asset.getSubProcessingActivities().remove(subProcessingActivityId);
        assetMongoRepository.save(asset);
        return true;

    }


    /**
     * @description create asset at unit level  and suggest asset to country admin
     * @param unitId -unitid
     * @param countryId -country id
     * @param assetDTO
     * @return
     */
    public Map<String, AssetDTO> saveAssetAndSuggestToCountryAdmin(Long unitId, Long countryId, AssetDTO assetDTO) {

        Map<String, AssetDTO> result = new HashMap<>();
        assetDTO = createAssetWithBasicDetail(unitId, assetDTO);
        AssetDTO masterAsset = masterAssetService.saveSuggestedAssetDataFromUnit(countryId, unitId, assetDTO);
        result.put("new", assetDTO);
        result.put("SuggestedData", masterAsset);
        return result;

    }


    public List<ProcessingActivityBasicResponseDTO> getAllRelatedProcessingActivityAndSubProcessingActivities(Long unitId, BigInteger assetId) {

        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assetId);
        }
        Set<BigInteger> processingActivitiesIdList = asset.getProcessingActivities();
        List<ProcessingActivityBasicResponseDTO> processingActivityResponseDTOList = new ArrayList<>();
        if (!processingActivitiesIdList.isEmpty()) {
            processingActivityResponseDTOList = processingActivityMongoRepository.getAllAssetRelatedProcessingActivityWithSubProcessAndMetaData(unitId, processingActivitiesIdList);
            Set<BigInteger> subProcessingActivitiesIdsList = asset.getSubProcessingActivities();

            for (ProcessingActivityBasicResponseDTO processingActivityBasicResponseDTO : processingActivityResponseDTOList) {

                List<ProcessingActivityBasicResponseDTO> subProcessingActivities = processingActivityBasicResponseDTO.getSubProcessingActivities();
                boolean defaultSelected = true;
                List<ProcessingActivityBasicResponseDTO> defaultSubProcessingActivityList = new ArrayList<>();

                for (ProcessingActivityBasicResponseDTO subProcessingActivity : subProcessingActivities) {
                    if (subProcessingActivitiesIdsList.contains(subProcessingActivity.getId())) {
                        subProcessingActivity.setSelected(true);
                        defaultSelected = false;
                    } else if (defaultSelected) {
                        ProcessingActivityBasicResponseDTO defaultSubProcessingActivity = subProcessingActivity;
                        defaultSubProcessingActivity.setSelected(true);
                        defaultSubProcessingActivityList.add(defaultSubProcessingActivity);
                    }
                }

                if (defaultSelected) {
                    processingActivityBasicResponseDTO.setSubProcessingActivities(defaultSubProcessingActivityList);
                }
            }
        }
        return processingActivityResponseDTOList;
    }


}
