package com.kairos.persistance.repository.data_inventory.processing_activity;


import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@JaversSpringDataAuditable
public interface ProcessingActivityMongoRepository extends MongoRepository<ProcessingActivity,BigInteger> ,CustomProcessingActivityRepository {


    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    ProcessingActivity findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    ProcessingActivity findByid(BigInteger id);

}
