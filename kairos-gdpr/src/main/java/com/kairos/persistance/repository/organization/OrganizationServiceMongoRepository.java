package com.kairos.persistance.repository.organization;

import com.kairos.persistance.model.organization.OrganizationService;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.math.BigInteger;

@Repository
public interface OrganizationServiceMongoRepository extends MongoRepository<OrganizationService, BigInteger> {

    @Query("{'_id':?0}")
    OrganizationService findById(String id);

    OrganizationService findByName(String name);


}
