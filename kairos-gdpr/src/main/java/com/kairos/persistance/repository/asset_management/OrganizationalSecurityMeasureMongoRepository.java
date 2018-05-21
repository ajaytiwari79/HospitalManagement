package com.kairos.persistance.repository.asset_management;


import com.kairos.persistance.model.asset_management.OrganizationalSecurityMeasure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface OrganizationalSecurityMeasureMongoRepository extends MongoRepository<OrganizationalSecurityMeasure,BigInteger> {


    @Query("{'_id':?0,deleted:false}")
    OrganizationalSecurityMeasure findByIdAndNonDeleted(BigInteger id);


    OrganizationalSecurityMeasure findByName(String name);


    @Query("{deleted:false}")
    List<OrganizationalSecurityMeasure> findAllHostingProviders();

}
