package com.kairos.persistance.repository.master_data;

import com.kairos.persistance.model.master_data.HostingProvider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface HostingProviderMongoRepository extends MongoRepository<HostingProvider,BigInteger> {

    HostingProvider findByid(BigInteger id);
    HostingProvider findByName(String name);

}
