package com.kairos.constants;

/**
 * Constants for Application Usage
 */
public final class ApiConstants {

    public static final String UNIT_URL = "/unit/{unitId}";
    public static final String STAFF_URL = "/staff";
    public static final String ORGANIZATION_UNIT_URL = UNIT_URL;
    public static final String COUNTRY_URL = "/country/{countryId}";
    public static final String API_V1 = "/api/v1";
    public static final String API_UNIT_URL = API_V1 + UNIT_URL;
    public static final String API_ABSENCE_PLANNING_URL = API_V1 + UNIT_URL + "/absence_planning";
    public static final String API_NOTIFICATION_URL = API_V1 + "/notification";
    public static final String API_REQUEST_COMPONENT_URL = API_V1 + "/resourceComponent";
    public static final String API_ORGANIZATION_COUNTRY_URL = API_V1 + COUNTRY_URL;
    public static final String TIMEBANK_URL = API_UNIT_URL + "/timeBank";
    public static final String PAYOUT_URL = API_UNIT_URL + "/payOut";
    public static final String COUNTER_DIST_URL = "/counter/dist";
    public static final String KPI_URL = "/kpi/{kpiId}";
    public static final String COUNTER_COUNTRY_DIST_URL = COUNTRY_URL + COUNTER_DIST_URL;
    public static final String COUNTER_UNIT_DIST_URL = UNIT_URL + COUNTER_DIST_URL;
    public static final String COUNTER_STAFF_UNIT_DIST_URL = UNIT_URL + STAFF_URL + COUNTER_DIST_URL;
    public static final String COUNTER_CONF_URL = "/counter/conf";
    public static final String COUNTER_DATA_URL = API_UNIT_URL + "/counters/data";
    public static final String ORDER_URL = API_V1 + UNIT_URL + "/orders";
    public static final String COUNTRY_ACTIVITY_CONFIGURATION = COUNTRY_URL + "/activity_configuration";
    public static final String UNIT_ACTIVITY_CONFIGURATION = UNIT_URL + "/activity_configuration";
    public static final String DASHBOARD_URL = "/dashboard_tab";
    public static final String COUNTERS = "/counters";
    public static final String CATEGORY = "/category";
    public static final String TAB = "/tab";
    public static final String ACCESS_GROUP = "/access_group";
    public static final String ORG_TYPE = "/org_type";
    //RestClientURL
    public static final String CTA_BASIC_INFO = "/cta_basic_info";
    public static final String GET_EMPLOYMENT = "/employment/{employmentId}";
    public static final String GET_REASONCODE = "/reason_codes";
    public static final String REMOVE_FUNCTIONS_BY_EMPLOYMENT_ID = "/employment/{employmentId}/remove_functions";
    public static final String RESTORE_FUNCTIONS_BY_EMPLOYMENT_ID = "/employment/{employmentId}/restore_functions";
    public static final String APPLY_FUNCTION = "/employment/{employmentId}/applyFunction";
    public static final String REMOVE_FUNCTION_FROM_EMPLOYMENT_ON_DELETE_SHIFT = "/employment/{employmentId}/remove_function_on_delete_shift";
    public static final String STAFF_USER_ACCESS_GROUP = "/staff/user/accessgroup";
    public static final String USER_STAFF_ID = "/user/staffId";
    public static final String STAFF_DETAILS = "/staff/details";
    public static final String ACCESS_GROUP_STAFFS = "/access_group/staffs";
    public static final String ORGANIZATION_TYPE_GET_ORGANIZATION_IDS = "/orgtype/get_organization_ids";
    public static final String KPI_DETAILS = "/kpi_details";
    public static final String UNIT_PARENT_ORGANIZATION_AND_COUNTRY = "/unit/parent_org_and_country";
    public static final String USER_USERID_STAFFS = "/user/{userId}/staffs";
    public static final String DAY_TYPES_AND_EMPLOYEMENT_TYPES = "/day_types_and_employment_types";
    public static final String STAFF_ACCESS_ROLES = "/staff/access_roles";
    public static final String STAFF_AND_EMPLOYMENTS_BY_EXPERTISE_ID = "/expertise/{expertiseId}/staff_and_employments";
    public static final String STAFF_EMAILS = "/staff/emails";
    public static final String EMPLOYMENTS_BY_EXPERTISE_ID = "/expertise/{expertiseId}/employments";
    public static final String EMPLOYEMENT_TYPE_AND_EXPERTISE = "/employment_type_and_expertise";
    public static final String STAFF_ACCESS_GROUPS = "/staff/access_groups";
    public static final String USER_WITH_ID_UNIT_SICK_SETTINGS = "/user/{userId}/unit_sick_settings";
    public static final String SICK_SETTINGS_DEFAULT = "/sick_settings/default";
    public static final String STAFFS_ACCESS_GROUPS = "/staffs/access_groups";
    public static final String COUNTRY_ID = "/countryId";
    public static final String STAFF_ID_EXPERTISE_ID_UNIT_EMPLOYMENT_ID = "/staff/{staffId}/expertise/{expertiseId}/employmentId";
    public static final String ACCESS_GROUPS_BY_PARENT = "/access_groups_by_parent";

