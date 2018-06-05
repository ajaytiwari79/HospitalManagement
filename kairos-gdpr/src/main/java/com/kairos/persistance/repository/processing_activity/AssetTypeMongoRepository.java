package com.kairos.persistance.repository.processing_activity;

import com.kairos.persistance.model.processing_activity.AssetType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface AssetTypeMongoRepository extends MongoRepository<AssetType,BigInteger> {
    @Query("{'_id':?0,deleted:false}")
AssetType findByIdAndNonDeleted(BigInteger id);


AssetType findByName(String name);

    @Query("{deleted:false}")
    List<AssetType> findAllAssetTypes();

}
