package com.kairos.service.master_data.asset_management;


import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.OrgTypeSubTypeServicesAndSubServicesDTO;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.data_inventory.AssetDTO;
import com.kairos.dto.gdpr.master_data.AssetTypeDTO;
import com.kairos.dto.gdpr.master_data.MasterAssetDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import com.kairos.response.dto.master_data.AssetTypeResponseDTO;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
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

    @Inject
    private GenericRestClient restClient;

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;


    /**
     * @param countryId
     * @param masterAssetDto
     * @return return MasterAsset object
     * @throws DuplicateDataException throw exception id MasterAsset not exist for given id
     */

    public MasterAssetDTO addMasterAsset(Long countryId, MasterAssetDTO masterAssetDto) {

        MasterAsset previousAsset = masterAssetMongoRepository.findByName(countryId, masterAssetDto.getName());
        if (Optional.ofNullable(previousAsset).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.asset", masterAssetDto.getName());
        }
        MasterAsset masterAsset = new MasterAsset(masterAssetDto.getName(), masterAssetDto.getDescription(), countryId, masterAssetDto.getOrganizationTypes(), masterAssetDto.getOrganizationSubTypes()
                , masterAssetDto.getOrganizationServices(), masterAssetDto.getOrganizationSubServices(), SuggestedDataStatus.APPROVED);
        saveAndUpdateAssetType(countryId, masterAsset, masterAssetDto);
        masterAssetMongoRepository.save(masterAsset);
        masterAssetDto.setId(masterAsset.getId());
        return masterAssetDto;
    }


    private void saveAndUpdateAssetType(Long countryId, MasterAsset masterAsset, MasterAssetDTO masterAssetDTO) {


        if (Optional.ofNullable(masterAssetDTO.getAssetType().getId()).isPresent()) {

            AssetType assetType = assetTypeMongoRepository.findOne(masterAssetDTO.getAssetType().getId());
            masterAsset.setAssetTypeId(assetType.getId());
            Optional.ofNullable(masterAssetDTO.getAssetSubType()).ifPresent(assetSubTypeBasicDTO -> {
                if (assetSubTypeBasicDTO.getId() != null) {
                    masterAsset.setAssetSubTypeId(assetSubTypeBasicDTO.getId());
                } else {
                    AssetType assetSubType = new AssetType(assetSubTypeBasicDTO.getName(), countryId, SuggestedDataStatus.APPROVED);
                    assetSubType.setSubAssetType(true);
                    assetTypeMongoRepository.save(assetSubType);
                    assetType.getSubAssetTypes().add(assetSubType.getId());
                    masterAsset.setAssetSubTypeId(assetSubType.getId());
                }
            });
        } else {
            AssetType previousAssetType = assetTypeMongoRepository.findByNameAndCountryId(countryId, masterAssetDTO.getAssetType().getName());
            Optional.ofNullable(previousAssetType).ifPresent(assetType -> {
                exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetType.getName());
            });
            AssetType assetType = new AssetType(masterAssetDTO.getAssetType().getName(), countryId, SuggestedDataStatus.APPROVED);
            Optional.ofNullable(masterAssetDTO.getAssetSubType()).ifPresent(assetSubTypeDto -> {

                AssetType assetSubType = new AssetType(assetSubTypeDto.getName(), countryId, SuggestedDataStatus.APPROVED);
                assetSubType.setSubAssetType(true);
                assetTypeMongoRepository.save(assetSubType);
                assetType.getSubAssetTypes().add(assetSubType.getId());
                masterAsset.setAssetSubTypeId(assetSubType.getId());
            });
            assetTypeMongoRepository.save(assetType);
            masterAsset.setAssetTypeId(assetType.getId());
        }
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
        masterAsset = masterAssetMongoRepository.findOne(id);
        masterAsset.setOrganizationTypes(masterAssetDto.getOrganizationTypes())
                .setOrganizationSubTypes(masterAssetDto.getOrganizationSubTypes())
                .setOrganizationServices(masterAssetDto.getOrganizationServices())
                .setOrganizationSubServices(masterAssetDto.getOrganizationSubServices())
                .setName(masterAssetDto.getName())
                .setDescription(masterAssetDto.getDescription());
        saveAndUpdateAssetType(countryId, masterAsset, masterAssetDto);
        masterAssetMongoRepository.save(masterAsset);
        return masterAssetDto;
    }


    public Boolean deleteMasterAsset(Long countryId, BigInteger id) {
        MasterAsset masterAsset = masterAssetMongoRepository.findByIdAndCountryId(countryId, id);
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


    /**
     * @param countryId -country id
     * @param unitId    -unit id which suggest asset to country admin
     * @param assetDTO  -contain basic detail about asset ,name and description
     * @return
     */
    public AssetDTO saveSuggestedAssetFromUnit(Long countryId, Long unitId, AssetDTO assetDTO) {
        MasterAsset previousAsset = masterAssetMongoRepository.findByName(countryId, assetDTO.getName());
        if (Optional.ofNullable(previousAsset).isPresent()) {
            return null;
        }
        OrgTypeSubTypeServicesAndSubServicesDTO orgTypeSubTypeServicesAndSubServicesDTO = restClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/organization_type", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrgTypeSubTypeServicesAndSubServicesDTO>>() {
        });
        MasterAsset masterAsset = new MasterAsset(assetDTO.getName(), assetDTO.getDescription(), countryId, LocalDate.now(), SuggestedDataStatus.PENDING)
                .setOrganizationTypes(Arrays.asList(new OrganizationType(orgTypeSubTypeServicesAndSubServicesDTO.getId(), orgTypeSubTypeServicesAndSubServicesDTO.getName())))
                .setOrganizationSubTypes(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubTypes())
                .setOrganizationServices(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationServices())
                .setOrganizationSubServices(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubServices());
        masterAssetMongoRepository.save(masterAsset);
        assetDTO.setId(masterAsset.getId());
        return assetDTO;
    }

    /**
     * @param countryId
     * @param suggestedDataStatus
     * @return
     * @description update status of asset (suggest by unit)
     */
    public boolean updateSuggestedStatusOfMasterAsset(Long countryId, Set<BigInteger> assetIds, SuggestedDataStatus suggestedDataStatus) {

        List<MasterAsset> masterAssetList = masterAssetMongoRepository.findMasterAssetByCountryIdAndIds(countryId, assetIds);
        masterAssetList.forEach(masterAsset -> masterAsset.setSuggestedDataStatus(suggestedDataStatus));
        masterAssetMongoRepository.saveAll(getNextSequence(masterAssetList));
        return true;
    }


}
