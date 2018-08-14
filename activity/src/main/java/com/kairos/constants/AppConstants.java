package com.kairos.constants;

/**
 * Created by prabjot on 22/11/16.
 */
public class AppConstants {

    private AppConstants() {
    }

    // Request methods
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";
    public static final String OPTIONS = "OPTIONS";

    //Swagger
    public static final String SWAGGER_UI_PATH = "/swagger-ui.html";
    public static final String SWAGGER_API_DOCS = "/v2/api-docs";
    public static final String SWAGGER_RESOURCES = "swagger-resources";

    public static final String WEBJARS = "webjars";

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String API_LOGIN_URL = "/api/v1/login";
    public static final String API_LOGOUT_URL = "/api/v1/logout";
    public static final String API_LOGIN_MOBILE_URL = "/api/v1/login/mobile";
    public static final String API_VERIFY_OTP = "/api/v1/login/verify/otp";
    public static final String API_LOGIN_MOBILE_NUMBER_URL = "/api/v1/login/mobile_number";
    public static final String API_COUNTRY_CODE_LIST = "/api/v1/country_code_list_for_login";
    public static final String API_TIME_CARE_SHIFTS = "/api/v1/time_care/getShifts";
    public static final String API_TIME_CARE_ACTIVITIES = "/api/v1/time_care/getWorkPlaces";
    public static final String API_KMD_CARE_CITIZEN = "/api/v1/external_citizen_import/citizen/preferences/";
    public static final String API_KMD_CARE_CITIZEN_GRANTS = "/api/v1/external_citizen_import/citizen/grants";
    public static final String API_KMD_CARE_CITIZEN_RELATIVE_DATA = "/api/v1/external_citizen_import/citizen/nextToKin";
    public static final String API_KMD_CARE_STAFF_SHIFTS = "/api/v1/external_citizen_import/citizen/unit/";

    public static final String API_CREDENTIAL_UPDATE_URL = "/api/v1/user/password";

    public static final String SCHEDULE = "SCHEDULE";
    public static final String TIMING = "TIMING";
    public static final String LEAVES = "LEAVES";

    public static final String FORWARD_SLASH = "/";


    //URL
    public static final String INDEX_PAGE = "index";
    public static final String UNAUTH_PAGE = "user/401";

    public static final String USER_PAGE = "user/user";
    public static final String ADMIN_PAGE = "admin/admin";
    public static final String LOGIN_PAGE = "auth/login";

    public static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";

    public static final String JAVA_MAIL_FILE = "classpath:java-mail.properties";

    public static final String HOST = "mail.server.host";
    public static final String PORT = "mail.server.port";
    public static final String PROTOCOL = "mail.server.protocol";
    public static final String MAIL_USERNAME = "mail.server.username";
    public static final String MAIL_AUTH = "mail.server.password";

    public static final String ALL_DAYS = "All Days";
    public static final String ALL_WEEKENDS = "All Weekdays";
    public static final String SATURDAY = "Saturday";
    public static final String SUNDAY = "Sunday";

    public static final String BODY = "Body";
    public static final String TO = "To";
    public static final String FROM = "From";

    public static final String OTP_MESSAGE = "Onetime password for registrartion is  ";
    public static final String VISITATOR = "VISITATOR";
    public static final String PLANNER = "PLANNER";
    public static final String TASK_GIVERS = "TASK_GIVERS";
    public static final String COUNTRY_ADMIN = "COUNTRY_ADMIN";
    public static final String UNIT_MANAGER = "UNIT_MANAGER";
    public static final String ACCESS_PAGE_PROPERTIES_FILE_PATH = "/page-id.properties";