    public static final String STAFF_PRIORTY_GROUP = "/staff/priority_group";
    public static final String ABSENCE_TYPES_TITLE = " /absenceTypes/{title}";
    public static final String CONTRACT_TYPE = "/contractType";
    public static final String COUNTRY_ORGANIZATION_SERVICE_URL = "/country/organizaton_service/{organizationServiceId}";
    public static final String ORGANIZATION_TYPES_HIERARCHY = "/organization_types/hierarchy";
    public static final String TASK_TYPES_SKILLS = "/task_type/skills";
    public static final String TIME_SLOTS = "/time_slots";
    public static final String DAY_TYPES = "/day_types";
    public static final String INTEGRATION_UNIT_CITIZEN_UNIT_ID_FLSCRED = "/integration/unit/{citizenUnitId}/flsCred";
    public static final String COUNTRY_COUNTRY_ID = COUNTRY_ID + "/{countryId}";
    public static final String SERVICE_DATA = "/service/data";
    public static final String SKILLS = "/skills";
    public static final String STAFF_WITH_STAFF_ID = "/staff/{staffId}";
    public static final String STAFF_UNIT_WISE = "/staff/unitwise";
    public static final String EMPLOYMENT_EXPERTISE = "/employment/expertise";
    public static final String STAFF_GET_STAFF_INFO = "/staff/getStaffInfo";
    public static final String STAFF_GET_STAFF_BY_UNIT = "/staff/get_Staff_By_Unit";
    public static final String UNIT_MANAGER_IDS_UNIT_ID = "/unit_manager_ids/{unitId}";
    public static final String COUNTRY_ADMINS_IDS_OF_UNIT = "/country_admins_ids/{countryAdminsOfUnitId}";
    public static final String VERIFY_UNIT_EMPLOYEMNT_BY_STAFF_ID = STAFF_WITH_STAFF_ID + "/verifyUnitEmployment";
    public static final String VERIFY_UNIT_EMPLOYEMNT_BY_STAFF_ID_UNIT_EMPLOYEMENT_ID = VERIFY_UNIT_EMPLOYEMNT_BY_STAFF_ID + "/{unitEmploymentId}";
    public static final String STAFF_CURRENT_USER_ID = "/staff/current_user/{userId}";
    public static final String STAFF_GET_STAFF_BY_EXPERTISES = "/staff/getStaffByExperties";
    public static final String CURRENT_USER_ACCESS_ROLE = "/current_user/access_role";
    public static final String ACCESS_ROLE_AND_REASON_CODE = "/current_user/access_role_and_reason_codes";
    public static final String TIME_SLOT_URL = "/time_slot/{timeSlotId}";
    public static final String CURRENT_TIME_SLOTS = "/current/time_slots";
    public static final String GET_CTA_BY_EMPLOYMENT_ID = "/cta_by_employment/{employmentId}";
    public static final String WTA_RULE_INFO = "/WTARelatedInfo";
    public static final String GET_WTA_TEMPLATE_DEFAULT_DATA_INFO = "/getWtaTemplateDefaultDataInfo";
    public static final String GET_WTA_TEMPLATE_DEFAULT_DATA_INFO_BY_UNIT_ID = GET_WTA_TEMPLATE_DEFAULT_DATA_INFO + "ByUnitId";
    public static final String CLIENT_ID_URL = "/client/{clientId}";
    public static final String UPDATE_CLIENT_TEMP_ADDRESS_BY_CLIENT_ID = CLIENT_ID_URL + "/updateClientTempAddress";
    public static final String CLIENT_CITIZEN_ID = "/client/{citizenId}";
    public static final String CLIENT_CITIZEN_ID_INFO = CLIENT_CITIZEN_ID + "/info";
    public static final String CLIENT_CITIZEN_ID_ADDRESS_INFO = CLIENT_CITIZEN_ID + "/addressInfo";
    public static final String GET_CLIENT_INFO = "/client/getClientInfo";
    public static final String CLIENT_CITIZEN_ID_UNIT_ID_TASK_PREREQUISITES = CLIENT_CITIZEN_ID + "/{unitId}/task_prerequisites";
    public static final String CLIENT_ORGANIZATION_CLIENTS = "/client/organization_clients";
    public static final String ORGANIZATION_CLIENTS_IDS = "/client/organization_clients/ids";
    public static final String CLIENT_CLIENT_IDS = "/client/client_ids";
    public static final String GET_CLIENT_STAFF_INFO_BY_CLIENT_ID = CLIENT_ID_URL + "/getClientStaffInfo";
    public static final String GET_STAFF_CITIZEN_HOUSEHOLDS_BY_CITIZEN_ID_AND_STAFF_ID = CLIENT_CITIZEN_ID + STAFF_WITH_STAFF_ID + "/getStaffCitizenHouseholds";
    public static final String CLIENT_BY_IDS = "/client/clientsByIds";
    public static final String GET_UNIT_IDS_BY_CLIENT_IDS = "/client/client_ids_by_unitIds";
    public static final String API_EXPERTISE_URL = "/expertise/{expertiseId}";
    public static final String API_EXPERTISE_BREAK_URL = "/expertise/{expertiseId}/break";

