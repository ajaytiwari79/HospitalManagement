package com.kairos.persistance.repository.asset;

import com.kairos.persistance.model.asset.Asset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AssetMongoRepository extends MongoRepository<Asset,BigInteger> {

    @Query("{'_id':?0}")
    Asset findById(String id);

    @Query("{'asset.name':?0}")
    Asset findByName(String name);

}