    public static final String KAIROS = "@kairos.com";
    public static final String IMAGES_PATH = "/opt/kairos/images";
    public static final String ACTIVITY_TYPE_IMAGE_PATH = "/opt/kairos/images/activity_type/";
    public static final String HAS_ACCESS_OF_TABS = "HAS_ACCESS_OF_TABS";
    public static final String ORGANIZATION = "organization";
    public static final String GROUP = "group";
    public static final String TEAM = "team";
    public static final String ADMIN_EMAIL = "prabjot.singh@oodlestechnologies.com";
    public static final String ORGANIZATION_LABEL = "Organization";
    public static final String GROUP_LABEL = "Group";
    public static final String TEAM_LABEL = "Team";

    //Kettle commands
    public static final String KETTLE_TRANS_STATUS = "/kettle/transStatus/?name=GetAllWorkShiftsByWorkPlaceId&xml=y";
    public static final String KETTLE_EXECUTE_TRANS = "/kettle/executeTrans/?trans=";

    //Transformation Paths
    //development and production
    public static final String IMPORT_TIMECARE_SHIFTS_PATH = "/opt/infra/data-integration/GetAllWorkShiftsByWorkPlaceId.ktr";

    //Absence planner
    public static final String TASK_TYPE_MISSING_MESSAGE = "Please update taskType details of task just imported from TimeCare";
    public static final String STAFF_MISSING_MESSAGE = "Please update staff details of task just imported from TimeCare";
    public static final String TASK_TYPE_MISSING_STATUS = "Task Type Missing";
    public static final String STAFF_MISSING_STATUS = "Staff Missing";
    public static final String LOCATION_MISSING_MESSAGE = "Please update location of task just imported from TimeCare";
    public static final String LOCATION_MISSING_STATUS = "Location Missing";
    public static final String PARTIAL_ABSENCE_TAB = "partialAbsence";
    public static final String FULL_DAY_ABSENCE_TAB = "fullDayAbsence";
    public static final String PRESENCE_TAB = "presence";
    public static final String ALL_TAB = "all";
    public static final String ABSENT = "Absent";
    public static final String PRESENT = "Present";
    public static final String FULL_DAY = "Full day";
    public static final String PARTIALLY = "Partially";

    public static final String DAY = "Day";
    public static final String EVENING = "Evening";
    public static final String NIGHT = "Night";
    public static final int DAY_START_HOUR = 7;
    public static final int DAY_END_HOUR = 17;
    public static final int EVENING_START_HOUR = 17;
    public static final int EVENING_END_HOUR = 23;
    public static final int NIGHT_START_HOUR = 23;
    public static final int NIGHT_END_HOUR = 7;

    //KMD Nexus
    public static final String KMD_NEXUS_CLIENT_ID = "third_party_vendor";
    public static final String KMD_NEXUS_CLIENT_SECRET = "APNaYeGDVGOdjQf-jIFgU59tfkPux2mD6xGpbbAEuUc32ie8FRn7Y2cxcSayV6VafluS0pLu2TyhARFHtdHc-NQ";
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


    public static final String WEEK = "Week";
    public static final String MINUTES = "MINUTES";
    public static final String PERCENT = "PERCENT";
    public static final String DAILY = "daily";
    public static final String ANNUALLY = "annually";
    public static final String QUATERLY = "quaterly";
    public static final String QUARTER = "quater";
    public static final String YEAR = "year";
    public static final String WEEKLY = "weekly";
    public static final String MONTHLY = "monthly";
    public static final String REQUEST_TO_CREATE_NEW_UTILITY = "Request to create new utility";
    public static final String ABSENCE_PLANNING = "Absence Planning";
    public static final String UNIT = "unit";
    public static final String HUB = "hub";
    public static final String MODULE_3 = "module_3";
    public static final String TAB_45 = "tab_45";


    public static final String CAPACITY_PLANNING = "CAPACITY PLANNING";
    public static final String WEEKLY_ABSENCE_PLANNING = "WEEKLY ABSENCE PLANNING";

