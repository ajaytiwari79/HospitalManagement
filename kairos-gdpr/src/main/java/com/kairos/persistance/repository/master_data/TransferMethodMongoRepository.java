package com.kairos.persistance.repository.master_data;


import com.kairos.persistance.model.master_data.TransferMethod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface TransferMethodMongoRepository extends MongoRepository<TransferMethod,BigInteger> {


TransferMethod findByid(BigInteger id);

TransferMethod findByName(String name);

}
