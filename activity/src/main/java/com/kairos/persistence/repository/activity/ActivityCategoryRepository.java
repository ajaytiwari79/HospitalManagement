package com.kairos.persistence.repository.activity;


import com.kairos.persistence.model.activity.tabs.ActivityCategory;

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

    @Query(value = "{ 'name' :{$regex:?0,$options:'i'} ,'deleted':false} ")
    ActivityCategory getCategoryByName(String categoryName);

    @Query(value = "{ 'countryId':?0, 'timeTypeId':?1, 'deleted':false }")
    ActivityCategory getCategoryByTimeType(Long countryId, BigInteger timeTypeId);

    @Query(value = "{ '_id':?0, 'deleted':false}")
    ActivityCategory getByIdAndNonDeleted(BigInteger categoryId);

    @Query(value = "{ 'name':?0, 'deleted':?1 }")
    boolean existsByNameIgnoreCaseAndDeleted(String name, boolean status );

    @Query("{'deleted': false,  _id : {'$in': ?0} }")
    List<ActivityCategory> findAllByIdsIn(List<BigInteger> activityCategoriesIds);
}
