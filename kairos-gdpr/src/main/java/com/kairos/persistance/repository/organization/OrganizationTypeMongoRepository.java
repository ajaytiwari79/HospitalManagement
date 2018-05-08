package com.kairos.persistance.repository.organization;

import com.kairos.persistance.model.organization.OrganizationService;
import com.kairos.persistance.model.organization.OrganizationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.math.BigInteger;


@Repository
public interface OrganizationTypeMongoRepository extends MongoRepository<OrganizationType ,BigInteger> {
    @Query("{'_id':?0}")
    OrganizationType findById(String id);
}
