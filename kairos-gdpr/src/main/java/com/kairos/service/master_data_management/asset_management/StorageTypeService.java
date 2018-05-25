package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.StorageType;
import com.kairos.persistance.repository.master_data_management.asset_management.StorageTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class StorageTypeService extends MongoBaseService {
    @Inject
    private StorageTypeMongoRepository storageTypeMongoRepository;


    public Map<String, List<StorageType>> createStorageType(Long countryId,List<StorageType> storageTypes) {
        Map<String, List<StorageType>> result = new HashMap<>();
        List<StorageType> existing = new ArrayList<>();
        List<StorageType> newStorageTypes = new ArrayList<>();
        if (storageTypes.size() != 0) {
            for (StorageType storageType : storageTypes) {
                if (!StringUtils.isBlank(storageType.getName())) {
                    StorageType exist = storageTypeMongoRepository.findByName(countryId,storageType.getName());
                    if (Optional.ofNullable(exist).isPresent()) {
                        existing.add(exist);
                    } else {
                        StorageType newStorageType = new StorageType();
                        newStorageType.setName(storageType.getName());
                        newStorageType.setCountryId(countryId);
                        newStorageTypes.add(save(newStorageType));
                    }
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            result.put("existing", existing);
            result.put("new", newStorageTypes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }

    public List<StorageType> getAllStorageType() {
        return storageTypeMongoRepository.findAllStorageTypes(UserContext.getCountryId());
           }


    public StorageType getStorageType(Long countryId,BigInteger id) {

        StorageType exist = storageTypeMongoRepository.findByIdAndNonDeleted(countryId,id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteStorageType(BigInteger id) {
        StorageType exist = storageTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public StorageType updateStorageType(BigInteger id, StorageType storageType) {

        StorageType exist = storageTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setName(storageType.getName());
            return save(exist);

        }
    }


    public StorageType getStorageTypeByName(Long countryId,String name) {


        if (!StringUtils.isBlank(name)) {
            StorageType exist = storageTypeMongoRepository.findByName(countryId,name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}
