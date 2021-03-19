package com.kairos.constants;

/**
 * Created by prabjot on 22/11/16.
 */
public class AppConstants {
    // Request methods
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";

    public static final String FORWARD_SLASH = "/";
    public static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";
    public static final String JAVA_MAIL_FILE = "classpath:java-mail.properties";

    public static final String HOST = "mail.server.host";
    public static final String PORT = "mail.server.port";
    public static final String PROTOCOL = "mail.server.protocol";
    public static final String MAIL_USERNAME = "mail.server.username";
    public static final String MAIL_AUTH = "mail.server.password";
    public static final String BODY = "Body";
    public static final String TO = "To";
    public static final String FROM = "From";

    public static final String IMAGES_PATH = "/opt/kairos/images";
    public static final String ACTIVITY_TYPE_IMAGE_PATH = "/opt/kairos/images/activity_type/";

    public static final String ORGANIZATION = "organization";

    public static final String TEAM = "team";

    //Kettle commands
    public static final String KETTLE_TRANS_STATUS = "/kettle/transStatus/?name=GetAllWorkShiftsByWorkPlaceId&xml=y";
    public static final String KETTLE_EXECUTE_TRANS = "/kettle/executeTrans/?trans=";

    //Transformation Paths
    //development and production
    public static final String IMPORT_TIMECARE_SHIFTS_PATH = "/opt/infra/data-integration/GetAllWorkShiftsByWorkPlaceId.ktr";

    //Absence planner
    public static final String TASK_TYPE_MISSING_MESSAGE = "Please update taskType details of task just imported from TimeCare";
    public static final String STAFF_MISSING_MESSAGE = "Please update staff details of task just imported from TimeCare";
    public static final String STAFF_MISSING_STATUS = "Staff Missing";
    public static final String PARTIAL_ABSENCE_TAB = "partialAbsence";
    public static final String FULL_DAY_ABSENCE_TAB = "fullDayAbsence";
    public static final String PRESENCE_TAB = "presence";
    public static final String ALL_TAB = "all";
    public static final String ABSENT = "Absent";
    public static final String PRESENT = "Present";
    public static final String FULL_DAY = "Full day";
    public static final String PARTIALLY = "Partially";

    //KMD Nexus
    public static final String KMD_NEXUS_CLIENT_ID = "third_party_vendor";
    public static final String KMD_NEXUS_CLIENT_SECRET = "APNaYeGDVGOdjQf-jIFgU59tfkPux2mD6xGpbbAEuUc32ie8FRn7Y2cxcSayV6VafluS0pLu2TyhARFHtdHc-NQ";
    public static final String SHIFT_S = "Shift(s)";
    public static String KMD_NEXUS_ACCESS_TOKEN = "";
    public static final String KMD_NEXUS_CUSTOMER = "unity";
    public static final String KMD_NEXUS_GRANT_TYPE = "password";
    public static final String KMD_NEXUS_USERNAME = "admin";
    public static final String KMD_NEXUS_ORGANIZATION = "Gruppe 1 (Hjemmepleje)";
    public static final String KMD_NEXUS_AUTH = "admin";
    public static final String KMD_NEXUS_AUTH_URL = "https://nexus-test.kmd.dk/auth/token";
    public static final String KMD_NEXUS_PATIENT_PREFERENCE = "https://test.avaleo.net/mobile/unity/v2/preferences/";
    public static final String KMD_NEXUS_PATIENT_PATHWAY_PREFERENCE = "https://test.avaleo.net/mobile/unity/v2/patient/%s/preferences/";
    public static final String KMD_NEXUS_PATIENT_FILTER = "https://test.avaleo.net/mobile/unity/v2/patients?filterId=496";
    public static final String KMD_NEXUS_PATIENT_EVENT_GRANTS = "https://test.avaleo.net/mobile/unity/v2/patientGrants/calendar/orderGrants/PATIENT_EVENT?patientId=%s&filterId=420";
    public static final String KMD_NEXUS_PATIENT_PATHWAY = "https://test.avaleo.net/mobile/unity/v2/patients/%s/pathways/references?filterId=";
    public static final String KMD_NEXUS_PATIENT_GRANTS = "https://test.avaleo.net/mobile/unity/v2/patientGrants/";
    public static final String KMD_NEXUS_PATIENT_ORDER_GRANTS = "https://test.avaleo.net/mobile/unity/v2/patientGrants/orderGrants/";
    public static final String KMD_NEXUS_PATIENT_RELATIVE_CONTACT = "https://test.avaleo.net/mobile/unity/v2/patients/%s/overview?relativeContactLimit=3";
    public static final String KMD_NEXUS_CALENDAR_STAFFS_SHIFT_FILTER = "https://test.avaleo.net/mobile/unity/v2/calendar/events/criteria/EVENT/%s";
    public static final String KMD_NEXUS_STAFFS_DETAILS = "https://nexus-test.kmd.dk/mobile/unity/v2/professionals/%s";
    public static final String API_CREATE_KMD_TASK_DEMAND = "/api/v1/task_demand/organization/{organizationId}/service/{subServiceId}";


