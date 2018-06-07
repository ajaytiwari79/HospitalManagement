package com.kairos.activity.persistence.repository.activity;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.ActivityWithCompositeDTO;
import com.kairos.activity.response.dto.OrganizationTypeAndSubTypeDTO;
import com.kairos.activity.response.dto.activity.ActivityTagDTO;
import com.kairos.activity.response.dto.activity.ActivityWithCTAWTASettingsDTO;
import com.kairos.activity.response.dto.activity.OrganizationActivityDTO;

import java.math.BigInteger;
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

    Set<BigInteger> findAllActivitiesByUnitIdAndUnavailableTimeType(long unitId);
}
