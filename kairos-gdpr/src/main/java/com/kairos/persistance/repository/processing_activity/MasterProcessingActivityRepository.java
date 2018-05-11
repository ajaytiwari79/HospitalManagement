package com.kairos.persistance.repository.processing_activity;

import com.kairos.persistance.model.asset.GlobalAsset;
import com.kairos.persistance.model.processing_activity.MasterProcessingActivity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigInteger;

public interface MasterProcessingActivityRepository extends MongoRepository<MasterProcessingActivity,BigInteger> {


    MasterProcessingActivity findByid(BigInteger id);
    MasterProcessingActivity findByName(String name);


}
