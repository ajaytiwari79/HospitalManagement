package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.StorageFormat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface StorageFormatMongoRepository extends MongoRepository<StorageFormat,BigInteger> {



    @Query("{countryId:?0,_id:?1,deleted:false}")
    StorageFormat findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    StorageFormat findByNameAndCountryId(Long countryId,String name);

    StorageFormat findByid(BigInteger id);

    @Query("{countryId:?0,deleted:false}")
    List<StorageFormat> findAllStorageFormats(Long countryId);

    @Query("{countryId:?0,name:{$in:?1},deleted:false}")
    List<StorageFormat>  findByCountryAndNameList(Long countryId,Set<String> name);

}
