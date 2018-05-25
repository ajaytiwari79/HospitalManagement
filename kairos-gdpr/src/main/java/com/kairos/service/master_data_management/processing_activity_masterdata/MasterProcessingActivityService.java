package com.kairos.service.master_data_management.processing_activity_masterdata;

import com.kairos.client.OrganizationTypeRestClient;
import com.kairos.client.OrganizationTypeAndServiceRestClientRequestDto;
import com.kairos.client.OrganizationTypeAndServiceResultDto;
import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import com.kairos.dto.MasterProcessingActivityDto;
import com.kairos.persistance.repository.master_data_management.processing_activity_masterdata.MasterProcessingActivityRepository;
import com.kairos.response.dto.MasterProcessingActivityResponseDto;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import com.kairos.utils.userContext.UserContext;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class MasterProcessingActivityService extends MongoBaseService {


    @Inject
    OrganizationTypeRestClient organizationTypeAndServiceRestClient;

    @Inject
    MasterProcessingActivityRepository masterProcessingActivityRepository;


    @Inject
    ComparisonUtils comparisonUtils;

    public MasterProcessingActivity createMasterProcessingActivity(Long countryId, MasterProcessingActivityDto masterProcessingActivityDto) {

        if (masterProcessingActivityRepository.findByNameAndCountryId(countryId,masterProcessingActivityDto.getName()) != null) {

            throw new DuplicateDataException("asset for name " + masterProcessingActivityDto.getName() + " already exists");
        }
        Set<Long> orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds;
        orgTypeIds = masterProcessingActivityDto.getOrganizationTypes();
        orgSubTypeIds = masterProcessingActivityDto.getOrganizationSubTypes();
        orgServiceIds = masterProcessingActivityDto.getOrganizationServices();
        orgSubServiceIds = masterProcessingActivityDto.getOrganizationSubServices();

        OrganizationTypeAndServiceRestClientRequestDto requestDto = new OrganizationTypeAndServiceRestClientRequestDto(orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds);
        OrganizationTypeAndServiceResultDto orgTypeAndServices = organizationTypeAndServiceRestClient.getOrganizationTypeAndServices(requestDto);
        MasterProcessingActivity masterProcessingActivity = new MasterProcessingActivity(countryId,masterProcessingActivityDto.getName(),masterProcessingActivityDto.getDescription());
        if (orgServiceIds.size() != 0) {
            List<OrganizationTypeAndServiceBasicDto> orgSubTypes = orgTypeAndServices.getOrganizationSubTypes();
            comparisonUtils.checkOrgTypeAndService(orgSubTypeIds, orgSubTypes);
            masterProcessingActivity.setOrganizationSubTypes(orgSubTypes);

        }
        if (orgServiceIds.size() != 0) {
            List<OrganizationTypeAndServiceBasicDto> orgServices = orgTypeAndServices.getOrganizationServices();
            comparisonUtils.checkOrgTypeAndService(orgServiceIds, orgServices);
            masterProcessingActivity.setOrganizationServices(orgServices);

        }
        if (orgSubServiceIds.size() != 0) {
            List<OrganizationTypeAndServiceBasicDto> orgSubServices = orgTypeAndServices.getOrganizationSubServices();
            comparisonUtils.checkOrgTypeAndService(orgSubServiceIds, orgSubServices);
            masterProcessingActivity.setOrganizationSubServices(orgSubServices);

        }
        if ( masterProcessingActivityDto.getSubProcessingActivity().size()>1) {
            masterProcessingActivity.setSubProcessingActivityIds(addSubProcessingActivity(countryId, masterProcessingActivityDto.getSubProcessingActivity(), orgTypeAndServices));
        }
        comparisonUtils.checkOrgTypeAndService(orgTypeIds, orgTypeAndServices.getOrganizationTypes());
        masterProcessingActivity.setOrganizationTypes(orgTypeAndServices.getOrganizationTypes());
        masterProcessingActivity.setCountryId(countryId);
        return save(masterProcessingActivity);


    }


    public List<BigInteger> addSubProcessingActivity(Long countryId, List<MasterProcessingActivityDto> subProcessingActivities, OrganizationTypeAndServiceResultDto orgTypeAndServices) {

        List<String> checkDuplicateInSubProcess = new ArrayList<>();
        List<MasterProcessingActivity> subProcessingActivityList = new ArrayList<>();
        for (MasterProcessingActivityDto activity : subProcessingActivities) {
            if (checkDuplicateInSubProcess.contains(activity.getName())) {
                throw new DuplicateDataException("sub processing name Duplicacy " + activity.getName());
            }
            checkDuplicateInSubProcess.add(activity.getName());
            MasterProcessingActivity subProcessingActivity = new MasterProcessingActivity(countryId, activity.getName(), activity.getDescription());
            subProcessingActivity.setOrganizationTypes(orgTypeAndServices.getOrganizationTypes());
            subProcessingActivity.setOrganizationSubTypes(orgTypeAndServices.getOrganizationSubTypes());
            subProcessingActivity.setOrganizationServices(orgTypeAndServices.getOrganizationServices());
            subProcessingActivity.setOrganizationSubServices(orgTypeAndServices.getOrganizationSubServices());
            subProcessingActivityList.add(subProcessingActivity);
        }

        checkForDuplicacyByName(countryId,checkDuplicateInSubProcess);
        subProcessingActivityList = save(subProcessingActivityList);
        List<BigInteger> subProcessingActicitiesIds = new ArrayList<>();
        subProcessingActivityList.forEach(o -> subProcessingActicitiesIds.add(o.getId()));
        return subProcessingActicitiesIds;

    }






    public void checkForDuplicacyByName(Long countryId,List<String> subProcessingAcitivityNames) {

        List<MasterProcessingActivity> processingActivities = masterProcessingActivityRepository.masterProcessingActivityListByNames(countryId,subProcessingAcitivityNames);
        System.err.println("size"+processingActivities.size());

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
        }
        System.err.println("_++++++"+exists.getName());
        Set<Long> orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds;
        orgTypeIds = masterProcessingActivityDto.getOrganizationTypes();
        orgSubTypeIds = masterProcessingActivityDto.getOrganizationSubTypes();
        orgServiceIds = masterProcessingActivityDto.getOrganizationServices();
        orgSubServiceIds = masterProcessingActivityDto.getOrganizationSubServices();

        OrganizationTypeAndServiceRestClientRequestDto requestDto = new OrganizationTypeAndServiceRestClientRequestDto(orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds);
        OrganizationTypeAndServiceResultDto orgTypeAndServices = organizationTypeAndServiceRestClient.getOrganizationTypeAndServices(requestDto);

        if (orgServiceIds.size() != 0) {
            List<OrganizationTypeAndServiceBasicDto> orgSubTypes = orgTypeAndServices.getOrganizationSubTypes();
            comparisonUtils.checkOrgTypeAndService(orgSubTypeIds, orgSubTypes);
            exists.setOrganizationSubTypes(orgSubTypes);
        }
        if (orgServiceIds.size() != 0) {
            List<OrganizationTypeAndServiceBasicDto> orgServices = orgTypeAndServices.getOrganizationServices();
            comparisonUtils.checkOrgTypeAndService(orgServiceIds, orgServices);
            exists.setOrganizationServices(orgServices);
        }
        if (orgSubServiceIds.size() != 0) {
            List<OrganizationTypeAndServiceBasicDto> orgSubServices = orgTypeAndServices.getOrganizationSubServices();
            comparisonUtils.checkOrgTypeAndService(orgSubServiceIds, orgSubServices);
            exists.setOrganizationSubServices(orgSubServices);

        }
        comparisonUtils.checkOrgTypeAndService(orgTypeIds, orgTypeAndServices.getOrganizationTypes());
        exists.setOrganizationTypes(orgTypeAndServices.getOrganizationTypes());
        exists.setSubProcessingActivityIds(addSubProcessingActivity(countryId, masterProcessingActivityDto.getSubProcessingActivity(), orgTypeAndServices));
        exists.setName(masterProcessingActivityDto.getName());
        exists.setDescription(masterProcessingActivityDto.getDescription());
        return save(exists);
    }


    public MasterProcessingActivityResponseDto getMasterProcessingActivityWithData(Long countryId, BigInteger id) {
        MasterProcessingActivityResponseDto result = masterProcessingActivityRepository.getMasterProcessingActivityWithData(countryId,id);
        if (!Optional.of(result).isPresent()) {
            throw new DataNotFoundByIdException("MasterProcessingActivity not Exist for id " + id);

        } else
            return result;

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
