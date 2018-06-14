package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.AssetType;
import com.kairos.persistance.repository.master_data_management.asset_management.AssetTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class AssetTypeService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetTypeService.class);

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;


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

        List<AssetType> sotrageList=new ArrayList<>();
        if (newAssetTypes.size() != 0) {
            for (AssetType AssetType : newAssetTypes) {

                AssetType newAssetType = new AssetType();
                newAssetType.setName(AssetType.getName());
                newAssetType.setNameInLowerCase(AssetType.getName().toLowerCase().trim());
                newAssetType.setCountryId(countryId);
                sotrageList.add(newAssetType);

            }

            sotrageList = save(sotrageList);
        }
        result.put("existing", existing);
        result.put("new", sotrageList);
        return result;

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


    public Boolean deleteAssetType(BigInteger id) {
        AssetType exist = assetTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public AssetType updateAssetType(BigInteger id, AssetType AssetType) {

        AssetType exist = assetTypeMongoRepository.findByName(UserContext.getCountryId(), AssetType.getName().toLowerCase());
        if (Optional.ofNullable(exist).isPresent()) {
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


}
