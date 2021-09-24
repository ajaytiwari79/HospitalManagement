package com.kairos.enums.task_type;

/**
 * Created by prabjot on 7/10/16.
 */
public enum TaskTypeEnum {
    ;

    TaskTypeEnum() {
    }

    public enum CostType {

        FIXED_COST("Fixed cost"), HOURLY_COST("Hourly cost");

        public String value;

        CostType(String value) {
            this.value = value;
        }

        public static CostType getByValue(String value) {
            for (CostType type : CostType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum TaskTypeCreation {
        CLIENTS("Clients"), RELATIVES("Relatives"), CONTACT_PERSONS("Contract_Persons"),
        MANAGERS("Managers"), PLANNERS("Planners"), VISITATORS("Visitators"), ADMINS("Admins");

        public String value;

        TaskTypeCreation(String value) {
            this.value = value;
        }

        public static TaskTypeCreation getByValue(String value) {
            for (TaskTypeCreation type : TaskTypeCreation.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum TaskTypeStatus {
        ACTIVE("Active"), INACTIVE("Inactive");
        public String value;

        TaskTypeStatus(String value) {
            this.value = value;
        }

        public static TaskTypeStatus getByValue(final String value) {
            for (TaskTypeStatus taskTypeStatus : TaskTypeStatus.values()) {
                if (taskTypeStatus.value.equals(value)) {
                    return taskTypeStatus;
                }
            }
            return null;
        }
    }

    public enum GenderRestrictions {

        MEN("Men"), WOMEN("Women"), BOTH("Both");
        public String value;

        GenderRestrictions(String value) {
            this.value = value;
        }

        public static GenderRestrictions getByValue(final String value) {
            for (GenderRestrictions genderRestrictions : GenderRestrictions.values()) {
                if (genderRestrictions.value.equals(value)) {
                    return genderRestrictions;
                }
            }
            return null;
        }
    }

    public enum PreplanStatus {
        NORMAL("Normal"), CONFIRMED("Confirmed"), FIXED("Fixed");

        public String value;

        PreplanStatus(String value) {
            this.value = value;
        }
    }

    public enum ShiftPlanningPhase {
        SIMULATION("Simulation"), LONG_TERM_ABSENCE("Long_term_absence"), REQUEST("Request"), DRAFT("Draft"), CONSTRUCTION("Construction"), FINAL("Final"), SHORT_TERM("Short_term"), PAST("Past"), PAYROLL("Payroll");

        public String value;

        ShiftPlanningPhase(String value) {
            this.value = value;
        }

        public static ShiftPlanningPhase getByValue(final String value) {
            for (ShiftPlanningPhase phase : ShiftPlanningPhase.values()) {
                if (phase.value.equals(value)) {
                    return phase;
                }
            }
            return null;
        }
    }

    public enum Points {
        FIXED_POINT_PER_TIME_SLOT("Fixed_point_per_timeslot"), SUPPLY_DEMAND("Supply_demand"), FIXED_POINTS("Fixed_points"), GAMIFICATION_MODEL("Gamification_model");
        public String value;

        Points(String value) {
            this.value = value;
        }

        public static Points getByValue(final String value) {
            for (Points point : Points.values()) {
                if (point.value.equals(value)) {
                    return point;
                }
            }
            return null;
        }
    }

    public enum Resource {
        VAN("Van"), CAR("Car"), MINIVAN("Minivan"), BICYCLE("Bicycle"), SMALL_BUS("Small_bus");
        public String value;

        Resource(String value) {
            this.value = value;
        }

        public static Resource getByValue(final String value) {
            for (Resource resource : Resource.values()) {
                if (resource.value.equals(value)) {
                    return resource;
                }
            }
            return null;
        }
    }

    public enum TaskTypeStaff {
        FIXED_EMPLOYEES("Fixed_employees"), HOURLY_PAID_EMPLOYEES("Hourly_paid_employees"), SUB_CONTRACTORS("Sub_contractors"), VOLUNTEERS("Volunteers"), TASKERS("Taskers"), PREFERRED_EMPLOYEES("Preferred_employees"),
        EXCLUDED_EMPLOYEES("Excluded_employees"), FIXED("Fixed"), PREFERRED_1_EMPLOYEES("Preferred1_employees"), PREFERRED_2_EMPLOYEES("Preferred2_employees"), EXPERIENCE_RESTRICTIONS("Experience_restrictions"),
        ALLOWED_EMPLOYEES("Allowed_employees");

        public String value;

        TaskTypeStaff(String value) {
            this.value = value;
        }

        public static TaskTypeStaff getByValue(String value) {
            for (TaskTypeStaff taskTypeStaff : TaskTypeStaff.values()) {
                if (taskTypeStaff.value.equals(value)) {
                    return taskTypeStaff;
                }
            }
            return null;
        }
    }

    public enum TaskTypeDays {
        MONDAYS("Mondays"), TUESDAYS("Tuesdays"), WEDNESDAYS("Wednesdays"), THURSDAYS("Thursdays"), FRIDAYS("Fridays"),
        SATURDAYS("Saturdays"), SUNDAYS("Sundays"), FULL_PUBLIC_HOLIDAYS("Full_public_holidays"), OUTSIDE_OPENING_HOURS("Outside_opening_hours_of_organization"),
        HALF_PUBLIC_HOLIDAYS("Half_public_holidays");

        public String value;

        TaskTypeDays(String value) {
            this.value = value;
        }

        public static TaskTypeDays getByValue(String value) {
            for (TaskTypeDays taskTypeDay : TaskTypeDays.values()) {
                if (taskTypeDay.value.equals(value)) {
                    return taskTypeDay;
                }
            }
            return null;
        }

    }

    public enum CauseGroup {
        PICK_UP("Pick_up"), DELIVERY("Delivery"), TRANSPORTATION("Transportation"), ASSESSOR("Assessor"), CONSULTING("Consulting"), FULL_SERVICE("Full_service"),
        PERSONAL_ASSISTANCE("Personal_assistance"), VIRTUAL("Virtual"), WARRANTY("Warranty"), OFFICE("Office"), TRAINING("Training"), QUALITY("Quality"),
        PERSONAL("Personal"), MAINTENANCE("Maintenance"), PROJECT("Project"), MEETING("Meeting"), BREAK("Break");

        public String value;

        CauseGroup(String value) {
            this.value = value;
        }

        public static CauseGroup getByValue(String value) {
            for (CauseGroup group : CauseGroup.values()) {
                if (group.value.equals(value)) {
                    return group;
                }
            }
            return null;
        }


    }

    public enum SequenceGroup {
        FIRST_CLIENT("First_client"), FRONT_CLIENT("Front_client"), MIDDLE_CLIENT("Middle_client"), BACK_CLIENT("Back_client"), LAST_CLIENT("Last_client"),
        FRONT_OR_BACK("Front_or_back");
        public String value;

        SequenceGroup(String value) {
            this.value = value;
        }

        public static SequenceGroup getByValue(String value) {
            for (SequenceGroup group : SequenceGroup.values()) {
                if (group.value.equals(value)) {
                    return group;
                }
            }
            return null;
        }


    }

    public enum TimeTypes {
        PRE_PROCESSING_TIME("Pre_processing_time"), POST_PROCESSING_TIME("Post_processing_time"), SETUP_TIME("Setup_time");
        public String value;

        TimeTypes(String value) {
            this.value = value;
        }

        public static TimeTypes getByValue(String value) {
            for (TimeTypes timeTypes : TimeTypes.values()) {
                if (timeTypes.value.equals(value)) {
                    return timeTypes;
                }
            }
            return null;
        }
    }

    public enum DurationType {
        YES("Yes"), NO("No"), VISITATION_ONLY("Visitation_only");
        public String value;

        DurationType(String value) {
            this.value = value;
        }

        public static DurationType getByValue(String value) {
            for (DurationType durationType : DurationType.values()) {
                if (durationType.value.equals(value)) {
                    return durationType;
                }
            }
            return null;
        }
    }

    public enum TaskTypeCount {
        POSITIVE("Positive"),NEGATIVE("Negative");
        public String value;

        TaskTypeCount(String value) {
            this.value = value;
        }

        public static TaskTypeCount getByValue(String value) {
            for (TaskTypeCount taskTypeCount : TaskTypeCount.values()) {
                if (taskTypeCount.value.equals(value)) {
                    return taskTypeCount;
                }
            }
            return null;
        }
    }

    public enum TaskTypeInclude {
        TIME_BANK("Time bank"),ANNUAL_DUTY_CALCULATION("Annual duty calculation");
        public String value;

        TaskTypeInclude(String value) {
            this.value = value;
        }

        public static TaskTypeInclude getByValue(String value) {
            for (TaskTypeInclude taskTypeInclude : TaskTypeInclude.values()) {
                if (taskTypeInclude.value.equals(value)) {
                    return taskTypeInclude;
                }
            }
            return null;
        }

    }

    public enum TaskTypeDate {
        WEEKDAYS("Weekdays"),SATURDAYS("Saturdays"),SUNDAYS("Sundays"),PUBLIC_HOLIDAYS("Public holidays"),HALF_PUBLIC_HOLIDAYS("Half public holidays"),
        APPROVED_VACATION_PERIOD("Approved vacation period"),COMPENSATION_DAYS("Compensation days");
        public String value;

        TaskTypeDate(String value) {
            this.value = value;
        }

        public static TaskTypeDate getByValue(String value) {
            for (TaskTypeDate taskTypeDate : TaskTypeDate.values()) {
                if (taskTypeDate.value.equals(value)) {
                    return taskTypeDate;
                }
            }
            return null;
        }
    }

    public enum TaskOriginator {
        TIMECARE("TimeCare"),PRE_KAIROS("Pre Kairos"),KAIROS("Kairos"),PRE_PLANNING("Pre Planning"),ACTUAL_PLANNING("Actual planner");

        public String value;

        TaskOriginator(String value) {
            this.value = value;
        }

        public static TaskOriginator getByValue(String value) {
            for (TaskOriginator taskOriginator : TaskOriginator.values()) {
                if (taskOriginator.value.equals(value)) {
                    return taskOriginator;
                }
            }
            return null;
        }
    }

    public enum TaskTypeSlaDay {
        MONDAY("Monday"),TUESDAY("Tuesday"),WEDNESDAY("Wednesday"),THURSDAY("Thursday"),FRIDAY("Friday"),SATURDAY("Saturday"),SUNDAY("Sunday"),PUBLIC_HOLIDAY("Public Holiday");
        public String value;

        TaskTypeSlaDay(String value) {
            this.value = value;
        }

        public static TaskTypeSlaDay getByValue(String value) {
            for (TaskTypeSlaDay taskTypeSlaDay : TaskTypeSlaDay.values()) {
                if (taskTypeSlaDay.value.equals(value)) {
                    return taskTypeSlaDay;
                }
            }
            return null;
        }
    }


}
