package com.kairos.service.data_inventory.asset;


import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.gdpr.master_data.AssetTypeDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistance.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistance.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.ASSET_TYPES_LIST;
import static com.kairos.constants.AppConstant.IDS_LIST;

@Service
public class OrganizationAssetTypeService extends MongoBaseService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationAssetTypeService.class);


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;


    @Inject
    private AssetMongoRepository assetMongoRepository;


    /**
     * @param
     * @param organizationId
     * @param assetTypeDto   contain asset data ,and list of sub asset types
     * @return asset type object
     * @throws DuplicateDataException if asset type is already present with same name
     * @description method create Asset type if sub Asset Types if present then create and add sub Asset Types to Asset type.
     */
    public AssetTypeDTO createAssetTypeAndAddSubAssetTypes(Long organizationId, AssetTypeDTO assetTypeDto) {


        AssetType previousAssetType = assetTypeMongoRepository.findByNameAndOrganizationId(organizationId, assetTypeDto.getName());
        if (Optional.ofNullable(previousAssetType).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDto.getName());
        }

        Map<String, Object> subAssetTypes;
        AssetType assetType = new AssetType();
        if (!assetTypeDto.getSubAssetTypes().isEmpty()) {
            subAssetTypes = createNewSubAssetTypesList(organizationId, assetTypeDto.getSubAssetTypes());
            assetType.setSubAssetTypes((List<BigInteger>) subAssetTypes.get(IDS_LIST));
            assetType.setHasSubAsset(true);
        }
        assetType.setName(assetTypeDto.getName());
        assetType.setOrganizationId(organizationId);
        try {
            assetType = assetTypeMongoRepository.save(assetType);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        assetTypeDto.setId(assetType.getId());
        return assetTypeDto;
    }

    /**
     * @param
     * @param subAssetTypesDto contain list of sub Asset DTOs
     * @return create new Sub Asset type And return  map of Sub Asset types and Ids of Sub Asset types
     */
    public Map<String, Object> createNewSubAssetTypesList(Long organizationId, List<AssetTypeDTO> subAssetTypesDto) {

        checkForDuplicacyInNameOfAssetType(subAssetTypesDto);
        List<AssetType> subAssetTypes = new ArrayList<>();
        for (AssetTypeDTO subAssetTypeDto : subAssetTypesDto) {
            AssetType assetType = new AssetType();
            assetType.setName(subAssetTypeDto.getName());
            assetType.setOrganizationId(organizationId);
            assetType.setSubAsset(true);
            subAssetTypes.add(assetType);
        }
        Map<String, Object> result = new HashMap<>();
        List<BigInteger> subAssetTypesIds = new ArrayList<>();
        try {
            subAssetTypes = assetTypeMongoRepository.saveAll(getNextSequence(subAssetTypes));
            subAssetTypes.forEach(subAssetType -> subAssetTypesIds.add(subAssetType.getId()));
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        result.put(IDS_LIST, subAssetTypesIds);
        result.put(ASSET_TYPES_LIST, subAssetTypes);
        return result;
    }


    /**
     * @param
     * @param subAssetTypesDto contain list of Existing Sub Asset type which need to we update
     * @return map of Sub asset Types List and Ids (List for rollback)
     * @description this method update existing Sub asset Types and return list of Sub Asset Types and  ids list
     */
    public Map<String, Object> updateSubAssetTypes(Long organizationId, List<AssetTypeDTO> subAssetTypesDto) {

        List<BigInteger> subAssetTypesIds = new ArrayList<>();
        Map<BigInteger, AssetTypeDTO> subAssetTypeDtoCorrespondingToIds = new HashMap<>();
        subAssetTypesDto.forEach(subAssetTypeDto -> {
            subAssetTypesIds.add(subAssetTypeDto.getId());
            subAssetTypeDtoCorrespondingToIds.put(subAssetTypeDto.getId(), subAssetTypeDto);
        });
        List<AssetType> subAssetTypesList = assetTypeMongoRepository.findAllAssetTypeByIdsAndOrganizationId(organizationId, subAssetTypesIds);
        subAssetTypesList.forEach(subAssetType -> {

            AssetTypeDTO subAssetTypeDto = subAssetTypeDtoCorrespondingToIds.get(subAssetType.getId());
            subAssetType.setName(subAssetTypeDto.getName());
            subAssetType.setOrganizationId(organizationId);
        });
        Map<String, Object> result = new HashMap<>();
        try {
            subAssetTypesList = assetTypeMongoRepository.saveAll(getNextSequence(subAssetTypesList));
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        result.put(IDS_LIST, subAssetTypesIds);
        result.put(ASSET_TYPES_LIST, subAssetTypesList);
        return result;
    }


    /**
     * @param
     * @param organizationId
     * @return return list of Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public List<AssetTypeResponseDTO> getAllAssetType(Long organizationId) {
        return assetTypeMongoRepository.getAllOrganizationAssetTypesWithSubAssetTypes(organizationId);
    }


    /**
     * @param
     * @param organizationId
     * @return return Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public AssetTypeResponseDTO getAssetTypeById(Long organizationId, BigInteger id) {
        AssetTypeResponseDTO assetType = assetTypeMongoRepository.getOrganizationAssetTypesWithSubAssetTypes(organizationId, id);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset Type", id);
        }
        return assetType;

    }


    /**
     * @param
     * @param organizationId
     * @param id             id of Asset Type to which Sub Asset Types Link.
     * @param assetTypeDto   asset type Dto contain list of Existing sub Asset types which need to be update and New SubAsset Types  which we need to create and add to asset afterward.
     * @return Asset Type with updated Sub Asset and new Sub Asset Types
     * @throws DuplicateDataException if Asset type is already present with same name .
     * @description method simply (update already exit Sub asset types if id is present)and (add create new sub asset types if id is not present in sub asset types)
     */
    public AssetTypeDTO updateAssetTypeUpdateAndCreateNewSubAssetsAndAddToAssetType(Long organizationId, BigInteger id, AssetTypeDTO assetTypeDto) {
        AssetType assetType = assetTypeMongoRepository.findByNameAndOrganizationId(organizationId, assetTypeDto.getName());
        if (Optional.ofNullable(assetType).isPresent() && !id.equals(assetType.getId())) {
            throw new DuplicateDataException("data  exist for  " + assetTypeDto.getName());
        }

        assetType = assetTypeMongoRepository.findByOrganizationIdAndId(organizationId, id);
        assetType.setName(assetTypeDto.getName());
        List<AssetTypeDTO> newSubAssetTypesList = new ArrayList<>();
        List<AssetTypeDTO> updateExistingSubAssetTypes = new ArrayList<>();
        assetTypeDto.getSubAssetTypes().forEach(subAssetTypeDto -> {
            if (Optional.ofNullable(subAssetTypeDto.getId()).isPresent()) {
                updateExistingSubAssetTypes.add(subAssetTypeDto);
            } else {
                newSubAssetTypesList.add(subAssetTypeDto);
            }
        });
        Map<String, Object> updatedSubAssetTypes = new HashMap<>(), newSubAssetTypes = new HashMap<>();
        List<BigInteger> updatedAndNewSubAssetTypeIds = new ArrayList<>();
        if (newSubAssetTypesList.size() != 0) {
            newSubAssetTypes = createNewSubAssetTypesList(organizationId, newSubAssetTypesList);
            updatedAndNewSubAssetTypeIds.addAll((List<BigInteger>) newSubAssetTypes.get(IDS_LIST));
        }
        if (updateExistingSubAssetTypes.size() != 0) {
            updatedSubAssetTypes = updateSubAssetTypes(organizationId, updateExistingSubAssetTypes);
            updatedAndNewSubAssetTypeIds.addAll((List<BigInteger>) updatedSubAssetTypes.get(IDS_LIST));
        }

        try {
            assetType.setSubAssetTypes(updatedAndNewSubAssetTypeIds);
            assetTypeMongoRepository.save(assetType);
        } catch (Exception e) {
            List<AssetType> subAssetTypes = new ArrayList<>();
            subAssetTypes.addAll((List<AssetType>) newSubAssetTypes.get(ASSET_TYPES_LIST));
            subAssetTypes.addAll((List<AssetType>) updatedSubAssetTypes.get(ASSET_TYPES_LIST));
            assetTypeMongoRepository.deleteAll(subAssetTypes);
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());

        }
        return assetTypeDto;

    }


    public boolean deleteAssetTypeById(Long unitId, BigInteger assetTypeId) {

        List<AssetBasicResponseDTO> assetsLinkedWithAssetType = assetMongoRepository.findAllAssetLinkedWithAssetType(unitId, assetTypeId);
        if (!assetsLinkedWithAssetType.isEmpty()) {
            StringBuilder assetNames = new StringBuilder();
            assetsLinkedWithAssetType.forEach(asset -> assetNames.append(asset.getName() + ","));
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Asset Type", assetNames);
        }
        AssetType assetType = assetTypeMongoRepository.findByOrganizationIdAndId(unitId, assetTypeId);
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
        AssetType assetType = assetTypeMongoRepository.findByOrganizationIdAndId(unitId, assetTypeId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset Type", assetType);
        } else {
            assetType.getSubAssetTypes().remove(subAssetTypeId);
            assetTypeMongoRepository.save(assetType);
            AssetType subAssetType = assetTypeMongoRepository.findByOrganizationIdAndId(unitId, subAssetTypeId);
            if (!Optional.ofNullable(subAssetType).isPresent()) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "Sub AssetType", subAssetType);
            }
            delete(subAssetType);
        }
        return true;

    }


    /**
     * @param assetTypeDTOs check for duplicates in name of Asset types
     */
    private void checkForDuplicacyInNameOfAssetType(List<AssetTypeDTO> assetTypeDTOs) {
        List<String> names = new ArrayList<>();
        for (AssetTypeDTO assetTypeDTO : assetTypeDTOs) {
            if (names.contains(assetTypeDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDTO.getName());
            }
            names.add(assetTypeDTO.getName().toLowerCase());
        }
    }


}
