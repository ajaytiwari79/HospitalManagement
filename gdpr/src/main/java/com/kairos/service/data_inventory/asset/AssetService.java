package com.kairos.service.data_inventory.asset;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.TranslationInfo;
import com.kairos.dto.gdpr.data_inventory.AssetDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.enums.RiskSeverity;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.data_disposal.DataDisposalRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureRepository;
import com.kairos.persistence.repository.master_data.asset_management.storage_format.StorageFormatRepository;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureRepository;
import com.kairos.persistence.repository.risk_management.RiskRepository;
import com.kairos.response.dto.common.*;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import com.kairos.response.dto.data_inventory.RelatedProcessingActivityResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
import com.kairos.service.master_data.asset_management.MasterAssetService;
import com.kairos.service.risk_management.RiskService;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstant.IS_SUCCESS;
import static com.kairos.constants.GdprMessagesConstants.*;


@Service
public class AssetService {

    @Inject
    private AssetRepository assetRepository;

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
    private ProcessingActivityRepository processingActivityRepository;

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
    private OrganizationAssetTypeService organizationAssetTypeService;

    @Inject
    private Javers javers;


    public AssetDTO saveAsset(Long unitId, AssetDTO assetDTO,boolean copyOfMasterAssets) {
        Asset previousAsset = assetRepository.findByOrganizationIdAndDeletedAndName(unitId, assetDTO.getName());
        if (isNull(previousAsset)) {
            Asset asset = buildAsset(unitId, assetDTO);
            addAssetTypeAndSubAssetType(unitId, asset, assetDTO);
            assetRepository.save(asset);
            assetDTO.setId(asset.getId());
        }else if(!copyOfMasterAssets) {
            exceptionService.duplicateDataException(MESSAGE_DATANOTFOUND, MESSAGE_ASSET, assetDTO.getName());
        }
        return assetDTO;
    }


