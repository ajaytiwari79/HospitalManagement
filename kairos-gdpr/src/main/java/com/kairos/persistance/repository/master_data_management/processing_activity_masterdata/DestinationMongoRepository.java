package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.Destination;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface DestinationMongoRepository extends MongoRepository<Destination,BigInteger> {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    Destination findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    Destination findByName(Long countryId,Long organizationId,String name);

    Destination findByid(BigInteger id);

    @Query("{_id:{$in:?0},deleted:false}")
    List<Destination> destinationList(List<BigInteger> ids);


    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<Destination> findAllDestinations(Long countryId,Long organizationId);

    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<Destination>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);


}
