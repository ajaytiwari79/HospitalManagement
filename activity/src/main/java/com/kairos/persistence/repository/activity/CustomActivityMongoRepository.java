package com.kairos.persistence.repository.activity;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.CompositeActivityDTO;
import com.kairos.dto.activity.activity.OrganizationActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityWithCTAWTASettingsDTO;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.time_type.TimeTypeAndActivityIdDTO;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.wrapper.activity.ActivityTagDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CustomActivityMongoRepository {

    List<ActivityDTO> findAllActivityByOrganizationGroupWithCategoryName(Long unitId, boolean deleted);

    List<ActivityTagDTO> findAllActivitiesByOrganizationType(List<Long> orgTypeIds, List<Long> orgSubTypeIds);

    List<CompositeActivityDTO> getCompositeActivities(BigInteger activityId);

    List<ActivityTagDTO> findAllActivityByCountry(long countryId);

    List<ActivityTagDTO> findAllowChildActivityByCountryId(long countryId);

    ActivityWithCompositeDTO findActivityByActivityId(BigInteger activityId);

    List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByCountry(long countryId);

    List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId);

    List<OrganizationActivityDTO> findAllActivityOfUnitsByParentActivity(List<BigInteger> parentActivityIds, List<Long> unitIds);

    List<ActivityTagDTO> findAllActivityByParentOrganization(long unitId);

    List<ActivityTagDTO> findAllActivityByUnitIdAndDeleted(Long unitId, boolean deleted);

    List<ActivityTagDTO> findAllowChildActivityByUnitIdAndDeleted(Long unitId, boolean deleted);

    List<ActivityWithCompositeDTO> findAllActivityByUnitIdWithCompositeActivities(List<BigInteger> activityIds);

    List<ActivityWithCompositeDTO> findAllActivityByUnitIdWithCompositeActivities(Long unitId);

    List<PhaseSettingsActivityTab> findActivityIdAndStatusByUnitAndAccessGroupIds(Long unitId, List<Long> accessGroupIds);

    List<ActivityDTO> findAllActivityByUnitId(Long unitId, boolean deleted);

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
    List<ActivityWrapper> findActivitiesAndTimeTypeByParentIdsAndUnitId(List<BigInteger> activityIds,Long unitId);
    List<ActivityDTO> findAllActivitiesByCountryIdAndTimeTypes(Long countryId,List<BigInteger> timeTypeIds);


    List<Activity> findAllActivitiesByOrganizationTypeOrSubTypeOrBreakTypes(Long orgTypeIds, List<Long> orgSubTypeIds);

    List<ActivityWrapper> findActivityAndTimeTypeByActivityIdsAndNotFullDayAndFullWeek(Set<BigInteger> activityIds);

    List<ActivityDTO> findChildActivityActivityIds(Set<BigInteger> activityIds);

    boolean existsByActivityIdInChildActivities(BigInteger activityId);

    boolean unassignExpertiseFromActivitiesByExpertiesId(Long expertiseId);

    boolean unassignCompositeActivityFromActivitiesByactivityId(BigInteger activityId);

    List<Activity> findByActivityIdInChildActivities(BigInteger activityId, List<BigInteger> allowedActivityIds);

    ActivityDTO findByIdAndChildActivityEligibleForStaffingLevelTrue(BigInteger activityId);

    List<ActivityTagDTO> findAllActivityByUnitIdAndNotPartOfTeam(Long unitId);
    TimeTypeEnum findTimeTypeByActivityId(BigInteger activityId);
}
