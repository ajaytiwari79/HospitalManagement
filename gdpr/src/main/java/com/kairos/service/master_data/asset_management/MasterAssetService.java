package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.*;
import com.kairos.dto.master_data.MasterAssetDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistance.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistance.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
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
    private ComparisonUtils comparisonUtils;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeService assetTypeService;

    /**
     * @throws DuplicateDataException throw exception id MasterAsset not exist for given id
     * @param countryId
     * @param organizationId
     * @param masterAssetDto
     * @return return MasterAsset object
     */

    public MasterAsset addMasterAsset(Long countryId, Long organizationId, MasterAssetDTO masterAssetDto) {


        if (masterAssetMongoRepository.findByName(countryId, organizationId, masterAssetDto.getName()) != null) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        assetTypeService.getAssetTypeById(countryId,organizationId,masterAssetDto.getAssetTypeId());
        MasterAsset newAsset = new MasterAsset(masterAssetDto.getName(),masterAssetDto.getDescription(),masterAssetDto.getOrganizationTypes(),masterAssetDto.getOrganizationSubTypes()
        ,masterAssetDto.getOrganizationServices(),masterAssetDto.getOrganizationSubServices());
        newAsset.setCountryId(countryId);
        newAsset.setOrganizationId(organizationId);
        newAsset.setAssetType(masterAssetDto.getAssetTypeId());
        return masterAssetMongoRepository.save(sequenceGenerator(newAsset));
    }


    /**
     *
     * @param countryId
     * @param organizationId
     * @return list of MasterAsset
     */

    public List<MasterAssetResponseDTO> getAllMasterAsset(Long countryId, Long organizationId) {
        return masterAssetMongoRepository.getAllMasterAssetWithAssetTypeAndSubAssetType(countryId, organizationId);
    }


    /**
     * @throws  DuplicateDataException if MasterAsset not exist already exist with same  name
     * {@link DataNotFoundByIdException throw exception if MasterAsset not found for given id}
     * @param countryId
     * @param organizationId
     * @param id id of MasterAsset
     * @param masterAssetDto
     * @return updated object of MasterAsset
     */
    public MasterAsset updateMasterAsset(Long countryId, Long organizationId, BigInteger id, MasterAssetDTO masterAssetDto) {


        MasterAsset exists = masterAssetMongoRepository.findByName(countryId, organizationId, masterAssetDto.getName());
        if (Optional.ofNullable(exists).isPresent() && !id.equals(exists.getId())) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        exists = masterAssetMongoRepository.findByIdANdNonDeleted(countryId,organizationId,id);
        if (!Optional.of(exists).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "master.asset", id);
        }
        assetTypeService.getAssetTypeById(countryId,organizationId,masterAssetDto.getAssetTypeId());
        exists.setOrganizationTypes(masterAssetDto.getOrganizationTypes());
        exists.setOrganizationSubTypes(masterAssetDto.getOrganizationSubTypes());
        exists.setOrganizationServices(masterAssetDto.getOrganizationServices());
        exists.setOrganizationSubServices(masterAssetDto.getOrganizationSubServices());
        exists.setName(masterAssetDto.getName());
        exists.setAssetType(masterAssetDto.getAssetTypeId());
        exists.setDescription(masterAssetDto.getDescription());
        masterAssetMongoRepository.save(sequenceGenerator(exists));
        return exists;
    }


    public Boolean deleteMasterAsset(Long countryId, Long organizationId, BigInteger id) {
        MasterAsset exists = masterAssetMongoRepository.findByIdANdNonDeleted(countryId, organizationId, id);
        if (exists == null) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
            delete(exists);
        return true;

    }


    public MasterAssetResponseDTO getMasterAssetById(Long countryId, Long organizationId, BigInteger id) {
        MasterAssetResponseDTO exists = masterAssetMongoRepository.getMasterAssetWithAssetTypeAndSubAssetTypeById(countryId, organizationId, id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw new DataNotFoundByIdException("master asset not Exist for id " + id);

        }
        return exists;
    }


}
