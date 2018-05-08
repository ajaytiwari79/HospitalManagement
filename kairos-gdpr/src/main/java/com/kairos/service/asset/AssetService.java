package com.kairos.service.asset;


import com.kairos.ExceptionHandler.DataNotFoundByIdException;
import com.kairos.ExceptionHandler.NotExists;
import com.kairos.persistance.model.asset.Asset;
import com.kairos.persistance.repository.asset.AssetMongoRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Service
public class AssetService {


    @Inject
    private AssetMongoRepository assetMongoRepository;


    public Asset addAsset(Asset asset) {
        Asset newAsset = new Asset();
        newAsset.setDescription(asset.getDescription());
        newAsset.setName(asset.getName());
        return assetMongoRepository.save(asset);


    }


    public List<Asset> getAllAsset() {
        List<Asset> assets = assetMongoRepository.findAll();
        if (assets.size() <= 0) {
            throw new NotExists("no Assets found create assets");
        } else
            return assets;

    }


    public Asset updateAsset(Long id, Asset asset) {
        Asset exists = assetMongoRepository.findById(id.toString());
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else {
            exists.setName(asset.getName());
            exists.setDescription(asset.getDescription());
        }
        return assetMongoRepository.save(exists);
    }

    public Asset getAssetById(Long id) {
        Asset exists = assetMongoRepository.findById(id.toString());
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
            return assetMongoRepository.save(exists);

    }



    public Boolean deleteAssetById(Long id) {
        Asset exists = assetMongoRepository.findById(id.toString());
        if (!Optional.of(exists).isPresent()) {
            throw new DataNotFoundByIdException("asset not Exist for id " + id);

        } else
             assetMongoRepository.delete(exists);
        return true;

    }



}
