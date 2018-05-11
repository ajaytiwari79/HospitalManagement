package com.kairos.service.processing_activity;

import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.RequestDataNull;
import com.kairos.persistance.model.processing_activity.MasterProcessingActivity;
import com.kairos.persistance.repository.processing_activity.MasterProcessingActivityRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class MasterProcessingActivityService  extends MongoBaseService {



@Inject
private MasterProcessingActivityRepository masterProcessingActivityRepository;

    public MasterProcessingActivity createMasterProcessingActivity(MasterProcessingActivity   masterProcessingActivity) {

        if (StringUtils.isEmpty(masterProcessingActivity.getDescription())||StringUtils.isEmpty(masterProcessingActivity.getName())) {
            throw new RequestDataNull("Global asset description and name cannotbe null");
        } else {

            if(masterProcessingActivityRepository.findByName(masterProcessingActivity.getName())!=null)
            {
                System.err.println("+++++++++++++++++++++++++++++++++++++");
                throw new DuplicateDataException("asset for name "+masterProcessingActivity.getName()+" already exists");
            }
            MasterProcessingActivity newAsset=new MasterProcessingActivity();
            List<Long> organizationType, organizationSubType, organizationService, organizationSubService;
            organizationType = masterProcessingActivity.getOrganisationType();
            organizationSubType = masterProcessingActivity.getOrganisationSubType();
            organizationService = masterProcessingActivity.getOrganisationService();
            organizationSubService = masterProcessingActivity.getOrganisationSubService();

            if (organizationType != null && !organizationType.isEmpty()) {
                newAsset.setOrganisationType(organizationType);
            }
            if (organizationSubType != null && !organizationSubType.isEmpty()) {
                newAsset.setOrganisationSubType(organizationSubType);
            }

            if (organizationService != null && !organizationService.isEmpty()) {
                newAsset.setOrganisationService(organizationService);
            }

            if (organizationSubService != null && !organizationSubService.isEmpty()) {
                newAsset.setOrganisationSubService(organizationSubService);
            }
            newAsset.setName(masterProcessingActivity.getName());
            newAsset.setDescription(masterProcessingActivity.getDescription());
            return save(newAsset);

        }


    }


    public List<MasterProcessingActivity> getAllmasterProcessingActivity() {
        List<MasterProcessingActivity> processingActivities = masterProcessingActivityRepository.findAll();
        if (processingActivities.size() != 0) {
            return processingActivities;
        } else
            throw new DataNotExists("No masterProcessingActivity found create assets");

    }


    public MasterProcessingActivity updateMasterProcessingActivity(BigInteger id, MasterProcessingActivity masterProcessingActivity) {
        MasterProcessingActivity exists = masterProcessingActivityRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        } else {
            List<Long> organizationType, organizationSubType, organizationService, organizationSubService;
            organizationType = masterProcessingActivity.getOrganisationType();
            organizationSubType = masterProcessingActivity.getOrganisationSubType();
            organizationService = masterProcessingActivity.getOrganisationService();
            organizationSubService = masterProcessingActivity.getOrganisationSubService();
            if (StringUtils.isEmpty(masterProcessingActivity.getDescription()))
            {
                throw new RequestDataNull("description cannot be null");
            }
            if (organizationType != null && !organizationType.isEmpty()) {
                exists.setOrganisationType(organizationType);
            }
            if (organizationSubType != null && !organizationSubType.isEmpty()) {
                exists.setOrganisationSubType(organizationSubType);
            }
            if (organizationService != null && !organizationService.isEmpty()) {
                exists.setOrganisationService(organizationService);
            }
            if (organizationSubService != null && !organizationSubService.isEmpty()) {
                exists.setOrganisationSubService(organizationSubService);
            }
        }
        exists.setName(masterProcessingActivity.getName());
        exists.setDescription(masterProcessingActivity.getDescription());
        return save(exists);
    }

    public MasterProcessingActivity getMasterProcessingActivityById(BigInteger id) {
        MasterProcessingActivity exists = masterProcessingActivityRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        } else
            return exists;

    }


    public Boolean deleteMasterProcessingActivity(BigInteger id) {
        MasterProcessingActivity exists = masterProcessingActivityRepository.findByid(id);
        if (exists==null) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        } else
            masterProcessingActivityRepository.delete(exists);
        return true;

    }

    public MasterProcessingActivity getmasterProcessingActivityById(BigInteger id) {
        MasterProcessingActivity exists = masterProcessingActivityRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        } else

            return exists;

    }





}
