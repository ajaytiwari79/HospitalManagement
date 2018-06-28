package com.kairos.persistance.repository.processing_activity;


import com.kairos.persistance.model.processing_activity.ProcessingActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface ProcessingActivityMongoRepository extends MongoRepository<ProcessingActivity,BigInteger> {

ProcessingActivity findByid(BigInteger id);
    ProcessingActivity findByName(String name);

}
