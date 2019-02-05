package com.kairos.service.data_inventory.processing_activity;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.dto.gdpr.metadata.ProcessingPurposeDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.ProcessingPurpose;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeRepository;
import com.kairos.response.dto.common.ProcessingPurposeResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.ProcessingPurposeService;
import com.kairos.utils.ComparisonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.EXISTING_DATA_LIST;
import static com.kairos.constants.AppConstant.NEW_DATA_LIST;

@Service
public class OrganizationProcessingPurposeService{


    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationProcessingPurposeService.class);

    @Inject
    private ProcessingPurposeRepository processingPurposeRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private ProcessingPurposeService processingPurposeService;

    @Inject
    private ProcessingActivityRepository processingActivityRepository;


    /**
     * @param organizationId
     * @param processingPurposeDTOS
     * @return return map which contain list of new ProcessingPurpose and list of existing ProcessingPurpose if ProcessingPurpose already exist
     * @description this method create new ProcessingPurpose if ProcessingPurpose not exist with same name ,
     * and if exist then simply add  ProcessingPurpose to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing ProcessingPurpose using collation ,used for case insensitive result
     */
    public Map<String, List<ProcessingPurpose>> createProcessingPurpose(Long organizationId, List<ProcessingPurposeDTO> processingPurposeDTOS) {

        Map<String, List<ProcessingPurpose>> result = new HashMap<>();
        Set<String> processingPurposesNames = new HashSet<>();
        if (!processingPurposeDTOS.isEmpty()) {
            for (ProcessingPurposeDTO processingPurpose : processingPurposeDTOS) {
                processingPurposesNames.add(processingPurpose.getName());
            }
            List<String> nameInLowerCase = processingPurposesNames.stream().map(String::toLowerCase)
                    .collect(Collectors.toList());
            //TODO still need to update we can return name of list from here and can apply removeAll on list
            List<ProcessingPurpose> existing = processingPurposeRepository.findByOrganizationIdAndDeletedAndNameIn(organizationId, false, nameInLowerCase);
            processingPurposesNames = ComparisonUtils.getNameListForMetadata(existing, processingPurposesNames);

            List<ProcessingPurpose> newProcessingPurposes = new ArrayList<>();
            if (processingPurposesNames.size() != 0) {
                for (String name : processingPurposesNames) {

                    ProcessingPurpose newProcessingPurpose = new ProcessingPurpose(name);
                    newProcessingPurpose.setOrganizationId(organizationId);
                    newProcessingPurposes.add(newProcessingPurpose);

                }
                newProcessingPurposes = processingPurposeRepository.saveAll(newProcessingPurposes);
            }
            result.put(EXISTING_DATA_LIST, existing);
            result.put(NEW_DATA_LIST, newProcessingPurposes);
            return result;
        } else
            throw new InvalidRequestException("list cannot be empty");


    }


    /**
     * @param organizationId
     * @return list of ProcessingPurpose
     */
    public List<ProcessingPurposeResponseDTO> getAllProcessingPurpose(Long organizationId) {
        return processingPurposeRepository.findAllByOrganizationIdAndSortByCreatedDate(organizationId);
    }

    /**
     * @param organizationId
     * @param id             id of ProcessingPurpose
     * @return ProcessingPurpose object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ProcessingPurpose not found for given id
     */
    public ProcessingPurpose getProcessingPurpose(Long organizationId, Long id) {

        ProcessingPurpose exist = processingPurposeRepository.findByIdAndOrganizationIdAndDeleted( id, organizationId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurpose(Long unitId, Long processingPurposeId) {

        List<String> processingActivities = processingActivityRepository.findAllProcessingActivityLinkedWithProcessingPurpose(unitId, processingPurposeId);
        if (!processingActivities.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "Processing Purpose", StringUtils.join(processingActivities,','));
        }
        processingPurposeRepository.deleteByIdAndOrganizationId(processingPurposeId, unitId);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if ProcessingPurpose data not exist for given id
     * @param organizationId
     * @param id id of ProcessingPurpose
     * @param processingPurposeDTO
     * @return ProcessingPurpose updated object
     */
    public ProcessingPurposeDTO updateProcessingPurpose(Long organizationId, Long id, ProcessingPurposeDTO processingPurposeDTO) {

        ProcessingPurpose processingPurpose = processingPurposeRepository.findByOrganizationIdAndDeletedAndName(organizationId, processingPurposeDTO.getName());
        if (Optional.ofNullable(processingPurpose).isPresent()) {
            if (id.equals(processingPurpose.getId())) {
                return processingPurposeDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "Processing Purpose", processingPurpose.getName());
        }
        Integer resultCount =  processingPurposeRepository.updateMetadataName(processingPurposeDTO.getName(), id, organizationId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Purpose", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, processingPurposeDTO.getName());
        }
        return processingPurposeDTO;

    }

    public Map<String, List<ProcessingPurpose>> saveAndSuggestProcessingPurposes(Long countryId, Long organizationId, List<ProcessingPurposeDTO> processingPurposeDTOS) {

        Map<String, List<ProcessingPurpose>> result = createProcessingPurpose(organizationId, processingPurposeDTOS);
        List<ProcessingPurpose> masterProcessingPurposeSuggestedByUnit = processingPurposeService.saveSuggestedProcessingPurposesFromUnit(countryId, processingPurposeDTOS);
        if (!masterProcessingPurposeSuggestedByUnit.isEmpty()) {
            result.put("SuggestedData", masterProcessingPurposeSuggestedByUnit);
        }
        return result;
    }


}
