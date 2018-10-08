package com.kairos.persistence.repository.activity;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.CountQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/**
 * Created by pawanmandhan on 17/8/17.
 */
@Repository
public interface ActivityMongoRepository extends MongoBaseRepository<Activity, BigInteger>,
        CustomActivityMongoRepository {


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

    List<Activity> findByUnitIdAndExternalIdInAndDeletedFalse(Long unitId, List<String> activityExternalIds);

    @Query("{'deleted' : false, 'generalActivityTab.categoryId' :?0}")
    List<Activity> findActivitiesByCategoryId(BigInteger activityCategoryId);


    @Query("{_id:{$in:?0}, deleted:false}")
    List<Activity> findAllActivitiesByIds(Set<BigInteger> activityIds);


    List<Activity> findAllByUnitIdAndDeletedFalse(Long unitId);

}
