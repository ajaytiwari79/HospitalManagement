package com.kairos.constants;

/**
 * Constants for Application Usage
 */
public final class ApiConstants {
    public static final String API_V1 ="/api/v1";
    public static final String PARENT_ORGANIZATION_URL = "/organization/{organizationId}";
    public static final String UNIT_URL = "/unit/{unitId}";
    public static final String COUNTRY_URL = "/country/{countryId}";
    public static final String API_ORGANIZATION_URL =  API_V1 + PARENT_ORGANIZATION_URL;
    public static final String API_ORGANIZATION_UNIT_URL = API_ORGANIZATION_URL + UNIT_URL;
    public static final String API_CONTROL_PANEL_SETTINGS_URL = "/control_panel/settings";
    public static final String API_CONTROL_PANEL_URL = API_ORGANIZATION_URL + UNIT_URL + API_CONTROL_PANEL_SETTINGS_URL;
    public static final String API_ABSENCE_PLANNING_URL = API_ORGANIZATION_URL + UNIT_URL + "/absence_planning";
    public static final String API_INTEGRATION_URL = API_ORGANIZATION_URL + UNIT_URL + "/integration";
    public static final String API_NOTIFICATION_URL = API_ORGANIZATION_URL + UNIT_URL  + "/notification";
    public static final String API_EXTERNAL_CITIZEN_URL = API_V1 + "/kmdNexus/citizen";
    public static final String API_AGGREGATOR_CITIZEN_URL = API_V1 + "/aggregator/citizen";
    public static final String WS_URL="ws://localhost:8090"+API_V1+"/kairos/ws";
    public static final String API_REQUEST_COMPONENT_URL =  API_V1 + PARENT_ORGANIZATION_URL + "/resourceComponent";
    public static final String API_RESOURCE_URL =  API_V1 + PARENT_ORGANIZATION_URL  + UNIT_URL + "/resource";
    public static final String API_ORGANIZATION_COUNTRY_URL =API_V1+PARENT_ORGANIZATION_URL+COUNTRY_URL;
    public static final String API_UNIT_TYPE =  API_ORGANIZATION_URL  + COUNTRY_URL + "/unit_type";
    public static final String API_ACCOUNT_TYPE_URL = API_V1 +PARENT_ORGANIZATION_URL+COUNTRY_URL+ "/account";
    public static final String API_SICK_SETTINGS_URL =  API_V1 + PARENT_ORGANIZATION_URL  + UNIT_URL + "/sick_settings";

    //RestTemplate URL
    public static final String GET_VERSION_CTA = "/get_versions_cta";
    public static final String GET_DEFAULT_CTA = "/get_default_cta/expertise/{expertiseId}";
    public static final String GET_CTA_WTA_BY_EXPERTISE = "/expertise/{expertiseId}/cta_wta";
    public static final String GET_CTA_WTA_BY_UPIDS = "/unitposition-cta-wta";
    public static final String GET_VERSION_WTA = "/wta/versions";
    public static final String GET_WTA_BY_UNITPOSITION = "/wta/unitPosition/{unitPositionId}";
}
