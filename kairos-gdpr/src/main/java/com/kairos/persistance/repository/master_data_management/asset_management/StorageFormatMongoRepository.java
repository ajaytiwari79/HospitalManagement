package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.StorageFormat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface StorageFormatMongoRepository extends MongoRepository<StorageFormat,BigInteger> {



    @Query("{countryId:?0,_id:?1,deleted:false}")
    StorageFormat findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    StorageFormat findByName(Long countryId,String name);

    StorageFormat findByid(BigInteger id);

    @Query("{countryId:?0,deleted:false}")
    List<StorageFormat> findAllStorageFormats(Long countryId);



}
