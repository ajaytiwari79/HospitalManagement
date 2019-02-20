package com.kairos.service.master_data.processing_activity_masterdata;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.*;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import com.kairos.persistence.model.embeddables.ServiceCategory;
import com.kairos.persistence.model.embeddables.SubServiceCategory;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivityDeprecated;
import com.kairos.dto.gdpr.master_data.MasterProcessingActivityDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.MasterProcessingActivityRiskResponseDTO;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.risk_management.RiskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;


@Service
public class MasterProcessingActivityService{

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterProcessingActivityService.class);


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private RiskService riskService;

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
            exceptionService.duplicateDataException("message.duplicate", "processing activity", masterProcessingActivityDto.getName().toLowerCase());
        }
        MasterProcessingActivity masterProcessingActivity = new MasterProcessingActivity(masterProcessingActivityDto.getName(), masterProcessingActivityDto.getDescription(), SuggestedDataStatus.APPROVED, countryId);
        masterProcessingActivity = getMetadataOfMasterProcessingActivity(masterProcessingActivityDto, masterProcessingActivity);
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
     *  This method is used to fetch all the metadata related to master asset from DTO like organisationType,
     *  organisationSubType, Service Category and Sub Service Category
     *
     * @param masterProcessingActivityDto
     * @return
     */
    //TODO need to make common method for asset and processing activity and others
    private MasterProcessingActivity getMetadataOfMasterProcessingActivity(MasterProcessingActivityDTO masterProcessingActivityDto, MasterProcessingActivity masterProcessingActivity){
        masterProcessingActivity.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterProcessingActivityDto.getOrganizationTypes(), OrganizationType.class));
        masterProcessingActivity.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterProcessingActivityDto.getOrganizationSubServices(), OrganizationSubType.class));
        masterProcessingActivity.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(masterProcessingActivityDto.getOrganizationServices(), ServiceCategory.class));
        masterProcessingActivity.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(masterProcessingActivityDto.getOrganizationSubServices(), SubServiceCategory.class));
        return masterProcessingActivity;
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
            subProcessingActivity = getMetadataOfMasterProcessingActivity(activity, subProcessingActivity);
            subProcessingActivityList.add(subProcessingActivity);
        }
       return subProcessingActivityList;

    }


    /**
     * updateExistingAndCreateNewSubProcessingActivity(countryId, organizationId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto)
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
            throw new DuplicateDataException("processing Activity with name Already Exist" + processingActivity.getName());
        }
        processingActivity = masterProcessingActivityRepository.getOne(id);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);
        } else {
            if (!masterProcessingActivityDto.getSubProcessingActivities().isEmpty()) {
               List<MasterProcessingActivity> subProcessingActivities = updateExistingAndCreateNewSubProcessingActivity(countryId, masterProcessingActivityDto.getSubProcessingActivities(), processingActivity);
                processingActivity.setHasSubProcessingActivity(true);
                processingActivity.setSubProcessingActivities(subProcessingActivities);

            }
            getMetadataOfMasterProcessingActivity(masterProcessingActivityDto, processingActivity);
            processingActivity.setDescription(masterProcessingActivityDto.getDescription());
            processingActivity.setName(masterProcessingActivityDto.getName());
            try {
                masterProcessingActivityRepository.save(processingActivity);

            }catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
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
            subProcessingActivityList.addAll(updateSubProcessingActivities(countryId, updateSubProcessingActivities, parentProcessingActivity));
        }
        return subProcessingActivityList;

    }


    /**
     * @param countryId
     * @param subProcessingActivities  list of existing Sub processing activities
     * @param parentProcessingActivity for inheriting organization types,sub types,Service category and Sub service category for sub processing activities
     * @return map which contain list of ids and list of sub processing activities
     */
    private List<MasterProcessingActivity> updateSubProcessingActivities(Long countryId, List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivity parentProcessingActivity) {


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
        List<MasterProcessingActivity> masterProcessingActivities =  masterProcessingActivityRepository.findAllByCountryId(countryId);
        List<MasterProcessingActivityResponseDTO> masterProcessingActivityResponseDTOS = new ArrayList<>();
        for(MasterProcessingActivity masterProcessingActivity : masterProcessingActivities){
            masterProcessingActivityResponseDTOS.add(prepareMasterProcessingActivityResponseDTO(masterProcessingActivity, new ArrayList<>()));
        }
        return masterProcessingActivityResponseDTOS;

    }


    public Boolean deleteMasterProcessingActivity(Long countryId, Long id) {
        Integer updateCount = masterProcessingActivityRepository.updateMasterProcessingActivity(countryId, id);
        if(updateCount > 0){
            LOGGER.info("Master Processing Activity is deleted successfully with id :: {}", id);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Master Processing Activity", id);
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
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }else{
            LOGGER.info("Sub processing Activity deleted successfully");
        }
        return true;

    }


    public MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessing(Long countryId, Long id) {
        MasterProcessingActivity masterProcessingActivity = masterProcessingActivityRepository.getMasterAssetByCountryIdAndId(countryId, id);
        if (!Optional.of(masterProcessingActivity).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);
        }
            return prepareMasterProcessingActivityResponseDTO(masterProcessingActivity, new ArrayList<>());

    }

    private MasterProcessingActivityResponseDTO  prepareMasterProcessingActivityResponseDTO(MasterProcessingActivity processingActivity, List<MasterProcessingActivityResponseDTO> masterSPAResponseDTO){
        MasterProcessingActivityResponseDTO masterPAResponseDTO = new MasterProcessingActivityResponseDTO(processingActivity.getId(),processingActivity.getName(),processingActivity.getDescription(), processingActivity.getSuggestedDate(), processingActivity.getSuggestedDataStatus());
        List<OrganizationTypeDTO> organizationTypes = new ArrayList<>();
        List<OrganizationSubTypeDTO> organizationSubTypes = new ArrayList<>();
        List<ServiceCategoryDTO> serviceCategories = new ArrayList<>();
        List<SubServiceCategoryDTO> subServiceCategories = new ArrayList<>();
        for(OrganizationType orgType : processingActivity.getOrganizationTypes()){
            organizationTypes.add(new OrganizationTypeDTO(orgType.getId(), orgType.getName())) ;
        }
        for(OrganizationSubType orgSubType : processingActivity.getOrganizationSubTypes()){
            organizationSubTypes.add(new OrganizationSubTypeDTO(orgSubType.getId(), orgSubType.getName())) ;
        }
        for(ServiceCategory category : processingActivity.getOrganizationServices()){
            serviceCategories.add(new ServiceCategoryDTO(category.getId(), category.getName())) ;
        }
        for(SubServiceCategory subServiceCategory : processingActivity.getOrganizationSubServices()){
            subServiceCategories.add(new SubServiceCategoryDTO(subServiceCategory.getId(), subServiceCategory.getName())) ;
        }

        masterPAResponseDTO.setOrganizationTypes(organizationTypes);
        masterPAResponseDTO.setOrganizationSubTypes(organizationSubTypes);
        masterPAResponseDTO.setOrganizationServices(serviceCategories);
        masterPAResponseDTO.setOrganizationSubServices(subServiceCategories);
        if(processingActivity.isHasSubProcessingActivity()) {
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
        MasterProcessingActivity masterProcessingActivity = masterProcessingActivityRepository.getMasterAssetByCountryIdAndId(countryId, processingActivityId);
        if (!Optional.ofNullable(masterProcessingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        if (!processingActivityRiskDTO.getRisks().isEmpty()) {
            List<Risk> processingActivityRisks = ObjectMapperUtils.copyPropertiesOfListByMapper(processingActivityRiskDTO.getRisks(), Risk.class);
            processingActivityRisks.forEach(risk -> risk.setCountryId(countryId));
            masterProcessingActivity.setRisks(processingActivityRisks);
        }
        if (!processingActivityRiskDTO.getSubProcessingActivities().isEmpty()) {
            processingActivityRiskDTO.getSubProcessingActivities().forEach( subProcessingActivity -> {
                createRiskAndLinkWithProcessingActivityAndSubProcessingActivity(countryId, subProcessingActivity.getId(),subProcessingActivity);
            });
        }
        masterProcessingActivityRepository.save(masterProcessingActivity);
        return processingActivityRiskDTO;
    }


    /**
     * @param countryId
     * @param processingActivityId
     * @return
     */
    public Boolean deleteRiskAndUnlinkFromProcessingActivityOrSubProcessingActivity(Long countryId, BigInteger processingActivityId, BigInteger riskId) {
      //TODO
       /* MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByIdAndCountryId(countryId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);

        }
        processingActivity.getRisks().remove(riskId);
        riskMongoRepository.safeDeleteById(riskId);
        masterProcessingActivityRepository.save(processingActivity);*/
        return true;

    }


    /**
     * @param countryId
     * @return -method return list of Processing Activity and Risks linked with them
     */
    public List<MasterProcessingActivityRiskResponseDTO> getAllMasterProcessingActivityWithSubProcessingActivitiesAndRisks(Long countryId) {
       //TODO
        /* List<MasterProcessingActivityRiskResponseDTO> masterProcessingActivityRiskResponseDTOS = masterProcessingActivityRepository.getAllProcessingActivityWithLinkedRisksAndSubProcessingActivitiesByCountryId(countryId);
        masterProcessingActivityRiskResponseDTOS.forEach(masterProcessingActivity -> {
            if (!Optional.ofNullable(masterProcessingActivity.getProcessingActivities().get(0).getId()).isPresent()) {
                masterProcessingActivity.setProcessingActivities(new ArrayList<>());
            }
            masterProcessingActivity.getProcessingActivities().add(0, new MasterProcessingActivityRiskResponseDTO(masterProcessingActivity.getId(), masterProcessingActivity.getName(), true, masterProcessingActivity.getRisks(),masterProcessingActivity.getSuggestedDate(),masterProcessingActivity.getSuggestedDataStatus()));
            masterProcessingActivity.setMainParent(true);
            masterProcessingActivity.setRisks(new ArrayList<>());
        });*/
        return new ArrayList<>();


    }


    /**
     * @param countryId             -country id
     * @param unitId                -unit id which suggest Processing Activity to country admin
     * @param processingActivityDTO -contain basic detail about Processing Activity ,name and description
     * @return
     */
    public ProcessingActivityDTO saveSuggestedMasterProcessingActivityDataFromUnit(Long countryId, Long unitId, ProcessingActivityDTO processingActivityDTO) {
  //TODO
        /*      MasterProcessingActivity previousProcessingActivity = masterProcessingActivityRepository.findByName(countryId, processingActivityDTO.getName());
        if (Optional.ofNullable(previousProcessingActivity).isPresent()) {
            return null;
        }
        OrgTypeSubTypeServicesAndSubServicesDTO orgTypeSubTypeServicesAndSubServicesDTO = restClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/organization_type/", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrgTypeSubTypeServicesAndSubServicesDTO>>() {
        });
        MasterProcessingActivity processingActivity = buildSuggestedProcessingActivityAndSubProcessingActivity(countryId, processingActivityDTO, orgTypeSubTypeServicesAndSubServicesDTO);
        if (Optional.ofNullable(processingActivityDTO.getSubProcessingActivities()).isPresent() && CollectionUtils.isNotEmpty(processingActivityDTO.getSubProcessingActivities())) {
            List<MasterProcessingActivity> subProcessingActivities = new ArrayList<>();
            processingActivityDTO.getSubProcessingActivities().forEach(subProcessingActivityDTO -> {
                if (subProcessingActivityDTO.isSuggestToCountryAdmin()) {
                    processingActivity.setHasSubProcessingActivity(true);
                    subProcessingActivities.add(buildSuggestedProcessingActivityAndSubProcessingActivity(countryId, subProcessingActivityDTO, orgTypeSubTypeServicesAndSubServicesDTO).setSubProcess(true));
                }
            });

            if (CollectionUtils.isNotEmpty(subProcessingActivities)) {
                processingActivity.setSubProcessingActivityIds(masterProcessingActivityRepository.saveAll(getNextSequence(subProcessingActivities)).stream().map(MasterProcessingActivity::getId).collect(Collectors.toList()));
            }
        }
        masterProcessingActivityRepository.save(processingActivity);*/
        return processingActivityDTO;
    }


    private MasterProcessingActivityDeprecated buildSuggestedProcessingActivityAndSubProcessingActivity(Long countryId, ProcessingActivityDTO processingActivityDTO, OrgTypeSubTypeServicesAndSubServicesDTO orgTypeSubTypeServicesAndSubServicesDTO) {
        MasterProcessingActivityDeprecated processingActivity = new MasterProcessingActivityDeprecated(processingActivityDTO.getName(), processingActivityDTO.getDescription(), countryId, SuggestedDataStatus.PENDING, LocalDate.now());
        processingActivity.setOrganizationServices(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationServices())
                .setOrganizationTypeDTOS(Arrays.asList(new OrganizationTypeDTO(orgTypeSubTypeServicesAndSubServicesDTO.getId(), orgTypeSubTypeServicesAndSubServicesDTO.getName())))
                .setOrganizationSubTypeDTOS(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubTypeDTOS())
                .setOrganizationSubServices(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubServices());
        return processingActivity;
    }


    /**
     * @param countryId
     * @param processingActivityId
     * @param suggestedDataStatus
     * @return
     */
    public boolean updateSuggestedStatusOfMasterProcessingActivity(Long countryId, BigInteger processingActivityId, SuggestedDataStatus suggestedDataStatus) {
//TODO
      /*  MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByIdAndCountryId(countryId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);

        }
        processingActivity.setSuggestedDataStatus(suggestedDataStatus);
        masterProcessingActivityRepository.save(processingActivity);*/
        return true;
    }


    public boolean updateSuggestedStatusOfSubProcessingActivities(Long countryId, BigInteger processingActivityId, Set<BigInteger> subProcessingActiivtyIds, SuggestedDataStatus suggestedDataStatus) {
//TODO
      /*  MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByIdAndCountryId(countryId, processingActivityId);
        if (!processingActivity.getSuggestedDataStatus().value.equals(SuggestedDataStatus.APPROVED.value)) {
            exceptionService.invalidRequestException("message.processingActivity.notApproved", processingActivity.getName(), processingActivity.getSuggestedDataStatus(), SuggestedDataStatus.APPROVED);
        }
        List<MasterProcessingActivity> subProcessingActivityList = masterProcessingActivityRepository.findAllMasterSubProcessingActivityByIds(countryId, subProcessingActiivtyIds);
        subProcessingActivityList.forEach(subProcessingActivity -> subProcessingActivity.setSuggestedDataStatus(suggestedDataStatus));
        masterProcessingActivityRepository.saveAll(getNextSequence(subProcessingActivityList));*/
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
