package com.kairos.service.data_inventory.processing_activity;


import com.kairos.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivityRelatedDataCategory;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivityRelatedDataSubject;
import com.kairos.persistance.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistance.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistance.repository.master_data.data_category_element.DataSubjectMappingRepository;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.response.dto.data_inventory.ProcessingActivityBasicResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataCategoryResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataElementBasicResponseDTO;
import com.kairos.response.dto.master_data.data_mapping.DataSubjectMappingResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
import com.kairos.service.master_data.processing_activity_masterdata.*;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
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

    @Inject
    private Javers javers;

    @Inject
    private JaversCommonService javersCommonService;

    @Inject
    private AssetMongoRepository assetMongoRepository;

    @Inject
    private DataSubjectMappingRepository dataSubjectMappingRepository;


    public ProcessingActivityDTO createProcessingActivity(Long organizationId, ProcessingActivityDTO processingActivityDTO) {


        ProcessingActivity exist = processingActivityMongoRepository.findByName(organizationId, processingActivityDTO.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Processing Activity", processingActivityDTO.getName());
        }
        ProcessingActivity processingActivity = buildProcessingActivity(organizationId, processingActivityDTO);
        if (!processingActivityDTO.getSubProcessingActivities().isEmpty()) {
            processingActivity.setSubProcessingActivities(createSubProcessingActivity(organizationId, processingActivityDTO.getSubProcessingActivities()));
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
        processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(organizationId, id);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", id);
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
        subProcessingActivities = processingActivityMongoRepository.saveAll(subProcessingActivities);
        subProcessingActivities.forEach(processingActivity -> subProcessingActivityIdList.add(processingActivity.getId()));
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
        processingActivityMongoRepository.saveAll(subProcessingActivities);

    }


    /**
     * @param unitId
     * @param processingActivityId
     * @return
     * @description method delete processing activity and Sub processing activity is activity is associated with asset then method simply return  without deleting activities
     */
    public boolean deleteProcessingActivity(Long unitId, BigInteger processingActivityId) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        delete(processingActivity);
        return true;

    }


    public boolean deleteSubProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger subProcessingActivityId) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        ProcessingActivity subProcessingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(unitId, subProcessingActivityId);
        if (!Optional.ofNullable(subProcessingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Sub Processing Activity", subProcessingActivityId);
        }
        delete(subProcessingActivity);
        processingActivity.getSubProcessingActivities().remove(subProcessingActivityId);
        processingActivityMongoRepository.save(processingActivity);
        return true;

    }


    /**
     * @param orgId
     * @param id
     * @return
     * @description method return list of SubProcessing Activity
     */
    public List<ProcessingActivityResponseDTO> getProcessingActivityWithWithSubProcessingActivitiesById(Long orgId, BigInteger id) {
        return processingActivityMongoRepository.getAllSubProcessingActivitiesOfProcessingActivity(orgId, id);

    }


    public List<ProcessingActivityResponseDTO> getAllProcessingActivityWithMetaData(Long orgId) {
        return processingActivityMongoRepository.getAllProcessingActivityAndMetaData(orgId);
    }


    /**
     * @param processingActivityId
     * @return
     * @description method return audit history of Processing Activity , old Object list and latest version also.
     * return object contain  changed field with key fields and values with key Values in return list of map
     */
    public List<Map<String, Object>> getProcessingActivityActivitiesHistory(BigInteger processingActivityId, int size, int skip) {

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(processingActivityId, ProcessingActivity.class).limit(size).skip(skip);
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

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(unitId, processingActivityId);
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
     * @param assetId
     * @return
     * @description map asset with processing activity (related tab processing activity)
     */
    public boolean mapAssetWithProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger assetId) {
        ProcessingActivity processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        processingActivity.setAssetId(assetId);
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

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(unitId, processingActivityId);
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
    public boolean removelinkedDataSubjectFromProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger dataSubjectId) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(unitId, processingActivityId);
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
     * @param assetId
     * @return
     * @description method removed linked asset id from Processing activity
     */
    public boolean removelinkedAssetFromProcessingActivity(Long unitId, BigInteger processingActivityId, BigInteger assetId) {
        ProcessingActivity processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        processingActivity.setAssetId(null);
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
            Map<BigInteger, Set<BigInteger>> dataElementsCoresspondingToDataCategory = new HashMap<>();
            relatedDataCategoriesToDataSubject.forEach(dataCategory -> {
                dataElementsCoresspondingToDataCategory.put(dataCategory.getId(), dataCategory.getDataElements());
            });
            List<DataCategoryResponseDTO> dataCategoryResponseDTOS = new ArrayList<>();
            dataSubjectMappingResponseDTO.getDataCategories().forEach(dataCategoryResponseDTO -> {

                if (dataElementsCoresspondingToDataCategory.containsKey(dataCategoryResponseDTO.getId())) {
                    List<DataElementBasicResponseDTO> dataElementBasicResponseDTOS = new ArrayList<>();
                    Set<BigInteger> dataELementIdList = dataElementsCoresspondingToDataCategory.get(dataCategoryResponseDTO.getId());
                    dataCategoryResponseDTO.getDataElements().forEach(dataElementBasicResponseDTO -> {
                        if (dataELementIdList.contains(dataElementBasicResponseDTO.getId())) {
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
}

