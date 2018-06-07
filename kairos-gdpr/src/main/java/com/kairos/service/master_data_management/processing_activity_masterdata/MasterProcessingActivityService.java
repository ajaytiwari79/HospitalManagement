package com.kairos.service.master_data_management.processing_activity_masterdata;

import com.kairos.client.OrganizationTypeRestClient;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.dto.master_data.MasterProcessingActivityDto;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.response.dto.master_data.MasterProcessingActivityResponseDto;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class MasterProcessingActivityService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterProcessingActivityService.class);

    @Inject
    OrganizationTypeRestClient organizationTypeAndServiceRestClient;

    @Inject
    MasterProcessingActivityRepository masterProcessingActivityRepository;

    public MasterProcessingActivity createMasterProcessingActivity(Long countryId, MasterProcessingActivityDto masterProcessingActivityDto) {

        if (masterProcessingActivityRepository.findByNameAndCountryId(countryId, masterProcessingActivityDto.getName()) != null) {

            throw new DuplicateDataException("asset for name " + masterProcessingActivityDto.getName() + " already exists");
        }
        MasterProcessingActivity masterProcessingActivity = new MasterProcessingActivity(countryId, masterProcessingActivityDto.getName(), masterProcessingActivityDto.getDescription());
        try {
            if (masterProcessingActivityDto.getOrganizationTypes() != null && masterProcessingActivityDto.getOrganizationTypes().size() != 0) {
                masterProcessingActivity.setOrganizationTypes(masterProcessingActivityDto.getOrganizationTypes());
            }
            if (masterProcessingActivityDto.getOrganizationSubTypes() != null && masterProcessingActivityDto.getOrganizationSubTypes().size() != 0) {
                masterProcessingActivity.setOrganizationSubTypes(masterProcessingActivityDto.getOrganizationSubTypes());

            }
            if (masterProcessingActivityDto.getOrganizationServices() != null && masterProcessingActivityDto.getOrganizationServices().size() != 0) {
                masterProcessingActivity.setOrganizationServices(masterProcessingActivityDto.getOrganizationServices());

            }
            if (masterProcessingActivityDto.getOrganizationSubServices() != null && masterProcessingActivityDto.getOrganizationSubServices().size() != 0) {
                masterProcessingActivity.setOrganizationSubServices(masterProcessingActivityDto.getOrganizationTypes());

            }
            if (masterProcessingActivityDto.getSubProcessingActivities() != null && masterProcessingActivityDto.getSubProcessingActivities().size() !=0) {
                masterProcessingActivity.setSubProcessingActivityIds(addSubProcessingActivity(countryId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto));
            }

            masterProcessingActivity.setCountryId(countryId);
            masterProcessingActivity.setSubProcess(false);
            masterProcessingActivity = save(masterProcessingActivity);
        } catch (NullPointerException e) {
            LOGGER.warn(e.getMessage());
            e.printStackTrace();
        }

        return masterProcessingActivity;
    }


    public List<BigInteger> addSubProcessingActivity(Long countryId, List<MasterProcessingActivityDto> subProcessingActivities, MasterProcessingActivityDto processingActivityDto) {

        List<String> checkDuplicateInSubProcess = new ArrayList<>();
        List<MasterProcessingActivity> subProcessingActivityList = new ArrayList<>();
        for (MasterProcessingActivityDto activity : subProcessingActivities) {
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
            subProcessingActivityList.add(subProcessingActivity);
        }

        checkForDuplicacyByName(countryId, checkDuplicateInSubProcess);
        subProcessingActivityList = save(subProcessingActivityList);
        List<BigInteger> subProcessingActicitiesIds = new ArrayList<>();
        subProcessingActivityList.forEach(o -> subProcessingActicitiesIds.add(o.getId()));
        return subProcessingActicitiesIds;

    }


    public void checkForDuplicacyByName(Long countryId, List<String> subProcessingAcitivityNames) {

        List<MasterProcessingActivity> processingActivities = masterProcessingActivityRepository.masterProcessingActivityListByNames(countryId, subProcessingAcitivityNames);
        if (processingActivities.size() != 0) {
            throw new DuplicateDataException(" sub processing acitvity " + processingActivities.get(0).getName() + " exist");
        }
    }


    public List<MasterProcessingActivity> getAllmasterProcessingActivity() {
        return masterProcessingActivityRepository.getAllMasterProcessingsctivity(UserContext.getCountryId());

    }


    public MasterProcessingActivity updateMasterProcessingActivity(Long countryId, BigInteger id, MasterProcessingActivityDto masterProcessingActivityDto) {

        MasterProcessingActivity exists = masterProcessingActivityRepository.findByid(id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);
        } else {

            if (masterProcessingActivityDto.getOrganizationTypes() != null && masterProcessingActivityDto.getOrganizationTypes().size() != 0) {
                exists.setOrganizationTypes(masterProcessingActivityDto.getOrganizationTypes());

            }

            if (masterProcessingActivityDto.getOrganizationSubTypes() != null && masterProcessingActivityDto.getOrganizationSubTypes().size() != 0) {
                exists.setOrganizationSubTypes(masterProcessingActivityDto.getOrganizationSubTypes());

            }
            if (masterProcessingActivityDto.getOrganizationServices() != null && masterProcessingActivityDto.getOrganizationServices().size() != 0) {
                exists.setOrganizationServices(masterProcessingActivityDto.getOrganizationServices());

            }
            if (masterProcessingActivityDto.getOrganizationSubServices() != null && masterProcessingActivityDto.getOrganizationSubServices().size() != 0) {
                exists.setOrganizationSubServices(masterProcessingActivityDto.getOrganizationTypes());

            }
            if (masterProcessingActivityDto.getSubProcessingActivities() != null && masterProcessingActivityDto.getSubProcessingActivities().size() > 1) {
                exists.setSubProcessingActivityIds(addSubProcessingActivity(countryId, masterProcessingActivityDto.getSubProcessingActivities(), masterProcessingActivityDto));
            }


            exists.setDescription(masterProcessingActivityDto.getDescription());
            exists.setName(masterProcessingActivityDto.getName());


        }
        return save(exists);
    }

    public MasterProcessingActivityResponseDto getMasterProcessingActivityWithSubProcessing(Long countryId, BigInteger id) {
        MasterProcessingActivityResponseDto result = masterProcessingActivityRepository.getMasterProcessingActivityWithSubProcessingActivity(countryId, id);
        if (!Optional.of(result).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        } else
            return result;

    }


    public List<MasterProcessingActivityResponseDto> getMasterProcessingActivityListWithSubProcessing(Long countryId) {
        return masterProcessingActivityRepository.getMasterProcessingActivityListWithSubProcessingActivity(countryId);

    }


    public Boolean deleteMasterProcessingActivity(BigInteger id) {
        MasterProcessingActivity exists = masterProcessingActivityRepository.findByid(id);
        if (exists == null) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        } else
            exists.setDeleted(true);
        save(exists);
        return true;

    }





/*    public MasterProcessingActivity getmasterProcessingActivityById(BigInteger id) {
        MasterProcessingActivity exists = masterProcessingActivityRepository.findByIdAndNonDeleted(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        } else
            return exists;

    }*/


}