    //Control panel unique keys
    public static final String IMPORT_TIMECARE_SHIFTS = "IMPORT_TIMECARE_SHIFTS";
    public static final String IMPORT_KMD_CITIZEN = "IMPORT_KMD_CITIZEN";
    public static final String IMPORT_KMD_CITIZEN_NEXT_TO_KIN = "IMPORT_KMD_CITIZEN_NEXT_TO_KIN";
    public static final String IMPORT_KMD_CITIZEN_GRANTS = "IMPORT_KMD_CITIZEN_GRANTS";
    public static final String IMPORT_KMD_STAFF_AND_WORKING_HOURS = "IMPORT_KMD_STAFF_AND_WORKING_HOURS";
    public static final String IMPORT_KMD_TASKS = "IMPORT_KMD_TASKS";
    public static final String DATA_SAVED_FROM_SERVICE = "Data saved from Service";
    public static final String REQUEST_FROM_TIME_CARE = "TIME_CARE";
    public static final String REQUEST_FROM_KMD = "KMD";
    public static final String MERGED_TASK_NAME = "Merged Task";
    public static final String CITIZEN_ID = "citizenId";


    public static final String EVERYDAY = "EVERYDAY";
    public static final String WEEK = "Week";
    public static final String MONTH = "Month";
    public static final String MINUTES = "MINUTES";
    public static final String PERCENT = "PERCENT";
    public static final String DAILY = "daily";
    public static final String ANNUALLY = "annually";
    public static final String QUATERLY = "quaterly";
    public static final String QUARTER = "quater";
    public static final String QUARTERS = "QUARTERS";
    public static final String YEAR = "year";
    public static final String WEEKLY = "weekly";
    public static final String MONTHLY = "monthly";
    public static final String CAMEL_CASE_MONTHLY = "Monthly";
    public static final String REQUEST_TO_CREATE_NEW_UTILITY = "Request to create new utility";
    public static final String ABSENCE_PLANNING = "Absence Planning";
    public static final String UNIT = "unit";
    public static final String HUB = "hub";
    public static final String MODULE_3 = "module_3";
    public static final String TAB_45 = "tab_45";



    public static final String REQUEST_PHASE_NAME = "Request";
    public static final String PUZZLE_PHASE_NAME = "Puzzle";
    public static final String CONSTRUCTION_PHASE_NAME = "Construction";
    public static final String DRAFT_PHASE_NAME = "Draft";
    public static final String COPY_OF = "copy of";

    public static final int REQUEST_PHASE_SEQUENCE = 1;

    //Scheduled Hours Calculation constants
    public static final String ENTERED_TIMES = "ENTERED_TIMES";
    public static final String WEEKLY_HOURS = "WEEKLY_HOURS";
    public static final String FIXED_TIME = "FIXED_TIME";
    public static final String ENTERED_MANUALLY = "ENTERED_MANUALLY";
    //hours calculation types
    public static final String TIMEBANK_ACCOUNT = "TIMEBANK_ACCOUNT";
    public static final String PAIDOUT_ACCOUNT = "PAID_OUT";

    //TimeCare date importing constants
    public static final String CALCULATED_TIME = "CalculatedTime";
    public static final String WEEKLY_WORK_TIME = "WeeklyWorkTime";
    public static final String FIXED_TIME_FOR_TIMECARE = "FixedTime";
    public static final String FULL_TIME_HOUR = "FullTimeHour";

    // Default Settings of period for unit
    public static final int PRESENCE_LIMIT_IN_YEAR = 1;
    public static final int ABSENCE_LIMIT_IN_YEAR = 2;
    public static final int ACTIVITY_CHANGED_FROM_PRESENCE_TO_ABSENCE = 1;
    public static final int ACTIVITY_CHANGED_FROM_ABSENCE_TO_PRESENCE = 2;


    // Staff Questionnaire
    public static final String QUESTIONNAIE_NAME_PREFIX = "Questionnaire";
    public static final int MAX_ONE_ACTIVITY_PER_SHIFT = 10;



