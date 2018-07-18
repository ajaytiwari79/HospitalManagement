package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.persistance.model.master_data.default_asset_setting.HostingProvider;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface HostingProviderMongoRepository extends MongoRepository<HostingProvider,BigInteger> {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    HostingProvider findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,name:?2}")
    HostingProvider findByName(Long countryId,Long organizationId,String name);

    HostingProvider findByid(BigInteger id);

    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<HostingProvider> findAllHostingProviders(Long countryId,Long organizationId);


    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<HostingProvider>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);
}
