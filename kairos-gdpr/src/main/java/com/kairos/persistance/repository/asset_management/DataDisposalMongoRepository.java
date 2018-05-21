package com.kairos.persistance.repository.asset_management;


import com.kairos.persistance.model.asset_management.DataDisposal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface DataDisposalMongoRepository extends MongoRepository<DataDisposal,BigInteger> {


    @Query("{'_id':?0,deleted:false}")
    DataDisposal findByIdAndNonDeleted(BigInteger id);

    DataDisposal findByName(String name);


    @Query("{deleted:false}")
    List<DataDisposal> findAllDataDisposals();
}
