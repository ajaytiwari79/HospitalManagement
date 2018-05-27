package com.kairos.persistance.repository.filter;


import com.kairos.persistance.model.filter.FilterGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface FilterGroupMongoRepository extends MongoRepository<FilterGroup,BigInteger> {

}
