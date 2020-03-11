package com.planner.service.constraint.country.default_;

import com.kairos.enums.constraint.ConstraintLevel;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.planner.domain.constraint.country.CountryConstraint;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultCountryConstraintService {
    /**
     * Method to create following constraints for particular country
     * 1.)Activity
     * 2.)WTA
     *
     * @return
     */

    static final String COMMON_DESCRIPTION = "This constraint is for";
    static final int PENALTY_SOFT = -1;
    static final int PENALTY_MEDIUM = -5;
    static final int PENALTY_HARD = -1;
    static final ConstraintSubType AVERAGE_SHEDULED_TIME = ConstraintSubType.AVERAGE_SCHEDULED_TIME;
    static final ConstraintSubType CONSECUTIVE_WORKING_PARTOFDAY = ConstraintSubType.CONSECUTIVE_WORKING_PARTOFDAY;
    static final ConstraintSubType DAYS_OFF_IN_PERIOD = ConstraintSubType.DAYS_OFF_IN_PERIOD;
    static final ConstraintSubType NUMBER_OF_PARTOFDAY = ConstraintSubType.NUMBER_OF_PARTOFDAY;
    static final ConstraintSubType SHIFT_LENGTH = ConstraintSubType.SHIFT_LENGTH;
    static final ConstraintSubType NUMBER_OF_SHIFTS_IN_INTERVAL = ConstraintSubType.NUMBER_OF_SHIFTS_IN_INTERVAL;
    static final ConstraintSubType TIME_BANK = ConstraintSubType.TIME_BANK;
    static final ConstraintSubType VETO_PER_PERIOD = ConstraintSubType.VETO_PER_PERIOD;
    static final ConstraintSubType DAILY_RESTING_TIME = ConstraintSubType.DAILY_RESTING_TIME;
    static final ConstraintSubType DURATION_BETWEEN_SHIFTS = ConstraintSubType.DURATION_BETWEEN_SHIFTS;
    static final ConstraintSubType REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS = ConstraintSubType.REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS;
    static final ConstraintSubType WEEKLY_REST_PERIOD = ConstraintSubType.WEEKLY_REST_PERIOD;
    static final ConstraintSubType NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD = ConstraintSubType.NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD;
    static final ConstraintSubType SHORTEST_AND_AVERAGE_DAILY_REST = ConstraintSubType.SHORTEST_AND_AVERAGE_DAILY_REST;
    static final ConstraintSubType SENIOR_DAYS_PER_YEAR = ConstraintSubType.SENIOR_DAYS_PER_YEAR;
    static final ConstraintSubType CHILD_CARE_DAYS_CHECK = ConstraintSubType.CHILD_CARE_DAYS_CHECK;
    static final ConstraintSubType DAYS_OFF_AFTER_A_SERIES = ConstraintSubType.DAYS_OFF_AFTER_A_SERIES;
    static final ConstraintSubType NO_OF_SEQUENCE_SHIFT = ConstraintSubType.NO_OF_SEQUENCE_SHIFT;
    static final ConstraintSubType EMPLOYEES_WITH_INCREASE_RISK = ConstraintSubType.EMPLOYEES_WITH_INCREASE_RISK;
    static final ConstraintSubType WTA_FOR_CARE_DAYS = ConstraintSubType.WTA_FOR_CARE_DAYS;
    //For Activity
    static final ConstraintSubType ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS = ConstraintSubType.ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS;
    static final ConstraintSubType ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH = ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH;
    static final ConstraintSubType MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF = ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF;

    public static List<CountryConstraint> createDefaultCountryConstraints(Long countryId, BigInteger planningProblemId) {
        Long COUNTRY_ID = countryId;
        Long ORGANIZATION_SERVICE_ID = null;
        Long ORGANIZATION_SUB_SERVICE_ID = null;
        BigInteger PLANNING_PROBLEM_ID = planningProblemId;

        List<CountryConstraint> countryConstraintList = new ArrayList<>();
        countryConstraintList.add(new CountryConstraint(null, AVERAGE_SHEDULED_TIME.toString(), COMMON_DESCRIPTION +"AVERAGE_SHEDULED_TIME", ConstraintType.WTA, AVERAGE_SHEDULED_TIME, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, CONSECUTIVE_WORKING_PARTOFDAY.toString(), COMMON_DESCRIPTION +"CONSECUTIVE_WORKING_PARTOFDAY", ConstraintType.WTA,CONSECUTIVE_WORKING_PARTOFDAY , ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, DAYS_OFF_IN_PERIOD.toString(), COMMON_DESCRIPTION +"DAYS_OFF_IN_PERIOD", ConstraintType.WTA, DAYS_OFF_IN_PERIOD, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, NUMBER_OF_PARTOFDAY.toString(), COMMON_DESCRIPTION +"NUMBER_OF_PARTOFDAY", ConstraintType.WTA, NUMBER_OF_PARTOFDAY, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, SHIFT_LENGTH.toString(), COMMON_DESCRIPTION +"SHIFT_LENGTH", ConstraintType.WTA, SHIFT_LENGTH, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, NUMBER_OF_SHIFTS_IN_INTERVAL.toString(), COMMON_DESCRIPTION +"NUMBER_OF_SHIFTS_IN_INTERVAL", ConstraintType.WTA, NUMBER_OF_SHIFTS_IN_INTERVAL, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, TIME_BANK.toString(), COMMON_DESCRIPTION +"TIME_BANK", ConstraintType.WTA, TIME_BANK, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, VETO_PER_PERIOD.toString(), COMMON_DESCRIPTION +"VETO_AND_STOP_BRICKS", ConstraintType.WTA,VETO_PER_PERIOD, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, DAILY_RESTING_TIME.toString(), COMMON_DESCRIPTION +"DAILY_RESTING_TIME", ConstraintType.WTA, DAILY_RESTING_TIME, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS.toString(), COMMON_DESCRIPTION +"REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS", ConstraintType.WTA, REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, WEEKLY_REST_PERIOD.toString(), COMMON_DESCRIPTION +"WEEKLY_REST_PERIOD", ConstraintType.WTA, WEEKLY_REST_PERIOD, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD.toString(), COMMON_DESCRIPTION +"NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD", ConstraintType.WTA, NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, SHORTEST_AND_AVERAGE_DAILY_REST.toString(), COMMON_DESCRIPTION +"SHORTEST_AND_AVERAGE_DAILY_REST", ConstraintType.WTA, SHORTEST_AND_AVERAGE_DAILY_REST, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, SENIOR_DAYS_PER_YEAR.toString(), COMMON_DESCRIPTION +"SENIOR_DAYS_PER_YEAR", ConstraintType.WTA, SENIOR_DAYS_PER_YEAR, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, CHILD_CARE_DAYS_CHECK.toString(), COMMON_DESCRIPTION +"CHILD_CARE_DAYS_CHECK", ConstraintType.WTA, CHILD_CARE_DAYS_CHECK, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, NO_OF_SEQUENCE_SHIFT.toString(), COMMON_DESCRIPTION +"NO_OF_SEQUENCE_SHIFT", ConstraintType.WTA, NO_OF_SEQUENCE_SHIFT, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, DAYS_OFF_AFTER_A_SERIES.toString(), COMMON_DESCRIPTION +"DAYS_OFF_AFTER_A_SERIES", ConstraintType.WTA, DAYS_OFF_AFTER_A_SERIES, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, EMPLOYEES_WITH_INCREASE_RISK.toString(), COMMON_DESCRIPTION +"EMPLOYEES_WITH_INCREASE_RISK", ConstraintType.WTA, EMPLOYEES_WITH_INCREASE_RISK, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, WTA_FOR_CARE_DAYS.toString(), COMMON_DESCRIPTION +"WTA_FOR_CARE_DAYS", ConstraintType.WTA, WTA_FOR_CARE_DAYS, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, DURATION_BETWEEN_SHIFTS.toString(), COMMON_DESCRIPTION +"DURATION_BETWEEN_SHIFTS", ConstraintType.WTA, DURATION_BETWEEN_SHIFTS, ConstraintLevel.SOFT, PENALTY_SOFT, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        //For Activity
        countryConstraintList.add(new CountryConstraint(null, DURATION_BETWEEN_SHIFTS.toString(), COMMON_DESCRIPTION +"ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS", ConstraintType.ACTIVITY, ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS, ConstraintLevel.HARD, PENALTY_HARD, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH.toString(), COMMON_DESCRIPTION +"ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH", ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ConstraintLevel.HARD, PENALTY_HARD, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        countryConstraintList.add(new CountryConstraint(null, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF.toString(), COMMON_DESCRIPTION +"MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF", ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ConstraintLevel.HARD, PENALTY_HARD, PLANNING_PROBLEM_ID, null, COUNTRY_ID, ORGANIZATION_SERVICE_ID, ORGANIZATION_SUB_SERVICE_ID));
        return countryConstraintList;
    }
}
