package com.kairos.persistance.repository.master_data;


import com.kairos.persistance.model.master_data.TransferMethod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface TransferMethodMongoRepository extends MongoRepository<TransferMethod,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    TransferMethod findByIdAndNonDeleted(BigInteger id);

TransferMethod findByName(String name);

    @Query("{deleted:false}")
    List<TransferMethod> findAllTransferMethods();
}
