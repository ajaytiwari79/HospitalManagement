package com.kairos.service.master_data.asset_management;



import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.StorageFormatDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.StorageFormat;
import com.kairos.persistence.repository.master_data.asset_management.storage_format.StorageFormatRepository;
import com.kairos.response.dto.common.StorageFormatResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class StorageFormatService{

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageFormatService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private StorageFormatRepository storageFormatRepository;

    /**
     * @param countryId
     * @param
     * @param storageFormatDTOS
     * @return return map which contain list of new StorageFormat and list of existing StorageFormat if StorageFormat already exist
     * @description this method create new StorageFormat if StorageFormat not exist with same name ,
     * and if exist then simply add  StorageFormat to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing StorageFormat using collation ,used for case insensitive result
     */
    public Map<String, List<StorageFormat>> createStorageFormat(Long countryId, List<StorageFormatDTO> storageFormatDTOS, boolean isSuggestion) {
        //TODO still need to optimize we can get name of list in string from here
        Map<String, List<StorageFormat>> result = new HashMap<>();
        Set<String> storageFormatNames = new HashSet<>();
            for (StorageFormatDTO storageFormat : storageFormatDTOS) {
                storageFormatNames.add(storageFormat.getName());
            }
            List<String> nameInLowerCase = storageFormatNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());

            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<StorageFormat> existing = storageFormatRepository.findByCountryIdAndDeletedAndNameIn(countryId,  nameInLowerCase);
            storageFormatNames = ComparisonUtils.getNameListForMetadata(existing, storageFormatNames);

            List<StorageFormat> newStorageFormats = new ArrayList<>();
            if (!storageFormatNames.isEmpty()) {
                for (String name : storageFormatNames) {
                    StorageFormat newStorageFormat = new StorageFormat(name,countryId);
                    if(isSuggestion){
                        newStorageFormat.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                        newStorageFormat.setSuggestedDate(LocalDate.now());
                    }else {
                        newStorageFormat.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                    }
                    newStorageFormats.add(newStorageFormat);
                }
                newStorageFormats = storageFormatRepository.saveAll(newStorageFormats);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newStorageFormats);
            return result;
    }


    /**
     * @param countryId
     * @param
     * @return list of StorageFormat
     */
    public List<StorageFormatResponseDTO> getAllStorageFormat(Long countryId) {
        return storageFormatRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }

    /**
     * @param countryId
     * @param
     * @param id        id of StorageFormat
     * @return StorageFormat object fetch via given id
     * @throws DataNotFoundByIdException throw exception if StorageFormat not exist for given id
     */
    public StorageFormat getStorageFormat(Long countryId, Long id) {

        StorageFormat exist = storageFormatRepository.findByIdAndCountryIdAndDeletedFalse(id, countryId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteStorageFormat(Long countryId, Long id) {
        Integer resultCount = storageFormatRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Storage Format deleted successfully for id :: {}", id);
        }else{
            throw new DataNotFoundByIdException("No data found");
        }
        return true;
    }


    /**
     * @param countryId
     * @param
     * @param id               id of StorageFormat
     * @param storageFormatDTO
     * @return StorageFormat updated object
     * @throws DuplicateDataException throw exception if data not exist for given id
     */
    public StorageFormatDTO updateStorageFormat(Long countryId, Long id, StorageFormatDTO storageFormatDTO) {
        //TODO What actually this code is doing?
        StorageFormat storageFormat = storageFormatRepository.findByCountryIdAndName(countryId,  storageFormatDTO.getName());
        if (Optional.ofNullable(storageFormat).isPresent()) {
            if (id.equals(storageFormat.getId())) {
                return storageFormatDTO;
            }
            throw new DuplicateDataException("data  exist for  " + storageFormatDTO.getName());
        }
        Integer resultCount =  storageFormatRepository.updateMasterMetadataName(storageFormatDTO.getName(), id, countryId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Storage Format", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, storageFormatDTO.getName());
        }
        return storageFormatDTO;


    }

    /**
     * @description method save Storage format suggested by unit
     * @param countryId
     * @param storageFormatDTOS
     * @return
     */
    public List<StorageFormat> saveSuggestedStorageFormatsFromUnit(Long countryId, List<StorageFormatDTO> storageFormatDTOS) {
        Map<String, List<StorageFormat>> result = createStorageFormat(countryId, storageFormatDTOS,true);
        return result.get(NEW_DATA_LIST);

    }


    /**
     *
     * @param countryId
     * @param storageFormatIds
     * @param suggestedDataStatus
     * @return
     */
    public List<StorageFormat> updateSuggestedStatusOfStorageFormatList(Long countryId, Set<Long> storageFormatIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = storageFormatRepository.updateMetadataStatus(countryId, storageFormatIds, suggestedDataStatus);
        if(updateCount > 0){
            LOGGER.info("Storage Formats are updated successfully with ids :: {}", storageFormatIds);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Storage Format", storageFormatIds);
        }
        return storageFormatRepository.findAllByIds(storageFormatIds);
    }


}
