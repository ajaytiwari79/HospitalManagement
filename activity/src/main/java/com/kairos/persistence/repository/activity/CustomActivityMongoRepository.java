package com.kairos.persistence.repository.activity;

import com.kairos.activity.activity.ActivityDTO;
import com.kairos.activity.time_type.TimeTypeAndActivityIdDTO;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.kairos.activity.activity.OrganizationActivityDTO;
import com.kairos.activity.activity.activity_tabs.ActivityWithCTAWTASettingsDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.wrapper.activity.ActivityTagDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CustomActivityMongoRepository {

    List<ActivityTagDTO> findAllActivityByOrganizationGroupWithCategoryName(Long unitId, boolean deleted);

    List<ActivityTagDTO> findAllActivitiesByOrganizationType(List<Long> orgTypeIds, List<Long> orgSubTypeIds);

    List<ActivityDTO> findAllActivitiesWithDataByIds(Set<BigInteger> activityIds);

    List<ActivityTagDTO> findAllActivityByCountry(long countryId);

    List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByCountry(long countryId);

    List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId);

    List<OrganizationActivityDTO> findAllActivityOfUnitsByParentActivity(List<BigInteger> parentActivityIds, List<Long> unitIds);

    List<ActivityTagDTO> findAllActivityByParentOrganization(long unitId);

    //List<ActivityDTO> findAllActivitiesWithDataByIds(List<BigInteger> activityIds);

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

}
