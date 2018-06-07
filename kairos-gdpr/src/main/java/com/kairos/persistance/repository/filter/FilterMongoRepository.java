package com.kairos.persistance.repository.filter;

import com.kairos.persistance.model.filter.FilterGroup;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface FilterMongoRepository extends MongoRepository<FilterGroup, BigInteger>, CustomeFilterMongoRepository {


    @Query("{'accessModule.moduleId':?0,countryId:?1}")
    FilterGroup findFilterGroupByModuleId(String moduleId, Long countryId);


    @Query("{'accessModule.moduleId':{$in:?0},'accessModule.active':?1}")
    List<FilterGroup> findFilterGroupByModuleIds(List<String> moduleIds, Boolean active);


}
