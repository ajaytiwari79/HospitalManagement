package com.kairos.persistance.repository.filter;

import com.kairos.persistance.model.filter.FilterGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;

public interface FilterGroupMongoRepository extends MongoRepository<FilterGroup,BigInteger> {


    @Query("{'accessModule.moduleId':?0}")
    FilterGroup findFilterGroupByModuleId(String moduleId,Boolean active);


}
