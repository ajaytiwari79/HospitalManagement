package com.kairos.persistence.repository.staff_settings;

import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface StaffActivitySettingRepository extends MongoBaseRepository<StaffActivitySetting, BigInteger> {
    List<StaffActivitySettingDTO> findAllByUnitIdAndDeletedFalse(Long unitId);

    StaffActivitySetting findByIdAndDeletedFalse(BigInteger staffActivitySettingId);

    List<ActivityWithCompositeDTO> findAllByUnitIdAndStaffIdAndDeletedFalse(Long unitId, Long staffId);

    StaffActivitySettingDTO findByIdAndUnitIdAndDeletedFalse(BigInteger staffActivitySettingId,Long unitId);

    Set<StaffActivitySetting> findByStaffIdInAndActivityIdInAndDeletedFalse(Set<Long> staffIds, Set<BigInteger> activityIds);

    List<StaffActivitySetting> findByStaffIdAndActivityIdInAndDeletedFalse(Long staffId,List<BigInteger> activityId);

}
