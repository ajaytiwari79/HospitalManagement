package com.kairos.service.data_inventory;

import com.kairos.dto.data_inventory.AssetDto;
import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.util.Optional;


@Service
public class AssetService extends MongoBaseService {


    @Inject
    private AssetMongoRepository assetMongoRepository;


    @Inject
    private ExceptionService exceptionService;


    public Asset createBasicAsset(Long countryId, Long organizationId, AssetDto assetDto) {

        Asset exist = assetMongoRepository.findByName(countryId, organizationId, assetDto.getName());
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", " Asset ", assetDto.getName());
        }

        Asset asset = new Asset(assetDto.getName(), assetDto.getDescription(), countryId, assetDto.getAssetType(), assetDto.getAssetSubTypes());
        asset.setOrganizationId(organizationId);
        assetMongoRepository.save(sequenceGenerator(asset));
        return asset;
    }


}
