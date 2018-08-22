package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistance.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import com.kairos.gdpr.master_data.MasterProcessingActivityDTO;
import com.kairos.persistance.repository.master_data.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.mongodb.MongoClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.IDS_LIST;
import static com.kairos.constants.AppConstant.PROCESSING_ACTIVITIES;


@Service
public class MasterProcessingActivityService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterProcessingActivityService.class);


    @Inject
    private MasterProcessingActivityRepository masterProcessingActivityRepository;

    @Inject
    private ExceptionService exceptionService;


    /**
     * @param countryId
     * @param organizationId
     * @param masterProcessingActivityDto
     * @return master processing Activity with Sub processing activity .
     * create Master processing activity and new Sub processing activity list and set ids to master processing activities
     */
    public MasterProcessingActivityDTO createMasterProcessingActivity(Long countryId, Long organizationId, MasterProcessingActivityDTO masterProcessingActivityDto) {

        if (masterProcessingActivityRepository.findByName(countryId, organizationId, masterProcessingActivityDto.getName()) != null) {
            exceptionService.duplicateDataException("message.duplicate", "processing activity", masterProcessingActivityDto.getName().toLowerCase());
        }
        MasterProcessingActivity masterProcessingActivity = new MasterProcessingActivity(masterProcessingActivityDto.getName(), masterProcessingActivityDto.getDescription(), masterProcessingActivityDto.getOrganizationTypes()
                , masterProcessingActivityDto.getOrganizationSubTypes(), masterProcessingActivityDto.getOrganizationServices(), masterProcessingActivityDto.getOrganizationSubServices());
        Map<String, Object> subProcessingActivity = new HashMap<>();
        if (Optional.ofNullable(masterProcessingActivityDto.getSubProcessingActivities()).isPresent() && !masterProcessingActivityDto.getSubProcessingActivities().isEmpty()) {
            subProcessingActivity = createNewSubProcessingActivity(countryId, organizationId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto);
            masterProcessingActivity.setHasSubProcessingActivity(true);
            masterProcessingActivity.setSubProcessingActivityIds((List<BigInteger>) subProcessingActivity.get(IDS_LIST));
        }
        masterProcessingActivity.setCountryId(countryId);
        masterProcessingActivity.setOrganizationId(organizationId);
        try {
            masterProcessingActivity = masterProcessingActivityRepository.save(masterProcessingActivity);
        } catch (MongoClientException e) {
            masterProcessingActivityRepository.deleteAll((List<MasterProcessingActivity>) subProcessingActivity.get(PROCESSING_ACTIVITIES));
            LOGGER.info(e.getMessage());
            throw new MongoClientException(e.getMessage());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        masterProcessingActivityDto.setId(masterProcessingActivity.getId());
        return masterProcessingActivityDto;
    }


    /**
     * @param countryId
     * @param organizationId
     * @param subProcessingActivities
     * @param parentProcessingActivity required to get organization types ,sub types and Services category and Sub Service Category list for sub processing activity
     * @return return map of Sub processing activities list and ids of sub processing activity
     */
    public Map<String, Object> createNewSubProcessingActivity(Long countryId, Long organizationId, List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivityDTO parentProcessingActivity) {

        List<String> checkDuplicateInSubProcess = new ArrayList<>();
        List<MasterProcessingActivity> subProcessingActivityList = new ArrayList<>();
        for (MasterProcessingActivityDTO activity : subProcessingActivities) {
            if (checkDuplicateInSubProcess.contains(activity.getName())) {
                throw new DuplicateDataException("Duplicate Sub processing Activity " + activity.getName());
            }
            checkDuplicateInSubProcess.add(activity.getName());
            MasterProcessingActivity subProcessingActivity = new MasterProcessingActivity(activity.getName(), activity.getDescription(), parentProcessingActivity.getOrganizationTypes()
                    , parentProcessingActivity.getOrganizationSubTypes(), parentProcessingActivity.getOrganizationServices(), parentProcessingActivity.getOrganizationSubServices());
            subProcessingActivity.setOrganizationId(organizationId);
            subProcessingActivity.setCountryId(countryId);
            subProcessingActivity.setSubProcess(true);
            subProcessingActivityList.add(subProcessingActivity);
        }

        subProcessingActivityList = masterProcessingActivityRepository.saveAll(subProcessingActivityList);
        List<BigInteger> subProcessingActivityIds = new ArrayList<>();
        subProcessingActivityList.forEach(o -> subProcessingActivityIds.add(o.getId()));
        Map<String, Object> result = new HashMap<>();
        result.put(IDS_LIST, subProcessingActivityIds);
        result.put(PROCESSING_ACTIVITIES, subProcessingActivityList);
        return result;

    }


    public List<MasterProcessingActivity> getAllMasterProcessingActivity(Long countryId, Long organizationId) {
        return masterProcessingActivityRepository.getAllMasterProcessingActivity(countryId, organizationId);

    }


    /**
     * updateExistingAndCreateNewSubProcessingActivity(countryId, organizationId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto)
     * is used for updating and creating new sub processing activity
     *
     * @param countryId
     * @param organizationId
     * @param id
     * @param masterProcessingActivityDto contain list of existing(which need to be update) and new(for creating new sub process) Sub processing activities
     * @return master processing activity with sub processing activities list ids
     */
    public MasterProcessingActivityDTO updateMasterProcessingActivityAndSubProcessingActivities(Long countryId, Long organizationId, BigInteger id, MasterProcessingActivityDTO masterProcessingActivityDto) {

        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByName(countryId, organizationId, masterProcessingActivityDto.getName());
        if (Optional.ofNullable(processingActivity).isPresent() && !id.equals(processingActivity.getId())) {
            throw new DuplicateDataException("processing Activity with name Already Exist" + processingActivity.getName());
        }
        processingActivity = masterProcessingActivityRepository.findByid(id);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);
        } else {
            Map<String, Object> subProcessingActivity;
            if (!masterProcessingActivityDto.getSubProcessingActivities().isEmpty()) {
                subProcessingActivity = updateExistingAndCreateNewSubProcessingActivity(countryId, organizationId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto);
                processingActivity.setSubProcessingActivityIds((List<BigInteger>) subProcessingActivity.get(IDS_LIST));
                processingActivity.setHasSubProcessingActivity(true);

            }
            processingActivity.setOrganizationTypes(masterProcessingActivityDto.getOrganizationTypes());
            processingActivity.setOrganizationSubTypes(masterProcessingActivityDto.getOrganizationSubTypes());
            processingActivity.setOrganizationServices(masterProcessingActivityDto.getOrganizationServices());
            processingActivity.setOrganizationSubServices(masterProcessingActivityDto.getOrganizationSubServices());
            processingActivity.setDescription(masterProcessingActivityDto.getDescription());
            processingActivity.setName(masterProcessingActivityDto.getName());
            try {
                masterProcessingActivityRepository.save(processingActivity);

            } catch (MongoClientException e) {
                LOGGER.info(e.getMessage());
                throw new MongoClientException(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return masterProcessingActivityDto;
    }


    /**
     * @param countryId
     * @param organizationId
     * @param subProcessingActivities
     * @param parentProcessingActivity for inheriting organization types,sub types,Service category and Sub service category for sub processing activities
     * @return
     */
    public Map<String, Object> updateExistingAndCreateNewSubProcessingActivity(Long countryId, Long organizationId, List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivityDTO parentProcessingActivity) {

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

        Map<String, Object> updatedSubProcessingActivities = new HashMap<>();
        List<BigInteger> subProcessingActivityIds = new ArrayList<>();
        List<MasterProcessingActivity> subProcessingActivityList = new ArrayList<>();
        if (!createNewSubProcessingActivities.isEmpty()) {
            Map<String, Object> newSubProcessingActivities = createNewSubProcessingActivity(countryId, organizationId, createNewSubProcessingActivities, parentProcessingActivity);
            subProcessingActivityIds.addAll((List<BigInteger>) newSubProcessingActivities.get(IDS_LIST));
            subProcessingActivityList.addAll((List<MasterProcessingActivity>) newSubProcessingActivities.get(PROCESSING_ACTIVITIES));
        }
        if (!updateSubProcessingActivities.isEmpty()) {
            Map<String, Object> updatedSubProcessingActivityList = updateSubProcessingActivities(countryId, organizationId, updateSubProcessingActivities, parentProcessingActivity);
            subProcessingActivityIds.addAll((List<BigInteger>) updatedSubProcessingActivityList.get(IDS_LIST));
            subProcessingActivityList.addAll((List<MasterProcessingActivity>) updatedSubProcessingActivityList.get(PROCESSING_ACTIVITIES));
        }
        updatedSubProcessingActivities.put(IDS_LIST, subProcessingActivityIds);
        updatedSubProcessingActivities.put(PROCESSING_ACTIVITIES, subProcessingActivityList);
        return updatedSubProcessingActivities;

    }


    /**
     * @param countryId
     * @param organizationId
     * @param subProcessingActivities  list of existing Sub processing activities
     * @param parentProcessingActivity for inheriting organization types,sub types,Service category and Sub service category for sub processing activities
     * @return map which contain list of ids and list of sub processing activities
     */
    public Map<String, Object> updateSubProcessingActivities(Long countryId, Long organizationId, List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivityDTO parentProcessingActivity) {


        Map<BigInteger, MasterProcessingActivityDTO> subProcessingActivityDTOList = new HashMap<>();
        List<BigInteger> subProcessingActivitiesIds = new ArrayList<>();
        subProcessingActivities.forEach(subProcess -> {
            subProcessingActivityDTOList.put(subProcess.getId(), subProcess);
            subProcessingActivitiesIds.add(subProcess.getId());
        });
        List<MasterProcessingActivity> subProcessingActivityList = masterProcessingActivityRepository.getAllMasterSubProcessingActivityByIds(countryId, organizationId, subProcessingActivitiesIds);
        subProcessingActivityList.forEach(subProcess -> {

            MasterProcessingActivityDTO subProcessDto = subProcessingActivityDTOList.get(subProcess.getId());
            subProcess.setName(subProcessDto.getName());
            subProcess.setDescription(parentProcessingActivity.getDescription());
            subProcess.setOrganizationTypes(parentProcessingActivity.getOrganizationTypes());
            subProcess.setOrganizationSubTypes(parentProcessingActivity.getOrganizationSubTypes());
            subProcess.setOrganizationServices(parentProcessingActivity.getOrganizationServices());
            subProcess.setOrganizationSubServices(parentProcessingActivity.getOrganizationSubServices());
        });
        Map<String, Object> result = new HashMap<>();
        try {
            subProcessingActivityList = masterProcessingActivityRepository.saveAll(subProcessingActivityList);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e.getMessage());

        }

        result.put(IDS_LIST, subProcessingActivitiesIds);
        result.put(PROCESSING_ACTIVITIES, subProcessingActivityList);
        return result;

    }


    public List<MasterProcessingActivityResponseDTO> getMasterProcessingActivityListWithSubProcessing(Long countryId, Long organizationId) {
        return masterProcessingActivityRepository.getMasterProcessingActivityListWithSubProcessingActivity(countryId, organizationId);

    }


    public Boolean deleteMasterProcessingActivity(Long countryId, Long organizationId, BigInteger id) {
        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByIdAndCountryIdAndNonDeleted(countryId, organizationId, id);
        if (processingActivity == null) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        }
        delete(processingActivity);
        return true;

    }


    /**
     * @param countryId
     * @param organizationId
     * @param processingActivityId
     * @param subProcessingActivityId
     * @return
     */
    public boolean deleteSubProcessingActivity(Long countryId, Long organizationId, BigInteger processingActivityId, BigInteger subProcessingActivityId) {
        MasterProcessingActivity processingActivity = masterProcessingActivityRepository.findByIdAndCountryIdAndNonDeleted(countryId, organizationId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        List<BigInteger> subProcessingActviityIdList = processingActivity.getSubProcessingActivityIds();
        MasterProcessingActivity subProcessingActivity = masterProcessingActivityRepository.findByIdAndCountryIdAndNonDeleted(countryId, organizationId, subProcessingActivityId);
        if (!Optional.ofNullable(subProcessingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Sub Processing Activity", subProcessingActivityId);

        } else {
            subProcessingActviityIdList.remove(subProcessingActivityId);
            processingActivity.setSubProcessingActivityIds(subProcessingActviityIdList);
            masterProcessingActivityRepository.save(processingActivity);
            delete(subProcessingActivity);
        }
        return true;

    }


    public MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessing(Long countryId, Long organizationId, BigInteger id) {
        MasterProcessingActivityResponseDTO result = masterProcessingActivityRepository.getMasterProcessingActivityWithSubProcessingActivity(countryId, organizationId, id);
        if (!Optional.of(result).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);
        } else
            return result;

    }


    public void checkForDuplicacyInName(List<MasterProcessingActivityDTO> processingActivityDTOs) {
        List<String> names = new ArrayList<>();
        processingActivityDTOs.forEach(dataElementDto -> {
            if (names.contains(dataElementDto.getName())) {
                throw new DuplicateDataException("Duplicate Sub process Activity " + dataElementDto.getName());
            }
            names.add(dataElementDto.getName());
        });


    }

}
