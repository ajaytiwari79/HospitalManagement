package com.kairos.service.master_data.processing_activity_masterdata;

import com.kairos.custom_exception.DataNotFoundByIdException;
import com.kairos.custom_exception.DuplicateDataException;
import com.kairos.persistance.model.master_data.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.dto.master_data.MasterProcessingActivityDTO;
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
     *
     * @param countryId
     * @param organizationId
     * @param masterProcessingActivityDto
     * @return master processing Activity with Sub processing activity .
     * create Master processing activity and new Sub processing activity list and set ids to master processing activities
     */
    public MasterProcessingActivity createMasterProcessingActivity(Long countryId, Long organizationId, MasterProcessingActivityDTO masterProcessingActivityDto) {

        if (masterProcessingActivityRepository.findByName(countryId, organizationId, masterProcessingActivityDto.getName()) != null) {
            exceptionService.duplicateDataException("message.duplicate", "processing activity", masterProcessingActivityDto.getName().toLowerCase());
        }
        MasterProcessingActivity masterProcessingActivity = new MasterProcessingActivity(masterProcessingActivityDto.getName(), masterProcessingActivityDto.getDescription(), masterProcessingActivityDto.getOrganizationTypes()
                , masterProcessingActivityDto.getOrganizationSubTypes(), masterProcessingActivityDto.getOrganizationServices(), masterProcessingActivityDto.getOrganizationSubServices());
        Map<String, Object> subProcessingActivity = new HashMap<>();
        if (Optional.ofNullable(masterProcessingActivityDto.getSubProcessingActivities()).isPresent() && !masterProcessingActivityDto.getSubProcessingActivities().isEmpty()) {
            subProcessingActivity = createNewSubProcessingActivity(countryId, organizationId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto);
            masterProcessingActivity.setHasSubProcess(true);
            masterProcessingActivity.setSubProcessingActivityIds((List<BigInteger>) subProcessingActivity.get(IDS_LIST));
        }
        masterProcessingActivity.setCountryId(countryId);
        masterProcessingActivity.setOrganizationId(organizationId);
        try {
            masterProcessingActivity = masterProcessingActivityRepository.save(sequenceGenerator(masterProcessingActivity));
        } catch (MongoClientException e) {
            masterProcessingActivityRepository.deleteAll((List<MasterProcessingActivity>) subProcessingActivity.get(PROCESSING_ACTIVITIES));
            LOGGER.info(e.getMessage());
            throw new MongoClientException(e.getMessage());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }

        return masterProcessingActivity;
    }


    /**
     *
     * @param countryId
     * @param organizationId
     * @param subProcessingActivities
     * @param parentProcessingActivity required to get oranization types ,sub types and Services category and Sub Service Category list for sub processing activity
     * @return return map of Subprocessing activities list and ids of sub processing activity
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

        subProcessingActivityList = masterProcessingActivityRepository.saveAll(sequenceGenerator(subProcessingActivityList));
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
     *updateExistingAndCreateNewSubProcessingActivity(countryId, organizationId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto)
     * is used for updating and creating new sub processing activity
     * @param countryId
     * @param organizationId
     * @param id
     * @param masterProcessingActivityDto  contain list of existing(which need to be update) and new(for creating new sub process) Sub processing activities
     * @return master processing activity with sub processing activities list ids
     */
    public MasterProcessingActivity updateMasterProcessingActivityAndSubProcessingActivities(Long countryId, Long organizationId, BigInteger id, MasterProcessingActivityDTO masterProcessingActivityDto) {

        MasterProcessingActivity exists = masterProcessingActivityRepository.findByName(countryId, organizationId, masterProcessingActivityDto.getName());
        if (Optional.ofNullable(exists).isPresent() && !id.equals(exists.getId())) {
            throw new DuplicateDataException("processing Activity with name Already Exist" + exists.getName());
        }
        exists = masterProcessingActivityRepository.findByid(id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);
        } else {
            Map<String, Object> subProcessingActivity = new HashMap<>();
            if (!masterProcessingActivityDto.getSubProcessingActivities().isEmpty()) {
                subProcessingActivity = updateExistingAndCreateNewSubProcessingActivity(countryId, organizationId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto);
                exists.setSubProcessingActivityIds((List<BigInteger>) subProcessingActivity.get(IDS_LIST));
            }
            exists.setOrganizationTypes(masterProcessingActivityDto.getOrganizationTypes());
            exists.setOrganizationSubTypes(masterProcessingActivityDto.getOrganizationSubTypes());
            exists.setOrganizationServices(masterProcessingActivityDto.getOrganizationServices());
            exists.setOrganizationSubServices(masterProcessingActivityDto.getOrganizationSubServices());
            exists.setDescription(masterProcessingActivityDto.getDescription());
            exists.setName(masterProcessingActivityDto.getName());
            try {
                exists = masterProcessingActivityRepository.save(sequenceGenerator(exists));

            } catch (MongoClientException e) {
                LOGGER.info(e.getMessage());
                throw new MongoClientException(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return exists;
    }


    /**
     *
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
     *
     * @param countryId
     * @param organizationId
     * @param subProcessingActivities list of existing Sub processing activities
     * @param parentProcessingActivity for inheriting organization types,sub types,Service category and Sub service category for sub processing activities
     * @return map which contain list of ids and list of sub processing activities
     */
    public Map<String, Object> updateSubProcessingActivities(Long countryId, Long organizationId, List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivityDTO parentProcessingActivity) {


        Map<BigInteger, MasterProcessingActivityDTO> subProcessingActivityDTOList = new HashMap<>();
        List<BigInteger> subProcessingActivitiesIds = new ArrayList<>();
        List<String> subProcessingActivityNames = new ArrayList<>();
        subProcessingActivities.forEach(subProcess -> {
            subProcessingActivityDTOList.put(subProcess.getId(), subProcess);
            subProcessingActivitiesIds.add(subProcess.getId());
            subProcessingActivityNames.add(subProcess.getName());
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
            subProcessingActivityList = masterProcessingActivityRepository.saveAll(sequenceGenerator(subProcessingActivityList));
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
        MasterProcessingActivity exists = masterProcessingActivityRepository.findByIdAndCountryIdAndNonDeleted(countryId, organizationId, id);
        if (exists == null) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        }
        delete(exists);
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
