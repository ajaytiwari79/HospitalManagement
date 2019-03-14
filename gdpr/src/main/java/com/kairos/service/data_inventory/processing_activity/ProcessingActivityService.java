package com.kairos.service.data_inventory.processing_activity;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.enums.RiskSeverity;
import com.kairos.dto.gdpr.data_inventory.RelatedDataSubjectDTO;
import com.kairos.dto.gdpr.data_inventory.RelatedDataCategoryDTO;
import com.kairos.persistence.model.data_inventory.processing_activity.*;
//import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityRelatedDataCategory;
//import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityRelatedDataSubject;
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
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityRiskResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectResponseDTO;
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
            exceptionService.duplicateDataException("message.duplicate", "Processing Activity", processingActivityDTO.getName());
        }
        ProcessingActivity processingActivity = new ProcessingActivity();
        buildProcessingActivity(unitId, processingActivityDTO, processingActivity);
        if (!processingActivityDTO.getSubProcessingActivities().isEmpty()) {
            processingActivity.setSubProcessingActivities(createSubProcessingActivity(unitId, processingActivityDTO.getSubProcessingActivities(), processingActivity));
        }
        if (!processingActivityDTO.getDataSubjectSet().isEmpty()) {
            processingActivity.setDataSubjects(createRelatedDataProcessingActivity(unitId, processingActivityDTO.getDataSubjectSet()));
        }
        processingActivityRepository.save(processingActivity);
        processingActivityDTO.setId(processingActivity.getId());
        return processingActivityDTO;
    }


    private List<RelatedDataSubject> createRelatedDataProcessingActivity(Long unitId, List<RelatedDataSubjectDTO> relatedDataSubjects) {
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
            exceptionService.duplicateDataException("message.duplicate", "Processing Activity", processingActivityDTO.getName());
        }
        processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndDeletedFalse(id, unitId);
        if (!processingActivity.isActive()) {
            exceptionService.invalidRequestException("message.processing.activity.inactive");
        }
        buildProcessingActivity(unitId, processingActivityDTO, processingActivity);
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getSubProcessingActivities())) {
            processingActivity.setSubProcessingActivities(updateExistingSubProcessingActivitiesAndCreateNewSubProcess(unitId, processingActivityDTO.getSubProcessingActivities(), processingActivity));

        }
        processingActivityRepository.save(processingActivity);
        return processingActivityDTO;

    }

    private List<ProcessingActivity> createSubProcessingActivity(Long unitId, List<ProcessingActivityDTO> subProcessingActivityDTOs, ProcessingActivity processingActivity) {
        List<ProcessingActivity> subProcessingActivities = new ArrayList<>();
        for (ProcessingActivityDTO processingActivityDTO : subProcessingActivityDTOs) {
            ProcessingActivity subProcessingActivity = new ProcessingActivity();
            buildProcessingActivity(unitId, processingActivityDTO, subProcessingActivity);
            subProcessingActivity.setSubProcessingActivity(true);
            subProcessingActivity.setProcessingActivity(processingActivity);
            subProcessingActivity.setSubProcessingActivity(true);
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
        Optional.ofNullable(processingActivityDTO.getResponsibilityType()).ifPresent(resposibilityTypeId -> processingActivity.setResponsibilityType(responsibilityTypeRepository.findByIdAndOrganizationIdAndDeletedFalse(resposibilityTypeId, unitId)));
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
        processingActivity.setSuggested(processingActivityDTO.isSuggested());
        processingActivity.setDataRetentionPeriod(processingActivityDTO.getDataRetentionPeriod());
        processingActivity.setDpoContactInfo(processingActivityDTO.getDpoContactInfo());
    }

    private List<ProcessingActivity> updateExistingSubProcessingActivitiesAndCreateNewSubProcess(Long unitId, List<ProcessingActivityDTO> subProcessingActivityDTOs, ProcessingActivity processingActivity) {

        List<ProcessingActivityDTO> newSubProcessingActivityDTOList = new ArrayList<>();
        Map<Long, ProcessingActivityDTO> existingSubProcessingActivityMap = new HashMap<>();
        subProcessingActivityDTOs.forEach(processingActivityDTO -> {
            if (Optional.ofNullable(processingActivityDTO.getId()).isPresent()) {
                existingSubProcessingActivityMap.put(processingActivityDTO.getId(), processingActivityDTO);
            } else {
                newSubProcessingActivityDTOList.add(processingActivityDTO);
            }
        });
        List<ProcessingActivity> subProcessingActivities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(existingSubProcessingActivityMap.keySet())) {
            subProcessingActivities.addAll(updateSubProcessingActivities(unitId, existingSubProcessingActivityMap.keySet(), existingSubProcessingActivityMap));
        } else if (CollectionUtils.isNotEmpty(newSubProcessingActivityDTOList)) {
            subProcessingActivities.addAll(createSubProcessingActivity(unitId, newSubProcessingActivityDTOList, processingActivity));
        }
        return subProcessingActivities;

    }


    @SuppressWarnings("unchecked")
    private List<ProcessingActivity> updateSubProcessingActivities(Long orgId, Set<Long> subProcessingActivityIds, Map<Long, ProcessingActivityDTO> subProcessingActivityMap) {
        List updatesSubProcessingActivities = new ArrayList();
        List<ProcessingActivity> subProcessingActivities = processingActivityRepository.findSubProcessingActivitiesByIdsAndOrganisationId(orgId, subProcessingActivityIds);
        subProcessingActivities.forEach(subProcessingActivity -> {
            ProcessingActivityDTO processingActivityDTO = subProcessingActivityMap.get(subProcessingActivity.getId());
            buildProcessingActivity(orgId, processingActivityDTO, subProcessingActivity);
            updatesSubProcessingActivities.add(subProcessingActivity);
        });
        return updatesSubProcessingActivities;
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
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Sub Processing Activity", processingActivityId);
        }
        Integer updateCount = processingActivityRepository.unlinkSubProcessingActivityFromProcessingActivity(subProcessingActivityId, unitId, processingActivityId);
        processingActivity.delete();
        processingActivityRepository.save(processingActivity);
        return true;

    }


    public List<ProcessingActivityResponseDTO> getAllProcessingActivityWithMetaData(Long unitId) {
        List<ProcessingActivityResponseDTO> processingActivityResponseDTOS = new ArrayList<>();
        List<ProcessingActivity> processingActivitys = processingActivityRepository.findAllByOrganizationIdAndDeletedFalse(unitId);
        processingActivitys.forEach(processingActivity -> {
            processingActivityResponseDTOS.add(prepareProcessingActivityResponseData(processingActivity));
        });
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
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
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


    /*
      @param unitId
     * @param processingActivityId
     * @param activityRelatedDataSubjects list of data subject which contain list of data category and data Element list
     * @return
     */
    //TODO
    /*public boolean mapDataSubjectDataCategoryAndDataElementToProcessingActivity(Long unitId, BigInteger processingActivityId, List<ProcessingActivityRelatedDataSubject> activityRelatedDataSubjects) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }

        processingActivity.setDataSubjects(activityRelatedDataSubjects);
        processingActivityMongoRepository.save(processingActivity);
        return true;
    }*/

    /*
      @param unitId
     * @param processingActivityId
     * @param assetId              - asset id linked with processing activity
     * @return
     * @description map asset with processing activity (related tab processing activity)
     */
    //TODO
    /*public boolean mapAssetWithProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger assetId) {
        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        List<BigInteger> assetIds = processingActivity.getLinkedAssets();
        assetIds.add(assetId);
        processingActivity.setLinkedAssets(assetIds);
        processingActivityMongoRepository.save(processingActivity);
        return true;

    }*/

    /**
     * @param unitId
     * @param processingActivityId
     * @return
     * @description map Data Subject ,Data category and Data Element with processing activity(related tab processing activity)
     */
    public List<RelatedDataSubjectDTO> getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(Long unitId, Long processingActivityId) {

        ProcessingActivity processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndDeletedFalse(processingActivityId, unitId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getDataSubjects(), RelatedDataSubjectDTO.class);
    }
  /*  public List<DataSubjectMappingResponseDTO> getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(Long unitId, BigInteger processingActivityId) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        List<DataSubjectMappingResponseDTO> dataSubjectList = new ArrayList<>();
        List<ProcessingActivityRelatedDataSubject> mappedDataSubjectList = processingActivity.getDataSubjects();
        if (!mappedDataSubjectList.isEmpty()) {
            List<Long> dataSubjectIdList = new ArrayList<>();
            Map<Long, List<ProcessingActivityRelatedDataCategory>> relatedDataCategoryMap = new HashMap<>();
            for (ProcessingActivityRelatedDataSubject processingActivityRelatedDataSubject : mappedDataSubjectList) {
                dataSubjectIdList.add(processingActivityRelatedDataSubject.getId());
                relatedDataCategoryMap.put(processingActivityRelatedDataSubject.getId(), processingActivityRelatedDataSubject.getDataCategories());
            }
            dataSubjectList = processingActivityMongoRepository.getAllMappedDataSubjectWithDataCategoryAndDataElement(unitId, dataSubjectIdList);
            filterSelectedDataSubjectDataCategoryAndDataElementForProcessingActivity(dataSubjectList, relatedDataCategoryMap);

        }
        return dataSubjectList;
    }*/


    /*
      @param unitId
     * @param processingActivityId
     * @param dataSubjectId
     * @return
     */
    //TODO
    /*public boolean removeLinkedDataSubjectFromProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger dataSubjectId) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        List<ProcessingActivityRelatedDataSubject> activityRelatedDataSubjects = processingActivity.getDataSubjects();
        if (!activityRelatedDataSubjects.isEmpty()) {
            for (ProcessingActivityRelatedDataSubject activityRelatedDataSubject : processingActivity.getDataSubjects()) {
                if (activityRelatedDataSubject.getId().equals(dataSubjectId)) {
                    activityRelatedDataSubjects.remove(activityRelatedDataSubject);
                    break;
                }
            }
        }
        processingActivity.setDataSubjects(activityRelatedDataSubjects);
        processingActivityMongoRepository.save(processingActivity);
        return true;
    }
*/

    /*
      @param unitId
     * @param processingActivityId
     * @return
     */
    //TODO
   /* public List<AssetBasicResponseDTO> getAllAssetLinkedWithProcessingActivity(Long unitId, BigInteger processingActivityId) {
        return processingActivityMongoRepository.getAllAssetLinkedWithProcessingActivityById(unitId, processingActivityId);
    }*/


    /*
      @param unitId
     * @param processingActivityId
     * @param assetId
     * @return
     * @description method removed linked asset id from Processing activity
     */
    //TODO
    /*public boolean removeLinkedAssetFromProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger assetId) {
        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        processingActivity.getLinkedAssets().remove(assetId);
        processingActivityMongoRepository.save(processingActivity);
        return true;

    }*/

    /**
     * @param dataSubjectList
     * @param relatedDataCategoryMap
     * @description method filter data Category and there Corresponding data Element ,method filter data Category and remove Data category from data Category response List
     * similarly Data Elements are remove from data Element response list.
     */
    private void filterSelectedDataSubjectDataCategoryAndDataElementForProcessingActivity(List<DataSubjectResponseDTO> dataSubjectList, Map<Long, List<RelatedDataCategoryDTO>> relatedDataCategoryMap) {

        for (DataSubjectResponseDTO dataSubjectResponseDTO : dataSubjectList) {

            List<RelatedDataCategoryDTO> relatedDataCategoriesToDataSubject = relatedDataCategoryMap.get(dataSubjectResponseDTO.getId());
            Map<Long, Set<Long>> dataElementsCorrespondingToDataCategory = new HashMap<>();
            //relatedDataCategoriesToDataSubject.forEach(dataCategory -> dataElementsCorrespondingToDataCategory.put(dataCategory.getId(), dataCategory.getDataElements()));
            List<DataCategoryResponseDTO> dataCategoryResponseDTOS = new ArrayList<>();
            dataSubjectResponseDTO.getDataCategories().forEach(dataCategoryResponseDTO -> {

                if (dataElementsCorrespondingToDataCategory.containsKey(dataCategoryResponseDTO.getId())) {
                    //List<DataElementDeprecated> dataElementBasicResponseDTOS = new ArrayList<>();
                    Set<Long> dataElementIdList = dataElementsCorrespondingToDataCategory.get(dataCategoryResponseDTO.getId());
                    dataCategoryResponseDTO.getDataElements().forEach(dataElementBasicResponseDTO -> {
                        if (dataElementIdList.contains(dataElementBasicResponseDTO.getId())) {
                            //dataElementBasicResponseDTOS.add(dataElementBasicResponseDTO);
                        }
                    });
                    // dataCategoryResponseDTO.setDataElements(dataElementBasicResponseDTOS);
                    dataCategoryResponseDTOS.add(dataCategoryResponseDTO);
                }
            });
            dataSubjectResponseDTO.setDataCategories(dataCategoryResponseDTOS);
        }

    }


    /*
      @param unitId
     * @param processingActivityId
     * @param processingActivityRiskDTO
     */
    //TODO
    /*public ProcessingActivityRiskDTO createRiskAndLinkWithProcessingActivities(Long unitId, BigInteger processingActivityId, ProcessingActivityRiskDTO processingActivityRiskDTO) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivity);
        }
        List<ProcessingActivity> processingActivityList = new ArrayList<>();
        processingActivityList.add(processingActivity);
        Map<ProcessingActivity, List<OrganizationLevelRiskDTO>> riskDTOListCorrespondingToProcessingActivity = new HashMap<>();
        riskDTOListCorrespondingToProcessingActivity.put(processingActivity, processingActivityRiskDTO.getRisks());
        if (!processingActivityRiskDTO.getSubProcessingActivities().isEmpty()) {
            Map<BigInteger, List<OrganizationLevelRiskDTO>> subProcessingActivityAndRiskDtoListMap = new HashMap<>();
            processingActivityRiskDTO.getSubProcessingActivities().forEach(subProcessingActivityRiskDTO -> subProcessingActivityAndRiskDtoListMap.put(subProcessingActivityRiskDTO.getId(), subProcessingActivityRiskDTO.getRisks()));
            List<ProcessingActivity> subProcessingActivityList = processingActivityMongoRepository.findSubProcessingActivitiesByIds(unitId, subProcessingActivityAndRiskDtoListMap.keySet());
            subProcessingActivityList.stream().forEach(subProcessingActivity -> riskDTOListCorrespondingToProcessingActivity.put(subProcessingActivity, subProcessingActivityAndRiskDtoListMap.get(subProcessingActivity.getId())));
            processingActivityList.addAll(subProcessingActivityList);
        }
        if (!riskDTOListCorrespondingToProcessingActivity.isEmpty()) {
            Map<ProcessingActivity, List<Risk>> riskListRelatedProcessingActivities = riskService.saveRiskAtCountryLevelOrOrganizationLevel(unitId, true, riskDTOListCorrespondingToProcessingActivity);
            List<Risk> risks = new ArrayList<>();
            processingActivityList.forEach(processingActivityWithRisk -> {
                processingActivityWithRisk.setRisks(riskListRelatedProcessingActivities.get(processingActivity).stream().map(Risk::getId).collect(Collectors.toSet()));
                riskListRelatedProcessingActivities.get(processingActivity).forEach(risk -> {
                    risk.setProcessingActivity(processingActivity.getId());
                    risks.add(risk);
                });

            });
            riskMongoRepository.saveAll(getNextSequence(risks));

        }
        processingActivityMongoRepository.saveAll(getNextSequence(processingActivityList));
        return processingActivityRiskDTO;

    }
*/
    /*
      @param unitId
     * @return
     */
    //TODO
    public List<ProcessingActivityRiskResponseDTO> getAllProcessingActivityAndSubProcessingActivitiesWithRisk(Long unitId) {
        List<ProcessingActivity> processingActivities = processingActivityRepository.findAllByOrganizationId(unitId);
        List<ProcessingActivityRiskResponseDTO> processingActivityRiskResponseDTOS = prepareProcessingActivityRiskResponseDTOData(processingActivities, true);
        return processingActivityRiskResponseDTOS;
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


    /**
     * @param unitId //@param processingActivityId
     * @return
     */
    //TODO
    /*public boolean unLinkRiskFromProcessingOrSubProcessingActivityAndSafeDeleteRisk(Long unitId, BigInteger processingActivityId, BigInteger riskId) {
        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        processingActivity.getRisks().remove(riskId);
        riskMongoRepository.safeDeleteById(riskId);
        processingActivityMongoRepository.save(processingActivity);
        return true;
    }
*/
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

