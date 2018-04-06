package com.kairos.activity.persistence.repository.restrcition_freuency;

import com.kairos.activity.persistence.model.restrcition_freuency.RestrictionFrequency;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * Created by prabjot on 15/9/17.
 */
@Repository
public interface RestrictionFrequencyRepository extends MongoBaseRepository<RestrictionFrequency,BigInteger> {
}
