package com.kairos.service.data_inventory.asset;

import com.kairos.dto.gdpr.data_inventory.AssetDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.enums.RiskSeverity;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.asset.AssetMD;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetTypeMD;
import com.kairos.persistence.model.risk_management.RiskMD;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentMongoRepository;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.data_disposal.DataDisposalRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureRepository;
import com.kairos.persistence.repository.master_data.asset_management.storage_format.StorageFormatRepository;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureRepository;
import com.kairos.persistence.repository.risk_management.RiskRepository;
import com.kairos.response.dto.common.AssessmentBasicResponseDTO;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
import com.kairos.service.master_data.asset_management.*;
import com.kairos.service.risk_management.RiskService;
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
    private AssetRepository assetRepository;

    @Inject
    private Javers javers;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private JaversCommonService javersCommonService;

    @Inject
    private RiskRepository riskRepository;

    @Inject
    private AssetTypeRepository assetTypeRepository;

    @Inject
    private HostingProviderRepository hostingProviderRepository;

    @Inject
    private HostingTypeRepository hostingTypeRepository;

    @Inject
    private TechnicalSecurityMeasureRepository technicalSecurityMeasureRepository;

    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;

    @Inject
    private ProcessingActivityRepository processingActivityRepository;

    @Inject
    private AssessmentMongoRepository assessmentMongoRepository;

    @Inject
    private OrganizationalSecurityMeasureRepository organizationalSecurityMeasureRepository;

    @Inject
    private StorageFormatRepository storageFormatRepository;

    @Inject
    private DataDisposalRepository dataDisposalRepository;

    @Inject
    private MasterAssetService masterAssetService;

    @Inject
    private RiskService riskService;
    @Inject
    private OrganizationHostingTypeService organizationHostingTypeService;
    @Inject
    private OrganizationHostingProviderService organizationHostingProviderService;
    @Inject
    private OrganizationStorageFormatService organizationStorageFormatService;
    @Inject
    private OrganizationDataDisposalService organizationDataDisposalService;
    @Inject
    private OrganizationTechnicalSecurityMeasureService organizationTechnicalSecurityMeasureService;
    @Inject
    private OrganizationOrganizationalSecurityMeasureService organizationOrganizationalSecurityMeasureService;
    @Inject
    private OrganizationAssetTypeService organizationAssetTypeService;


    public AssetDTO saveAsset(Long unitId, AssetDTO assetDTO) {
        AssetMD previousAsset = assetRepository.findByOrganizationIdAndDeletedAndName(unitId, false, assetDTO.getName());
        Optional.ofNullable(previousAsset).ifPresent(asset ->
                {
                    //TODO need refactor why these conditions
                    if (assetDTO.getId() == null || (assetDTO.getId() != null && !asset.getId().equals(assetDTO.getId()))) {
                        exceptionService.duplicateDataException("message.duplicate", "message.asset", assetDTO.getName());

                    }
                }
        );
        AssetMD asset = buildAsset(unitId, assetDTO);
        saveAssetTypeSubTypeAndRisk(unitId, asset, assetDTO);
        assetRepository.save(asset);
        assetDTO.setId(asset.getId());
        return assetDTO;
    }


    private AssetMD buildAsset(Long unitId, AssetDTO assetDTO) {

        AssetMD asset;
        if (Optional.ofNullable(assetDTO.getId()).isPresent())
            asset = assetRepository.getOne(assetDTO.getId());
        else
            asset = new AssetMD();
        asset.setOrganizationId(unitId);
        asset.setName(assetDTO.getName());
        asset.setDescription(assetDTO.getDescription());
        asset.setHostingProvider(hostingProviderRepository.findByIdAndOrganizationIdAndDeleted(assetDTO.getHostingProvider(), unitId, false));
        asset.setHostingType(hostingTypeRepository.findByIdAndOrganizationIdAndDeleted(assetDTO.getHostingType(), unitId, false));
        asset.setOrgSecurityMeasures(organizationalSecurityMeasureRepository.findAllByIds(assetDTO.getOrgSecurityMeasures()));
        asset.setTechnicalSecurityMeasures(technicalSecurityMeasureRepository.findAllByIds(assetDTO.getTechnicalSecurityMeasures()));
        asset.setStorageFormats(storageFormatRepository.findAllByIds(assetDTO.getStorageFormats()));
        asset.setDataDisposal(dataDisposalRepository.findByIdAndOrganizationIdAndDeleted(assetDTO.getDataDisposal(),unitId, false));
        asset.setDataRetentionPeriod(assetDTO.getDataRetentionPeriod());
        asset.setAssetAssessor(assetDTO.getAssetAssessor());
        asset.setSuggested(assetDTO.isSuggested());
        asset.setManagingDepartment(new ManagingOrganization(assetDTO.getManagingDepartment().getId(), assetDTO.getManagingDepartment().getName()));
        asset.setAssetOwner(new Staff(assetDTO.getAssetOwner().getId(), assetDTO.getAssetOwner().getFirstName(),assetDTO.getAssetOwner().getLastName()));
        asset.setHostingLocation(assetDTO.getHostingLocation());
        asset.setAssetAssessor(assetDTO.getAssetAssessor());
        //asset.setProcessingActivityIds(assetDTO.getProcessingActivityIds());
        //asset.setSubProcessingActivityIds(assetDTO.getSubProcessingActivityIds());
        return asset;
    }


    private void saveAssetTypeSubTypeAndRisk(Long unitId, AssetMD asset, AssetDTO assetDTO) {
        AssetTypeMD assetType;
        AssetTypeMD assetSubType;
        if (Optional.ofNullable(assetDTO.getAssetType().getId()).isPresent()) {
            assetType = assetTypeRepository.getOne(assetDTO.getAssetType().getId());
            assetType = linkRiskWithAssetTypeAndSubType(assetType,assetDTO.getAssetType().getRisks());
            if (Optional.ofNullable(assetDTO.getAssetSubType()).isPresent()) {
                if (assetDTO.getAssetSubType().getId() != null)
                    assetSubType = assetTypeRepository.getOne(assetDTO.getAssetSubType().getId());
                else
                    assetSubType = new AssetTypeMD(assetDTO.getAssetSubType().getName());
                assetSubType.setOrganizationId(unitId);
                assetSubType.setSubAssetType(true);
                assetSubType.setAssetType(assetType);
                assetSubType = linkRiskWithAssetTypeAndSubType(assetSubType, assetDTO.getAssetSubType().getRisks());
            }
        } else {
            AssetTypeMD previousAssetType = assetTypeRepository.findByNameAndOrganizationIdAndSubAssetType(assetDTO.getAssetType().getName(), unitId, false);
            if (Optional.ofNullable(previousAssetType).isPresent()) {
                exceptionService.duplicateDataException("message.duplicate", "message.asset", assetDTO.getName());
            }
            assetType = new AssetTypeMD(assetDTO.getAssetType().getName());
            assetType.setOrganizationId(unitId);
            assetType = linkRiskWithAssetTypeAndSubType(assetType, assetDTO.getAssetType().getRisks());
            if (Optional.ofNullable(assetDTO.getAssetSubType()).isPresent()) {
                assetSubType = new AssetTypeMD(assetDTO.getAssetSubType().getName());
                assetSubType.setOrganizationId(unitId);
                assetSubType.setSubAssetType(true);
                assetSubType.setAssetType(assetType);
                assetSubType = linkRiskWithAssetTypeAndSubType(assetSubType, assetDTO.getAssetSubType().getRisks());
            }

        }

        //TODO need to resolve
       /* Map<AssetType, List<Risk>> assetTypeMap = riskService.saveRiskAtCountryLevelOrOrganizationLevel(unitId, true, assetTypeRiskListMap);
        assetTypeMap.forEach((k, v) -> {
            if (CollectionUtils.isNotEmpty(v)) k.setRisks(v.stream().map(Risk::getId).collect(Collectors.toSet()));
            else k.setRisks(new HashSet<>());

        });*/

       //TODO
      /*  asset.setAssetSubTypeId(null);
        if (assetSubType != null) {
            assetType.getSubAssetTypes().add(assetTypeRepository.save(assetSubType).getId());
            asset.setAssetSubTypeId(assetSubType.getId());
        }
        */
        assetTypeRepository.save(assetType);
        asset.setAssetType(assetType);
        //asset.setSubAssetType(assetSubType);


    }

    private AssetTypeMD linkRiskWithAssetTypeAndSubType(AssetTypeMD assetType, List<OrganizationLevelRiskDTO> risks){
        List<RiskMD> assetTypeRisks = new ArrayList<>();
        risks.forEach( risk ->{
            if (!Optional.ofNullable(risk.getId()).isPresent()) {
                RiskMD assetTypeRisk = new RiskMD(risk.getName(), risk.getDescription(), risk.getRiskRecommendation(), risk.getRiskLevel());
                assetTypeRisk.setAssetType(assetType);
                assetTypeRisks.add(assetTypeRisk);
            }else{
                RiskMD  existingRisk = riskRepository.getOne(risk.getId());
                existingRisk.setName(risk.getName());
                existingRisk.setDescription(risk.getDescription());
                existingRisk.setRiskRecommendation(risk.getRiskRecommendation());
                existingRisk.setRiskLevel(risk.getRiskLevel());
                assetTypeRisks.add(existingRisk);
            }
        });
        assetType.getRisks().addAll(assetTypeRisks);
        return assetType;

    }


    public Map<String, Object> deleteAssetById(Long organizationId, Long assetId) {
        AssetMD asset = assetRepository.findByIdAndOrganizationIdAndDeleted( assetId, organizationId, false);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.asset" + assetId);
        }
        List<String> linkedProcessingActivities = new ArrayList<>();//processingActivityRepository.findAllProcessingActivityLinkWithAssetById(organizationId, assetId);
        Map<String, Object> result = new HashMap<>();
        if (!linkedProcessingActivities.isEmpty()) {
            result.put(IS_SUCCESS, false);
            result.put("data", linkedProcessingActivities);
            result.put("message", "Asset is linked with Processing Activities");
        } else {
            assetRepository.deleteByIdAndOrganizationId(asset.getId(),organizationId );
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
     * @param unitId
     * @param id
     * @return method return Asset with Meta Data (storage format ,data Disposal, hosting type and etc)
     */
    public AssetResponseDTO getAssetWithRelatedDataAndRiskByUnitIdAndId(Long unitId, Long id) {
        AssetResponseDTO asset = null; //assetMongoRepository.getAssetWithRiskAndRelatedProcessingActivitiesById(unitId, id);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset " + id);
        }
        if (!Optional.ofNullable(asset.getProcessingActivities().get(0).getId()).isPresent()) {
            asset.getProcessingActivities().clear();
        }
        return asset;
    }


    /**
     * @param
     * @param unitId
     * @return return list Of Asset With Meta Data
     */
    public List<AssetResponseDTO> getAllAssetByUnitId(Long unitId) {
       return assetMongoRepository.findAllByUnitId(unitId);
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
        assetDTO = saveAsset(unitId, assetDTO);
        AssetDTO masterAsset = masterAssetService.saveSuggestedAssetFromUnit(countryId, unitId, assetDTO);
        result.put("new", assetDTO);
        result.put("SuggestedData", masterAsset);
        return result;
    }

    /**
     *
     * @return
     */
    public Map<String, Object> getAssetMetaData(Long unitId){
        Map<String, Object> assetMetaDataMap=new HashMap<>();
        assetMetaDataMap.put("hostingTypeList",organizationHostingTypeService.getAllHostingType(unitId));
        assetMetaDataMap.put("hostingProviderList",organizationHostingProviderService.getAllHostingProvider(unitId));
        assetMetaDataMap.put("storageFormatList",organizationStorageFormatService.getAllStorageFormat(unitId));
        assetMetaDataMap.put("dataDisposalList",organizationDataDisposalService.getAllDataDisposal(unitId));
        assetMetaDataMap.put("technicalSecurityMeasureList",organizationTechnicalSecurityMeasureService.getAllTechnicalSecurityMeasure(unitId));
        assetMetaDataMap.put("organizationalSecurityMeasureList",organizationOrganizationalSecurityMeasureService.getAllOrganizationalSecurityMeasure(unitId));
        assetMetaDataMap.put("organizationAssetTypeList",organizationAssetTypeService.getAllAssetType(unitId));
        assetMetaDataMap.put("riskLevelList", RiskSeverity.values());
        return assetMetaDataMap;

    }


}
