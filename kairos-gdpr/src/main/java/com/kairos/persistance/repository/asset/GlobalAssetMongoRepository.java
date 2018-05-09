package com.kairos.persistance.repository.asset;

import com.kairos.persistance.model.asset.GlobalAsset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface GlobalAssetMongoRepository extends MongoRepository<GlobalAsset,BigInteger> {


    GlobalAsset findByid(BigInteger id);

    GlobalAsset findByName(String name);


}
