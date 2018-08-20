package com.kairos.persistence.repository.open_shift;/*
 *Created By Pavan on 17/8/18
 *
 */

import com.kairos.persistence.model.open_shift.OpenShift;

import java.util.Date;
import java.util.List;

public interface CustomOpenShiftNotificationMongoRepository {
    List<OpenShift> findAllApplicableOpenShiftsForStaff(Long staffId, Date startDate, Date endDate);
}
