package com.kairos.service.data_inventory.asset;


import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.data_inventory.AssetTypeOrganizationLevelDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.dto.gdpr.metadata.AssetTypeBasicDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetTypeDeprecated;
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
import java.math.BigInteger;
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
        AssetType assetType = new AssetType(assetTypeDto.getName());
        assetType.setOrganizationId(unitId);
        assetType.setSubAssetType(false);
        List<Risk> assetTypeRisks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assetTypeDto.getSubAssetTypes())) {
            List<AssetType> subAssetTypeList = buildSubAssetTypeListAndRiskAndLinkedToAssetType(unitId, assetTypeDto.getSubAssetTypes(), assetType);
            assetType.setHasSubAsset(true);
            assetType.setSubAssetTypes(subAssetTypeList);
        }
        for (OrganizationLevelRiskDTO assetTypeRisk : assetTypeDto.getRisks()) {
            Risk risk = new Risk(assetTypeRisk.getName(), assetTypeRisk.getDescription(), assetTypeRisk.getRiskRecommendation(), assetTypeRisk.getRiskLevel());
            assetTypeRisks.add(risk);
        }
        assetType.setRisks(assetTypeRisks);
        assetTypeRepository.save(assetType);
        assetTypeDto.setId(assetType.getId());
        return assetTypeDto;


    }


    public List<AssetType> buildSubAssetTypeListAndRiskAndLinkedToAssetType(Long unitId, List<AssetTypeOrganizationLevelDTO> subAssetTypesDto, AssetType assetTypeMD) {
        checkForDuplicacyInNameOfAssetType(subAssetTypesDto);
        List<AssetType> subAssetTypes = new ArrayList<>();
        List<Risk> subAssetRisks = new ArrayList<>();
        for (AssetTypeOrganizationLevelDTO subAssetTypeDto : subAssetTypesDto) {
            AssetType assetSubType = new AssetType(subAssetTypeDto.getName());
            assetSubType.setSubAssetType(true);
            assetSubType.setOrganizationId(unitId);
            assetSubType.setAssetType(assetTypeMD);
            assetSubType.setHasSubAsset(false);
            for (OrganizationLevelRiskDTO subAssetTypeRisk : subAssetTypeDto.getRisks()) {
                Risk risk = new Risk(subAssetTypeRisk.getName(), subAssetTypeRisk.getDescription(), subAssetTypeRisk.getRiskRecommendation(), subAssetTypeRisk.getRiskLevel());
                subAssetRisks.add(risk);
            }
            assetSubType.setRisks(subAssetRisks);
            subAssetTypes.add(assetSubType);
        }
        return subAssetTypes;

    }


    /**
     * @param
     * @param subAssetTypesDto contain list of Existing Sub Asset type which need to we update
     * @return map of Sub asset Types List and Ids (List for rollback)
     * @description this method update existing Sub asset Types and return list of Sub Asset Types and  ids list
     */
    private List<AssetTypeDeprecated> updateSubAssetTypes(Long unitId, List<AssetTypeOrganizationLevelDTO> subAssetTypesDto, Map<AssetTypeDeprecated, List<OrganizationLevelRiskDTO>> riskRelatedToSubAssetTypes) {

        Set<BigInteger> subAssetTypesIds = new HashSet<>();
        Map<BigInteger, AssetTypeOrganizationLevelDTO> subAssetTypeDtoCorrespondingToIds = new HashMap<>();
        subAssetTypesDto.forEach(subAssetTypeDto -> {
//            subAssetTypesIds.add(subAssetTypeDto.getId());
            //   subAssetTypeDtoCorrespondingToIds.put(subAssetTypeDto.getId(), subAssetTypeDto);
        });
        //TODO
        List<AssetTypeDeprecated> subAssetTypesList = new ArrayList<>();/*assetTypeMongoRepository.findAllByUnitIdAndIds(unitId, subAssetTypesIds);
        subAssetTypesList.forEach(subAssetType -> {
            AssetTypeOrganizationLevelDTO subAssetTypeDto = subAssetTypeDtoCorrespondingToIds.get(subAssetType.getId());
            riskRelatedToSubAssetTypes.put(subAssetType, subAssetTypeDto.getRisks());
            subAssetType.setName(subAssetTypeDto.getName());
        });*/
        return subAssetTypesList;
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
        assetTypeRiskResponseDTO.setHasSubAsset(assetType.isHasSubAsset());
        if (!assetType.getRisks().isEmpty()) {
            assetTypeRiskResponseDTO.setRisks(buildAssetTypeRisksResponse(assetType.getRisks()));
        }
        if (assetType.isHasSubAsset()) {
            assetType.getSubAssetTypes().forEach(subAssetType -> {
                subAssetTypeData.add(buildAssetTypeOrSubTypeResponseData(subAssetType));
            });
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
        List<OrganizationLevelRiskDTO> riskBasicResponseDTOS = new ArrayList<>();
        for (Risk assetTypeRisk : risks) {
            OrganizationLevelRiskDTO riskBasicResponseDTO = new OrganizationLevelRiskDTO();
            riskBasicResponseDTO.setId(assetTypeRisk.getId());
            riskBasicResponseDTO.setName(assetTypeRisk.getName());
            riskBasicResponseDTO.setDescription(assetTypeRisk.getDescription());
            riskBasicResponseDTO.setRiskRecommendation(assetTypeRisk.getRiskRecommendation());
            riskBasicResponseDTO.setRiskLevel(assetTypeRisk.getRiskLevel());
            riskBasicResponseDTO.setDaysToReminderBefore(assetTypeRisk.getDaysToReminderBefore());
            riskBasicResponseDTO.setReminderActive(assetTypeRisk.isReminderActive());
            riskBasicResponseDTOS.add(riskBasicResponseDTO);
        }
        return riskBasicResponseDTOS;
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
        assetType = assetTypeRepository.findByIdAndOrganizationIdAndDeleted(assetTypeId, unitId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset type", assetTypeId);
        }
        List<AssetType> subAssetTypeList = updateSubAssetTypes(unitId, assetTypeDto.getSubAssetTypes(), assetType);
        assetType.setName(assetTypeDto.getName());
        assetType.setSubAssetTypes(subAssetTypeList);
        assetType.setOrganizationId(unitId);
        assetType = updateOrAddAssetTypeRisk(assetType, assetTypeDto);
        assetTypeRepository.save(assetType);
        return assetTypeDto;

    }

    private AssetType updateOrAddAssetTypeRisk(AssetType assetType, AssetTypeOrganizationLevelDTO assetTypeDto) {
        List<OrganizationLevelRiskDTO> newRisks = new ArrayList<>();
        Map<Long, OrganizationLevelRiskDTO> existingRiskDtoCorrespondingToIds = new HashMap<>();
        Map<Long, List<OrganizationLevelRiskDTO>> assetTypeNewRiskDto = new HashMap<>();
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
                    assetType.getRisks().add(risk);
                });
            });
        }
        return assetType;
    }

    /**
     * @param unitId
     * @param subAssetTypesDto contain list of Existing Sub Asset type which need to we update
     * @return map of Sub asset Types List and Ids (List for rollback)
     * @description this method update existing Sub asset Types and return list of Sub Asset Types and  ids list
     */
    private List<AssetType> updateSubAssetTypes(Long unitId, List<AssetTypeOrganizationLevelDTO> subAssetTypesDto, AssetType assetTypeMD) {
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
                AssetType assetSubType = new AssetType(subAssetTypeDto.getName(), unitId, SuggestedDataStatus.APPROVED);
                assetSubType.setSubAssetType(true);
                assetSubType.setAssetType(assetTypeMD);
                for (OrganizationLevelRiskDTO subAssetTypeRisk : subAssetTypeDto.getRisks()) {
                    Risk risk = new Risk(subAssetTypeRisk.getName(), subAssetTypeRisk.getDescription(), subAssetTypeRisk.getRiskRecommendation(), subAssetTypeRisk.getRiskLevel());
                    subAssetRisks.add(risk);
                }
                assetSubType.setRisks(subAssetRisks);
                subAssetTypes.add(assetSubType);
            }

        });

        assetTypeMD.getSubAssetTypes().forEach(subAssetType -> {
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
                subAssetType.getRisks().add(risk);
            });
            subAssetTypes.add(subAssetType);
        });
        return subAssetTypes;
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
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "message.assetSubType", StringUtils.join(assetsLinkedWithAssetSubType, ','));
        }
        AssetType subAssetType = assetTypeRepository.findByIdAndOrganizationIdAndAssetTypeAndDeleted( subAssetTypeId, assetTypeId, unitId);
        if (!Optional.ofNullable(subAssetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetSubType", subAssetType);
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
    public Map<String, AssetTypeBasicDTO> saveAndSuggestAssetTypeAndSubAssetTypeToCountryAdmin(Long unitId, Long countryId, AssetTypeBasicDTO assetTypeBasicDTO) {

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
    private Map<String, AssetTypeBasicDTO> createAssetTypeAndSubAssetTypeWithBasicDetail(Long unitId, AssetTypeBasicDTO assetTypeBasicDTO) {

        AssetType assetType = new AssetType(assetTypeBasicDTO.getName());
        assetType.setOrganizationId(unitId);
        List<AssetType> subAssetTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assetTypeBasicDTO.getSubAssetTypes())) {
            for (AssetTypeBasicDTO subAssetTypeDTO : assetTypeBasicDTO.getSubAssetTypes()) {
                AssetType subAssetType = new AssetType(subAssetTypeDTO.getName());
                subAssetType.setOrganizationId(unitId);
                subAssetType.setSuggestedDate(LocalDate.now());
                assetType.setHasSubAsset(true);
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
