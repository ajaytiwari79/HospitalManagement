package com.kairos.service.processing_activity;

import com.kairos.client.OrganizationTypeRestClient;
import com.kairos.client.dto.OrganizationTypeAndServiceRestClientRequestDto;
import com.kairos.client.dto.OrganizationTypeAndServiceResultDto;
import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.persistance.model.processing_activity.MasterProcessingActivity;
import com.kairos.dto.MasterProcessingActivityDto;
import com.kairos.persistance.repository.processing_activity.MasterProcessingActivityRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MasterProcessingActivityService extends MongoBaseService {


    @Inject
    OrganizationTypeRestClient organizationTypeAndServiceRestClient;

    @Inject
    MasterProcessingActivityRepository masterProcessingActivityRepository;


    @Inject
    ComparisonUtils comparisonUtils;

    public MasterProcessingActivity addMasterProcessingActivity(MasterProcessingActivityDto masterProcessingActivityDto) {

        Set<Long> orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds;
        orgTypeIds = masterProcessingActivityDto.getOrganizationTypes();
        orgSubTypeIds = masterProcessingActivityDto.getOrganizationSubTypes();
        orgServiceIds = masterProcessingActivityDto.getOrganizationServices();
        orgSubServiceIds = masterProcessingActivityDto.getOrganizationSubServices();
        MasterProcessingActivity masterProcessingActivity = new MasterProcessingActivity();

        if (masterProcessingActivityRepository.findByName(masterProcessingActivityDto.getName()) != null) {
            throw new DuplicateDataException("asset for name " + masterProcessingActivityDto.getName() + " already exists");
        }
        OrganizationTypeAndServiceRestClientRequestDto requestDto = new OrganizationTypeAndServiceRestClientRequestDto();
        requestDto.setOrganizationTypeIds(orgTypeIds);
        requestDto.setOrganizationSubTypeIds(orgSubTypeIds);
        requestDto.setOrganizationServiceIds(orgServiceIds);
        requestDto.setOrganizationSubServiceIds(orgSubServiceIds);
        OrganizationTypeAndServiceResultDto requestResult = organizationTypeAndServiceRestClient.getOrganizationTypeAndServices(requestDto);

        if (orgSubTypeIds != null && orgServiceIds.size() != 0) {

            List<OrganizationTypeAndServiceBasicDto> orgSubTypes = requestResult.getOrganizationSubTypes();
            comparisonUtils.checkOrgTypeAndService(orgSubTypeIds, orgSubTypes);
            masterProcessingActivity.setOrganizationSubTypes(orgSubTypes);

        }
        if (orgServiceIds != null && orgServiceIds.size() != 0) {
            List<OrganizationTypeAndServiceBasicDto> orgServices = requestResult.getOrganizationServices();
            comparisonUtils.checkOrgTypeAndService(orgServiceIds, orgServices);
            masterProcessingActivity.setOrganizationServices(orgServices);


        }
        if (orgSubServiceIds != null && orgSubServiceIds.size() != 0) {
            List<OrganizationTypeAndServiceBasicDto> orgSubServices = requestResult.getOrganizationSubServices();
            comparisonUtils.checkOrgTypeAndService(orgSubServiceIds, orgSubServices);
            masterProcessingActivity.setOrganizationSubServices(orgSubServices);

        }
        comparisonUtils.checkOrgTypeAndService(orgTypeIds, requestResult.getOrganizationTypes());
        masterProcessingActivity.setOrganizationTypes(requestResult.getOrganizationTypes());
        masterProcessingActivity.setName(masterProcessingActivityDto.getName());
        masterProcessingActivity.setDescription(masterProcessingActivityDto.getDescription());
        return save(masterProcessingActivity);


    }


    public List<MasterProcessingActivity> getAllmasterProcessingActivity() {
        List<MasterProcessingActivity> processingActivities = masterProcessingActivityRepository.findAll();
        if (processingActivities.size() != 0) {
            return processingActivities;
        } else
            throw new DataNotExists("No masterProcessingActivity found create assets");

    }


    public MasterProcessingActivity updateMasterProcessingActivity(BigInteger id, MasterProcessingActivityDto masterProcessingActivityDto) {

        Set<Long> orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds;
        orgTypeIds = masterProcessingActivityDto.getOrganizationTypes();
        orgSubTypeIds = masterProcessingActivityDto.getOrganizationSubTypes();
        orgServiceIds = masterProcessingActivityDto.getOrganizationServices();
        orgSubServiceIds = masterProcessingActivityDto.getOrganizationSubServices();

        MasterProcessingActivity exists = masterProcessingActivityRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        }
        OrganizationTypeAndServiceRestClientRequestDto requestDto = new OrganizationTypeAndServiceRestClientRequestDto();
        requestDto.setOrganizationTypeIds(orgTypeIds);
        requestDto.setOrganizationSubTypeIds(orgSubTypeIds);
        requestDto.setOrganizationServiceIds(orgServiceIds);
        requestDto.setOrganizationSubServiceIds(orgSubServiceIds);
        OrganizationTypeAndServiceResultDto requestResult = organizationTypeAndServiceRestClient.getOrganizationTypeAndServices(requestDto);

        if (orgSubTypeIds != null && orgServiceIds.size() != 0) {

            List<OrganizationTypeAndServiceBasicDto> orgSubTypes = requestResult.getOrganizationSubTypes();
            comparisonUtils.checkOrgTypeAndService(orgSubTypeIds, orgSubTypes);
            exists.setOrganizationSubTypes(orgSubTypes);

        }
        if (orgServiceIds != null && orgServiceIds.size() != 0) {
            List<OrganizationTypeAndServiceBasicDto> orgServices = requestResult.getOrganizationServices();
            comparisonUtils.checkOrgTypeAndService(orgServiceIds, orgServices);
            exists.setOrganizationServices(orgServices);


        }
        if (orgSubServiceIds != null && orgSubServiceIds.size() != 0) {
            List<OrganizationTypeAndServiceBasicDto> orgSubServices = requestResult.getOrganizationSubServices();
            comparisonUtils.checkOrgTypeAndService(orgSubServiceIds, orgSubServices);
            exists.setOrganizationSubServices(orgSubServices);

        }
        comparisonUtils.checkOrgTypeAndService(orgTypeIds, requestResult.getOrganizationTypes());
        exists.setOrganizationTypes(requestResult.getOrganizationTypes());


        exists.setName(masterProcessingActivityDto.getName());
        exists.setDescription(masterProcessingActivityDto.getDescription());
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
        if (exists == null) {
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
