package com.kairos.persistance.repository.master_data;

import com.kairos.persistance.model.master_data.HostingLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface HostingLocationMongoRepository extends MongoRepository<HostingLocation,BigInteger> {

    HostingLocation findByid(BigInteger id);
    HostingLocation findByName(String name);

}
