package com.kairos.persistence.repository.staff_settings;

import com.kairos.wrapper.activity.ActivityWithCompositeDTO;

import java.util.List;

public interface StaffActivitySettingRepositoryCustom {

    List<ActivityWithCompositeDTO> findAllStaffActivitySettingByStaffIdAndUnityIdWithMostUsedActivityCount(Long unitId, Long staffId);
}
