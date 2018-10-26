package com.planner.service.constraint.country.default_;

import com.kairos.enums.constraint.ConstraintLevel;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.planner.domain.constraint.country.CountryConstraint;

import java.util.ArrayList;
import java.util.List;

//@Service
public class DefaultCountryConstraintService {
    /**
     * Method to create following constraints for particular country
     * 1.)Activity
     * 2.)WTA
     *
     * @return
     */

    static String commonDescription = "Temporary Message";
    static int penaltySoft = -1;
    static int penaltyMedium = -5;
    static int penaltyHard = -1;
    static ConstraintSubType AVERAGE_SHEDULED_TIME=ConstraintSubType.AVERAGE_SHEDULED_TIME;
    static ConstraintSubType CONSECUTIVE_WORKING_PARTOFDAY=ConstraintSubType.CONSECUTIVE_WORKING_PARTOFDAY;
    static ConstraintSubType DAYS_OFF_IN_PERIOD=ConstraintSubType.DAYS_OFF_IN_PERIOD;
    static ConstraintSubType NUMBER_OF_PARTOFDAY=ConstraintSubType.NUMBER_OF_PARTOFDAY;
    static ConstraintSubType SHIFT_LENGTH=ConstraintSubType.SHIFT_LENGTH;
    static ConstraintSubType NUMBER_OF_SHIFTS_IN_INTERVAL=ConstraintSubType.NUMBER_OF_SHIFTS_IN_INTERVAL;
    static ConstraintSubType TIME_BANK=ConstraintSubType.TIME_BANK;
    static ConstraintSubType VETO_PER_PERIOD=ConstraintSubType.VETO_PER_PERIOD;
    static ConstraintSubType DAILY_RESTING_TIME=ConstraintSubType.DAILY_RESTING_TIME;
    static ConstraintSubType DURATION_BETWEEN_SHIFTS=ConstraintSubType.DURATION_BETWEEN_SHIFTS;
    static ConstraintSubType REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS=ConstraintSubType.REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS;
    static ConstraintSubType WEEKLY_REST_PERIOD=ConstraintSubType.WEEKLY_REST_PERIOD;
    static ConstraintSubType NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD=ConstraintSubType.NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD;
    static ConstraintSubType SHORTEST_AND_AVERAGE_DAILY_REST=ConstraintSubType.SHORTEST_AND_AVERAGE_DAILY_REST;
    static ConstraintSubType SENIOR_DAYS_PER_YEAR=ConstraintSubType.SENIOR_DAYS_PER_YEAR;
    static ConstraintSubType CHILD_CARE_DAYS_CHECK=ConstraintSubType.CHILD_CARE_DAYS_CHECK;
    static ConstraintSubType DAYS_OFF_AFTER_A_SERIES=ConstraintSubType.DAYS_OFF_AFTER_A_SERIES;
    static ConstraintSubType NO_OF_SEQUENCE_SHIFT=ConstraintSubType.NO_OF_SEQUENCE_SHIFT;
    static ConstraintSubType EMPLOYEES_WITH_INCREASE_RISK=ConstraintSubType.EMPLOYEES_WITH_INCREASE_RISK;
    static ConstraintSubType WTA_FOR_CARE_DAYS=ConstraintSubType.WTA_FOR_CARE_DAYS;
    //For Activity
    static ConstraintSubType ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS=ConstraintSubType.ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS;
    static ConstraintSubType ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH=ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH;
    static ConstraintSubType MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF=ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF;

    public static List<CountryConstraint> createDefaultCountryConstraints(Long countryId, Long organizationServiceId, Long organizationSubServiceId, Long planningProblemId) {
        Long COUNTRY_ID = countryId;
        Long ORGANIZATION_SERVICE_ID = organizationServiceId;
        Long ORGANIZATION_SUB_SERVICE_ID = organizationSubServiceId;
        Long PLANNING_PROBLEM_ID = planningProblemId;

        List<CountryConstraint> countryConstraintList = new ArrayList<>();
        countryConstraintList.add(new CountryConstraint(null, AVERAGE_SHEDULED_TIME.toString(), commonDescription, ConstraintType.WTA, AVERAGE_SHEDULED_TIME, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, CONSECUTIVE_WORKING_PARTOFDAY.toString(), commonDescription, ConstraintType.WTA,CONSECUTIVE_WORKING_PARTOFDAY , ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, DAYS_OFF_IN_PERIOD.toString(), commonDescription, ConstraintType.WTA, DAYS_OFF_IN_PERIOD, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, NUMBER_OF_PARTOFDAY.toString(), commonDescription, ConstraintType.WTA, NUMBER_OF_PARTOFDAY, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, SHIFT_LENGTH.toString(), commonDescription, ConstraintType.WTA, SHIFT_LENGTH, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, NUMBER_OF_SHIFTS_IN_INTERVAL.toString(), commonDescription, ConstraintType.WTA, NUMBER_OF_SHIFTS_IN_INTERVAL, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, TIME_BANK.toString(), commonDescription, ConstraintType.WTA, TIME_BANK, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, VETO_PER_PERIOD.toString(), commonDescription, ConstraintType.WTA,VETO_PER_PERIOD, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, DAILY_RESTING_TIME.toString(), commonDescription, ConstraintType.WTA, DAILY_RESTING_TIME, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS.toString(), commonDescription, ConstraintType.WTA, REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, WEEKLY_REST_PERIOD.toString(), commonDescription, ConstraintType.WTA, WEEKLY_REST_PERIOD, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD.toString(), commonDescription, ConstraintType.WTA, NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, SHORTEST_AND_AVERAGE_DAILY_REST.toString(), commonDescription, ConstraintType.WTA, SHORTEST_AND_AVERAGE_DAILY_REST, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, SENIOR_DAYS_PER_YEAR.toString(), commonDescription, ConstraintType.WTA, SENIOR_DAYS_PER_YEAR, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, CHILD_CARE_DAYS_CHECK.toString(), commonDescription, ConstraintType.WTA, CHILD_CARE_DAYS_CHECK, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, NO_OF_SEQUENCE_SHIFT.toString(), commonDescription, ConstraintType.WTA, NO_OF_SEQUENCE_SHIFT, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, DAYS_OFF_AFTER_A_SERIES.toString(), commonDescription, ConstraintType.WTA, DAYS_OFF_AFTER_A_SERIES, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, EMPLOYEES_WITH_INCREASE_RISK.toString(), commonDescription, ConstraintType.WTA, EMPLOYEES_WITH_INCREASE_RISK, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, WTA_FOR_CARE_DAYS.toString(), commonDescription, ConstraintType.WTA, WTA_FOR_CARE_DAYS, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, DURATION_BETWEEN_SHIFTS.toString(), commonDescription, ConstraintType.WTA, DURATION_BETWEEN_SHIFTS, ConstraintLevel.SOFT, penaltySoft, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        //For Activity
        countryConstraintList.add(new CountryConstraint(null, DURATION_BETWEEN_SHIFTS.toString(), commonDescription, ConstraintType.ACTIVITY, ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS, ConstraintLevel.HARD, penaltyHard, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH.toString(), commonDescription, ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ConstraintLevel.HARD, penaltyHard, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF.toString(), commonDescription, ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ConstraintLevel.HARD, penaltyHard, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        return countryConstraintList;
    }
}
