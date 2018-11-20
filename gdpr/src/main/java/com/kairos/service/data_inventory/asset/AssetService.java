package com.kairos.service.data_inventory.asset;

import com.kairos.dto.gdpr.BasicRiskDTO;
import com.kairos.dto.gdpr.data_inventory.AssetDTO;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentMongoRepository;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.response.dto.common.AssessmentBasicResponseDTO;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
import com.kairos.service.master_data.asset_management.MasterAssetService;
import com.kairos.service.risk_management.RiskService;
import org.apache.commons.collections.CollectionUtils;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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
    private MasterAssetService masterAssetService;

    @Inject
    private RiskService riskService;


    public AssetDTO createAssetWithBasicDetail(Long unitId, AssetDTO assetDTO) {
        Asset previousAsset = assetMongoRepository.findByName(unitId, assetDTO.getName());
        Optional.ofNullable(previousAsset).ifPresent(asset ->
                {
                    if (assetDTO.getId() == null || (assetDTO.getId() != null && !asset.getId().equals(assetDTO.getId()))) {
                        exceptionService.duplicateDataException("message.duplicate", "message.asset", assetDTO.getName());
                    }
                }
        );
        Asset asset = buildAsset(unitId, assetDTO);
        saveAssetTypeSubTypeAndRisk(unitId, asset, assetDTO);
        assetMongoRepository.save(asset);
        assetDTO.setId(asset.getId());
        return assetDTO;
    }


    private Asset buildAsset(Long unitId, AssetDTO assetDTO) {

        Asset asset;
        if (Optional.ofNullable(assetDTO.getId()).isPresent())
            asset = assetMongoRepository.findOne(assetDTO.getId());
        else
            asset = new Asset();
        asset.setOrganizationId(unitId);
        asset.setName(assetDTO.getName());
        asset.setDescription(assetDTO.getDescription());
        asset.setHostingProviderId(assetDTO.getHostingProvider());
        asset.setHostingTypeId(assetDTO.getHostingType());
        asset.setOrgSecurityMeasures(assetDTO.getOrgSecurityMeasures());
        asset.setTechnicalSecurityMeasures(assetDTO.getTechnicalSecurityMeasures());
        asset.setStorageFormats(assetDTO.getStorageFormats());
        asset.setDataDisposalId(assetDTO.getDataDisposal());
        asset.setDataRetentionPeriod(assetDTO.getDataRetentionPeriod());
        asset.setAssetAssessor(assetDTO.getAssetAssessor());
        asset.setSuggested(assetDTO.isSuggested());
        asset.setManagingDepartment(assetDTO.getManagingDepartment());
        asset.setAssetOwner(assetDTO.getAssetOwner());
        asset.setHostingLocation(assetDTO.getHostingLocation());
        asset.setStorageFormats(assetDTO.getStorageFormats());
        asset.setAssetAssessor(assetDTO.getAssetAssessor());
        asset.setProcessingActivityIds(assetDTO.getProcessingActivityIds());
        asset.setSubProcessingActivityIds(assetDTO.getSubProcessingActivityIds());
        return asset;
    }


    private void saveAssetTypeSubTypeAndRisk(Long unitId, Asset asset, AssetDTO assetDTO) {

        Map<AssetType, List<BasicRiskDTO>> assetTypeRiskListMap = new HashMap<>();
        AssetType assetType;
        AssetType assetSubType = null;
        if (Optional.ofNullable(assetDTO.getAssetType().getId()).isPresent()) {
            assetType = assetTypeMongoRepository.findOne(assetDTO.getAssetType().getId());
            assetTypeRiskListMap.put(assetType, assetDTO.getAssetType().getRisks());
            if (Optional.ofNullable(assetDTO.getAssetSubType()).isPresent()) {

                if (assetDTO.getAssetSubType().getId() != null)
                    assetSubType = assetTypeMongoRepository.findOne(assetDTO.getAssetSubType().getId());
                else
                    assetSubType = new AssetType(assetDTO.getAssetSubType().getName());
                assetSubType.setOrganizationId(unitId);
                assetSubType.setSubAssetType(true);
                assetTypeRiskListMap.put(assetSubType, assetDTO.getAssetSubType().getRisks());
            }
        } else {
            AssetType previousAssetType = assetTypeMongoRepository.findByNameAndUnitId(unitId, assetDTO.getAssetType().getName());
            if (Optional.ofNullable(previousAssetType).isPresent()) {
                exceptionService.duplicateDataException("message.duplicate", "message.asset", assetDTO.getName());
            }
            assetType = new AssetType(assetDTO.getAssetType().getName());
            assetType.setOrganizationId(unitId);
            assetTypeRiskListMap.put(assetType, assetDTO.getAssetType().getRisks());
            if (Optional.ofNullable(assetDTO.getAssetSubType()).isPresent()) {
                assetSubType = new AssetType(assetDTO.getAssetSubType().getName());
                assetSubType.setOrganizationId(unitId);
                assetSubType.setSubAssetType(true);
                assetTypeRiskListMap.put(assetSubType, assetDTO.getAssetSubType().getRisks());
            }

        }
        Map<AssetType, List<Risk>> assetTypeMap = riskService.saveRiskAtCountryLevelOrOrganizationLevel(unitId, true, assetTypeRiskListMap);
        assetTypeMap.forEach((k, v) -> {
            if (CollectionUtils.isNotEmpty(v)) {
                k.setRisks(v.stream().map(Risk::getId).collect(Collectors.toSet()));
            }
        });
        if (assetSubType != null) {
            assetType.getSubAssetTypes().add(assetTypeMongoRepository.save(assetSubType).getId());
            asset.setAssetSubTypeId(assetSubType.getId());
        }
        assetTypeMongoRepository.save(assetType);

    }


    public Map<String, Object> deleteAssetById(Long organizationId, BigInteger assetId) {
        Asset asset = assetMongoRepository.findByIdAndNonDeleted(organizationId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.asset" + assetId);
        }
        List<ProcessingActivityBasicDTO> linkedProcessingActivities = processingActivityMongoRepository.findAllProcessingActivityLinkWithAssetById(organizationId, assetId);
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
        AssetResponseDTO asset = assetMongoRepository.getAssetWithRiskAndRelatedProcessingActivitiesById(organizationId, id);
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
        return assetMongoRepository.findAllByUnitId(organizationId);
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

    /*   *//**
     * @param
     * @param organizationId
     * @param assetId        - asset id
     * @param assetDTO       - asset dto contain meta data about asset
     * @return - updated Asset
     *//*
    public AssetDTO updateAssetData(Long organizationId, BigInteger assetId, AssetDTO assetDTO) {

        Asset asset = assetMongoRepository.findByName(organizationId, assetDTO.getName());
        if (Optional.ofNullable(asset).isPresent() && !assetId.equals(asset.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "Asset", assetDTO.getName());
        }
        asset = assetMongoRepository.findOne(assetId);
        if (!asset.isActive()) {
            exceptionService.invalidRequestException("message.asset.inactive");
        }
        AssetType assetType = assetTypeMongoRepository.findOne(assetDTO.getAssetTypeId());
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset  type", assetDTO.getAssetTypeId());
        }
        asset.setName(assetDTO.getName());
        asset.setDescription(assetDTO.getDescription());
        asset.setHostingProviderId(assetDTO.getHostingProvider());
        asset.setHostingTypeId(assetDTO.getHostingType());
        asset.setOrgSecurityMeasures(assetDTO.getOrgSecurityMeasures());
        asset.setTechnicalSecurityMeasures(assetDTO.getTechnicalSecurityMeasures());
        asset.setStorageFormats(assetDTO.getStorageFormats());
        asset.setDataDisposalId(assetDTO.getDataDisposal());
        asset.setDataRetentionPeriod(assetDTO.getDataRetentionPeriod());
        asset.setAssetAssessor(assetDTO.getAssetAssessor());
        asset.setSuggested(assetDTO.isSuggested());
        asset.setManagingDepartment(assetDTO.getManagingDepartment());
        asset.setAssetOwner(assetDTO.getAssetOwner());
        asset.setHostingLocation(assetDTO.getHostingLocation());
        asset.setStorageFormats(assetDTO.getStorageFormats());
        assetMongoRepository.save(asset);
        return assetDTO;
    }
*/

    /*  *//**
     * @param unitId
     * @param assetId
     * @param assetRelateProcessingActivityDTO
     * @return
     * @description map asset with Processing activity
     *//*
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
*/

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
        asset.getProcessingActivityIds().remove(processingActivityId);
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
        asset.getSubProcessingActivityIds().remove(subProcessingActivityId);
        assetMongoRepository.save(asset);
        return true;

    }


    /**
     * @param unitId
     * @param assetId
     * @return
     * @description get all Previous Assessment Launched for Asset
     */
    public List<AssessmentBasicResponseDTO> getAssessmentListByAssetId(Long unitId, BigInteger assetId) {
        return assessmentMongoRepository.findAllAssessmentLaunchedForAssetByAssetIdAndUnitId(unitId, assetId);
    }


    /**
     * @param unitId    -unit Id
     * @param countryId -country id
     * @param assetDTO
     * @return
     * @description create asset at unit level  and suggest asset to country admin
     */
    public Map<String, AssetDTO> saveAssetAndSuggestToCountryAdmin(Long unitId, Long countryId, AssetDTO assetDTO) {

        Map<String, AssetDTO> result = new HashMap<>();
        assetDTO = createAssetWithBasicDetail(unitId, assetDTO);
        AssetDTO masterAsset = masterAssetService.saveSuggestedAssetFromUnit(countryId, unitId, assetDTO);
        result.put("new", assetDTO);
        result.put("SuggestedData", masterAsset);
        return result;
    }


  /*  public List<ProcessingActivityBasicResponseDTO> getAllRelatedProcessingActivityAndSubProcessingActivities(Long unitId, BigInteger assetId) {

        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assetId);
        }
        Set<BigInteger> processingActivityIds = asset.getProcessingActivities();
        List<ProcessingActivityBasicResponseDTO> processingActivityResponseDTOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(processingActivityIds)) {
            processingActivityResponseDTOList = processingActivityMongoRepository.getAllAssetRelatedProcessingActivityWithSubProcessAndMetaData(unitId, processingActivityIds);
            Set<BigInteger> subProcessingActivitiesIdsList = asset.getSubProcessingActivities();

            for (ProcessingActivityBasicResponseDTO processingActivityBasicResponseDTO : processingActivityResponseDTOList) {

                List<ProcessingActivityBasicResponseDTO> subProcessingActivities = processingActivityBasicResponseDTO.getSubProcessingActivities();
                processingActivityBasicResponseDTO.setSubProcessingActivities(subProcessingActivities.stream().filter(subProcessingActivity -> subProcessingActivitiesIdsList.contains(subProcessingActivity.getId())).collect(Collectors.toList()));

            }
        }
        return processingActivityResponseDTOList;
    }
*/

}
