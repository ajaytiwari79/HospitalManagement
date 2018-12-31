package com.kairos.persistence.repository.activity;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.CompositeActivityDTO;
import com.kairos.dto.activity.break_settings.BreakActivitiesDTO;
import com.kairos.dto.activity.time_type.TimeTypeAndActivityIdDTO;
import com.kairos.enums.shift.BreakPaymentSetting;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.kairos.dto.activity.activity.OrganizationActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityWithCTAWTASettingsDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.wrapper.activity.ActivityTagDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CustomActivityMongoRepository {

    List<ActivityTagDTO> findAllActivityByOrganizationGroupWithCategoryName(Long unitId, boolean deleted);

    List<ActivityTagDTO> findAllActivitiesByOrganizationType(List<Long> orgTypeIds, List<Long> orgSubTypeIds);

    List<Activity> findAllActivitiesByOrganizationTypeOrSubType(Long orgTypeIds, List<Long> orgSubTypeIds);

    List<CompositeActivityDTO> getCompositeActivities(BigInteger activityId);

    List<ActivityTagDTO> findAllActivityByCountry(long countryId);

    List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByCountry(long countryId);

    List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId);

    List<OrganizationActivityDTO> findAllActivityOfUnitsByParentActivity(List<BigInteger> parentActivityIds, List<Long> unitIds);

    List<ActivityTagDTO> findAllActivityByParentOrganization(long unitId);

    List<ActivityTagDTO> findAllActivityByUnitIdAndDeleted(Long unitId, boolean deleted);

    List<ActivityWithCompositeDTO> findAllActivityByUnitIdWithCompositeActivities(long unitId);

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

    List<BreakActivitiesDTO> getAllActivitiesGroupedByTimeType(Long unitId);
     List<ActivityDTO> findAllByTimeTypeIdAndUnitId(Set<BigInteger> timeTypeIds,Long unitId) ;

    List<ActivityWrapper> findActivitiesAndTimeTypeByActivityId(List<BigInteger> activityIds);
    List<ActivityWrapper> findActivitiesAndTimeTypeByParentIdsAndUnitId(List<BigInteger> activityIds,Long unitId);
    List<ActivityDTO> findAllActivitiesByCountryIdAndTimeTypes(Long countryId,List<BigInteger> timeTypeIds);


    List<Activity> findAllActivitiesByOrganizationTypeOrSubTypeOrBreakTypes(Long orgTypeIds, List<Long> orgSubTypeIds);

    ActivityDTO eligibleForCopy(BigInteger activityId);

}
