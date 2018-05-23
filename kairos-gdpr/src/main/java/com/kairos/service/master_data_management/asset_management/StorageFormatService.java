package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.StorageFormat;
import com.kairos.persistance.repository.master_data_management.asset_management.StorageFormatMongoRepository;
import com.kairos.service.MongoBaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class StorageFormatService extends MongoBaseService {


    @Inject
    private StorageFormatMongoRepository storageFormatMongoRepository;

    public Map<String, List<StorageFormat>> createStorageFormat(List<StorageFormat> storageFormats) {
        Map<String, List<StorageFormat>> result = new HashMap<>();
        List<StorageFormat> existing = new ArrayList<>();
        List<StorageFormat> newStorageFormats = new ArrayList<>();
        if (storageFormats.size() != 0) {
            for (StorageFormat storageFormat : storageFormats) {
                if (!StringUtils.isBlank(storageFormat.getName())) {
                    StorageFormat exist = storageFormatMongoRepository.findByName(storageFormat.getName());
                    if (Optional.ofNullable(exist).isPresent()) {
                        existing.add(exist);

                    } else {
                        StorageFormat newStorageFormat = new StorageFormat();
                        newStorageFormat.setName(storageFormat.getName());
                        newStorageFormats.add(save(newStorageFormat));
                    }
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }

            result.put("existing", existing);
            result.put("new", newStorageFormats);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    public List<StorageFormat> getAllStorageFormat() {
        return storageFormatMongoRepository.findAllStorageFormats();
    }


    public StorageFormat getStorageFormat(BigInteger id) {

        StorageFormat exist = storageFormatMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteStorageFormat(BigInteger id) {

        StorageFormat exist = storageFormatMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public StorageFormat updateStorageFormat(BigInteger id, StorageFormat storageFormat) {

        StorageFormat exist = storageFormatMongoRepository.findByIdAndNonDeleted(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setName(storageFormat.getName());
            return save(exist);

        }
    }


    public StorageFormat getStorageFormatByName(String name) {


        if (!StringUtils.isBlank(name)) {
            StorageFormat exist = storageFormatMongoRepository.findByName(name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}
