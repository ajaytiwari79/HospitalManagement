package com.kairos.service.data_inventory;


import com.kairos.dto.data_inventory.ProcessingActivityDTO;
import com.kairos.dto.metadata.ResponsibilityTypeDTO;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistance.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.master_data.processing_activity_masterdata.*;
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


    public ProcessingActivity createProcessingActivity(Long countryId, Long organizationId, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivity exist = processingActivityMongoRepository.findByName(countryId, organizationId, processingActivityDTO.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", " Processing Activity ", processingActivityDTO.getName());
        }
        ProcessingActivity processingActivity = new ProcessingActivity(processingActivityDTO.getName(), processingActivityDTO.getDescription(), countryId,
                processingActivityDTO.getManagingDepartment(), processingActivityDTO.getProcessOwner());
        processingActivity.setOrganizationId(organizationId);
        buildProcessingActivityWithMetaDataAndUpdate(countryId, organizationId, processingActivityDTO, processingActivity);
        processingActivity.setControllerContactInfo(processingActivityDTO.getControllerContactInfo());
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
        buildProcessingActivityWithMetaDataAndUpdate(countryId, organizationId, processingActivityDTO, exist);
        exist.setName(processingActivityDTO.getName());
        exist.setDescription(processingActivityDTO.getDescription());
        exist.setManagingDepartment(processingActivityDTO.getManagingDepartment());
        exist.setProcessOwner(processingActivityDTO.getProcessOwner());
        exist.setControllerContactInfo(processingActivityDTO.getControllerContactInfo());
        exist.setJointControllerContactInfo(processingActivityDTO.getJointControllerContactInfo());
        exist.setMaxDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        exist.setMinDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        return processingActivityMongoRepository.save(sequenceGenerator(exist));

    }


    private void buildProcessingActivityWithMetaDataAndUpdate(Long countryId, Long organizationId, ProcessingActivityDTO processingActivityDTO, ProcessingActivity processingActivity) {

        if (Optional.ofNullable(processingActivityDTO.getAccessorParties()).isPresent() && !processingActivityDTO.getAccessorParties().isEmpty()) {
            List<BigInteger> accessorPartyIds = accessorPartyService.createAccessorPartyForOrganizationOnInheritingFromParentOrganization(countryId, organizationId, processingActivityDTO);
            processingActivity.setAccessorParties(accessorPartyIds);
        } else if (Optional.ofNullable(processingActivityDTO.getDataSources()).isPresent() && !processingActivityDTO.getDataSources().isEmpty()) {
            List<BigInteger> dataSourceIds = dataSourceService.createDataSourceForOrganizationOnInheritingFromParentOrganization(countryId, organizationId, processingActivityDTO);
            processingActivity.setDataSources(dataSourceIds);
        } else if (Optional.ofNullable(processingActivityDTO.getProcessingLegalBasis()).isPresent() && !processingActivityDTO.getProcessingLegalBasis().isEmpty()) {
            List<BigInteger> processingLegalBasisIds = processingLegalBasisService.createProcessingLegaBasisForOrganizationOnInheritingFromParentOrganization(countryId, organizationId, processingActivityDTO);
            processingActivity.setProcessingLegalBasis(processingLegalBasisIds);
        } else if (Optional.ofNullable(processingActivityDTO.getProcessingPurposes()).isPresent() && !processingActivityDTO.getProcessingPurposes().isEmpty()) {
            List<BigInteger> processingPurposeIds = processingPurposeService.createProcessingPurposeForOrganizationOnInheritingFromParentOrganization(countryId, organizationId, processingActivityDTO);
            processingActivity.setProcessingPurposes(processingPurposeIds);

        } else if (Optional.ofNullable(processingActivityDTO.getSourceTransferMethods()).isPresent() && !processingActivityDTO.getSourceTransferMethods().isEmpty()) {

            List<BigInteger> sourceTransferMethodIds = transferMethodService.createTransferMethodForOrganizationOnInheritingFromParentOrganization(countryId, organizationId, processingActivityDTO.getSourceTransferMethods());
            processingActivity.setSourceTransferMethods(sourceTransferMethodIds);
        } else if (Optional.ofNullable(processingActivityDTO.getDestinationTransferMethods()).isPresent() && !processingActivityDTO.getDestinationTransferMethods().isEmpty()) {

            List<BigInteger> destinationTransferMethodIds = transferMethodService.createTransferMethodForOrganizationOnInheritingFromParentOrganization(countryId, organizationId, processingActivityDTO.getDestinationTransferMethods());
            processingActivity.setSourceTransferMethods(destinationTransferMethodIds);
        } else if (Optional.ofNullable(processingActivityDTO.getResponsibilityType()).isPresent()) {

            ResponsibilityTypeDTO responsibilityTypeDTO = processingActivityDTO.getResponsibilityType();
            if (!responsibilityTypeDTO.getOrganizationId().equals(organizationId)) {
                ResponsibilityType responsibilityType = new ResponsibilityType(responsibilityTypeDTO.getName(), countryId);
                responsibilityType.setOrganizationId(organizationId);
                responsibilityType = responsibilityTypeMongoRepository.save(sequenceGenerator(responsibilityType));
                processingActivity.setResponsibilityType(responsibilityType.getId());
            } else {
                processingActivity.setResponsibilityType(responsibilityTypeDTO.getId());
            }
        }
    }










}
