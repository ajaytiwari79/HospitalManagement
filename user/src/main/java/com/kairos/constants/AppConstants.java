package com.kairos.constants;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by prabjot on 22/11/16.
 */
public class AppConstants {
    public static final String SHIFT_S = "Shift(s)";

    private AppConstants() {
    }

    //forgot password mail
    public static final String MAIL_SUBJECT ="Kairos Planning password reset link";
    public static final String MAIL_BODY="We received a request to reset your password. Click the link to choose a new one :\n";
    public static final String TIMEZONE_UTC = "UTC";
    public static final String HYPHEN = "-";
    public static final String KAI = "KAI-";
    public static final String ONE = "1";
    // Request methods
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";
    public static final String OPTIONS = "OPTIONS";
    public static final String YES="YES";
    public static final String NO="NO";

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
    public static final String API_KMD_CARE_CITIZEN = "/api/v1/kmdNexus/citizen/preferences/{unitId}";
    public static final String KMD_CARE_CITIZEN_URL = "/api/v1/kmdNexus/citizen/preferences/";
    public static final String API_KMD_CARE_CITIZEN_GRANTS = "/api/v1/kmdNexus/citizen/grants";
    public static final String API_KMD_CARE_CITIZEN_RELATIVE_DATA = "/api/v1/kmdNexus/citizen/nextToKin";
    public static final String FORWARD_SLASH = "/";

    public static final String API_KMD_CARE_STAFF_SHIFTS = "/api/v1/kmdNexus/citizen/unit/{unitId}/getShifts/{filterId}";
    public static final String API_KMD_CARE_TIME_SLOTS = "/api/v1/kmdNexus/citizen/unit/{unitId}/getTimeSlots";
    public static final String API_KMD_CARE_URL = "/api/v1/kmdNexus/citizen/unit/";
    public static final String API_TIME_SLOTS_NAME = "/api/v1/organization/{organizationId}/unit/{unitId}/time_slot_name";

    public static final String JAVA_MAIL_FILE = "classpath:java-mail.properties";

    public static final String HOST = "mail.server.host";
    public static final String PORT = "mail.server.port";
    public static final String PROTOCOL = "mail.server.protocol";
    public static final String MAIL_USERNAME = "mail.server.username";
    public static final String MAIL_AUTH = "mail.server.password";

    public static final String BODY = "Body";
    public static final String TO = "To";
    public static final String FROM = "From";

    public static final String OTP_MESSAGE = "Onetime password for registrartion is  ";
    public static final String VISITATOR = "VISITATOR";
    public static final String PLANNER = "PLANNER";
    public static final String TASK_GIVERS = "TASK_GIVERS";
    public static final String COUNTRY_ADMIN = "COUNTRY_ADMIN";
    public static final String SUPER_ADMIN="SUPER_ADMIN";

    public static final String IMAGES_PATH = "/opt/kairos/images";
    public static final String HAS_ACCESS_OF_TABS = "HAS_ACCESS_OF_TABS";
    public static final String ORGANIZATION = "organization";
    public static final String GROUP = "group";
    public static final String TEAM = "team";
    public static final String ADMIN_EMAIL = "prabjot.singh@oodlestechnologies.com";
    public static final String ORGANIZATION_LABEL = "Organization";
    public static final String GROUP_LABEL = "Group";

    //Kettle commands
    public static final String KETTLE_TRANS_STATUS = "/kettle/transStatus/?name=GetAllWorkShiftsByWorkPlaceId&xml=y";
    public static final String KETTLE_EXECUTE_TRANS = "/kettle/executeTrans/?trans=";

    //Transformation Paths
    //development and production
    public static final String IMPORT_TIMECARE_SHIFTS_PATH = "/opt/infra/data-integration/GetAllWorkShiftsByWorkPlaceId.ktr";

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
    public static final String KMD_NEXUS_PATIENT_PATHWAY_PREFERENCE = "https://test.avaleo.net/mobile/unity/v2/patient/%s/preferences/";
    public static final String KMD_NEXUS_PATIENT_FILTER = "https://test.avaleo.net/mobile/unity/v2/patients?filterId=496";
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

    public static final String TIME_SLOT_SET_NAME = "Time slot 1";
    public static final String SKILL_CATEGORY_FOR_TIME_CARE = "Skills From TimeCare";
    public static final int DB_RECORD_LIMIT = 100;
    public static final String KAIROS_EMAIL = "@kairos.com";
    public static final String DEFAULT_PASSPHRASE_ENDS_WITH = "@kairos";


