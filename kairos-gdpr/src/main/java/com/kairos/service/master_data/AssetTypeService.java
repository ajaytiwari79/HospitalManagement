package com.kairos.service.master_data;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.custome_exception.DataNotFoundByIdException;
import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.custome_exception.InvalidRequestException;
import com.kairos.persistance.model.master_data.AssetType;
import com.kairos.persistance.repository.master_data.AssetTypeMongoRepository;
import com.kairos.service.MongoBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
public class AssetTypeService extends MongoBaseService {


    @Inject
     AssetTypeMongoRepository assetTypeMongoRepository;



    public AssetType createAssetType(String assetType) {
        if (StringUtils.isEmpty(assetType))
        {
        throw new InvalidRequestException("requested AssetType is null");
        }
        AssetType exist = assetTypeMongoRepository.findByName(assetType);
        if (Optional.ofNullable(exist).isPresent()) {
            throw new DuplicateDataException("data already exist for  " + assetType);
        } else {
            AssetType newAssetType = new AssetType();
            newAssetType.setName(assetType);
            return save(newAssetType);
        }
    }


    public List<AssetType> getAllAssetType() {
        List<AssetType> result = assetTypeMongoRepository.findAll();
        if (result.size()!=0) {
            return result;

        } else
            throw new DataNotExists("AssetType not exist please create purpose ");
    }



    public AssetType getAssetTypeById(BigInteger id) {

        AssetType exist = assetTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            return exist;

        }
    }



    public Boolean deleteAssetTypeById(BigInteger id) {

        AssetType exist = assetTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            assetTypeMongoRepository.delete(exist);
            return true;

        }
    }


    public AssetType updateAssetType(BigInteger id,String assetType) {
        if (StringUtils.isEmpty(assetType))
        {
            throw new InvalidRequestException("requested AssetType is null");

        }
        AssetType exist = assetTypeMongoRepository.findByid(id);
        if (!Optional.ofNullable(exist).isPresent()) {
            throw new DataNotFoundByIdException("data not exist for id ");
        } else {
            exist.setName(assetType);
            return save(exist);

        }
    }






}
