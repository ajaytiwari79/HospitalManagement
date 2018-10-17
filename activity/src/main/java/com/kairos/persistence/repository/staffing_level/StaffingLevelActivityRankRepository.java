package com.kairos.persistence.repository.staffing_level;/*
 *Created By Pavan on 10/10/18
 *
 */

import com.kairos.persistence.model.staffing_level.StaffingLevelActivityRank;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public interface StaffingLevelActivityRankRepository extends MongoBaseRepository<StaffingLevelActivityRank,BigInteger> {

   List<StaffingLevelActivityRank> findAllByStaffingLevelIdAndStaffingLevelDateAndDeletedFalse();

   Integer findByStaffingLevelDateAndActivityId(LocalDate staffingLevelDate,BigInteger activityId);
}
