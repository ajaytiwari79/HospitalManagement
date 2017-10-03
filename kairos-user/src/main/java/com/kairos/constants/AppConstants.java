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
    public static final String API_KMD_CARE_CITIZEN = "/api/v1/kmdNexus/citizen/preferences/{unitId}";
    public static final String KMD_CARE_CITIZEN_URL = "/api/v1/kmdNexus/citizen/preferences/";
    public static final String API_KMD_CARE_CITIZEN_GRANTS = "/api/v1/kmdNexus/citizen/grants";
    public static final String API_KMD_CARE_CITIZEN_RELATIVE_DATA = "/api/v1/kmdNexus/citizen/nextToKin";

    public static final String API_KMD_CARE_STAFF_SHIFTS = "/api/v1/kmdNexus/citizen/unit/{unitId}/getShifts/{filterId}";
    public static final String API_KMD_CARE_TIME_SLOTS = "/api/v1/kmdNexus/citizen/unit/{unitId}/getTimeSlots";
    public static final String API_KMD_CARE_URL= "/api/v1/kmdNexus/citizen/unit/";
    public static final String API_TIME_SLOTS_NAME = "/api/v1/organization/{organizationId}/unit/{unitId}/time_slot_name";

    public static final String API_CREDENTIAL_UPDATE_URL = "/api/v1/user/password";

    public static final String SCHEDULE = "SCHEDULE";
    public static final String TIMING = "TIMING";
    public static final String LEAVES = "LEAVES";


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

    //Absence planning
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
    public static final String KMD_NEXUS_GET_TIME_SLOTS = "https://test.avaleo.net/mobile/unity/v2/shifts";

    //Control panel unique keys
    public static final String IMPORT_TIMECARE_SHIFTS = "IMPORT_TIMECARE_SHIFTS";
    public static final String IMPORT_KMD_CITIZEN = "IMPORT_KMD_CITIZEN";
    public static final String IMPORT_KMD_CITIZEN_NEXT_TO_KIN = "IMPORT_KMD_CITIZEN_NEXT_TO_KIN";
    public static final String IMPORT_KMD_CITIZEN_GRANTS = "IMPORT_KMD_CITIZEN_GRANTS";
    public static final String IMPORT_KMD_STAFF_AND_WORKING_HOURS = "IMPORT_KMD_STAFF_AND_WORKING_HOURS";
    public static final String IMPORT_KMD_TASKS = "IMPORT_KMD_TASKS";
    public static final String IMPORT_KMD_TIME_SLOTS = "IMPORT_KMD_TIME_SLOTS";
    public static final String DATA_SAVED_FROM_SERVICE = "Data saved from Service";
    public static final String REQUEST_FROM_TIME_CARE = "TIME_CARE";
    public static final String REQUEST_FROM_KMD = "KMD";
    public static final String MERGED_TASK_NAME = "Merged Task";
    public static final String CITIZEN_ID = "citizenId";


    public static final String TEMPLATE1 = "TEMPLATE1";
    public static final String TEMPLATE1_NAME = "Maximum Shift Length";
    public static final String TEMPLATE1_DESCRIPTION = "Checks that the shift length does not exceed a set value. Only shifts with an activity that adds time to the chosen balance types will be checked. If \"Check Time Rules\" is checked only those activities that have the setting \"Check Time Rules\" will be checked";

    public static final String TEMPLATE2 = "TEMPLATE2";
    public static final String TEMPLATE2_NAME = "Minimum shift length";
    public static final String TEMPLATE2_DESCRIPTION = "Checks that the shift length is not below a set value. Only shifts with an activity that adds time to the chosen balance types will be checked. If \"Check Time Rules\" is checked only those activities that have the setting \"Check Time Rules\" will be checked.";

    public static final String TEMPLATE3 = "TEMPLATE3";
    public static final String TEMPLATE3_NAME = "Maximum number of consecutive days";
    public static final String TEMPLATE3_DESCRIPTION = "Checks maximum number of consecutive scheduled days";

    public static final String TEMPLATE4 = "TEMPLATE4";
    public static final String TEMPLATE4_NAME = "Minimum rest after consecutive days worked";
    public static final String TEMPLATE4_DESCRIPTION = "Checks the least amount of continuous rest after a given number of consecutive days with shifts.";

    public static final String TEMPLATE5 = "TEMPLATE5";
    public static final String TEMPLATE5_NAME = "Maximum night shift’s length";
    public static final String TEMPLATE5_DESCRIPTION = "Checks that the shift length for a night shift does not exceed a set value. Only shifts with an activity that adds time to the chosen balance types will be checked. If \"Check Time Rules\" is checked only those activities that have the setting \"Check Time Rules\" will be checked.";

    public static final String TEMPLATE6 = "TEMPLATE6";
    public static final String TEMPLATE6_NAME = "Minimum number of consecutive nights";
    public static final String TEMPLATE6_DESCRIPTION = "Checks minimum number of consecutive nights.";

    public static final String TEMPLATE7 = "TEMPLATE7";
    public static final String TEMPLATE7_NAME = " Maximum number of consecutive nights";
    public static final String TEMPLATE7_DESCRIPTION = "Checks maximum number of consecutive nights with night shifts.";

    public static final String TEMPLATE8 = "TEMPLATE8";
    public static final String TEMPLATE8_NAME = "Minimum rest after consecutive nights worked";
    public static final String TEMPLATE8_DESCRIPTION = "Checks the least amount of continuous rest after a given number of consecutive nights with\n" +
            "night shifts.";

    public static final String TEMPLATE9 = "TEMPLATE9";
    public static final String TEMPLATE9_NAME = "Maximum number of work nights";
    public static final String TEMPLATE9_DESCRIPTION = "Checks that the number of nights worked in a set interval do not exceed the set value.";

    public static final String TEMPLATE10 = "TEMPLATE10";
    public static final String TEMPLATE10_NAME = "Minimum number of days off per period";
    public static final String TEMPLATE10_DESCRIPTION = "Minimum amount of days off per interval. A day off is a non working day. A day off between\n" +
            "00:00-24:00. For persons that work night shifts the day the calculation of hours should be on the day that has the majority of hours.";

    public static final String TEMPLATE11 = "TEMPLATE11";
    public static final String TEMPLATE11_NAME = "Maximum average scheduled time per week within an interval";
    public static final String TEMPLATE11_DESCRIPTION = "The rule checks that the average scheduled time per week for the specified balance type in the interval does not exceed the specified max time.";

    public static final String TEMPLATE12 = "TEMPLATE12";
    public static final String TEMPLATE12_NAME = "Maximum veto per period";
    public static final String TEMPLATE12_DESCRIPTION = "Sets the maximum amount of veto time per period. The value is set in percent of the possible work time. The possible work time = work time - absence shifts.";

    public static final String TEMPLATE13 = "TEMPLATE13";
    public static final String TEMPLATE13_NAME = "Number of weekend shifts in a period compared to average.";
    public static final String TEMPLATE13_DESCRIPTION = "This rule is to prevent persons who have already requested weekend shifts (more than the average an employee should do during the period) from being allocated more weekend shifts at optimisation.";

    public static final String TEMPLATE14 = "TEMPLATE14";
    public static final String TEMPLATE14_NAME = "Care days check";
    public static final String TEMPLATE14_DESCRIPTION = "Care days check.";

    public static final String TEMPLATE15 = "TEMPLATE15";
    public static final String TEMPLATE15_NAME = "Minimum daily resting time";
    public static final String TEMPLATE15_DESCRIPTION = "Checks the minimum continuous rest period in every arbitrary 24h interval.";

    public static final String TEMPLATE16 = "TEMPLATE16";
    public static final String TEMPLATE16_NAME = "Minimum duration between shifts";
    public static final String TEMPLATE16_DESCRIPTION = "Minimum duration between shifts.";

    public static final String TEMPLATE17 = "TEMPLATE17";
    public static final String TEMPLATE17_NAME = "Minimum weekly rest period,fixed weeks";
    public static final String TEMPLATE17_DESCRIPTION = "Sets the minimum consecutive rest for any 7-days interval. Uses the persons setting for week offset.";

    public static final String TEMPLATE18 = "TEMPLATE18";
    public static final String TEMPLATE18_NAME = "Shortest and average daily rest,fixed times";
    public static final String TEMPLATE18_DESCRIPTION = "Daily rest is calculated for all working days within a period. Minimum X h rest and average 11 h rest per period. A working day is defined as having a shift on that day.";

    public static final String TEMPLATE19 = "TEMPLATE19";
    public static final String TEMPLATE19_NAME = "Maximum number of shifts per interval";
    public static final String TEMPLATE19_DESCRIPTION = "Checks that the number of shifts in the specified interval do not exceed the indicated value. If this rule is added to an activities contract, shifts with that activity are checked";

    public static final String TEMPLATE20 = "TEMPLATE20";
    public static final String TEMPLATE20_NAME = "Maximum senior days per year";
    public static final String TEMPLATE20_DESCRIPTION = "Maximum amount of senior days per year";
    public static final String ORGANIZATION_ID = "organizationId";
    public static final String UNIT_ID = "unitId";

    public static final String WEEKLY = "WEEKLY";
    public static final String REQUEST_TO_CREATE_NEW_UTILITY = "Request to create new utility";
    public static final String UNIT = "unit";
    public static final String SUCCESS = "success";


    public static final String REQUEST_PHASE_NAME = "REQUEST";
    public static final String REQUEST_PHASE_DESCRIPTION = "REQUEST PHASE";
    public static final String PUZZLE_PHASE_NAME = "PUZZLE";
    public static final String PUZZLE_PHASE_DESCRIPTION = "PUZZLE PHASE";
    public static final String CONSTRUCTION_PHASE_NAME = "CONSTRUCTION";
    public static final String CONSTRUCTION_PHASE_DESCRIPTION = "CONSTRUCTION PHASE";
    public static final String FINAL_PHASE_NAME = "FINAL";
    public static final String FINAL_PHASE_DESCRIPTION = "FINAL PHASE";
    public static final Long DURATION_IN_WEEK=4L;



}
