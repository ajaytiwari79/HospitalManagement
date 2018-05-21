package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.MasterProcessingActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface MasterProcessingActivityRepository extends MongoRepository<MasterProcessingActivity,BigInteger> {


    MasterProcessingActivity findByid(BigInteger id);

    @Query("{'name':?0,deleted:false}")
    MasterProcessingActivity findByName(String name);


}
