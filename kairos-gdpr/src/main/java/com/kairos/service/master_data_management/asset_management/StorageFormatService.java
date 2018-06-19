package com.kairos.service.master_data_management.asset_management;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.StorageFormat;
import com.kairos.persistance.repository.master_data_management.asset_management.StorageFormatMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import jdk.nashorn.internal.runtime.options.Option;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class StorageFormatService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageFormatService.class);

    @Inject
    private StorageFormatMongoRepository storageFormatMongoRepository;

    public Map<String, List<StorageFormat>> createStorageFormat(Long countryId, List<StorageFormat> storageFormats) {
        Map<String, List<StorageFormat>> result = new HashMap<>();
        List<StorageFormat> existing = new ArrayList<>();
        Set<String> names = new HashSet<>();
        List<StorageFormat> newStorageFormats = new ArrayList<>();
        if (storageFormats.size() != 0) {
            for (StorageFormat storageFormat : storageFormats) {
                if (!StringUtils.isBlank(storageFormat.getName())) {
                    names.add(storageFormat.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            existing = storageFormatMongoRepository.findByCountryAndNameList(countryId, names);
            existing.forEach(item -> names.remove(item.getName()));
            if (names.size() != 0) {
                for (String name : names) {

                    StorageFormat newStorageFormat = new StorageFormat();
                    newStorageFormat.setName(name);
                    newStorageFormat.setCountryId(countryId);
                    newStorageFormats.add(newStorageFormat);

                }


                newStorageFormats = save(newStorageFormats);
            }
            result.put("existing", existing);
            result.put("new", newStorageFormats);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    public List<StorageFormat> getAllStorageFormat() {
        return storageFormatMongoRepository.findAllStorageFormats(UserContext.getCountryId());
    }


    public StorageFormat getStorageFormat(Long countryId, BigInteger id) {

        StorageFormat exist = storageFormatMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteStorageFormat(BigInteger id) {

        StorageFormat exist = storageFormatMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public StorageFormat updateStorageFormat(BigInteger id, StorageFormat storageFormat) {

        StorageFormat exist = storageFormatMongoRepository.findByName(UserContext.getCountryId(), storageFormat.getName());
        if (Optional.ofNullable(exist).isPresent() && !id.equals(exist.getId())) {
            throw new DuplicateDataException("data  exist for  " + storageFormat.getName());
        } else {
            exist = storageFormatMongoRepository.findByid(id);
            exist.setName(storageFormat.getName());
            return save(exist);

        }
    }


    public StorageFormat getStorageFormatByName(Long countryId, String name) {


        if (!StringUtils.isBlank(name)) {
            StorageFormat exist = storageFormatMongoRepository.findByName(countryId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}
