package com.kairos.service.data_inventory.asset;

import com.kairos.gdpr.data_inventory.AssetDTO;
import com.kairos.gdpr.data_inventory.AssetRelateProcessingActivityDTO;
import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistance.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistance.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
import com.kairos.util.ObjectMapperUtils;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class AssetService extends MongoBaseService {


    @Inject
    private AssetMongoRepository assetMongoRepository;

    @Inject
    private Javers javers;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private JaversCommonService javersCommonService;


    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;


    public AssetDTO createAssetWithBasicDetail(Long organizationId, AssetDTO assetDTO) {
        Asset previousAsset = assetMongoRepository.findByName(organizationId, assetDTO.getName());
        if (Optional.ofNullable(previousAsset).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", " Asset ", assetDTO.getName());
        }
        AssetType assetType = assetTypeMongoRepository.findByOrganizationIdAndId(organizationId, assetDTO.getAssetType());
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset  type", assetDTO.getAssetType());
        } else {
            if (Optional.ofNullable(assetType.getSubAssetTypes()).isPresent()) {
                if (!assetType.getSubAssetTypes().containsAll(assetDTO.getAssetSubTypes())) {
                    exceptionService.invalidRequestException("message.invalid", " invalid Sub Asset is Selected ");
                }
            }
        }
        Asset asset = new Asset(assetDTO.getName(), assetDTO.getDescription(), assetDTO.getHostingLocation(),
                assetDTO.getAssetType(), assetDTO.getAssetSubTypes(), assetDTO.getManagingDepartment(), assetDTO.getAssetOwner(), true);
        asset.setOrganizationId(organizationId);
        asset.setHostingProvider(assetDTO.getHostingProvider());
        asset.setHostingType(assetDTO.getHostingType());
        asset.setOrgSecurityMeasures(assetDTO.getOrgSecurityMeasures());
        asset.setTechnicalSecurityMeasures(assetDTO.getTechnicalSecurityMeasures());
        asset.setStorageFormats(assetDTO.getStorageFormats());
        asset.setDataDisposal(assetDTO.getDataDisposal());
        asset.setDataRetentionPeriod(assetDTO.getDataRetentionPeriod());
        asset.setMaxDataSubjectVolume(assetDTO.getMaxDataSubjectVolume());
        asset.setMinDataSubjectVolume(assetDTO.getMinDataSubjectVolume());
        asset = assetMongoRepository.save(asset);
        assetDTO.setId(asset.getId());
        return assetDTO;
    }


    public Boolean deleteAssetById(Long organizationId, BigInteger id) {
        Asset asset = assetMongoRepository.findByIdAndNonDeleted(organizationId, id);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset " + id);
        }
        delete(asset);
        return true;
    }


    /**
     * @param
     * @param organizationId
     * @param id
     * @return method return Asset with Meta Data (storage format ,data Disposal, hosting type and etc)
     */
    public AssetResponseDTO getAssetWithMetadataById(Long organizationId, BigInteger id) {
        AssetResponseDTO asset = assetMongoRepository.findAssetWithMetaDataById(organizationId, id);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset " + id);
        }
        return asset;
    }


    /**
     * @param
     * @param organizationId
     * @return return list Of Asset With Meta Data
     */
    public List<AssetResponseDTO> getAllAssetWithMetadata(Long organizationId) {
        return assetMongoRepository.findAllAssetWithMetaData(organizationId);
    }


    /**
     * @param assetId
     * @return
     * @description method return audit history of asset , old Object list and latest version also.
     * return object contain  changed field with key fields and values with key Values in return list of map
     */
    public List<Map<String, Object>> getAssetActivitiesHistory(BigInteger assetId, int size, int skip) {

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(assetId, Asset.class).limit(size).skip(skip);
        List<CdoSnapshot> changes = javers.findSnapshots(jqlQuery.build());
        changes.sort((o1, o2) -> -1 * (int) o1.getVersion() - (int) o2.getVersion());
        return javersCommonService.getHistoryMap(changes, assetId, Asset.class);


    }


    /**
     * @param
     * @param organizationId
     * @param assetId        - asset id
     * @param assetDTO       - asset dto contain meta data about asset
     * @return - updated Asset
     */
    public AssetDTO updateAssetData(Long organizationId, BigInteger assetId, AssetDTO assetDTO) {

        Asset asset = assetMongoRepository.findByName(organizationId, assetDTO.getName());
        if (Optional.ofNullable(asset).isPresent() && !assetId.equals(asset.getId())) {
            exceptionService.duplicateDataException("message.duplicate", " Asset ", assetDTO.getName());
        }
        asset = assetMongoRepository.findByIdAndNonDeleted(organizationId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset ", assetId);
        }
        AssetType assetType = assetTypeMongoRepository.findByOrganizationIdAndId(organizationId, assetDTO.getAssetType());
        if (!Optional.ofNullable(assetType).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset Type", assetDTO.getAssetType());

        } else {
            if (Optional.ofNullable(assetType.getSubAssetTypes()).isPresent()) {
                if (!assetType.getSubAssetTypes().containsAll(assetDTO.getAssetSubTypes())) {
                    exceptionService.invalidRequestException("message.invalid", " invalid Sub Asset is Selected ");
                }
            }
        }
        ObjectMapperUtils.copyProperties(assetDTO, asset);
        assetMongoRepository.save(asset);
        return assetDTO;
    }


    public Asset addRelatedProcessingActivitiesAndSubProcess(Long unitId, BigInteger assetId, AssetRelateProcessingActivityDTO assetRelateProcessingActivityDTO) {
        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset ", assetId);
        }
        asset.setProcessingActivities(assetRelateProcessingActivityDTO.getProcessingActivities());
        asset.setSubProcessingActivities(assetRelateProcessingActivityDTO.getSubProcessingActivities());
        assetMongoRepository.save(asset);
        return asset;
    }


}