    public static final String REQUEST_PHASE_NAME = "REQUEST";
    public static final String REQUEST_PHASE_DESCRIPTION = "REQUEST PHASE";
    public static final String PUZZLE_PHASE_NAME = "PUZZLE";
    public static final String PUZZLE_PHASE_DESCRIPTION = "PUZZLE PHASE";
    public static final String CONSTRUCTION_PHASE_NAME = "CONSTRUCTION";
    public static final String CONSTRUCTION_PHASE_DESCRIPTION = "CONSTRUCTION PHASE";
    public static final String FINAL_PHASE_NAME = "FINAL";
    public static final String FINAL_PHASE_DESCRIPTION = "FINAL PHASE";
    public static final String DRAFT_PHASE_NAME = "DRAFT";
    public static final String DRAFT_PHASE_DESCRIPTION = "DRAFT PHASE";
    public static final String TENTATIVE_PHASE_NAME = "TENTATIVE";
    public static final String TENTATIVE_PHASE_DESCRIPTION = "TENTATIVE PHASE";
    public static final String REALTIME_PHASE_NAME = "REALTIME";
    public static final String REALTIME_PHASE_DESCRIPTION = "REALTIME PHASE";

    public static final String CONSTRUCTION_TO_FINAL_PHASE = "CONSTRUCTION_TO_FINAL_PHASE";
    public static final String REQUEST_TO_PUZZLE_PHASE = "REQUEST_TO_PUZZLE_PHASE";
    public static final String PUZZLE_TO_CONSTRUCTION_PHASE = "PUZZLE_TO_CONSTRUCTION_PHASE";
    public static final Long DURATION_IN_WEEK = 4L;
    public static final String COPY_OF = "copy of";

    public static final int REQUEST_PHASE_SEQUENCE = 1;

    //Scheduled Hours Calculation constants
    public static final String ENTERED_TIMES = "ENTERED_TIMES";
    public static final String WEEKLY_HOURS = "WEEKLY_HOURS";
    public static final String FIXED_TIME = "FIXED_TIME";
    public static final String FULL_DAY_CALCULATION = "FULL_DAY";
    public static final String ENTERED_MANUALLY = "ENTERED_MANUALLY";
    public static final String FULL_WEEK = "FULL_WEEK";
    //hours calculation types
    public static final String FULL_TIME_WEEKLY_HOURS_TYPE = "FULL_TIME_WEEKLY_HOURS";
    public static final String WEEKLY_HOURS_TYPE = "WEEKLY_HOURS_TYPE";
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


    // Default Unit Age Settings
    public static final int YOUNGER_AGE = 18;
    public static final int OLDER_AGE = 62;

    public static final String PAID_BREAK = "PAID BREAK";
    public static final String UNPAID_BREAK = "UNPAID BREAK";
    public static final String SUCCESS = "SUCCESS";
    public static final String NO_CONFLICTS = "NO CONFLICTS";
    public static final String BREAK = "BREAK";
    public static final String SHIFT = "SHIFT";
    public static final String OPENSHIFT_EMAIL_BODY = "Hi, You have been picked for openshift. fibonacii order- %s";
    public static final String OPENSHIFT_SUBJECT = "Open Shift";
    public static final String NOTIFY="NOTIFY";
    public static final String ASSIGN="ASSIGN";
    public static final String SHIFT_NOTIFICATION="SHIFT_NOTIFICATION";
    public static final String SHIFT_NOTIFICATION_MESSAGE="SHIFT_NOTIFICATION_MESSAGE";
    public static final String DECLINE="DECLINE";
    public static final String ASSIGN_OR_NOTIFY="ASSIGN_OR_NOTIFY";
    public static final String NORMAL_TIME="Normal Time";
    public static final String EXTRA_TIME="Extra Time";
    public static final String OVER_TIME="Overtime";

    //for Planning period name
    public static final String DATE_FORMET_STRING="dd.MMM.yyyy";
    //Phases
    public static final String REALTIME="REALTIME";
    public static final String TIME_AND_ATTENDANCE="TIME & ATTENDANCE";
    public static final String TENTATIVE="TENTATIVE";
    public static final String PAYROLL="PAYROLL";

    public static final String PRIORITY_GROUP1_NAME = "PRIORITY_GROUP1";



}
