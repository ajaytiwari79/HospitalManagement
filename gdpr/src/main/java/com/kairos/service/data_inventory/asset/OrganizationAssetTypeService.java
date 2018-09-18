package com.kairos.service.data_inventory.asset;


import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.data_inventory.AssetTypeOrganizationLevelDTO;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistence.repository.risk_management.RiskMongoRepository;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.response.dto.master_data.AssetTypeRiskResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.risk_management.RiskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrganizationAssetTypeService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationAssetTypeService.class);


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;


    @Inject
    private AssetMongoRepository assetMongoRepository;

    @Inject
    private RiskService riskService;

    @Inject
    private RiskMongoRepository riskMongoRepository;


    /**
     * @param
     * @param unitId
     * @param assetTypeDto contain asset data ,and list of sub asset types
     * @return asset type object
     * @throws DuplicateDataException if asset type is already present with same name
     * @description method create Asset type if sub Asset Types if present then create and add sub Asset Types to Asset type.
     */
    public AssetTypeOrganizationLevelDTO createAssetTypeAndAddSubAssetTypes(Long unitId, AssetTypeOrganizationLevelDTO assetTypeDto) {


        AssetType previousAssetType = assetTypeMongoRepository.findByNameAndUnitId(unitId, assetTypeDto.getName());
        if (Optional.ofNullable(previousAssetType).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDto.getName());
        }
        AssetType assetType = new AssetType(assetTypeDto.getName());
        assetType.setOrganizationId(unitId);
        Map<AssetType, List<OrganizationLevelRiskDTO>> riskRelatedToAssetTypeAndSubAssetType = new HashMap<>();
        List<AssetType> subAssetTypeList = new ArrayList<>();
        if (!assetTypeDto.getRisks().isEmpty()) {
            riskRelatedToAssetTypeAndSubAssetType.put(assetType, assetTypeDto.getRisks());
        }
        if (!assetTypeDto.getSubAssetTypes().isEmpty()) {
            subAssetTypeList = buildSubAssetTypeListAndRiskAndLinkedToAssetType(unitId, assetTypeDto.getSubAssetTypes(), riskRelatedToAssetTypeAndSubAssetType);
            assetType.setHasSubAsset(true);
        }
        Map<AssetType, List<BigInteger>> riskIdsCoresspondingToAssetAndSubAssetType;
        if (!riskRelatedToAssetTypeAndSubAssetType.isEmpty()) {
            riskIdsCoresspondingToAssetAndSubAssetType = riskService.saveRiskAtCountryLevelOrOrganizationLevel(unitId, true, riskRelatedToAssetTypeAndSubAssetType);
            for (AssetType subAssetType : subAssetTypeList) {
                subAssetType.setRisks(riskIdsCoresspondingToAssetAndSubAssetType.get(subAssetType));
            }
            assetType.setRisks(riskIdsCoresspondingToAssetAndSubAssetType.get(assetType));
        }
        if (!subAssetTypeList.isEmpty()) {
            assetTypeMongoRepository.saveAll(getNextSequence(subAssetTypeList));
            List<BigInteger> subAssetTypeIds = subAssetTypeList.stream().map(AssetType::getId).collect(Collectors.toList());
            assetType.setSubAssetTypes(subAssetTypeIds);
        }
        assetTypeMongoRepository.save(assetType);
        assetTypeDto.setId(assetType.getId());
        return assetTypeDto;


    }


    public List<AssetType> buildSubAssetTypeListAndRiskAndLinkedToAssetType(Long unitId, List<AssetTypeOrganizationLevelDTO> subAssetTypesDto, Map<AssetType, List<OrganizationLevelRiskDTO>> riskRelatedToSubAssetTypes) {

        checkForDuplicacyInNameOfAssetType(subAssetTypesDto);
        List<AssetType> subAssetTypes = new ArrayList<>();
        for (AssetTypeOrganizationLevelDTO subAssetTypeDto : subAssetTypesDto) {
            AssetType assetSubType = new AssetType(subAssetTypeDto.getName());
            assetSubType.setOrganizationId(unitId);
            assetSubType.setSubAsset(true);
            if (!subAssetTypeDto.getRisks().isEmpty()) {
                riskRelatedToSubAssetTypes.put(assetSubType, subAssetTypeDto.getRisks());
            }
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
    private List<AssetType> updateSubAssetTypes(Long unitId, List<AssetTypeOrganizationLevelDTO> subAssetTypesDto, Map<AssetType, List<OrganizationLevelRiskDTO>> riskRelatedToSubAssetTypes) {

        List<BigInteger> subAssetTypesIds = new ArrayList<>();
        Map<BigInteger, AssetTypeOrganizationLevelDTO> subAssetTypeDtoCorrespondingToIds = new HashMap<>();
        subAssetTypesDto.forEach(subAssetTypeDto -> {
            subAssetTypesIds.add(subAssetTypeDto.getId());
            subAssetTypeDtoCorrespondingToIds.put(subAssetTypeDto.getId(), subAssetTypeDto);
        });
        List<AssetType> subAssetTypesList = assetTypeMongoRepository.findAllAssetTypeByUnitIdAndIds(unitId, subAssetTypesIds);
        subAssetTypesList.forEach(subAssetType -> {
            AssetTypeOrganizationLevelDTO subAssetTypeDto = subAssetTypeDtoCorrespondingToIds.get(subAssetType.getId());
            if (!subAssetTypeDto.getRisks().isEmpty()) {
                riskRelatedToSubAssetTypes.put(subAssetType, subAssetTypeDto.getRisks());
            }
            subAssetType.setName(subAssetTypeDto.getName());
        });
        return subAssetTypesList;
    }


    /**
     * @param
     * @param organizationId
     * @return return list of Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public List<AssetTypeRiskResponseDTO> getAllAssetType(Long organizationId) {
        return assetTypeMongoRepository.getAllAssetTypeWithSubAssetTypeAndRiskByUnitId(organizationId);
    }


    /**
     * @param
     * @param organizationId
     * @return return Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public AssetTypeResponseDTO getAssetTypeById(Long organizationId, BigInteger id) {
        AssetTypeResponseDTO assetType = assetTypeMongoRepository.getAssetTypesWithSubAssetTypesByIdAndUnitId(organizationId, id);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset Type", id);
        }
        return assetType;

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
    public AssetTypeOrganizationLevelDTO updateAssetTypeAndSubAssetsAndAddRisks(Long unitId, BigInteger assetTypeId, AssetTypeOrganizationLevelDTO assetTypeDto) {

        AssetType assetType = assetTypeMongoRepository.findByNameAndUnitId(unitId, assetTypeDto.getName());
        if (Optional.ofNullable(assetType).isPresent() && !assetTypeId.equals(assetType.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDto.getName());
        }
        assetType = assetTypeMongoRepository.findByUnitIdAndId(unitId, assetTypeId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.duplicateDataException("message.dataNotFound", "Asset Type", assetTypeId);
        }
        assetType.setName(assetTypeDto.getName());
        List<AssetTypeOrganizationLevelDTO> newSubAssetTypeDTOs = new ArrayList<>();
        List<AssetTypeOrganizationLevelDTO> updateExistingSubAssetTypeDTOs = new ArrayList<>();
        assetTypeDto.getSubAssetTypes().forEach(subAssetTypeDto -> {
            if (Optional.ofNullable(subAssetTypeDto.getId()).isPresent()) {
                updateExistingSubAssetTypeDTOs.add(subAssetTypeDto);
            } else {
                newSubAssetTypeDTOs.add(subAssetTypeDto);
            }
        });
        Map<AssetType, List<OrganizationLevelRiskDTO>> riskRelatedToAssetTypeAndSubAssetType = new HashMap<>();
        List<AssetType> subAssetTypeList = new ArrayList<>();
        if (!assetTypeDto.getRisks().isEmpty()) {
            riskRelatedToAssetTypeAndSubAssetType.put(assetType, assetTypeDto.getRisks());
        }
        if (!newSubAssetTypeDTOs.isEmpty()) {
            subAssetTypeList.addAll(buildSubAssetTypeListAndRiskAndLinkedToAssetType(unitId, newSubAssetTypeDTOs, riskRelatedToAssetTypeAndSubAssetType));
        }
        if (!updateExistingSubAssetTypeDTOs.isEmpty()) {
            subAssetTypeList.addAll(updateSubAssetTypes(unitId, updateExistingSubAssetTypeDTOs, riskRelatedToAssetTypeAndSubAssetType));
        }
        Map<AssetType, List<BigInteger>> riskIdsRelatedToSubAssetTypeOrAssetType;
        if (!riskRelatedToAssetTypeAndSubAssetType.isEmpty()) {
            riskIdsRelatedToSubAssetTypeOrAssetType = riskService.saveRiskAtCountryLevelOrOrganizationLevel(unitId, true, riskRelatedToAssetTypeAndSubAssetType);
            for (AssetType subAssetType : subAssetTypeList) {
                subAssetType.setRisks(riskIdsRelatedToSubAssetTypeOrAssetType.get(subAssetType));
            }
            assetType.setRisks(riskIdsRelatedToSubAssetTypeOrAssetType.get(assetType));
        }

        if (!subAssetTypeList.isEmpty()) {
            assetTypeMongoRepository.saveAll(getNextSequence(subAssetTypeList));
            List<BigInteger> subAssetTypeIds = subAssetTypeList.stream().map(AssetType::getId).collect(Collectors.toList());
            assetType.setSubAssetTypes(subAssetTypeIds);
        }
        assetTypeMongoRepository.save(assetType);
        return assetTypeDto;

    }


    /**
     * @param unitId
     * @param assetTypeId
     * @return
     */
    public boolean deleteAssetTypeById(Long unitId, BigInteger assetTypeId) {

        List<AssetBasicResponseDTO> assetsLinkedWithAssetType = assetMongoRepository.findAllAssetLinkedWithAssetType(unitId, assetTypeId);
        if (!assetsLinkedWithAssetType.isEmpty()) {
            StringBuilder assetNames = new StringBuilder();
            assetsLinkedWithAssetType.forEach(asset -> assetNames.append(asset.getName() + ","));
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Asset Type", assetNames);
        }
        AssetType assetType = assetTypeMongoRepository.findByUnitIdAndId(unitId, assetTypeId);
        if (!Optional.ofNullable(assetType).isPresent() && !assetType.isSubAsset()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset Type", assetType);
        }
        delete(assetType);
        return true;

    }


    public boolean deleteAssetSubTypeById(Long unitId, BigInteger assetTypeId, BigInteger subAssetTypeId) {

        List<AssetBasicResponseDTO> assetsLinkedWithAssetSubType = assetMongoRepository.findAllAssetLinkedWithAssetSubType(unitId, subAssetTypeId);
        if (!assetsLinkedWithAssetSubType.isEmpty()) {
            StringBuilder assetNames = new StringBuilder();
            assetsLinkedWithAssetSubType.forEach(asset -> assetNames.append(asset.getName() + ","));
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Data Disposal", assetNames);
        }
        AssetType assetType = assetTypeMongoRepository.findByUnitIdAndId(unitId, assetTypeId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset Type", assetType);
        } else {
            assetType.getSubAssetTypes().remove(subAssetTypeId);
            assetTypeMongoRepository.save(assetType);
            AssetType subAssetType = assetTypeMongoRepository.findByUnitIdAndId(unitId, subAssetTypeId);
            if (!Optional.ofNullable(subAssetType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "Sub AssetType", subAssetType);
            }
            delete(subAssetType);
        }
        return true;

    }


    /**
     * @param unitId
     * @param assetTypeId
     * @param riskId      - risk id linke with asset type and Sub Asset type
     * @return
     * @description - Remove risk id from asset type and soft deleted risk
     */
    public boolean unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(Long unitId, BigInteger assetTypeId, BigInteger riskId) {

        AssetType assetType = assetTypeMongoRepository.findByUnitIdAndId(unitId, assetTypeId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset Type", assetTypeId);
        }
        assetType.getRisks().remove(riskId);
        riskMongoRepository.findByIdAndSafeDelete(riskId);
        assetTypeMongoRepository.save(assetType);
        return true;
    }

    /**
     * @param assetTypeDTOs check for duplicates in name of Asset types
     */
    private void checkForDuplicacyInNameOfAssetType(List<AssetTypeOrganizationLevelDTO> assetTypeDTOs) {
        List<String> names = new ArrayList<>();
        for (AssetTypeOrganizationLevelDTO assetTypeDTO : assetTypeDTOs) {
            if (names.contains(assetTypeDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDTO.getName());
            }
            names.add(assetTypeDTO.getName().toLowerCase());
        }
    }


}