    private Asset buildAsset(Long unitId, AssetDTO assetDTO) {
        Asset asset;
        if (Optional.ofNullable(assetDTO.getId()).isPresent()) {
            asset = assetRepository.findByIdAndOrganizationIdAndDeletedFalse(assetDTO.getId(), unitId);
            if (!Optional.ofNullable(asset).isPresent()) {
                exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_ASSET, assetDTO.getId());
            }
        } else {
            asset = new Asset();
        }
        asset.setOrganizationId(unitId);
        asset.setName(assetDTO.getName());
        asset.setDescription(assetDTO.getDescription());
        setAllOptionalData(unitId, assetDTO, asset);
        return asset;
    }

    private void setAllOptionalData(Long unitId, AssetDTO assetDTO, Asset asset) {
        if (assetDTO.getHostingProvider() != null) {
            asset.setHostingProvider(hostingProviderRepository.findByIdAndOrganizationIdAndDeletedFalse(assetDTO.getHostingProvider(), unitId));
        }
        if (assetDTO.getHostingType() != null) {
            asset.setHostingType(hostingTypeRepository.findByIdAndOrganizationIdAndDeletedFalse(assetDTO.getHostingType(), unitId));
        }
        if (isCollectionNotEmpty(assetDTO.getOrgSecurityMeasures())) {
            asset.setOrgSecurityMeasures(organizationalSecurityMeasureRepository.findAllByIds(assetDTO.getOrgSecurityMeasures()));
        }
        if (isCollectionNotEmpty(assetDTO.getTechnicalSecurityMeasures())) {
            asset.setTechnicalSecurityMeasures(technicalSecurityMeasureRepository.findAllByIds(assetDTO.getTechnicalSecurityMeasures()));
        }
        if (isCollectionNotEmpty(assetDTO.getStorageFormats())) {
            asset.setStorageFormats(storageFormatRepository.findAllByIds(assetDTO.getStorageFormats()));
        }
        if (assetDTO.getDataDisposal() != null) {
            asset.setDataDisposal(dataDisposalRepository.findByIdAndOrganizationIdAndDeletedFalse(assetDTO.getDataDisposal(), unitId));
        }
        if (isNotNull(assetDTO.getDataRetentionPeriod())) {
            asset.setDataRetentionPeriod(assetDTO.getDataRetentionPeriod());
        }
        if (isNotNull(assetDTO.isSuggested())) {
            asset.setSuggested(assetDTO.isSuggested());
        }
        if (isNotNull(assetDTO.getManagingDepartment())) {
            asset.setManagingDepartment(new ManagingOrganization(assetDTO.getManagingDepartment().getManagingOrgId(), assetDTO.getManagingDepartment().getManagingOrgName()));
        }
        if (isNotNull(assetDTO.getAssetOwner())) {
            asset.setAssetOwner(new Staff(assetDTO.getAssetOwner().getStaffId(), assetDTO.getAssetOwner().getFirstName(), assetDTO.getAssetOwner().getLastName()));
        }
        if (isNotNull(assetDTO.getHostingLocation())) {
            asset.setHostingLocation(assetDTO.getHostingLocation());
        }
        if (isNotNull(assetDTO.getAssetAssessor())) {
            asset.setAssetAssessor(assetDTO.getAssetAssessor());
        }
    }


    private void addAssetTypeAndSubAssetType(Long unitId, Asset asset, AssetDTO assetDTO) {
        AssetType assetType;
        AssetType subAssetType = null;
        if (Optional.ofNullable(assetDTO.getAssetType().getId()).isPresent()) {
            assetType = assetTypeRepository.findByIdAndDeletedFalse(assetDTO.getAssetType().getId());
            if (assetType == null) {
                exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_ASSETTYPE, assetDTO.getAssetType().getId());
            }
            if (assetDTO.getSubAssetType() != null) {
                if (assetDTO.getSubAssetType().getId() == null) {
                    subAssetType = saveAssetType(unitId, assetDTO, assetType);
                    assetType.getSubAssetTypes().add(subAssetType);
                } else {
                    subAssetType = linkedRiskWithAllAssetTypeAndSubType(assetDTO, assetType, subAssetType);
                }
            }
        } else {
            assetType = getAssetType(unitId, assetDTO);
            if (assetDTO.getSubAssetType() != null) {
                subAssetType = new AssetType(assetDTO.getSubAssetType().getName(), unitId, true);
                subAssetType.setAssetType(assetType);
                assetType.setSubAssetTypes(Arrays.asList(subAssetType));
            }
        }
        if (isCollectionNotEmpty(assetDTO.getAssetType().getRisks())) {
            linkRiskWithAssetTypeAndSubType(assetType, assetDTO.getAssetType().getRisks());
        }
        assetTypeRepository.save(assetType);
        asset.setAssetType(assetType);
        asset.setSubAssetType(subAssetType);
    }

    private AssetType getAssetType(Long unitId, AssetDTO assetDTO) {
        AssetType previousAssetType = assetTypeRepository.findByNameAndOrganizationIdAndSubAssetType(assetDTO.getAssetType().getName(), unitId, false);
        if (Optional.ofNullable(previousAssetType).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_DATANOTFOUND, MESSAGE_ASSETTYPE, assetDTO.getAssetType().getName());
        }
        return new AssetType(assetDTO.getAssetType().getName(), unitId, false);
    }

    private AssetType saveAssetType(Long unitId, AssetDTO assetDTO, AssetType assetType) {
        AssetType subAssetType = new AssetType(assetDTO.getSubAssetType().getName(), unitId, true);
        if (isCollectionNotEmpty(assetDTO.getSubAssetType().getRisks())) {
            linkRiskWithAssetTypeAndSubType(subAssetType, assetDTO.getSubAssetType().getRisks());
        }
        subAssetType.setAssetType(assetType);
        assetTypeRepository.save(subAssetType);
        return subAssetType;
    }

    private AssetType linkedRiskWithAllAssetTypeAndSubType(AssetDTO assetDTO, AssetType assetType, AssetType subAssetType) {
        for (AssetType assetSubType : assetType.getSubAssetTypes()) {
            if (assetDTO.getSubAssetType().getId().equals(assetSubType.getId())) {
                subAssetType = assetSubType;
                if (isCollectionNotEmpty(assetDTO.getSubAssetType().getRisks())) {
                    linkRiskWithAssetTypeAndSubType(assetSubType, assetDTO.getSubAssetType().getRisks());
                }
                break;
            }
        }
        return subAssetType;
    }

    private AssetType linkRiskWithAssetTypeAndSubType(AssetType assetType, Set<OrganizationLevelRiskDTO> risks) {
        List<Risk> assetTypeRisks = new ArrayList<>();
        Map<Long, OrganizationLevelRiskDTO> riskIdMap = new HashMap<>();
        risks.forEach(risk -> {
            if (!Optional.ofNullable(risk.getId()).isPresent()) {
                Risk assetTypeRisk = ObjectMapperUtils.copyPropertiesByMapper(risk, Risk.class);
                assetTypeRisks.add(assetTypeRisk);
            } else {
                riskIdMap.put(risk.getId(), risk);
            }
        });
        if (riskIdMap.keySet().size() == assetType.getRisks().size()) {
            assetType.getRisks().forEach(risk -> {
                OrganizationLevelRiskDTO organizationLevelRiskDTO = riskIdMap.get(risk.getId());
                risk.setName(organizationLevelRiskDTO.getName());
                risk.setDescription(organizationLevelRiskDTO.getDescription());
                risk.setDaysToReminderBefore(organizationLevelRiskDTO.getDaysToReminderBefore());
                risk.setReminderActive(organizationLevelRiskDTO.isReminderActive());
                risk.setRiskRecommendation(organizationLevelRiskDTO.getRiskRecommendation());
                risk.setRiskLevel(organizationLevelRiskDTO.getRiskLevel());
            });
        } else {
            exceptionService.invalidRequestException(MESSAGE_RISK_IDS_SIZE_NOT_EQUAL_TO_PREVIOUS_RISK);
        }
        assetType.getRisks().addAll(assetTypeRisks);
        return assetType;

    }


    public Map<String, Object> deleteAssetById(Long unitId, Long assetId) {
        Asset asset = assetRepository.findByIdAndOrganizationIdAndDeletedFalse(assetId, unitId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_ASSET + assetId);
        }
        List<String> linkedProcessingActivities = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        if (!linkedProcessingActivities.isEmpty()) {
            result.put(IS_SUCCESS, false);
            result.put("data", linkedProcessingActivities);
            result.put("message", "Asset is linked with Processing Activities");
        } else {
            assetRepository.deleteByIdAndOrganizationId(asset.getId(), unitId);
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
    public boolean updateStatusOfAsset(Long unitId, Long assetId, boolean active) {
        Asset asset = assetRepository.findByIdAndOrganizationIdAndDeletedFalse(assetId, unitId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_ASSET, assetId);
        }
        asset.setActive(active);
        assetRepository.save(asset);
        return true;
    }


    /**
     * @param
     * @param unitId
     * @param id
     * @return method return Asset with Meta Data (storage format ,data Disposal, hosting type and etc)
     */
    public AssetResponseDTO getAssetWithRelatedDataAndRiskByUnitIdAndId(Long unitId, Long id) {
        Asset asset = assetRepository.findByIdAndOrganizationIdAndDeletedFalse(id, unitId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATANOTFOUND, MESSAGE_ASSET + id);
        }
        return prepareAssetResponseData(asset, null, false);
    }


    private AssetResponseDTO prepareAssetResponseData(Asset asset, Map<Long, List<RelatedProcessingActivityResponseDTO>> assetIdAndProcessingActivityListMap, boolean isBasicDataOnly) {
        AssetResponseDTO assetResponseDTO = new AssetResponseDTO();
        assetResponseDTO.setId(asset.getId());
        assetResponseDTO.setName(asset.getName());
        assetResponseDTO.setDescription(asset.getDescription());
        assetResponseDTO.setHostingLocation(asset.getHostingLocation());
        assetResponseDTO.setActive(asset.isActive());
        assetResponseDTO.setManagingDepartment(asset.getManagingDepartment());
        if (assetIdAndProcessingActivityListMap != null) {
            assetResponseDTO.setProcessingActivities(assetIdAndProcessingActivityListMap.get(asset.getId()));
        }
        if (!isBasicDataOnly) {
            setAllData(asset, assetResponseDTO);
        }
        return assetResponseDTO;
    }

    private void setAllData(Asset asset, AssetResponseDTO assetResponseDTO) {
        assetResponseDTO.setDataRetentionPeriod(asset.getDataRetentionPeriod());
        assetResponseDTO.setSuggested(asset.isSuggested());
        assetResponseDTO.setAssetOwner(asset.getAssetOwner());
        assetResponseDTO.setAssetAssessor(asset.getAssetAssessor());
        assetResponseDTO.setStorageFormats(ObjectMapperUtils.copyCollectionPropertiesByMapper(asset.getStorageFormats(), StorageFormatResponseDTO.class));
        assetResponseDTO.setOrgSecurityMeasures(ObjectMapperUtils.copyCollectionPropertiesByMapper(asset.getOrgSecurityMeasures(), OrganizationalSecurityMeasureResponseDTO.class));
        assetResponseDTO.setTechnicalSecurityMeasures(ObjectMapperUtils.copyCollectionPropertiesByMapper(asset.getTechnicalSecurityMeasures(), TechnicalSecurityMeasureResponseDTO.class));
        assetResponseDTO.setStorageFormats(ObjectMapperUtils.copyCollectionPropertiesByMapper(asset.getStorageFormats(), StorageFormatResponseDTO.class));
        assetResponseDTO.setHostingProvider(ObjectMapperUtils.copyPropertiesByMapper(asset.getHostingProvider(), HostingProviderResponseDTO.class));
        assetResponseDTO.setHostingType(ObjectMapperUtils.copyPropertiesByMapper(asset.getHostingType(), HostingTypeResponseDTO.class));
        assetResponseDTO.setDataDisposal(ObjectMapperUtils.copyPropertiesByMapper(asset.getDataDisposal(), DataDisposalResponseDTO.class));
        if (Optional.ofNullable(asset.getAssetType()).isPresent()) {
            assetResponseDTO.setAssetType(new AssetTypeBasicResponseDTO(asset.getAssetType().getId(), asset.getAssetType().getName(), asset.getAssetType().isSubAssetType(), isCollectionNotEmpty(asset.getAssetType().getRisks()) ? organizationAssetTypeService.buildAssetTypeRisksResponse(asset.getAssetType().getRisks()) : new ArrayList<>()));
        }
        if (Optional.ofNullable(asset.getSubAssetType()).isPresent()) {
            assetResponseDTO.setSubAssetType(new AssetTypeBasicResponseDTO(asset.getSubAssetType().getId(), asset.getSubAssetType().getName(), asset.getSubAssetType().isSubAssetType(), organizationAssetTypeService.buildAssetTypeRisksResponse(asset.getSubAssetType().getRisks())));
        }
    }

    /**
     * @param
     * @param unitId
     * @return return list Of Asset With Meta Data
     */
    public List<AssetResponseDTO> getAllAssetByUnitId(Long unitId) {
        List<AssetResponseDTO> assetResponseDTOS = new ArrayList<>();
        List<Asset> assets = assetRepository.findAllByOrganizationId(unitId);
        List<AssetBasicResponseDTO> assetsWithRelatedProcessingActivities = assetRepository.getAllAssetRelatedProcessingActivityByOrgId(unitId);
        Map<Long, List<RelatedProcessingActivityResponseDTO>> assetIdAndProcessingActivityListMap = new HashMap<>();
        assetsWithRelatedProcessingActivities.forEach(assetBasicResponseDTO -> {
            if (assetIdAndProcessingActivityListMap.containsKey(assetBasicResponseDTO.getId())) {
                List<RelatedProcessingActivityResponseDTO> relatedProcessingActivityResponseDTOS = assetIdAndProcessingActivityListMap.get(assetBasicResponseDTO.getId());
                relatedProcessingActivityResponseDTOS.add(assetBasicResponseDTO.getProcessingActivity());
                assetIdAndProcessingActivityListMap.put(assetBasicResponseDTO.getId(), relatedProcessingActivityResponseDTOS);
            } else {
                assetIdAndProcessingActivityListMap.put(assetBasicResponseDTO.getId(), new ArrayList<>(Arrays.asList(assetBasicResponseDTO.getProcessingActivity())));
            }
        });
        assets.forEach(asset -> assetResponseDTOS.add(prepareAssetResponseData(asset, assetIdAndProcessingActivityListMap, false)));
        return assetResponseDTOS;
    }


    /**
     * @param //assetId
     * @return
     * @description method return audit history of asset , old Object list and latest version also.
     * return object contain  changed field with key fields and values with key Values in return list of map
     */
    public List<Map<String, Object>> getAssetActivitiesHistory(Long assetId) throws ClassNotFoundException {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(assetId, Asset.class);
        List<CdoSnapshot> changes = javers.findSnapshots(jqlQuery.build());
        changes.sort((o1, o2) -> -1 * (int) o1.getVersion() - (int) o2.getVersion());
        return javersCommonService.getHistoryMap(changes, assetId, Asset.class);
    }

    public List<AssetResponseDTO> getAllActiveAsset(Long unitId) {
        List<Asset> activeAssets = assetRepository.findAllActiveAssetByOrganizationId(unitId);
        List<AssetResponseDTO> assetResponseDTOS = new ArrayList<>();
        activeAssets.forEach(asset -> assetResponseDTOS.add(prepareAssetResponseData(asset, null, true)));
        return assetResponseDTOS;
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
        assetDTO = saveAsset(unitId, assetDTO,false);
        AssetDTO masterAsset = masterAssetService.saveSuggestedAssetFromUnit(countryId, unitId, assetDTO);
        result.put("new", assetDTO);
        result.put("SuggestedData", masterAsset);
        return result;
    }

    /**
     * @return
     */
    public Map<String, Object> getAssetMetaData(Long unitId) {
        Map<String, Object> assetMetaDataMap = new HashMap<>();
        assetMetaDataMap.put("hostingTypeList", hostingTypeRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        assetMetaDataMap.put("hostingProviderList", hostingProviderRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        assetMetaDataMap.put("storageFormatList", storageFormatRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        assetMetaDataMap.put("dataDisposalList", dataDisposalRepository.findAllByUnitIdAndSortByCreatedDate(unitId));
        assetMetaDataMap.put("technicalSecurityMeasureList", technicalSecurityMeasureRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        assetMetaDataMap.put("organizationalSecurityMeasureList", organizationalSecurityMeasureRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        assetMetaDataMap.put("organizationAssetTypeList", organizationAssetTypeService.getAllAssetType(unitId));
        assetMetaDataMap.put("riskLevelList", RiskSeverity.values());
        return assetMetaDataMap;
    }

    public AssetDTO updateAssetData(Long unitId, Long assetId, AssetDTO assetDTO) {
        Asset asset = assetRepository.findByOrganizationIdAndDeletedAndName(unitId, assetDTO.getName());
        if (Optional.ofNullable(asset).isPresent() && !assetId.equals(asset.getId())) {
            exceptionService.duplicateDataException(MESSAGE_DUPLICATE, MESSAGE_ASSET, assetDTO.getName());
        }
        assetDTO.setId(assetId);
        asset = buildAsset(unitId,assetDTO);
        if (!asset.isActive()) {
            exceptionService.invalidRequestException(MESSAGE_ASSET_INACTIVE);
        }
        addAssetTypeAndSubAssetType(unitId, asset, assetDTO);
        assetRepository.save(asset);
        return assetDTO;
    }

    public Map<String,TranslationInfo> getTranslatedData(Long assetId) {
        Asset asset = assetRepository.findByIdAndDeletedFalse(assetId);
        return asset.getTranslations();
    }

    public TranslationInfo updateTranslation(Long assetId, TranslationInfo translationData) {
        Asset asset = assetRepository.findByIdAndDeletedFalse(assetId);
        asset.getTranslations().put(translationData.getName(),translationData);
        assetRepository.save(asset);
        return translationData;
    }
}
