package com.kairos.persistance.repository.master_data;

import com.kairos.persistance.model.master_data.AssetType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface AssetTypeMongoRepository extends MongoRepository<AssetType,BigInteger> {

AssetType findByid(BigInteger id);
AssetType findByName(String name);

}
