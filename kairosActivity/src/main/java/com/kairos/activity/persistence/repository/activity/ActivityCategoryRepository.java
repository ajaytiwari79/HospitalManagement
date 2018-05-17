package com.kairos.activity.persistence.repository.activity;


import com.kairos.activity.persistence.model.activity.tabs.ActivityCategory;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by pawanmandhan on 22/8/17.
 */
@Repository

public interface ActivityCategoryRepository extends MongoRepository<ActivityCategory, BigInteger> {

    @Query(value = "{ 'name' :{$regex:?0,$options:'i'} , countryId: ?1, deleted:?2} ")
    ActivityCategory getCategoryByNameAndCountryAndDeleted(String categoryName, long countryId,boolean status);

    @Query("{'deleted': false, 'countryId' : ?0}")
    List<ActivityCategory> findByCountryId(long countryId);

    @Query("{'deleted': false, 'name' :{$regex:?0,$options:'i'}, 'unitId' : ?1}")
    ActivityCategory getCategoryByNameAndUnit(String categoryName, Long unitId);

    @Query(value = "{ 'name' :{$regex:?0,$options:'i'} ,'deleted':false} ")
    ActivityCategory getCategoryByName(String categoryName);

    List<ActivityCategory> findByDeletedFalse();

    boolean existsByNameIgnoreCaseAndDeleted(String name, boolean status );
}
