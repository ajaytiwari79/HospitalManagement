package com.kairos.service.master_data.asset_management;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.gdpr.master_data.AssetTypeDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistance.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistance.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.IDS_LIST;
import static com.kairos.constants.AppConstant.ASSET_TYPES_LIST;


@Service
public class AssetTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetTypeService.class);


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;

    @Inject
    private MasterAssetMongoRepository masterAssetMongoRepository;


    /**
     * @param countryId
     * @param
     * @param assetTypeDto contain asset data ,and list of sub asset types
     * @return asset type object
     * @throws DuplicateDataException if asset type is already present with same name
     * @description method create Asset type if sub Asset Types if present then create and add sub Asset Types to Asset type.
     */
    public AssetTypeDTO createAssetTypeAndAddSubAssetTypes(Long countryId, AssetTypeDTO assetTypeDto) {


        AssetType previousAssetType = assetTypeMongoRepository.findByNameAndCountryId(countryId, assetTypeDto.getName());
        if (Optional.ofNullable(previousAssetType).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDto.getName());
        }

        Map<String, Object> subAssetTypes;
        AssetType assetType = new AssetType();
        if (!assetTypeDto.getSubAssetTypes().isEmpty()) {
            subAssetTypes = createNewSubAssetTypesList(countryId, assetTypeDto.getSubAssetTypes());
            assetType.setSubAssetTypes((List<BigInteger>) subAssetTypes.get(IDS_LIST));
            assetType.setHasSubAsset(true);
        }
        assetType.setName(assetTypeDto.getName());
        assetType.setCountryId(countryId);
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
     * @param countryId
     * @param subAssetTypesDto contain list of sub Asset DTOs
     * @return create new Sub Asset type And return  map of Sub Asset types and Ids of Sub Asset types
     */
    public Map<String, Object> createNewSubAssetTypesList(Long countryId, List<AssetTypeDTO> subAssetTypesDto) {

        checkForDuplicacyInNameOfAssetType(subAssetTypesDto);
        List<AssetType> subAssetTypes = new ArrayList<>();
        for (AssetTypeDTO subAssetTypeDto : subAssetTypesDto) {
            AssetType assetType = new AssetType();
            assetType.setCountryId(countryId);
            assetType.setName(subAssetTypeDto.getName());
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
     * @param countryId
     * @param subAssetTypesDto contain list of Existing Sub Asset type which need to we update
     * @return map of Sub asset Types List and Ids (List for rollback)
     * @description this method update existing Sub asset Types and return list of Sub Asset Types and  ids list
     */
    public Map<String, Object> updateSubAssetTypes(Long countryId, List<AssetTypeDTO> subAssetTypesDto) {

        List<BigInteger> subAssetTypesIds = new ArrayList<>();
        Map<BigInteger, AssetTypeDTO> subAssetTypeDtoCorrespondingToIds = new HashMap<>();
        subAssetTypesDto.forEach(subAssetTypeDto -> {
            subAssetTypesIds.add(subAssetTypeDto.getId());
            subAssetTypeDtoCorrespondingToIds.put(subAssetTypeDto.getId(), subAssetTypeDto);
        });
        List<AssetType> subAssetTypesList = assetTypeMongoRepository.findAllAssetTypeByIds(countryId, subAssetTypesIds);
        subAssetTypesList.forEach(subAssetType -> {

            AssetTypeDTO subAssetTypeDto = subAssetTypeDtoCorrespondingToIds.get(subAssetType.getId());
            subAssetType.setCountryId(countryId);
            subAssetType.setName(subAssetTypeDto.getName());
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
     * @param countryId
     * @param
     * @return return list of Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public List<AssetTypeResponseDTO> getAllAssetType(Long countryId) {
        return assetTypeMongoRepository.getAllCountryAssetTypesWithSubAssetTypes(countryId);
    }


    /**
     * @param countryId
     * @param
     * @return return Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public AssetTypeResponseDTO getAssetTypeById(Long countryId, BigInteger id) {
        AssetTypeResponseDTO assetType = assetTypeMongoRepository.getCountryAssetTypesWithSubAssetTypes(countryId, id);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset Type", id);
        }
        return assetType;

    }


    public Boolean deleteAssetType(Long countryId, BigInteger assetTypeId) {
        AssetType existingAssetType = assetTypeMongoRepository.findByIdAndNonDeleted(countryId, assetTypeId);
        if (!Optional.ofNullable(existingAssetType).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + assetTypeId);
        }
        delete(existingAssetType);
        return true;

    }


    /**
     * @param countryId
     * @param
     * @param id           id of Asset Type to which Sub Asset Types Link.
     * @param assetTypeDto asset type Dto contain list of Existing sub Asset types which need to be update and New SubAsset Types  which we need to create and add to asset afterward.
     * @return Asset Type with updated Sub Asset and new Sub Asset Types
     * @throws DuplicateDataException if Asset type is already present with same name .
     * @description method simply (update already exit Sub asset types if id is present)and (add create new sub asset types if id is not present in sub asset types)
     */
    public AssetTypeDTO updateAssetTypeUpdateAndCreateNewSubAssetsAndAddToAssetType(Long countryId, BigInteger id, AssetTypeDTO assetTypeDto) {
        AssetType assetType = assetTypeMongoRepository.findByNameAndCountryId(countryId, assetTypeDto.getName());
        if (Optional.ofNullable(assetType).isPresent() && !id.equals(assetType.getId())) {
            throw new DuplicateDataException("data  exist for  " + assetTypeDto.getName());
        }

        assetType = assetTypeMongoRepository.findByIdAndNonDeleted(countryId, id);
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
            newSubAssetTypes = createNewSubAssetTypesList(countryId, newSubAssetTypesList);
            updatedAndNewSubAssetTypeIds.addAll((List<BigInteger>) newSubAssetTypes.get(IDS_LIST));
        }
        if (updateExistingSubAssetTypes.size() != 0) {
            updatedSubAssetTypes = updateSubAssetTypes(countryId, updateExistingSubAssetTypes);
            updatedAndNewSubAssetTypeIds.addAll((List<BigInteger>) updatedSubAssetTypes.get(IDS_LIST));
        }

        try {
            assetType.setSubAssetTypes(updatedAndNewSubAssetTypeIds);
            assetType = assetTypeMongoRepository.save(assetType);
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

    /**
     * @param countryId
     * @param
     * @param name      name of asset types
     * @return return basic object of asset type
     * @throws DataNotExists if Asset type not found for given name
     */
    public AssetType getAssetTypeByName(Long countryId, String name) {
        if (!StringUtils.isBlank(name)) {
            AssetType exist = assetTypeMongoRepository.findByNameAndCountryId(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


    /**
     * @param assetTypeDTOs check for duplicates in name of Asset types
     */
    public void checkForDuplicacyInNameOfAssetType(List<AssetTypeDTO> assetTypeDTOs) {
        List<String> names = new ArrayList<>();
        for (AssetTypeDTO assetTypeDTO : assetTypeDTOs) {
            if (names.contains(assetTypeDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDTO.getName());
            }
            names.add(assetTypeDTO.getName().toLowerCase());
        }
    }


}
