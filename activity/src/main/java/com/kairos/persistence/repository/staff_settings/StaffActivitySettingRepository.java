package com.kairos.persistence.repository.staff_settings;

import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface StaffActivitySettingRepository extends MongoBaseRepository<StaffActivitySetting, BigInteger> {
    List<StaffActivitySettingDTO> findAllByUnitIdAndDeletedFalse(Long unitId);

    StaffActivitySetting findByIdAndDeletedFalse(BigInteger staffActivitySettingId);

    List<StaffActivitySettingDTO> findAllByUnitIdAndStaffIdAndDeletedFalse(Long unitId,Long staffId);

    StaffActivitySettingDTO findByIdAndUnitIdAndDeletedFalse(BigInteger staffActivitySettingId,Long unitId);

    StaffActivitySetting findByStaffIdAndActivityIdAndDeletedFalse(Long staffId,BigInteger activityId);

}
