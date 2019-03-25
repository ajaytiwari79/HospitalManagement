package com.kairos.service.data_inventory.asset;

import com.kairos.commons.utils.ObjectMapperUtils;
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
import com.kairos.service.master_data.asset_management.*;
import com.kairos.service.risk_management.RiskService;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static com.kairos.constants.AppConstant.IS_SUCCESS;


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


    public AssetDTO saveAsset(Long unitId, AssetDTO assetDTO) {
        Asset previousAsset = assetRepository.findByOrganizationIdAndDeletedAndName(unitId, assetDTO.getName());
        Optional.ofNullable(previousAsset).ifPresent(asset ->
                {
                    if (assetDTO.getId() == null || (assetDTO.getId() != null && !asset.getId().equals(assetDTO.getId()))) {
                        exceptionService.duplicateDataException("message.duplicate", "message.asset", assetDTO.getName());

                    }
                }
        );
        Asset asset = buildAsset(unitId, assetDTO);
        saveAssetTypeSubTypeAndRisk(unitId, asset, assetDTO);
        assetRepository.save(asset);
        assetDTO.setId(asset.getId());
        return assetDTO;
    }


    private Asset buildAsset(Long unitId, AssetDTO assetDTO) {

        Asset asset;
        if (Optional.ofNullable(assetDTO.getId()).isPresent()) {
            asset = assetRepository.findByIdAndOrganizationIdAndDeletedFalse(assetDTO.getId(), unitId);
            if (!Optional.ofNullable(asset).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.asset", assetDTO.getId());
            }
        } else
            asset = new Asset();
        asset.setOrganizationId(unitId);
        asset.setName(assetDTO.getName());
        asset.setDescription(assetDTO.getDescription());
        if (assetDTO.getHostingProvider() != null) {
            asset.setHostingProvider(hostingProviderRepository.findByIdAndOrganizationIdAndDeletedFalse(assetDTO.getHostingProvider(), unitId));
        }
        if (assetDTO.getHostingType() != null) {
            asset.setHostingType(hostingTypeRepository.findByIdAndOrganizationIdAndDeletedFalse(assetDTO.getHostingType(), unitId));
        }
        if (!assetDTO.getOrgSecurityMeasures().isEmpty()) {
            asset.setOrgSecurityMeasures(organizationalSecurityMeasureRepository.findAllByIds(assetDTO.getOrgSecurityMeasures()));
        }
        if (!assetDTO.getTechnicalSecurityMeasures().isEmpty()) {
            asset.setTechnicalSecurityMeasures(technicalSecurityMeasureRepository.findAllByIds(assetDTO.getTechnicalSecurityMeasures()));
        }
        if (!assetDTO.getStorageFormats().isEmpty()) {
            asset.setStorageFormats(storageFormatRepository.findAllByIds(assetDTO.getStorageFormats()));
        }
        if (assetDTO.getDataDisposal() != null) {
            asset.setDataDisposal(dataDisposalRepository.findByIdAndOrganizationIdAndDeletedFalse(assetDTO.getDataDisposal(), unitId));
        }
        asset.setDataRetentionPeriod(assetDTO.getDataRetentionPeriod());
        asset.setAssetAssessor(assetDTO.getAssetAssessor());
        asset.setSuggested(assetDTO.isSuggested());
        asset.setManagingDepartment(new ManagingOrganization(assetDTO.getManagingDepartment().getId(), assetDTO.getManagingDepartment().getName()));
        asset.setAssetOwner(new Staff(assetDTO.getAssetOwner().getStaffId(), assetDTO.getAssetOwner().getFirstName(), assetDTO.getAssetOwner().getLastName()));
        asset.setHostingLocation(assetDTO.getHostingLocation());
        asset.setAssetAssessor(assetDTO.getAssetAssessor());
        return asset;
    }


    private void saveAssetTypeSubTypeAndRisk(Long unitId, Asset asset, AssetDTO assetDTO) {
        AssetType assetType;
        AssetType subAssetType = null;
        if (Optional.ofNullable(assetDTO.getAssetType().getId()).isPresent()) {
            assetType = assetTypeRepository.findById(assetDTO.getAssetType().getId()).orElse(null);
            if (!Optional.ofNullable(assetType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", assetDTO.getAssetType().getId());
            }
            linkRiskWithAssetTypeAndSubType(assetType, assetDTO.getAssetType().getRisks());
            if (Optional.ofNullable(assetDTO.getAssetSubType()).isPresent()) {
                if (assetDTO.getAssetSubType().getId() != null) {
                    Optional<AssetType> subAssetTypeObj = assetType.getSubAssetTypes().stream().filter(assetSubType -> assetSubType.getId().equals(assetDTO.getAssetSubType().getId())).findAny();
                    if (!subAssetTypeObj.isPresent()) {
                        exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", assetDTO.getAssetSubType().getId());
                    }
                    subAssetType = subAssetTypeObj.get();
                } else {
                    subAssetType = new AssetType(assetDTO.getAssetSubType().getName());
                }
                linkRiskWithAssetTypeAndSubType(subAssetType, assetDTO.getAssetSubType().getRisks());
            }
        } else {
            AssetType previousAssetType = assetTypeRepository.findByNameAndOrganizationIdAndSubAssetType(assetDTO.getAssetType().getName(), unitId, false);
            if (Optional.ofNullable(previousAssetType).isPresent()) {
                exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetDTO.getAssetType().getName());
            }
            assetType = new AssetType(assetDTO.getAssetType().getName());
            assetType.setOrganizationId(unitId);
            linkRiskWithAssetTypeAndSubType(assetType, assetDTO.getAssetType().getRisks());
            if (Optional.ofNullable(assetDTO.getAssetSubType()).isPresent()) {
                subAssetType = new AssetType(assetDTO.getAssetSubType().getName());
                linkRiskWithAssetTypeAndSubType(subAssetType, assetDTO.getAssetSubType().getRisks());
            }
            if (Optional.ofNullable(subAssetType).isPresent()) {
                subAssetType.setOrganizationId(unitId);
                subAssetType.setSubAssetType(true);
                subAssetType.setAssetType(assetType);
                assetType.getSubAssetTypes().add(subAssetType);
            }

        }
        assetTypeRepository.save(assetType);
        asset.setAssetType(assetType);
        asset.setSubAssetType(subAssetType);
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
            exceptionService.invalidRequestException("message.risk.ids.size.not.equal.to.previous.risk");
        }
        assetType.getRisks().addAll(assetTypeRisks);
        return assetType;

    }


    public Map<String, Object> deleteAssetById(Long unitId, Long assetId) {
        Asset asset = assetRepository.findByIdAndOrganizationIdAndDeletedFalse(assetId, unitId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.asset" + assetId);
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
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.asset", assetId);
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
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.asset" + id);
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
            assetResponseDTO.setDataRetentionPeriod(asset.getDataRetentionPeriod());
            assetResponseDTO.setSuggested(asset.isSuggested());
            assetResponseDTO.setAssetOwner(asset.getAssetOwner());
            assetResponseDTO.setAssetAssessor(asset.getAssetAssessor());
            assetResponseDTO.setStorageFormats(ObjectMapperUtils.copyPropertiesOfListByMapper(asset.getStorageFormats(), StorageFormatResponseDTO.class));
            assetResponseDTO.setOrgSecurityMeasures(ObjectMapperUtils.copyPropertiesOfListByMapper(asset.getOrgSecurityMeasures(), OrganizationalSecurityMeasureResponseDTO.class));
            assetResponseDTO.setTechnicalSecurityMeasures(ObjectMapperUtils.copyPropertiesOfListByMapper(asset.getTechnicalSecurityMeasures(), TechnicalSecurityMeasureResponseDTO.class));
            assetResponseDTO.setStorageFormats(ObjectMapperUtils.copyPropertiesOfListByMapper(asset.getStorageFormats(), StorageFormatResponseDTO.class));
            assetResponseDTO.setHostingProvider(ObjectMapperUtils.copyPropertiesByMapper(asset.getHostingProvider(), HostingProviderResponseDTO.class));
            assetResponseDTO.setHostingType(ObjectMapperUtils.copyPropertiesByMapper(asset.getHostingType(), HostingTypeResponseDTO.class));
            assetResponseDTO.setDataDisposal(ObjectMapperUtils.copyPropertiesByMapper(asset.getDataDisposal(), DataDisposalResponseDTO.class));
            assetResponseDTO.setAssetType(new AssetTypeBasicResponseDTO(asset.getAssetType().getId(), asset.getAssetType().getName(), asset.getAssetType().isSubAssetType(), organizationAssetTypeService.buildAssetTypeRisksResponse(asset.getAssetType().getRisks())));
            if (Optional.ofNullable(asset.getSubAssetType()).isPresent()) {
                assetResponseDTO.setSubAssetType(new AssetTypeBasicResponseDTO(asset.getSubAssetType().getId(), asset.getSubAssetType().getName(), asset.getSubAssetType().isSubAssetType(), organizationAssetTypeService.buildAssetTypeRisksResponse(asset.getSubAssetType().getRisks())));
            }
        }
        return assetResponseDTO;


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
        assetDTO = saveAsset(unitId, assetDTO);
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


}
