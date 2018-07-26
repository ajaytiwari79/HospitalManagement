package com.kairos.persistance.repository.data_inventory.asset;

import com.kairos.persistance.model.data_inventory.asset.Asset;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@JaversSpringDataAuditable
public interface AssetMongoRepository extends MongoRepository<Asset,BigInteger>,CustomAssetRepository {


    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    Asset findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);


    Asset findByid(BigInteger id);

}
