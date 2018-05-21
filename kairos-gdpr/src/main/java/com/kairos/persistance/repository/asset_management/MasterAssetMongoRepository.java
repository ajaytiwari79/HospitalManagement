package com.kairos.persistance.repository.asset_management;

import com.kairos.persistance.model.asset_management.MasterAsset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MasterAssetMongoRepository extends MongoRepository<MasterAsset,BigInteger> {


    @Query("{'_id':?0,deleted:false}")
    MasterAsset findByIdANdNonDeleted(BigInteger id);

    @Query("{deleted:false}")
    List<MasterAsset> findAllMasterAssets( );

    MasterAsset findByName(String name);


}
