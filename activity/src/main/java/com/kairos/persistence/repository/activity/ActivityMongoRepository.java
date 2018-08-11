package com.kairos.persistence.repository.activity;

import com.kairos.activity.activity.ActivityDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Created by pawanmandhan on 17/8/17.
 */
@Repository
public interface ActivityMongoRepository extends MongoBaseRepository<Activity, BigInteger>,
        CustomActivityMongoRepository {

    @CountQuery("{_id:{$in:?0}, deleted:false}")
    Integer countActivityByIds(Set<BigInteger> activityIds);

    @Query("{'deleted' : false,'_id':?0}")
    Activity findActivityByIdAndEnabled(BigInteger id);

    Activity findByParentIdAndDeletedFalseAndUnitId(BigInteger parentId, Long unitId);

    @Query(value = "{'deleted' : false, 'countryId' :?0 }", fields = "{'name':1,'description':1,'parentId':1,'_id':1,'unitId':1,'timeCalculationActivityTab.methodForCalculatingTime':1}")
    List<ActivityDTO> findByDeletedFalseAndCountryId(Long countryId);

    @Query(value = "{'deleted' : false, 'unitId' :?0 }", fields = "{'name':1,'description':1,'parentId':1,'_id':1,'unitId':1,'timeCalculationActivityTab.methodForCalculatingTime':1}")
    List<ActivityDTO> findByDeletedFalseAndUnitId(Long unitId);

    @Query(value = "{'deleted' : false, 'unitId' :?0 }", fields = "{'name':1,'description':1,'parentId':1,'_id':1,'compositeActivities':1,'unitId':1,'timeCalculationActivityTab.methodForCalculatingTime':1}")
    List<Activity> findAllActivitiesByUnitId(Long unitId);

    List<Activity> findByExternalIdIn(List<String> activityExternalIds);

    List<Activity> findByUnitIdAndExternalIdIn(Long unitId, List<String> activityExternalIds);

    @Query("{'deleted' : false, 'generalActivityTab.categoryId' :?0}")
    List<Activity> findActivitiesByCategoryId(BigInteger activityCategoryId);

    Activity findByNameIgnoreCaseAndUnitIdAndDeletedFalse(String unpaidBreak, Long unitId);

    List<Activity> findAllByUnitIdAndNameInIgnoreCaseAndDeletedFalse( Long unitId,String... unpaidBreak);

    @Query("{_id:{$in:?0}, deleted:false}")
    List<Activity> findAllActivitiesByIds(Set<BigInteger> activityIds);

//    @Query("{_id:{$in:?0}, deleted:false, 'generalActivityTab.startDate':{ $lt: ?1 } , $cond: { if: { $ne:{'generalActivityTab.endDate':null,?1:null }, then: {'generalActivityTab.endDate':{ $lt: ?1 }}, elseif: { $ne:{'generalActivityTab.endDate':null}}, then:{?1:null} } }}")
//    List<ActivityDTO> getAllInvalidActivity(Set<BigInteger> activityIds, LocalDate startDate,LocalDate endDate);

    @Query("{_id:{$in:?0}, deleted:false, 'generalActivityTab.startDate':{ $lt: ?1 } }")
    List<ActivityDTO> getInvalidActivitiesByStartDate(Set<BigInteger> activityIds, LocalDate startDate);

    @Query("{_id:{$in:?0}, deleted:false  ,$or : [{'generalActivityTab.startDate':{ $lt: ?1 }}, {'generalActivityTab.endDate':null},{'generalActivityTab.endDate': {$gt:?2}}]}")
    List<ActivityDTO> getInvalidActivitiesBetweenDateRange(Set<BigInteger> activityIds, LocalDate startDate,LocalDate endDate);

}
