package com.kairos.service.employment;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.dto.activity.shift.FunctionDTO;
import com.kairos.persistence.model.user.expertise.Response.ExpertisePlannedTimeQueryResult;
import com.kairos.persistence.model.user.employment.query_result.StaffEmploymentDetails;
import com.kairos.persistence.model.user.employment.query_result.EmploymentLinesQueryResult;
import com.kairos.persistence.model.user.employment.query_result.EmploymentQueryResult;

import java.util.List;
import java.util.Optional;

/**
 * CreatedBy vipulpandey on 27/10/18
 **/
public class EmploymentUtility {
    public static com.kairos.dto.activity.shift.StaffEmploymentDetails convertEmploymentObject(EmploymentQueryResult employment) {
        com.kairos.dto.activity.shift.StaffEmploymentDetails employmentDetails = new com.kairos.dto.activity.shift.StaffEmploymentDetails();
        employmentDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(employment.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        EmploymentLinesQueryResult currentEmploymentLine = ObjectMapperUtils.copyPropertiesByMapper(employment.getEmploymentLines().get(0), EmploymentLinesQueryResult.class);
        employmentDetails.setEmploymentType(ObjectMapperUtils.copyPropertiesByMapper(currentEmploymentLine.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class));
        employmentDetails.setId(employment.getId());
        employmentDetails.setStartDate(employment.getStartDate());

        employmentDetails.setAppliedFunctions(ObjectMapperUtils.copyPropertiesOfListByMapper(employment.getAppliedFunctions(),FunctionDTO.class));
        employmentDetails.setEndDate(employment.getEndDate());
        employmentDetails.setEmploymentLines(ObjectMapperUtils.copyPropertiesOfListByMapper(employment.getEmploymentLines(), EmploymentLinesDTO.class));
        employmentDetails.setFullTimeWeeklyMinutes(currentEmploymentLine.getFullTimeWeeklyMinutes());
        employmentDetails.setTotalWeeklyMinutes(currentEmploymentLine.getTotalWeeklyMinutes());
        employmentDetails.setTotalWeeklyHours(currentEmploymentLine.getTotalWeeklyHours());
        employmentDetails.setWorkingDaysInWeek(currentEmploymentLine.getWorkingDaysInWeek());
        employmentDetails.setAvgDailyWorkingHours(currentEmploymentLine.getAvgDailyWorkingHours());
        employmentDetails.setHourlyCost(currentEmploymentLine.getHourlyCost());
        employmentDetails.setPublished(employment.getPublished());
        employmentDetails.setEditable(employment.getEditable());
        employmentDetails.setAccumulatedTimebankMinutes(employment.getAccumulatedTimebankMinutes());
        employmentDetails.setAccumulatedTimebankDate(employment.getAccumulatedTimebankDate());
        return employmentDetails;
    }

    public static void convertStaffEmploymentObject(StaffEmploymentDetails employment, com.kairos.dto.activity.shift.StaffEmploymentDetails employmentDetails, List<ExpertisePlannedTimeQueryResult> expertisePlannedTimes ) {
        employmentDetails.setExpertise(ObjectMapperUtils.copyPropertiesByMapper(employment.getExpertise(), com.kairos.dto.activity.shift.Expertise.class));
        employmentDetails.setStaff(ObjectMapperUtils.copyPropertiesByMapper(employment.getStaff(), com.kairos.dto.user.staff.staff.Staff.class));
        EmploymentLinesQueryResult currentEmploymentLine = ObjectMapperUtils.copyPropertiesByMapper(employment.getEmploymentLines().get(0), EmploymentLinesQueryResult.class);
        com.kairos.dto.activity.shift.EmploymentType employmentType =ObjectMapperUtils.copyPropertiesByMapper(currentEmploymentLine.getEmploymentType(), com.kairos.dto.activity.shift.EmploymentType.class);
        Optional<ExpertisePlannedTimeQueryResult> plannedTimeQueryResult=expertisePlannedTimes.stream().filter(
                current -> current.getEmploymentTypes().stream()
                        .anyMatch(employmentTypeOfExpertise -> employmentType.getId().equals(employmentTypeOfExpertise.getId()))).findAny();
        if (plannedTimeQueryResult.isPresent()){
            employmentDetails.setExcludedPlannedTime(plannedTimeQueryResult.get().getExcludedPlannedTime());
            employmentDetails.setIncludedPlannedTime(plannedTimeQueryResult.get().getIncludedPlannedTime());
        }
        employmentDetails.setEmploymentType(employmentType);
        employmentDetails.setId(employment.getId());
        employmentDetails.setStartDate(employment.getStartDate());
        employmentDetails.setAppliedFunctions(employment.getAppliedFunctions());
        employmentDetails.setEndDate(employment.getEndDate());
        employmentDetails.setFullTimeWeeklyMinutes(currentEmploymentLine.getFullTimeWeeklyMinutes());
        employmentDetails.setTotalWeeklyMinutes(currentEmploymentLine.getTotalWeeklyHours()*60+currentEmploymentLine.getTotalWeeklyMinutes());
        employmentDetails.setWorkingDaysInWeek(currentEmploymentLine.getWorkingDaysInWeek());
        employmentDetails.setAvgDailyWorkingHours(currentEmploymentLine.getAvgDailyWorkingHours());

    }

}
