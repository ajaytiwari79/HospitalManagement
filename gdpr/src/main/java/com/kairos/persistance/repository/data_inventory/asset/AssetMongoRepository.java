package com.kairos.persistance.repository.data_inventory.asset;

import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@JaversSpringDataAuditable
public interface AssetMongoRepository extends MongoBaseRepository<Asset,BigInteger>,CustomAssetRepository {


    @Query("{organizationId:?0,_id:?1,deleted:false}")
    Asset findByIdAndNonDeleted(Long organizationId,BigInteger id);


    Asset findByid(BigInteger id);

}
