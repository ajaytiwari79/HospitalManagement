package com.kairos.service.master_data.asset_management;


import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.dto.gdpr.*;
import com.kairos.dto.gdpr.data_inventory.AssetDTO;
import com.kairos.dto.gdpr.master_data.MasterAssetDTO;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetTypeMD;
import com.kairos.persistence.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistence.model.master_data.default_asset_setting.MasterAssetMD;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.MasterAssetMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.MasterAssetRepository;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;
import com.kairos.persistence.model.master_data.default_asset_setting.*;
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
        MasterAssetMD previousAsset = masterAssetRepository.findByNameAndCountryId(masterAssetDto.getName(), countryId);
        if (Optional.ofNullable(previousAsset).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.asset", masterAssetDto.getName());
        }
        MasterAssetMD masterAsset = new MasterAssetMD(masterAssetDto.getName(), masterAssetDto.getDescription(), countryId, SuggestedDataStatus.APPROVED);
        Map<String, List> metaData = getMetadataOfMasterAsset(masterAssetDto);
        masterAsset.setOrganizationTypes(metaData.get("organizationTypes"));
        masterAsset.setOrganizationSubTypes(metaData.get("organizationSubTypes"));
        masterAsset.setOrganizationServices(metaData.get("serviceCategories"));
        masterAsset.setOrganizationSubServices(metaData.get("subServiceCategories"));
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
    private Map<String, List> getMetadataOfMasterAsset(MasterAssetDTO masterAssetDto){
        List<OrganizationType> organizationTypes = new ArrayList<>();
        List<OrganizationSubType> organizationSubTypes = new ArrayList<>();
        List<ServiceCategory> serviceCategories = new ArrayList<>();
        List<SubServiceCategory> subServiceCategories = new ArrayList<>();
        for(OrganizationTypeDTO organizationTypeDTO : masterAssetDto.getOrganizationTypeDTOS()){
            OrganizationType orgType = new OrganizationType(organizationTypeDTO.getId(), organizationTypeDTO.getName());
            organizationTypes.add(orgType);
        }
        for(OrganizationSubTypeDTO organizationSubTypeDTO : masterAssetDto.getOrganizationSubTypeDTOS()){
            OrganizationSubType orgSubType = new OrganizationSubType(organizationSubTypeDTO.getId(), organizationSubTypeDTO.getName());
            organizationSubTypes.add(orgSubType);
        }
        for(ServiceCategoryDTO category : masterAssetDto.getOrganizationServices()){
            ServiceCategory serviceCategory = new ServiceCategory(category.getId(), category.getName());
            serviceCategories.add(serviceCategory);
        }
        for(SubServiceCategoryDTO subCategory : masterAssetDto.getOrganizationSubServices()){
            SubServiceCategory subServiceCategory = new SubServiceCategory(subCategory.getId(), subCategory.getName());
            subServiceCategories.add(subServiceCategory);
        }
        Map<String , List> metadata = new HashMap<>();
        metadata.put("organisationType", organizationTypes);
        metadata.put("organisationSubType", organizationSubTypes);
        metadata.put("serviceCategory", serviceCategories);
        metadata.put("subServiceCategory", subServiceCategories);
        return metadata;
    }

    //TODO we can delete this method Refactored method is just define below this method with name "saveOrUpdateAssetType"
    private void saveAndUpdateAssetType(Long countryId, MasterAssetMD masterAsset, MasterAssetDTO masterAssetDTO) {
        if (Optional.ofNullable(masterAssetDTO.getAssetType().getId()).isPresent()) {

            AssetTypeMD assetType = assetTypeRepository.getOne(masterAssetDTO.getAssetType().getId());
            masterAsset.setAssetType(assetType);
            Optional.ofNullable(masterAssetDTO.getAssetSubType()).ifPresent(assetSubTypeBasicDTO -> {
                if (assetSubTypeBasicDTO.getId() != null) {
                    masterAsset.setSubAssetType(assetTypeRepository.getOne(assetSubTypeBasicDTO.getId()));
                } else {
                    AssetTypeMD assetSubType = new AssetTypeMD(assetSubTypeBasicDTO.getName(), countryId, SuggestedDataStatus.APPROVED);
                    assetSubType.setSubAssetType(true);
                    assetTypeRepository.save(assetSubType);
                    assetType.getSubAssetTypes().add(assetSubType);
                    masterAsset.setSubAssetType(assetSubType);
                }
            });
        } else {
            AssetTypeMD previousAssetType = assetTypeRepository.findByNameAndCountryIdAndSubAssetType( masterAssetDTO.getAssetType().getName(), countryId, false);
            Optional.ofNullable(previousAssetType).ifPresent(assetType -> {
                exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetType.getName());
            });
            AssetTypeMD assetType = new AssetTypeMD(masterAssetDTO.getAssetType().getName(), countryId, SuggestedDataStatus.APPROVED);
            Optional.ofNullable(masterAssetDTO.getAssetSubType()).ifPresent(assetSubTypeDto -> {

                AssetTypeMD assetSubType = new AssetTypeMD(assetSubTypeDto.getName(), countryId, SuggestedDataStatus.APPROVED);
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
    private void saveOrUpdateAssetType(Long countryId, MasterAssetMD masterAsset, MasterAssetDTO masterAssetDTO) {

        AssetTypeMD assetType;
        if (Optional.ofNullable(masterAssetDTO.getAssetType().getId()).isPresent()) {
            assetType = assetTypeRepository.getOne(masterAssetDTO.getAssetType().getId());
            masterAsset.setAssetType(assetType);
        }else {
            AssetTypeMD previousAssetType = assetTypeRepository.findByNameAndCountryIdAndSubAssetType( masterAssetDTO.getAssetType().getName(), countryId, false);
            Optional.ofNullable(previousAssetType).ifPresent(assetType1 -> {
                exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetType1.getName());
            });
            assetType = new AssetTypeMD(masterAssetDTO.getAssetType().getName(), countryId, SuggestedDataStatus.APPROVED);
        }
            Optional.ofNullable(masterAssetDTO.getAssetSubType()).ifPresent(assetSubTypeBasicDTO -> {
                if (assetSubTypeBasicDTO.getId() != null) {
                    masterAsset.setSubAssetType(assetTypeRepository.getOne(assetSubTypeBasicDTO.getId()));
                } else {
                    AssetTypeMD assetSubType = new AssetTypeMD(assetSubTypeBasicDTO.getName(), countryId, SuggestedDataStatus.APPROVED);
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
        List<MasterAssetMD> assets = masterAssetRepository.findAllByCountryId(countryId);
        for(MasterAssetMD asset : assets){
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

    private MasterAssetResponseDTO prepareAssetResponseDTO(MasterAssetMD masterAsset){
        MasterAssetResponseDTO masterAssetResponseDTO = new MasterAssetResponseDTO(masterAsset.getId(),masterAsset.getName(),masterAsset.getDescription(), masterAsset.getSuggestedDate(), masterAsset.getSuggestedDataStatus());
        masterAssetResponseDTO.setAssetType(new AssetTypeBasicResponseDTO(masterAsset.getAssetType().getId(),masterAsset.getAssetType().getName(), masterAsset.getAssetType().isSubAssetType()));
        masterAssetResponseDTO.setAssetSubType(new AssetTypeBasicResponseDTO(masterAsset.getSubAssetType().getId(),masterAsset.getSubAssetType().getName(), masterAsset.getSubAssetType().isSubAssetType()));

        List<OrganizationTypeDTO> organizationTypeDTOS = new ArrayList<>();
        List<OrganizationSubTypeDTO> organizationSubTypeDTOS = new ArrayList<>();
        List<ServiceCategoryDTO> serviceCategories = new ArrayList<>();
        List<SubServiceCategoryDTO> subServiceCategories = new ArrayList<>();
        for(OrganizationType orgType : masterAsset.getOrganizationTypes()){
            organizationTypeDTOS.add(new OrganizationTypeDTO(orgType.getId(), orgType.getName())) ;
        }
        for(OrganizationSubType orgSubType : masterAsset.getOrganizationSubTypes()){
            organizationSubTypeDTOS.add(new OrganizationSubTypeDTO(orgSubType.getId(), orgSubType.getName())) ;
        }
        for(ServiceCategory category : masterAsset.getOrganizationServices()){
            serviceCategories.add(new ServiceCategoryDTO(category.getId(), category.getName())) ;
        }
        for(SubServiceCategory subServiceCategory : masterAsset.getOrganizationSubServices()){
            subServiceCategories.add(new SubServiceCategoryDTO(subServiceCategory.getId(), subServiceCategory.getName())) ;
        }

        masterAssetResponseDTO.setOrganizationTypeDTOS(organizationTypeDTOS);
        masterAssetResponseDTO.setOrganizationSubTypeDTOS(organizationSubTypeDTOS);
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
        MasterAssetMD masterAsset = masterAssetRepository.findByNameAndCountryId(masterAssetDto.getName(), countryId);
        if (Optional.ofNullable(masterAsset).isPresent() && !id.equals(masterAsset.getId())) {
            throw new DuplicateDataException("master asset for name " + masterAssetDto.getName() + " exists");
        }
        Map<String, List> metaData = getMetadataOfMasterAsset(masterAssetDto);
        masterAsset = masterAssetRepository.getOne(id);
        masterAsset.setName(masterAssetDto.getName());
        masterAsset.setDescription(masterAssetDto.getDescription());
        masterAsset.setOrganizationTypes(metaData.get("organizationTypes"));
        masterAsset.setOrganizationSubTypes(metaData.get("organizationSubTypes"));
        masterAsset.setOrganizationServices(metaData.get("serviceCategories"));
        masterAsset.setOrganizationSubServices(metaData.get("subServiceCategories"));
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
        MasterAssetMD masterAsset = masterAssetRepository.getMasterAssetByCountryIdAndId(countryId, id);
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
        MasterAsset previousAsset = masterAssetMongoRepository.findByName(countryId, assetDTO.getName());
        if (Optional.ofNullable(previousAsset).isPresent()) {
            return null;
        }
        OrgTypeSubTypeServicesAndSubServicesDTO orgTypeSubTypeServicesAndSubServicesDTO = restClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/organization_type", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<OrgTypeSubTypeServicesAndSubServicesDTO>>() {
        });
        MasterAsset masterAsset = new MasterAsset(assetDTO.getName(), assetDTO.getDescription(), countryId, LocalDate.now(), SuggestedDataStatus.PENDING)
                .setOrganizationTypeDTOS(Arrays.asList(new OrganizationTypeDTO(orgTypeSubTypeServicesAndSubServicesDTO.getId(), orgTypeSubTypeServicesAndSubServicesDTO.getName())))
                .setOrganizationSubTypeDTOS(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubTypeDTOS())
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
