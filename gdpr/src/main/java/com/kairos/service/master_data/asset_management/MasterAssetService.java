package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.*;
import com.kairos.dto.master_data.MasterAssetDTO;
import com.kairos.persistance.model.master_data.asset_management.MasterAsset;
import com.kairos.persistance.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.ComparisonUtils;
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


    public MasterAsset addMasterAsset(Long countryId, Long organizationId, MasterAssetDTO masterAssetDto) {


        if (masterAssetMongoRepository.findByName(countryId, organizationId, masterAssetDto.getName()) != null) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        MasterAsset newAsset = new MasterAsset(masterAssetDto.getName(),masterAssetDto.getDescription(),masterAssetDto.getOrganizationTypes(),masterAssetDto.getOrganizationSubTypes()
        ,masterAssetDto.getOrganizationServices(),masterAssetDto.getOrganizationSubServices());
        newAsset.setCountryId(countryId);
        newAsset.setOrganizationId(organizationId);
        return masterAssetMongoRepository.save(sequenceGenerator(newAsset));
    }


    public List<MasterAsset> getAllMasterAsset(Long countryId, Long organizationId) {
        return masterAssetMongoRepository.findAllMasterAssets(countryId, organizationId);
    }


    public MasterAsset updateMasterAsset(Long countryId, Long organizationId, BigInteger id, MasterAssetDTO masterAssetDto) {


        MasterAsset exists = masterAssetMongoRepository.findByName(countryId, organizationId, masterAssetDto.getName());
        if (Optional.ofNullable(exists).isPresent() && !id.equals(exists.getId())) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        exists = masterAssetMongoRepository.findByid(id);
        if (!Optional.of(exists).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "master.asset", id);
        }
        exists.setOrganizationTypes(masterAssetDto.getOrganizationTypes());
        exists.setOrganizationSubTypes(masterAssetDto.getOrganizationSubTypes());
        exists.setOrganizationServices(masterAssetDto.getOrganizationServices());
        exists.setOrganizationSubServices(masterAssetDto.getOrganizationSubServices());
        exists.setName(masterAssetDto.getName());
        exists.setDescription(masterAssetDto.getDescription());
        return masterAssetMongoRepository.save(sequenceGenerator(exists));
    }


    public Boolean deleteMasterAsset(Long countryId, Long organizationId, BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByIdANdNonDeleted(countryId, organizationId, id);
        if (exists == null) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
            delete(exists);
        return true;

    }

    public MasterAsset getMasterAssetById(Long countryId, Long organizationId, BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByIdANdNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw new DataNotFoundByIdException("master asset not Exist for id " + id);

        }
        return exists;
    }


}
