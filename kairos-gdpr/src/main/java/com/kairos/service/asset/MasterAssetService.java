package com.kairos.service.asset;


import com.kairos.client.OrganizationTypeRestClient;
import com.kairos.client.dto.OrganizationTypeAndServiceRestClientRequestDto;
import com.kairos.client.dto.OrganizationTypeAndServiceResultDto;
import com.kairos.custome_exception.*;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.persistance.model.asset.MasterAsset;
<<<<<<< HEAD
import com.kairos.dto.MasterAssetDto;
=======
import com.kairos.persistance.model.asset.dto.MasterAssetDto;
>>>>>>> d97f2521641985b2ef7974bbd2871f4291a34c91
import com.kairos.persistance.repository.asset.MasterAssetMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.utils.ComparisonUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
<<<<<<< HEAD
=======
import java.util.stream.Collectors;
>>>>>>> d97f2521641985b2ef7974bbd2871f4291a34c91

@Service
public class MasterAssetService extends MongoBaseService {


    @Inject
    MasterAssetMongoRepository masterAssetMongoRepository;

    @Inject
    OrganizationTypeRestClient organizationTypeAndServiceRestClient;

    @Inject
    ComparisonUtils comparisonUtils;

    public MasterAsset addMasterAsset(MasterAssetDto masterAsset) {
        Set<Long> orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds;
        orgTypeIds = masterAsset.getOrganizationTypes();
        orgSubTypeIds = masterAsset.getOrganizationSubTypes();
        orgServiceIds = masterAsset.getOrganizationServices();
        orgSubServiceIds = masterAsset.getOrganizationSubServices();
        MasterAsset newAsset = new MasterAsset();
        if (masterAssetMongoRepository.findByName(masterAsset.getName()) != null) {
            throw new DuplicateDataException("master asset for name " + masterAsset.getName() + " exists");
        } else {

            OrganizationTypeAndServiceRestClientRequestDto requestDto = new OrganizationTypeAndServiceRestClientRequestDto();
            requestDto.setOrganizationTypeIds(orgTypeIds);
            requestDto.setOrganizationSubTypeIds(orgSubTypeIds);
            requestDto.setOrganizationServiceIds(orgServiceIds);
            requestDto.setOrganizationSubServiceIds(orgSubServiceIds);
            OrganizationTypeAndServiceResultDto requestResult = organizationTypeAndServiceRestClient.getOrganizationTypeAndServices(requestDto);

            if (orgSubTypeIds != null && orgServiceIds.size() != 0) {

                List<OrganizationTypeAndServiceBasicDto> orgSubTypes = requestResult.getOrganizationSubTypes();
                comparisonUtils.checkOrgTypeAndService(orgSubTypeIds, orgSubTypes);
                newAsset.setOrganizationSubTypes(orgSubTypes);

            }
            if (orgServiceIds != null && orgServiceIds.size() != 0) {
                List<OrganizationTypeAndServiceBasicDto> orgServices = requestResult.getOrganizationServices();
                comparisonUtils.checkOrgTypeAndService(orgServiceIds, orgServices);
                newAsset.setOrganizationServices(orgServices);


            }
            if (orgSubServiceIds != null && orgSubServiceIds.size() != 0) {
                List<OrganizationTypeAndServiceBasicDto> orgSubServices = requestResult.getOrganizationSubServices();
                comparisonUtils.checkOrgTypeAndService(orgSubServiceIds, orgSubServices);
                newAsset.setOrganizationSubServices(orgSubServices);

            }
            comparisonUtils.checkOrgTypeAndService(orgTypeIds, requestResult.getOrganizationTypes());
            newAsset.setOrganizationTypes(requestResult.getOrganizationTypes());

        }
        newAsset.setName(masterAsset.getName());
        newAsset.setDescription(masterAsset.getDescription());
        return save(newAsset);


    }


    public List<MasterAsset> getAllMasterAsset() {
        List<MasterAsset> assets = masterAssetMongoRepository.findAll();
        if (assets.size() != 0) {
            return assets;
        } else
            throw new DataNotExists("no Assets found create assets");

    }


    public MasterAsset updateMasterAsset(BigInteger id, MasterAssetDto masterAssetDto) {

        Set<Long> orgTypeIds, orgSubTypeIds, orgServiceIds, orgSubServiceIds;
        orgTypeIds = masterAssetDto.getOrganizationTypes();
        orgSubTypeIds = masterAssetDto.getOrganizationSubTypes();
        orgServiceIds = masterAssetDto.getOrganizationServices();
        orgSubServiceIds = masterAssetDto.getOrganizationSubServices();
        MasterAsset exists = masterAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

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
        exists.setName(masterAssetDto.getName());
        exists.setDescription(masterAssetDto.getDescription());
        return save(exists);
    }


    public MasterAsset getAssetById(BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
            return exists;

    }


    public Boolean deleteMasterAsset(BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByid(id);
        if (exists == null) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
            masterAssetMongoRepository.delete(exists);
        return true;

    }

    public MasterAsset getMasterAssetById(BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else {
        }

        return exists;
    }


    public Map<String, List<OrganizationTypeAndServiceBasicDto>> organizationTypeAndSubTypeFilter(Set<Long> orgTypeIds, List<OrganizationTypeAndServiceBasicDto> organizationTypeList) {
        List<OrganizationTypeAndServiceBasicDto> organizationTypes = new ArrayList<>();

        Map<String, List<OrganizationTypeAndServiceBasicDto>> result = new HashMap<>();
        Iterator iterator = orgTypeIds.iterator();

        while (iterator.hasNext()) {
            Long orgTypeId = (Long) iterator.next();
            for (OrganizationTypeAndServiceBasicDto orgType : organizationTypeList) {
                if (orgTypeId.equals(orgType.getId())) {
                    organizationTypes.add(orgType);
                    System.err.println(organizationTypes.get(0).getName());
                    break;
                }
            }

        }
        organizationTypeList.removeAll(organizationTypes);

        result.put("orgTypes", organizationTypes);
        result.put("orgSubTypes", organizationTypeList);
        return result;
    }


    public Map<String, List<OrganizationTypeAndServiceBasicDto>> organizationServiceAndSubServiceFilter(Set<Long> orgServiceIds, List<OrganizationTypeAndServiceBasicDto> organizationServiceList) {
        List<OrganizationTypeAndServiceBasicDto> organizationServices = new ArrayList<>();

        Map<String, List<OrganizationTypeAndServiceBasicDto>> result = new HashMap<>();
        Iterator iterator = orgServiceIds.iterator();

        while (iterator.hasNext()) {
            Long orgTypeId = (Long) iterator.next();
            for (OrganizationTypeAndServiceBasicDto orgService : organizationServiceList) {
                if (orgTypeId.equals(orgService.getId())) {
                    organizationServices.add(orgService);
                    break;
                }
            }


        }
        organizationServiceList.removeAll(organizationServices);

        result.put("orgServices", organizationServices);
        result.put("orgSubServices", organizationServiceList);
        return result;
    }


}
