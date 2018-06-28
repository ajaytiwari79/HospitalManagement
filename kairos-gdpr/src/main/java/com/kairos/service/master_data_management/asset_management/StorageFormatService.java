package com.kairos.service.master_data_management.asset_management;


import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data_management.asset_management.StorageFormat;
import com.kairos.persistance.repository.master_data_management.asset_management.StorageFormatMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import com.kairos.utils.userContext.UserContext;
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

    @Inject
    private ComparisonUtils  comparisonUtils;

    public Map<String, List<StorageFormat>> createStorageFormat(Long countryId, Long organizationId, List<StorageFormat> storageFormats) {

        Map<String, List<StorageFormat>> result = new HashMap<>();
        Set<String> names = new HashSet<>();
        List<StorageFormat> newStorageFormats = new ArrayList<>();
        if (storageFormats.size() != 0) {
            for (StorageFormat storageFormat : storageFormats) {
                if (!StringUtils.isBlank(storageFormat.getName())) {
                    names.add(storageFormat.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<StorageFormat> existing =  findByNamesList(countryId,organizationId,names,StorageFormat.class);
            if (existing.size() != 0) {
                Set<String> existingNames = new HashSet<>();
                existing.forEach(storageFormat -> {
                    existingNames.add(storageFormat.getName());
                });
                names = comparisonUtils.checkForExistingObjectAndRemoveFromList(names, existingNames);
            }
            if (names.size() != 0) {
                for (String name : names) {

                    StorageFormat newStorageFormat = new StorageFormat();
                    newStorageFormat.setName(name);
                    newStorageFormat.setCountryId(countryId);
                    newStorageFormat.setOrganizationId(organizationId);
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


    public List<StorageFormat> getAllStorageFormat(Long countryId, Long organizationId) {
        return storageFormatMongoRepository.findAllStorageFormats(countryId, organizationId);
    }


    public StorageFormat getStorageFormat(Long countryId, Long organizationId, BigInteger id) {

        StorageFormat exist = storageFormatMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteStorageFormat(Long countryId, Long organizationId, BigInteger id) {

        StorageFormat exist = storageFormatMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            exist.setDeleted(true);
            save(exist);
            return true;

        }
    }


    public StorageFormat updateStorageFormat(Long countryId, Long organizationId, BigInteger id, StorageFormat storageFormat) {

        StorageFormat exist = storageFormatMongoRepository.findByNameAndCountryId(countryId, organizationId, storageFormat.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + storageFormat.getName());
        } else {
            exist = storageFormatMongoRepository.findByid(id);
            exist.setName(storageFormat.getName());
            return save(exist);

        }
    }


    public StorageFormat getStorageFormatByName(Long countryId, Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            StorageFormat exist = storageFormatMongoRepository.findByNameAndCountryId(countryId, organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }


}
