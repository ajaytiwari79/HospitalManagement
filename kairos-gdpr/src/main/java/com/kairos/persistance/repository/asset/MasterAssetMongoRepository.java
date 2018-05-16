package com.kairos.persistance.repository.asset;

import com.kairos.persistance.model.asset.MasterAsset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface MasterAssetMongoRepository extends MongoRepository<MasterAsset,BigInteger> {


    MasterAsset findByid(BigInteger id);

    MasterAsset findByName(String name);


}
