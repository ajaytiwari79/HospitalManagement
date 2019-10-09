package com.kairos.service.master_data.asset_management;


import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.BasicRiskDTO;
import com.kairos.dto.gdpr.master_data.AssetTypeDTO;
import com.kairos.dto.gdpr.metadata.AssetTypeBasicDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.MasterAssetRepository;
import com.kairos.response.dto.common.RiskBasicResponseDTO;
import com.kairos.response.dto.master_data.AssetTypeRiskResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.risk_management.RiskService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AssetTypeService {

    public static final String MESSAGE_DUPLICATE = "message.duplicate";
    public static final String MESSAGE_ASSET_TYPE = "message.assetType";
    public static final String MESSAGE_DATA_NOT_FOUND = "message.dataNotFound";
    @Inject
    private ExceptionService exceptionService;

    @Inject
    private RiskService riskService;

    @Inject
    private AssetTypeRepository assetTypeRepository;

    @Inject
    private MasterAssetRepository masterAssetRepository;


    /**
     * @param countryId
     * @param
     * @param assetTypeDto contain asset data ,and list of sub asset types
     * @return asset type object
     * @throws DuplicateDataException if asset type is already present with same name
     * @description method create Asset type if sub Asset Types if present then create and add sub Asset Types to Asset type.
     */
    public AssetTypeDTO createAssetTypeAndAddSubAssetTypes(Long countryId, AssetTypeDTO assetTypeDto) {


        AssetType assetTypeExist = assetTypeRepository.findByNameAndCountryIdAndSubAssetType(assetTypeDto.getName(), countryId, false);
        if (Optional.ofNullable(assetTypeExist).isPresent()) {
            exceptionService.duplicateDataException(MESSAGE_DUPLICATE, MESSAGE_ASSET_TYPE, assetTypeDto.getName());
        }
        AssetType assetType = new AssetType(assetTypeDto.getName(), countryId, SuggestedDataStatus.APPROVED);
        List<Risk> assetTypeRisks;
        if (!assetTypeDto.getSubAssetTypes().isEmpty()) {
            List<AssetType> subAssetTypeList = buildSubAssetTypesListAndRiskAndLinkedToAssetType(countryId, assetTypeDto.getSubAssetTypes(), assetType);
            assetType.setHasSubAssetType(true);
            assetType.setSubAssetTypes(subAssetTypeList);
        }
        assetTypeRisks = ObjectMapperUtils.copyPropertiesOfListByMapper(assetTypeDto.getRisks(), Risk.class);
        assetTypeRisks.forEach(risk -> risk.setCountryId(countryId));
        assetType.setRisks(assetTypeRisks);
        assetTypeRepository.save(assetType);
        assetTypeDto.setId(assetType.getId());
        return assetTypeDto;
    }

    /**
     * @param countryId
     * @param subAssetTypesDto contain list of sub Asset DTOs
     * @return create new Sub Asset type ids
     */
    private List<AssetType> buildSubAssetTypesListAndRiskAndLinkedToAssetType(Long countryId, List<AssetTypeDTO> subAssetTypesDto, AssetType assetType) {

        checkForDuplicacyInNameOfAssetType(subAssetTypesDto);
        List<AssetType> subAssetTypes = new ArrayList<>();
        List<Risk> subAssetRisks;
        for (AssetTypeDTO subAssetTypeDto : subAssetTypesDto) {
            AssetType subAssetType = new AssetType(subAssetTypeDto.getName(), countryId, SuggestedDataStatus.APPROVED);
            subAssetType.setSubAssetType(true);
            subAssetType.setAssetType(assetType);
            subAssetRisks = ObjectMapperUtils.copyPropertiesOfListByMapper(subAssetTypeDto.getRisks(), Risk.class);
            subAssetRisks.forEach(risk -> risk.setCountryId(countryId));
            subAssetType.setRisks(subAssetRisks);
            subAssetTypes.add(subAssetType);
        }
        return subAssetTypes;
    }


    /**
     * @param countryId
     * @param subAssetTypesDtos contain list of Existing Sub Asset type which need to we update
     * @return map of Sub asset Types List and Ids (List for rollback)
     * @description this method update existing Sub asset Types and return list of Sub Asset Types and  ids list
     */
    private List<AssetType> updateSubAssetTypes(Long countryId, List<AssetTypeDTO> subAssetTypesDtos, AssetType assetType) {
        checkForDuplicacyInNameOfAssetType(subAssetTypesDtos);
        Map<Long, AssetType> subAssetTypesMap = assetType.getSubAssetTypes().stream().collect(Collectors.toMap(BaseEntity::getId, v -> v));
        List<AssetType> subAssetTypes = new ArrayList<>();
        subAssetTypesDtos.forEach(subAssetTypeDTO ->
                {
                    List<Risk> subAssetRisks;
                    AssetType subAssetType;
                    if (subAssetTypeDTO.getId() != null) {
                        subAssetType = subAssetTypesMap.get(subAssetTypeDTO.getId());
                    } else {
                        subAssetType = new AssetType(countryId, SuggestedDataStatus.APPROVED);
                        subAssetType.setAssetType(assetType);
                    }
                    subAssetType.setSubAssetType(true);
                    subAssetType.setName(subAssetTypeDTO.getName());
                    subAssetRisks = ObjectMapperUtils.copyPropertiesOfListByMapper(subAssetTypeDTO.getRisks(), Risk.class);
                    subAssetRisks.forEach(risk -> risk.setCountryId(countryId));
                    subAssetType.setRisks(subAssetRisks);
                    subAssetTypes.add(subAssetType);
                }
        );
        return subAssetTypes;
    }


    /**
     * @param countryId
     * @param
     * @return return list of Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public List<AssetTypeRiskResponseDTO> getAllAssetTypeWithSubAssetTypeAndRisk(Long countryId) {
        List<AssetType> assetTypes = assetTypeRepository.getAllAssetTypeByCountryId(countryId);
        List<AssetTypeRiskResponseDTO> assetTypesWithAllData = new ArrayList<>();
        for (AssetType assetType : assetTypes) {
            assetTypesWithAllData.add(buildAssetTypeOrSubTypeResponseData(assetType));
        }
        return assetTypesWithAllData;
    }


    public List<AssetType> prepareAssetTypeDataForUnitLevel(List<AssetType> assetTypes, Long unitId, boolean isSubAssetType, AssetType parentAssetType) {
        List<AssetType> unitLevelAssetTypes = new ArrayList<>();
        assetTypes.forEach(assetType -> {
            AssetType unitLevelAssetType = new AssetType();
            unitLevelAssetType.setName(assetType.getName());
            unitLevelAssetType.setHasSubAssetType(assetType.isHasSubAssetType());
            unitLevelAssetType.setSubAssetType(assetType.isSubAssetType());
            unitLevelAssetType.setOrganizationId(unitId);
            unitLevelAssetType.setCountryId(null);
            List<Risk> unitLevelAssetTypeRiskList = new ArrayList<>();
            assetType.getRisks().forEach(risk -> {
                Risk unitLevelAssetTypeRisk = new Risk(risk.getName(), risk.getDescription(), risk.getRiskRecommendation(), risk.getRiskLevel());
                unitLevelAssetTypeRisk.setOrganizationId(unitId);
                unitLevelAssetTypeRiskList.add(unitLevelAssetTypeRisk);
            });
            unitLevelAssetType.setRisks(unitLevelAssetTypeRiskList);
            if (!isSubAssetType) {
                unitLevelAssetType.setSubAssetTypes(prepareAssetTypeDataForUnitLevel(assetType.getSubAssetTypes(), unitId, true, assetType));
                if (!unitLevelAssetType.getSubAssetTypes().isEmpty()) {
                    unitLevelAssetType.setHasSubAssetType(true);
                }
            }
            if (isSubAssetType) {
                unitLevelAssetType.setAssetType(parentAssetType);
            }
            unitLevelAssetTypes.add(unitLevelAssetType);
        });
        return unitLevelAssetTypes;
    }

    public AssetType prepareAssetTypeDataForUnitLevel(AssetType assetType, Long unitId, boolean isSubAssetType, AssetType parentAssetType) {
        AssetType unitLevelAssetType = new AssetType();
        unitLevelAssetType.setName(assetType.getName());
        unitLevelAssetType.setSubAssetType(assetType.isSubAssetType());
        unitLevelAssetType.setOrganizationId(unitId);
        unitLevelAssetType.setCountryId(null);
        List<Risk> unitLevelAssetTypeRiskList = new ArrayList<>();
        assetType.getRisks().forEach(risk -> {
            Risk unitLevelAssetTypeRisk = new Risk(risk.getName(), risk.getDescription(), risk.getRiskRecommendation(), risk.getRiskLevel());
            unitLevelAssetTypeRisk.setOrganizationId(unitId);
            unitLevelAssetTypeRiskList.add(unitLevelAssetTypeRisk);
        });
        unitLevelAssetType.setRisks(unitLevelAssetTypeRiskList);
        if (!isSubAssetType) {
            unitLevelAssetType.setSubAssetTypes(prepareAssetTypeDataForUnitLevel(assetType.getSubAssetTypes(), unitId, true, unitLevelAssetType));
            if (!unitLevelAssetType.getSubAssetTypes().isEmpty()) {
                unitLevelAssetType.setHasSubAssetType(true);
            }
        }
        if (isSubAssetType) {
            unitLevelAssetType.setAssetType(parentAssetType);
        }

        return unitLevelAssetType;
    }

    /**
     * THis method is used to build response of asset type and asset sub type. This method used recursion
     * to prepare the data of asset sub type.
     *
     * @param assetType - It may be asset type or asset sub type.
     * @return List<AssetTypeRiskResponseDTO> - List of asset-type or Sub asset-type response DTO.
     */
    private AssetTypeRiskResponseDTO buildAssetTypeOrSubTypeResponseData(AssetType assetType) {
        List<AssetTypeRiskResponseDTO> subAssetTypeData = new ArrayList<>();
        AssetTypeRiskResponseDTO assetTypeRiskResponseDTO = new AssetTypeRiskResponseDTO(assetType.getId(), assetType.getName());
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
     * Convert Risk into RiskBasicResponseDTO
     *
     * @param risks - Risks of asset-type or Sub asset-type
     * @return List<RiskBasicResponseDTO> - List of RiskResponse DTO.
     */
    private List<RiskBasicResponseDTO> buildAssetTypeRisksResponse(List<Risk> risks) {
        return ObjectMapperUtils.copyPropertiesOfListByMapper(risks, RiskBasicResponseDTO.class);
    }

    /**
     * @param countryId
     * @param
     * @return return Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public AssetTypeRiskResponseDTO getAssetTypeById(Long countryId, Long id) {
        AssetType assetType = assetTypeRepository.findByIdAndCountryIdAndDeleted(id, countryId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATA_NOT_FOUND, MESSAGE_ASSET_TYPE, id);
        }
        return buildAssetTypeOrSubTypeResponseData(assetType);

    }


    public Boolean deleteAssetType(Long countryId, Long assetTypeId) {

        List<String> masterAssetsLinkedWithAssetType = masterAssetRepository.findMasterAssetsLinkedWithAssetType(countryId, assetTypeId);
        if (CollectionUtils.isNotEmpty(masterAssetsLinkedWithAssetType)) {
            exceptionService.invalidRequestException("message.metaData.linked.with.asset", MESSAGE_ASSET_TYPE, StringUtils.join(masterAssetsLinkedWithAssetType, ','));
        }
        AssetType assetType = assetTypeRepository.findByCountryIdAndId(countryId, assetTypeId, false);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATA_NOT_FOUND, MESSAGE_ASSET_TYPE, assetTypeId);
        }
        assetType.delete();
        assetTypeRepository.save(assetType);
        return true;

    }


    /**
     * @param countryId
     * @param
     * @param assetTypeId  id of Asset Type to which Sub Asset Types Link.
     * @param assetTypeDto asset type Dto contain list of Existing sub Asset types which need to be update and New SubAsset Types  which we need to create and add to asset afterward.
     * @return Asset Type with updated Sub Asset and new Sub Asset Types
     * @throws DuplicateDataException if Asset type is already present with same name .
     * @description method simply (update already exit Sub asset types if id is present)and (add create new sub asset types if id is not present in sub asset types)
     */
    public AssetTypeDTO updateAssetTypeUpdateAndCreateNewSubAssetsAndAddToAssetType(Long countryId, Long assetTypeId, AssetTypeDTO assetTypeDto) {
        AssetType assetType = assetTypeRepository.findByNameAndCountryIdAndSubAssetType(assetTypeDto.getName(), countryId, false);
        if (Optional.ofNullable(assetType).isPresent() && !assetTypeId.equals(assetType.getId())) {
            exceptionService.duplicateDataException(MESSAGE_DUPLICATE, MESSAGE_ASSET_TYPE, assetTypeDto.getName());
        }
        assetType = assetTypeRepository.findByCountryIdAndId(countryId, assetTypeId, false);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException(MESSAGE_DATA_NOT_FOUND, MESSAGE_ASSET_TYPE, assetTypeId);
        }
        assetType.setName(assetTypeDto.getName());
        List<AssetType> subAssetTypeList = updateSubAssetTypes(countryId, assetTypeDto.getSubAssetTypes(), assetType);
        if (!subAssetTypeList.isEmpty()) {
            assetType.setSubAssetTypes(subAssetTypeList);
            assetType.setHasSubAssetType(true);
        }
        updateOrAddAssetTypeRisk(assetType, assetTypeDto);
        assetType.getRisks().forEach(risk -> risk.setCountryId(countryId));
        assetTypeRepository.save(assetType);
        return assetTypeDto;

    }

    private AssetType updateOrAddAssetTypeRisk(AssetType assetType, AssetTypeDTO assetTypeDto) {
        List<BasicRiskDTO> newRisks = new ArrayList<>();
        List<Risk> assetTypeRisks = assetType.getRisks();
        Map<Long, BasicRiskDTO> existingRiskDtoCorrespondingToIds = new HashMap<>();
        assetTypeDto.getRisks().forEach(assetTypeRiskDto -> {
            if (Optional.ofNullable(assetTypeRiskDto.getId()).isPresent()) {
                existingRiskDtoCorrespondingToIds.put(assetTypeRiskDto.getId(), assetTypeRiskDto);
            } else {
                newRisks.add(assetTypeRiskDto);
            }
        });
        List<Risk> existingRisk = assetType.getRisks();
        if (!existingRisk.isEmpty() && !existingRiskDtoCorrespondingToIds.isEmpty()) {
            existingRisk.forEach(assetTypeRisk -> {
                BasicRiskDTO basicRiskDTO = existingRiskDtoCorrespondingToIds.get(assetTypeRisk.getId());
                assetTypeRisk.setName(basicRiskDTO.getName());
                assetTypeRisk.setDescription(basicRiskDTO.getDescription());
                assetTypeRisk.setRiskRecommendation(basicRiskDTO.getRiskRecommendation());
                assetTypeRisk.setRiskLevel(basicRiskDTO.getRiskLevel());
            });
        }
        newRisks.forEach(newRisk -> {
            Risk risk = new Risk(newRisk.getName(), newRisk.getDescription(), newRisk.getRiskRecommendation(), newRisk.getRiskLevel());
            assetTypeRisks.add(risk);
        });
        assetType.setRisks(assetTypeRisks);
        return assetType;
    }

    /**
     * @return
     */
    public AssetTypeBasicDTO saveSuggestedAssetTypeAndSubAssetTypeFromUnit(Long countryId, AssetTypeBasicDTO assetTypeDTO) {
        AssetType previousAssetType = assetTypeRepository.findByNameAndCountryIdAndSubAssetType(assetTypeDTO.getName(), countryId, false);
        if (Optional.ofNullable(previousAssetType).isPresent()) {
            return null;
        }
        AssetType assetType = new AssetType(assetTypeDTO.getName(), countryId, SuggestedDataStatus.PENDING);
        assetType.setSuggestedDate(LocalDate.now());
        List<AssetType> subAssetTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assetTypeDTO.getSubAssetTypes())) {
            for (AssetTypeBasicDTO subAssetTypeDTO : assetTypeDTO.getSubAssetTypes()) {
                AssetType subAssetType = new AssetType(subAssetTypeDTO.getName(), countryId, SuggestedDataStatus.PENDING);
                subAssetType.setSuggestedDate(LocalDate.now());
                subAssetType.setSubAssetType(true);
                subAssetTypes.add(subAssetType);
            }
        }
        if (!subAssetTypes.isEmpty()) {
            assetType.setSubAssetTypes(subAssetTypes);
        }
        assetTypeRepository.save(assetType);
        assetTypeDTO.setId(assetType.getId());
        return assetTypeDTO;
    }


    /**
     * @param assetTypeDTOs check for duplicates in name of Asset types
     */
    private void checkForDuplicacyInNameOfAssetType(List<AssetTypeDTO> assetTypeDTOs) {
        List<String> names = new ArrayList<>();
        for (AssetTypeDTO assetTypeDTO : assetTypeDTOs) {
            if (names.contains(assetTypeDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException(MESSAGE_DUPLICATE, MESSAGE_ASSET_TYPE, assetTypeDTO.getName());
            }
            names.add(assetTypeDTO.getName().toLowerCase());
        }
    }


}
