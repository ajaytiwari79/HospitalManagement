package com.kairos.constants;

/**
 * Created by prabjot on 22/11/16.
 */
public class AppConstants {

    private AppConstants(){}
    // DB
    public static final  String NEO4J_EMBEDDED_DRIVER ="org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver";
    public static final  String DB_PATH="dbpath";
    public static final  String DB_USER="neo4j";
    public static final  String DB_PASS="oodles";

    //Authority
    public static final  String READ_PRIVILEGE="READ_PRIVILEGE";
    public static final  String WRITE_PRIVILEGE="WRITE_PRIVILEGE";
    public static final  String EDIT_PRIVILEGE="READ_PRIVILEGE";
    public static final  String ALL_PRIVILEGE="ALL_PRIVILEGE";

    // Request methods
    public static final  String GET="GET";
    public  static final  String POST="POST";
    public static final  String DELETE="DELETE";
    public static final  String PUT="PUT";
    public static final String OPTIONS="OPTIONS";

    //Swagger
    public static final  String SWAGGER_UI_PATH="/swagger-ui.html";
    public static final  String SWAGGER_API_DOCS="/v2/api-docs";
    public  static final  String SWAGGER_RESOURCES="swagger-resources";

    public static final  String WEBJARS ="webjars";

    public static final  String CONTENT_TYPE_JSON  ="application/json";
    public static final  String API_LOGIN_URL="/api/v1/login";
    public static final  String API_LOGOUT_URL="/api/v1/logout";
    public static final  String API_LOGIN_MOBILE_URL="/api/v1/login/mobile";
    public static final  String API_VERIFY_OTP="/user/api/v1/login/verify/otp";
    public static final  String API_LOGIN_MOBILE_NUMBER_URL="/api/v1/login/mobile_number";
    public static final  String API_COUNTRY_CODE_LIST="/api/v1/country_code_list_for_login";
    public static final  String API_TIME_CARE_SHIFTS="/api/v1/time_care/getShifts";
    public static final  String API_TIME_CARE_ACTIVITIES="/api/v1/time_care/getWorkPlaces";
    public static final  String API_KMD_CARE_CITIZEN="/api/v1/kmdNexus/citizen/preferences/";
    public static final  String API_KMD_CARE_CITIZEN_GRANTS="/api/v1/kmdNexus/citizen/grants";
    public static final  String API_KMD_CARE_CITIZEN_RELATIVE_DATA="/api/v1/kmdNexus/citizen/nextToKin";

    public static final String API_CREDENTIAL_UPDATE_URL = "/api/v1/user/password";

    public static final  String SCHEDULE="SCHEDULE";
    public static final  String TIMING="TIMING";
    public  static final  String LEAVES="LEAVES";


    //URL
    public static final  String INDEX_PAGE="index";
    public static final  String UNAUTH_PAGE="user/401";

    public static final  String USER_PAGE="user/user";
    public static final  String ADMIN_PAGE="admin/admin";
    public static final  String LOGIN_PAGE="auth/login";

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
    public static final String ACTUAL_PLANNING = "actual_planning";
    public static final String PRE_PLANNING = "pre_planning";

    //KMD Nexus
    public static final String KMD_NEXUS_CLIENT_ID = "third_party_vendor";
    public static final String KMD_NEXUS_CLIENT_SECRET = "APNaYeGDVGOdjQf-jIFgU59tfkPux2mD6xGpbbAEuUc32ie8FRn7Y2cxcSayV6VafluS0pLu2TyhARFHtdHc-NQ";
    public static String KMD_NEXUS_ACCESS_TOKEN = "";
    public static final String KMD_NEXUS_CUSTOMER = "unity";
    public static final String KMD_NEXUS_GRANT_TYPE = "password";
    public static final String KMD_NEXUS_USERNAME = "admin";
    public static final String KMD_NEXUS_ORGANIZATION = "Gruppe 1 (Hjemmepleje)";
    public static final String KMD_NEXUS_AUTH = "admin";
    public static final String KMD_NEXUS_AUTH_URL = "https://test.avaleo.net/auth/token";
    public static final String KMD_NEXUS_PATIENT_PREFERENCE = "https://test.avaleo.net/mobile/unity/v2/preferences/";
    public static final String KMD_NEXUS_PATIENT_PATHWAY_PREFERENCE = "https://test.avaleo.net/mobile/unity/v2/patient/%s/preferences/";
    public static final String KMD_NEXUS_PATIENT_FILTER = "https://test.avaleo.net/mobile/unity/v2/patients?filterId=495";
    public static final String KMD_NEXUS_PATIENT_EVENT_GRANTS = "https://test.avaleo.net/mobile/unity/v2/patientGrants/calendar/orderGrants/PATIENT_EVENT?patientId=%s&filterId=420";
    public static final String KMD_NEXUS_PATIENT_PATHWAY = "https://test.avaleo.net/mobile/unity/v2/patients/%s/pathways/references?filterId=";
    public static final String KMD_NEXUS_PATIENT_GRANTS = "https://test.avaleo.net/mobile/unity/v2/patientGrants/";
    public static final String KMD_NEXUS_PATIENT_ORDER_GRANTS = "https://test.avaleo.net/mobile/unity/v2/patientGrants/orderGrants/";
    public static final String KMD_NEXUS_PATIENT_RELATIVE_CONTACT = "https://test.avaleo.net/mobile/unity/v2/patients/%s/overview?relativeContactLimit=3";

    //Control panel unique keys
    public static final String IMPORT_TIMECARE_SHIFTS = "IMPORT_TIMECARE_SHIFTS";
    public static final String IMPORT_KMD_CITIZEN = "IMPORT_KMD_CITIZEN";
    public static final String IMPORT_KMD_CITIZEN_NEXT_TO_KIN = "IMPORT_KMD_CITIZEN_NEXT_TO_KIN";
    public static final String IMPORT_KMD_CITIZEN_GRANTS = "IMPORT_KMD_CITIZEN_GRANTS";

    public static final String DATA_SAVED_FROM_SERVICE = "Data saved from Service";



}
