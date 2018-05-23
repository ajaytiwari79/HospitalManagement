package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.AccessorParty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
public interface AccessorPartyMongoRepository extends MongoRepository<AccessorParty,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    AccessorParty findByIdAndNonDeleted(BigInteger id);

    @Query("{'name':?0,deleted:false}")
    AccessorParty findByName(String name);

    @Query("{'_id':{$in:?0}}")
    List<AccessorParty> accessorPartyList(List<BigInteger> ids);


    @Query("{deleted:false}")
    List<AccessorParty> findAllAccessorParties();

}
