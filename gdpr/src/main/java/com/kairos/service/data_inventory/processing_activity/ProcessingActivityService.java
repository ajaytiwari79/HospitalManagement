package com.kairos.service.data_inventory.processing_activity;


import com.kairos.dto.gdpr.data_inventory.OrganizationLevelRiskDTO;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityRiskDTO;
import com.kairos.enums.RiskSeverity;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityRelatedDataSubject;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityRelatedDataCategory;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
//import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityRelatedDataCategory;
//import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityRelatedDataSubject;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentMongoRepository;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.master_data.data_category_element.DataSubjectMappingRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateMongoRepository;
import com.kairos.persistence.repository.risk_management.RiskMongoRepository;
import com.kairos.response.dto.common.AssessmentBasicResponseDTO;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityRiskResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataElementBasicResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
import com.kairos.service.master_data.processing_activity_masterdata.*;
import com.kairos.service.risk_management.RiskService;
import org.apache.commons.collections.CollectionUtils;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcessingActivityService extends MongoBaseService {


    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AccessorPartyService accessorPartyService;

    @Inject
    private OrganizationAccessorPartyService organizationAccessorPartyService;

    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;

    @Inject
    private DataSourceService dataSourceService;

    @Inject
    private OrganizationDataSourceService organizationDataSourceService;

    @Inject
    private TransferMethodService transferMethodService;
    @Inject
    private OrganizationTransferMethodService organizationTransferMethodService;

    @Inject
    private ProcessingLegalBasisService processingLegalBasisService;

    @Inject
    private ProcessingPurposeService processingPurposeService;

    @Inject
    private OrganizationProcessingPurposeService organizationProcessingPurposeService;

    @Inject
    private Javers javers;

    @Inject
    private JaversCommonService javersCommonService;

    @Inject
    private AssetMongoRepository assetMongoRepository;

    @Inject
    private DataSubjectMappingRepository dataSubjectMappingRepository;

    @Inject
    private AssessmentMongoRepository assessmentMongoRepository;

    @Inject
    private QuestionnaireTemplateMongoRepository questionnaireTemplateMongoRepository;

    @Inject
    private RiskService riskService;

    @Inject
    private RiskMongoRepository riskMongoRepository;

    @Inject
    private MasterProcessingActivityService masterProcessingActivityService;
    @Inject
    private OrganizationResponsibilityTypeService organizationResponsibilityTypeService;
    @Inject
    private OrganizationProcessingLegalBasisService organizationProcessingLegalBasisService;



    public ProcessingActivityDTO createProcessingActivity(Long organizationId, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivity exist = processingActivityMongoRepository.findByName(organizationId, processingActivityDTO.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Processing Activity", processingActivityDTO.getName());
        }
        ProcessingActivity processingActivity = buildProcessingActivity(organizationId, processingActivityDTO);
        if (!processingActivityDTO.getSubProcessingActivities().isEmpty()) {
            processingActivity.setSubProcessingActivities(processingActivityMongoRepository.saveAll(getNextSequence(createSubProcessingActivity(organizationId, processingActivityDTO.getSubProcessingActivities()))).stream().map(ProcessingActivity::getId).collect(Collectors.toList()));
        }
        processingActivityMongoRepository.save(processingActivity);
        processingActivityDTO.setId(processingActivity.getId());
        return processingActivityDTO;
    }


    public ProcessingActivityDTO updateProcessingActivity(Long organizationId, BigInteger id, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivity processingActivity = processingActivityMongoRepository.findByName(organizationId, processingActivityDTO.getName());
        if (Optional.ofNullable(processingActivity).isPresent() && !id.equals(processingActivity.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "Processing Activity", processingActivityDTO.getName());
        }
        processingActivity = processingActivityMongoRepository.findByUnitIdAndId(organizationId, id);
        if (!processingActivity.isActive()) {
            exceptionService.invalidRequestException("message.processing.activity.inactive");
        }
        if (CollectionUtils.isNotEmpty(processingActivityDTO.getSubProcessingActivities())) {
            processingActivity.setSubProcessingActivities(updateExistingSubProcessingActivitiesAndCreateNewSubProcess(organizationId, processingActivityDTO.getSubProcessingActivities()));

        }
        processingActivity.setResponsibilityType(processingActivityDTO.getResponsibilityType());
        processingActivity.setTransferMethods(processingActivityDTO.getTransferMethods());
        processingActivity.setDataSources(processingActivityDTO.getDataSources());
        processingActivity.setProcessingPurposes(processingActivityDTO.getProcessingPurposes());
        processingActivity.setAccessorParties(processingActivityDTO.getAccessorParties());
        processingActivity.setProcessingPurposes(processingActivityDTO.getProcessingPurposes());
        processingActivity.setProcessingLegalBasis(processingActivityDTO.getProcessingLegalBasis());
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

    private List<ProcessingActivity> createSubProcessingActivity(Long organizationId, List<ProcessingActivityDTO> subProcessingActivityDTOs) {
        List<ProcessingActivity> subProcessingActivities = new ArrayList<>();
        for (ProcessingActivityDTO processingActivityDTO : subProcessingActivityDTOs) {
            ProcessingActivity processingActivity = buildProcessingActivity(organizationId, processingActivityDTO);
            processingActivity.setSubProcess(true);
            subProcessingActivities.add(processingActivity);
        }
        return subProcessingActivities;
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
        processingActivity.setSuggested(processingActivityDTO.isSuggested());
        processingActivity.setDataSubjects(processingActivityDTO.getDataSubjectList());
        return processingActivity;

    }

    private List<BigInteger> updateExistingSubProcessingActivitiesAndCreateNewSubProcess(Long organizationId, List<ProcessingActivityDTO> subProcessingActivityDTOs) {

        List<ProcessingActivityDTO> newSubProcessingActivityDTOList = new ArrayList<>();
        Map<BigInteger, ProcessingActivityDTO> existingSubProcessingActivityMap = new HashMap<>();
        subProcessingActivityDTOs.forEach(processingActivityDTO -> {
            if (Optional.ofNullable(processingActivityDTO.getId()).isPresent()) {
                existingSubProcessingActivityMap.put(processingActivityDTO.getId(), processingActivityDTO);
            } else {
                newSubProcessingActivityDTOList.add(processingActivityDTO);
            }
        });
        List<ProcessingActivity> subProcessingActivities = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(existingSubProcessingActivityMap.keySet())) {
            subProcessingActivities.addAll(updateSubProcessingActivities(organizationId, existingSubProcessingActivityMap.keySet(), existingSubProcessingActivityMap));
        } else if (CollectionUtils.isNotEmpty(newSubProcessingActivityDTOList)) {
            subProcessingActivities.addAll(createSubProcessingActivity(organizationId, newSubProcessingActivityDTOList));
        }
        return processingActivityMongoRepository.saveAll(getNextSequence(subProcessingActivities)).stream().map(ProcessingActivity::getId).collect(Collectors.toList());

    }


    private List<ProcessingActivity> updateSubProcessingActivities(Long orgId, Set<BigInteger> subProcessingActivityIds, Map<BigInteger, ProcessingActivityDTO> subProcessingActivityMap) {

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
        return subProcessingActivities;
    }


    /**
     * @param unitId
     * @param processingActivityId
     * @return
     * @description method delete processing activity and Sub processing activity is activity is associated with asset then method simply return  without deleting activities
     */
    public boolean deleteProcessingActivity(Long unitId, BigInteger processingActivityId) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        Set<BigInteger> riskIds = new HashSet<>();
        riskIds.addAll(processingActivity.getRisks());
        List<ProcessingActivity> subProcessingActivities = processingActivityMongoRepository.findSubProcessingActivitiesByIds(unitId, new HashSet<>(processingActivity.getSubProcessingActivities()));
        if (CollectionUtils.isNotEmpty(subProcessingActivities)) {
            processingActivityMongoRepository.safeDeleteAll(subProcessingActivities);
            subProcessingActivities.forEach(subProcessingActivity -> riskIds.addAll(subProcessingActivity.getRisks()));
        }
        if (CollectionUtils.isNotEmpty(riskIds)) riskMongoRepository.safeDeleteByIds(riskIds);
        processingActivityMongoRepository.safeDeleteById(processingActivityId);
        return true;

    }


    public boolean deleteSubProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger subProcessingActivityId) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        ProcessingActivity subProcessingActivity = processingActivityMongoRepository.safeDeleteById(subProcessingActivityId);
        if (!Optional.ofNullable(subProcessingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Sub Processing Activity", subProcessingActivityId);
        }
        if (CollectionUtils.isNotEmpty(subProcessingActivity.getRisks()))
            riskMongoRepository.safeDeleteByIds(subProcessingActivity.getRisks());
        processingActivity.getSubProcessingActivities().remove(subProcessingActivityId);
        processingActivityMongoRepository.save(processingActivity);
        return true;

    }


    public List<ProcessingActivityResponseDTO> getAllProcessingActivityWithMetaData(Long unitId) {
        List<ProcessingActivityResponseDTO> processingActivityResponseDTOList = processingActivityMongoRepository.getAllProcessingActivityAndMetaDataAndSubProcessingActivities(unitId);
        processingActivityResponseDTOList.forEach(processingActivityResponseDTO -> {
            if (!Optional.ofNullable(processingActivityResponseDTO.getSubProcessingActivities().get(0).getId()).isPresent()) {
                processingActivityResponseDTO.setSubProcessingActivities(new ArrayList<>());
            }
        });
        return processingActivityResponseDTOList;
    }


    /**
     * @param unitId
     * @param processingActivityId processing activity id
     * @param active               status of processing activity
     * @return
     */
    public boolean changeStatusOfProcessingActivity(Long unitId, BigInteger processingActivityId, boolean active) {
        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        processingActivity.setActive(active);
        processingActivityMongoRepository.save(processingActivity);
        return true;
    }


    /**
     * @param processingActivityId
     * @return
     * @description method return audit history of Processing Activity , old Object list and latest version also.
     * return object contain  changed field with key fields and values with key Values in return list of map
     */
    public List<Map<String, Object>> getProcessingActivityActivitiesHistory(BigInteger processingActivityId) {

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(processingActivityId, ProcessingActivity.class);
        List<CdoSnapshot> changes = javers.findSnapshots(jqlQuery.build());
        changes.sort((o1, o2) -> -1 * (int) o1.getVersion() - (int) o2.getVersion());
        return javersCommonService.getHistoryMap(changes, processingActivityId, ProcessingActivity.class);

    }

    /**
     * @param unitId
     * @return
     * @description method return processing activities and SubProcessing Activities with basic detail ,name,description
     */
    public List<ProcessingActivityBasicResponseDTO> getAllProcessingActivityBasicDetailsAndWithSubProcess(Long unitId) {
        return processingActivityMongoRepository.getAllProcessingActivityBasicDetailWithSubProcessingActivities(unitId);
    }


    /**
     * @param unitId
     * @param processingActivityId
     * @param activityRelatedDataSubjects list of data subject which contain list of data category and data Element list
     * @return
     */
    public boolean mapDataSubjectDataCategoryAndDataElementToProcessingActivity(Long unitId, BigInteger processingActivityId, List<ProcessingActivityRelatedDataSubject> activityRelatedDataSubjects) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }

        processingActivity.setDataSubjects(activityRelatedDataSubjects);
        processingActivityMongoRepository.save(processingActivity);
        return true;
    }

    /**
     * @param unitId
     * @param processingActivityId
     * @param assetId              - asset id linked with processing activity
     * @return
     * @description map asset with processing activity (related tab processing activity)
     */
    public boolean mapAssetWithProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger assetId) {
        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        List<BigInteger> assetIds = processingActivity.getLinkedAssets();
        assetIds.add(assetId);
        processingActivity.setLinkedAssets(assetIds);
        processingActivityMongoRepository.save(processingActivity);
        return true;

    }

    /**
     * @param unitId
     * @param processingActivityId
     * @return
     * @description map Data Subject ,Data category and Data Element with processing activity(related tab processing activity)
     */
    public List<DataSubjectMappingResponseDTO> getDataSubjectDataCategoryAndDataElementsMappedWithProcessingActivity(Long unitId, BigInteger processingActivityId) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        List<DataSubjectMappingResponseDTO> dataSubjectList = new ArrayList<>();
        List<ProcessingActivityRelatedDataSubject> mappedDataSubjectList = processingActivity.getDataSubjects();
        if (!mappedDataSubjectList.isEmpty()) {
            List<BigInteger> dataSubjectIdList = new ArrayList<>();
            Map<BigInteger, List<ProcessingActivityRelatedDataCategory>> relatedDataCategoryMap = new HashMap<>();
            for (ProcessingActivityRelatedDataSubject processingActivityRelatedDataSubject : mappedDataSubjectList) {
                dataSubjectIdList.add(processingActivityRelatedDataSubject.getId());
                relatedDataCategoryMap.put(processingActivityRelatedDataSubject.getId(), processingActivityRelatedDataSubject.getDataCategories());
            }
            dataSubjectList = processingActivityMongoRepository.getAllMappedDataSubjectWithDataCategoryAndDataElement(unitId, dataSubjectIdList);
            filterSelectedDataSubjectDataCategoryAndDataElementForProcessingActivity(dataSubjectList, relatedDataCategoryMap);

        }
        return dataSubjectList;
    }


    /**
     * @param unitId
     * @param processingActivityId
     * @param dataSubjectId
     * @return
     */
    public boolean removeLinkedDataSubjectFromProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger dataSubjectId) {

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


    /**
     * @param unitId
     * @param processingActivityId
     * @return
     */
    public List<AssetBasicResponseDTO> getAllAssetLinkedWithProcessingActivity(Long unitId, BigInteger processingActivityId) {
        return processingActivityMongoRepository.getAllAssetLinkedWithProcessingActivityById(unitId, processingActivityId);
    }


    /**
     * @param unitId
     * @param processingActivityId
     * @param assetId
     * @return
     * @description method removed linked asset id from Processing activity
     */
    public boolean removeLinkedAssetFromProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger assetId) {
        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        processingActivity.getLinkedAssets().remove(assetId);
        processingActivityMongoRepository.save(processingActivity);
        return true;

    }

    /**
     * @param dataSubjectList
     * @param relatedDataCategoryMap
     * @description method filter data Category and there Corresponding data Element ,method filter data Category and remove Data category from data Category response List
     * similarly Data Elements are remove from data Element response list.
     */
    private void filterSelectedDataSubjectDataCategoryAndDataElementForProcessingActivity(List<DataSubjectMappingResponseDTO> dataSubjectList, Map<BigInteger, List<ProcessingActivityRelatedDataCategory>> relatedDataCategoryMap) {

        for (DataSubjectMappingResponseDTO dataSubjectMappingResponseDTO : dataSubjectList) {

            List<ProcessingActivityRelatedDataCategory> relatedDataCategoriesToDataSubject = relatedDataCategoryMap.get(dataSubjectMappingResponseDTO.getId());
            Map<BigInteger, Set<BigInteger>> dataElementsCorrespondingToDataCategory = new HashMap<>();
            relatedDataCategoriesToDataSubject.forEach(dataCategory -> dataElementsCorrespondingToDataCategory.put(dataCategory.getId(), dataCategory.getDataElements()));
            List<DataCategoryResponseDTO> dataCategoryResponseDTOS = new ArrayList<>();
            dataSubjectMappingResponseDTO.getDataCategories().forEach(dataCategoryResponseDTO -> {

                if (dataElementsCorrespondingToDataCategory.containsKey(dataCategoryResponseDTO.getId())) {
                    List<DataElementBasicResponseDTO> dataElementBasicResponseDTOS = new ArrayList<>();
                    Set<BigInteger> dataElementIdList = dataElementsCorrespondingToDataCategory.get(dataCategoryResponseDTO.getId());
                    dataCategoryResponseDTO.getDataElements().forEach(dataElementBasicResponseDTO -> {
                        if (dataElementIdList.contains(dataElementBasicResponseDTO.getId())) {
                            dataElementBasicResponseDTOS.add(dataElementBasicResponseDTO);
                        }
                    });
                    dataCategoryResponseDTO.setDataElements(dataElementBasicResponseDTOS);
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
    public ProcessingActivityRiskDTO createRiskAndLinkWithProcessingActivities(Long unitId, BigInteger processingActivityId, ProcessingActivityRiskDTO processingActivityRiskDTO) {

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

    /**
     * @param unitId
     * @return
     */
    public List<ProcessingActivityRiskResponseDTO> getAllProcessingActivityAndSubProcessingActivitiesWithRisk(Long unitId) {
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
    }


    /**
     * @param unitId
     * @param processingActivityId
     * @return
     */
    public boolean unLinkRiskFromProcessingOrSubProcessingActivityAndSafeDeleteRisk(Long unitId, BigInteger processingActivityId, BigInteger riskId) {
        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        processingActivity.getRisks().remove(riskId);
        riskMongoRepository.safeDeleteById(riskId);
        processingActivityMongoRepository.save(processingActivity);
        return true;
    }


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
        processingActivityMetaDataMap.put("responsibilityTypeList", organizationResponsibilityTypeService.getAllResponsibilityType(unitId));
        processingActivityMetaDataMap.put("processingPurposeList",organizationProcessingPurposeService.getAllProcessingPurpose(unitId));
        processingActivityMetaDataMap.put("dataSourceList",organizationDataSourceService.getAllDataSource(unitId));
        processingActivityMetaDataMap.put("transferMethodList",organizationTransferMethodService.getAllTransferMethod(unitId));
        processingActivityMetaDataMap.put("accessorPartyList", organizationAccessorPartyService.getAllAccessorParty(unitId));
        processingActivityMetaDataMap.put("processingLegalBasisList",organizationProcessingLegalBasisService.getAllProcessingLegalBasis(unitId));
        processingActivityMetaDataMap.put("riskLevelList", RiskSeverity.values());
        return processingActivityMetaDataMap;

    }


}

