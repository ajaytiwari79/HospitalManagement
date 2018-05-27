package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.TransferMethod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface TransferMethodMongoRepository extends MongoRepository<TransferMethod,BigInteger> {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    TransferMethod findByIdAndNonDeleted(Long countryId,BigInteger id);

    TransferMethod findByid(BigInteger id);
    @Query("{countryId:?0,name:?1,deleted:false}")
    TransferMethod findByName(Long countryId,String name);

    @Query("{countryId:?0,deleted:false}")
    List<TransferMethod> findAllTransferMethods(Long countryId);
}