    //RuleTemplate
    public static final String MONTHS = "MONTHS";
    public static final String WEEKS = "WEEKS";
    public static final String DAYS = "DAYS";
    public static final String YEARS = "YEARS";
    public static final int MINIMUM_WTA_RULE_TEMPLATE_COUNTER = 0;
    public static final int MINIMUM_VALUE = 0;


    // Default Unit Age Settings
    public static final int YOUNGER_AGE = 18;
    public static final int OLDER_AGE = 62;

    public static final String PAID_BREAK = "PAID_BREAK";
    public static final String UNPAID_BREAK = "UNPAID_BREAK";
    public static final String NO_CONFLICTS = "NO CONFLICTS";
    public static final String BREAK = "BREAK";
    public static final String SHIFT = "Shift";
    public static final String OPENSHIFT_EMAIL_BODY = "Hi, You have been picked for openshift. fibonacii order- %s, Accumulated timebank- %s, Delta Weeklytimebank- %s," +
            " PlannedHoursweek- %s";
    public static final String SHIFT_EMAIL_BODY = "Hi, %s, \n shift %s  will start on %s at %s .";
    public static final String ABSENCE_ACTIVITY_REMINDER_EMAIL_BODY = "Please plan  activity %s before it expires on %s.\n Count left is %s.";
    public static final String OPENSHIFT_SUBJECT = "Open Shift";
    public static final String SHIFT_NOTIFICATION="Shift Reminder";
    public static final String ACTIVITY_REMINDER="Activity Reminder";
    public static final String DESCRIPTION="description";
    public static final String SHIFT_NOTIFICATION_MESSAGE="SHIFT_NOTIFICATION_MESSAGE";
    public static final String NORMAL_TIME="Normal Time";
    public static final String EXTRA_TIME="Extra Time";
    //for Planning period name
    public static final String DATE_FORMET_STRING="dd.MMM.yyyy";
    //Phases
    public static final String REALTIME="Realtime";
    public static final String TIME_AND_ATTENDANCE="Time & Attendance";
    public static final String TENTATIVE="Tentative";
    public static final String PAYROLL="Payroll";
    public static final String PRIORITY_GROUP1_NAME = "PRIORITY_GROUP1";
    public static final int STAFF_GRACE_PERIOD_DAYS=2;
    public static final int MANAGEMENT_GRACE_PERIOD_DAYS=2;
    public static final String SCHEDULER_TO_ACTIVITY_QUEUE_TOPIC= "SchedulerToActivityQueue";

    public static final String ACTIVITY_TO_SCHEDULER_JOB_QUEUE_TOPIC="activityToSchedulerJobQueue";
    public static final String ACTIVITY_TO_SCHEDULER_LOGS_QUEUE_TOPIC="activityToSchedulerLogQueue";
    public static final long ONE_DAY_MINUTES=1439;
    public static final String OVERSTAFFING="OVERSTAFFING";
    public static final String UNDERSTAFFING="UNDERSTAFFING";
    public static final String BALANCED="BALANCED";
    public static final String LOW_ACTIVITY_RANK="LOW ACTIVITY RANK";
    public static final float VETO_BLOCKING_POINT=1;
    public static final float STOP_BRICK_BLOCKING_POINT=0.5f;

    //Time Type Default Colors - Green for Working and Pink for Non-Working
    public static final String WORKING_TYPE_COLOR = "#7ddc7d";
    public static final String NON_WORKING_TYPE_COLOR = "#f7c8ec";
    public static final String IS_BROKEN = " is broken";
    public static final long ONE_HOUR_MINUTES=60;
    public static final long SHIFT_LEAST_GRANULARITY =15;

    //KPI  BAR chart property
    public static final String HOURS = "Hours";
    public static final String CONTRACTUAL_HOURS = "Contractual Hours";
    public static final String PLANNED_HOURS = "Planned Hours";
    public static final String TIME_BANK ="time bank";
    public static final String STAFF = "Staff";
    public static final String STAFF_LIST = "Staff Name";

    public static final String DATE = "Dates";
    public static final String LABEL = "date";
    public static final String VALUE_FIELD = "value";
    //for ContractualAndPlannedHoursCalculationService
    public static final String BAR_YAXIS = "barValue";
    public static final String LINE_FIELD = "lineValue";
    public static final String KPI_DEFAULT_COLOR = "#b7b7b7";
    public static final String SEND_GRID_API_KEY = "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";

    //for use timeslot in timeslot kpi
    public static final String NIGHT = "Night";
    public static final String EVENING = "Evening";
    public static final String DAY = "Day";

