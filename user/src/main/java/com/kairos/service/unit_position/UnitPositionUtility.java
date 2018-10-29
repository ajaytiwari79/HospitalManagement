package com.kairos.service.unit_position;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.persistence.model.user.unit_position.query_result.StaffUnitPositionDetails;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionLinesQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionQueryResult;

/**
 * CreatedBy vipulpandey on 27/10/18
 **/
public class UnitPositionUtility {
    public static void convertUnitPositionObject(UnitPositionQueryResult unitPosition, com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails) {
        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        UnitPositionLinesQueryResult currentPositionLine = ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getPositionLines().get(0), UnitPositionLinesQueryResult.class);
        unitPositionDetails.setEmploymentType(ObjectMapperUtils.copyPropertiesByMapper(currentPositionLine.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class));
        unitPositionDetails.setId(unitPosition.getId());
        unitPositionDetails.setStartDate(unitPosition.getStartDate());
        unitPositionDetails.setAppliedFunctions(unitPosition.getAppliedFunctions());
        unitPositionDetails.setEndDate(unitPosition.getEndDate());
        unitPositionDetails.setFullTimeWeeklyMinutes(currentPositionLine.getFullTimeWeeklyMinutes());
        unitPositionDetails.setTotalWeeklyMinutes(currentPositionLine.getTotalWeeklyMinutes());
        unitPositionDetails.setTotalWeeklyHours(currentPositionLine.getTotalWeeklyHours());
        unitPositionDetails.setWorkingDaysInWeek(currentPositionLine.getWorkingDaysInWeek());
        unitPositionDetails.setAvgDailyWorkingHours(currentPositionLine.getAvgDailyWorkingHours());
        unitPositionDetails.setHourlyCost(currentPositionLine.getHourlyCost());
    }

    public static void convertUnitPositionObject(StaffUnitPositionDetails unitPosition, com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails) {
        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        unitPositionDetails.setStaff(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getStaff(), com.kairos.dto.user.staff.staff.Staff.class));
        UnitPositionLinesQueryResult currentPositionLine = ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getPositionLines().get(0), UnitPositionLinesQueryResult.class);
        unitPositionDetails.setEmploymentType(ObjectMapperUtils.copyPropertiesByMapper(currentPositionLine.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class));
        unitPositionDetails.setId(unitPosition.getId());

        unitPositionDetails.setStartDate(unitPosition.getStartDate());
        unitPositionDetails.setAppliedFunctions(unitPosition.getAppliedFunctions());
        unitPositionDetails.setEndDate(unitPosition.getEndDate());
        unitPositionDetails.setFullTimeWeeklyMinutes(currentPositionLine.getFullTimeWeeklyMinutes());
        unitPositionDetails.setTotalWeeklyMinutes(currentPositionLine.getTotalWeeklyMinutes());
        unitPositionDetails.setWorkingDaysInWeek(currentPositionLine.getWorkingDaysInWeek());
        unitPositionDetails.setAvgDailyWorkingHours(currentPositionLine.getAvgDailyWorkingHours());
        unitPositionDetails.setHourlyCost(currentPositionLine.getHourlyCost());
    }

}
