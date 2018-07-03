package com.kairos.service.master_data_management.asset_management;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.master_data.AssetTypeDTO;
import com.kairos.persistance.model.master_data_management.asset_management.AssetType;
import com.kairos.persistance.repository.master_data_management.asset_management.AssetTypeMongoRepository;
import com.kairos.response.dto.master_data.AssetTypeResponseDto;
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

    public AssetType createAssetTypeAndAddSubAssetTypes(Long countryId, Long organizationId, AssetTypeDTO assetTypeDto) {


        AssetType exist = assetTypeMongoRepository.findByName(countryId, organizationId, assetTypeDto.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDto.getName());
        }

        Map<String, Object> subAssetTypes = new HashMap<>();
        AssetType assetType = new AssetType();
        if (assetTypeDto.getSubAssetTypes().size() != 0) {
            subAssetTypes = createNewSubAssetTypesList(countryId, organizationId, assetTypeDto.getSubAssetTypes());
            assetType.setSubAssetTypes((List<BigInteger>) subAssetTypes.get(IDS_LIST));
            assetType.setSubAsset(true);
        }
        assetType.setName(assetTypeDto.getName());
        assetType.setCountryId(countryId);
        assetType.setOrganizationId(organizationId);
        try {
            assetType = assetTypeMongoRepository.save(save(assetType));
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return assetType;
    }

    /**
     * @param countryId
     * @param subAssetTypesDto list of sub asset types needed to create new Sub asset types
     * @return
     */
    public Map<String, Object> createNewSubAssetTypesList(Long countryId, Long organizationId, List<AssetTypeDTO> subAssetTypesDto) {

        checkForDuplicacyInNameOfAssetType(subAssetTypesDto);
        List<AssetType> subAssetTypes = new ArrayList<>();
        for (AssetTypeDTO subAssetTypeDto : subAssetTypesDto) {
            AssetType assetType = new AssetType();
            assetType.setCountryId(countryId);
            assetType.setName(subAssetTypeDto.getName());
            assetType.setOrganizationId(organizationId);
            subAssetTypes.add(assetType);
        }
        Map<String, Object> result = new HashMap<>();
        List<BigInteger> subAssetTypesIds = new ArrayList<>();
        try {
            subAssetTypes = assetTypeMongoRepository.saveAll(save(subAssetTypes));
            subAssetTypes.forEach(subAssetType -> {
                subAssetTypesIds.add(subAssetType.getId());
            });
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
     * @return
     */
    public Map<String, Object> updateSubAssetTypes(Long countryId, Long organizationId, List<AssetTypeDTO> subAssetTypesDto) {

        List<BigInteger> subAssetTypesIds = new ArrayList<>();
        Map<BigInteger, AssetTypeDTO> subAssetTypeDtoCorrespondingToIds = new HashMap<>();
        subAssetTypesDto.forEach(subAssetTypeDto -> {
            subAssetTypesIds.add(subAssetTypeDto.getId());
            subAssetTypeDtoCorrespondingToIds.put(subAssetTypeDto.getId(), subAssetTypeDto);
        });
        List<AssetType> subAssetTypesList = assetTypeMongoRepository.findAllAssetTypesbyIds(countryId, organizationId, subAssetTypesIds);
        subAssetTypesList.forEach(subAssetType -> {

            AssetTypeDTO subAssetTypeDto = subAssetTypeDtoCorrespondingToIds.get(subAssetType.getId());
            subAssetType.setCountryId(countryId);
            subAssetType.setName(subAssetTypeDto.getName());
            subAssetType.setOrganizationId(organizationId);
        });
        Map<String, Object> result = new HashMap<>();
        try {
            subAssetTypesList = assetTypeMongoRepository.saveAll(save(subAssetTypesList));
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        result.put(IDS_LIST, subAssetTypesIds);
        result.put(ASSET_TYPES_LIST, subAssetTypesList);
        return result;
    }


    public List<AssetTypeResponseDto> getAllAssetType(Long countryId, Long organizationId) {
        return assetTypeMongoRepository.getAllAssetTypesWithSubAssetTypes(countryId, organizationId);
    }


    public AssetTypeResponseDto getAssetTypeById(Long countryId, Long organizationId, BigInteger id) {
        return assetTypeMongoRepository.getAssetTypesWithSubAssetTypes(countryId, organizationId, id);

    }


    public Boolean deleteAssetType(Long countryId, Long organizationId, BigInteger id) {
        AssetType exist = assetTypeMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        }
        if (Optional.ofNullable(exist.getSubAssetTypes()).isPresent()) {
            List<AssetType> subAssetTypes = assetTypeMongoRepository.findAllAssetTypesbyIds(countryId, organizationId, exist.getSubAssetTypes());
            assetTypeMongoRepository.deleteAll(subAssetTypes);
        }
        delete(exist);
        return true;
    }



    public AssetType updateAssetTypeUpdateAndCreateNewSubAssetsAndAddToAssetType(Long countryId, Long organizationId, BigInteger id, AssetTypeDTO assetTypeDto) {
        AssetType exist = assetTypeMongoRepository.findByName(countryId, organizationId, assetTypeDto.getName());
        if (Optional.ofNullable(exist).isPresent() && !id.equals(exist.getId())) {
            throw new DuplicateDataException("data  exist for  " + assetTypeDto.getName());
        }

        exist = assetTypeMongoRepository.findByIdAndNonDeleted(countryId,organizationId,id);
        exist.setName(assetTypeDto.getName());
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
            newSubAssetTypes = createNewSubAssetTypesList(countryId, organizationId, newSubAssetTypesList);
            updatedAndNewSubAssetTypeIds.addAll((List<BigInteger>) newSubAssetTypes.get(IDS_LIST));
        }
        if (updateExistingSubAssetTypes.size() != 0) {
            updatedSubAssetTypes = updateSubAssetTypes(countryId, organizationId, updateExistingSubAssetTypes);
            updatedAndNewSubAssetTypeIds.addAll((List<BigInteger>) updatedSubAssetTypes.get(IDS_LIST));
        }

        try {
            exist.setSubAssetTypes(updatedAndNewSubAssetTypeIds);
            exist = assetTypeMongoRepository.save(save(exist));
        } catch (Exception e) {
            List<AssetType> subAssetTypes = new ArrayList<>();
            subAssetTypes.addAll((List<AssetType>) newSubAssetTypes.get(ASSET_TYPES_LIST));
            subAssetTypes.addAll((List<AssetType>) updatedSubAssetTypes.get(ASSET_TYPES_LIST));
            assetTypeMongoRepository.deleteAll(subAssetTypes);
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());

        }
        return exist;

    }


    public AssetType getAssetTypeByName(Long countryId, Long organizationId, String name) {
        if (!StringUtils.isBlank(name)) {
            AssetType exist = assetTypeMongoRepository.findByName(countryId, organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }

    /**
     * @param countryId
     * @param subAssetTypesDto contains list of Sub Asset Types
     * @return
     */

    /**
     * @param assetTypeDtos check for duplicacy in name of Asset types
     */
    public void checkForDuplicacyInNameOfAssetType(List<AssetTypeDTO> assetTypeDtos) {
        List<String> names = new ArrayList<>();
        for (AssetTypeDTO assetTypeDto : assetTypeDtos) {
            if (names.contains(assetTypeDto.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDto.getName());
            }
            names.add(assetTypeDto.getName().toLowerCase());
        }
    }


}
