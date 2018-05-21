package com.kairos.persistance.repository.asset_management;


import com.kairos.persistance.model.asset_management.StorageType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface StorageTypeMongoRepository extends MongoRepository<StorageType,BigInteger> {




    @Query("{'_id':?0,deleted:false}")
    StorageType findByIdAndNonDeleted(BigInteger id);

    StorageType findByName(String name);


    @Query("{deleted:false}")
    List<StorageType> findAllStorageTypes();
}
