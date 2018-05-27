package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.HostingType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface HostingTypeMongoRepository extends MongoRepository<HostingType,BigInteger> {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    HostingType findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    HostingType findByName(Long countryId,String name);

    HostingType findByid(BigInteger id);

    @Query("{countryId:?0,deleted:false}")
    List<HostingType> findAllHostingTypes(Long countryId);
}

