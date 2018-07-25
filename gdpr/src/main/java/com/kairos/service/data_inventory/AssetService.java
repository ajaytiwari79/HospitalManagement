package com.kairos.service.data_inventory;

import com.kairos.dto.data_inventory.AssetDTO;
import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistance.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import jdk.nashorn.internal.runtime.options.Option;
import org.apache.commons.lang3.StringUtils;
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


    public Asset createAsseWithBasictDetail(Long countryId, Long organizationId, AssetDTO assetDto) {

        Asset exist = assetMongoRepository.findByName(countryId, organizationId, assetDto.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", " Asset ", assetDto.getName());
        }
        Asset asset = new Asset(assetDto.getName(), assetDto.getDescription(),assetDto.getHostingLocation(), countryId,
                           assetDto.getAssetType(), assetDto.getAssetSubTypes(), assetDto.getManagingDepartment(), assetDto.getAssetOwner());
        asset.setOrganizationId(organizationId);
        asset.setActive(true);
        asset.setHostingProvider(assetDto.getHostingProvider());
        asset.setHostingType(assetDto.getHostingType());
        asset.setOrgSecurityMeasures(assetDto.getOrgSecurityMeasures());
        asset.setTechnicalSecurityMeasures(assetDto.getTechnicalSecurityMeasures());
        asset.setStorageFormats(assetDto.getStorageFormats());
        asset.setDataDisposal(assetDto.getDataDisposal());
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
        existAsset.setAssetType(assetDto.getAssetType());
        existAsset.setAssetSubTypes(assetDto.getAssetSubTypes());
        existAsset.setHostingProvider(assetDto.getHostingProvider());
        existAsset.setHostingType(assetDto.getHostingType());
        existAsset.setOrgSecurityMeasures(assetDto.getOrgSecurityMeasures());
        existAsset.setTechnicalSecurityMeasures(assetDto.getTechnicalSecurityMeasures());
        existAsset.setStorageFormats(assetDto.getStorageFormats());
        existAsset.setDataDisposal(assetDto.getDataDisposal());
        assetMongoRepository.save(sequenceGenerator(existAsset));
        return existAsset;
    }


    public List<CdoSnapshot> getAssetActivities(Long countryId, Long organizationId, BigInteger assetId) {

        QueryBuilder jqlQuery1 = QueryBuilder.byInstanceId(assetId, Asset.class);

        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(BigInteger.valueOf(8), MasterAsset.class);
        List<CdoSnapshot> changes = javers.findSnapshots(jqlQuery.build());
        changes.sort((o1, o2) -> -1 * (int) o1.getVersion() - (int) o2.getVersion());


        return changes;
    }

}
