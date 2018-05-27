package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface MasterAssetMongoRepository extends MongoRepository<MasterAsset,BigInteger> {


    @Query("{countryId:?0,_id:?1,deleted:false}")
    MasterAsset findByIdANdNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<MasterAsset> findAllMasterAssets( Long countryId);

    @Query("{countryId:?0,name:?1,deleted:false}")
    MasterAsset findByNameAndCountry(Long countryId,String name);

    MasterAsset findByid(BigInteger id);


}