    //Timebank Ruletemplate not valid reason
    public static final String DAYTYPE_IS_NOT_VALID = "Daytype is not valid";
    public static final String ACCOUNT_TYPE_IS_NOT_VALID = "Account type is not valid";
    public static final String ACTIVITY_IS_NOT_VALID = "Activity is not valid";
    public static final String PHASE_IS_NOT_VALID = "phase is not valid";
    public static final String EMPLOYMENT_IS_NOT_VALID = "Employment is not valid";
    public static final String BLANK_STRING="";
    public static final String DELETED="deleted";

    //activity status mail
    public static final String MAIL_SUBJECT ="Activiy Status";

    public static final String UNCATEGORIZED = "Uncategorized";


    //todo status color code
    public static final String REQUESTED_COLOR_CODE ="#657cde";
    public static final String APPROVE_COLOR_CODE="#4eb98d";
    public static final String DISAPPROVE_COLOR_CODE="#f7665e";
    public static final String PENDING_COLOR_CODE="#f5f591";

    public static final String REQUESTED ="Requested";
    public static final String APPROVED ="Approved";
    public static final String DISAPPROVED ="Disapproved";
    public static final String PENDING="Pending";


    public static final String LEVEL = "level";
    public static final String SKILL_ID = "skillId";

    public static final String PLANNING_PERIOD_NAME="PP_";
    public static final int NOT_VALID_VALUE = -1;
    public static final String PLANNING_PERIOD="Planning_Period";

    public static final String TIME_SLOT_SET_NAME = "Time slot 1";
    public static final int DAY_START_HOUR = 7;
    public static final int DAY_END_HOUR = 17;
    public static final int EVENING_START_HOUR = 17;
    public static final int EVENING_END_HOUR = 23;
    public static final int NIGHT_START_HOUR = 23;
    public static final int NIGHT_END_HOUR = 7;

