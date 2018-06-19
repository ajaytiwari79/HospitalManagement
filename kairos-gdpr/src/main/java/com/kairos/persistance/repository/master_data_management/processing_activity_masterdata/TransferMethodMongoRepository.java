package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.TransferMethod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface TransferMethodMongoRepository extends MongoRepository<TransferMethod,BigInteger> {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    TransferMethod findByIdAndNonDeleted(Long countryId,BigInteger id);

    TransferMethod findByid(BigInteger id);
    @Query("{countryId:?0,name:{$regex:?1,$options:'i'},deleted:false}")
    TransferMethod findByName(Long countryId,String name);

    @Query("{countryId:?0,deleted:false}")
    List<TransferMethod> findAllTransferMethods(Long countryId);


    @Query("{countryId:?0,name:{$in:?1},deleted:false}")
    List<TransferMethod>  findByCountryAndNameList(Long countryId, Set<String> name);



}
