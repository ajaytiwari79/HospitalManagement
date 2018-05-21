package com.kairos.persistance.repository.master_data;

import com.kairos.persistance.model.master_data.HostingLocation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface HostingLocationMongoRepository extends MongoRepository<HostingLocation,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    HostingLocation findByIdAndNonDeleted(BigInteger id);
    HostingLocation findByName(String name);

    @Query("{deleted:false}")
    List<HostingLocation> findAllHostingLocations();

}
