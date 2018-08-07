package com.kairos.service.data_inventory.asset;

import com.kairos.custom_exception.DataNotExists;
import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.default_asset_setting.StorageFormat;
import com.kairos.persistance.repository.master_data.asset_management.storage_format.StorageFormatMongoRepository;
import com.kairos.response.dto.common.StorageFormatResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class OrganizationStorageFormatService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationStorageFormatService.class);

    @Inject
    private StorageFormatMongoRepository storageFormatMongoRepository;


    /**
     * @param
     * @param organizationId
     * @param storageFormats
     * @return return map which contain list of new StorageFormat and list of existing StorageFormat if StorageFormat already exist
     * @description this method create new StorageFormat if StorageFormat not exist with same name ,
     * and if exist then simply add  StorageFormat to existing list and return list ;
     * findByNamesAndCountryId()  return list of existing StorageFormat using collation ,used for case insensitive result
     */
    public Map<String, List<StorageFormat>> createStorageFormat(Long organizationId, List<StorageFormat> storageFormats) {

        Map<String, List<StorageFormat>> result = new HashMap<>();
        Set<String> storageFormatNames = new HashSet<>();
        if (!storageFormats.isEmpty()) {
            for (StorageFormat storageFormat : storageFormats) {
                if (!StringUtils.isBlank(storageFormat.getName())) {
                    storageFormatNames.add(storageFormat.getName());
                } else
                    throw new InvalidRequestException("name could not be empty or null");
            }
            List<StorageFormat> existing = findAllByNameAndOrganizationId(organizationId, storageFormatNames, StorageFormat.class);
            storageFormatNames = ComparisonUtils.getNameListForMetadata(existing, storageFormatNames);

            List<StorageFormat> newStorageFormats = new ArrayList<>();
            if (!storageFormatNames.isEmpty()) {
                for (String name : storageFormatNames) {

                    StorageFormat newStorageFormat = new StorageFormat(name);
                    newStorageFormat.setOrganizationId(organizationId);
                    newStorageFormats.add(newStorageFormat);

                }


                newStorageFormats = storageFormatMongoRepository.saveAll(getNextSequence(newStorageFormats));
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newStorageFormats);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     * @param
     * @param organizationId
     * @return list of StorageFormat
     */
    public List<StorageFormatResponseDTO> getAllStorageFormat(Long organizationId) {
        return storageFormatMongoRepository.findAllOrganizationStorageFormats(organizationId);
    }

    /**
     * @param
     * @param organizationId
     * @param id             id of StorageFormat
     * @return StorageFormat object fetch via given id
     * @throws DataNotFoundByIdException throw exception if StorageFormat not exist for given id
     */
    public StorageFormat getStorageFormat(Long organizationId, BigInteger id) {

        StorageFormat exist = storageFormatMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteStorageFormat(Long organizationId, BigInteger id) {

        StorageFormat exist = storageFormatMongoRepository.findByOrganizationIdAndId(organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            delete(exist);
            return true;

        }
    }


    /**
     * @param
     * @param organizationId
     * @param id             id of StorageFormat
     * @param storageFormat
     * @return StorageFormat updated object
     * @throws DuplicateDataException throw exception if data not exist for given id
     */
    public StorageFormat updateStorageFormat(Long organizationId, BigInteger id, StorageFormat storageFormat) {

        StorageFormat exist = storageFormatMongoRepository.findByOrganizationIdAndName(organizationId, storageFormat.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            if (id.equals(exist.getId())) {
                return exist;
            }
            throw new DuplicateDataException("data  exist for  " + storageFormat.getName());
        } else {
            exist = storageFormatMongoRepository.findByid(id);
            exist.setName(storageFormat.getName());
            return storageFormatMongoRepository.save(getNextSequence(exist));

        }
    }

    /**
     * @param
     * @param organizationId
     * @param name           name of StorageFormat
     * @return StorageFormat object fetch on basis of  name
     * @throws DataNotExists throw exception if StorageFormat not exist for given name
     */
    public StorageFormat getStorageFormatByName(Long organizationId, String name) {


        if (!StringUtils.isBlank(name)) {
            StorageFormat exist = storageFormatMongoRepository.findByOrganizationIdAndName(organizationId, name);
            if (!Optional.ofNullable(exist).isPresent()) {
                throw new DataNotExists("data not exist for name " + name);
            }
            return exist;
        } else
            throw new InvalidRequestException("request param cannot be empty  or null");

    }

}
