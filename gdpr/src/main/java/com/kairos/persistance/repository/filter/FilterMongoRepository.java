package com.kairos.persistance.repository.filter;

import com.kairos.persistance.model.filter.FilterGroup;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
@JaversSpringDataAuditable
public interface FilterMongoRepository extends MongoBaseRepository<FilterGroup, BigInteger>, CustomFilterMongoRepository {


    @Query("{'accessModule.moduleId':?0,countryId:?1}")
    FilterGroup findFilterGroupByModuleId(String moduleId, Long countryId);


    @Query("{'accessModule.moduleId':{$in:?0},'accessModule.active':?1}")
    List<FilterGroup> findFilterGroupByModuleIds(List<String> moduleIds, Boolean active);


}
