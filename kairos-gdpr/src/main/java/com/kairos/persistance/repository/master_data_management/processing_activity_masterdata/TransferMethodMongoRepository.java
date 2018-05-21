package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.TransferMethod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface TransferMethodMongoRepository extends MongoRepository<TransferMethod,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    TransferMethod findByIdAndNonDeleted(BigInteger id);

    @Query("{'name':?0,deleted:false}")
    TransferMethod findByName(String name);

    @Query("{deleted:false}")
    List<TransferMethod> findAllTransferMethods();
}
