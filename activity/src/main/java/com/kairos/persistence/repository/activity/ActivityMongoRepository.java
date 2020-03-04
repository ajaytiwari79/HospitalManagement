package com.kairos.persistence.repository.activity;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by pawanmandhan on 17/8/17.
 */
@Repository
public interface ActivityMongoRepository extends MongoBaseRepository<Activity, BigInteger>,CustomActivityMongoRepository {

    @Query("{'deleted' : false,'_id':?0}")
    Activity findActivityByIdAndEnabled(BigInteger id);

    Activity findByIdAndUnitIdAndDeleted(BigInteger id,Long unitId,boolean deleted);

    Activity findByParentIdAndDeletedFalseAndUnitId(BigInteger parentId, Long unitId);

    @Query(value = "{'deleted' : false, 'countryId' :?0 }", fields = "{'name':1,'description':1,'parentId':1,'_id':1,'unitId':1,'timeCalculationActivityTab.methodForCalculatingTime':1,'rulesActivityTab':1,'balanceSettingsActivityTab':1}")
    List<ActivityDTO> findByDeletedFalseAndCountryId(Long countryId);

    @Query(value = "{'deleted' : false, 'unitId' :?0 }", fields = "{'name':1,'description':1,'parentId':1,'_id':1,'unitId':1,'timeCalculationActivityTab.methodForCalculatingTime':1,'rulesActivityTab':1, 'balanceSettingsActivityTab':1,'countryParentId':1}")
    List<ActivityDTO> findByDeletedFalseAndUnitId(Long unitId);

    @Query(value = "{'deleted' : false, 'unitId' :{$in:?0} }", fields = "{'name':1,'_id':1,'unitId':1}")
    List<ActivityDTO> findAllActivityByDeletedFalseAndUnitId(List<Long> unitId);

    @Query(value = "{'deleted' : false, 'unitId' :?0 }", fields = "{'name':1,'description':1,'parentId':1,'_id':1,'compositeActivities':1,'unitId':1,'timeCalculationActivityTab.methodForCalculatingTime':1}")
    List<Activity> findAllActivitiesByUnitId(Long unitId);

    List<Activity> findByExternalIdIn(List<String> activityExternalIds);

    @Query(value = "{'deleted' : false, 'unitId' :{$in:?0},'countryParentId':{$in:?1}}", fields = "{'countryParentId':1,'_id':1,'unitId':1}")
    List<Activity> findAllActivitiesByUnitIds(List<Long> unitIds,Set<BigInteger> activityIds);

    List<Activity> findByUnitIdAndExternalIdInAndDeletedFalse(Long unitId, List<String> activityExternalIds);

    @Query("{'deleted' : false, 'generalActivityTab.categoryId' :?0}")
    List<Activity> findActivitiesByCategoryId(BigInteger activityCategoryId);

    @Query("{_id:{$in:?0}, deleted:false}")
    List<Activity> findAllActivitiesByIds(Collection<BigInteger> activityIds);

    @Query(value = "{childActivityIds:?0, deleted:false}")
    Activity findByChildActivityId(BigInteger childActivityId);

    @Query(value = "{childActivityIds:{$in:?0}, deleted:false}",fields ="{'_id':1,'childActivityIds':1}")
    List<Activity> findByChildActivityIds(Collection<BigInteger> childActivityIds);

    @Query(value = "{_id:{$in:?0}, deleted:false}",fields = "{'_id':1, 'phaseSettingsActivityTab':1 ,'rulesActivityTab':1,'name':1,'balanceSettingsActivityTab':1,'timeCalculationActivityTab':1}")
    List<Activity> findAllPhaseSettingsByActivityIds(Collection<BigInteger> activityIds);

    List<Activity> findAllByUnitIdAndDeletedFalse(Long unitId);

    @Query(value = "{deleted:false,'compositeActivities.activityId':?0}",exists = true)
    boolean existsByActivityIdInCompositeActivitiesAndDeletedFalse(BigInteger id);

    @Query(value = "{'balanceSettingsActivityTab.timeTypeId':?0}, deleted:false}")
    List<Activity>  findAllByTimeTypeId(BigInteger timeTypeId);

    @Query(value = "{'_id':{'$in':?0}, 'deleted':false}")
    List<ActivityDTO> findByDeletedFalseAndIdsIn(Collection<BigInteger> activityIds);

    @Query(value = "{'balanceSettingsActivityTab.timeTypeId':?0}, deleted:false}",exists = true)
    boolean existsByTimeTypeId(BigInteger timeTypeId);

    @Query(value = "{'deleted' : false, 'unitId' :?0,'activityPriorityId':?1 }",exists = true)
    boolean existsActivitiesByActivityPriorityIdAndUnitId(Long unitId,BigInteger activityPriorityId);

    @Query(value = "{'deleted' : false, 'countryId' :?0,'activityPriorityId':?1 }",exists = true)
    boolean existsActivitiesByActivityPriorityIdAndCountryId(Long countryId,BigInteger activityPriorityId);

    @Query(value = "{deleted: false, parentId :?0,unitId:{$in:?1 }}",exists = true)
    boolean existsByParentIdAndDeletedFalse( BigInteger activityId,List<Long> unitIds);

    @Query(value = "{'balanceSettingsActivityTab.timeType':?0}, deleted:false}")
    List<Activity>  findAllBySecondLevelTimeType(TimeTypeEnum timeTypeEnum);


    @Query(value = "{'balanceSettingsActivityTab.timeType':?0,unitId:{$in:?1 }, deleted:false}")
    List<Activity> findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum timeTypeEnum, Set<Long> unitIds);

    @Query(value = "{'generalActivityTab.tags':?0}")
    List<Activity> findActivitiesByTagId(BigInteger tagId);

    @Query(value = "{unitId:?0, 'timeCalculationActivityTab.methodForCalculatingTime':{$in:?1 }, 'rulesActivityTab.approvalAllowedPhaseIds':?2, deleted:false}")
    List<Activity> findAllAbsenceActivities(Long unitId, Set<String> methodForCalculatingTimes, BigInteger phaseId);

    @Query(value = "{'deleted' : false,'unitId' :?0,'rulesActivityTab.sicknessSettingValid':true}",fields ="{'_id':1,'rulesActivityTab':1}")
    List<Activity> findAllSicknessActivity(Long unitId);

    @Query(value = "{unitId:?0, deleted:false}")
    List<ActivityDTO> getActivitiesByUnitId(Long unitId);
}