    public static final String ACTIVITY_RULES_ACTIVITY_TAB = "activity.activityRulesSettings";
    public static final String ACTIVITY_INDIVIDUAL_POINTS_ACTIVITY_TAB = "activity.activityIndividualPointsSettings";
    public static final String UNIT_ID = "unitId";
    public static final String TIME_TYPE = "time_Type";
    public static final String BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID = "activityBalanceSettings.timeTypeId";
    public static final String TIME_TYPE_INFO = "timeTypeInfo";
    public static final String IS_PARENT_ACTIVITY = "isParentActivity";
    public static final String ORGANIZATION_TYPES = "organizationTypes";
    public static final String ORGANIZATION_SUB_TYPES = "organizationSubTypes";
    public static final String STATE = "state";
    public static final String TAGS_DATA = "tags_data";
    public static final String PARENT_ID = "parentId";
    public static final String GENERAL_ACTIVITY_TAB = "activityGeneralSettings";
    public static final String ACTIVITY_PRIORITY_ID = "activityPriorityId";
    public static final String TIME_TYPE1 = "timeType";
    public static final String RULES_ACTIVITY_TAB = "activityRulesSettings";
    public static final String TIME_TYPE_ALLOW_CHILD_ACTIVITIES = "timeType.allowChildActivities";
    public static final String TIME_TYPE_SICKNESS_SETTING="timeType.sicknessSettingValid";
    public static final String ALLOW_CHILD_ACTIVITIES = "allowChildActivities";
    public static final String SICKNESS_SETTING = "sicknessSettingValid";
    public static final String CHILD_ACTIVITY_IDS = "childActivityIds";
    public static final String APPLICABLE_FOR_CHILD_ACTIVITIES = "applicableForChildActivities";
    public static final String TIME_CALCULATION_ACTIVITY_TAB_METHOD_FOR_CALCULATING_TIME = "activityTimeCalculationSettings.methodForCalculatingTime";
    public static final String ACTIVITIES = "activities";
    public static final String CTA_AND_WTA_SETTINGS_ACTIVITY_TAB = "activityCTAAndWTASettings";
    public static final String GENERAL_ACTIVITY_TAB_CATEGORY_ID = "activityGeneralSettings.categoryId";
    public static final String TIME_TYPE_TIME_TYPES = "timeType.timeTypes";
    public static final String BALANCE_SETTINGS_ACTIVITY_TAB = "activityBalanceSettings";
    public static final String EXPERTISES = "expertises";
    public static final String SKILL_ACTIVITY_TAB = "activitySkillSettings";
    public static final String BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO = "activityBalanceSettings.timeTypeInfo";
    public static final String TIME_TYPE_INFO_LABEL = "timeTypeInfo.label";
    public static final String GENERAL_ACTIVITY_TAB_START_DATE = "activityGeneralSettings.startDate";
    public static final String GENERAL_ACTIVITY_TAB_END_DATE = "activityGeneralSettings.endDate";
    public static final String ACTIVITY_ID = "activity._id";
    public static final String ACTIVITY_NAME = "activity.name";
    public static final String ACTIVITY_COUNTRY_ID = "activity.countryId";
    public static final String ACTIVITY_EXPERTISES = "activity.expertises";
    public static final String EMPLOYMENT_TYPES = "employmentTypes";
    public static final String ACTIVITY_EMPLOYMENT_TYPES = "activity.employmentTypes";
    public static final String ACTIVITY_STATE = "activity.state";
    public static final String ACTIVITY_UNIT_ID = "activity.unitId";
    public static final String ACTIVITY_PARENT_ID = "activity.parentId";
    public static final String ACTIVITY_IS_PARENT_ACTIVITY = "activity.isParentActivity";
    public static final String ACTIVITY_GENERAL_ACTIVITY_TAB = "activity.activityGeneralSettings";
    public static final String ACTIVITY_BALANCE_SETTINGS_ACTIVITY_TAB = "activity.activityBalanceSettings";
    public static final String INDIVIDUAL_POINTS_ACTIVITY_TAB = "activityIndividualPointsSettings";
    public static final String TIME_CALCULATION_ACTIVITY_TAB = "activityTimeCalculationSettings";
    public static final String ACTIVITY_TIME_CALCULATION_ACTIVITY_TAB = "activity.activityTimeCalculationSettings";
    public static final String NOTES_ACTIVITY_TAB = "activityNotesSettings";
    public static final String ACTIVITY_NOTES_ACTIVITY_TAB = "activity.activityNotesSettings";
    public static final String COMMUNICATION_ACTIVITY_TAB = "activityCommunicationSettings";
    public static final String ACTIVITY_COMMUNICATION_ACTIVITY_TAB = "activity.activityCommunicationSettings";
    public static final String BONUS_ACTIVITY_TAB = "activityBonusSettings";
    public static final String ACTIVITY_BONUS_ACTIVITY_TAB = "activity.activityBonusSettings";
    public static final String ACTIVITY_SKILL_ACTIVITY_TAB = "activity.activitySkillSettings";
    public static final String OPTA_PLANNER_SETTING_ACTIVITY_TAB = "activityOptaPlannerSetting";
    public static final String ACTIVITY_OPTA_PLANNER_SETTING_ACTIVITY_TAB = "activity.activityOptaPlannerSetting";
    public static final String LOCATION_ACTIVITY_TAB = "activityLocationSettings";
    public static final String ACTIVITY_CTA_AND_WTA_SETTINGS_ACTIVITY_TAB = "activity.activityCTAAndWTASettings";
    public static final String ACTIVITY_LOCATION_ACTIVITY_TAB = "activity.activityLocationSettings";
    public static final String PHASE_SETTINGS_ACTIVITY_TAB = "activityPhaseSettings";
    public static final String ACTIVITY_PHASE_SETTINGS_ACTIVITY_TAB = "activity.activityPhaseSettings";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_ID = "categoryId";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String CHILD_ACTIVITIES = "childActivities";
    public static final String COMPOSITE_TIME_TYPE_INFO = "compositeTimeTypeInfo";
    public static final String CHILD_ACTIVITIES_ID = "childActivities._id";
    public static final String PHASE_TEMPLATE_VALUES_ACTIVITY_SHIFT_STATUS_SETTINGS = "phaseTemplateValues.activityShiftStatusSettings";
    public static final String ACTIVITY_SHIFT_STATUS_SETTINGS = "activityShiftStatusSettings";
    public static final String PHASE_ID = "phaseId";
    public static final String ACTIVITY_PRIORITY = "activityPriority";
    public static final String CHILD_ACTIVITY_PRIORITY = "childActivityPriority";
    public static final String COUNTRY_ID = "countryId";
    public static final String PARENT_ACTIVITY_ID = "parentActivityId";
    public static final String NAME = "name";
    public static final String UNDERSCORE_ID = "_id";
    public static final String COUNTRY_PARENT_ID = "countryParentId";
    public static final String ID = "id";
    public static final String DOLLAR_ID = "$id";
    public static final String BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE = "activityBalanceSettings.timeType";
    public static final String TRANSLATIONS="translations";
    public static final String ACTIVITY_PRIORITY_SEQUENCE="activityPriority.sequence";
    public static final String ACTIVITY_SEQUENCE ="activitySequence";
    public static final String TIME_SLOT_SET = "timeSlotSet";

}
