package com.kairos.persistance.repository.master_data;

import com.kairos.persistance.model.master_data.StorageFormat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface StorageFormatMongoRepository extends MongoRepository<StorageFormat,BigInteger> {

    StorageFormat findByid(BigInteger id);
    StorageFormat findByName(String name);

}
