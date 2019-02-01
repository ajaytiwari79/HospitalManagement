package com.kairos.service.data_inventory.processing_activity;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.enums.RiskSeverity;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityRelatedDataSubject;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityRelatedDataCategory;
import com.kairos.persistence.model.data_inventory.processing_activity.*;
//import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityRelatedDataCategory;
//import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityRelatedDataSubject;
import com.kairos.persistence.model.master_data.data_category_element.DataElement;
import com.kairos.persistence.model.risk_management.RiskMD;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.data_category_element.RelatedDataSubjectRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodRepository;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
import com.kairos.service.master_data.processing_activity_masterdata.*;
import com.kairos.service.risk_management.RiskService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class ProcessingActivityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessingActivityService.class);

   /* @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;*/


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
    public ProcessingActivityDTO createProcessingActivity(Long organizationId, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivityMD exist = processingActivityRepository.findByOrganizationIdAndDeletedAndName(organizationId, false, processingActivityDTO.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Processing Activity", processingActivityDTO.getName());
        }
        ProcessingActivityMD processingActivity = new ProcessingActivityMD();
        processingActivity = buildProcessingActivity(organizationId, processingActivityDTO, processingActivity);
        if (!processingActivityDTO.getSubProcessingActivities().isEmpty()) {
            processingActivity.setSubProcessingActivities(createSubProcessingActivity(organizationId, processingActivityDTO.getSubProcessingActivities()));
        }
        if (!processingActivityDTO.getRisks().isEmpty()) {
            processingActivity.setRisks(updateExistingRisksOrCreateNewRisk(organizationId, processingActivityDTO.getRisks(),processingActivity));
        }
        if (!processingActivityDTO.getDataSubjectSet().isEmpty()) {
            processingActivity.setDataSubjects(createRelatedDataProcessingActivity(organizationId, processingActivityDTO.getDataSubjectSet()));
        }
        processingActivityRepository.save(processingActivity);
        processingActivityDTO.setId(processingActivity.getId());
        return processingActivityDTO;
    }


    private List<RiskMD> updateExistingRisksOrCreateNewRisk(Long orgId, List<OrganizationLevelRiskDTO> riskDTOList, ProcessingActivityMD processingActivity){
        Map<Long, OrganizationLevelRiskDTO> existingRiskDtoCorrespondingToIds = new HashMap<>();
        List<RiskMD> risks = new ArrayList<>();
        riskDTOList.forEach( riskDTO -> {
            if (Optional.ofNullable(riskDTO.getId()).isPresent()) {
                existingRiskDtoCorrespondingToIds.put(riskDTO.getId(), riskDTO);
            }else{
                risks.add(ObjectMapperUtils.copyPropertiesByMapper(riskDTO, RiskMD.class));
            }
        });
        processingActivity.getRisks().forEach( processingActivityRisk -> {
            OrganizationLevelRiskDTO organizationLevelRiskDTO = existingRiskDtoCorrespondingToIds.get(processingActivityRisk.getId());
           /* processingActivityRisk.setName(organizationLevelRiskDTO.getName());
            processingActivityRisk.setDescription(organizationLevelRiskDTO.getDescription());*/
            risks.add(ObjectMapperUtils.copyPropertiesByMapper(organizationLevelRiskDTO, RiskMD.class));
        });
        return risks;
    }


    public List<RelatedDataSubject> createRelatedDataProcessingActivity(Long organizationId, List<ProcessingActivityRelatedDataSubject> relatedDataSubjects){
        List<RelatedDataSubject> dataSubjects =  new ArrayList<>();
        relatedDataSubjects.forEach( dataSubject -> {
            RelatedDataSubject relatedDataSubject = new RelatedDataSubject(dataSubject.getId(), dataSubject.getName());
            List<RelatedDataCategory> dataCategories = new ArrayList<>();
            dataSubject.getDataCategories().forEach( dataCategory -> {
                dataCategories.add(new RelatedDataCategory(dataCategory.getId(), dataCategory.getName(), ObjectMapperUtils.copyPropertiesOfListByMapper(dataCategory.getDataElements(), RelatedDataElements.class)));
            });
            relatedDataSubject.setDataCategories(dataCategories);
            dataSubjects.add(relatedDataSubject);
        });

        return relatedDataSubjectRepository.saveAll(dataSubjects);
    }


    public ProcessingActivityDTO updateProcessingActivity(Long organizationId, Long id, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivityMD processingActivity = processingActivityRepository.findByOrganizationIdAndDeletedAndName(organizationId, false,  processingActivityDTO.getName());
        if (Optional.ofNullable(processingActivity).isPresent() && !id.equals(processingActivity.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "Processing Activity", processingActivityDTO.getName());
        }
        processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndDeleted(id, organizationId, false);
        if (!processingActivity.isActive()) {
            exceptionService.invalidRequestException("message.processing.activity.inactive");
        }
        processingActivity = buildProcessingActivity(organizationId, processingActivityDTO, processingActivity);
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getSubProcessingActivities())) {
            processingActivity.setSubProcessingActivities(updateExistingSubProcessingActivitiesAndCreateNewSubProcess(organizationId, processingActivityDTO.getSubProcessingActivities()));

        }
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getRisks())) {
            processingActivity.setRisks(updateExistingRisksOrCreateNewRisk(organizationId, processingActivityDTO.getRisks(), processingActivity));

        }
        processingActivityRepository.save(processingActivity);
        return processingActivityDTO;

    }

    private List<ProcessingActivityMD> createSubProcessingActivity(Long organizationId, List<ProcessingActivityDTO> subProcessingActivityDTOs) {
        List<ProcessingActivityMD> subProcessingActivities = new ArrayList<>();
        for (ProcessingActivityDTO processingActivityDTO : subProcessingActivityDTOs) {
            ProcessingActivityMD subProcessingActivity = new ProcessingActivityMD();
            subProcessingActivity = buildProcessingActivity(organizationId, processingActivityDTO,subProcessingActivity);
            subProcessingActivity.setSubProcessingActivity(true);
            subProcessingActivities.add(subProcessingActivity);
        }
        return subProcessingActivities;
    }


    private ProcessingActivityMD buildProcessingActivity(Long organizationId, ProcessingActivityDTO processingActivityDTO, ProcessingActivityMD processingActivity) {
        processingActivity = ObjectMapperUtils.copyPropertiesByMapper(processingActivityDTO, ProcessingActivityMD.class);
        processingActivity.setOrganizationId(organizationId);
       /* processingActivity.setName(processingActivityDTO.getName());
        processingActivity.setDescription(processingActivityDTO.getDescription());
        processingActivity.setOrganizationId(organizationId);
        processingActivity.setControllerContactInfo(processingActivityDTO.getControllerContactInfo());
        processingActivity.setJointControllerContactInfo(processingActivityDTO.getJointControllerContactInfo());
        processingActivity.setMaxDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        processingActivity.setMinDataSubjectVolume(processingActivityDTO.getMinDataSubjectVolume());
        processingActivity.setManagingDepartment(new ManagingOrganization(processingActivityDTO.getManagingDepartment().getId(), processingActivityDTO.getManagingDepartment().getName()));
        processingActivity.setProcessOwner(new Staff(processingActivityDTO.getProcessOwner().getId(), processingActivityDTO.getProcessOwner().getFirstName(),processingActivityDTO.getProcessOwner().getLastName()));
        processingActivity.setResponsibilityType(responsibilityTypeRepository.findByIdAndOrganizationIdAndDeleted(processingActivityDTO.getResponsibilityType(), organizationId, false));
        processingActivity.setTransferMethods(transferMethodRepository.findAllByIds(processingActivityDTO.getTransferMethods()));
        processingActivity.setProcessingPurposes(processingPurposeRepository.findAllByIds(processingActivityDTO.getProcessingPurposes()));
        processingActivity.setDataSources(dataSourceRepository.findAllByIds(processingActivityDTO.getDataSources()));
        processingActivity.setAccessorParties(accessorPartyRepository.findAllByIds(processingActivityDTO.getAccessorParties()));
        processingActivity.setProcessingLegalBasis(processingLegalBasisRepository.findAllByIds(processingActivityDTO.getProcessingLegalBasis()));
        processingActivity.setSuggested(processingActivityDTO.isSuggested());
        processingActivity.setDataRetentionPeriod(processingActivityDTO.getDataRetentionPeriod());
        processingActivity.setDpoContactInfo(processingActivityDTO.getDpoContactInfo());*/
        //processingActivity.setDataSubjects(processingActivityDTO.getDataSubjectList());
        return processingActivity;

    }

    private List<ProcessingActivityMD> updateExistingSubProcessingActivitiesAndCreateNewSubProcess(Long organizationId, List<ProcessingActivityDTO> subProcessingActivityDTOs) {

        List<ProcessingActivityDTO> newSubProcessingActivityDTOList = new ArrayList<>();
        Map<Long, ProcessingActivityDTO> existingSubProcessingActivityMap = new HashMap<>();
        subProcessingActivityDTOs.forEach(processingActivityDTO -> {
            if (Optional.ofNullable(processingActivityDTO.getId()).isPresent()) {
                existingSubProcessingActivityMap.put(processingActivityDTO.getId(), processingActivityDTO);
            } else {
                newSubProcessingActivityDTOList.add(processingActivityDTO);
            }
        });
        List<ProcessingActivityMD> subProcessingActivities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(existingSubProcessingActivityMap.keySet())) {
            subProcessingActivities.addAll(updateSubProcessingActivities(organizationId, existingSubProcessingActivityMap.keySet(), existingSubProcessingActivityMap));
        } else if (CollectionUtils.isNotEmpty(newSubProcessingActivityDTOList)) {
            subProcessingActivities.addAll(createSubProcessingActivity(organizationId, newSubProcessingActivityDTOList));
        }
        return subProcessingActivities;

    }


    private List<ProcessingActivityMD> updateSubProcessingActivities(Long orgId, Set<Long> subProcessingActivityIds, Map<Long, ProcessingActivityDTO> subProcessingActivityMap) {
        List updatesSubProcessingActivities = new ArrayList();
        List<ProcessingActivityMD> subProcessingActivities = processingActivityRepository.findSubProcessingActivitiesByIdsAndOrganisationId(orgId, subProcessingActivityIds);
        subProcessingActivities.forEach(subProcessingActivity -> {
            ProcessingActivityDTO processingActivityDTO = subProcessingActivityMap.get(subProcessingActivity.getId());
            subProcessingActivity = buildProcessingActivity(orgId, processingActivityDTO, subProcessingActivity);
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

        ProcessingActivityMD processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndDeleted(processingActivityId, unitId, false);
        processingActivity.delete();
        processingActivityRepository.save(processingActivity);
        return true;

    }


    public boolean deleteSubProcessingActivity(Long unitId, Long processingActivityId, Long subProcessingActivityId) {

        ProcessingActivityMD processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndProcessingActivityId(subProcessingActivityId,unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Sub Processing Activity", processingActivityId);
        }
        Integer updateCount = processingActivityRepository.unlinkSubProcessingActivityFromProcessingActivity(subProcessingActivityId,unitId, processingActivityId);
        processingActivity.delete();
        processingActivityRepository.save(processingActivity);
        return true;

    }


    public List<ProcessingActivityResponseDTO> getAllProcessingActivityWithMetaData(Long unitId) {
        List<ProcessingActivityResponseDTO> processingActivityResponseDTOS = new ArrayList<>();
        List<ProcessingActivityMD> processingActivitys = processingActivityRepository.findAllByOrganizationId(unitId);
        processingActivitys.forEach(processingActivity -> {
            processingActivityResponseDTOS.add(prepareProcessingActivityResponseData(processingActivity));
           /* if (!Optional.ofNullable(processingActivityResponseDTO.getSubProcessingActivities().get(0).getId()).isPresent()) {
                processingActivityResponseDTO.setSubProcessingActivities(new ArrayList<>());
            }*/
        });
        return processingActivityResponseDTOS;
    }


    ProcessingActivityResponseDTO prepareProcessingActivityResponseData(ProcessingActivityMD processingActivity){
        /*ProcessingActivityResponseDTO processingActivityResponseDTO = new ProcessingActivityResponseDTO();
        processingActivityResponseDTO.setId(processingActivityResponseDTO.getId());
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
        processingActivityResponseDTO.setDataSubjects(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getDataSubjects(), com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityRelatedDataSubject.class));*/
        return ObjectMapperUtils.copyPropertiesByMapper(processingActivity, ProcessingActivityResponseDTO.class);

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
        }else{
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
    /*public List<Map<String, Object>> getProcessingActivityActivitiesHistory(BigInteger processingActivityId) {

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(processingActivityId, ProcessingActivity.class);
        List<CdoSnapshot> changes = javers.findSnapshots(jqlQuery.build());
        changes.sort((o1, o2) -> -1 * (int) o1.getVersion() - (int) o2.getVersion());
        return javersCommonService.getHistoryMap(changes, processingActivityId, ProcessingActivity.class);

    }*/

    /**
     * @param unitId
     * @return
     * @description method return processing activities and SubProcessing Activities with basic detail ,name,description
     */
    //TODO
    /*public List<ProcessingActivityBasicResponseDTO> getAllProcessingActivityBasicDetailsAndWithSubProcess(Long unitId) {
        return processingActivityMongoRepository.getAllProcessingActivityBasicDetailWithSubProcessingActivities(unitId);
    }*/


    /**
     * @param unitId
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

    /**
     * @param unitId
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
    public List<ProcessingActivityRelatedDataSubject> getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(Long unitId, Long processingActivityId) {

        ProcessingActivityMD processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndDeleted(processingActivityId,unitId, false);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        List<ProcessingActivityRelatedDataSubject> mappedDataSubjectList = ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getDataSubjects(), ProcessingActivityRelatedDataSubject.class);
        return mappedDataSubjectList;
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


    /**
     * @param unitId
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

    /**
     * @param unitId
     * @param processingActivityId
     * @return
     */
    //TODO
   /* public List<AssetBasicResponseDTO> getAllAssetLinkedWithProcessingActivity(Long unitId, BigInteger processingActivityId) {
        return processingActivityMongoRepository.getAllAssetLinkedWithProcessingActivityById(unitId, processingActivityId);
    }*/


    /**
     * @param unitId
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
    private void filterSelectedDataSubjectDataCategoryAndDataElementForProcessingActivity(List<DataSubjectMappingResponseDTO> dataSubjectList, Map<Long, List<ProcessingActivityRelatedDataCategory>> relatedDataCategoryMap) {

        for (DataSubjectMappingResponseDTO dataSubjectMappingResponseDTO : dataSubjectList) {

            List<ProcessingActivityRelatedDataCategory> relatedDataCategoriesToDataSubject = relatedDataCategoryMap.get(dataSubjectMappingResponseDTO.getId());
            Map<Long, Set<Long>> dataElementsCorrespondingToDataCategory = new HashMap<>();
            //relatedDataCategoriesToDataSubject.forEach(dataCategory -> dataElementsCorrespondingToDataCategory.put(dataCategory.getId(), dataCategory.getDataElements()));
            List<DataCategoryResponseDTO> dataCategoryResponseDTOS = new ArrayList<>();
            dataSubjectMappingResponseDTO.getDataCategories().forEach(dataCategoryResponseDTO -> {

                if (dataElementsCorrespondingToDataCategory.containsKey(dataCategoryResponseDTO.getId())) {
                    List<DataElement> dataElementBasicResponseDTOS = new ArrayList<>();
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
            dataSubjectMappingResponseDTO.setDataCategories(dataCategoryResponseDTOS);
        }

    }


    /**
     * @param unitId
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
    /**
     * @param unitId
     * @return
     */
    //TODO
   /* public List<ProcessingActivityRiskResponseDTO> getAllProcessingActivityAndSubProcessingActivitiesWithRisk(Long unitId) {
        List<ProcessingActivityRiskResponseDTO> processingActivityRiskResponseDTOS = processingActivityMongoRepository.getAllProcessingActivityAndSubProcessWithRisksByUnitId(unitId);
        processingActivityRiskResponseDTOS.forEach(processingActivity -> {
            if (!Optional.ofNullable(processingActivity.getProcessingActivities().get(0).getId()).isPresent()) {
                processingActivity.setProcessingActivities(new ArrayList<>());
            }
            processingActivity.getProcessingActivities().add(0, new ProcessingActivityRiskResponseDTO(processingActivity.getId(), processingActivity.getName(), true, processingActivity.getRisks()));
            processingActivity.setMainParent(true);
        });
        return processingActivityRiskResponseDTOS;
    }


    public List<AssessmentBasicResponseDTO> getAssessmentListByProcessingActivityId(Long unitId, BigInteger processingActivityId) {
        return assessmentMongoRepository.findAllAssessmentLaunchedForProcessingActivityByActivityIdAndUnitId(unitId, processingActivityId);
    }*/


    /**
     * @param unitId
     * //@param processingActivityId
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
     *
     * @param unitId
     * @return
     */
    public Map<String,Object> getProcessingActivityMetaData(Long unitId){
        Map<String,Object> processingActivityMetaDataMap=new HashMap<>();
        processingActivityMetaDataMap.put("responsibilityTypeList", responsibilityTypeRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("processingPurposeList",processingPurposeRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("dataSourceList",dataSourceRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("transferMethodList",transferMethodRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("accessorPartyList", accessorPartyRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("processingLegalBasisList",processingLegalBasisRepository.findAllByOrganizationIdAndSortByCreatedDate(unitId));
        processingActivityMetaDataMap.put("riskLevelList", RiskSeverity.values());
        return processingActivityMetaDataMap;

    }


}

