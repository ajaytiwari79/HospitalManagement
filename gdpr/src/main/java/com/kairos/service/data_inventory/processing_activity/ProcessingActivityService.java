package com.kairos.service.data_inventory.processing_activity;


import com.kairos.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistance.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class ProcessingActivityService extends MongoBaseService {


    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AccessorPartyService accessorPartyService;

    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;

    @Inject
    private DataSourceService dataSourceService;

    @Inject
    private TransferMethodService transferMethodService;

    @Inject
    private ProcessingLegalBasisService processingLegalBasisService;

    @Inject
    private ProcessingPurposeService processingPurposeService;


    public ProcessingActivityDTO createProcessingActivity(Long organizationId, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivity exist = processingActivityMongoRepository.findByName(organizationId, processingActivityDTO.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", " Processing Activity ", processingActivityDTO.getName());
        }
        ProcessingActivity processingActivity = buildProcessingActivity(organizationId, processingActivityDTO);
        if (!processingActivityDTO.getSubProcessingActivities().isEmpty()) {
            processingActivity.setSubProcessingActivities(createSubProcessingActivity(organizationId, processingActivityDTO.getSubProcessingActivities()));
        }
         processingActivityMongoRepository.save(processingActivity);
         processingActivityDTO.setId(processingActivity.getId());
         return processingActivityDTO;
    }


    public Boolean deleteProcessingActivity(Long organizationId, BigInteger id) {
        ProcessingActivity processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(organizationId, id);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Processing Activity ", id);
        }
        delete(processingActivity);
        return true;

    }


    public ProcessingActivityResponseDTO getProcessingActivityWithMetaDataById(Long orgId, BigInteger id) {
        ProcessingActivityResponseDTO processingActivity = processingActivityMongoRepository.getAllSubProcessingActivitiesOfProcessingActivity(orgId, id);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Processing Activity ", id);
        }
        return processingActivity;
    }


    public List<ProcessingActivityResponseDTO> getAllProcessingActivityWithMetaData(Long orgId) {
        return processingActivityMongoRepository.getAllProcessingActivityAndMetaData(orgId);
    }



    public ProcessingActivityDTO updateProcessingActivity(Long organizationId, BigInteger id, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivity processingActivity = processingActivityMongoRepository.findByName(organizationId, processingActivityDTO.getName());
        if (Optional.ofNullable(processingActivity).isPresent() && !id.equals(processingActivity.getId())) {
            exceptionService.duplicateDataException("message.duplicate", " Processing Activity ", processingActivityDTO.getName());
        }
        processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(organizationId, id);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Processing Activity ", id);
        }
        if (!processingActivityDTO.getSubProcessingActivities().isEmpty()) {
            processingActivity.setSubProcessingActivities(updateExistingSubProcessingActivitiesAndCreateNewSubProcess(organizationId, processingActivityDTO.getSubProcessingActivities()));

        }
        processingActivity.setName(processingActivityDTO.getName());
        processingActivity.setDescription(processingActivityDTO.getDescription());
        processingActivity.setManagingDepartment(processingActivityDTO.getManagingDepartment());
        processingActivity.setProcessOwner(processingActivityDTO.getProcessOwner());
        processingActivity.setControllerContactInfo(processingActivityDTO.getControllerContactInfo());
        processingActivity.setJointControllerContactInfo(processingActivityDTO.getJointControllerContactInfo());
        processingActivity.setMaxDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        processingActivity.setMinDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
         processingActivityMongoRepository.save(processingActivity);
        return processingActivityDTO;

    }

    private List<BigInteger> createSubProcessingActivity(Long organizationId, List<ProcessingActivityDTO> subProcessingActivityDTOs) {

        List<ProcessingActivity> subProcessingActivities = new ArrayList<>();
        List<BigInteger> subProcessingActivityIdList = new ArrayList<>();

        for (ProcessingActivityDTO processingActivityDTO : subProcessingActivityDTOs) {

            ProcessingActivity processingActivity = buildProcessingActivity(organizationId, processingActivityDTO);
            processingActivity.setSubProcess(true);
            subProcessingActivities.add(processingActivity);
        }
        subProcessingActivities = processingActivityMongoRepository.saveAll(getNextSequence(subProcessingActivities));
        subProcessingActivities.forEach(processingActivity -> {

            subProcessingActivityIdList.add(processingActivity.getId());
        });
        return subProcessingActivityIdList;

    }


    private ProcessingActivity buildProcessingActivity(Long organizationId, ProcessingActivityDTO processingActivityDTO) {
        ProcessingActivity processingActivity = new ProcessingActivity(processingActivityDTO.getName(), processingActivityDTO.getDescription(),
                processingActivityDTO.getManagingDepartment(), processingActivityDTO.getProcessOwner());
        processingActivity.setOrganizationId(organizationId);
        processingActivity.setControllerContactInfo(processingActivityDTO.getControllerContactInfo());
        processingActivity.setJointControllerContactInfo(processingActivityDTO.getJointControllerContactInfo());
        processingActivity.setMaxDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        processingActivity.setMinDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        processingActivity.setResponsibilityType(processingActivityDTO.getResponsibilityType());
        processingActivity.setTransferMethods(processingActivityDTO.getTransferMethods());
        processingActivity.setProcessingPurposes(processingActivityDTO.getProcessingPurposes());
        processingActivity.setDataSources(processingActivityDTO.getDataSources());
        processingActivity.setAccessorParties(processingActivityDTO.getAccessorParties());
        processingActivity.setProcessingLegalBasis(processingActivityDTO.getProcessingLegalBasis());
        return processingActivity;

    }

    private List<BigInteger> updateExistingSubProcessingActivitiesAndCreateNewSubProcess(Long organizationId, List<ProcessingActivityDTO> subProcessingActivityDTOs) {

        List<ProcessingActivityDTO> newSubProcessingActivityDTOList = new ArrayList<>();
        Map<BigInteger, ProcessingActivityDTO> existingSubProcessingActivityMap = new HashMap<>();
        List<BigInteger> subProcessingActivitiesIdList = new ArrayList<>();
        subProcessingActivityDTOs.forEach(processingActivityDTO -> {
            if (Optional.ofNullable(processingActivityDTO.getId()).isPresent()) {
                existingSubProcessingActivityMap.put(processingActivityDTO.getId(), processingActivityDTO);
                subProcessingActivitiesIdList.add(processingActivityDTO.getId());
            } else {
                newSubProcessingActivityDTOList.add(processingActivityDTO);
            }
        });
        if (!existingSubProcessingActivityMap.isEmpty()) {
            updateSubProcessingActivities(organizationId, subProcessingActivitiesIdList, existingSubProcessingActivityMap);
        } else if (!newSubProcessingActivityDTOList.isEmpty()) {
            subProcessingActivitiesIdList.addAll(createSubProcessingActivity(organizationId, newSubProcessingActivityDTOList));
        }
        return subProcessingActivitiesIdList;

    }


    private void updateSubProcessingActivities(Long orgId, List<BigInteger> subProcessingActivityIds, Map<BigInteger, ProcessingActivityDTO> subProcessingActivityMap) {

        List<ProcessingActivity> subProcessingActivities = processingActivityMongoRepository.findSubProcessingActivitiesByIds(orgId, subProcessingActivityIds);
        subProcessingActivities.forEach(processingActivity -> {
            ProcessingActivityDTO processingActivityDTO = subProcessingActivityMap.get(processingActivity.getId());
            processingActivity.setName(processingActivityDTO.getName());
            processingActivity.setDescription(processingActivityDTO.getDescription());
            processingActivity.setManagingDepartment(processingActivityDTO.getManagingDepartment());
            processingActivity.setProcessOwner(processingActivityDTO.getProcessOwner());
            processingActivity.setControllerContactInfo(processingActivityDTO.getControllerContactInfo());
            processingActivity.setJointControllerContactInfo(processingActivityDTO.getJointControllerContactInfo());
            processingActivity.setMaxDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
            processingActivity.setMinDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
            processingActivity.setProcessingLegalBasis(processingActivityDTO.getProcessingLegalBasis());
            processingActivity.setAccessorParties(processingActivityDTO.getAccessorParties());
            processingActivity.setDataSources(processingActivityDTO.getDataSources());
            processingActivity.setProcessingLegalBasis(processingActivityDTO.getProcessingLegalBasis());
            processingActivity.setTransferMethods(processingActivityDTO.getTransferMethods());
            processingActivity.setResponsibilityType(processingActivityDTO.getResponsibilityType());

        });
        processingActivityMongoRepository.saveAll(getNextSequence(subProcessingActivities));

    }


}

