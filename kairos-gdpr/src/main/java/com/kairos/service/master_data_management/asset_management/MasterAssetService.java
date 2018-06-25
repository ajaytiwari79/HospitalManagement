package com.kairos.service.master_data_management.asset_management;


import com.kairos.custom_exception.*;
import com.kairos.dto.master_data.MasterAssetDTO;
import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.persistance.repository.master_data_management.asset_management.MasterAssetMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
import com.kairos.utils.userContext.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;


@Service
public class MasterAssetService extends MongoBaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetService.class);

    @Inject
    MasterAssetMongoRepository masterAssetMongoRepository;


    @Inject
    ComparisonUtils comparisonUtils;

    @Inject
    private ExceptionService exceptionService;


    public MasterAsset addMasterAsset(Long countryId, MasterAssetDTO masterAssetDto) {

        MasterAsset newAsset = new MasterAsset();
        if (masterAssetMongoRepository.findByNameAndCountry(countryId, masterAssetDto.getName()) != null) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        } else {
            if (masterAssetDto.getOrganizationTypes() != null && masterAssetDto.getOrganizationTypes().size() != 0) {
                newAsset.setOrganizationTypes(masterAssetDto.getOrganizationTypes());

            }
            if (masterAssetDto.getOrganizationSubTypes() != null && masterAssetDto.getOrganizationSubTypes().size() != 0) {
                newAsset.setOrganizationSubTypes(masterAssetDto.getOrganizationSubTypes());

            }
            if (masterAssetDto.getOrganizationServices() != null && masterAssetDto.getOrganizationServices().size() != 0) {
                newAsset.setOrganizationServices(masterAssetDto.getOrganizationServices());

            }
            if (masterAssetDto.getOrganizationSubServices() != null && masterAssetDto.getOrganizationSubServices().size() != 0) {
                newAsset.setOrganizationSubServices(masterAssetDto.getOrganizationSubServices());

            }

        }
        newAsset.setName(masterAssetDto.getName());
        newAsset.setCountryId(countryId);
        newAsset.setDescription(masterAssetDto.getDescription());
        return save(newAsset);


    }


    public List<MasterAsset> getAllMasterAsset() {
        return masterAssetMongoRepository.findAllMasterAssets(UserContext.getCountryId());
    }


    public MasterAsset updateMasterAsset(BigInteger id, MasterAssetDTO masterAssetDto) {


        MasterAsset exists = masterAssetMongoRepository.findByNameAndCountry(UserContext.getCountryId(), masterAssetDto.getName());
        if (Optional.ofNullable(exists).isPresent() && !id.equals(exists.getId())) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        exists = masterAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "master.asset", id);
        } else {
            if (masterAssetDto.getOrganizationTypes() != null && masterAssetDto.getOrganizationTypes().size() != 0) {
                exists.setOrganizationTypes(masterAssetDto.getOrganizationTypes());

            }
            if (masterAssetDto.getOrganizationSubTypes() != null && masterAssetDto.getOrganizationSubTypes().size() != 0) {
                exists.setOrganizationSubTypes(masterAssetDto.getOrganizationSubTypes());

            }
            if (masterAssetDto.getOrganizationServices() != null && masterAssetDto.getOrganizationServices().size() != 0) {
                exists.setOrganizationServices(masterAssetDto.getOrganizationServices());

            }
            if (masterAssetDto.getOrganizationSubServices() != null && masterAssetDto.getOrganizationSubServices().size() != 0) {
                exists.setOrganizationSubServices(masterAssetDto.getOrganizationSubServices());

            }
            exists.setName(masterAssetDto.getName());
            exists.setDescription(masterAssetDto.getDescription());

        }
        return save(exists);
    }


    public Boolean deleteMasterAsset(BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByid(id);
        if (exists == null) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
            masterAssetMongoRepository.delete(exists);
        return true;

    }

    public MasterAsset getMasterAssetById(Long countryId, BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByIdANdNonDeleted(countryId, id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw new DataNotFoundByIdException("master asset not Exist for id " + id);

        }
        return exists;
    }





/*

    public Map<String, List<OrganizationTypeAndServiceBasicDTO>> organizationTypeAndSubTypeFilter(Set<Long> orgTypeIds, List<OrganizationTypeAndServiceBasicDTO> organizationTypeList) {
        List<OrganizationTypeAndServiceBasicDTO> organizationTypes = new ArrayList<>();

        Map<String, List<OrganizationTypeAndServiceBasicDTO>> result = new HashMap<>();
        Iterator iterator = orgTypeIds.iterator();

        while (iterator.hasNext()) {
            Long orgTypeId = (Long) iterator.next();
            for (OrganizationTypeAndServiceBasicDTO orgType : organizationTypeList) {
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


    public Map<String, List<OrganizationTypeAndServiceBasicDTO>> organizationServiceAndSubServiceFilter(Set<Long> orgServiceIds, List<OrganizationTypeAndServiceBasicDTO> organizationServiceList) {
        List<OrganizationTypeAndServiceBasicDTO> organizationServices = new ArrayList<>();

        Map<String, List<OrganizationTypeAndServiceBasicDTO>> result = new HashMap<>();
        Iterator iterator = orgServiceIds.iterator();

        while (iterator.hasNext()) {
            Long orgTypeId = (Long) iterator.next();
            for (OrganizationTypeAndServiceBasicDTO orgService : organizationServiceList) {
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
*/


}
