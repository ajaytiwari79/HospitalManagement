package com.kairos.service.master_data.asset_management;


import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.*;
import com.kairos.dto.gdpr.data_inventory.AssetDTO;
import com.kairos.dto.gdpr.master_data.MasterAssetDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;
import com.kairos.persistence.model.embeddables.ServiceCategory;
import com.kairos.persistence.model.embeddables.SubServiceCategory;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.MasterAssetRepository;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;


@Service
public class MasterAssetService{

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private GenericRestClient restClient;

    @Inject
    private MasterAssetRepository masterAssetRepository;

    @Inject
    private AssetTypeRepository assetTypeRepository;


    /**
     * @param countryId
     * @param masterAssetDto
     * @return return MasterAsset object
     * @throws DuplicateDataException throw exception id MasterAsset not exist for given id
     */

    public MasterAssetDTO addMasterAsset(Long countryId, MasterAssetDTO masterAssetDto) {
        MasterAsset previousAsset = masterAssetRepository.findByNameAndCountryId(masterAssetDto.getName(), countryId);
        if (Optional.ofNullable(previousAsset).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.asset", masterAssetDto.getName());
        }
        MasterAsset masterAsset = new MasterAsset(masterAssetDto.getName(), masterAssetDto.getDescription(), countryId, SuggestedDataStatus.APPROVED);
        masterAsset = getMetadataOfMasterAsset(masterAssetDto, masterAsset);
        saveOrUpdateAssetType(countryId, masterAsset, masterAssetDto);
        masterAssetRepository.save(masterAsset);
        masterAssetDto.setId(masterAsset.getId());
        return masterAssetDto;
    }

