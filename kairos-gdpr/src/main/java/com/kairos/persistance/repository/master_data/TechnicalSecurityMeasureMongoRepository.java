package com.kairos.persistance.repository.master_data;

import com.kairos.persistance.model.master_data.TechnicalSecurityMeasure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface TechnicalSecurityMeasureMongoRepository extends MongoRepository<TechnicalSecurityMeasure,BigInteger> {


    @Query("{'_id':?0,deleted:false}")
    TechnicalSecurityMeasure findByIdAndNonDeleted(BigInteger id);


    TechnicalSecurityMeasure findByName(String name);


    @Query("{deleted:false}")
    List<TechnicalSecurityMeasure> findAllTechnicalSecurityMeasures();



}
