package com.kairos.persistance.repository.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.StorageType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface StorageTypeMongoRepository extends MongoRepository<StorageType,BigInteger> {




    @Query("{'countryId':?0,_id:?1,deleted:false}")
    StorageType findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    StorageType findByName(Long countryId,String name);

    StorageType findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<StorageType> findAllStorageTypes(Long countryId);
}
