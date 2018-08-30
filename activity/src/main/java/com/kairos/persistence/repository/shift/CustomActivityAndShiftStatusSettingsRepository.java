package com.kairos.persistence.repository.shift;/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.activity.shift.ActivityAndShiftStatusWrapper;

import java.util.List;

public interface CustomActivityAndShiftStatusSettingsRepository {

    List<ActivityAndShiftStatusWrapper> getActivityAndShiftStatusSettingsGroupedByStatus(Long countryId);

    List<ActivityAndShiftStatusWrapper> getActivityAndShiftStatusSettingsGroupedByStatusForUnit(Long unitId);

}
