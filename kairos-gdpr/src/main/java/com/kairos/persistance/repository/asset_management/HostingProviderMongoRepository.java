package com.kairos.persistance.repository.asset_management;

import com.kairos.persistance.model.asset_management.HostingProvider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface HostingProviderMongoRepository extends MongoRepository<HostingProvider,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    HostingProvider findByIdAndNonDeleted(BigInteger id);

    HostingProvider findByName(String name);


    @Query("{deleted:false}")
    List<HostingProvider> findAllHostingProviders();
}
