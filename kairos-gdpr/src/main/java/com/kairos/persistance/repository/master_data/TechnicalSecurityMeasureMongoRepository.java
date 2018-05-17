package com.kairos.persistance.repository.master_data;

import com.kairos.persistance.model.master_data.TechnicalSecurityMeasure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface TechnicalSecurityMeasureMongoRepository extends MongoRepository<TechnicalSecurityMeasure,BigInteger> {

    TechnicalSecurityMeasure findByid(BigInteger id);
    TechnicalSecurityMeasure findByName(String name);

}
