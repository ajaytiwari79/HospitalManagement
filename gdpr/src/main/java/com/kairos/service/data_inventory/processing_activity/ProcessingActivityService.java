package com.kairos.service.data_inventory.processing_activity;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.enums.RiskSeverity;
import com.kairos.dto.gdpr.data_inventory.RelatedDataSubjectDTO;
import com.kairos.persistence.model.data_inventory.processing_activity.*;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import com.kairos.persistence.model.embeddables.Staff;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.data_category_element.RelatedDataSubjectRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodRepository;
import com.kairos.response.dto.common.*;
import com.kairos.response.dto.data_inventory.*;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
import com.kairos.service.master_data.processing_activity_masterdata.*;
import com.kairos.service.risk_management.RiskService;
import org.apache.commons.collections.CollectionUtils;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcessingActivityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingActivityService.class);

    @Inject
    private Javers javers;


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AccessorPartyRepository accessorPartyRepository;

    @Inject
    private ResponsibilityTypeRepository responsibilityTypeRepository;

    @Inject
    private DataSourceRepository dataSourceRepository;

    @Inject
    private TransferMethodRepository transferMethodRepository;

    @Inject
    private ProcessingPurposeRepository processingPurposeRepository;

    @Inject
    private ProcessingLegalBasisRepository processingLegalBasisRepository;

    @Inject
    private JaversCommonService javersCommonService;

    @Inject
    private AssetRepository assetRepository;

    @Inject
    private RiskService riskService;

    @Inject
    private ProcessingActivityRepository processingActivityRepository;

    @Inject
    private MasterProcessingActivityService masterProcessingActivityService;

    @Inject
    private RelatedDataSubjectRepository relatedDataSubjectRepository;


    @Transactional
    public ProcessingActivityDTO createProcessingActivity(Long unitId, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivity exist = processingActivityRepository.findByOrganizationIdAndDeletedAndName(unitId, processingActivityDTO.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.ProcessingActivity", processingActivityDTO.getName());
        }
        ProcessingActivity processingActivity = new ProcessingActivity();
        buildProcessingActivity(unitId, processingActivityDTO, processingActivity);
        if (!processingActivityDTO.getSubProcessingActivities().isEmpty()) {
            processingActivity.setSubProcessingActivities(createSubProcessingActivity(unitId, processingActivityDTO.getSubProcessingActivities(), processingActivity));
        }
        if (!processingActivityDTO.getDataSubjectSet().isEmpty()) {
            processingActivity.setDataSubjects(createRelatedDataProcessingActivity(processingActivityDTO.getDataSubjectSet()));
        }
        processingActivityRepository.save(processingActivity);
        processingActivityDTO.setId(processingActivity.getId());
        return processingActivityDTO;
    }


    private List<RelatedDataSubject> createRelatedDataProcessingActivity(List<RelatedDataSubjectDTO> relatedDataSubjects) {
        List<RelatedDataSubject> dataSubjects = new ArrayList<>();
        relatedDataSubjects.forEach(dataSubject -> {
            RelatedDataSubject relatedDataSubject = new RelatedDataSubject(dataSubject.getId(), dataSubject.getName());
            List<RelatedDataCategory> dataCategories = new ArrayList<>();
            dataSubject.getDataCategories().forEach(dataCategory -> dataCategories.add(new RelatedDataCategory(dataCategory.getId(), dataCategory.getName(), ObjectMapperUtils.copyPropertiesOfListByMapper(dataCategory.getDataElements(), RelatedDataElements.class))));
            relatedDataSubject.setDataCategories(dataCategories);
            dataSubjects.add(relatedDataSubject);
        });
        return relatedDataSubjectRepository.saveAll(dataSubjects);
    }


    public ProcessingActivityDTO updateProcessingActivity(Long unitId, Long id, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivity processingActivity = processingActivityRepository.findByOrganizationIdAndDeletedAndName(unitId, processingActivityDTO.getName());
        if (Optional.ofNullable(processingActivity).isPresent() && !id.equals(processingActivity.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "message.ProcessingActivity", processingActivityDTO.getName());
        }
        processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndDeletedFalse(id, unitId);
        if (!processingActivity.isActive()) {
            exceptionService.invalidRequestException("message.processing.activity.inactive");
        }
        buildProcessingActivity(unitId, processingActivityDTO, processingActivity);
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getSubProcessingActivities())) {
            processingActivity.setSubProcessingActivities(updateSubProcessingActivities(unitId, processingActivityDTO.getSubProcessingActivities(), processingActivity));

        }
        processingActivityRepository.save(processingActivity);
        return processingActivityDTO;

    }

    private List<ProcessingActivity> createSubProcessingActivity(Long unitId, List<ProcessingActivityDTO> subProcessingActivityDTOs, ProcessingActivity processingActivity) {
        List<ProcessingActivity> subProcessingActivities = new ArrayList<>();
        Set<String> subProcessNames = new HashSet<>();
        subProcessNames.add(processingActivity.getName().trim().toLowerCase());
        for (ProcessingActivityDTO subProcessingActivityDTO : subProcessingActivityDTOs) {
            if (subProcessNames.contains(subProcessingActivityDTO.getName().toLowerCase().trim())) {
                exceptionService.duplicateDataException("message.duplicate", "message.ProcessingActivity", subProcessingActivityDTO.getName());
            }
            subProcessNames.add(subProcessingActivityDTO.getName().trim().toLowerCase());
            ProcessingActivity subProcessingActivity = new ProcessingActivity();
            buildProcessingActivity(unitId, subProcessingActivityDTO, subProcessingActivity);
            subProcessingActivity.setSubProcessingActivity(true);
            subProcessingActivity.setProcessingActivity(processingActivity);
            subProcessingActivities.add(subProcessingActivity);
        }
        return subProcessingActivities;
    }


    private void buildProcessingActivity(Long unitId, ProcessingActivityDTO processingActivityDTO, ProcessingActivity processingActivity) {
        processingActivity.setOrganizationId(unitId);
        processingActivity.setName(processingActivityDTO.getName());
        processingActivity.setDescription(processingActivityDTO.getDescription());
        processingActivity.setOrganizationId(unitId);
        processingActivity.setControllerContactInfo(processingActivityDTO.getControllerContactInfo());
        processingActivity.setJointControllerContactInfo(processingActivityDTO.getJointControllerContactInfo());
        processingActivity.setMaxDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        processingActivity.setMinDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        processingActivity.setManagingDepartment(new ManagingOrganization(processingActivityDTO.getManagingDepartment().getId(), processingActivityDTO.getManagingDepartment().getName()));
        processingActivity.setProcessOwner(new Staff(processingActivityDTO.getProcessOwner().getStaffId(), processingActivityDTO.getProcessOwner().getFirstName(), processingActivityDTO.getProcessOwner().getLastName()));
        Optional.ofNullable(processingActivityDTO.getResponsibilityType()).ifPresent(responsibilityTypeId -> processingActivity.setResponsibilityType(responsibilityTypeRepository.findByIdAndOrganizationIdAndDeletedFalse(responsibilityTypeId, unitId)));
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getTransferMethods()))
            processingActivity.setTransferMethods(transferMethodRepository.findAllByIds(processingActivityDTO.getTransferMethods()));
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getProcessingPurposes()))
            processingActivity.setProcessingPurposes(processingPurposeRepository.findAllByIds(processingActivityDTO.getProcessingPurposes()));
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getDataSources()))
            processingActivity.setDataSources(dataSourceRepository.findAllByIds(processingActivityDTO.getDataSources()));
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getAccessorParties()))
            processingActivity.setAccessorParties(accessorPartyRepository.findAllByIds(processingActivityDTO.getAccessorParties()));
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getProcessingLegalBasis()))
            processingActivity.setProcessingLegalBasis(processingLegalBasisRepository.findAllByIds(processingActivityDTO.getProcessingLegalBasis()));
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getRisks())) {
            processingActivityDTO.getRisks().forEach(organizationLevelRiskDTO -> organizationLevelRiskDTO.setOrganizationId(unitId));
            processingActivity.setRisks(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivityDTO.getRisks(), Risk.class));
        }
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getAssetIds())) {
            processingActivity.setAssets(assetRepository.findAllByUnitIdAndIds(unitId, processingActivityDTO.getAssetIds()));
        }
        processingActivity.setSuggested(processingActivityDTO.isSuggested());
        processingActivity.setDataRetentionPeriod(processingActivityDTO.getDataRetentionPeriod());
        processingActivity.setDpoContactInfo(processingActivityDTO.getDpoContactInfo());
    }

    private List<ProcessingActivity> updateSubProcessingActivities(Long unitId, List<ProcessingActivityDTO> subProcessingActivityDTOs, ProcessingActivity processingActivity) {

        Map<Long, ProcessingActivity> longSubProcessingActivityMap = new HashMap<>();
        Set<String> subProcessNames = new HashSet<>();
        subProcessNames.add(processingActivity.getName().toLowerCase().trim());
        processingActivity.getSubProcessingActivities().forEach(subProcessingActivity -> longSubProcessingActivityMap.put(subProcessingActivity.getId(), subProcessingActivity));
        return subProcessingActivityDTOs.stream().map(subProcessingActivityDTO -> {
            if (subProcessNames.contains(subProcessingActivityDTO.getName().toLowerCase().trim())) {
                exceptionService.duplicateDataException("message.duplicate", "message.ProcessingActivity", subProcessingActivityDTO.getName());
            }
            subProcessNames.add(subProcessingActivityDTO.getName().trim().toLowerCase());
            ProcessingActivity subProcessingActivity;
            if (Optional.ofNullable(subProcessingActivityDTO.getId()).isPresent()) {
                subProcessingActivity = longSubProcessingActivityMap.get(subProcessingActivityDTO.getId());
            } else {
                subProcessingActivity = new ProcessingActivity();
            }
            subProcessingActivity.setSubProcessingActivity(true);
            subProcessingActivity.setProcessingActivity(processingActivity);
            buildProcessingActivity(unitId, subProcessingActivityDTO, subProcessingActivity);
            return subProcessingActivity;
        }).collect(Collectors.toList());
    }

    /**
     * @param unitId
     * @param processingActivityId
     * @return
     * @description method delete processing activity and Sub processing activity is activity is associated with asset then method simply return  without deleting activities
     */
    public boolean deleteProcessingActivity(Long unitId, Long processingActivityId) {

        ProcessingActivity processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndDeletedFalse(processingActivityId, unitId);
        processingActivity.delete();
        processingActivityRepository.save(processingActivity);
        return true;

    }


    public boolean deleteSubProcessingActivity(Long unitId, Long processingActivityId, Long subProcessingActivityId) {

        ProcessingActivity processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndProcessingActivityId(subProcessingActivityId, unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.ProcessingActivity", processingActivityId);
        }
        processingActivity.delete();
        processingActivityRepository.save(processingActivity);
        return true;

    }


    public List<ProcessingActivityResponseDTO> getAllProcessingActivityWithMetaData(Long unitId) {
        List<ProcessingActivityResponseDTO> processingActivityResponseDTOS = new ArrayList<>();
        List<ProcessingActivity> processingActivitys = processingActivityRepository.findAllByOrganizationIdAndDeletedFalse(unitId);
        processingActivitys.forEach(processingActivity ->
                processingActivityResponseDTOS.add(prepareProcessingActivityResponseData(processingActivity)));
        return processingActivityResponseDTOS;
    }


    private ProcessingActivityResponseDTO prepareProcessingActivityResponseData(ProcessingActivity processingActivity) {
        ProcessingActivityResponseDTO processingActivityResponseDTO = new ProcessingActivityResponseDTO();
        processingActivityResponseDTO.setId(processingActivity.getId());
        processingActivityResponseDTO.setName(processingActivity.getName());
        processingActivityResponseDTO.setDescription(processingActivity.getDescription());
        processingActivityResponseDTO.setControllerContactInfo(processingActivity.getControllerContactInfo());
        processingActivityResponseDTO.setJointControllerContactInfo(processingActivity.getJointControllerContactInfo());
        processingActivityResponseDTO.setMaxDataSubjectVolume(processingActivity.getMinDataSubjectVolume());
        processingActivityResponseDTO.setMinDataSubjectVolume(processingActivity.getMinDataSubjectVolume());
        processingActivityResponseDTO.setManagingDepartment(ObjectMapperUtils.copyPropertiesByMapper(processingActivity.getManagingDepartment(), com.kairos.dto.gdpr.ManagingOrganization.class));
        processingActivityResponseDTO.setProcessOwner(ObjectMapperUtils.copyPropertiesByMapper(processingActivity.getProcessOwner(), com.kairos.dto.gdpr.Staff.class));
        processingActivityResponseDTO.setResponsibilityType(ObjectMapperUtils.copyPropertiesByMapper(processingActivity.getResponsibilityType(), ResponsibilityTypeResponseDTO.class));
        processingActivityResponseDTO.setTransferMethods(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getTransferMethods(), TransferMethodResponseDTO.class));
        processingActivityResponseDTO.setProcessingPurposes(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getProcessingPurposes(), ProcessingPurposeResponseDTO.class));
        processingActivityResponseDTO.setDataSources(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getDataSources(), DataSourceResponseDTO.class));
        processingActivityResponseDTO.setAccessorParties(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getAccessorParties(), AccessorPartyResponseDTO.class));
        processingActivityResponseDTO.setProcessingLegalBasis(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getProcessingLegalBasis(), ProcessingLegalBasisResponseDTO.class));
        processingActivityResponseDTO.setSuggested(processingActivity.isSuggested());
        processingActivityResponseDTO.setDataRetentionPeriod(processingActivity.getDataRetentionPeriod());
        processingActivityResponseDTO.setDpoContactInfo(processingActivity.getDpoContactInfo());
        processingActivityResponseDTO.setActive(processingActivity.isActive());
        if (CollectionUtils.isNotEmpty(processingActivity.getRisks())) {
            processingActivityResponseDTO.setRisks(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getRisks(), RiskBasicResponseDTO.class));
        }
        if (CollectionUtils.isNotEmpty(processingActivity.getAssets())) {
            processingActivityResponseDTO.setAssets(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getAssets(), AssetBasicResponseDTO.class));
        }
        processingActivityResponseDTO.setDataSubjects(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getDataSubjects(), RelatedDataSubjectDTO.class));
        if (CollectionUtils.isNotEmpty(processingActivity.getSubProcessingActivities())) {
            processingActivity.getSubProcessingActivities().forEach(subProcessingActivity -> processingActivityResponseDTO.getSubProcessingActivities().add(prepareProcessingActivityResponseData(subProcessingActivity)));
        }
        return processingActivityResponseDTO;

    }


    /**
     * @param unitId
     * @param processingActivityId processing activity id
     * @param active               status of processing activity
     * @return
     */
    public boolean changeStatusOfProcessingActivity(Long unitId, Long processingActivityId, boolean active) {
        Integer updateCount = processingActivityRepository.updateProcessingActivityStatus(unitId, processingActivityId, active);
        if (updateCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.ProcessingActivity", processingActivityId);
        } else {
            LOGGER.info("Processing activity is updated successfully with id :: {}", processingActivityId);
        }
        return true;
    }


    /**
     * @param processingActivityId
     * @return
     * @description method return audit history of Processing Activity , old Object list and latest version also.
     * return object contain  changed field with key fields and values with key Values in return list of map
     */
    public List<Map<String, Object>> getProcessingActivityActivitiesHistory(Long processingActivityId) throws ClassNotFoundException {

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(processingActivityId, ProcessingActivity.class);
        List<CdoSnapshot> changes = javers.findSnapshots(jqlQuery.build());
        changes.sort((o1, o2) -> -1 * (int) o1.getVersion() - (int) o2.getVersion());
        return javersCommonService.getHistoryMap(changes, processingActivityId, ProcessingActivity.class);

    }

    /*
      @param unitId
     * @return
     * @description method return processing activities and SubProcessing Activities with basic detail ,name,description
     */
    public List<ProcessingActivityBasicResponseDTO> getAllProcessingActivityWithBasicDetailForAsset(Long unitId) {
        return processingActivityRepository.getAllProcessingActivityWithBasicDetailForAsset(unitId);
    }


    /**
     * @param unitId
     * @param processingActivityId
     * @return
     * @description map Data Subject ,Data category and Data Element with processing activity(related tab processing activity)
     */
    public List<RelatedDataSubjectDTO> getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(Long unitId, Long processingActivityId) {

        ProcessingActivity processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndDeletedFalse(processingActivityId, unitId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.ProcessingActivity", processingActivityId);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getDataSubjects(), RelatedDataSubjectDTO.class);
    }

    /*
      @param unitId
     * @return
     */
    public List<ProcessingActivityRiskResponseDTO> getAllProcessingActivityAndSubProcessingActivitiesWithRisk(Long unitId) {
        List<ProcessingActivity> processingActivities = processingActivityRepository.findAllByOrganizationId(unitId);
        return prepareProcessingActivityRiskResponseDTOData(processingActivities, true);
    }

    private List<ProcessingActivityRiskResponseDTO> prepareProcessingActivityRiskResponseDTOData(List<ProcessingActivity> processingActivities, boolean isParentProcessingActivity) {
        List<ProcessingActivityRiskResponseDTO> processingActivityRiskResponseDTOS = new ArrayList<>();
        for (ProcessingActivity processingActivity : processingActivities) {
            List<ProcessingActivityRiskResponseDTO> subProcessingActivityRiskResponseDTOS = new ArrayList<>();
            ProcessingActivityRiskResponseDTO processingActivityRiskResponseDTO = new ProcessingActivityRiskResponseDTO();
            processingActivityRiskResponseDTO.setId(processingActivity.getId());
            processingActivityRiskResponseDTO.setMainParent(isParentProcessingActivity);
            processingActivityRiskResponseDTO.setName(processingActivity.getName());
            if (!isParentProcessingActivity) {
                processingActivityRiskResponseDTO.setRisks(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getRisks(), RiskBasicResponseDTO.class));
            }
            List<ProcessingActivity> subProcessingActivities = processingActivity.getSubProcessingActivities();
            if (!subProcessingActivities.isEmpty()) {
                subProcessingActivityRiskResponseDTOS = prepareProcessingActivityRiskResponseDTOData(subProcessingActivities, false);
            }
            if (isParentProcessingActivity) {
                subProcessingActivityRiskResponseDTOS.add(0, new ProcessingActivityRiskResponseDTO(processingActivityRiskResponseDTO.getId(), processingActivityRiskResponseDTO.getName(), processingActivityRiskResponseDTO.getMainParent(), ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getRisks(), RiskBasicResponseDTO.class)));
                processingActivityRiskResponseDTO.setProcessingActivities(subProcessingActivityRiskResponseDTOS);
            }
            processingActivityRiskResponseDTOS.add(processingActivityRiskResponseDTO);
        }
        return processingActivityRiskResponseDTOS;
    }


    @Transactional
    public Map<String, ProcessingActivityDTO> saveProcessingActivityAndSuggestToCountryAdmin(Long unitId, Long countryId, ProcessingActivityDTO processingActivityDTO) {

        if (CollectionUtils.isNotEmpty(processingActivityDTO.getSubProcessingActivities())) {
            processingActivityDTO.getSubProcessingActivities().forEach(subProcessingActivityDTO -> subProcessingActivityDTO.setSuggested(true));
        }
        Map<String, ProcessingActivityDTO> result = new HashMap<>();
        processingActivityDTO = createProcessingActivity(unitId, processingActivityDTO);
        ProcessingActivityDTO masterProcessingActivity = masterProcessingActivityService.saveSuggestedMasterProcessingActivityDataFromUnit(countryId, unitId, processingActivityDTO);
        result.put("new", processingActivityDTO);
        result.put("SuggestedData", masterProcessingActivity);
        return result;

    }

    /**
     * @param unitId
     * @return
     */
    public Map<String, Object> getProcessingActivityMetaData(Long unitId) {
        Map<String, Object> processingActivityMetaDataMap = new HashMap<>();
        processingActivityMetaDataMap.put("responsibilityTypeList", responsibilityTypeRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("processingPurposeList", processingPurposeRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("dataSourceList", dataSourceRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("transferMethodList", transferMethodRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("accessorPartyList", accessorPartyRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("processingLegalBasisList", processingLegalBasisRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("riskLevelList", RiskSeverity.values());
        return processingActivityMetaDataMap;

    }


}

