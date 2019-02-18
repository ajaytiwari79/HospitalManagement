package com.kairos.constants;

/**
 * Constants for Application Usage
 */
public final class ApiConstants {
    public static final String UNIT_URL = "/unit/{unitId}";
    public static final String COUNTRY_URL = "/country/{countryId}";
    public static final String API_V1 =  "/api/v1";
    public static final String API_ORGANIZATION_UNIT_URL = API_V1 + UNIT_URL;
    public static final String API_INTEGRATION_URL = API_V1 + UNIT_URL + "/integration";
    public static final String API_EXTERNAL_CITIZEN_URL = API_V1 + "/kmdNexus/citizen";
    public static final String API_RESOURCE_URL =  API_V1+ UNIT_URL + "/resource";
    public static final String API_ORGANIZATION_COUNTRY_URL =API_V1+COUNTRY_URL;
    public static final String API_UNIT_TYPE =  API_V1 + COUNTRY_URL + "/unit_type";
    public static final String API_ACCOUNT_TYPE_URL = API_V1 +COUNTRY_URL+ "/account";
    public static final String API_SICK_SETTINGS_URL =  API_V1   + UNIT_URL + "/sick_settings";

    //RestTemplate URL
    public static final String GET_VERSION_CTA = "/get_versions_cta";
    public static final String GET_CTA_WTA_BY_EXPERTISE = "/expertise/{expertiseId}/cta_wta";
    public static final String GET_CTA_WTA_BY_UPIDS = "/unitposition-cta-wta";
    public static final String GET_VERSION_WTA = "/wta/versions";
    public static final String API_ALL_PHASES_URL = "/phase/all";
    public static final String APPLICABLE_CTA_WTA = "/applicable-cta-wta";
    public static final String APPLY_CTA_WTA = "/unitPosition/{unitPositionId}/apply_cta_wta";
    public static final String APPLY_CTA_WTA_END_DATE = "/unitPosition/{unitPositionId}/apply_end_date";
    public static final String UNIT_LOCATION_AND_REASON_CODE = "/unit_location_and_reason_codes";


    //Scheduler restClient
    public static final String CREATE_SCHEDULER_PANEL = "/scheduler_panel";
    public static final String UPDATE_SCHEDULER_PANEL = CREATE_SCHEDULER_PANEL+"/update_scheduler_panel_by_job_sub_type";
    public static final String DELETE_SCHEDULER_PANEL = CREATE_SCHEDULER_PANEL+"/delete_job_by_sub_type_and_entity_id";
    public static final String SCHEDULER_EXECUTE_JOB = "/scheduler_execute_job";
    public final static String JOB_DETAILS = "/job_details";



}
