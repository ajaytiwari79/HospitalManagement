package com.kairos.service.data_inventory.asset;

import com.kairos.dto.data_inventory.AssetDTO;
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


    public AssetDTO createAsseWithBasictDetail(Long organizationId, AssetDTO assetDTO) {

        Asset existingAsset = assetMongoRepository.findByName(organizationId, assetDTO.getName());
        if (Optional.ofNullable(existingAsset).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", " Asset ", assetDTO.getName());
        }
        AssetType assetType = assetTypeMongoRepository.findByOrganizationIdAndId(organizationId, assetDTO.getAssetType());
        if (!assetType.getSubAssetTypes().containsAll(assetDTO.getAssetSubTypes())) {
            exceptionService.invalidRequestException("message.invalid.request", "Asset Sub Type is Not Selected");
        }
        Asset newAsset = new Asset(assetDTO.getName(), assetDTO.getDescription(), assetDTO.getHostingLocation(),
                assetDTO.getAssetType(), assetDTO.getAssetSubTypes(), assetDTO.getManagingDepartment(), assetDTO.getAssetOwner(), true);
        newAsset.setOrganizationId(organizationId);
        newAsset.setHostingProvider(assetDTO.getHostingProvider());
        newAsset.setHostingType(assetDTO.getHostingType());
        newAsset.setOrgSecurityMeasures(assetDTO.getOrgSecurityMeasures());
        newAsset.setTechnicalSecurityMeasures(assetDTO.getTechnicalSecurityMeasures());
        newAsset.setStorageFormats(assetDTO.getStorageFormats());
        newAsset.setDataDisposal(assetDTO.getDataDisposal());
        newAsset.setDataRetentionPeriod(assetDTO.getDataRetentionPeriod());
        newAsset.setMaxDataSubjectVolume(assetDTO.getMaxDataSubjectVolume());
        newAsset.setMinDataSubjectVolume(assetDTO.getMinDataSubjectVolume());
        newAsset = assetMongoRepository.save(getNextSequence(newAsset));
        assetDTO.setId(newAsset.getId());
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
     * @return method return Asset with Meta Data (storgae format ,data Disposal, hosting type and etc)
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
     * @description method return aduit history of asset , old Object list and latest version also.
     * return object contain  changed field with key feilds and values with key Values in return list of map
     */
    public List<Map<String, Object>> getAssetActivities(BigInteger assetId) throws ClassNotFoundException {

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(assetId, Asset.class);
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
        if (!assetType.getSubAssetTypes().containsAll(assetDTO.getAssetSubTypes())) {
            exceptionService.invalidRequestException("message.invalid.request", "Asset Sub Type is Not Selected");
        }
        asset.setHostingLocation(assetDTO.getHostingLocation());
        asset.setName(assetDTO.getName());
        asset.setDescription(assetDTO.getDescription());
        asset.setManagingDepartment(assetDTO.getManagingDepartment());
        asset.setAssetOwner(assetDTO.getAssetOwner());
        asset.setActive(assetDTO.getActive());
        asset.setAssetType(assetDTO.getAssetType());
        asset.setAssetSubTypes(assetDTO.getAssetSubTypes());
        asset.setHostingProvider(assetDTO.getHostingProvider());
        asset.setHostingType(assetDTO.getHostingType());
        asset.setOrgSecurityMeasures(assetDTO.getOrgSecurityMeasures());
        asset.setTechnicalSecurityMeasures(assetDTO.getTechnicalSecurityMeasures());
        asset.setStorageFormats(assetDTO.getStorageFormats());
        asset.setDataDisposal(assetDTO.getDataDisposal());
        asset.setDataRetentionPeriod(assetDTO.getDataRetentionPeriod());
        asset.setMaxDataSubjectVolume(assetDTO.getMaxDataSubjectVolume());
        asset.setMinDataSubjectVolume(assetDTO.getMinDataSubjectVolume());
        asset = assetMongoRepository.save(getNextSequence(asset));
        assetDTO.setId(asset.getId());
        return assetDTO;
    }


}
