package com.kairos.service.master_data_management.asset_management;

import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.dto.master_data.AssetTypeDto;
import com.kairos.persistance.model.master_data_management.asset_management.AssetType;
import com.kairos.persistance.repository.master_data_management.asset_management.AssetTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.userContext.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constant.AppConstant.IDS_LIST;
import static com.kairos.constant.AppConstant.ASSET_TYPES_LIST;


@Service
public class AssetTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetTypeService.class);


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;

    //Todo  if requirement is to create single asset with multiple sub asset then remove this method
    public Map<String, List<AssetType>> createAssetType(Long countryId, List<AssetType> assetTypes) {
        Map<String, List<AssetType>> result = new HashMap<>();
        List<AssetType> existing = new ArrayList<>();
        Set<String> namesInLowercase = new HashSet<>();
        List<AssetType> newAssetTypes = new ArrayList<>();
        if (assetTypes.size() != 0) {
            for (AssetType AssetType : assetTypes) {
                if (!StringUtils.isBlank(AssetType.getName())) {
                    namesInLowercase.add(AssetType.getName().trim().toLowerCase());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            existing = assetTypeMongoRepository.findByCountryAndNameList(countryId, namesInLowercase);
            existing.forEach(item -> namesInLowercase.remove(item.getNameInLowerCase()));
            for (AssetType AssetType : assetTypes)
                if (namesInLowercase.contains(AssetType.getName().toLowerCase().trim())) {
                    newAssetTypes.add(AssetType);

                }
        }
        List<AssetType> assetTypeList = new ArrayList<>();
        if (newAssetTypes.size() != 0) {
            for (AssetType AssetType : newAssetTypes) {

                AssetType newAssetType = new AssetType();
                newAssetType.setName(AssetType.getName());
                newAssetType.setNameInLowerCase(AssetType.getName().toLowerCase().trim());
                newAssetType.setCountryId(countryId);
                assetTypeList.add(newAssetType);

            }
            assetTypeList = save(assetTypeList);
        }
        result.put("existing", existing);
        result.put("new", assetTypeList);
        return result;

    }


    //Todo if requiremenet is to create single Asset type with multiple Sub asset use this
    public AssetType createAssetTypeAndAddSubAssetTypes(Long countryId, AssetTypeDto assetTypeDto) {


        AssetType exist = assetTypeMongoRepository.findByName(countryId, assetTypeDto.getName().toLowerCase());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDto.getName());
        }

        Map<String, Object> subAssetTypes = new HashMap<>();
        AssetType assetType = new AssetType();
        if (assetTypeDto.getSubAssetTypes().size() != 0) {
            subAssetTypes = buildSubAssetTypeAndAddToAssetType(countryId, assetTypeDto.getSubAssetTypes());
            assetType.setSubAssetTypes((List<BigInteger>) subAssetTypes.get(IDS_LIST));
        }
        assetType.setName(assetTypeDto.getName());
        assetType.setCountryId(countryId);
        assetType.setNameInLowerCase(assetTypeDto.getName().toLowerCase());
        try {
            assetType = save(assetType);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return assetType;
    }


    public List<AssetType> getAllAssetType() {
        return assetTypeMongoRepository.findAllAssetTypes(UserContext.getCountryId());
    }


    public AssetType getAssetType(Long countryId, BigInteger id) {

        AssetType exist = assetTypeMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteAssetType(Long countryId, BigInteger id) {
        AssetType exist = assetTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        }
        exist.setDeleted(true);
        if (exist.getSubAssetTypes().size() != 0) {
            List<AssetType> subAssetTypes = assetTypeMongoRepository.findAllAssetTypesbyIds(countryId, exist.getSubAssetTypes());
            assetTypeMongoRepository.deleteAll(subAssetTypes);
        }
        save(exist);
        return true;
    }


    public AssetType updateAssetType(BigInteger id, AssetType AssetType) {
        AssetType exist = assetTypeMongoRepository.findByName(UserContext.getCountryId(), AssetType.getName().toLowerCase());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + AssetType.getName());
        } else {
            exist = assetTypeMongoRepository.findByid(id);
            exist.setName(AssetType.getName());
            return save(exist);

        }
    }


    public AssetType getAssetTypeByName(Long countryId, String name) {
        if (!StringUtils.isBlank(name)) {
            AssetType exist = assetTypeMongoRepository.findByName(countryId, name);
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
    public Map<String, Object> buildSubAssetTypeAndAddToAssetType(Long countryId, List<AssetTypeDto> subAssetTypesDto) {

        checkForDuplicacyInNameOfAssetType(subAssetTypesDto);
        List<AssetType> subAssetTypes = new ArrayList<>();
        for (AssetTypeDto subAssetTypeDto : subAssetTypesDto) {
            AssetType assetType = new AssetType();
            assetType.setCountryId(countryId);
            assetType.setName(subAssetTypeDto.getName());
            assetType.setSubAsset(true);
            assetType.setNameInLowerCase(subAssetTypeDto.getName().toLowerCase());
            subAssetTypes.add(assetType);
        }
        Map<String, Object> result = new HashMap<>();
        List<BigInteger> subAssetTypesIds = new ArrayList<>();
        try {
            subAssetTypes = save(subAssetTypes);
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
     * @param assetTypeDtos check for duplicacy in name of Asset types
     */
    public void checkForDuplicacyInNameOfAssetType(List<AssetTypeDto> assetTypeDtos) {
        List<String> names = new ArrayList<>();
        for (AssetTypeDto assetTypeDto : assetTypeDtos) {
            if (names.contains(assetTypeDto.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "Asset Type", assetTypeDto.getName());
            }
            names.add(assetTypeDto.getName().toLowerCase());
        }
    }


}
