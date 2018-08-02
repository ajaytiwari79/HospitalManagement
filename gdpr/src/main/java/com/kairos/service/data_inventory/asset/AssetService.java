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


    public Asset createAsseWithBasictDetail(Long organizationId, Asset asset) {

        Asset existingAsset = assetMongoRepository.findByName(organizationId, asset.getName());
        if (Optional.ofNullable(existingAsset).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", " Asset ", asset.getName());
        }
        AssetType assetType = assetTypeMongoRepository.findByOrganizationIdAndId(organizationId, asset.getAssetType());
        if (!assetType.getSubAssetTypes().containsAll(asset.getAssetSubTypes())) {
            exceptionService.invalidRequestException("message.invalid.request", "Asset Sub Type is Not Selected");
        }
        Asset newAsset = new Asset(asset.getName(), asset.getDescription(), asset.getHostingLocation(),
                asset.getAssetType(), asset.getAssetSubTypes(), asset.getManagingDepartment(), asset.getAssetOwner(), true);
        newAsset.setOrganizationId(organizationId);
        newAsset.setHostingProvider(asset.getHostingProvider());
        newAsset.setHostingType(asset.getHostingType());
        newAsset.setOrgSecurityMeasures(asset.getOrgSecurityMeasures());
        newAsset.setTechnicalSecurityMeasures(asset.getTechnicalSecurityMeasures());
        newAsset.setStorageFormats(asset.getStorageFormats());
        newAsset.setDataDisposal(asset.getDataDisposal());
        newAsset.setDataRetentionPeriod(asset.getDataRetentionPeriod());
        newAsset.setMaxDataSubjectVolume(asset.getMaxDataSubjectVolume());
        newAsset.setMinDataSubjectVolume(asset.getMinDataSubjectVolume());
        assetMongoRepository.save(getNextSequence(newAsset));
        return newAsset;
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
     * @param asset          - asset dto contain meta data about asset
     * @return - updated Asset
     */
    public Asset updateAssetData(Long organizationId, BigInteger assetId, Asset asset) {

        Asset existAsset = assetMongoRepository.findByName(organizationId, asset.getName());
        if (Optional.ofNullable(existAsset).isPresent() && !assetId.equals(existAsset.getId())) {
            exceptionService.duplicateDataException("message.duplicate", " Asset ", asset.getName());
        }
        existAsset = assetMongoRepository.findByIdAndNonDeleted(organizationId, assetId);
        if (!Optional.ofNullable(existAsset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset ", assetId);
        }
        AssetType assetType = assetTypeMongoRepository.findByOrganizationIdAndId(organizationId, asset.getAssetType());
        if (!assetType.getSubAssetTypes().containsAll(asset.getAssetSubTypes())) {
            exceptionService.invalidRequestException("message.invalid.request", "Asset Sub Type is Not Selected");
        }
        existAsset.setHostingLocation(asset.getHostingLocation());
        existAsset.setName(asset.getName());
        existAsset.setDescription(asset.getDescription());
        existAsset.setManagingDepartment(asset.getManagingDepartment());
        existAsset.setAssetOwner(asset.getAssetOwner());
        existAsset.setActive(asset.getActive());
        existAsset.setAssetType(asset.getAssetType());
        existAsset.setAssetSubTypes(asset.getAssetSubTypes());
        existAsset.setHostingProvider(asset.getHostingProvider());
        existAsset.setHostingType(asset.getHostingType());
        existAsset.setOrgSecurityMeasures(asset.getOrgSecurityMeasures());
        existAsset.setTechnicalSecurityMeasures(asset.getTechnicalSecurityMeasures());
        existAsset.setStorageFormats(asset.getStorageFormats());
        existAsset.setDataDisposal(asset.getDataDisposal());
        existAsset.setDataRetentionPeriod(asset.getDataRetentionPeriod());
        existAsset.setMaxDataSubjectVolume(asset.getMaxDataSubjectVolume());
        existAsset.setMinDataSubjectVolume(asset.getMinDataSubjectVolume());
        assetMongoRepository.save(getNextSequence(existAsset));
        return existAsset;
    }


}
