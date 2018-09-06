package com.kairos.service.master_data.asset_management;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.gdpr.data_inventory.RiskDTO;
import com.kairos.gdpr.master_data.AssetTypeDTO;
import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistance.model.risk_management.Risk;
import com.kairos.persistance.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistance.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.risk_management.RiskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

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

    @Inject
    private RiskService riskService;


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
        AssetType assetType = new AssetType(assetTypeDto.getName(), countryId);
        Map<AssetType, List<RiskDTO>> riskRelatedToAssetTypeAndSubAssetType = new HashMap<>();
        List<AssetType> subAssetTypeList = new ArrayList<>();
        if (!assetTypeDto.getRisk().isEmpty()) {
            riskRelatedToAssetTypeAndSubAssetType.put(assetType, assetTypeDto.getRisk());
        }
        if (!assetTypeDto.getSubAssetTypes().isEmpty()) {
            subAssetTypeList = buildSubAssetTypesListAndRiskAndLinkedToAssetType(countryId, assetTypeDto.getSubAssetTypes(), riskRelatedToAssetTypeAndSubAssetType);
            assetType.setHasSubAsset(true);
        }
        Map<AssetType, List<BigInteger>> riskIdsCoresspondingToAssetAndSubAssetType = new HashMap<>();
        if (!riskRelatedToAssetTypeAndSubAssetType.isEmpty()) {
            riskIdsCoresspondingToAssetAndSubAssetType = riskService.saveRiskAtCountryLevel(countryId, riskRelatedToAssetTypeAndSubAssetType);
        }
        for (AssetType subAssetType : subAssetTypeList) {
            subAssetType.setRisks(riskIdsCoresspondingToAssetAndSubAssetType.get(subAssetType));
        }
        assetTypeMongoRepository.saveAll(getNextSequence(subAssetTypeList));
        List<BigInteger> subAssetTypeIds = subAssetTypeList.stream().map(AssetType::getId).collect(Collectors.toList());
        assetType.setRisks(riskIdsCoresspondingToAssetAndSubAssetType.get(assetType));
        assetType.setSubAssetTypes(subAssetTypeIds);
        assetTypeMongoRepository.save(assetType);
        assetTypeDto.setId(assetType.getId());
        return assetTypeDto;
    }

    /**
     * @param countryId
     * @param subAssetTypesDto contain list of sub Asset DTOs
     * @return create new Sub Asset type ids
     */
    public List<AssetType> buildSubAssetTypesListAndRiskAndLinkedToAssetType(Long countryId, List<AssetTypeDTO> subAssetTypesDto, Map<AssetType, List<RiskDTO>> riskRelatedToSubAssetTypes) {

        checkForDuplicacyInNameOfAssetType(subAssetTypesDto);
        List<AssetType> subAssetTypes = new ArrayList<>();
        for (AssetTypeDTO subAssetTypeDto : subAssetTypesDto) {
            AssetType assetSubType = new AssetType(subAssetTypeDto.getName(), countryId);
            assetSubType.setSubAsset(true);
            if (!subAssetTypeDto.getRisk().isEmpty()) {
                riskRelatedToSubAssetTypes.put(assetSubType, subAssetTypeDto.getRisk());
            }
            subAssetTypes.add(assetSubType);
        }
        return subAssetTypes;
    }


    /**
     * @param countryId
     * @param subAssetTypesDto contain list of Existing Sub Asset type which need to we update
     * @return map of Sub asset Types List and Ids (List for rollback)
     * @description this method update existing Sub asset Types and return list of Sub Asset Types and  ids list
     */
    private List<AssetType> updateSubAssetTypes(Long countryId, List<AssetTypeDTO> subAssetTypesDto, Map<AssetType, List<RiskDTO>> riskRelatedToSubAssetTypes) {

        List<BigInteger> subAssetTypesIds = new ArrayList<>();
        Map<BigInteger, AssetTypeDTO> subAssetTypeDtoCorrespondingToIds = new HashMap<>();
        subAssetTypesDto.forEach(subAssetTypeDto -> {
            subAssetTypesIds.add(subAssetTypeDto.getId());
            subAssetTypeDtoCorrespondingToIds.put(subAssetTypeDto.getId(), subAssetTypeDto);
        });
        List<AssetType> subAssetTypesList = assetTypeMongoRepository.findAllAssetTypeByIds(countryId, subAssetTypesIds);
        subAssetTypesList.forEach(subAssetType -> {
            AssetTypeDTO subAssetTypeDto = subAssetTypeDtoCorrespondingToIds.get(subAssetType.getId());
            if (!subAssetTypeDto.getRisk().isEmpty()) {
                riskRelatedToSubAssetTypes.put(subAssetType, subAssetTypeDto.getRisk());
            }
            subAssetType.setName(subAssetTypeDto.getName());
        });
        return subAssetTypesList;
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
            exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDto.getName());
        }
        assetType = assetTypeMongoRepository.findByIdAndNonDeleted(countryId, id);
        assetType.setName(assetTypeDto.getName());
        List<AssetTypeDTO> newSubAssetTypeDTOs = new ArrayList<>();
        List<AssetTypeDTO> updateExistingSubAssetTypeDTOs = new ArrayList<>();
        assetTypeDto.getSubAssetTypes().forEach(subAssetTypeDto -> {
            if (Optional.ofNullable(subAssetTypeDto.getId()).isPresent()) {
                updateExistingSubAssetTypeDTOs.add(subAssetTypeDto);
            } else {
                newSubAssetTypeDTOs.add(subAssetTypeDto);
            }
        });
        Map<AssetType, List<RiskDTO>> riskRelatedToAssetTypeAndSubAssetType = new HashMap<>();
        List<AssetType> subAssetTypeList = new ArrayList<>();
        if (!assetTypeDto.getRisk().isEmpty()) {
            riskRelatedToAssetTypeAndSubAssetType.put(assetType, assetTypeDto.getRisk());
        }
        if (!newSubAssetTypeDTOs.isEmpty()) {
            subAssetTypeList.addAll(buildSubAssetTypesListAndRiskAndLinkedToAssetType(countryId, newSubAssetTypeDTOs, riskRelatedToAssetTypeAndSubAssetType));
        }
        if (!updateExistingSubAssetTypeDTOs.isEmpty()) {
            subAssetTypeList.addAll(updateSubAssetTypes(countryId, updateExistingSubAssetTypeDTOs, riskRelatedToAssetTypeAndSubAssetType));
        }
        Map<AssetType, List<BigInteger>> riskIdsRelatedToSubAssetTypeOrAssetType = new HashMap<>();
        if (!riskRelatedToAssetTypeAndSubAssetType.isEmpty()) {
            riskIdsRelatedToSubAssetTypeOrAssetType = riskService.saveRiskAtCountryLevel(countryId, riskRelatedToAssetTypeAndSubAssetType);
        }
        for (AssetType subAssetType : subAssetTypeList) {
            subAssetType.setRisks(riskIdsRelatedToSubAssetTypeOrAssetType.get(subAssetType));
        }
        assetType.setRisks(riskIdsRelatedToSubAssetTypeOrAssetType.get(assetType));
        assetTypeMongoRepository.saveAll(getNextSequence(subAssetTypeList));
        List<BigInteger> subAssetTypeIds = subAssetTypeList.stream().map(AssetType::getId).collect(Collectors.toList());
        assetType.setSubAssetTypes(subAssetTypeIds);
        assetTypeMongoRepository.save(assetType);
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
