package com.kairos.persistence.repository.activity;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.enums.PriorityFor;
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

    @Query(value = "{'deleted' : false, 'countryId' :?0 }", fields = "{'name':1,'description':1,'parentId':1,'_id':1,'unitId':1,'activityTimeCalculationSettings.methodForCalculatingTime':1,'activityRulesSettings':1,'activityBalanceSettings':1}")
    List<ActivityDTO> findByDeletedFalseAndCountryId(Long countryId);

    @Query(value = "{'deleted' : false, 'unitId' :?0 }", fields = "{'name':1,'description':1,'parentId':1,'_id':1,'unitId':1,'activityTimeCalculationSettings.methodForCalculatingTime':1,'activityRulesSettings':1, 'activityBalanceSettings':1,'countryParentId':1}")
    List<ActivityDTO> findByDeletedFalseAndUnitId(Long unitId);

    @Query(value = "{'deleted' : false, 'unitId' :{$in:?0} }", fields = "{'name':1,'_id':1,'unitId':1}")
    List<ActivityDTO> findAllActivityByDeletedFalseAndUnitId(List<Long> unitId);

    List<Activity> findByExternalIdIn(List<String> activityExternalIds);

    @Query(value = "{'deleted' : false, 'unitId' :{$in:?0},'countryParentId':{$in:?1}}", fields = "{'countryParentId':1,'_id':1,'unitId':1}")
    List<Activity> findAllActivitiesByUnitIds(List<Long> unitIds,Set<BigInteger> activityIds);

    List<Activity> findByUnitIdAndExternalIdInAndDeletedFalse(Long unitId, List<String> activityExternalIds);

    @Query("{_id:{$in:?0}, deleted:false}")
    List<Activity> findAllActivitiesByIds(Collection<BigInteger> activityIds);

    @Query(value = "{childActivityIds:?0, deleted:false}")
    Activity findByChildActivityId(BigInteger childActivityId);

    @Query(value = "{childActivityIds:{$in:?0}, deleted:false}",fields ="{'_id':1,'childActivityIds':1}")
    List<Activity> findByChildActivityIds(Collection<BigInteger> childActivityIds);

    @Query(value = "{_id:{$in:?0}, deleted:false}",fields = "{'_id':1, 'activityPhaseSettings':1 ,'activityRulesSettings':1,'name':1,'activityBalanceSettings':1,'activityTimeCalculationSettings':1}")
    List<Activity> findAllPhaseSettingsByActivityIds(Collection<BigInteger> activityIds);

    List<Activity> findAllByUnitIdAndDeletedFalse(Long unitId);

    @Query(value = "{deleted:false,'compositeActivities.activityId':?0}",exists = true)
    boolean existsByActivityIdInCompositeActivitiesAndDeletedFalse(BigInteger id);

    @Query(value = "{'activityBalanceSettings.timeTypeId':?0, deleted:false}")
    List<Activity>  findAllByTimeTypeId(BigInteger timeTypeId);

    @Query(value = "{'_id':{'$in':?0}, 'deleted':false}")
    List<ActivityDTO> findByDeletedFalseAndIdsIn(Collection<BigInteger> activityIds);

    @Query(value = "{'activityBalanceSettings.timeTypeId':?0, deleted:false}",exists = true)
    boolean existsByTimeTypeId(BigInteger timeTypeId);

    @Query(value = "{'deleted' : false, 'unitId' :?0,'activityPriorityId':?1 }",exists = true)
    boolean existsActivitiesByActivityPriorityIdAndUnitId(Long unitId,BigInteger activityPriorityId);

    @Query(value = "{'deleted' : false, 'countryId' :?0,'activityPriorityId':?1 }",exists = true)
    boolean existsActivitiesByActivityPriorityIdAndCountryId(Long countryId,BigInteger activityPriorityId);

    @Query(value = "{deleted: false, parentId :?0,unitId:{$in:?1 }}",exists = true)
    boolean existsByParentIdAndDeletedFalse( BigInteger activityId,List<Long> unitIds);

    @Query(value = "{'activityBalanceSettings.timeType':?0,unitId:{$in:?1 }, deleted:false}")
    List<Activity> findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum timeTypeEnum, Set<Long> unitIds);

    @Query(value = "{'activityGeneralSettings.tags':?0}")
    List<Activity> findActivitiesByTagId(BigInteger tagId);

    @Query(value = "{unitId:?0, 'activityBalanceSettings.timeType':?1 , 'activityRulesSettings.approvalAllowedPhaseIds':?2, deleted:false}")
    List<Activity> findAllAbsenceActivities(Long unitId, TimeTypeEnum timeType, BigInteger phaseId);

    @Query(value = "{'activityRulesSettings.sicknessSettingValid':true,deleted:false ,unitId:?0}",fields ="{'_id':1,'activityRulesSettings':1,'unitId':1}")
    List<Activity> findAllSicknessActivity(Long unitId);

    @Query(value = "{'activityBalanceSettings.priorityFor':?0,activityPriorityId:?1,_id:{$ne: ?2}}",exists = true)
    boolean isActivityPriorityIdIsExistOrNot(PriorityFor priorityFor,BigInteger priorityId,BigInteger activityId);

    @Query(value = "{unitId:?0, 'activityBalanceSettings.timeTypeId':{$in:?1 }, deleted:false}")
    List<Activity>  findAllByUnitIdAndTimeTypeIds(Long unitId, Collection<BigInteger> timeTypeIds);

    @Query(value = "{countryId:?0, 'activityBalanceSettings.timeType':?1 , deleted:false}",fields ="{'_id':1,'name':1,'countryParentId':1}" )
    List<ActivityDTO> findAllAbsenceActivitiesByCountryId(Long countryId, TimeTypeEnum timeType);

}
