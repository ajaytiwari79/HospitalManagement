package com.kairos.service.data_inventory.asset;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.StorageFormatDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.StorageFormat;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.storage_format.StorageFormatMongoRepository;
import com.kairos.response.dto.common.StorageFormatResponseDTO;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.StorageFormatService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class OrganizationStorageFormatService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationStorageFormatService.class);

    @Inject
    private StorageFormatMongoRepository storageFormatMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private StorageFormatService storageFormatService;

    @Inject
    private AssetMongoRepository assetMongoRepository;


    /**
     * @param
     * @param organizationId
     * @param storageFormatDTOS
     * @return return map which contain list of new StorageFormat and list of existing StorageFormat if StorageFormat already exist
     * @description this method create new StorageFormat if StorageFormat not exist with same name ,
     * and if exist then simply add  StorageFormat to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing StorageFormat using collation ,used for case insensitive result
     */
    public Map<String, List<StorageFormat>> createStorageFormat(Long organizationId, List<StorageFormatDTO> storageFormatDTOS) {

        Map<String, List<StorageFormat>> result = new HashMap<>();
        Set<String> storageFormatNames = new HashSet<>();
        if (!storageFormatDTOS.isEmpty()) {
            for (StorageFormatDTO storageFormat : storageFormatDTOS) {
                storageFormatNames.add(storageFormat.getName());
            }
            List<StorageFormat> existing = findMetaDataByNameAndUnitId(organizationId, storageFormatNames, StorageFormat.class);
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
        return storageFormatMongoRepository.findAllOrganizationStorageFormats(organizationId, new Sort(Sort.Direction.DESC, "createdAt"));
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


    public Boolean deleteStorageFormat(Long unitId, BigInteger storageFormatId) {

        List<AssetBasicResponseDTO> assetsLinkedWithStorageFormat = assetMongoRepository.findAllAssetLinkedWithStorageFormat(unitId, storageFormatId);
        if (CollectionUtils.isNotEmpty(assetsLinkedWithStorageFormat)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "Storage Format", new StringBuilder(assetsLinkedWithStorageFormat.stream().map(AssetBasicResponseDTO::getName).map(String::toString).collect(Collectors.joining(","))));
        }
        storageFormatMongoRepository.safeDelete(storageFormatId);
        return true;
    }


    /**
     * @param
     * @param organizationId
     * @param id               id of StorageFormat
     * @param storageFormatDTO
     * @return StorageFormat updated object
     * @throws DuplicateDataException throw exception if data not exist for given id
     */
    public StorageFormatDTO updateStorageFormat(Long organizationId, BigInteger id, StorageFormatDTO storageFormatDTO) {

        StorageFormat storageFormat = storageFormatMongoRepository.findByOrganizationIdAndName(organizationId, storageFormatDTO.getName());
        if (Optional.ofNullable(storageFormat).isPresent()) {
            if (id.equals(storageFormat.getId())) {
                return storageFormatDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Storage Format", storageFormat.getName());
        }
        storageFormat = storageFormatMongoRepository.findByid(id);
        if (!Optional.ofNullable(storageFormat).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Storage Format", id);
        }
        storageFormat.setName(storageFormatDTO.getName());
        storageFormatMongoRepository.save(storageFormat);
        return storageFormatDTO;

    }


    public Map<String, List<StorageFormat>> saveAndSuggestStorageFormats(Long countryId, Long organizationId, List<StorageFormatDTO> storageFormatDTOS) {

        Map<String, List<StorageFormat>> result;
        result = createStorageFormat(organizationId, storageFormatDTOS);
        List<StorageFormat> masterStorageFormatSuggestedByUnit = storageFormatService.saveSuggestedStorageFormatsFromUnit(countryId, storageFormatDTOS);
        if (!masterStorageFormatSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterStorageFormatSuggestedByUnit);
        }
        return result;
    }

}
