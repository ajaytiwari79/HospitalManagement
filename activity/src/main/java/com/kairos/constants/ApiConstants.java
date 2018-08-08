package com.kairos.constants;

/**
 * Constants for Application Usage
 */
public final class ApiConstants {

    public static final String API_V1 ="/api/v1";
    public static final String PARENT_ORGANIZATION_URL = "/organization/{organizationId}";
    public static final String UNIT_URL = "/unit/{unitId}";
    public static final String ORGANIZATION_UNIT_URL = PARENT_ORGANIZATION_URL + UNIT_URL;
    public static final String COUNTRY_URL = "/country/{countryId}";
    public static final String API_ORGANIZATION_URL =  API_V1 + PARENT_ORGANIZATION_URL;
    public static final String API_ORGANIZATION_UNIT_URL = API_ORGANIZATION_URL + UNIT_URL;
    public static final String API_CONTROL_PANEL_SETTINGS_URL = "/control_panel/settings";
    public static final String API_CONTROL_PANEL_URL = API_ORGANIZATION_URL + UNIT_URL + API_CONTROL_PANEL_SETTINGS_URL;
    public static final String API_ABSENCE_PLANNING_URL = API_ORGANIZATION_URL + UNIT_URL + "/absence_planning";
    public static final String API_INTEGRATION_URL = API_ORGANIZATION_URL + UNIT_URL + "/integration";
    public static final String API_NOTIFICATION_URL = API_ORGANIZATION_URL  + "/notification";
    public static final String API_KMD_NEXUS_CITIZEN_URL = API_V1 + "/kmdNexus/citizen";
    public static final String API_AGGREGATOR_CITIZEN_URL = API_V1 + "/aggregator/citizen";
    public static final String WS_URL="ws://localhost:8090"+API_V1+"/kairos/ws";
    public static final String API_REQUEST_COMPONENT_URL =  API_V1 + PARENT_ORGANIZATION_URL + "/resourceComponent";
    public static final String API_ORGANIZATION_COUNTRY_URL =API_V1+PARENT_ORGANIZATION_URL+COUNTRY_URL;
    public static final String TIMEBANK_URL = API_ORGANIZATION_UNIT_URL+"/timeBank";
    public static final String PAYOUT_URL = API_ORGANIZATION_UNIT_URL+"/payOut";
    public static final String COUNTER_COUNTRY_DIST_URL = API_ORGANIZATION_COUNTRY_URL+"/counters/dist";
    public static final String COUNTER_UNIT_DIST_URL = API_ORGANIZATION_UNIT_URL+"/counters/dist";
    public static final String COUNTER_CONF_URL = API_ORGANIZATION_COUNTRY_URL+"/counters/conf";
    public static final String COUNTER_DATA_URL = API_ORGANIZATION_UNIT_URL+"/counters/data";
    public static final String ORDER_URL = API_V1 + PARENT_ORGANIZATION_URL + UNIT_URL + "/orders";
    public static final String OPENSHIFT_URL = API_V1 + PARENT_ORGANIZATION_URL + UNIT_URL + "/order/{orderId}/openshifts";
    public static final String OPEN_SHIFT_URL = API_V1 + PARENT_ORGANIZATION_URL + UNIT_URL + "/open_shift";
    public static final String ACTIVITY_CONFIGURATION = API_ORGANIZATION_UNIT_URL + "/activity_configuration";



    //RestClientURL
    public static final String CTA_BASIC_INFO = COUNTRY_URL+"/cta_basic_info";
    public static final String GET_UNIT_POSITION = "/unit_position/{unitPositionId}";

}
