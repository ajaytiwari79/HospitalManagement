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


    public MasterProcessingActivity createMasterProcessingActivity(Long countryId, Long organizationId, MasterProcessingActivityDTO masterProcessingActivityDto) {

        if (masterProcessingActivityRepository.findByName(countryId, organizationId, masterProcessingActivityDto.getName()) != null) {
            exceptionService.duplicateDataException("message.duplicate", "processing activity", masterProcessingActivityDto.getName().toLowerCase());
        }
        MasterProcessingActivity masterProcessingActivity = new MasterProcessingActivity(countryId, masterProcessingActivityDto.getName(), masterProcessingActivityDto.getDescription());
        Map<String, Object> subProcessingActivity = new HashMap<>();
        if (masterProcessingActivityDto.getSubProcessingActivities() != null && masterProcessingActivityDto.getSubProcessingActivities().size() != 0) {
            subProcessingActivity = addSubProcessingActivity(countryId, organizationId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto);
            masterProcessingActivity.setSubProcessingActivityIds((List<BigInteger>) subProcessingActivity.get(IDS_LIST));
        }
        masterProcessingActivity.setOrganizationTypes(masterProcessingActivityDto.getOrganizationTypes());
        masterProcessingActivity.setOrganizationSubTypes(masterProcessingActivityDto.getOrganizationSubTypes());
        masterProcessingActivity.setOrganizationServices(masterProcessingActivityDto.getOrganizationServices());
        masterProcessingActivity.setOrganizationSubServices(masterProcessingActivityDto.getOrganizationSubServices());
        masterProcessingActivity.setCountryId(countryId);
        masterProcessingActivity.setOrganizationId(organizationId);
        try {

            masterProcessingActivity = masterProcessingActivityRepository.save(sequence(masterProcessingActivity));
        } catch (MongoClientException e) {
            masterProcessingActivityRepository.deleteAll((List<MasterProcessingActivity>) subProcessingActivity.get(PROCESSING_ACTIVITIES));
            LOGGER.info(e.getMessage());
            throw new MongoClientException(e.getMessage());
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }

        return masterProcessingActivity;
    }


    public Map<String, Object> addSubProcessingActivity(Long countryId, Long organizationId, List<MasterProcessingActivityDTO> subProcessingActivities, MasterProcessingActivityDTO processingActivityDto) {

        List<String> checkDuplicateInSubProcess = new ArrayList<>();
        List<MasterProcessingActivity> subProcessingActivityList = new ArrayList<>();
        for (MasterProcessingActivityDTO activity : subProcessingActivities) {
            if (checkDuplicateInSubProcess.contains(activity.getName())) {
                throw new DuplicateDataException("sub processing name Duplicacy " + activity.getName());
            }
            checkDuplicateInSubProcess.add(activity.getName());
            MasterProcessingActivity subProcessingActivity = new MasterProcessingActivity(countryId, activity.getName(), activity.getDescription());
            subProcessingActivity.setOrganizationTypes(processingActivityDto.getOrganizationTypes());
            subProcessingActivity.setSubProcess(true);
            subProcessingActivity.setOrganizationSubTypes(processingActivityDto.getOrganizationSubTypes());
            subProcessingActivity.setOrganizationServices(processingActivityDto.getOrganizationServices());
            subProcessingActivity.setOrganizationSubServices(processingActivityDto.getOrganizationSubServices());
            subProcessingActivity.setOrganizationId(organizationId);
            subProcessingActivityList.add(subProcessingActivity);
        }

        checkForDuplicacyByName(countryId, organizationId, checkDuplicateInSubProcess);
        subProcessingActivityList = masterProcessingActivityRepository.saveAll(sequence(subProcessingActivityList));
        List<BigInteger> subProcessingActicitiesIds = new ArrayList<>();
        subProcessingActivityList.forEach(o -> subProcessingActicitiesIds.add(o.getId()));
        Map<String, Object> result = new HashMap<>();
        result.put(IDS_LIST, subProcessingActicitiesIds);
        result.put(PROCESSING_ACTIVITIES, subProcessingActivityList);
        return result;

    }

    public void checkForDuplicacyByName(Long countryId, Long orgId, List<String> subProcessingAcitivityNames) {
        List<MasterProcessingActivity> processingActivities = masterProcessingActivityRepository.masterProcessingActivityListByNames(countryId, orgId, subProcessingAcitivityNames);
        if (processingActivities.size() != 0) {
            throw new DuplicateDataException(" sub processing acitvity " + processingActivities.get(0).getName() + " exist");
        }
    }


    public List<MasterProcessingActivity> getAllmasterProcessingActivity(Long countryId, Long organizationId) {
        return masterProcessingActivityRepository.getAllMasterProcessingsctivity(countryId, organizationId);

    }


    public MasterProcessingActivity updateMasterProcessingActivity(Long countryId, Long organizationId, BigInteger id, MasterProcessingActivityDTO masterProcessingActivityDto) {

        MasterProcessingActivity exists = masterProcessingActivityRepository.findByName(countryId, organizationId, masterProcessingActivityDto.getName());
        if (Optional.ofNullable(exists).isPresent() && !id.equals(exists.getId())) {
            throw new DuplicateDataException("processing Activity with name Already Exist" + exists.getName());
        }
        exists = masterProcessingActivityRepository.findByid(id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);
        } else {
            Map<String, Object> subProcessingActivity = new HashMap<>();
            exists.setOrganizationTypes(masterProcessingActivityDto.getOrganizationTypes());
            exists.setOrganizationSubTypes(masterProcessingActivityDto.getOrganizationSubTypes());
            exists.setOrganizationServices(masterProcessingActivityDto.getOrganizationServices());
            exists.setOrganizationSubServices(masterProcessingActivityDto.getOrganizationSubServices());
            if (masterProcessingActivityDto.getSubProcessingActivities() != null && masterProcessingActivityDto.getSubProcessingActivities().size() != 0) {
                subProcessingActivity = addSubProcessingActivity(countryId, organizationId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto);
                exists.setSubProcessingActivityIds((List<BigInteger>) subProcessingActivity.get(IDS_LIST));
            }
            exists.setDescription(masterProcessingActivityDto.getDescription());
            exists.setName(masterProcessingActivityDto.getName());
            try {
                exists = masterProcessingActivityRepository.save(sequence(exists));

            } catch (MongoClientException e) {
                LOGGER.info(e.getMessage());
                throw new MongoClientException(e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return exists;
    }

    public MasterProcessingActivityResponseDTO getMasterProcessingActivityWithSubProcessing(Long countryId, Long organizationId, BigInteger id) {
        MasterProcessingActivityResponseDTO result = masterProcessingActivityRepository.getMasterProcessingActivityWithSubProcessingActivity(countryId, organizationId, id);
        if (!Optional.of(result).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);
        } else
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


}
