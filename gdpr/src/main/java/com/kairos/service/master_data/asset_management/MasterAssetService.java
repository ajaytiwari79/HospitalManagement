package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.*;
import com.kairos.dto.master_data.MasterAssetDTO;
import com.kairos.persistance.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistance.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
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

       MasterAsset  previousAsset= masterAssetMongoRepository.findByName(countryId, organizationId, masterAssetDto.getName());
        if (Optional.ofNullable(previousAsset).isPresent()) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        assetTypeService.getAssetTypeById(countryId,masterAssetDto.getAssetTypeId());
        MasterAsset  masterAsset = new MasterAsset(masterAssetDto.getName(),masterAssetDto.getDescription(),masterAssetDto.getOrganizationTypes(),masterAssetDto.getOrganizationSubTypes()
        ,masterAssetDto.getOrganizationServices(),masterAssetDto.getOrganizationSubServices());
        masterAsset.setCountryId(countryId);
        masterAsset.setOrganizationId(organizationId);
        masterAsset.setAssetType(masterAssetDto.getAssetTypeId());
        masterAsset.setAssetSubTypes(masterAssetDto.getAssetSubTypes());
        return masterAssetMongoRepository.save(masterAsset);
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


        MasterAsset masterAsset = masterAssetMongoRepository.findByName(countryId, organizationId, masterAssetDto.getName());
        if (Optional.ofNullable(masterAsset).isPresent() && !id.equals(masterAsset.getId())) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        masterAsset = masterAssetMongoRepository.findByIdANdNonDeleted(countryId,organizationId,id);
        if (!Optional.of(masterAsset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "master.asset", id);
        }
        assetTypeService.getAssetTypeById(countryId,masterAssetDto.getAssetTypeId());
        masterAsset.setOrganizationTypes(masterAssetDto.getOrganizationTypes());
        masterAsset.setOrganizationSubTypes(masterAssetDto.getOrganizationSubTypes());
        masterAsset.setOrganizationServices(masterAssetDto.getOrganizationServices());
        masterAsset.setOrganizationSubServices(masterAssetDto.getOrganizationSubServices());
        masterAsset.setName(masterAssetDto.getName());
        masterAsset.setAssetType(masterAssetDto.getAssetTypeId());
        masterAsset.setAssetSubTypes(masterAssetDto.getAssetSubTypes());
        masterAsset.setDescription(masterAssetDto.getDescription());
        masterAssetMongoRepository.save(masterAsset);
        return masterAsset;
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
