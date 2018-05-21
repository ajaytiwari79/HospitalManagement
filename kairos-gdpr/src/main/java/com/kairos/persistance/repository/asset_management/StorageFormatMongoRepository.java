package com.kairos.persistance.repository.asset_management;

import com.kairos.persistance.model.asset_management.StorageFormat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface StorageFormatMongoRepository extends MongoRepository<StorageFormat,BigInteger> {



    @Query("{'_id':?0,deleted:false}")
    StorageFormat findByIdAndNonDeleted(BigInteger id);


    StorageFormat findByName(String name);


    @Query("{deleted:false}")
    List<StorageFormat> findAllStorageFormats();



}
