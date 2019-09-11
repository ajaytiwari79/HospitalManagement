package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.*;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.dto.gdpr.master_data.MasterProcessingActivityDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import com.kairos.persistence.model.embeddables.ServiceCategory;
import com.kairos.persistence.model.embeddables.SubServiceCategory;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.persistence.repository.risk_management.RiskRepository;
import com.kairos.response.dto.common.RiskBasicResponseDTO;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.MasterProcessingActivityRiskResponseDTO;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;


@Service
public class MasterProcessingActivityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterProcessingActivityService.class);


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private RiskRepository riskRepository;

    @Inject
    private GenericRestClient restClient;

    @Inject
    private MasterProcessingActivityRepository masterProcessingActivityRepository;


    /**
     * @param countryId
     * @param masterProcessingActivityDto
     * @return master processing Activity with Sub processing activity .
     * create Master processing activity and new Sub processing activity list and set ids to master processing activities
     */
    public MasterProcessingActivityDTO createMasterProcessingActivity(Long countryId, MasterProcessingActivityDTO masterProcessingActivityDto) {

        if (masterProcessingActivityRepository.findByNameAndCountryId(masterProcessingActivityDto.getName(), countryId) != null) {
            exceptionService.duplicateDataException("message.duplicate", "message.processingActivity", masterProcessingActivityDto.getName().toLowerCase());
        }
        MasterProcessingActivity masterProcessingActivity = new MasterProcessingActivity(masterProcessingActivityDto.getName(), masterProcessingActivityDto.getDescription(), SuggestedDataStatus.APPROVED, countryId);
        setMetadataOfMasterProcessingActivity(masterProcessingActivityDto, masterProcessingActivity);
        masterProcessingActivity.setSubProcessActivity(false);
        if (Optional.ofNullable(masterProcessingActivityDto.getSubProcessingActivities()).isPresent() && !masterProcessingActivityDto.getSubProcessingActivities().isEmpty()) {
            List<MasterProcessingActivity> subProcessingActivity = createNewSubProcessingActivity(countryId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivity);
            masterProcessingActivity.setHasSubProcessingActivity(true);
            masterProcessingActivity.setSubProcessingActivities(subProcessingActivity);
        }
        try {
            masterProcessingActivity = masterProcessingActivityRepository.save(masterProcessingActivity);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        masterProcessingActivityDto.setId(masterProcessingActivity.getId());
        return masterProcessingActivityDto;
    }

    /**
     * This method is used to fetch all the metadata related to master asset from DTO like organisationType,
     * organisationSubType, Service Category and Sub Service Category
     *
     * @param masterProcessingActivityDto
     * @return
     */
    private void setMetadataOfMasterProcessingActivity(MasterProcessingActivityDTO masterProcessingActivityDto, MasterProcessingActivity masterProcessingActivity) {
        masterProcessingActivity.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(new ArrayList<>(masterProcessingActivityDto.getOrganizationTypes()), OrganizationType.class));
        masterProcessingActivity.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(new ArrayList<>(masterProcessingActivityDto.getOrganizationSubTypes()), OrganizationSubType.class));
        masterProcessingActivity.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(new ArrayList<>(masterProcessingActivityDto.getOrganizationServices()), ServiceCategory.class));
        masterProcessingActivity.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(new ArrayList<>(masterProcessingActivityDto.getOrganizationSubServices()), SubServiceCategory.class));
    }


    /**
     * @param countryId
     * @param subProcessingActivities
     * @param parentProcessingActivity required to get organization types ,sub types and Services category and Sub Service Category list for sub processing activity
     * @return return map of Sub processing activities list and ids of sub processing activity
     */
    private List<MasterProcessingActivity> createNewSubProcessingActivity(Long countryId,
                                                                          List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivity parentProcessingActivity) {

        List<String> checkDuplicateInSubProcess = new ArrayList<>();
        List<MasterProcessingActivity> subProcessingActivityList = new ArrayList<>();
        for (MasterProcessingActivityDTO activity : subProcessingActivities) {
            if (checkDuplicateInSubProcess.contains(activity.getName())) {
                throw new DuplicateDataException("Duplicate Sub processing Activity " + activity.getName());
            }
            checkDuplicateInSubProcess.add(activity.getName());
            MasterProcessingActivity subProcessingActivity = new MasterProcessingActivity(activity.getName(), activity.getDescription(), SuggestedDataStatus.APPROVED, countryId);
            subProcessingActivity.setSubProcessActivity(true);
            subProcessingActivity.setMasterProcessingActivity(parentProcessingActivity);
            setMetadataOfMasterProcessingActivity(activity, subProcessingActivity);
            subProcessingActivityList.add(subProcessingActivity);
        }
        return subProcessingActivityList;

    }


    /**
     * updateExistingAndCreateNewSubProcessingActivity(countryId, unitId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto)
     * is used for updating and creating new sub processing activity
     *
     * @param countryId
     * @param id
     * @param masterProcessingActivityDto contain list of existing(which need to be update) and new(for creating new sub process) Sub processing activities
     * @return master processing activity with sub processing activities list ids
     */
    public MasterProcessingActivityDTO updateMasterProcessingActivityAndSubProcessingActivities(Long countryId, Long id, MasterProcessingActivityDTO masterProcessingActivityDto) {

        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByNameAndCountryId(masterProcessingActivityDto.getName(), countryId);
        if (Optional.ofNullable(processingActivity).isPresent() && !id.equals(processingActivity.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "message.processingActivity", masterProcessingActivityDto.getName());
        }
        processingActivity = masterProcessingActivityRepository.getOne(id);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.processingActivity", id);
        } else {
            if (!masterProcessingActivityDto.getSubProcessingActivities().isEmpty()) {
                List<MasterProcessingActivity> subProcessingActivities = updateExistingAndCreateNewSubProcessingActivity(countryId, masterProcessingActivityDto.getSubProcessingActivities(), processingActivity);
                processingActivity.setHasSubProcessingActivity(true);
                processingActivity.setSubProcessingActivities(subProcessingActivities);

            }
            setMetadataOfMasterProcessingActivity(masterProcessingActivityDto, processingActivity);
            processingActivity.setDescription(masterProcessingActivityDto.getDescription());
            processingActivity.setName(masterProcessingActivityDto.getName());
            masterProcessingActivityRepository.save(processingActivity);
        }
        return masterProcessingActivityDto;
    }


    /**
     * @param countryId
     * @param subProcessingActivities
     * @param parentProcessingActivity for inheriting organization types,sub types,Service category and Sub service category for sub processing activities
     * @return
     */
    private List<MasterProcessingActivity> updateExistingAndCreateNewSubProcessingActivity(Long countryId, List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivity parentProcessingActivity) {

        checkForDuplicacyInName(subProcessingActivities);
        List<MasterProcessingActivityDTO> updateSubProcessingActivities = new ArrayList<>();
        List<MasterProcessingActivityDTO> createNewSubProcessingActivities = new ArrayList<>();
        subProcessingActivities.forEach(processingActivity -> {
            if (Optional.ofNullable(processingActivity.getId()).isPresent()) {
                updateSubProcessingActivities.add(processingActivity);
            } else {
                createNewSubProcessingActivities.add(processingActivity);
            }
        });

        List<MasterProcessingActivity> subProcessingActivityList = new ArrayList<>();
        if (!createNewSubProcessingActivities.isEmpty()) {
            subProcessingActivityList.addAll(createNewSubProcessingActivity(countryId, createNewSubProcessingActivities, parentProcessingActivity));
        }
        if (!updateSubProcessingActivities.isEmpty()) {
            subProcessingActivityList.addAll(updateSubProcessingActivities( updateSubProcessingActivities, parentProcessingActivity));
        }
        return subProcessingActivityList;

    }


    /**
     * @param subProcessingActivities  list of existing Sub processing activities
     * @param parentProcessingActivity for inheriting organization types,sub types,Service category and Sub service category for sub processing activities
     * @return map which contain list of ids and list of sub processing activities
     */
    private List<MasterProcessingActivity> updateSubProcessingActivities(List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivity parentProcessingActivity) {


        Map<Long, MasterProcessingActivityDTO> subProcessingActivityDTOList = new HashMap<>();
        subProcessingActivities.forEach(subProcess -> subProcessingActivityDTOList.put(subProcess.getId(), subProcess));
        List<MasterProcessingActivity> subProcessingActivityList = parentProcessingActivity.getSubProcessingActivities();
        subProcessingActivityList.forEach(subProcess -> {
            MasterProcessingActivityDTO subProcessDto = subProcessingActivityDTOList.get(subProcess.getId());
            subProcess.setName(subProcessDto.getName());
            subProcess.setDescription(subProcessDto.getDescription());
            subProcess.setOrganizationTypes(parentProcessingActivity.getOrganizationTypes());
            subProcess.setOrganizationSubTypes(parentProcessingActivity.getOrganizationSubTypes());
            subProcess.setOrganizationServices(parentProcessingActivity.getOrganizationServices());
            subProcess.setOrganizationSubServices(parentProcessingActivity.getOrganizationSubServices());
        });
        return subProcessingActivityList;

    }


    public List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityListWithSubProcessing(Long countryId) {
        List<MasterProcessingActivity> masterProcessingActivities = masterProcessingActivityRepository.findAllByCountryId(countryId);
        List<MasterProcessingActivityResponseDTO> masterProcessingActivityResponseDTOS = new ArrayList<>();
        for (MasterProcessingActivity masterProcessingActivity : masterProcessingActivities) {
            masterProcessingActivityResponseDTOS.add(prepareMasterProcessingActivityResponseDTO(masterProcessingActivity, new ArrayList<>()));
        }
        return masterProcessingActivityResponseDTOS;

    }


    public Boolean deleteMasterProcessingActivity(Long countryId, Long id) {
        Integer updateCount = masterProcessingActivityRepository.updateMasterProcessingActivity(countryId, id);
        if (updateCount > 0) {
            LOGGER.info("Master Processing Activity is deleted successfully with id :: {}", id);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.processingActivity", id);
        }
        return true;


    }


    /**
     * @param countryId
     * @param processingActivityId
     * @param subProcessingActivityId
     * @return
     */
    public boolean deleteSubProcessingActivity(Long countryId, Long processingActivityId, Long subProcessingActivityId) {
        Integer updateCount = masterProcessingActivityRepository.deleteSubProcessingActivityFromMasterProcessingActivity(countryId, processingActivityId, subProcessingActivityId);
        if (updateCount <= 0) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.processingActivity", processingActivityId);
        } else {
            LOGGER.info("Sub processing Activity deleted successfully");
        }
        return true;

    }


    public MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessing(Long countryId, Long id) {
        MasterProcessingActivity masterProcessingActivity = masterProcessingActivityRepository.findByCountryIdAndId(countryId, id);
        if (!Optional.of(masterProcessingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.processingActivity", id);
        }
        return prepareMasterProcessingActivityResponseDTO(masterProcessingActivity, new ArrayList<>());

    }

    private MasterProcessingActivityResponseDTO prepareMasterProcessingActivityResponseDTO(MasterProcessingActivity processingActivity, List<MasterProcessingActivityResponseDTO> masterSPAResponseDTO) {
        MasterProcessingActivityResponseDTO masterPAResponseDTO = new MasterProcessingActivityResponseDTO(processingActivity.getId(), processingActivity.getName(), processingActivity.getDescription(), processingActivity.getSuggestedDate(), processingActivity.getSuggestedDataStatus());
        List<OrganizationTypeDTO> organizationTypes = new ArrayList<>();
        List<OrganizationSubTypeDTO> organizationSubTypes = new ArrayList<>();
        List<ServiceCategoryDTO> serviceCategories = new ArrayList<>();
        List<SubServiceCategoryDTO> subServiceCategories = new ArrayList<>();
        for (OrganizationType orgType : processingActivity.getOrganizationTypes()) {
            organizationTypes.add(new OrganizationTypeDTO(orgType.getId(), orgType.getName()));
        }
        for (OrganizationSubType orgSubType : processingActivity.getOrganizationSubTypes()) {
            organizationSubTypes.add(new OrganizationSubTypeDTO(orgSubType.getId(), orgSubType.getName()));
        }
        for (ServiceCategory category : processingActivity.getOrganizationServices()) {
            serviceCategories.add(new ServiceCategoryDTO(category.getId(), category.getName()));
        }
        for (SubServiceCategory subServiceCategory : processingActivity.getOrganizationSubServices()) {
            subServiceCategories.add(new SubServiceCategoryDTO(subServiceCategory.getId(), subServiceCategory.getName()));
        }

        masterPAResponseDTO.setOrganizationTypes(organizationTypes);
        masterPAResponseDTO.setOrganizationSubTypes(organizationSubTypes);
        masterPAResponseDTO.setOrganizationServices(serviceCategories);
        masterPAResponseDTO.setOrganizationSubServices(subServiceCategories);
        if (processingActivity.isHasSubProcessingActivity()) {
            for (MasterProcessingActivity subProcessingActivity : processingActivity.getSubProcessingActivities()) {
                masterSPAResponseDTO.add(prepareMasterProcessingActivityResponseDTO(subProcessingActivity, null));

            }
            masterPAResponseDTO.setSubProcessingActivities(masterSPAResponseDTO);
        }
        return masterPAResponseDTO;
    }


    /**
     * @param countryId
     * @param processingActivityId
     * @param processingActivityRiskDTO
     * @return
     */
    public MasterProcessingActivityRiskDTO createRiskAndLinkWithProcessingActivityAndSubProcessingActivity(Long countryId, Long processingActivityId, MasterProcessingActivityRiskDTO processingActivityRiskDTO) {
        MasterProcessingActivity masterProcessingActivity = masterProcessingActivityRepository.findByCountryIdAndId(countryId, processingActivityId);
        if (!Optional.ofNullable(masterProcessingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.processingActivity", processingActivityId);
        }
        if (!processingActivityRiskDTO.getRisks().isEmpty()) {
            List<Risk> processingActivityRisks = ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivityRiskDTO.getRisks(), Risk.class);
            processingActivityRisks.forEach(risk -> risk.setCountryId(countryId));
            masterProcessingActivity.setRisks(processingActivityRisks);
        }
        if (!processingActivityRiskDTO.getSubProcessingActivities().isEmpty()) {
            processingActivityRiskDTO.getSubProcessingActivities().forEach(subProcessingActivity -> createRiskAndLinkWithProcessingActivityAndSubProcessingActivity(countryId, subProcessingActivity.getId(), subProcessingActivity));
        }
        masterProcessingActivityRepository.save(masterProcessingActivity);
        return processingActivityRiskDTO;
    }


    /**
     * @param countryId
     * @param processingActivityId
     * @return
     */
    public Boolean deleteRiskAndUnlinkFromProcessingActivityOrSubProcessingActivity(Long countryId, Long processingActivityId, Long riskId) {

        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByCountryIdAndId(countryId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.processingActivity", processingActivityId);
        }
        Risk linkedRisk = processingActivity.getRisks().stream().filter(risk -> risk.getId().equals(riskId)).findFirst().orElse(null);
        if (linkedRisk != null) {
            processingActivity.getRisks().remove(linkedRisk);
            riskRepository.delete(linkedRisk);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.risk", processingActivityId);
        }
        masterProcessingActivityRepository.save(processingActivity);
        return true;

    }


    /**
     * @param countryId
     * @return -method return list of Processing Activity and Risks linked with them
     */
    public List<MasterProcessingActivityRiskResponseDTO> getAllMasterProcessingActivityWithSubProcessingActivitiesAndRisks(Long countryId) {
        List<MasterProcessingActivity> masterProcessingActivities = masterProcessingActivityRepository.findAllByCountryId(countryId);
        return prepareMasterProcessingActivityRiskResponseDTOData(masterProcessingActivities, true);
    }

    private List<MasterProcessingActivityRiskResponseDTO> prepareMasterProcessingActivityRiskResponseDTOData(List<MasterProcessingActivity> processingActivities, boolean isParentProcessingActivity) {
        List<MasterProcessingActivityRiskResponseDTO> processingActivityRiskResponseDTOS = new ArrayList<>();
        for (MasterProcessingActivity processingActivity : processingActivities) {
            List<MasterProcessingActivityRiskResponseDTO> subProcessingActivityRiskResponseDTOS = new ArrayList<>();
            MasterProcessingActivityRiskResponseDTO masterProcessingActivityRiskResponseDTO = new MasterProcessingActivityRiskResponseDTO();
            masterProcessingActivityRiskResponseDTO.setId(processingActivity.getId());
            masterProcessingActivityRiskResponseDTO.setMainParent(isParentProcessingActivity);
            masterProcessingActivityRiskResponseDTO.setName(processingActivity.getName());
            if (!isParentProcessingActivity) {
                masterProcessingActivityRiskResponseDTO.setRisks(ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getRisks(), RiskBasicResponseDTO.class));
            }
            List<MasterProcessingActivity> subProcessingActivities = processingActivity.getSubProcessingActivities();
            if (!subProcessingActivities.isEmpty()) {
                subProcessingActivityRiskResponseDTOS = prepareMasterProcessingActivityRiskResponseDTOData(subProcessingActivities, false);
            }
            if (isParentProcessingActivity) {
                subProcessingActivityRiskResponseDTOS.add(0, new MasterProcessingActivityRiskResponseDTO(masterProcessingActivityRiskResponseDTO.getId(), masterProcessingActivityRiskResponseDTO.getName(), masterProcessingActivityRiskResponseDTO.getMainParent(), ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivity.getRisks(), RiskBasicResponseDTO.class), masterProcessingActivityRiskResponseDTO.getSuggestedDate(), masterProcessingActivityRiskResponseDTO.getSuggestedDataStatus()));
                masterProcessingActivityRiskResponseDTO.setProcessingActivities(subProcessingActivityRiskResponseDTOS);
            }
            processingActivityRiskResponseDTOS.add(masterProcessingActivityRiskResponseDTO);
        }
        return processingActivityRiskResponseDTOS;
    }


    /**
     * @param countryId             -country id
     * @param unitId                -unit id which suggest Processing Activity to country admin
     * @param processingActivityDTO -contain basic detail about Processing Activity ,name and description
     * @return
     */
    public ProcessingActivityDTO saveSuggestedMasterProcessingActivityDataFromUnit(Long countryId, Long unitId, ProcessingActivityDTO processingActivityDTO) {
        MasterProcessingActivity previousProcessingActivity = masterProcessingActivityRepository.findByNameAndCountryId(processingActivityDTO.getName(), countryId);
        if (Optional.ofNullable(previousProcessingActivity).isPresent()) {
            return null;
        }
        OrgTypeSubTypeServicesAndSubServicesDTO orgTypeSubTypeServicesAndSubServicesDTO = restClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/organization_type/", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrgTypeSubTypeServicesAndSubServicesDTO>>() {
        });
        MasterProcessingActivity processingActivity = new MasterProcessingActivity(processingActivityDTO.getName(), processingActivityDTO.getDescription(), SuggestedDataStatus.PENDING, countryId);
        addMetaDataToSuggestedProcessingActivity(orgTypeSubTypeServicesAndSubServicesDTO, processingActivity);
        List<MasterProcessingActivity> subProcessingActivities = new ArrayList<>();
        processingActivityDTO.getSubProcessingActivities().forEach(subProcessingActivityDTO -> {
                    MasterProcessingActivity subProcessingActivity = new MasterProcessingActivity(subProcessingActivityDTO.getName(), subProcessingActivityDTO.getDescription(), SuggestedDataStatus.PENDING, countryId);
                    subProcessingActivity.setSubProcessActivity(true);
                    addMetaDataToSuggestedProcessingActivity(orgTypeSubTypeServicesAndSubServicesDTO, subProcessingActivity);
                    subProcessingActivity.setMasterProcessingActivity(processingActivity);
                    subProcessingActivities.add(subProcessingActivity);
                }
        );
        if (CollectionUtils.isNotEmpty(subProcessingActivities)) {
            processingActivity.setHasSubProcessingActivity(true);
            processingActivity.setSubProcessingActivities(subProcessingActivities);
        }
        masterProcessingActivityRepository.save(processingActivity);
        return processingActivityDTO;
    }


    private void addMetaDataToSuggestedProcessingActivity(OrgTypeSubTypeServicesAndSubServicesDTO orgTypeSubTypeServicesAndSubServicesDTO, MasterProcessingActivity processingActivity) {
        processingActivity.setOrganizationTypes(Arrays.asList(new OrganizationType(orgTypeSubTypeServicesAndSubServicesDTO.getId(), orgTypeSubTypeServicesAndSubServicesDTO.getName())));
        processingActivity.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubTypeDTOS(), OrganizationSubType.class));
        processingActivity.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationServices(), ServiceCategory.class));
        processingActivity.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubServices(), SubServiceCategory.class));
    }

    /**
     * @param countryId
     * @param processingActivityIds
     * @param suggestedDataStatus
     * @return
     */
    public boolean updateSuggestedStatusOfMasterProcessingActivities(Long countryId, Set<Long> processingActivityIds, SuggestedDataStatus suggestedDataStatus) {

        List<MasterProcessingActivity> processingActivityList = masterProcessingActivityRepository.findAllByCountryIdAndIds(countryId, processingActivityIds);
        processingActivityList.forEach(masterProcessingActivity -> {
            masterProcessingActivity.setSuggestedDataStatus(suggestedDataStatus);
            masterProcessingActivity.getSubProcessingActivities().forEach(subProcessingActivity -> subProcessingActivity.setSuggestedDataStatus(suggestedDataStatus));
        });
        masterProcessingActivityRepository.saveAll(processingActivityList);
        return true;
    }


    public boolean updateSuggestedStatusOfSubProcessingActivities(Long countryId, Long processingActivityId, Set<Long> subProcessingActivityIds, SuggestedDataStatus suggestedDataStatus) {
        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByCountryIdAndId(countryId, processingActivityId);
        if (!SuggestedDataStatus.APPROVED.value.equals(processingActivity.getSuggestedDataStatus().value)) {
            exceptionService.invalidRequestException("message.processingActivity.notApproved", processingActivity.getName(), processingActivity.getSuggestedDataStatus(), SuggestedDataStatus.APPROVED);
        }
        processingActivity.getSubProcessingActivities().forEach(subProcessingActivity -> {
            if (subProcessingActivityIds.contains(subProcessingActivity.getId())) {
                subProcessingActivity.setSuggestedDataStatus(suggestedDataStatus);
            }
        });
        masterProcessingActivityRepository.save(processingActivity);
        return true;
    }

    private void checkForDuplicacyInName(List<MasterProcessingActivityDTO> processingActivityDTOs) {
        List<String> names = new ArrayList<>();
        processingActivityDTOs.forEach(dataElementDto -> {
            if (names.contains(dataElementDto.getName())) {
                throw new DuplicateDataException("Duplicate Sub process Activity " + dataElementDto.getName());
            }
            names.add(dataElementDto.getName());
        });


    }

}
