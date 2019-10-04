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
import com.kairos.rest_client.GDPRGenericRestClient;
import com.kairos.rest_client.GDPRToUserIntegrationService;
import com.kairos.service.data_inventory.asset.AssetService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;


@Service
public class MasterAssetService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterAssetService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private AssetTypeService assetTypeService;

    @Inject
    private GDPRGenericRestClient restClient;

    @Inject
    private MasterAssetRepository masterAssetRepository;

    @Inject
    private AssetTypeRepository assetTypeRepository;

    @Inject
    private GDPRToUserIntegrationService gdprToUserIntegrationService;

    @Inject
    private AssetService assetService;

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
        addMetadataOfMasterAsset(masterAssetDto, masterAsset);
        addAssetTypeToMasterAsset(countryId, masterAsset, masterAssetDto);
        masterAssetRepository.save(masterAsset);
        assignAssetToUnits(countryId, masterAssetDto);
        masterAssetDto.setId(masterAsset.getId());
        return masterAssetDto;
    }

    private void assignAssetToUnits(Long countryId, MasterAssetDTO masterAssetDto){
        List<Long> organizationSubTypeId = masterAssetDto.getOrganizationSubTypes().stream().map(OrganizationSubTypeDTO::getId).collect(Collectors.toList());
        List<Long> unitIds = gdprToUserIntegrationService.getUnitIdsByOrgSubTypeId(countryId, organizationSubTypeId);
        if(isCollectionNotEmpty(unitIds)) {
            unitIds.forEach(unitId -> assetService.saveAsset(unitId, ObjectMapperUtils.copyPropertiesByMapper(masterAssetDto, AssetDTO.class),true));
        }
    }
    /**
     * This method is used to fetch all the metadata related to master asset from DTO like organisationType,
     * organisationSubType, Service Category and Sub Service Category
     *
     * @param masterAssetDto
     * @return
     */
    private void addMetadataOfMasterAsset(MasterAssetDTO masterAssetDto, MasterAsset masterAsset) {
        masterAsset.setOrganizationTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(new ArrayList<>(masterAssetDto.getOrganizationTypes()), OrganizationType.class));
        masterAsset.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(new ArrayList<>(masterAssetDto.getOrganizationSubTypes()), OrganizationSubType.class));
        masterAsset.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(new ArrayList<>(masterAssetDto.getOrganizationServices()), ServiceCategory.class));
        masterAsset.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(new ArrayList<>(masterAssetDto.getOrganizationSubServices()), SubServiceCategory.class));
    }

    /**
     * This method id used to update existing asset-type or sub asset-type or create new asset-type/sub asset-type
     *
     * @param countryId
     * @param masterAsset
     * @param masterAssetDTO
     */
    private void addAssetTypeToMasterAsset(Long countryId, MasterAsset masterAsset, MasterAssetDTO masterAssetDTO) {

        AssetType assetType;
        AssetType subAssetType = null;
        if (Optional.ofNullable(masterAssetDTO.getAssetType().getId()).isPresent()) {
            assetType = assetTypeRepository.getOne(masterAssetDTO.getAssetType().getId());
            masterAsset.setAssetType(assetType);
        } else {
            AssetType previousAssetType = assetTypeRepository.findByNameAndCountryIdAndSubAssetType(masterAssetDTO.getAssetType().getName(), countryId, false);
            Optional.ofNullable(previousAssetType).ifPresent(assetType1 -> exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetType1.getName()));
            assetType = new AssetType(masterAssetDTO.getAssetType().getName(), countryId, SuggestedDataStatus.APPROVED);
        }

        if (Optional.ofNullable(masterAssetDTO.getSubAssetType()).isPresent()) {
            if (masterAssetDTO.getSubAssetType().getId() != null) {
                Optional<AssetType> subAssetTypeObj = assetType.getSubAssetTypes().stream().filter(assetSubType -> assetSubType.getId().equals(masterAssetDTO.getSubAssetType().getId())).findAny();
                if (subAssetTypeObj.isPresent()) {
                    subAssetType = subAssetTypeObj.get();
                } else {
                    exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", masterAssetDTO.getSubAssetType().getId());
                }
            } else {
                subAssetType = new AssetType(masterAssetDTO.getSubAssetType().getName(), countryId, SuggestedDataStatus.APPROVED);
                subAssetType.setSubAssetType(true);
            }
            masterAsset.setSubAssetType(subAssetType);
        }
        assetTypeRepository.save(assetType);
        masterAsset.setAssetType(assetType);
        if (subAssetType != null) {
            subAssetType.setAssetType(assetType);
            assetTypeRepository.save(subAssetType);
            masterAsset.setSubAssetType(subAssetType);
        }
    }


    /**
     * @param countryId
     * @return list of MasterAsset
     */

    public List<MasterAssetResponseDTO> getAllMasterAsset(Long countryId) {
        List<MasterAssetResponseDTO> masterAssetResponseDTOS = new ArrayList<>();
        List<MasterAsset> assets = masterAssetRepository.findAllByCountryId(countryId);
        for (MasterAsset asset : assets) {
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

    private MasterAssetResponseDTO prepareAssetResponseDTO(MasterAsset masterAsset) {
        MasterAssetResponseDTO masterAssetResponseDTO = new MasterAssetResponseDTO(masterAsset.getId(), masterAsset.getName(), masterAsset.getDescription(), masterAsset.getSuggestedDate(), masterAsset.getSuggestedDataStatus());

        Optional.ofNullable(masterAsset.getAssetType()).ifPresent(assetType -> masterAssetResponseDTO.setAssetType(new AssetTypeBasicResponseDTO(assetType.getId(), assetType.getName(), assetType.isSubAssetType())));
        Optional.ofNullable(masterAsset.getSubAssetType()).ifPresent(subAssetType -> masterAssetResponseDTO.setSubAssetType(new AssetTypeBasicResponseDTO(subAssetType.getId(), subAssetType.getName(), subAssetType.isSubAssetType())));
        List<OrganizationTypeDTO> organizationTypes = new ArrayList<>();
        List<OrganizationSubTypeDTO> organizationSubTypes = new ArrayList<>();
        List<ServiceCategoryDTO> serviceCategories = new ArrayList<>();
        List<SubServiceCategoryDTO> subServiceCategories = new ArrayList<>();
        for (OrganizationType orgType : masterAsset.getOrganizationTypes()) {
            organizationTypes.add(new OrganizationTypeDTO(orgType.getId(), orgType.getName()));
        }
        for (OrganizationSubType orgSubType : masterAsset.getOrganizationSubTypes()) {
            organizationSubTypes.add(new OrganizationSubTypeDTO(orgSubType.getId(), orgSubType.getName()));
        }
        for (ServiceCategory category : masterAsset.getOrganizationServices()) {
            serviceCategories.add(new ServiceCategoryDTO(category.getId(), category.getName()));
        }
        for (SubServiceCategory subServiceCategory : masterAsset.getOrganizationSubServices()) {
            subServiceCategories.add(new SubServiceCategoryDTO(subServiceCategory.getId(), subServiceCategory.getName()));
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
            exceptionService.duplicateDataException("message.duplicate", "message.asset", masterAssetDto.getName());
        }
        addMetadataOfMasterAsset(masterAssetDto, masterAsset);
        masterAsset = masterAssetRepository.getOne(id);
        masterAsset.setName(masterAssetDto.getName());
        masterAsset.setDescription(masterAssetDto.getDescription());
        addAssetTypeToMasterAsset(countryId, masterAsset, masterAssetDto);
        masterAssetRepository.save(masterAsset);
        return masterAssetDto;
    }


    public Boolean deleteMasterAsset(Long countryId, Long id) {
        Integer updateCount = masterAssetRepository.updateMasterAsset(countryId, id);
        if (updateCount > 0) {
            LOGGER.info("Master Asset is deleted successfully with id :: {}", id);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.asset", id);
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
        MasterAsset masterAsset = new MasterAsset(assetDTO.getName(), assetDTO.getDescription(), countryId, LocalDate.now(), SuggestedDataStatus.PENDING);
        masterAsset.setOrganizationTypes(Arrays.asList(new OrganizationType(orgTypeSubTypeServicesAndSubServicesDTO.getId(), orgTypeSubTypeServicesAndSubServicesDTO.getName())));
        masterAsset.setOrganizationSubTypes(ObjectMapperUtils.copyPropertiesOfListByMapper(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubTypeDTOS(), OrganizationSubType.class));
        masterAsset.setOrganizationServices(ObjectMapperUtils.copyPropertiesOfListByMapper(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationServices(), ServiceCategory.class));
        masterAsset.setOrganizationSubServices(ObjectMapperUtils.copyPropertiesOfListByMapper(orgTypeSubTypeServicesAndSubServicesDTO.getOrganizationSubServices(), SubServiceCategory.class));
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
    public boolean updateStatusOfSuggestedMasterAsset(Long countryId, Set<Long> assetIds, SuggestedDataStatus suggestedDataStatus) {
        if (SuggestedDataStatus.APPROVED.equals(suggestedDataStatus)) {
            List<MasterAsset> masterAssetList = masterAssetRepository.findAllByCountryIdAndIds(countryId, assetIds);
            masterAssetList.forEach(masterAsset -> validateMasterAsset(masterAsset));
        }
        Integer updateCount = masterAssetRepository.updateMasterAssetStatus(countryId, assetIds, suggestedDataStatus);
        if (updateCount > 0) {
            LOGGER.info("Master Assets are updated successfully with ids :: {}", assetIds);
        } else {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.asset", assetIds);
        }
        return true;
    }

    private void validateMasterAsset(MasterAsset masterAsset) {
        if (!Optional.ofNullable(masterAsset.getAssetType()).isPresent())
            exceptionService.invalidRequestException("message.add.mandatory.field.status.approved", masterAsset.getName());
        if (!Optional.ofNullable(masterAsset.getName()).isPresent())
            exceptionService.invalidRequestException("message.add.mandatory.field.status.approved", masterAsset.getName());
        if (!Optional.ofNullable(masterAsset.getDescription()).isPresent())
            exceptionService.invalidRequestException("message.add.mandatory.field.status.approved", masterAsset.getName());
        if (CollectionUtils.isEmpty(masterAsset.getOrganizationTypes()))
            exceptionService.invalidRequestException("message.add.mandatory.field.status.approved", masterAsset.getName());
        if (CollectionUtils.isEmpty(masterAsset.getOrganizationSubTypes()))
            exceptionService.invalidRequestException("message.add.mandatory.field.status.approved", masterAsset.getName());
        if (CollectionUtils.isEmpty(masterAsset.getOrganizationServices()))
            exceptionService.invalidRequestException("message.add.mandatory.field.status.approved", masterAsset.getName());
        if (CollectionUtils.isEmpty(masterAsset.getOrganizationSubServices()))
            exceptionService.invalidRequestException("message.add.mandatory.field.status.approved", masterAsset.getName());
    }

}
