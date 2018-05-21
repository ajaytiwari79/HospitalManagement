package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.Destination;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface DestinationMongoRepository extends MongoRepository<Destination,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    Destination findByIdAndNonDeleted(BigInteger id);

    @Query("{'name':?0,deleted:false}")
    Destination findByName(String name);


    @Query("{'_id':{$in:?0},deleted:false}")
    List<Destination> destinationList(List<BigInteger> ids);


    @Query("{deleted:false}")
    List<Destination> findAllDestinations();


}