    // Default data to import employments from time care
    public static final Integer FULL_TIME_WEEKLY_MINUTES = 2220;
    public static final Integer NUMBER_OF_WORKING_DAYS_IN_WEEK = 5;

    // Staff
    public static final String UNIT_MANAGER_EMPLOYMENT_DESCRIPTION = "Working as Unit Manager";
    public static final String CHILD_CARE="childCare";
    public static final String SENIOR_DAYS="seniorDays";

    // Module/tab id prefix for Access Page
    public static final String MODULE_ID_PRFIX = "module_";
    public static final String TAB_ID_PRFIX = "tab_";
    public static final String ACCESS_PAGE_HAS_LANGUAGE ="ACCESS_PAGE_HAS_LANGUAGE";

    //Tomtom
    public static final String TOMTOM_KEY = "key";
    public static final String SCHEDULER_TO_USER_QUEUE_TOPIC = "SchedulerToUserQueue";
    public static final String USER_TO_SCHEDULER_JOB_QUEUE_TOPIC = "UserToSchedulerJobQueue";
    public static final String USER_TO_SCHEDULER_LOGS_QUEUE_TOPIC = "UserToSchedulerLogsQueue";

    //CountryService
    /**
     * Application name.
     */
    public static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    /**
     * Directory to store user credentials for this application.
     */
    /** Directory to store user credentials for this application. */
    public static final File DATA_STORE_DIR = new File(
            System.getProperty("user.home"), ".credentials/calendar-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    public static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    public static final JsonFactory JSON_FACTORY =JacksonFactory.getDefaultInstance();


    /** Global instance of the HTTP transport. */
    public static HttpTransport HTTP_TRANSPORT;


    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES =  Arrays.asList(CalendarScopes.CALENDAR_READONLY);


    //constants for hourly Cost calculation
    public static final BigDecimal PER_DAY_HOUR_OF_FULL_TIME_EMPLOYEE = new BigDecimal(7.4);
    public static final int LEAP_YEAR = 366;
    public static final int NON_LEAP_YEAR = 365;
    public static final short ONE_WEEK_MINUTES=10080;

    //Query result Keys
    public static final String STAFF="staff";
    public static final String EMPLOYMENT ="employment";
    public static final String EMPLOYMENT_ORGANIZATION_RELATIONSHIP ="employmentOrgRel";
    public static final String EMPLOYMENT_STAFF_RELATIONSHIP ="employmentStaffRel";

    //organization Hierarchy Filter constants
    public static final String ORGANIZATION_TYPES = "organizationType";
    public static final String ORGANIZATION_SUB_TYPES = "organizationSubType";
    public static final String ORGANIZATION_SERVICES = "organizationService";
    public static final String ORGANIZATION_SUB_SERVICES = "organizationSubService";
    public static final String ACCOUNT_TYPES = "accountType";


    //Locatiom
    public static final String MAIN_LOCATION = "Main Location";

    //country and system langange relation
    public static final String HAS_SYSTEM_LANGUAGE = "HAS_SYSTEM_LANGUAGE";
    public static final String KAIROS="Kairos";
    public static final String KAIROS_DENMARK_EMAIL="kairos_denmark@kairos.com";
    public static final String ULRIK_EMAIL="ulrik@kairos.com";
    public static final String ULRIK="Ulrik";
    public static final String AUTHORIZATION = "Authorization";


//Group
    public static final double DAYS_IN_ONE_YEAR = 365.242199;
    public static final double DAYS_IN_ONE_MONTH = 30.4368499;
    public static final long MAX_LONG_VALUE = 9999999999999999L;
    public static final String DATE_OF_BIRTH = "dateOfBirth";
    public static final String JOINING_BIRTH = "joiningDate";
    public static final String EXPERTISE_LIST = "expertiseList";
    public static final String EXPERTISE_START_DATE_IN_MILLIS = "expertiseStartDateInMillis";
    public static final String EMPLOYMENTS = "employments";
    public static final String START_DATE = "startDate";
    public static final String EMPLOYMENT_LINES = "employmentLines";
    public static final String PAY_GRADES = "payGrades";
    public static final String PAY_GRADE_LEVEL = "payGradeLevel";
    public static final String DURATION_TYPE = "durationType";
    public static final String ID = "id";
    public static final String NIGHT_WORKER = "nightWorker";
    public static final String WORK_TIME_AGREEMENTS = "workTimeAgreements";
}
