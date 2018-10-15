package com.kairos.constants;

/**
 * Constants for Application Usage
 */
public final class ApiConstants {

    public static final String API_V1 ="/api/v1";
    public static final String PARENT_ORGANIZATION_URL = "/organization/{organizationId}";
    public static final String UNIT_URL = "/unit/{unitId}";
    public static final String STAFF_URL = "/staff";
    public static final String ORGANIZATION_UNIT_URL = PARENT_ORGANIZATION_URL + UNIT_URL;
    public static final String COUNTRY_URL = "/country/{countryId}";
    public static final String API_ORGANIZATION_URL =  API_V1 + PARENT_ORGANIZATION_URL;
    public static final String API_ORGANIZATION_UNIT_URL = API_ORGANIZATION_URL + UNIT_URL;
    public static final String API_CONTROL_PANEL_SETTINGS_URL = "/control_panel/settings";
    public static final String API_CONTROL_PANEL_URL = API_ORGANIZATION_URL + UNIT_URL + API_CONTROL_PANEL_SETTINGS_URL;
    public static final String API_ABSENCE_PLANNING_URL = API_ORGANIZATION_URL + UNIT_URL + "/absence_planning";
    public static final String API_INTEGRATION_URL = API_ORGANIZATION_URL + UNIT_URL + "/integration";
    public static final String API_NOTIFICATION_URL = API_ORGANIZATION_URL  + "/notification";
    public static final String WS_URL="ws://localhost:8090"+API_V1+"/kairos/ws";
    public static final String API_REQUEST_COMPONENT_URL =  API_V1 + PARENT_ORGANIZATION_URL + "/resourceComponent";
    public static final String API_ORGANIZATION_COUNTRY_URL =API_V1+PARENT_ORGANIZATION_URL+COUNTRY_URL;
    public static final String TIMEBANK_URL = API_ORGANIZATION_UNIT_URL+"/timeBank";
    public static final String PAYOUT_URL = API_ORGANIZATION_UNIT_URL+"/payOut";
    public static final String COUNTER_DIST_URL = "/counter/dist";
    public static final String COUNTER_COUNTRY_DIST_URL=COUNTRY_URL+COUNTER_DIST_URL;
    public static final String COUNTER_UNIT_DIST_URL = UNIT_URL+COUNTER_DIST_URL;
    public static final String COUNTER_STAFF_UNIT_DIST_URL = UNIT_URL+STAFF_URL+COUNTER_DIST_URL;
    public static final String COUNTER_CONF_URL = "/counter/conf";
    public static final String COUNTER_DATA_URL = API_ORGANIZATION_UNIT_URL+"/counters/data";
    public static final String ORDER_URL = API_V1 + PARENT_ORGANIZATION_URL + UNIT_URL + "/orders";
    public static final String OPEN_SHIFT_URL = API_V1 + PARENT_ORGANIZATION_URL + UNIT_URL + "/open_shift";
    public static final String COUNTRY_ACTIVITY_CONFIGURATION=COUNTRY_URL+"/activity_configuration";
    public static final String UNIT_ACTIVITY_CONFIGURATION=UNIT_URL+"/activity_configuration";
    public static final String ACTIVITY_SHIFT_STATUS_SETTINGS_URL =UNIT_URL+"/activity_shift_status_settings";
    public static final String DASHBOARD_URL="/dashboard_tab";
    public static final String COUNTERS="/counters";
    public static final String CATEGORY="/category";
    public static final String TAB="/tab";
    public static final String ACCESS_GROUP="/access_group";
    public static final String ORG_TYPE="/org_type";
    //RestClientURL
    public static final String CTA_BASIC_INFO = "/cta_basic_info";
    public static final String GET_UNIT_POSITION = "/unit_position/{unitPositionId}";
    public static final String GET_REASONCODE = "/reason_codes";
    public static final String REMOVE_FUNCTIONS_BY_UNIT_POSITION_ID = "/unit_position/{unitPositionId}/remove_functions";
    public static final String APPLY_FUNCTIONS_BY_UNIT_POSITION_ID = "/unit_position/{unitPositionId}/applyFunction";
    public static final String STAFF_USER_ACCESS_GROUP ="/staff/user/accessgroup";
    public static final String USER_STAFF_ID ="/user/staffId";
    public static final String STAFF_DETAILS ="/staff/details";
    public static final String ACCESS_GROUP_STAFFS ="/access_group/staffs";
    public static final String ORGANIZATION_TYPE_GET_ORGANIZATION_IDS ="/orgtype/get_organization_ids";
    public static final String KPI_DETAILS ="/kpi_details";
    public static final String UNIT_PARENT_ORGANIZATION_AND_COUNTRY ="/unit/parent_org_and_country";
    public static final String USER_USERID_STAFFS ="/user/{userId}/staffs";
    public static final String DAY_TYPES_AND_EMPLOYEMENT_TYPES ="/day_types_and_employment_types";
    public static final String STAFF_ACCESS_ROLES ="/staff/access_roles";
    public static final String EXPERTIZE_WITHID_STAFF_AND_UNIT_POSITIONS ="/expertise/{expertiseId}/staff_and_unit_positions";
    public static final String STAFF_EMAILS ="/staff/emails";
    public static final String EXPERTIZE_WITHID_UNIT_POSITIONS ="/expertise/{expertiseId}/unitPositions";
    public static final String EMPLOYEMENT_TYPE_AND_EXPERTIZE ="/employment_type_and_expertise";
    public static final String STAFF_ACCESS_GROUPS ="/staff/access_groups";
    public static final String USER_WITH_ID_UNIT_SICK_SETTINGS ="/user/{userId}/unit_sick_settings";
    public static final String SICK_SETTINGS_DEFAULT ="/sick_settings/default";
    public static final String STAFFS_ACCESS_GROUPS ="/staffs/access_groups";
    public static final String COUNTRY_ID ="/countryId";


}
