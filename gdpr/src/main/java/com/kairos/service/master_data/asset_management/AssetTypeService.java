package com.kairos.service.master_data.asset_management;


import com.kairos.commons.custom_exception.DuplicateDataException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.BasicRiskDTO;
import com.kairos.dto.gdpr.master_data.AssetTypeDTO;
import com.kairos.dto.gdpr.metadata.AssetTypeBasicDTO;
import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.MasterAssetRepository;
import com.kairos.response.dto.common.RiskBasicResponseDTO;
import com.kairos.response.dto.master_data.AssetTypeRiskResponseDTO;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.risk_management.RiskService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;


@Service
public class AssetTypeService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetTypeService.class);


    @Inject
    private ExceptionService exceptionService;

    @Inject
    private RiskService riskService;

    @Inject
    private AssetTypeRepository assetTypeRepository;

    @Inject
    private MasterAssetRepository masterAssetRepository;


    /**
     * @param countryId
     * @param
     * @param assetTypeDto contain asset data ,and list of sub asset types
     * @return asset type object
     * @throws DuplicateDataException if asset type is already present with same name
     * @description method create Asset type if sub Asset Types if present then create and add sub Asset Types to Asset type.
     */
    public AssetTypeDTO createAssetTypeAndAddSubAssetTypes(Long countryId, AssetTypeDTO assetTypeDto) {


        AssetType assetTypeExist = assetTypeRepository.findByNameAndCountryIdAndSubAssetType(assetTypeDto.getName(),countryId,  false);
        if (Optional.ofNullable(assetTypeExist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetTypeDto.getName());
        }
        AssetType assetType = new AssetType(assetTypeDto.getName(), countryId, SuggestedDataStatus.APPROVED);
        List<Risk> assetTypeRisks = new ArrayList<>();
        if (!assetTypeDto.getSubAssetTypes().isEmpty()) {
            List<AssetType> subAssetTypeList = buildSubAssetTypesListAndRiskAndLinkedToAssetType(countryId, assetTypeDto.getSubAssetTypes(),assetType);
            assetType.setHasSubAsset(true);
            assetType.setSubAssetTypes(subAssetTypeList);
        }
        assetTypeRisks = ObjectMapperUtils.copyPropertiesOfListByMapper(assetTypeDto.getRisks(), Risk.class);
        assetTypeRisks.forEach(risk -> risk.setCountryId(countryId));
        assetType.setRisks(assetTypeRisks);
        assetTypeRepository.save(assetType);
        assetTypeDto.setId(assetType.getId());
        return assetTypeDto;
    }

    /**
     * @param countryId
     * @param subAssetTypesDto contain list of sub Asset DTOs
     * @return create new Sub Asset type ids
     */
    private List<AssetType> buildSubAssetTypesListAndRiskAndLinkedToAssetType(Long countryId, List<AssetTypeDTO> subAssetTypesDto, AssetType assetType) {

        checkForDuplicacyInNameOfAssetType(subAssetTypesDto);
        List<AssetType> subAssetTypes = new ArrayList<>();
        List<Risk> subAssetRisks = new ArrayList<>();
        for (AssetTypeDTO subAssetTypeDto : subAssetTypesDto) {
            AssetType assetSubType = new AssetType(subAssetTypeDto.getName(), countryId, SuggestedDataStatus.APPROVED);
            assetSubType.setSubAssetType(true);
            assetSubType.setAssetType(assetType);
            subAssetRisks = ObjectMapperUtils.copyPropertiesOfListByMapper(subAssetTypeDto.getRisks(), Risk.class);
            subAssetRisks.forEach(risk -> risk.setCountryId(countryId));
            assetSubType.setRisks(subAssetRisks);
            subAssetTypes.add(assetSubType);
        }
        return subAssetTypes;
    }


    /**
     * @param countryId
     * @param subAssetTypesDto contain list of Existing Sub Asset type which need to we update
     * @return map of Sub asset Types List and Ids (List for rollback)
     * @description this method update existing Sub asset Types and return list of Sub Asset Types and  ids list
     */
    private List<AssetType> updateSubAssetTypes(Long countryId, List<AssetTypeDTO> subAssetTypesDto, AssetType assetType) {
        List<BasicRiskDTO> newRiskOfSubAssetType = new ArrayList<>();
        Map<Long, AssetTypeDTO> subAssetTypeDtoCorrespondingToIds = new HashMap<>();
        Map<Long, BasicRiskDTO> subAssetTypeExistingRiskDtoCorrespondingToIds = new HashMap<>();
        Map<Long, List<BasicRiskDTO>> subAssetTypeNewRiskDto = new HashMap<>();
        List<AssetType> subAssetTypes = new ArrayList<>();
        subAssetTypesDto.forEach(subAssetTypeDto -> {
            if (Optional.ofNullable(subAssetTypeDto.getId()).isPresent()) {
                subAssetTypeDtoCorrespondingToIds.put(subAssetTypeDto.getId(), subAssetTypeDto);
                subAssetTypeDto.getRisks().forEach( subAssetTypeRiskDto -> {
                    if (Optional.ofNullable(subAssetTypeRiskDto.getId()).isPresent()) {
                        subAssetTypeExistingRiskDtoCorrespondingToIds.put(subAssetTypeRiskDto.getId(), subAssetTypeRiskDto);
                    }else{
                        newRiskOfSubAssetType.add(subAssetTypeRiskDto);
                    }
                });
                subAssetTypeNewRiskDto.put(subAssetTypeDto.getId(), newRiskOfSubAssetType);
            } else {
                AssetType assetSubType = new AssetType(subAssetTypeDto.getName(), countryId, SuggestedDataStatus.APPROVED);
                assetSubType.setSubAssetType(true);
                assetSubType.setAssetType(assetType);
                List<Risk>  subAssetRisks = ObjectMapperUtils.copyPropertiesOfListByMapper(subAssetTypeDto.getRisks(), Risk.class);
                assetSubType.setRisks(subAssetRisks);
                subAssetTypes.add(assetSubType);
            }

        });

        assetType.getSubAssetTypes().forEach(subAssetType -> {
            AssetTypeDTO subAssetTypeDto = subAssetTypeDtoCorrespondingToIds.get(subAssetType.getId());
            subAssetType.setName(subAssetTypeDto.getName());
            if(!subAssetType.getRisks().isEmpty() && !subAssetTypeExistingRiskDtoCorrespondingToIds.isEmpty()){
            subAssetType.getRisks().forEach(subAssetTypeRisk -> {
                BasicRiskDTO basicRiskDTO = subAssetTypeExistingRiskDtoCorrespondingToIds.get(subAssetTypeRisk.getId());
                subAssetTypeRisk.setName(basicRiskDTO.getName());
                subAssetTypeRisk.setDescription(basicRiskDTO.getDescription());
                subAssetTypeRisk.setRiskRecommendation(basicRiskDTO.getRiskRecommendation());
                subAssetTypeRisk.setRiskLevel(basicRiskDTO.getRiskLevel());
               // subAssetTypeRisk.setAssetType(subAssetType);
            });
            }
            subAssetTypeNewRiskDto.get(subAssetType.getId()).forEach( newRisk -> {
                Risk risk = new Risk(newRisk.getName(), newRisk.getDescription(), newRisk.getRiskRecommendation(), newRisk.getRiskLevel() );
                risk.setCountryId(countryId);
                subAssetType.getRisks().add(risk);
            });
            subAssetTypes.add(subAssetType);
        });


        return subAssetTypes;
    }


    /**
     * @param countryId
     * @param
     * @return return list of Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public List<AssetTypeRiskResponseDTO> getAllAssetTypeWithSubAssetTypeAndRisk(Long countryId) {
        List<AssetType> assetTypes = assetTypeRepository.getAllAssetTypes(countryId);
        List<AssetTypeRiskResponseDTO> assetTypesWithAllData = new ArrayList<>();
        for(AssetType assetType : assetTypes) {
            assetTypesWithAllData.add(buildAssetTypeOrSubTypeResponseData(assetType));
        }
        return assetTypesWithAllData;
    }

    public void findAndSaveAllAssetTypeWithSubAssetTypeAndRiskNotAssociatedWithAssetForUnitLevel(Long countryId, Long unitId){
        List<AssetType> assetTypes = assetTypeRepository.getAllAssetTypesNotAssociatedWithAsset(countryId);
        List unitLevelAssetTypes = prepareAssetTypeDataForUnitLevel(assetTypes, unitId, false);
        assetTypeRepository.saveAll(unitLevelAssetTypes);
    }


    List<AssetType> prepareAssetTypeDataForUnitLevel(List<AssetType> assetTypes, Long unitId, boolean isSubAssetType){
        List<AssetType> unitLevelAssetTypes = new ArrayList<>();
        assetTypes.forEach(assetType -> {
            AssetType unitLevelAssetType = new AssetType();
            unitLevelAssetType.setName(assetType.getName());
            unitLevelAssetType.setHasSubAsset(assetType.isHasSubAsset());
            unitLevelAssetType.setSubAssetType(assetType.isSubAssetType());
            unitLevelAssetType.setOrganizationId(unitId);
            unitLevelAssetType.setCountryId(null);
            unitLevelAssetType.setRisks(ObjectMapperUtils.copyPropertiesOfListByMapper(assetType.getRisks(), Risk.class));
            if(!isSubAssetType) {
                unitLevelAssetType.setSubAssetTypes(prepareAssetTypeDataForUnitLevel(assetType.getSubAssetTypes(), unitId, true));
            }
            unitLevelAssetTypes.add(unitLevelAssetType);
        });
            return unitLevelAssetTypes;
    }

    /**
     *  THis method is used to build response of asset type and asset sub type. This method used recursion
     *  to prepare the data of asset sub type.
     *
     * @param assetType - It may be asset type or asset sub type.
     * @return List<AssetTypeRiskResponseDTO> - List of asset-type or Sub asset-type response DTO.
     */
    //TODO Will try by Object mapper
    private AssetTypeRiskResponseDTO buildAssetTypeOrSubTypeResponseData(AssetType assetType){
            List<AssetTypeRiskResponseDTO> subAssetTypeData = new ArrayList<>();
            AssetTypeRiskResponseDTO assetTypeRiskResponseDTO = new AssetTypeRiskResponseDTO();
            assetTypeRiskResponseDTO.setId(assetType.getId());
            assetTypeRiskResponseDTO.setName(assetType.getName());
            assetTypeRiskResponseDTO.setHasSubAsset(assetType.isHasSubAsset());
            if(!assetType.getRisks().isEmpty()){
                assetTypeRiskResponseDTO.setRisks(buildAssetTypeRisksResponse(assetType.getRisks()));
            }
            if(assetType.isHasSubAsset()){
                assetType.getSubAssetTypes().forEach( subAssetType -> subAssetTypeData.add(buildAssetTypeOrSubTypeResponseData(subAssetType)));
                assetTypeRiskResponseDTO.setSubAssetTypes(subAssetTypeData);
            }
        return assetTypeRiskResponseDTO;
    }

    /**
     * Description : This method is used to convert Risks of asset-type or Sub asset-type to Risk Response DTO
     *  Convert Risk into RiskBasicResponseDTO
     * @param risks - Risks of asset-type or Sub asset-type
     * @return List<RiskBasicResponseDTO> - List of RiskResponse DTO.
     */
    //TODO Will try by Object mapper
    private List<RiskBasicResponseDTO> buildAssetTypeRisksResponse(List<Risk> risks){
        List<RiskBasicResponseDTO> riskBasicResponseDTOS = new ArrayList<>();
        for(Risk assetTypeRisk : risks){
            RiskBasicResponseDTO riskBasicResponseDTO = new RiskBasicResponseDTO();
            riskBasicResponseDTO.setId(assetTypeRisk.getId());
            riskBasicResponseDTO.setName(assetTypeRisk.getName());
            riskBasicResponseDTO.setDescription(assetTypeRisk.getDescription());
            riskBasicResponseDTO.setRiskRecommendation(assetTypeRisk.getRiskRecommendation());
            riskBasicResponseDTO.setRiskLevel(assetTypeRisk.getRiskLevel());
            riskBasicResponseDTO.setDaysToReminderBefore(assetTypeRisk.getDaysToReminderBefore());
            riskBasicResponseDTO.setReminderActive(assetTypeRisk.isReminderActive());
            riskBasicResponseDTOS.add(riskBasicResponseDTO);
        }
        return riskBasicResponseDTOS;
    }

    /**
     * @param countryId
     * @param
     * @return return Asset types with sub Asset types if exist and if sub asset not exist then return empty array
     */
    public AssetTypeRiskResponseDTO getAssetTypeById(Long countryId, Long id) {
        AssetType assetType = assetTypeRepository.findByIdAndCountryIdAndDeleted(id, countryId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", id);
        }
        return buildAssetTypeOrSubTypeResponseData(assetType);

    }


    public Boolean deleteAssetType(Long countryId, Long assetTypeId) {

        List<String> masterAssetsLinkedWithAssetType = masterAssetRepository.findMasterAssetsLinkedWithAssetType(countryId, assetTypeId);
        if (CollectionUtils.isNotEmpty(masterAssetsLinkedWithAssetType)) {
            exceptionService.invalidRequestException("message.metaData.linked.with.asset", "message.assetType",  StringUtils.join(masterAssetsLinkedWithAssetType, ','));
        }
        AssetType assetType = assetTypeRepository.findByCountryIdAndId(countryId, assetTypeId,false);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", assetTypeId);
        }
        assetType.delete();
        assetTypeRepository.save(assetType);
        return true;

    }


    /**
     * @param countryId
     * @param
     * @param assetTypeId  id of Asset Type to which Sub Asset Types Link.
     * @param assetTypeDto asset type Dto contain list of Existing sub Asset types which need to be update and New SubAsset Types  which we need to create and add to asset afterward.
     * @return Asset Type with updated Sub Asset and new Sub Asset Types
     * @throws DuplicateDataException if Asset type is already present with same name .
     * @description method simply (update already exit Sub asset types if id is present)and (add create new sub asset types if id is not present in sub asset types)
     */
    public AssetTypeDTO updateAssetTypeUpdateAndCreateNewSubAssetsAndAddToAssetType(Long countryId, Long assetTypeId, AssetTypeDTO assetTypeDto) {
        AssetType assetType = assetTypeRepository.findByNameAndCountryIdAndSubAssetType(assetTypeDto.getName(), countryId, false);
        if (Optional.ofNullable(assetType).isPresent() && !assetTypeId.equals(assetType.getId())) {
            exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetTypeDto.getName());
        }
        assetType = assetTypeRepository.findByCountryIdAndId(countryId, assetTypeId, false);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", assetTypeId);
        }
        assetType.setName(assetTypeDto.getName());
        List<AssetType> subAssetTypeList = updateSubAssetTypes(countryId, assetTypeDto.getSubAssetTypes(), assetType);
        assetType.setSubAssetTypes(subAssetTypeList);
        updateOrAddAssetTypeRisk(assetType,assetTypeDto);
        assetType.getRisks().forEach(risk -> risk.setCountryId(countryId));
        assetTypeRepository.save(assetType);
        return assetTypeDto;

    }

    private AssetType updateOrAddAssetTypeRisk(AssetType assetType, AssetTypeDTO assetTypeDto){
        List<BasicRiskDTO> newRisks = new ArrayList<>();
        List<Risk> assetTypeRisks = assetType.getRisks();
        Map<Long, BasicRiskDTO> existingRiskDtoCorrespondingToIds = new HashMap<>();
        assetTypeDto.getRisks().forEach( assetTypeRiskDto -> {
            if (Optional.ofNullable(assetTypeRiskDto.getId()).isPresent()) {
                existingRiskDtoCorrespondingToIds.put(assetTypeRiskDto.getId(), assetTypeRiskDto);
            }else{
                newRisks.add(assetTypeRiskDto);
            }
        });
        List<Risk> existingRisk = assetType.getRisks();
        if(!existingRisk.isEmpty() && ! existingRiskDtoCorrespondingToIds.isEmpty()) {
            existingRisk.forEach(assetTypeRisk -> {
                BasicRiskDTO basicRiskDTO = existingRiskDtoCorrespondingToIds.get(assetTypeRisk.getId());
                assetTypeRisk.setName(basicRiskDTO.getName());
                assetTypeRisk.setDescription(basicRiskDTO.getDescription());
                assetTypeRisk.setRiskRecommendation(basicRiskDTO.getRiskRecommendation());
                assetTypeRisk.setRiskLevel(basicRiskDTO.getRiskLevel());
            });
        }
        newRisks.forEach(newRisk -> {
            Risk risk = new Risk(newRisk.getName(), newRisk.getDescription(), newRisk.getRiskRecommendation(), newRisk.getRiskLevel());
            assetTypeRisks.add(risk);
        });
        assetType.setRisks(assetTypeRisks);
        return  assetType;
    }


    /*
      @param countryId
     * @param assetTypeId
     * @param riskId
     * @return
     */
    //TODO
   /* public boolean unlinkRiskFromAssetTypeOrSubAssetTypeAndDeletedRisk(Long countryId, BigInteger assetTypeId, BigInteger riskId) {

        AssetType assetType = assetTypeMongoRepository.findByCountryIdAndId(countryId, assetTypeId);
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.assetType", assetTypeId);
        }
        assetType.getRisks().remove(riskId);
        riskMongoRepository.safeDeleteById(riskId);
        assetTypeMongoRepository.save(assetType);
        return true;
    }*/


    /**
     * @return
     */
    public AssetTypeBasicDTO saveSuggestedAssetTypeAndSubAssetTypeFromUnit(Long countryId, AssetTypeBasicDTO assetTypeDTO) {
        AssetType previousAssetType = assetTypeRepository.findByNameAndCountryIdAndSubAssetType(assetTypeDTO.getName(), countryId, false);
        if (Optional.ofNullable(previousAssetType).isPresent()) {
            return null;
        }
        AssetType assetType = new AssetType(assetTypeDTO.getName(), countryId, SuggestedDataStatus.PENDING);
        assetType.setSuggestedDate(LocalDate.now());
        List<AssetType> subAssetTypes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assetTypeDTO.getSubAssetTypes())) {
            for (AssetTypeBasicDTO subAssetTypeDTO : assetTypeDTO.getSubAssetTypes()) {
                AssetType subAssetType = new AssetType(subAssetTypeDTO.getName(), countryId, SuggestedDataStatus.PENDING);
                subAssetType.setSuggestedDate(LocalDate.now());
                subAssetType.setSubAssetType(true);
                subAssetTypes.add(subAssetType);
            }
        }
        if (!subAssetTypes.isEmpty()) {
            assetType.setSubAssetTypes(subAssetTypes);
        }
        assetTypeRepository.save(assetType);
        assetTypeDTO.setId(assetType.getId());
        return assetTypeDTO;
    }


    /**
     * @param assetTypeDTOs check for duplicates in name of Asset types
     */
    private void checkForDuplicacyInNameOfAssetType(List<AssetTypeDTO> assetTypeDTOs) {
        List<String> names = new ArrayList<>();
        for (AssetTypeDTO assetTypeDTO : assetTypeDTOs) {
            if (names.contains(assetTypeDTO.getName().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "message.assetType", assetTypeDTO.getName());
            }
            names.add(assetTypeDTO.getName().toLowerCase());
        }
    }


}
