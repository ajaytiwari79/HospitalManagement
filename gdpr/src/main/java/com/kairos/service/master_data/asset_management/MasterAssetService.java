package com.kairos.service.master_data.asset_management;


import com.kairos.custom_exception.*;
import com.kairos.dto.gdpr.master_data.MasterAssetDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistence.repository.master_data.asset_management.MasterAssetMongoRepository;
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
     * @param countryId
     * @param masterAssetDto
     * @return return MasterAsset object
     * @throws DuplicateDataException throw exception id MasterAsset not exist for given id
     */

    public MasterAssetDTO addMasterAsset(Long countryId,  MasterAssetDTO masterAssetDto) {

        MasterAsset previousAsset = masterAssetMongoRepository.findByName(countryId, masterAssetDto.getName());
        if (Optional.ofNullable(previousAsset).isPresent()) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        assetTypeService.getAssetTypeById(countryId, masterAssetDto.getAssetTypeId());
        MasterAsset masterAsset = new MasterAsset(masterAssetDto.getName(), masterAssetDto.getDescription(), masterAssetDto.getOrganizationTypes(), masterAssetDto.getOrganizationSubTypes()
                , masterAssetDto.getOrganizationServices(), masterAssetDto.getOrganizationSubServices());
        masterAsset.setCountryId(countryId);
        masterAsset.setAssetType(masterAssetDto.getAssetTypeId());
        masterAsset.setAssetSubTypes(masterAssetDto.getAssetSubTypes());
        masterAssetMongoRepository.save(masterAsset);
        masterAssetDto.setId(masterAsset.getId());
        return masterAssetDto;
    }


    /**
     * @param countryId
     * @return list of MasterAsset
     */

    public List<MasterAssetResponseDTO> getAllMasterAsset(Long countryId) {
        return masterAssetMongoRepository.getAllMasterAssetWithAssetTypeAndSubAssetType(countryId);
    }


    /**
     * @param countryId
     * @param id             id of MasterAsset
     * @param masterAssetDto
     * @return updated object of MasterAsset
     * @throws DuplicateDataException if MasterAsset not exist already exist with same  name
     *                                {@link DataNotFoundByIdException throw exception if MasterAsset not found for given id}
     */
    public MasterAssetDTO updateMasterAsset(Long countryId, BigInteger id, MasterAssetDTO masterAssetDto) {


        MasterAsset masterAsset = masterAssetMongoRepository.findByName(countryId, masterAssetDto.getName());
        if (Optional.ofNullable(masterAsset).isPresent() && !id.equals(masterAsset.getId())) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        masterAsset = masterAssetMongoRepository.findByIdANdNonDeleted(countryId, id);
        if (!Optional.of(masterAsset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "master.asset", id);
        }
        assetTypeService.getAssetTypeById(countryId, masterAssetDto.getAssetTypeId());
        masterAsset.setOrganizationTypes(masterAssetDto.getOrganizationTypes());
        masterAsset.setOrganizationSubTypes(masterAssetDto.getOrganizationSubTypes());
        masterAsset.setOrganizationServices(masterAssetDto.getOrganizationServices());
        masterAsset.setOrganizationSubServices(masterAssetDto.getOrganizationSubServices());
        masterAsset.setName(masterAssetDto.getName());
        masterAsset.setAssetType(masterAssetDto.getAssetTypeId());
        masterAsset.setAssetSubTypes(masterAssetDto.getAssetSubTypes());
        masterAsset.setDescription(masterAssetDto.getDescription());
        masterAssetMongoRepository.save(masterAsset);
        return masterAssetDto;
    }


    public Boolean deleteMasterAsset(Long countryId, BigInteger id) {
        MasterAsset masterAsset = masterAssetMongoRepository.findByIdANdNonDeleted(countryId, id);
        if (!Optional.ofNullable(masterAsset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Master Asset", id);
        }
        delete(masterAsset);
        return true;

    }


    public MasterAssetResponseDTO getMasterAssetById(Long countryId, BigInteger id) {
        MasterAssetResponseDTO exists = masterAssetMongoRepository.getMasterAssetWithAssetTypeAndSubAssetTypeById(countryId, id);
        if (!Optional.ofNullable(exists).isPresent()) {
            throw new DataNotFoundByIdException("master asset not Exist for id " + id);

        }
        return exists;
    }


}
