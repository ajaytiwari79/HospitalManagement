package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.dto.gdpr.metadata.ProcessingPurposeDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeRepository;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;


@Service
public class ProcessingPurposeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingPurposeService.class);

    @Inject
    private ProcessingPurposeRepository processingPurposeRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param
     * @param processingPurposeDTOS
     * @return return map which contain list of new ProcessingPurpose and list of existing ProcessingPurpose if ProcessingPurpose already exist
     * @description this method create new ProcessingPurpose if ProcessingPurpose not exist with same name ,
     * and if exist then simply add  ProcessingPurpose to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing ProcessingPurpose using collation ,used for case insensitive result
     */
    public List<ProcessingPurposeDTO> createProcessingPurpose(Long countryId, List<ProcessingPurposeDTO> processingPurposeDTOS, boolean isSuggestion) {
        //TODO still need to optimize we can get name of list in string from here
        Set<String> processingPurposesNames = new HashSet<>();
        for (ProcessingPurposeDTO processingPurpose : processingPurposeDTOS) {
            processingPurposesNames.add(processingPurpose.getName());

        }
        List<String> nameInLowerCase = processingPurposesNames.stream().map(String::toLowerCase)
                .collect(Collectors.toList());
        //TODO still need to update we can return name of list from here and can apply removeAll on list
        List<ProcessingPurpose> previousProcessingPurposes = processingPurposeRepository.findByCountryIdAndDeletedAndNameIn(countryId, nameInLowerCase);
        processingPurposesNames = ComparisonUtils.getNameListForMetadata(previousProcessingPurposes, processingPurposesNames);

        List<ProcessingPurpose> processingPurposes = new ArrayList<>();
        if (!processingPurposesNames.isEmpty()) {
            for (String name : processingPurposesNames) {
                ProcessingPurpose processingPurpose = new ProcessingPurpose(name, countryId);
                if (isSuggestion) {
                    processingPurpose.setSuggestedDataStatus(SuggestedDataStatus.PENDING);
                    processingPurpose.setSuggestedDate(LocalDate.now());
                } else {
                    processingPurpose.setSuggestedDataStatus(SuggestedDataStatus.APPROVED);
                }
                processingPurposes.add(processingPurpose);
            }
            processingPurposeRepository.saveAll(processingPurposes);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(processingPurposes, ProcessingPurposeDTO.class);

    }


    /**
     * @param countryId
     * @param
     * @return list of ProcessingPurpose
     */
    public List<ProcessingPurposeResponseDTO> getAllProcessingPurpose(Long countryId) {
        return processingPurposeRepository.findAllByCountryIdAndSortByCreatedDate(countryId);
    }

    /**
     * @param countryId
     * @param
     * @param id        id of ProcessingPurpose
     * @return ProcessingPurpose object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ProcessingPurpose not found for given id
     */
    public ProcessingPurpose getProcessingPurpose(Long countryId, Long id) {
        ProcessingPurpose exist = processingPurposeRepository.findByIdAndCountryIdAndDeletedFalse(id, countryId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("No data found");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurpose(Long countryId, Long id) {
        Integer resultCount = processingPurposeRepository.deleteByIdAndCountryId(id, countryId);
        if (resultCount > 0) {
            LOGGER.info("Processing Purpose deleted successfully for id :: {}", id);
        } else {
            throw new DataNotFoundByIdException("No data found");
        }
        return true;

    }

    /***
     * @throws DuplicateDataException throw exception if ProcessingPurpose data not exist for given id
     * @param countryId
     * @param
     * @param id id of ProcessingPurpose
     * @param processingPurposeDTO
     * @return ProcessingPurpose updated object
     */
    public ProcessingPurposeDTO updateProcessingPurpose(Long countryId, Long id, ProcessingPurposeDTO processingPurposeDTO) {
        ProcessingPurpose processingPurpose = processingPurposeRepository.findByCountryIdAndName(countryId, processingPurposeDTO.getName());
        if (Optional.ofNullable(processingPurpose).isPresent()) {
            if (id.equals(processingPurpose.getId())) {
                return processingPurposeDTO;
            }
            throw new DuplicateDataException("data  exist for  " + processingPurposeDTO.getName());
        } else {
            Integer resultCount = processingPurposeRepository.updateMasterMetadataName(processingPurposeDTO.getName(), id, countryId);
            if (resultCount <= 0) {
                exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Purpose", id);
            } else {
                LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, processingPurposeDTO.getName());
            }
            return processingPurposeDTO;

        }
    }

    /**
     * @param countryId             - country id
     * @param processingPurposeDTOS - processing purpose suggested by unit
     * @return
     */
    public List<ProcessingPurposeDTO> saveSuggestedProcessingPurposesFromUnit(Long countryId, List<ProcessingPurposeDTO> processingPurposeDTOS) {
        return createProcessingPurpose(countryId, processingPurposeDTOS, true);

    }


    /**
     * @param countryId
     * @param processingPurposeIds
     * @param suggestedDataStatus
     * @return
     */
    public List<ProcessingPurpose> updateSuggestedStatusOfProcessingPurposeList(Long countryId, Set<Long> processingPurposeIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = processingPurposeRepository.updateMetadataStatus(countryId, processingPurposeIds, suggestedDataStatus);
        if (updateCount > 0) {
            LOGGER.info("Processing Purposes are updated successfully with ids :: {}", processingPurposeIds);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Purpose", processingPurposeIds);
        }
        return processingPurposeRepository.findAllByIds(processingPurposeIds);
    }


}

    
    
    

