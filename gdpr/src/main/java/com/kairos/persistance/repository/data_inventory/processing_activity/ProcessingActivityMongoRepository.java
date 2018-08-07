package com.kairos.persistance.repository.data_inventory.processing_activity;

import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface ProcessingActivityMongoRepository extends MongoRepository<ProcessingActivity,BigInteger>,CustomProcessingActivityRepository {


    @Query("{organizationId:?0,_id:?1,deleted:false}")
    ProcessingActivity findByIdAndNonDeleted(Long organizationId,BigInteger id);

    ProcessingActivity findByid(BigInteger id);


    @Query("{organizationId:?0,_id:{$in:?1},deleted:false,subProcess:false}")
    List<ProcessingActivity> findSubProcessingActvitiesByIds(Long organizationId, List<BigInteger> ids);

}
