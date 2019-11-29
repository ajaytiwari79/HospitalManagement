package com.kairos.service.data_inventory.asset;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.metadata.StorageFormatDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.StorageFormat;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.master_data.asset_management.storage_format.StorageFormatRepository;
import com.kairos.response.dto.common.StorageFormatResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.asset_management.StorageFormatService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class OrganizationStorageFormatService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationStorageFormatService.class);

    @Inject
    private StorageFormatRepository storageFormatRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private StorageFormatService storageFormatService;

    @Inject
    private AssetRepository assetRepository;


    /**
     * @param
     * @param unitId
     * @param storageFormatDTOS
     * @return return map which contain list of new StorageFormat and list of existing StorageFormat if StorageFormat already exist
     * @description this method create new StorageFormat if StorageFormat not exist with same name ,
     * and if exist then simply add  StorageFormat to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing StorageFormat using collation ,used for case insensitive result
     */
    public List<StorageFormatDTO> createStorageFormat(Long unitId, List<StorageFormatDTO> storageFormatDTOS) {
        Set<String> existingStorageFormatNames = storageFormatRepository.findNameByOrganizationIdAndDeleted(unitId);
        Set<String> storageFormatNames = ComparisonUtils.getNewMetaDataNames(storageFormatDTOS,existingStorageFormatNames );
        List<StorageFormat> storageFormats = new ArrayList<>();
        if (!storageFormatNames.isEmpty()) {
            for (String name : storageFormatNames) {
                StorageFormat storageFormat = new StorageFormat(name);
                storageFormat.setOrganizationId(unitId);
                storageFormats.add(storageFormat);
            }
            storageFormatRepository.saveAll(storageFormats);
        }
        return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(storageFormats, StorageFormatDTO.class);
    }


    /**
     * @param
     * @param unitId
     * @return list of StorageFormat
     */
    public List<StorageFormatResponseDTO> getAllStorageFormat(Long unitId) {
        return storageFormatRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId);
    }

    /**
     * @param
     * @param unitId
     * @param id             id of StorageFormat
     * @return StorageFormat object fetch via given id
     * @throws DataNotFoundByIdException throw exception if StorageFormat not exist for given id
     */
    public StorageFormat getStorageFormat(Long unitId, Long id) {

        StorageFormat exist = storageFormatRepository.findByIdAndOrganizationIdAndDeletedFalse(id, unitId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id " + id);
        } else {
            return exist;

        }
    }


    public Boolean deleteStorageFormat(Long unitId, Long storageFormatId) {

        List<String> assetsLinked = assetRepository.findAllAssetLinkedWithStorageFormat(unitId, storageFormatId);
        if (CollectionUtils.isNotEmpty(assetsLinked)) {
            exceptionService.metaDataLinkedWithAssetException("message.metaData.linked.with.asset", "message.storageFormat", StringUtils.join(assetsLinked, ','));
        }
        storageFormatRepository.deleteByIdAndOrganizationId(storageFormatId, unitId);
        return true;
    }


    /**
     * @param
     * @param unitId
     * @param id               id of StorageFormat
     * @param storageFormatDTO
     * @return StorageFormat updated object
     * @throws DuplicateDataException throw exception if data not exist for given id
     */
    public StorageFormatDTO updateStorageFormat(Long unitId, Long id, StorageFormatDTO storageFormatDTO) {

        StorageFormat storageFormat = storageFormatRepository.findByOrganizationIdAndDeletedAndName(unitId, storageFormatDTO.getName());
        if (Optional.ofNullable(storageFormat).isPresent()) {
            if (id.equals(storageFormat.getId())) {
                return storageFormatDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "message.storageFormat", storageFormat.getName());
        }
        Integer resultCount = storageFormatRepository.updateMetadataName(storageFormatDTO.getName(), id, unitId);
        if (resultCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.storageFormat", id);
        } else {
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, storageFormatDTO.getName());
        }
        return storageFormatDTO;

    }


    public List<StorageFormatDTO> saveAndSuggestStorageFormats(Long countryId, Long unitId, List<StorageFormatDTO> storageFormatDTOS) {

        List<StorageFormatDTO> result = createStorageFormat(unitId, storageFormatDTOS);
        storageFormatService.saveSuggestedStorageFormatsFromUnit(countryId, storageFormatDTOS);
        return result;
    }

    public List<StorageFormat> getAllOrganizationalStorageFormatByIds(Set<Long> ids) {
        return storageFormatRepository.findAllByIds(ids);
    }

}
