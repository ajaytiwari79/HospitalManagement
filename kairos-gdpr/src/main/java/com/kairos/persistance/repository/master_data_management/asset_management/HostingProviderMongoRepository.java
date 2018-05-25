package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.HostingProvider;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface HostingProviderMongoRepository extends MongoRepository<HostingProvider,BigInteger> {

    @Query("{countryId:?0,'_id':?1,deleted:false}")
    HostingProvider findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0,name:?1}")
    HostingProvider findByName(Long countryId,String name);

    HostingProvider findByid(BigInteger id);

    @Query("{countryId:?0,deleted:false}")
    List<HostingProvider> findAllHostingProviders(Long countryId);
}
