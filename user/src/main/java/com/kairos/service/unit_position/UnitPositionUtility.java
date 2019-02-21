package com.kairos.service.unit_position;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.employment.UnitPositionLinesDTO;
import com.kairos.persistence.model.user.expertise.Response.ExpertisePlannedTimeQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.StaffUnitPositionDetails;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionLinesQueryResult;
import com.kairos.persistence.model.user.unit_position.query_result.UnitPositionQueryResult;

import java.util.List;
import java.util.Optional;

/**
 * CreatedBy vipulpandey on 27/10/18
 **/
public class UnitPositionUtility {
    public static com.kairos.dto.activity.shift.StaffUnitPositionDetails convertUnitPositionObject(UnitPositionQueryResult unitPosition) {
        com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails = new com.kairos.dto.activity.shift.StaffUnitPositionDetails();
        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        UnitPositionLinesQueryResult currentPositionLine = ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getPositionLines().get(0), UnitPositionLinesQueryResult.class);
        unitPositionDetails.setEmploymentType(ObjectMapperUtils.copyPropertiesByMapper(currentPositionLine.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class));
        unitPositionDetails.setId(unitPosition.getId());
        unitPositionDetails.setStartDate(unitPosition.getStartDate());
        unitPositionDetails.setAppliedFunctions(unitPosition.getAppliedFunctions());
        unitPositionDetails.setEndDate(unitPosition.getEndDate());
        unitPositionDetails.setPositionLines(ObjectMapperUtils.copyPropertiesOfListByMapper(unitPosition.getPositionLines(), UnitPositionLinesDTO.class));
        unitPositionDetails.setFullTimeWeeklyMinutes(currentPositionLine.getFullTimeWeeklyMinutes());
        unitPositionDetails.setTotalWeeklyMinutes(currentPositionLine.getTotalWeeklyMinutes());
        unitPositionDetails.setTotalWeeklyHours(currentPositionLine.getTotalWeeklyHours());
        unitPositionDetails.setWorkingDaysInWeek(currentPositionLine.getWorkingDaysInWeek());
        unitPositionDetails.setAvgDailyWorkingHours(currentPositionLine.getAvgDailyWorkingHours());
        unitPositionDetails.setHourlyCost(currentPositionLine.getHourlyCost());
        unitPositionDetails.setPublished(unitPosition.getPublished());
        unitPositionDetails.setEditable(unitPosition.getEditable());
        unitPositionDetails.setAccumulatedTimebankMinutes(unitPosition.getAccumulatedTimebankMinutes());
        return unitPositionDetails;
    }

    public static void convertStaffUnitPositionObject(StaffUnitPositionDetails unitPosition, com.kairos.dto.activity.shift.StaffUnitPositionDetails unitPositionDetails,List<ExpertisePlannedTimeQueryResult> expertisePlannedTimes ) {
        unitPositionDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        unitPositionDetails.setStaff(ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getStaff(), com.kairos.dto.user.staff.staff.Staff.class));
        UnitPositionLinesQueryResult currentPositionLine = ObjectMapperUtils.copyPropertiesByMapper(unitPosition.getPositionLines().get(0), UnitPositionLinesQueryResult.class);
        com.kairos.dto.activity.shift.EmploymentType employmentType =ObjectMapperUtils.copyPropertiesByMapper(currentPositionLine.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class);
        Optional<ExpertisePlannedTimeQueryResult> plannedTimeQueryResult=expertisePlannedTimes.stream().filter(
                current -> current.getEmploymentTypes().stream()
                        .anyMatch(employmentTypeOfExpertise -> employmentType.getId().equals(employmentTypeOfExpertise.getId()))).findAny();
        if (plannedTimeQueryResult.isPresent()){
            unitPositionDetails.setExcludedPlannedTime(plannedTimeQueryResult.get().getExcludedPlannedTime());
            unitPositionDetails.setIncludedPlannedTime(plannedTimeQueryResult.get().getIncludedPlannedTime());
        }
        unitPositionDetails.setEmploymentType(employmentType);
        unitPositionDetails.setId(unitPosition.getId());
        unitPositionDetails.setStartDate(unitPosition.getStartDate());
        unitPositionDetails.setAppliedFunctions(unitPosition.getAppliedFunctions());
        unitPositionDetails.setEndDate(unitPosition.getEndDate());
        unitPositionDetails.setFullTimeWeeklyMinutes(currentPositionLine.getFullTimeWeeklyMinutes());
        unitPositionDetails.setTotalWeeklyMinutes(currentPositionLine.getTotalWeeklyHours()*60+currentPositionLine.getTotalWeeklyMinutes());
        unitPositionDetails.setWorkingDaysInWeek(currentPositionLine.getWorkingDaysInWeek());
        unitPositionDetails.setAvgDailyWorkingHours(currentPositionLine.getAvgDailyWorkingHours());

    }

}
