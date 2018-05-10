package com.kairos.activity.persistence.repository.activity;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.activity.response.dto.ActivityDTO;
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


    Activity findByNameIgnoreCaseAndDeletedFalseAndCountryId(String name, Long countryId);
    Activity findByNameIgnoreCaseAndDeletedFalseAndUnitId(String name, Long unitId);

    @Query("{'deleted' : false, 'unitId':?1, 'name':{$regex:?0, $options:'i'}}")
    Activity findByNameAndOrgIDIgnoreCaseAndDeletedFalse(String name, int orgID);


    @Query(value = "{'deleted' : false,'unitId':?2,'isParentActivity':false, 'name': {$regex : ?0, $options: 'i'},'_id':{'$ne':?1} }")
    Activity findByNameExcludingCurrentInUnit(String name, BigInteger activityId, Long unitId);

    @Query(value = "{'deleted' : false,'countryId':?2,'isParentActivity':true, 'name': {$regex : ?0, $options: 'i'},'_id':{'$ne':?1} }")
    Activity findByNameExcludingCurrentInCountry(String name, BigInteger activityId, Long countryId);

    @CountQuery("{_id:{$in:?0}, deleted:false}")
    Integer findAllActivityByIds(Set<BigInteger> activityIds);

    @Query("{'deleted' : false,'_id':?0}")
    Activity findActivityByIdAndEnabled(BigInteger id);

    Activity findByParentIdAndDeletedTrueAndUnitId(BigInteger parentId, Long unitId);

    Activity findByParentIdAndUnitId(BigInteger parentId, Long unitId);

    Activity findByParentIdAndDeletedFalseAndUnitId(BigInteger parentId, Long unitId);

    @Query(value = "{'deleted' : false, 'countryId' :?0 }", fields = "{'name':1,'description':1,'parentId':1,'_id':1,'compositeActivities':1,'unitId':1,'timeCalculationActivityTab.methodForCalculatingTime':1}")
    List<Activity> findByDeletedFalseAndUnitId(Long countryId);

    Integer countByParentIdAndDeletedFalse(BigInteger parentId);

    Integer countByparentIdAndDeletedFalseAndIsParentActivityFalse(BigInteger parentId);

    List<Activity> findByExternalIdIn(List<String> activityExternalIds);

    List<Activity> findByUnitIdAndExternalIdIn(Long unitId, List<String> activityExternalIds);

    @Query("{'deleted' : false, 'generalActivityTab.categoryId' :?0}")
    List<Activity> findActivitiesByCategoryId(BigInteger activityCategoryId);


    @Query(value="{unitPositionId:?0,deleted:false,isMainShift:true,startDate:{$gte:?1,$lte:?2}}",fields="{ 'startDate' : 1, 'endDate' : 1,'unitPositionId':1}")
    List<ActivityDTO> findAllActivitiesWithBalanceSettings(long unitId);

    ;
}
