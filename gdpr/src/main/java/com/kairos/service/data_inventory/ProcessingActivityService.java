package com.kairos.service.data_inventory;


import com.kairos.dto.data_inventory.ProcessingActivityDTO;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistance.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessingActivityService extends MongoBaseService {


    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;


    @Inject
    private ExceptionService exceptionService;

    public ProcessingActivity createProcessingActivity(Long countryId, Long organizationId, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivity exist = processingActivityMongoRepository.findByName(countryId, organizationId, processingActivityDTO.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", " Processing Activity ", processingActivityDTO.getName());
        }
        ProcessingActivity processingActivity = new ProcessingActivity(processingActivityDTO.getName(), processingActivityDTO.getDescription(), countryId,
                processingActivityDTO.getManagingDepartment(), processingActivityDTO.getProcessOwner());
        processingActivity.setOrganizationId(organizationId);
        processingActivity.setControllerContactInfo(processingActivityDTO.getControllerContactInfo());
        processingActivity.setDataDestinations(processingActivityDTO.getDataDestinations());
        processingActivity.setDataSources(processingActivityDTO.getDataSources());
        processingActivity.setSourceTransferMethods(processingActivityDTO.getSourceTransferMethods());
        processingActivity.setDestinationTransferMethods(processingActivityDTO.getDestinationTransferMethods());
        processingActivity.setJointControllerContactInfo(processingActivityDTO.getJointControllerContactInfo());
        processingActivity.setMaxDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        processingActivity.setMinDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        return processingActivityMongoRepository.save(sequenceGenerator(processingActivity));

    }


    public Boolean deleteProcessingActivity(Long countryId, Long organizationId, BigInteger id) {
        ProcessingActivity exist = processingActivityMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Processing Activity ", id);
        }
        delete(exist);
        return true;

    }


    public ProcessingActivityResponseDTO getProcessingActivityWithMetaDataById(Long countryId, Long orgId, BigInteger id) {
        ProcessingActivityResponseDTO processingActivity = processingActivityMongoRepository.getProcessingActivityWithMetaDataById(countryId, orgId, id);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Processing Activity ", id);

        }
        return processingActivity;
    }


    public List<ProcessingActivityResponseDTO> getAllProcessingActivityWithMetaData(Long countryId, Long orgId) {

        return processingActivityMongoRepository.getAllProcessingActivityWithMetaData(countryId, orgId);
    }


    public ProcessingActivity updateProcessingActivity(Long countryId, Long organizationId, BigInteger id, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivity exist = processingActivityMongoRepository.findByName(countryId, organizationId, processingActivityDTO.getName());
        if (Optional.ofNullable(exist).isPresent() && !id.equals(exist.getId())) {
            exceptionService.duplicateDataException("message.duplicate", " Processing Activity ", processingActivityDTO.getName());
        }
        exist = processingActivityMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exist).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Processing Activity ", id);
        }
        exist.setName(processingActivityDTO.getName());
        exist.setDescription(processingActivityDTO.getDescription());
        exist.setManagingDepartment(processingActivityDTO.getManagingDepartment());
        exist.setProcessOwner(processingActivityDTO.getProcessOwner());
        exist.setOrganizationId(organizationId);
        exist.setControllerContactInfo(processingActivityDTO.getControllerContactInfo());
        exist.setDataDestinations(processingActivityDTO.getDataDestinations());
        exist.setDataSources(processingActivityDTO.getDataSources());
        exist.setSourceTransferMethods(processingActivityDTO.getSourceTransferMethods());
        exist.setDestinationTransferMethods(processingActivityDTO.getDestinationTransferMethods());
        exist.setJointControllerContactInfo(processingActivityDTO.getJointControllerContactInfo());
        exist.setMaxDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        exist.setMinDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        return processingActivityMongoRepository.save(sequenceGenerator(exist));

    }


}
