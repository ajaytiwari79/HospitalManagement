package com.kairos.persistance.repository.master_data;

import com.kairos.persistance.model.master_data.OrganizationalSecurityMeasure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface OrganizationalSecurityMeasureMongoRepository extends MongoRepository<OrganizationalSecurityMeasure,BigInteger> {

    OrganizationalSecurityMeasure findByid(BigInteger id);
    OrganizationalSecurityMeasure findByName(String name);

}
