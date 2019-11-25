package com.kairos.service.data_inventory.processing_activity;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
     * @param unitId
     * @param processingPurposeDTOS
     * @return return map which contain list of new ProcessingPurpose and list of existing ProcessingPurpose if ProcessingPurpose already exist
     * @description this method create new ProcessingPurpose if ProcessingPurpose not exist with same name ,
     * and if exist then simply add  ProcessingPurpose to existing list and return list ;
     * findMetaDataByNamesAndCountryId()  return list of existing ProcessingPurpose using collation ,used for case insensitive result
     */
    public List<ProcessingPurposeDTO>  createProcessingPurpose(Long unitId, List<ProcessingPurposeDTO> processingPurposeDTOS) {
            Set<String> existingProcessingPurposeNames = processingPurposeRepository.findNameByOrganizationIdAndDeleted(unitId);
            Set<String> processingPurposesNames = ComparisonUtils.getNewMetaDataNames(processingPurposeDTOS,existingProcessingPurposeNames );
            List<ProcessingPurpose> processingPurposes = new ArrayList<>();
            if (!processingPurposesNames.isEmpty()) {
                for (String name : processingPurposesNames) {

                    ProcessingPurpose processingPurpose = new ProcessingPurpose(name);
                    processingPurpose.setOrganizationId(unitId);
                    processingPurposes.add(processingPurpose);

                }
               processingPurposeRepository.saveAll(processingPurposes);
            }
            return ObjectMapperUtils.copyPropertiesOfCollectionByMapper(processingPurposes,ProcessingPurposeDTO.class);
    }


    /**
     * @param unitId
     * @return list of ProcessingPurpose
     */
    public List<ProcessingPurposeResponseDTO> getAllProcessingPurpose(Long unitId) {
        return processingPurposeRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId);
    }

    /**
     * @param unitId
     * @param id             id of ProcessingPurpose
     * @return ProcessingPurpose object fetch by given id
     * @throws DataNotFoundByIdException throw exception if ProcessingPurpose not found for given id
     */
    public ProcessingPurpose getProcessingPurpose(Long unitId, Long id) {

        ProcessingPurpose exist = processingPurposeRepository.findByIdAndOrganizationIdAndDeletedFalse( id, unitId);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }


    public Boolean deleteProcessingPurpose(Long unitId, Long processingPurposeId) {

        List<String> processingActivities = processingActivityRepository.findAllProcessingActivityLinkedWithProcessingPurpose(unitId, processingPurposeId);
        if (!processingActivities.isEmpty()) {
            exceptionService.metaDataLinkedWithProcessingActivityException("message.metaData.linked.with.ProcessingActivity", "message.processingPurpose", StringUtils.join(processingActivities,','));
        }
        processingPurposeRepository.deleteByIdAndOrganizationId(processingPurposeId, unitId);
        return true;
    }

    /***
     * @throws DuplicateDataException throw exception if ProcessingPurpose data not exist for given id
     * @param unitId
     * @param id id of ProcessingPurpose
     * @param processingPurposeDTO
     * @return ProcessingPurpose updated object
     */
    public ProcessingPurposeDTO updateProcessingPurpose(Long unitId, Long id, ProcessingPurposeDTO processingPurposeDTO) {

        ProcessingPurpose processingPurpose = processingPurposeRepository.findByOrganizationIdAndDeletedAndName(unitId, processingPurposeDTO.getName());
        if (Optional.ofNullable(processingPurpose).isPresent()) {
            if (id.equals(processingPurpose.getId())) {
                return processingPurposeDTO;
            }
            exceptionService.duplicateDataException("message.duplicate", "message.processingPurpose", processingPurpose.getName());
        }
        Integer resultCount =  processingPurposeRepository.updateMetadataName(processingPurposeDTO.getName(), id, unitId);
        if(resultCount <=0){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.processingPurpose", id);
        }else{
            LOGGER.info("Data updated successfully for id : {} and name updated name is : {}", id, processingPurposeDTO.getName());
        }
        return processingPurposeDTO;

    }

    public List<ProcessingPurposeDTO> saveAndSuggestProcessingPurposes(Long countryId, Long unitId, List<ProcessingPurposeDTO> processingPurposeDTOS) {

        List<ProcessingPurposeDTO> result = createProcessingPurpose(unitId, processingPurposeDTOS);
        processingPurposeService.saveSuggestedProcessingPurposesFromUnit(countryId, processingPurposeDTOS);
        return result;
    }


}