    /**
     *  This method is used to fetch all the metadata related to master asset from DTO like organisationType,
     *  organisationSubType, Service Category and Sub Service Category
     *
     * @param masterAssetDto
     * @return
     */
    private MasterAsset getMetadataOfMasterAsset(MasterAssetDTO masterAssetDto, MasterAsset masterAsset){
        masterAsset.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterAssetDto.getOrganizationTypes(), OrganizationType.class));
        masterAsset.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(masterAssetDto.getOrganizationSubServices(), OrganizationSubType.class));
        masterAsset.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(masterAssetDto.getOrganizationServices(), ServiceCategory.class));
        masterAsset.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(masterAssetDto.getOrganizationSubServices(), SubServiceCategory.class));
        return masterAsset;
    }

    //TODO we can delete this method Refactored method is just define below this method with name "saveOrUpdateAssetType"
    private void saveAndUpdateAssetType(Long countryId, MasterAsset masterAsset, MasterAssetDTO masterAssetDTO) {
        if (Optional.ofNullable(masterAssetDTO.getAssetType().getId()).isPresent()) {

            AssetType assetType = assetTypeRepository.getOne(masterAssetDTO.getAssetType().getId());
            masterAsset.setAssetType(assetType);
            Optional.ofNullable(masterAssetDTO.getAssetSubType()).ifPresent(assetSubTypeBasicDTO -> {
                if (assetSubTypeBasicDTO.getId() != null) {
                    masterAsset.setSubAssetType(assetTypeRepository.getOne(assetSubTypeBasicDTO.getId()));
                } else {
                    AssetType assetSubType = new AssetType(assetSubTypeBasicDTO.getName(), countryId, SuggestedDataStatus.APPROVED);
                    assetSubType.setSubAssetType(true);
                    assetTypeRepository.save(assetSubType);
                    assetType.getSubAssetTypes().add(assetSubType);
                    masterAsset.setSubAssetType(assetSubType);
                }
            });
        } else {
            AssetType previousAssetType = assetTypeRepository.findByNameAndCountryIdAndSubAssetType( masterAssetDTO.getAssetType().getName(), countryId, false);
            Optional.ofNullable(previousAssetType).ifPresent(assetType -> exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetType.getName()));
            AssetType assetType = new AssetType(masterAssetDTO.getAssetType().getName(), countryId, SuggestedDataStatus.APPROVED);
            Optional.ofNullable(masterAssetDTO.getAssetSubType()).ifPresent(assetSubTypeDto -> {

                AssetType assetSubType = new AssetType(assetSubTypeDto.getName(), countryId, SuggestedDataStatus.APPROVED);
                assetSubType.setSubAssetType(true);
                assetTypeRepository.save(assetSubType);
                assetType.getSubAssetTypes().add(assetSubType);
                masterAsset.setSubAssetType(assetSubType);
            });
            assetTypeRepository.save(assetType);
            masterAsset.setAssetType(assetType);
        }
    }

    /**
     *  This method id used to update existing asset-type or sub asset-type or create new asset-type/sub asset-type
     *
     * @param countryId
     * @param masterAsset
     * @param masterAssetDTO
     */
    private void saveOrUpdateAssetType(Long countryId, MasterAsset masterAsset, MasterAssetDTO masterAssetDTO) {

        AssetType assetType;
        if (Optional.ofNullable(masterAssetDTO.getAssetType().getId()).isPresent()) {
            assetType = assetTypeRepository.getOne(masterAssetDTO.getAssetType().getId());
            masterAsset.setAssetType(assetType);
        }else {
            AssetType previousAssetType = assetTypeRepository.findByNameAndCountryIdAndSubAssetType( masterAssetDTO.getAssetType().getName(), countryId, false);
            Optional.ofNullable(previousAssetType).ifPresent(assetType1 -> exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetType1.getName()));
            assetType = new AssetType(masterAssetDTO.getAssetType().getName(), countryId, SuggestedDataStatus.APPROVED);
        }
            Optional.ofNullable(masterAssetDTO.getAssetSubType()).ifPresent(assetSubTypeBasicDTO -> {
                if (assetSubTypeBasicDTO.getId() != null) {
                    masterAsset.setSubAssetType(assetTypeRepository.getOne(assetSubTypeBasicDTO.getId()));
                } else {
                    AssetType assetSubType = new AssetType(assetSubTypeBasicDTO.getName(), countryId, SuggestedDataStatus.APPROVED);
                    assetSubType.setSubAssetType(true);
                    assetTypeRepository.save(assetSubType);
                    assetType.getSubAssetTypes().add(assetSubType);
                    masterAsset.setSubAssetType(assetSubType);
                }
            });
            assetTypeRepository.save(assetType);
            masterAsset.setAssetType(assetType);
        }


    /**
     * @param countryId
     * @return list of MasterAsset
     */

    public List<MasterAssetResponseDTO> getAllMasterAsset(Long countryId) {
        List<MasterAssetResponseDTO> masterAssetResponseDTOS = new ArrayList<>();
        List<MasterAsset> assets = masterAssetRepository.findAllByCountryId(countryId);
        for(MasterAsset asset : assets){
            masterAssetResponseDTOS.add(prepareAssetResponseDTO(asset));
        }
       return masterAssetResponseDTOS;
    }

    /**
     * This method is used to convert "MasterAsset" domain into "MasterAssetResponseDTO".
     *
     * @param masterAsset
     * @return MasterAssetResponseDTO
     */

    private MasterAssetResponseDTO prepareAssetResponseDTO(MasterAsset masterAsset){
        MasterAssetResponseDTO masterAssetResponseDTO = new MasterAssetResponseDTO(masterAsset.getId(),masterAsset.getName(),masterAsset.getDescription(), masterAsset.getSuggestedDate(), masterAsset.getSuggestedDataStatus());
        masterAssetResponseDTO.setAssetType(new AssetTypeBasicResponseDTO(masterAsset.getAssetType().getId(),masterAsset.getAssetType().getName(), masterAsset.getAssetType().isSubAssetType()));
        masterAssetResponseDTO.setAssetSubType(new AssetTypeBasicResponseDTO(masterAsset.getSubAssetType().getId(),masterAsset.getSubAssetType().getName(), masterAsset.getSubAssetType().isSubAssetType()));

        List<OrganizationTypeDTO> organizationTypes = new ArrayList<>();
        List<OrganizationSubTypeDTO> organizationSubTypes = new ArrayList<>();
        List<ServiceCategoryDTO> serviceCategories = new ArrayList<>();
        List<SubServiceCategoryDTO> subServiceCategories = new ArrayList<>();
        for(OrganizationType orgType : masterAsset.getOrganizationTypes()){
            organizationTypes.add(new OrganizationTypeDTO(orgType.getId(), orgType.getName())) ;
        }
        for(OrganizationSubType orgSubType : masterAsset.getOrganizationSubTypes()){
            organizationSubTypes.add(new OrganizationSubTypeDTO(orgSubType.getId(), orgSubType.getName())) ;
        }
        for(ServiceCategory category : masterAsset.getOrganizationServices()){
            serviceCategories.add(new ServiceCategoryDTO(category.getId(), category.getName())) ;
        }
        for(SubServiceCategory subServiceCategory : masterAsset.getOrganizationSubServices()){
            subServiceCategories.add(new SubServiceCategoryDTO(subServiceCategory.getId(), subServiceCategory.getName())) ;
        }

        masterAssetResponseDTO.setOrganizationTypes(organizationTypes);
        masterAssetResponseDTO.setOrganizationSubTypes(organizationSubTypes);
        masterAssetResponseDTO.setOrganizationServices(serviceCategories);
        masterAssetResponseDTO.setOrganizationSubServices(subServiceCategories);
        return masterAssetResponseDTO;
    }


    /**
     * @param countryId
     * @param id             id of MasterAsset
     * @param masterAssetDto
     * @return updated object of MasterAsset
     * @throws DuplicateDataException if MasterAsset not exist already exist with same  name
     *                                {@link DataNotFoundByIdException throw exception if MasterAsset not found for given id}
     */
    public MasterAssetDTO updateMasterAsset(Long countryId, Long id, MasterAssetDTO masterAssetDto) {
        MasterAsset masterAsset = masterAssetRepository.findByNameAndCountryId(masterAssetDto.getName(), countryId);
        if (Optional.ofNullable(masterAsset).isPresent() && !id.equals(masterAsset.getId())) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        masterAsset = getMetadataOfMasterAsset(masterAssetDto, masterAsset);
        masterAsset = masterAssetRepository.getOne(id);
        masterAsset.setName(masterAssetDto.getName());
        masterAsset.setDescription(masterAssetDto.getDescription());
        saveOrUpdateAssetType(countryId, masterAsset, masterAssetDto);
        masterAssetRepository.save(masterAsset);
        return masterAssetDto;
    }


    public Boolean deleteMasterAsset(Long countryId, Long id) {
        Integer updateCount = masterAssetRepository.updateMasterAsset(countryId, id);
        if(updateCount > 0){
            LOGGER.info("Master Asset is deleted successfully with id :: {}", id);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Master Asset", id);
        }
        return true;

    }


    public MasterAssetResponseDTO getMasterAssetById(Long countryId, Long id) {
        MasterAsset masterAsset = masterAssetRepository.getMasterAssetByCountryIdAndId(countryId, id);
        if (!Optional.ofNullable(masterAsset).isPresent()) {
            throw new DataNotFoundByIdException("master asset not Exist for id " + id);

        }
        return prepareAssetResponseDTO(masterAsset);
    }


    /**
     * @param countryId -country id
     * @param unitId    -unit id which suggest asset to country admin
     * @param assetDTO  -contain basic detail about asset ,name and description
     * @return
     */
    public AssetDTO saveSuggestedAssetFromUnit(Long countryId, Long unitId, AssetDTO assetDTO) {
        MasterAsset previousAsset = masterAssetRepository.findByNameAndCountryId(assetDTO.getName(), countryId);
        if (Optional.ofNullable(previousAsset).isPresent()) {
            return null;
        }
        OrgTypeSubTypeServicesAndSubServicesDTO orgTypeSubTypeServicesAndSubServicesDTO = restClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/organization_type", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrgTypeSubTypeServicesAndSubServicesDTO>>() {
        });
        MasterAsset masterAsset = new MasterAsset(assetDTO.getName(), assetDTO.getDescription(), countryId, LocalDate.now(), SuggestedDataStatus.PENDING)
                .setOrganizationTypes(Arrays.asList(new OrganizationType(orgTypeSubTypeServicesAndSubServicesDTO.getId(), orgTypeSubTypeServicesAndSubServicesDTO.getName())))
                .setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubTypeDTOS(), OrganizationSubType.class))
                .setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationServices(), ServiceCategory.class))
                .setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubServices(), SubServiceCategory.class));
        masterAssetRepository.save(masterAsset);
        assetDTO.setId(masterAsset.getId());
        return assetDTO;
    }

    /**
     * @param countryId
     * @param suggestedDataStatus
     * @return
     * @description update status of asset (suggest by unit)
     */
    public boolean updateSuggestedStatusOfMasterAsset(Long countryId, Set<Long> assetIds, SuggestedDataStatus suggestedDataStatus) {

        Integer updateCount = masterAssetRepository.updateMasterAssetStatus(countryId, assetIds,suggestedDataStatus);
        if(updateCount > 0){
            LOGGER.info("Master Assets are updated successfully with ids :: {}", assetIds);
        }else{
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Master Asset", assetIds);
        }
        return true;
    }


}
