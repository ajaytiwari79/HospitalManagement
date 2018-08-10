package com.kairos.service.data_inventory;

import com.kairos.dto.data_inventory.AssetDTO;
import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.javers.JaversCommonService;
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


    public Asset createAsseWithBasictDetail(Long countryId, Long organizationId, AssetDTO assetDto) {

        Asset exist = assetMongoRepository.findByName(countryId, organizationId, assetDto.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", " Asset ", assetDto.getName());
        }
        Asset asset = new Asset(assetDto.getName(), assetDto.getDescription(), assetDto.getHostingLocation(), countryId,
                assetDto.getAssetType(), assetDto.getAssetSubTypes(), assetDto.getManagingDepartment(), assetDto.getAssetOwner(),true);
        asset.setOrganizationId(organizationId);
        asset.setHostingProvider(assetDto.getHostingProvider());
        asset.setHostingType(assetDto.getHostingType());
        asset.setOrgSecurityMeasures(assetDto.getOrgSecurityMeasures());
        asset.setTechnicalSecurityMeasures(assetDto.getTechnicalSecurityMeasures());
        asset.setStorageFormats(assetDto.getStorageFormats());
        asset.setDataDisposal(assetDto.getDataDisposal());
        asset.setDataRetentionPeriod(assetDto.getDataRetentionPeriod());
        asset.setMaxDataSubjectVolume(assetDto.getMaxDataSubjectVolume());
        asset.setMinDataSubjectVolume(assetDto.getMinDataSubjectVolume());
        assetMongoRepository.save(sequenceGenerator(asset));
        return asset;
    }


    public Boolean deleteAssetById(Long countryId, Long organizationId, BigInteger id) {
        Asset asset = assetMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset " + id);
        }
        delete(asset);
        return true;
    }


    /**
     * @param countryId
     * @param organizationId
     * @param id
     * @return method return Asset with Meta Data (storgae format ,data Disposal, hosting type and etc)
     */
    public AssetResponseDTO getAssetWithMetadataById(Long countryId, Long organizationId, BigInteger id) {
        AssetResponseDTO asset = assetMongoRepository.findAssetWithMetaDataById(countryId, organizationId, id);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset " + id);
        }
        return asset;
    }


    /**
     * @param countryId
     * @param organizationId
     * @return return list Of Asset With Meta Data
     */
    public List<AssetResponseDTO> getAllAssetWithMetadata(Long countryId, Long organizationId) {
        return assetMongoRepository.findAllAssetWithMetaData(countryId, organizationId);
    }


    /**
     * @description method return aduit history of asset , old Object list and latest version also.
     * return object contain  changed field with key feilds and values with key Values in return list of map
     * @param assetId
     * @return
     */
    public List<Map<String, Object>> getAssetActivities( BigInteger assetId) throws ClassNotFoundException {

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(assetId, Asset.class);
        List<CdoSnapshot> changes = javers.findSnapshots(jqlQuery.build());
        changes.sort((o1, o2) -> -1 * (int) o1.getVersion() - (int) o2.getVersion());
        return javersCommonService.getHistoryMap(changes, assetId, Asset.class);


    }


    /**
     * @param countryId
     * @param organizationId
     * @param assetId        - asset id
     * @param assetDto       - asset dto contain meta data about asset
     * @return - updated Asset
     */
    public Asset updateAssetData(Long countryId, Long organizationId, BigInteger assetId, AssetDTO assetDto) {

        Asset existAsset = assetMongoRepository.findByName(countryId, organizationId, assetDto.getName());
        if (Optional.ofNullable(existAsset).isPresent() && !assetId.equals(existAsset.getId())) {
            exceptionService.duplicateDataException("message.duplicate", " Asset ", assetDto.getName());
        }
        existAsset = assetMongoRepository.findByIdAndNonDeleted(countryId, organizationId, assetId);
        if (!Optional.ofNullable(existAsset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " Asset ", assetId);
        }
        existAsset.setHostingLocation(assetDto.getHostingLocation());
        existAsset.setName(assetDto.getName());
        existAsset.setDescription(assetDto.getDescription());
        existAsset.setManagingDepartment(assetDto.getManagingDepartment());
        existAsset.setAssetOwner(assetDto.getAssetOwner());
        existAsset.setActive(assetDto.getActive());
        existAsset.setAssetType(assetDto.getAssetType());
        existAsset.setAssetSubTypes(assetDto.getAssetSubTypes());
        existAsset.setHostingProvider(assetDto.getHostingProvider());
        existAsset.setHostingType(assetDto.getHostingType());
        existAsset.setOrgSecurityMeasures(assetDto.getOrgSecurityMeasures());
        existAsset.setTechnicalSecurityMeasures(assetDto.getTechnicalSecurityMeasures());
        existAsset.setStorageFormats(assetDto.getStorageFormats());
        existAsset.setDataDisposal(assetDto.getDataDisposal());
        existAsset.setDataRetentionPeriod(assetDto.getDataRetentionPeriod());
        existAsset.setMaxDataSubjectVolume(assetDto.getMaxDataSubjectVolume());
        existAsset.setMinDataSubjectVolume(assetDto.getMinDataSubjectVolume());
        assetMongoRepository.save(sequenceGenerator(existAsset));
        return existAsset;
    }



}
