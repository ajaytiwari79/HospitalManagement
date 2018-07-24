package com.kairos.persistance.repository.master_data.processing_activity_masterdata;


import com.kairos.persistance.model.master_data.default_proc_activity_setting.TransferMethod;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface TransferMethodMongoRepository extends MongoRepository<TransferMethod,BigInteger> {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    TransferMethod findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    TransferMethod findByid(BigInteger id);
    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    TransferMethod findByName(Long countryId,Long organizationId,String name);

    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<TransferMethod> findAllTransferMethods(Long countryId,Long organizationId);


    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<TransferMethod>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);



}