    public static final String GET_ORGANIZATION_WITH_COUNTRY_ID = "/getOrganizationWithCountryId";
    public static final String COMMON_DATA = "/common_data";
    public static final String ONE_TIME_SYNC = "/one_time_sync";
    public static final String AUTO_GENERATE_TASK_SETTINGS = "/auto_generate_task_settings";
    public static final String TIME_SLOT_NAME = "/time_slot_name";
    public static final String GET_ORGANIZATION_BY_TEAM_ID = "/getOrganizationByTeamId/{teamId}";
    public static final String GET_PARENT_ORGANIZATION_OF_CITY_LEVEL = "/getParentOrganizationOfCityLevel";
    public static final String GET_PARENT_OF_ORGANIZATION = "/getParentOfOrganization";
    public static final String EXTERNAL_ID_URL = "/external/{externalId}";
    public static final String GET_TASK_DEMAND_SUPPLIER_INFO = "/getTaskDemandSupplierInfo";
    public static final String UNIT_VISITATION = "/unit_visitation";
    public static final String VERIFY_ORGANIZATION_EXPERTISE = "/verifyOrganizationExpertise";
    public static final String ORGANIZATION_TYPE_URL_ORGANIZATIONS = "/organization_type/{organizationTypeId}/organizations";
    public static final String ORGANIZATION_TYPE_AND_SUB_TYPES = "/organizationTypeAndSubTypes";
    public static final String ORGANIZATION_SERVICE_URL = "/organization_service/{organizationServiceId}";
    public static final String ORGANIZATION_SERVICE_ASSIGN_ORGANIZATION_TYPES = ORGANIZATION_SERVICE_URL + "/assign/organizationTypes";
    public static final String ORGANIZATION_SERVICE_DETACH_ORGANIZATION_TYPES = ORGANIZATION_SERVICE_URL + "/detach/organizationTypes";
    public static final String WITHOUT_AUTH = "/WithoutAuth";
    public static final String SKILL_ORG_TYPES = "/skill/orgTypes";
    public static final String GET_DATE_TYPE_BY_DATES = "/dayTypebydate";
    public static final String DAY_TYPE = "/dayType";
    public static final String SHOW_COUNTRY_TAGS = "/show_country_tags";
    public static final String TEAM_ORGANIZATION_ID = "/team/organizationId";
    public static final String COUNTRY_GLIDE_TIME_SETTINGS = "/country/{countryId}/glide_time";
    public static final String RESTORE_FUNCTION_ON_PHASE_RESTORATION = "/updateFunctionOnPhaseRestoration";
    public static final String GET_FUNCTIONS_OF_EMPLOYMENT = STAFF_WITH_STAFF_ID + "/employment/{employmentId}/functions";
    public static final String FUNCTIONS_OF_EMPLOYMENT = "/employment/functions";
    public static final String UNIT_TIMEZONE = "/time_zone";
    public static final String UNITS_TIMEZONE = "/units_time_zone";
    public static final String GET_ORGANIZATION_IDS = "/get_organization_ids";
    public static final String GET_PUBLIC_HOLIDAY_DAY_TYPE_REASON_CODE = "/holiday_day_type_reason_code";

    public static final String STAFF_EMPLOYMENT_BY_EMPLOYMENT_ID = STAFF_URL + "/staff_employment/{employmentId}";

    public static final String UNIT_LOCATION_AND_REASON_CODE = "/unit_location_and_reason_codes";
    public static final String GET_UNIT_BY_EMPLOYMENT = GET_EMPLOYMENT + "/get_unit";

    // /staff_by_employment_type
    public static final String STAFF_BY_KPI_FILTER = "/staff_by_kpi_filter";
    public static final String KPI_DEFAULT_DATA = "/kpi_default_data";
    public static final String KPI_FILTER_DEFAULT_DATA = "/kpi_filter_default_data";

    //Scheduler
    public static final String SCHEDULER_EXECUTE_JOB = "/scheduler_execute_job";
    public static final String REASON_CODE_LINK_WITH_TIME_TYPE = "/reason_codes/link_with_time_type/{timeTypeId}";

    public static final String TEAM_ACTIVITIES = "/staff/{staffId}/team_activities";

    public static final String FIBONACCI = "/fibonacci";
    public static final String IS_ACTIVITY_ASSIGNED = "/team/is_activity_assigned";
    public static final String KPI_SET="/kpi_set";

    public static final String REQUEST_ABSENCE = API_V1+"/request_absence";


}
