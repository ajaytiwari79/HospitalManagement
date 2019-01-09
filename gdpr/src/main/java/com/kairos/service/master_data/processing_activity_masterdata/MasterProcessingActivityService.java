package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.commons.client.RestTemplateResponseEnvelope;


import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.*;
import com.kairos.dto.gdpr.data_inventory.ProcessingActivityDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.OrganizationSubType;
import com.kairos.persistence.model.master_data.default_asset_setting.OrganizationType;
import com.kairos.persistence.model.master_data.default_asset_setting.ServiceCategory;
import com.kairos.persistence.model.master_data.default_asset_setting.SubServiceCategory;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.dto.gdpr.master_data.MasterProcessingActivityDTO;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivityMD;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.MasterProcessingActivityMDRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.persistence.repository.risk_management.RiskMongoRepository;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.MasterProcessingActivityRiskResponseDTO;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.risk_management.RiskService;
import com.mongodb.MongoClientException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


import static com.kairos.constants.AppConstant.IDS_LIST;
import static com.kairos.constants.AppConstant.PROCESSING_ACTIVITIES;


@Service
public class MasterProcessingActivityService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterProcessingActivityService.class);


    @Inject
    private MasterProcessingActivityRepository masterProcessingActivityRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private RiskService riskService;

    @Inject
    private RiskMongoRepository riskMongoRepository;

    @Inject
    private GenericRestClient restClient;

    @Inject
    private MasterProcessingActivityMDRepository masterProcessingActivityMDRepository;


    /**
     * @param countryId
     * @param masterProcessingActivityDto
     * @return master processing Activity with Sub processing activity .
     * create Master processing activity and new Sub processing activity list and set ids to master processing activities
     */
    public MasterProcessingActivityDTO createMasterProcessingActivity(Long countryId, MasterProcessingActivityDTO masterProcessingActivityDto) {

        if (masterProcessingActivityMDRepository.findByNameAndCountryId(masterProcessingActivityDto.getName(), countryId) != null) {
            exceptionService.duplicateDataException("message.duplicate", "processing activity", masterProcessingActivityDto.getName().toLowerCase());
        }
        MasterProcessingActivityMD masterProcessingActivity = new MasterProcessingActivityMD(masterProcessingActivityDto.getName(), masterProcessingActivityDto.getDescription(), SuggestedDataStatus.APPROVED, countryId);
        masterProcessingActivity = getMetadataOfMasterProcessingActivity(masterProcessingActivityDto, masterProcessingActivity);
        masterProcessingActivity.setSubProcessActivity(false);
        if (Optional.ofNullable(masterProcessingActivityDto.getSubProcessingActivities()).isPresent() && !masterProcessingActivityDto.getSubProcessingActivities().isEmpty()) {
            List<MasterProcessingActivityMD> subProcessingActivity = createNewSubProcessingActivity(countryId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivity);
            masterProcessingActivity.setHasSubProcessingActivity(true);
            masterProcessingActivity.setSubProcessingActivities(subProcessingActivity);
        }
        try {
            masterProcessingActivity = masterProcessingActivityMDRepository.save(masterProcessingActivity);
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
    private MasterProcessingActivityMD getMetadataOfMasterProcessingActivity(MasterProcessingActivityDTO masterProcessingActivityDto, MasterProcessingActivityMD masterProcessingActivity){
        List<OrganizationType> organizationTypes = new ArrayList<>();
        List<OrganizationSubType> organizationSubTypes = new ArrayList<>();
        List<ServiceCategory> serviceCategories = new ArrayList<>();
        List<SubServiceCategory> subServiceCategories = new ArrayList<>();
        for(OrganizationTypeDTO organizationTypeDTO : masterProcessingActivityDto.getOrganizationTypes()){
            OrganizationType orgType = new OrganizationType(organizationTypeDTO.getId(), organizationTypeDTO.getName());
            organizationTypes.add(orgType);
        }
        for(OrganizationSubTypeDTO organizationSubTypeDTO : masterProcessingActivityDto.getOrganizationSubTypes()){
            OrganizationSubType orgSubType = new OrganizationSubType(organizationSubTypeDTO.getId(), organizationSubTypeDTO.getName());
            organizationSubTypes.add(orgSubType);
        }
        for(ServiceCategoryDTO category : masterProcessingActivityDto.getOrganizationServices()){
            ServiceCategory serviceCategory = new ServiceCategory(category.getId(), category.getName());
            serviceCategories.add(serviceCategory);
        }
        for(SubServiceCategoryDTO subCategory : masterProcessingActivityDto.getOrganizationSubServices()){
            SubServiceCategory subServiceCategory = new SubServiceCategory(subCategory.getId(), subCategory.getName());
            subServiceCategories.add(subServiceCategory);
        }
        masterProcessingActivity.setOrganizationTypes(organizationTypes);
        masterProcessingActivity.setOrganizationSubTypes(organizationSubTypes);
        masterProcessingActivity.setOrganizationServices(serviceCategories);
        masterProcessingActivity.setOrganizationSubServices(subServiceCategories);
        return masterProcessingActivity;
    }


    /**
     * @param countryId
     * @param subProcessingActivities
     * @param parentProcessingActivity required to get organization types ,sub types and Services category and Sub Service Category list for sub processing activity
     * @return return map of Sub processing activities list and ids of sub processing activity
     */
    public List<MasterProcessingActivityMD> createNewSubProcessingActivity(Long countryId,
                      List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivityMD parentProcessingActivity) {

        List<String> checkDuplicateInSubProcess = new ArrayList<>();
        List<MasterProcessingActivityMD> subProcessingActivityList = new ArrayList<>();
        for (MasterProcessingActivityDTO activity : subProcessingActivities) {
            if (checkDuplicateInSubProcess.contains(activity.getName())) {
                throw new DuplicateDataException("Duplicate Sub processing Activity " + activity.getName());
            }
            checkDuplicateInSubProcess.add(activity.getName());
            MasterProcessingActivityMD subProcessingActivity = new MasterProcessingActivityMD(activity.getName(), activity.getDescription(), SuggestedDataStatus.APPROVED, countryId);
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

        MasterProcessingActivityMD processingActivity = masterProcessingActivityMDRepository.findByNameAndCountryId(masterProcessingActivityDto.getName(), countryId);
        if (Optional.ofNullable(processingActivity).isPresent() && !id.equals(processingActivity.getId())) {
            throw new DuplicateDataException("processing Activity with name Already Exist" + processingActivity.getName());
        }
        processingActivity = masterProcessingActivityMDRepository.getOne(id);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);
        } else {
            ;
           if (!masterProcessingActivityDto.getSubProcessingActivities().isEmpty()) {
               List<MasterProcessingActivityMD> subProcessingActivities = updateExistingAndCreateNewSubProcessingActivity(countryId, masterProcessingActivityDto.getSubProcessingActivities(), processingActivity);
                processingActivity.setHasSubProcessingActivity(true);
                processingActivity.setSubProcessingActivities(subProcessingActivities);

            }
            processingActivity = getMetadataOfMasterProcessingActivity(masterProcessingActivityDto, processingActivity);
            processingActivity.setDescription(masterProcessingActivityDto.getDescription());
            processingActivity.setName(masterProcessingActivityDto.getName());
            try {
                masterProcessingActivityMDRepository.save(processingActivity);

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
    public List<MasterProcessingActivityMD> updateExistingAndCreateNewSubProcessingActivity(Long countryId, List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivityMD parentProcessingActivity) {

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

        List<MasterProcessingActivityMD> subProcessingActivityList = new ArrayList<>();
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
    public List<MasterProcessingActivityMD> updateSubProcessingActivities(Long countryId, List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivityMD parentProcessingActivity) {


        Map<Long, MasterProcessingActivityDTO> subProcessingActivityDTOList = new HashMap<>();
        subProcessingActivities.forEach(subProcess -> {
            subProcessingActivityDTOList.put(subProcess.getId(), subProcess);
        });
        List<MasterProcessingActivityMD> subProcessingActivityList = parentProcessingActivity.getSubProcessingActivities();
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
        return masterProcessingActivityRepository.getMasterProcessingActivityListWithSubProcessingActivity(countryId);

    }


    public Boolean deleteMasterProcessingActivity(Long countryId, BigInteger id) {
        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByIdAndCountryId(countryId, id);
        if (processingActivity == null) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        }
        delete(processingActivity);
        return true;

    }


    /**
     * @param countryId
     * @param processingActivityId
     * @param subProcessingActivityId
     * @return
     */
    public boolean deleteSubProcessingActivity(Long countryId, BigInteger processingActivityId, BigInteger subProcessingActivityId) {
        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByIdAndCountryId(countryId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        List<BigInteger> subProcessingActivityIdList = processingActivity.getSubProcessingActivityIds();
        MasterProcessingActivity subProcessingActivity = masterProcessingActivityRepository.findByIdAndCountryId(countryId, subProcessingActivityId);
        if (!Optional.ofNullable(subProcessingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Sub Processing Activity", subProcessingActivityId);

        } else {
            subProcessingActivityIdList.remove(subProcessingActivityId);
            processingActivity.setSubProcessingActivityIds(subProcessingActivityIdList);
            masterProcessingActivityRepository.save(processingActivity);
            delete(subProcessingActivity);
        }
        return true;

    }


    public MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessing(Long countryId, BigInteger id) {
        MasterProcessingActivityResponseDTO result = masterProcessingActivityRepository.getMasterProcessingActivityWithSubProcessingActivity(countryId, id);
        if (!Optional.of(result).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);
        } else
            return result;

    }


    /**
     * @param countryId
     * @param processingActivityId
     * @param processingActivityRiskDTO
     * @return
     */
    public MasterProcessingActivityRiskDTO createRiskAndLinkWithProcessingActivityAndSubProcessingActivity(Long countryId, BigInteger processingActivityId, MasterProcessingActivityRiskDTO processingActivityRiskDTO) {


        MasterProcessingActivity masterProcessingActivity = masterProcessingActivityRepository.findByIdAndCountryId(countryId, processingActivityId);
        if (!Optional.ofNullable(masterProcessingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        List<MasterProcessingActivity> processingActivityList = new ArrayList<>();
        processingActivityList.add(masterProcessingActivity);
        Map<MasterProcessingActivity, List<BasicRiskDTO>> riskListCorrespondingToProcessingActivity = new HashMap<>();
        if (!processingActivityRiskDTO.getRisks().isEmpty()) {
            riskListCorrespondingToProcessingActivity.put(masterProcessingActivity, processingActivityRiskDTO.getRisks());

        }
        if (!processingActivityRiskDTO.getSubProcessingActivities().isEmpty()) {
            Set<BigInteger> subProcessingActivityIds = new HashSet<>();
            Map<BigInteger, List<BasicRiskDTO>> subProcessingActivityAndRiskDtoListMap = new HashMap<>();
            processingActivityRiskDTO.getSubProcessingActivities().forEach(subProcessingActivityRiskDTO -> {
                subProcessingActivityIds.add(subProcessingActivityRiskDTO.getId());
                subProcessingActivityAndRiskDtoListMap.put(subProcessingActivityRiskDTO.getId(), subProcessingActivityRiskDTO.getRisks());
            });
            List<MasterProcessingActivity> subProcessingActivityList = masterProcessingActivityRepository.findAllMasterSubProcessingActivityByIds(countryId, subProcessingActivityIds);
            for (MasterProcessingActivity subProcessingActivity : subProcessingActivityList) {
                if (!subProcessingActivityAndRiskDtoListMap.get(subProcessingActivity.getId()).isEmpty()) {
                    riskListCorrespondingToProcessingActivity.put(subProcessingActivity, subProcessingActivityAndRiskDtoListMap.get(subProcessingActivity.getId()));
                }
            }
            processingActivityList.addAll(subProcessingActivityList);
        }
        if (!riskListCorrespondingToProcessingActivity.isEmpty()) {
            Map<MasterProcessingActivity, List<Risk>> riskIdListCorresponsingProcessingActivities = riskService.saveRiskAtCountryLevelOrOrganizationLevel(countryId, false, riskListCorrespondingToProcessingActivity);
            processingActivityList.forEach(processingActivity -> processingActivity.setRisks(riskIdListCorresponsingProcessingActivities.get(processingActivity).stream().map(Risk::getId).collect(Collectors.toList())));
        }
        masterProcessingActivityRepository.saveAll(getNextSequence(processingActivityList));
        return processingActivityRiskDTO;
    }


    /**
     * @param countryId
     * @param processingActivityId
     * @return
     */
    public Boolean deleteRiskAndUnlinkFromProcessingActivityOrSubProcessingActivity(Long countryId, BigInteger processingActivityId, BigInteger riskId) {
        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByIdAndCountryId(countryId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);

        }
        processingActivity.getRisks().remove(riskId);
        riskMongoRepository.safeDeleteById(riskId);
        masterProcessingActivityRepository.save(processingActivity);
        return true;

    }


    /**
     * @param countryId
     * @return -method return list of Processing Activity and Risks linked with them
     */
    public List<MasterProcessingActivityRiskResponseDTO> getAllMasterProcessingActivityWithSubProcessingActivitiesAndRisks(Long countryId) {
        List<MasterProcessingActivityRiskResponseDTO> masterProcessingActivityRiskResponseDTOS = masterProcessingActivityRepository.getAllProcessingActivityWithLinkedRisksAndSubProcessingActivitiesByCountryId(countryId);
        masterProcessingActivityRiskResponseDTOS.forEach(masterProcessingActivity -> {
            if (!Optional.ofNullable(masterProcessingActivity.getProcessingActivities().get(0).getId()).isPresent()) {
                masterProcessingActivity.setProcessingActivities(new ArrayList<>());
            }
            masterProcessingActivity.getProcessingActivities().add(0, new MasterProcessingActivityRiskResponseDTO(masterProcessingActivity.getId(), masterProcessingActivity.getName(), true, masterProcessingActivity.getRisks(),masterProcessingActivity.getSuggestedDate(),masterProcessingActivity.getSuggestedDataStatus()));
            masterProcessingActivity.setMainParent(true);
            masterProcessingActivity.setRisks(new ArrayList<>());
        });
        return masterProcessingActivityRiskResponseDTOS;


    }


    /**
     * @param countryId             -country id
     * @param unitId                -unit id which suggest Processing Activity to country admin
     * @param processingActivityDTO -contain basic detail about Processing Activity ,name and description
     * @return
     */
    public ProcessingActivityDTO saveSuggestedMasterProcessingActivityDataFromUnit(Long countryId, Long unitId, ProcessingActivityDTO processingActivityDTO) {
        MasterProcessingActivity previousProcessingActivity = masterProcessingActivityRepository.findByName(countryId, processingActivityDTO.getName());
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
        masterProcessingActivityRepository.save(processingActivity);
        return processingActivityDTO;
    }


    private MasterProcessingActivity buildSuggestedProcessingActivityAndSubProcessingActivity(Long countryId, ProcessingActivityDTO processingActivityDTO, OrgTypeSubTypeServicesAndSubServicesDTO orgTypeSubTypeServicesAndSubServicesDTO) {
        MasterProcessingActivity processingActivity = new MasterProcessingActivity(processingActivityDTO.getName(), processingActivityDTO.getDescription(), countryId, SuggestedDataStatus.PENDING, LocalDate.now());
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

        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByIdAndCountryId(countryId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);

        }
        processingActivity.setSuggestedDataStatus(suggestedDataStatus);
        masterProcessingActivityRepository.save(processingActivity);
        return true;
    }


    public boolean updateSuggestedStatusOfSubProcessingActivities(Long countryId, BigInteger processingActivityId, Set<BigInteger> subProcessingActiivtyIds, SuggestedDataStatus suggestedDataStatus) {

        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByIdAndCountryId(countryId, processingActivityId);
        if (!processingActivity.getSuggestedDataStatus().value.equals(SuggestedDataStatus.APPROVED.value)) {
            exceptionService.invalidRequestException("message.processingActivity.notApproved", processingActivity.getName(), processingActivity.getSuggestedDataStatus(), SuggestedDataStatus.APPROVED);
        }
        List<MasterProcessingActivity> subProcessingActivityList = masterProcessingActivityRepository.findAllMasterSubProcessingActivityByIds(countryId, subProcessingActiivtyIds);
        subProcessingActivityList.forEach(subProcessingActivity -> subProcessingActivity.setSuggestedDataStatus(suggestedDataStatus));
        masterProcessingActivityRepository.saveAll(getNextSequence(subProcessingActivityList));
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
