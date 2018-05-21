package com.kairos.persistance.repository.asset_management;

import com.kairos.persistance.model.asset_management.HostingType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface HostingTypeMongoRepository extends MongoRepository<HostingType,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    HostingType findByIdAndNonDeleted(BigInteger id);

    HostingType findByName(String name);


    @Query("{deleted:false}")
    List<HostingType> findAllHostingTypes();
}

