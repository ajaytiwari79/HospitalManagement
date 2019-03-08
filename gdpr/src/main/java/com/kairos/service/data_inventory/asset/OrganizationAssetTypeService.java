package com.kairos.service.data_inventory.asset;


import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.data_inventory.AssetTypeOrganizationLevelDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.dto.gdpr.metadata.AssetTypeBasicDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;

@Service
public class OrganizationAssetTypeService{


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationAssetTypeService.class);


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
     * @param organizationId
     * @param assetTypeDto contain asset data ,and list of sub asset types
     * @return asset type object
     * @throws DuplicateDataException if asset type is already present with same name
     * @description method create Asset type if sub Asset Types if present then create and add sub Asset Types to Asset type.
     */
    public AssetTypeOrganizationLevelDTO createAssetTypeAndAddSubAssetTypes(Long organizationId, AssetTypeOrganizationLevelDTO assetTypeDto) {


        AssetType previousAssetType = assetTypeRepository.findByNameAndOrganizationIdAndSubAssetType(assetTypeDto.getName(), organizationId, false);
        if (Optional.ofNullable(previousAssetType).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetTypeDto.getName());
        }
        AssetType assetType = new AssetType(assetTypeDto.getName());
        assetType.setOrganizationId(organizationId);
        assetType.setSubAssetType(false);
        List<Risk> assetTypeRisks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assetTypeDto.getSubAssetTypes())) {
            List<AssetType> subAssetTypeList = buildSubAssetTypeListAndRiskAndLinkedToAssetType(organizationId, assetTypeDto.getSubAssetTypes(), assetType);
            assetType.setHasSubAssetType(true);
            assetType.setSubAssetTypes(subAssetTypeList);
        }
        for (OrganizationLevelRiskDTO assetTypeRisk : assetTypeDto.getRisks()) {
            Risk risk = new Risk(assetTypeRisk.getName(), assetTypeRisk.getDescription(), assetTypeRisk.getRiskRecommendation(), assetTypeRisk.getRiskLevel());
            risk.setOrganizationId(organizationId);
            assetTypeRisks.add(risk);
        }
        assetType.setRisks(assetTypeRisks);
        assetTypeRepository.save(assetType);
        assetTypeDto.setId(assetType.getId());
        return assetTypeDto;


    }


    private List<AssetType> buildSubAssetTypeListAndRiskAndLinkedToAssetType(Long organizationId, List<AssetTypeOrganizationLevelDTO> subAssetTypesDto, AssetType assetType) {
        checkForDuplicacyInNameOfAssetType(subAssetTypesDto);
        List<AssetType> subAssetTypes = new ArrayList<>();
        List<Risk> subAssetRisks = new ArrayList<>();
        for (AssetTypeOrganizationLevelDTO subAssetTypeDto : subAssetTypesDto) {
            AssetType subAssetType = new AssetType(subAssetTypeDto.getName());
            subAssetType.setSubAssetType(true);
            subAssetType.setOrganizationId(organizationId);
            subAssetType.setAssetType(assetType);
            subAssetType.setHasSubAssetType(false);
            for (OrganizationLevelRiskDTO subAssetTypeRisk : subAssetTypeDto.getRisks()) {
                Risk risk = new Risk(subAssetTypeRisk.getName(), subAssetTypeRisk.getDescription(), subAssetTypeRisk.getRiskRecommendation(), subAssetTypeRisk.getRiskLevel());
                risk.setOrganizationId(organizationId);
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
        if (assetType.isHasSubAssetType()) {
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
     * @param organizationId
     * @return return list of Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public List<AssetTypeResponseDTO> getAllAssetType(Long organizationId) {
        List<AssetType> assetTypes = assetTypeRepository.getAllAssetTypesByOrganization(organizationId);
        List<AssetTypeResponseDTO> assetTypesWithAllData = new ArrayList<>();
        for (AssetType assetType : assetTypes) {
            assetTypesWithAllData.add(buildAssetTypeOrSubTypeResponseData(assetType));
        }
        return assetTypesWithAllData;
    }


    /**
     * @param
     * @param organizationId
     * @return return Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public AssetTypeResponseDTO getAssetTypeById(Long organizationId, Long id) {
        AssetType assetType = assetTypeRepository.findByIdAndOrganizationIdAndDeleted(id, organizationId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", id);
        }

        return buildAssetTypeOrSubTypeResponseData(assetType);

    }

    /**
     * @param
     * @param organizationId
     * @param assetTypeId  id of Asset Type to which Sub Asset Types Link.
     * @param assetTypeDto asset type Dto contain list of Existing sub Asset types which need to be update and New SubAsset Types  which we need to create and add to asset afterward.
     * @return Asset Type with updated Sub Asset and new Sub Asset Types
     * @throws DuplicateDataException if Asset type is already present with same name .
     * @description method simply (update already exit Sub asset types if id is present)and (add create new sub asset types if id is not present in sub asset types)
     */
    public AssetTypeOrganizationLevelDTO updateAssetTypeAndSubAssetsAndAddRisks(Long organizationId, Long assetTypeId, AssetTypeOrganizationLevelDTO assetTypeDto) {

        AssetType assetType = assetTypeRepository.findByNameAndOrganizationIdAndSubAssetType(assetTypeDto.getName(), organizationId, false);
        if (Optional.ofNullable(assetType).isPresent() && !assetTypeId.equals(assetType.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetTypeDto.getName());
        }
        assetType = assetTypeRepository.findByIdAndOrganizationIdAndDeleted(assetTypeId, organizationId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset type", assetTypeId);
        }
        List<AssetType> subAssetTypeList = updateSubAssetTypes(organizationId, assetTypeDto.getSubAssetTypes(), assetType);
        assetType.setName(assetTypeDto.getName());
        assetType.setSubAssetTypes(subAssetTypeList);
        assetType.setOrganizationId(organizationId);
        updateOrAddAssetTypeRisk(organizationId,assetType, assetTypeDto);
        assetTypeRepository.save(assetType);
        return assetTypeDto;

    }

    private AssetType updateOrAddAssetTypeRisk(Long organizationId, AssetType assetType, AssetTypeOrganizationLevelDTO assetTypeDto) {
        List<OrganizationLevelRiskDTO> newRisks = new ArrayList<>();
        Map<Long, OrganizationLevelRiskDTO> existingRiskDtoCorrespondingToIds = new HashMap<>();
        Map<Long, List<OrganizationLevelRiskDTO>> assetTypeNewRiskDto = new HashMap<>();
        List<Risk> assetTypeRisks = assetType.getRisks();
        assetTypeDto.getRisks().forEach(assetTypeRiskDto -> {
            if (Optional.ofNullable(assetTypeRiskDto.getId()).isPresent()) {
                existingRiskDtoCorrespondingToIds.put(assetTypeRiskDto.getId(), assetTypeRiskDto);
            } else {
                newRisks.add(assetTypeRiskDto);
            }
        });
        assetTypeNewRiskDto.put(assetType.getId(), newRisks);
        if (!assetType.getRisks().isEmpty() && !existingRiskDtoCorrespondingToIds.isEmpty()) {
            assetType.getRisks().forEach(assetTypeRisk -> {
                OrganizationLevelRiskDTO basicRiskDTO = existingRiskDtoCorrespondingToIds.get(assetTypeRisk.getId());
                assetTypeRisk.setName(basicRiskDTO.getName());
                assetTypeRisk.setDescription(basicRiskDTO.getDescription());
                assetTypeRisk.setRiskRecommendation(basicRiskDTO.getRiskRecommendation());
                assetTypeRisk.setRiskLevel(basicRiskDTO.getRiskLevel());
                assetTypeNewRiskDto.get(assetTypeRisk.getId()).forEach(newRisk -> {
                    Risk risk = new Risk(newRisk.getName(), newRisk.getDescription(), newRisk.getRiskRecommendation(), newRisk.getRiskLevel());
                    risk.setOrganizationId(organizationId);
                    assetTypeRisks.add(risk);
                });
            });
        }
        assetType.setRisks(assetTypeRisks);
        return assetType;
    }

    /**
     * @param organizationId
     * @param subAssetTypesDto contain list of Existing Sub Asset type which need to we update
     * @return map of Sub asset Types List and Ids (List for rollback)
     * @description this method update existing Sub asset Types and return list of Sub Asset Types and  ids list
     */
    private List<AssetType> updateSubAssetTypes(Long organizationId, List<AssetTypeOrganizationLevelDTO> subAssetTypesDto, AssetType assetType) {
        List<OrganizationLevelRiskDTO> newRiskOfSubAssetType = new ArrayList<>();
        Map<Long, AssetTypeOrganizationLevelDTO> subAssetTypeDtoCorrespondingToIds = new HashMap<>();
        Map<Long, OrganizationLevelRiskDTO> subAssetTypeExistingRiskDtoCorrespondingToIds = new HashMap<>();
        Map<Long, List<OrganizationLevelRiskDTO>> subAssetTypeNewRiskDto = new HashMap<>();
        List<AssetType> subAssetTypes = new ArrayList<>();
        List<Risk> subAssetRisks = new ArrayList<>();
        subAssetTypesDto.forEach(subAssetTypeDto -> {
            if (Optional.ofNullable(subAssetTypeDto.getId()).isPresent()) {
                subAssetTypeDtoCorrespondingToIds.put(subAssetTypeDto.getId(), subAssetTypeDto);
                subAssetTypeDto.getRisks().forEach(subAssetTypeRiskDto -> {
                    if (Optional.ofNullable(subAssetTypeRiskDto.getId()).isPresent()) {
                        subAssetTypeExistingRiskDtoCorrespondingToIds.put(subAssetTypeRiskDto.getId(), subAssetTypeRiskDto);
                    } else {
                        newRiskOfSubAssetType.add(subAssetTypeRiskDto);
                    }
                });
                subAssetTypeNewRiskDto.put(subAssetTypeDto.getId(), newRiskOfSubAssetType);
            } else {
                AssetType subAssetType = new AssetType(subAssetTypeDto.getName(), organizationId, SuggestedDataStatus.APPROVED);
                subAssetType.setSubAssetType(true);
                subAssetType.setAssetType(assetType);
                for (OrganizationLevelRiskDTO subAssetTypeRisk : subAssetTypeDto.getRisks()) {
                    Risk risk = new Risk(subAssetTypeRisk.getName(), subAssetTypeRisk.getDescription(), subAssetTypeRisk.getRiskRecommendation(), subAssetTypeRisk.getRiskLevel());
                    risk.setOrganizationId(organizationId);
                    subAssetRisks.add(risk);
                }
                subAssetType.setRisks(subAssetRisks);
                subAssetTypes.add(subAssetType);
            }

        });

        assetType.getSubAssetTypes().forEach(subAssetType -> {
            AssetTypeOrganizationLevelDTO subAssetTypeDto = subAssetTypeDtoCorrespondingToIds.get(subAssetType.getId());
            subAssetType.setName(subAssetTypeDto.getName());
            if (!subAssetType.getRisks().isEmpty() && !subAssetTypeExistingRiskDtoCorrespondingToIds.isEmpty()) {
                subAssetType.getRisks().forEach(subAssetTypeRisk -> {
                    OrganizationLevelRiskDTO basicRiskDTO = subAssetTypeExistingRiskDtoCorrespondingToIds.get(subAssetTypeRisk.getId());
                    subAssetTypeRisk.setName(basicRiskDTO.getName());
                    subAssetTypeRisk.setDescription(basicRiskDTO.getDescription());
                    subAssetTypeRisk.setRiskRecommendation(basicRiskDTO.getRiskRecommendation());
                    subAssetTypeRisk.setRiskLevel(basicRiskDTO.getRiskLevel());
                });
            }
            subAssetTypeNewRiskDto.get(subAssetType.getId()).forEach(newRisk -> {
                Risk risk = new Risk(newRisk.getName(), newRisk.getDescription(), newRisk.getRiskRecommendation(), newRisk.getRiskLevel());
                risk.setOrganizationId(organizationId);
                subAssetType.getRisks().add(risk);
            });
            subAssetTypes.add(subAssetType);
        });
        return subAssetTypes;
    }



    /**
     * @param organizationId
     * @param assetTypeId
     * @return
     */
    public boolean deleteAssetTypeById(Long organizationId, Long assetTypeId) {

        List<String> assetsLinkedWithAssetType = assetRepository.findAllAssetLinkedWithAssetType(organizationId, assetTypeId);
        if (CollectionUtils.isNotEmpty(assetsLinkedWithAssetType)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "message.assetType", StringUtils.join(assetsLinkedWithAssetType, ','));
        }
        AssetType assetType = assetTypeRepository.findByIdAndOrganizationIdAndDeleted(assetTypeId, organizationId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", assetTypeId);
        }
        assetType.delete();
        assetTypeRepository.save(assetType);
        return true;

    }


    public boolean deleteAssetSubTypeById(Long organizationId, Long assetTypeId, Long subAssetTypeId) {
        List<String> assetsLinkedWithAssetSubType = assetRepository.findAllAssetLinkedWithAssetType(organizationId, subAssetTypeId);
        if (CollectionUtils.isNotEmpty(assetsLinkedWithAssetSubType)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "message.subAssetType", StringUtils.join(assetsLinkedWithAssetSubType, ','));
        }
        AssetType subAssetType = assetTypeRepository.findByIdAndOrganizationIdAndAssetTypeAndDeleted( subAssetTypeId, assetTypeId, organizationId);
        if (!Optional.ofNullable(subAssetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.subAssetType", subAssetType);
        }

        subAssetType.delete();
        subAssetType.getAssetType().getSubAssetTypes().remove(subAssetType);
        assetTypeRepository.save(subAssetType);
        return true;

    }


    /**
     * @param organizationId
     * @param assetTypeId
     * @param riskId      - risk id link with asset type and Sub Asset type
     * @return
     * @description - Remove risk id from asset type and soft deleted risk
     */
    public boolean unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(Long organizationId, Long assetTypeId, Long riskId) {
        //TODO
        /*Integer updateCount = riskRepository.unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(riskId, assetTypeId,organizationId);
        if (updateCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", assetTypeId);
        }else{
            LOGGER.info("Data updated successfully.");
        }*/

        return true;
    }


    /**
     * @param countryId
     * @param organizationId
     * @param assetTypeBasicDTO
     * @return
     */
    public Map<String, AssetTypeBasicDTO> saveAndSuggestAssetTypeAndSubAssetTypeToCountryAdmin(Long organizationId, Long countryId, AssetTypeBasicDTO assetTypeBasicDTO) {

        Map<String, AssetTypeBasicDTO> result = createAssetTypeAndSubAssetTypeWithBasicDetail(organizationId, assetTypeBasicDTO);
        AssetTypeBasicDTO acceptedByCountryAdmin = assetTypeService.saveSuggestedAssetTypeAndSubAssetTypeFromUnit(countryId, assetTypeBasicDTO);
        result.put("SuggestedData", acceptedByCountryAdmin);
        return result;
    }


    /**
     * @param organizationId
     * @param assetTypeBasicDTO
     * @return
     * @description create sub Asset tYpe and Asset type with basic detail on Save and Suggest of data from unit to country admin
     */
    @SuppressWarnings("unchecked")
    private Map<String, AssetTypeBasicDTO> createAssetTypeAndSubAssetTypeWithBasicDetail(Long organizationId, AssetTypeBasicDTO assetTypeBasicDTO) {

        AssetType assetType = new AssetType(assetTypeBasicDTO.getName());
        assetType.setOrganizationId(organizationId);
        List<AssetType> subAssetTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assetTypeBasicDTO.getSubAssetTypes())) {
            for (AssetTypeBasicDTO subAssetTypeDTO : assetTypeBasicDTO.getSubAssetTypes()) {
                AssetType subAssetType = new AssetType(subAssetTypeDTO.getName());
                subAssetType.setOrganizationId(organizationId);
                subAssetType.setSuggestedDate(LocalDate.now());
                assetType.setHasSubAssetType(true);
                subAssetType.setSubAssetType(true);
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
     * @param assetTypeDTOs check for duplicates in name of Asset types
     */
    private void checkForDuplicacyInNameOfAssetType(List<AssetTypeOrganizationLevelDTO> assetTypeDTOs) {
        List<String> names = new ArrayList<>();
        for (AssetTypeOrganizationLevelDTO assetTypeDTO : assetTypeDTOs) {
            if (names.contains(assetTypeDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetTypeDTO.getName());
            }
            names.add(assetTypeDTO.getName().toLowerCase());
        }
    }


}
