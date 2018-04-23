package com.kairos.activity.persistence.repository.staffing_level;

import com.planner.domain.staffinglevel.StaffingLevel;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;


@Repository

public interface StaffingLevelMongoRepository{

    StaffingLevel findByUnitIdAndPhaseIdAndDeletedFalse(Long organizationId, Long phaseId);
    StaffingLevel findByUnitIdAndCurrentDateAndDeletedFalse(Long unitId, Date currentDate);
    boolean existsByUnitIdAndCurrentDateAndDeletedFalse(Long unitId, Date currentDate);
    List<StaffingLevel> findByUnitIdAndCurrentDateBetweenAndDeletedFalse(Long unitId, Date startDate, Date endDate);
}
