package com.kairos.service.data_inventory.asset;


import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.data_inventory.AssetTypeOrganizationLevelDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.dto.gdpr.metadata.AssetTypeBasicDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.risk_management.RiskRepository;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.AssetTypeService;
import com.kairos.service.risk_management.RiskService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrganizationAssetTypeService {


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeRepository assetTypeRepository;


    @Inject
    private RiskService riskService;

    @Inject
    private RiskRepository riskRepository;

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private AssetRepository assetRepository;


    /**
     * @param
     * @param unitId
     * @param assetTypeDto contain asset data ,and list of sub asset types
     * @return asset type object
     * @throws DuplicateDataException if asset type is already present with same name
     * @description method create Asset type if sub Asset Types if present then create and add sub Asset Types to Asset type.
     */
    public AssetTypeOrganizationLevelDTO createAssetTypeAndAddSubAssetTypes(Long unitId, AssetTypeOrganizationLevelDTO assetTypeDto) {


        AssetType previousAssetType = assetTypeRepository.findByNameAndOrganizationIdAndSubAssetType(assetTypeDto.getName(), unitId, false);
        if (Optional.ofNullable(previousAssetType).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetTypeDto.getName());
        }
        checkForDuplicacyInNameOfAssetType(assetTypeDto);
        AssetType assetType = new AssetType(assetTypeDto.getName(),  unitId, false);
        List<Risk> assetTypeRisks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assetTypeDto.getSubAssetTypes())) {
            List<AssetType> subAssetTypeList = buildSubAssetTypeListAndRiskAndLinkedToAssetType(unitId, assetTypeDto.getSubAssetTypes(), assetType);
            assetType.setHasSubAssetType(true);
            assetType.setSubAssetTypes(subAssetTypeList);
        }
        for (OrganizationLevelRiskDTO assetTypeRisk : assetTypeDto.getRisks()) {
            Risk risk = new Risk(assetTypeRisk.getName(), assetTypeRisk.getDescription(), assetTypeRisk.getRiskRecommendation(), assetTypeRisk.getRiskLevel());
            risk.setOrganizationId(unitId);
            assetTypeRisks.add(risk);
        }
        assetType.setRisks(assetTypeRisks);
        assetTypeRepository.save(assetType);
        assetTypeDto.setId(assetType.getId());
        return assetTypeDto;


    }


    private List<AssetType> buildSubAssetTypeListAndRiskAndLinkedToAssetType(Long unitId, List<AssetTypeOrganizationLevelDTO> subAssetTypesDto, AssetType assetType) {
        List<AssetType> subAssetTypes = new ArrayList<>();
        List<Risk> subAssetRisks = new ArrayList<>();
        for (AssetTypeOrganizationLevelDTO subAssetTypeDto : subAssetTypesDto) {
            AssetType subAssetType = new AssetType(subAssetTypeDto.getName(),  unitId, true);
            subAssetType.setAssetType(assetType);
            subAssetType.setHasSubAssetType(false);
            for (OrganizationLevelRiskDTO subAssetTypeRisk : subAssetTypeDto.getRisks()) {
                Risk risk = new Risk(subAssetTypeRisk.getName(), subAssetTypeRisk.getDescription(), subAssetTypeRisk.getRiskRecommendation(), subAssetTypeRisk.getRiskLevel());
                risk.setOrganizationId(unitId);
                subAssetRisks.add(risk);
            }
            subAssetType.setRisks(subAssetRisks);
            subAssetTypes.add(subAssetType);
        }
        return subAssetTypes;

    }


    /**
     * THis method is used to build response of asset type and asset sub type. This method used recursion
     * to prepare the data of asset sub type.
     *
     * @param assetType - It may be asset type or asset sub type.
     * @return List<AssetTypeResponseDTO> - List of asset-type or Sub asset-type response DTO.
     */
    private AssetTypeResponseDTO buildAssetTypeOrSubTypeResponseData(AssetType assetType) {
        List<AssetTypeResponseDTO> subAssetTypeData = new ArrayList<>();
        AssetTypeResponseDTO assetTypeRiskResponseDTO = new AssetTypeResponseDTO();
        assetTypeRiskResponseDTO.setId(assetType.getId());
        assetTypeRiskResponseDTO.setName(assetType.getName());
        assetTypeRiskResponseDTO.setHasSubAsset(assetType.isHasSubAssetType());
        if (!assetType.getRisks().isEmpty()) {
            assetTypeRiskResponseDTO.setRisks(buildAssetTypeRisksResponse(assetType.getRisks()));
        }
        if (CollectionUtils.isNotEmpty(assetType.getSubAssetTypes())) {
            assetType.getSubAssetTypes().forEach(subAssetType -> subAssetTypeData.add(buildAssetTypeOrSubTypeResponseData(subAssetType)));
            assetTypeRiskResponseDTO.setSubAssetTypes(subAssetTypeData);
        }
        return assetTypeRiskResponseDTO;
    }

    /**
     * Description : This method is used to convert Risks of asset-type or Sub asset-type to Risk Response DTO
     * Convert Risk into OrganizationLevelRiskDTO
     *
     * @param risks - Risks of asset-type or Sub asset-type
     * @return List<OrganizationLevelRiskDTO> - List of RiskResponse DTO.
     */
    public List<OrganizationLevelRiskDTO> buildAssetTypeRisksResponse(List<Risk> risks) {
        return ObjectMapperUtils.copyPropertiesOfListByMapper(risks, OrganizationLevelRiskDTO.class);
    }


    /**
     * @param
     * @param unitId
     * @return return list of Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public List<AssetTypeResponseDTO> getAllAssetType(Long unitId) {
        List<AssetType> assetTypes = assetTypeRepository.getAllAssetTypesByOrganization(unitId);
        List<AssetTypeResponseDTO> assetTypesWithAllData = new ArrayList<>();
        for (AssetType assetType : assetTypes) {
            assetTypesWithAllData.add(buildAssetTypeOrSubTypeResponseData(assetType));
        }
        return assetTypesWithAllData;
    }


    /**
     * @param
     * @param unitId
     * @return return Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public AssetTypeResponseDTO getAssetTypeById(Long unitId, Long id) {
        AssetType assetType = assetTypeRepository.findByIdAndOrganizationIdAndDeleted(id, unitId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", id);
        }

        return buildAssetTypeOrSubTypeResponseData(assetType);

    }

    /**
     * @param
     * @param unitId
     * @param assetTypeId  id of Asset Type to which Sub Asset Types Link.
     * @param assetTypeDto asset type Dto contain list of Existing sub Asset types which need to be update and New SubAsset Types  which we need to create and add to asset afterward.
     * @return Asset Type with updated Sub Asset and new Sub Asset Types
     * @throws DuplicateDataException if Asset type is already present with same name .
     * @description method simply (update already exit Sub asset types if id is present)and (add create new sub asset types if id is not present in sub asset types)
     */
    public AssetTypeOrganizationLevelDTO updateAssetTypeAndSubAssetsAndAddRisks(Long unitId, Long assetTypeId, AssetTypeOrganizationLevelDTO assetTypeDto) {

        AssetType assetType = assetTypeRepository.findByNameAndOrganizationIdAndSubAssetType(assetTypeDto.getName(), unitId, false);
        if (Optional.ofNullable(assetType).isPresent() && !assetTypeId.equals(assetType.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetTypeDto.getName());
        }
        checkForDuplicacyInNameOfAssetType(assetTypeDto);
        assetType = assetTypeRepository.findByIdAndOrganizationIdAndDeleted(assetTypeId, unitId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset type", assetTypeId);
        }
        assetType.setName(assetTypeDto.getName());
        assetType.setOrganizationId(unitId);
        updateAssetTypeAndSubAssetTypeRisk(unitId, assetType, assetTypeDto);
        assetTypeRepository.save(assetType);
        return assetTypeDto;

    }

    private void updateAssetTypeAndSubAssetTypeRisk(Long unitId, AssetType assetType, AssetTypeOrganizationLevelDTO assetTypeDto) {
        updateRiskOfAssetType(unitId, assetType, assetTypeDto);
        checkForDuplicacyInNameOfAssetType(assetTypeDto);
        if (CollectionUtils.isNotEmpty(assetTypeDto.getSubAssetTypes())) {
            Map<Long, AssetType> longAssetTypeMap = new HashMap<>();
            assetType.getSubAssetTypes().forEach(assetSubType -> longAssetTypeMap.put(assetSubType.getId(), assetSubType));
            assetType.setHasSubAssetType(true);
            assetType.setSubAssetTypes(
                    assetTypeDto.getSubAssetTypes().stream().map(assetSubTypeDTO -> {
                        AssetType assetSubType;
                        if (Optional.ofNullable(assetSubTypeDTO.getId()).isPresent()) {
                            assetSubType = longAssetTypeMap.get(assetSubTypeDTO.getId());
                        } else {
                            assetSubType = new AssetType();
                        }
                        assetSubType.setSubAssetType(true);
                        assetSubType.setAssetType(assetType);
                        assetSubType.setName(assetSubTypeDTO.getName());
                        assetSubType.setOrganizationId(unitId);
                        updateRiskOfAssetType(unitId, assetSubType, assetSubTypeDTO);
                        return assetSubType;
                    }).collect(Collectors.toList())
            );
        }

    }


    private void updateRiskOfAssetType(Long unitId, AssetType assetType, AssetTypeOrganizationLevelDTO assetTypeDto) {
        if (CollectionUtils.isNotEmpty(assetTypeDto.getRisks())) {
            Map<Long, Risk> longOrganizationLevelRiskDTOMap = new HashMap<>();
            assetType.getRisks().forEach(organizationLevelRisk -> longOrganizationLevelRiskDTOMap.put(organizationLevelRisk.getId(), organizationLevelRisk));
            assetType.setRisks(assetTypeDto.getRisks().stream().map(organizationLevelRiskDTO -> {
                Risk risk;
                if (Optional.ofNullable(organizationLevelRiskDTO.getId()).isPresent()) {
                    risk = longOrganizationLevelRiskDTOMap.get(organizationLevelRiskDTO.getId());
                } else {
                    risk = new Risk();
                }
                risk.setOrganizationId(unitId);
                risk.setName(organizationLevelRiskDTO.getName());
                risk.setDescription(organizationLevelRiskDTO.getDescription());
                risk.setRiskRecommendation(organizationLevelRiskDTO.getRiskRecommendation());
                risk.setRiskLevel(organizationLevelRiskDTO.getRiskLevel());
                return risk;
            }).collect(Collectors.toList()));
        }
    }


    /**
     * @param unitId
     * @param assetTypeId
     * @return
     */
    public boolean deleteAssetTypeById(Long unitId, Long assetTypeId) {

        List<String> assetsLinkedWithAssetType = assetRepository.findAllAssetLinkedWithAssetType(unitId, assetTypeId);
        if (CollectionUtils.isNotEmpty(assetsLinkedWithAssetType)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "message.assetType", StringUtils.join(assetsLinkedWithAssetType, ','));
        }
        AssetType assetType = assetTypeRepository.findByIdAndOrganizationIdAndDeleted(assetTypeId, unitId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", assetTypeId);
        }
        assetType.delete();
        assetTypeRepository.save(assetType);
        return true;

    }


    public boolean deleteAssetSubTypeById(Long unitId, Long assetTypeId, Long subAssetTypeId) {
        List<String> assetsLinkedWithAssetSubType = assetRepository.findAllAssetLinkedWithAssetType(unitId, subAssetTypeId);
        if (CollectionUtils.isNotEmpty(assetsLinkedWithAssetSubType)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "message.subAssetType", StringUtils.join(assetsLinkedWithAssetSubType, ','));
        }
        AssetType subAssetType = assetTypeRepository.findByIdAndOrganizationIdAndAssetTypeAndDeleted(subAssetTypeId, assetTypeId, unitId);
        if (!Optional.ofNullable(subAssetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.subAssetType", subAssetType);
        }

        subAssetType.delete();
        subAssetType.getAssetType().getSubAssetTypes().remove(subAssetType);
        assetTypeRepository.save(subAssetType);
        return true;

    }


    /**
     * @param unitId
     * @param assetTypeId
     * @param riskId      - risk id link with asset type and Sub Asset type
     * @return
     * @description - Remove risk id from asset type and soft deleted risk
     */
    public boolean unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(Long unitId, Long assetTypeId, Long riskId) {
        //TODO
        /*Integer updateCount = riskRepository.unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(riskId, assetTypeId,unitId);
        if (updateCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", assetTypeId);
        }else{
            LOGGER.info("Data updated successfully.");
        }*/

        return true;
    }


    /**
     * @param countryId
     * @param unitId
     * @param assetTypeBasicDTO
     * @return
     */
    public Map<String, AssetTypeBasicDTO> saveAndSuggestAssetTypeAndSubAssetTypeToCountryAdmin(Long unitId, Long
            countryId, AssetTypeBasicDTO assetTypeBasicDTO) {

        Map<String, AssetTypeBasicDTO> result = createAssetTypeAndSubAssetTypeWithBasicDetail(unitId, assetTypeBasicDTO);
        AssetTypeBasicDTO acceptedByCountryAdmin = assetTypeService.saveSuggestedAssetTypeAndSubAssetTypeFromUnit(countryId, assetTypeBasicDTO);
        result.put("SuggestedData", acceptedByCountryAdmin);
        return result;
    }


    /**
     * @param unitId
     * @param assetTypeBasicDTO
     * @return
     * @description create sub Asset tYpe and Asset type with basic detail on Save and Suggest of data from unit to country admin
     */
    @SuppressWarnings("unchecked")
    private Map<String, AssetTypeBasicDTO> createAssetTypeAndSubAssetTypeWithBasicDetail(Long unitId, AssetTypeBasicDTO assetTypeBasicDTO) {

        AssetType assetType = new AssetType(assetTypeBasicDTO.getName(),unitId,false);
        List<AssetType> subAssetTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assetTypeBasicDTO.getSubAssetTypes())) {
            for (AssetTypeBasicDTO subAssetTypeDTO : assetTypeBasicDTO.getSubAssetTypes()) {
                AssetType subAssetType = new AssetType(subAssetTypeDTO.getName(),unitId,true);
                subAssetType.setAssetType(assetType);
                subAssetType.setSuggestedDate(LocalDate.now());
                assetType.setHasSubAssetType(true);
                subAssetTypes.add(subAssetType);
            }
        }
        if (!subAssetTypes.isEmpty()) {
            assetType.setSubAssetTypes(subAssetTypes);
        }
        assetTypeRepository.save(assetType);
        assetTypeBasicDTO.setId(assetType.getId());
        return (Map<String, AssetTypeBasicDTO>) new HashMap<>().put("new", assetTypeBasicDTO);

    }


    /**
     * @param assetTypeDTO check for duplicates in name of Asset types
     */
    private void checkForDuplicacyInNameOfAssetType(AssetTypeOrganizationLevelDTO assetTypeDTO) {
        List<String> names = new ArrayList<>();
        names.add(assetTypeDTO.getName().toLowerCase());
        for (AssetTypeOrganizationLevelDTO assetSubType : assetTypeDTO.getSubAssetTypes()) {
            if (names.contains(assetSubType.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetSubType.getName());
            }
            names.add(assetSubType.getName().toLowerCase());
        }
    }


}
