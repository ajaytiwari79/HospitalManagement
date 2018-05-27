package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.Destination;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface DestinationMongoRepository extends MongoRepository<Destination,BigInteger> {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    Destination findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    Destination findByName(Long countryId,String name);

    Destination findByid(BigInteger id);

    @Query("{_id:{$in:?0},deleted:false}")
    List<Destination> destinationList(List<BigInteger> ids);


    @Query("{countryId:?0,deleted:false}")
    List<Destination> findAllDestinations(Long countryId);


}
