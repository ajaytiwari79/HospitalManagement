package com.kairos.persistence.repository.activity;

import com.kairos.dto.activity.activity.ActivityCategoryListDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.CompositeActivityDTO;
import com.kairos.dto.activity.activity.OrganizationActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityPhaseSettings;
import com.kairos.dto.activity.activity.activity_tabs.ActivityWithCTAWTASettingsDTO;
import com.kairos.dto.activity.time_type.TimeTypeAndActivityIdDTO;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.enums.PriorityFor;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.UnityActivitySetting;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.wrapper.activity.ActivityTagDTO;
import com.kairos.wrapper.activity.ActivityTimeTypeWrapper;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CustomActivityMongoRepository {

    List<ActivityCategoryListDTO> findAllActivityByOrganizationGroupWithCategoryName(Long unitId, boolean deleted);

    List<ActivityTagDTO> findAllActivitiesByOrganizationType(List<Long> orgTypeIds, List<Long> orgSubTypeIds);

    List<ActivityTagDTO> findAllActivityByCountry(long countryId);

    List<ActivityTagDTO> findAllowChildActivityByCountryId(long countryId);

    ActivityWithCompositeDTO findActivityByActivityId(BigInteger activityId);

    List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByCountry(long countryId);

    List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId);

    List<OrganizationActivityDTO> findAllActivityOfUnitsByParentActivity(List<BigInteger> parentActivityIds, List<Long> unitIds);

    List<ActivityTagDTO> findAllActivityByParentOrganization(long unitId);

    List<ActivityTagDTO> findAllActivityByUnitIdAndDeleted(Long unitId, Long countryId);

    List<ActivityTagDTO> findAllowChildActivityByUnitIdAndDeleted(Long unitId, boolean deleted);

    //@Cacheable(value = "findAllActivityByUnitIdWithCompositeActivities", key = "#unitId", cacheManager = "cacheManager")
    List<ActivityWithCompositeDTO> findAllActivityByUnitIdWithCompositeActivities(Long unitId,Collection<BigInteger> activitIds);

    List<ActivityPhaseSettings> findActivityIdAndStatusByUnitAndAccessGroupIds(Long unitId, List<Long> accessGroupIds);

    List<ActivityDTO> findAllActivityByUnitId(Long unitId);

    Activity getActivityByNameAndUnitId(Long unitId,String name);

    List<ActivityDTO> findAllActivitiesWithBalanceSettings(long unitId);

    List<ActivityDTO> findAllActivitiesWithTimeTypes(long countryId);

    List<ActivityDTO> findAllActivitiesWithTimeTypesByUnit(Long unitId);

    Activity findByNameExcludingCurrentInCountryAndDate(String name, BigInteger activityId, Long countryId,LocalDate startDate,LocalDate endDate);

    Activity findByNameExcludingCurrentInUnitAndDate(String name, BigInteger activityId, Long unitId,LocalDate startDate,LocalDate endDate);

    Set<BigInteger> findAllActivitiesByUnitIdAndUnavailableTimeType(long unitId);

    Activity findByNameIgnoreCaseAndCountryIdAndByDate(String name, Long countryId, LocalDate startDate,LocalDate endDate);

    Activity findByNameIgnoreCaseAndUnitIdAndByDate(String name, Long unitId, LocalDate startDate,LocalDate endDate);

    ActivityWrapper findActivityAndTimeTypeByActivityId(BigInteger activityId);

    List<TimeTypeAndActivityIdDTO> findAllTimeTypeByActivityIds(Set<BigInteger> activityIds);

    StaffActivitySettingDTO findStaffPersonalizedSettings(Long unitId,BigInteger activityId);

    List<ActivityDTO> findAllByTimeTypeIdAndUnitId(Set<BigInteger> timeTypeIds,Long unitId) ;

    List<ActivityWrapper> findActivitiesAndTimeTypeByActivityId(Collection<BigInteger> activityIds);

    List<ActivityWrapper> findParentActivitiesAndTimeTypeByActivityId(Collection<BigInteger> activityIds);

    List<Activity> findActivitiesSickSettingByActivityIds(Collection<BigInteger> activityIds);
    List<ActivityWrapper> findActivitiesAndTimeTypeByParentIdsAndUnitId(List<BigInteger> activityIds,Long unitId);
    List<ActivityDTO> findAllActivitiesByCountryIdAndTimeTypes(Long countryId,List<BigInteger> timeTypeIds);


    List<Activity> findAllActivitiesByOrganizationTypeOrSubTypeOrBreakTypes(Long orgTypeIds, List<Long> orgSubTypeIds);

    List<ActivityDTO> findChildActivityActivityIds(Set<BigInteger> activityIds);

    boolean existsByActivityIdInChildActivities(BigInteger activityId);

    boolean unassignExpertiseFromActivitiesByExpertiesId(Long expertiseId);

    boolean unassignCompositeActivityFromActivitiesByactivityId(BigInteger activityId);

    List<ActivityTagDTO> findAllActivityByUnitIdAndNotPartOfTeam(Long unitId);

    TimeTypeEnum findTimeTypeByActivityId(BigInteger activityId);

    List<ActivityDTO> findAbsenceActivityByUnitId(Long unitId);
    List<ActivityDTO> getActivityRankWithRankByUnitId(Long unitId);

    List<ActivityWrapper> getAllActivityWrapperBySecondLevelTimeType(TimeTypeEnum secondLevelTimeType, Long unitId);

    List<ActivityTimeTypeWrapper> getActivityPath(final String activityId);

    List<ActivityDTO> getActivityDetailsWithRankByUnitId(Long unitId);

    List<Activity> findAllBreakActivitiesByOrganizationId(Long unitId);

    Set<BigInteger> findAllShowOnCallAndStandByActivitiesByUnitId(Long unitId, UnityActivitySetting unityActivitySetting);

    List<ActivityWithCompositeDTO> findAllActivityByIdsAndIncludeChildActivitiesWithMostUsedCountOfActivity(Collection<BigInteger> activityIds,Long unitId,Long staffId,boolean isActivityType);
    List[] findAllNonProductiveTypeActivityIdsAndAssignedStaffIds(Collection<BigInteger> activityIds);

    List<ActivityDTO> findAllActivitiesByTimeType(Long countryId, TimeTypeEnum timeType);

    List<ActivityDTO> findAllActivityByCountryAndPriorityFor(long refId, boolean refType, PriorityFor priorityFor);
}
