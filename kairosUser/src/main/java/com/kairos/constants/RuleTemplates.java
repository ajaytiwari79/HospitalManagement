package com.kairos.constants;

/**
 * Created by pavan on 7/3/18.
 */
public enum RuleTemplates{
    MAXIMUM_SHIFT_LENGTH("MAXIMUM_SHIFT_LENGTH","Maximum Shift Length","Checks that the shift length does not exceed a set value. Only shifts with an activity that adds time to the chosen balance types will be checked. If \"Check Time Rules\" is checked only those activities that have the setting \"Check Time Rules\" will be checked"),
    MINIMUM_SHIFT_LENGTH ("MINIMUM_SHIFT_LENGTH","Minimum shift length","Checks that the shift length is not below a set value. Only shifts with an activity that adds time to the chosen balance types will be checked. If \"Check Time Rules\" is checked only those activities that have the setting \"Check Time Rules\" will be checked."),
    MAXIMUM_NUMBER_OF_CONSECUTIVE_DAYS("MAXIMUM_NUMBER_OF_CONSECUTIVE_DAYS","Maximum number of consecutive days","Checks maximum number of consecutive scheduled days"),
    MINIMUM_REST_AFTER_CONSECUTIVE_DAYS_WORKED("MINIMUM_REST_AFTER_CONSECUTIVE_DAYS_WORKED","Minimum rest after consecutive days worked","Checks the least amount of continuous rest after a given number of consecutive days with shifts."),
    MAXIMUM_NIGHT_SHIFTS_LENGTH("MAXIMUM_NIGHT_SHIFTS_LENGTH","Maximum night shift’s length","Checks that the shift length for a night shift does not exceed a set value. Only shifts with an activity that adds time to the chosen balance types will be checked. If \"Check Time Rules\" is checked only those activities that have the setting \"Check Time Rules\" will be checked."),
    MINIMUM_NUMBER_OF_CONSECUTIVE_NIGHTS("MINIMUM_NUMBER_OF_CONSECUTIVE_NIGHTS","Minimum number of consecutive nights","Checks minimum number of consecutive nights."),
    MAXIMUM_NUMBER_OF_CONSECUTIVE_NIGHTS("MAXIMUM_NUMBER_OF_CONSECUTIVE_NIGHTS","Maximum number of consecutive nights","Checks maximum number of consecutive nights with night shifts."),
    MINIMUM_REST_AFTER_CONSECUTIVE_NIGHTS_WORKED("MINIMUM_REST_AFTER_CONSECUTIVE_NIGHTS_WORKED","Minimum rest after consecutive nights worked","Checks the least amount of continuous rest after a given number of consecutive nights with night shifts."),
    MAXIMUM_NUMBER_OF_WORK_NIGHTS("MAXIMUM_NUMBER_OF_WORK_NIGHTS","Maximum number of work nights","Checks that the number of nights worked in a set interval do not exceed the set value."),
    MINIMUM_NUMBER_OF_DAYS_OFF_PER_PERIOD("MINIMUM_NUMBER_OF_DAYS_OFF_PER_PERIOD","Minimum number of days off per period","Minimum amount of days off per interval. A day off is a non working day. A day off between\n" +
            "00:00-24:00. For persons that work night shifts the day the calculation of hours should be on the day that has the majority of hours."),
    MAXIMUM_AVERAGE_SCHEDULED_TIME_PER_WEEK_WITHIN_AN_INTERVAL("MAXIMUM_AVERAGE_SCHEDULED_TIME_PER_WEEK_WITHIN_AN_INTERVAL","Maximum average scheduled time per week within an interval","The rule checks that the average scheduled time per week for the specified balance type in the interval does not exceed the specified max time."),
    MAXIMUM_VETO_PER_PERIOD("MAXIMUM_VETO_PER_PERIOD","Maximum veto per period","Sets the maximum amount of veto time per period. The value is set in percent of the possible work time. The possible work time = work time - absence shifts."),
    NUMBER_OF_WEEKEND_SHIFTS_IN_A_PERIOD_COMPARED_TO_AVERAGE("NUMBER_OF_WEEKEND_SHIFTS_IN_A_PERIOD_COMPARED_TO_AVERAGE","Number of weekend shifts in a period compared to average.","This rule is to prevent persons who have already requested weekend shifts (more than the average an employee should do during the period) from being allocated more weekend shifts at optimisation."),
    CARE_DAYS_CHECK("CARE_DAYS_CHECK","Care days check","Care days check"),
    MINIMUM_DAILY_RESTING_TIME("MINIMUM_DAILY_RESTING_TIME","Minimum daily resting time","Checks the minimum continuous rest period in every arbitrary 24h interval."),
    MINIMUM_DURATION_BETWEEN_SHIFTS("MINIMUM_DURATION_BETWEEN_SHIFTS","Minimum duration between shifts","Minimum duration between shifts"),
    MINIMUM_WEEKLY_REST_PERIOD_FIXED_WEEKS("MINIMUM_WEEKLY_REST_PERIOD_FIXED_WEEKS","Minimum weekly rest period,fixed weeks","Sets the minimum consecutive rest for any 7-days interval. Uses the persons setting for week offset."),
    SHORTEST_AND_AVERAGE_DAILY_REST_FIXED_TIMES("SHORTEST_AND_AVERAGE_DAILY_REST_FIXED_TIMES","Shortest and average daily rest,fixed times","Daily rest is calculated for all working days within a period. Minimum X h rest and average 11 h rest per period. A working day is defined as having a shift on that day."),
    MAXIMUM_NUMBER_OF_SHIFTS_PER_INTERVAL("MAXIMUM_NUMBER_OF_SHIFTS_PER_INTERVAL","Maximum number of shifts per interval","Checks that the number of shifts in the specified interval do not exceed the indicated value. If this rule is added to an activities contract, shifts with that activity are checked"),
    MAXIMUM_SENIOR_DAYS_PER_YEAR("MAXIMUM_SENIOR_DAYS_PER_YEAR","Maximum senior days per year","Maximum amount of senior days per year"),
    MAXIMUM_TIME_BANK("MAXIMUM_TIME_BANK","Maximum time bank","Maximum time bank for staff"),
    MINIMUM_TIME_BANK("MINIMUM_TIME_BANK","Minimum time bank","Minimum time bank for staff"),

    MAXIMUM_SENIOR_DAYS_PER_YEAR_DAYS_UNIT("MAXIMUM_SENIOR_DAYS_PER_YEAR_DAYS_UNIT","Maximum senior days per year - Days Unit (Days)","Maximum senior days per year - Days Unit (Days)"),
    CHILD_CARE_DAYS_CHECK("CHILD_CARE_DAYS_CHECK","Care days check - Days Unit (Days)","Care days check - Days Unit (Days)"),
    BREAKS_IN_SHIFT("BREAKS_IN_SHIFT","Breaks in shift","Breaks in shift");




    private String templateType;
    private String name;
    private String description;

    RuleTemplates(String uniqueName,String generalName,String description){
        this.templateType = uniqueName;
        this.name = generalName;
        this.description = description;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static RuleTemplates getByTemplateType(String templateType) {
        for(RuleTemplates r: RuleTemplates.values()) {
            if(r.templateType.equals(templateType)) {
                return r;
            }
        }
        return null;
    }

}





