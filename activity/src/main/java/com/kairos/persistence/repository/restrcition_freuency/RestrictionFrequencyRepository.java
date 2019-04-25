package com.kairos.persistence.repository.restrcition_freuency;

import com.kairos.persistence.model.restrcition_freuency.RestrictionFrequency;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * Created by prabjot on 15/9/17.
 */
@Repository
public interface RestrictionFrequencyRepository extends MongoBaseRepository<RestrictionFrequency,BigInteger> {
}
