package com.kairos.persistence.model.constants;

/**
 * Constants for relationship types
 */

public abstract class RelationshipConstants {

    /**
     * Default constructor
     */
    private RelationshipConstants() {
    }

    // Country
    public final static String COUNTRY_HAS_TAG = "COUNTRY_HAS_TAG";
    public final static String COUNTRY_HAS_FEATURE = "COUNTRY_HAS_FEATURE";
    public final static String COUNTRY_HAS_EQUIPMENT = "COUNTRY_HAS_EQUIPMENT";
    public final static String EQUIPMENT_HAS_CATEGORY = "EQUIPMENT_HAS_CATEGORY";
    public final static String HAS_TAG = "HAS_TAG";

    // Organization
    public final static String COUNTRY = "BELONGS_TO";
    public final static String HAS_SETTING = "HAS_SETTING";
    public final static String TYPE_OF = "TYPE_OF";
    public final static String SUB_TYPE_OF = "SUB_TYPE_OF";
    public final static String ORGANISATION_HAS_SKILL = "ORGANISATION_HAS_SKILL";
    public final static String ORGANIZATION_TIME_SLOT = "ORGANIZATION_TIME_SLOT";
    public final static String HAS_SUB_ORGANIZATION = "HAS_SUB_ORGANIZATION";
    public final static String PROVIDE_SERVICE = "PROVIDE_SERVICE";
    public final static String ORGANIZATION_TYPE_HAS_SERVICES = "ORGANIZATION_TYPE_HAS_SERVICES";
    public final static String HAS_PUBLIC_PHONE_NUMBER = "HAS_PUBLIC_PHONE_NUMBER";
    public final static String HAS_BILLING_ADDRESS = "HAS_BILLING_ADDRESS";
    public final static String REQUESTED_BY = "REQUESTED_BY";
    public final static String APPROVED_BY = "APPROVED_BY";
    public final static String ORGANIZATION = "ORGANIZATION";
    public final static String PAYMENT_TYPE = "PAYMENT_TYPE";
    public final static String CURRENCY = "CURRENCY";
    public final static String ORGANIZATION_HAS_OFFICE_RESOURCE = "ORGANIZATION_HAS_OFFICE_RESOURCE";
    public final static String HAS_EMPLOYMENT_TYPE = "HAS_EMPLOYMENT_TYPE";
    public final static String ORGANIZATION_HAS_TAG = "ORGANIZATION_HAS_TAG";
    public final static String HAS_CUSTOM_SERVICE_NAME_FOR = "HAS_CUSTOM_SERVICE_NAME_FOR";
    public final static String HAS_COMPANY_CATEGORY = "HAS_COMPANY_CATEGORY";

    public final static String EMPLOYMENT_TYPE_SETTINGS = "EMPLOYMENT_TYPE_SETTINGS";

    // Group
    public final static String HAS_TEAMS = "HAS_TEAMS";

    // Team
    public final static String TEAM_HAS_SKILLS = "TEAM_HAS_SKILLS";
    public final static String TEAM_HAS_SERVICES = "TEAM_HAS_SERVICES";
    public final static String TEAM_HAS_LOCATION = "TEAM_HAS_LOCATION";
    public final static String TEAM_HAS_MEMBER = "TEAM_HAS_MEMBER";

    // Skill ,Skill type, Skill Category , Country


    public final static String STAFF_HAS_SKILLS = "STAFF_HAS_SKILLS";
    public final static String HAS_HOLIDAY = "HAS_HOLIDAY";
    public final static String HAS_CATEGORY = "HAS_CATEGORY";

    // UserRole with User & Role
    public final static String HAS_PROFILE = "HAS_PROFILE";
    public final static String HAS_USERROLE = "USER_IS";
    public final static String HAS_ROLE_OF = "HAS_ROLE_OF";
    public final static String ADMINS_COUNTRY = "ADMINS_COUNTRY";
    public final static String MANAGE = "MANAGE";
    public final static String HAS_STAFF = "HAS_STAFF";
    public final static String ENGINEER_TYPE = "ENGINEER_TYPE";

    public final static String ACCESS_GROUP_HAS_ACCESS_TO_PAGE = "ACCESS_GROUP_HAS_ACCESS_TO_PAGE";
    public final static String STAFF_HAS_ACCESS_GROUP = "STAFF_HAS_ACCESS_GROUP";
    public final static String ORGANIZATION_HAS_ACCESS_GROUPS = "ORGANIZATION_HAS_ACCESS_GROUPS";

    //client
    public final static String GET_SERVICE_FROM = "GET_SERVICE_FROM";
    public final static String ADDRESS_ACCESS_DEAILS = "ADDRESS_ACCESS_DEAILS";
    public final static String HAS_OFFICE_ADDRESS = "HAS_OFFICE_ADDRESS";
    public final static String HAS_HOME_ADDRESS = "HAS_HOME_ADDRESS";
    public final static String HAS_SECONDARY_ADDRESS = "HAS_SECONDARY_ADDRESS";
    public final static String HAS_PARTNER_ADDRESS = "HAS_PARTNER_ADDRESS";
    public final static String HAS_TEMPORARY_ADDRESS = "HAS_TEMPORARY_ADDRESS";
    public final static String HAS_CONTACT_DETAIL = "HAS_CONTACT_DETAIL";
    public final static String HAS_LOCAL_AREA_TAG = "HAS_LOCAL_AREA_TAG";

    public final static String HAS_DOCTOR = "HAS_DOCTOR";
    public final static String HAS_DISEASE = "HAS_DISEASE";
    public final static String HAS_DIAGNOSE = "HAS_DIAGNOSE";
    public final static String HAS_ALLERGY = "HAS_ALLERGY";

    public final static String ORGANIZATION_HAS_RESOURCE = "ORGANIZATION_HAS_RESOURCE";
    public final static String ORGANIZATION_SUB_SERVICE = "ORGANIZATION_SUB_SERVICE";
    public final static String LINK_WITH_EXTERNAL_SERVICE = "LINK_WITH_EXTERNAL_SERVICE";

    public static final String BELONGS_TO = "BELONGS_TO";
    public static final String IS_A = "IS_A";

    public static final String HAS_ORGANIZATION_SERVICES = "HAS_ORGANIZATION_SERVICES";
    public static final String CIVILIAN_STATUS = "CIVILIAN_STATUS";
    public static final String HAS_EXPERTISE_IN = "HAS_EXPERTISE_IN";
    public static final String STAFF_HAS_EXPERTISE = "STAFF_HAS_EXPERTISE";

    public final static String SERVED_BY_STAFF = "SERVED_BY_STAFF";
    public final static String SERVED_BY_TEAM = "SERVED_BY_TEAM";
    public final static String SUB_PAGE = "SUB_PAGE";
    public final static String HAS_POSITIONS = "HAS_POSITIONS";
    public final static String HAS_LOCAL_AREA_TAGS = "HAS_LOCAL_AREA_TAGS";
    public final static String LAT_LNG = "LAT_AND_LNG";
    public final static String HAS_TIME_WINDOW = "HAS_TIME_WINDOW";
    public final static String APPLICABLE_IN_UNIT = "APPLICABLE_IN_UNIT";
    public final static String HAS_UNIT_PERMISSIONS = "HAS_UNIT_PERMISSIONS";
    public final static String HAS_PARTIAL_LEAVES= "HAS_PARTIAL_LEAVES";
    public final static String HAS_ACCESS_PAGE_PERMISSION = "HAS_ACCESS_PAGE_PERMISSION";
    public final static String HAS_ACCESS_GROUP = "HAS_ACCESS_GROUP";
    public final static String HAS_CUSTOMIZED_PERMISSION = "HAS_CUSTOMIZED_PERMISSION";
    public final static String HAS_ACCESS_PERMISSION = "HAS_ACCESS_PERMISSION";
    public final static String HAS_SUB_TYPE = "HAS_SUB_TYPE";
    public final static String HAS_CONTACT_ADDRESS = "HAS_CONTACT_ADDRESS";
    public final static String DAY_TYPE = "DAY_TYPE";

    //TimeCare
    public final static String CONTACT_DETAIL = "CONTACT_DETAIL";
    public final static String CONTACT_ADDRESS = "CONTACT_ADDRESS";
    public final static String SECONDARY_CONTACT_ADDRESS = "SECONDARY_CONTACT_ADDRESS";
    public final static String ZIP_CODE = "ZIP_CODE";
    public final static String BUSINESS_TYPE = "BUSINESS_TYPE";
    public final static String OWNERSHIP_TYPE = "OWNERSHIP_TYPE";
    public final static String INDUSTRY_TYPE = "INDUSTRY_TYPE";
    public final static String EMPLOYEE_LIMIT = "EMPLOYEE_LIMIT";
    public final static String CONTRACT_TYPE = "CONTRACT_TYPE";
    public final static String VAT_TYPE = "VAT_TYPE";
    public final static String EXPERTISE_HAS_SKILLS = "EXPERTISE_HAS_SKILLS";
    public final static String ORG_TYPE_HAS_SKILL = "ORG_TYPE_HAS_SKILL";
    public final static String NEXT_TO_KIN = "NEXT_TO_KIN";
    public final static String PEOPLE_IN_HOUSEHOLD_LIST = "PEOPLE_IN_HOUSEHOLD_LIST";
    public final static String MUNICIPALITY = "MUNICIPALITY";
    public final static String PROVINCE = "PROVINCE";
    public final static String REGION = "REGION";
    public final static String KAIROS_STATUS = "KAIROS_STATUS";
    public final static String TYPE_OF_HOUSING = "TYPE_OF_HOUSING";
    public final static String CLIENT_CONTACT_PERSON_RELATION_TYPE = "CLIENT_CONTACT_PERSON_RELATION_TYPE";
    public final static String CLIENT_CONTACT_PERSON_STAFF = "CLIENT_CONTACT_PERSON_STAFF";
    public final static String CLIENT_CONTACT_PERSON_SERVICE = "CLIENT_CONTACT_PERSON_SERVICE";

    // RULE Template
    public final static String HAS_LEVEL = "HAS_LEVEL";
    public final static String IN_ORGANIZATION_LEVEL = "IN_ORGANIZATION_LEVEL";
    public final static String HAS_RELATION_TYPES = "HAS_RELATION_TYPES";
    public final static String RELATION_TYPE = "RELATION_TYPE";
    public final static String RELATION_WITH_NEXT_TO_KIN = "RELATION_WITH_NEXT_TO_KIN";
    public final static String HAS_RELATION_OF = "HAS_RELATION_OF";
    public final static String HAS_RESOURCES = "HAS_RESOURCES";

    public final static String HAS_FAVOURITE_FILTERS = "HAS_FAVOURITE_FILTERS";
    public final static String FILTER_DETAIL = "FILTER_DETAIL";
    public final static String APPLICABLE_FOR = "APPLICABLE_FOR";
    public final static String HAS_FILTER_GROUP = "HAS_FILTER_GROUP";

    public final static String BELONGS_TO_STAFF = "BELONGS_TO_STAFF";
    public final static String UNAVAILABLE_ON = "UNAVAILABLE_ON";
    public final static String VEHICLE_HAS_FEATURE = "VEHICLE_HAS_FEATURE";
    public final static String HAS_TIME_SLOT_SET = "HAS_TIME_SLOT_SET";
    public final static String HAS_TIME_SLOT = "HAS_TIME_SLOT";

    public final static String RESOURCE_HAS_FEATURE = "RESOURCE_HAS_FEATURE";
    public final static String RESOURCE_HAS_EQUIPMENT = "RESOURCE_HAS_EQUIPMENT";
    public final static String ORGANIZATION_HAS_UNIONS = "ORGANIZATION_HAS_UNIONS";

    public final static String IN_UNIT = "IN_UNIT";
    public final static String HAS_ACCESS_FOR_ORG_CATEGORY = "HAS_ACCESS_FOR_ORG_CATEGORY";
    public final static String HAS_UNION = "HAS_UNION";
    public final static String HAS_ORGANIZATION_LEVEL = "HAS_ORGANIZATION_LEVEL";

    public final static String HAS_MUNICIPALITY = "HAS_MUNICIPALITY";
    public final static String IN_LEVEL = "IN_LEVEL";
    public final static String HAS_PAY_GRADE = "HAS_PAY_GRADE";
    public final static String HAS_TEMP_PAY_TABLE = "HAS_TEMP_PAY_TABLE";
    public final static String HAS_PAY_GROUP_AREA = "HAS_PAY_GROUP_AREA";
    public final static String FOR_SENIORITY_LEVEL = "FOR_SENIORITY_LEVEL";
    public final static String SUPPORTED_BY_UNION = "SUPPORTED_BY_UNION";
    public final static String SUPPORTS_SERVICES = "SUPPORTS_SERVICES";
    public final static String HAS_FUNCTION = "HAS_FUNCTION";
    public final static String VERSION_OF = "VERSION_OF";
    public final static String HAS_BASE_PAY_GRADE = "HAS_BASE_PAY_GRADE";
    public final static String HAS_SENIORITY_LEVEL = "HAS_SENIORITY_LEVEL";
    public final static String HAS_REASON_CODE = "HAS_REASON_CODE";
    public final static String HAS_PAYMENT_SETTINGS = "HAS_PAYMENT_SETTINGS";
    public final static String HAS_PERSONALIZED_SETTINGS = "HAS_PERSONALIZED_SETTINGS";
    public final static String HAS_SENIOR_DAYS = "HAS_SENIOR_DAYS";
    public final static String HAS_CHILD_CARE_DAYS = "HAS_CHILD_CARE_DAYS";
    public static final String APPLICABLE_FOR_EXPERTISE = "APPLICABLE_FOR_EXPERTISE";
    public static final String SENIORITY_LEVEL_FUNCTIONS = "SENIORITY_LEVEL_FUNCTIONS";
    public final static String HAS_FUNCTIONAL_AMOUNT = "HAS_FUNCTIONAL_AMOUNT";
    public final static String FUNCTIONAL_PAYMENT_MATRIX = "FUNCTIONAL_PAYMENT_MATRIX";
    public final static String APPLIED_FUNCTION = "APPLIED_FUNCTION";
    public final static String APPLICABLE_FUNCTION = "APPLICABLE_FUNCTION";
    public final static String EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT = "EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT";
    public final static String HAS_PREFERED_TIME_WINDOW = "HAS_PREFERED_TIME_WINDOW";
    public final static String BELONGS_TO_SECTOR = "BELONGS_TO_SECTOR";

// System Langugae

    public final static String HAS_SYSTEM_LANGUAGE = "HAS_SYSTEM_LANGUAGE";
    public final static String SELECTED_LANGUAGE = "SELECTED_LANGUAGE";
    public final static String IN_COUNTRY = "IN_COUNTRY";
    public final static String HAS_ACCOUNT_TYPE = "HAS_ACCOUNT_TYPE";
    public final static String HAS_ACCESS_Of_MODULE = "HAS_ACCESS_Of_MODULE";
    public final static String HAS_UNIT_TYPE = "HAS_UNIT_TYPE";

    public final static String HAS_EMPLOYMENT_LINES = "HAS_EMPLOYMENT_LINES";

    public static final String DAY_TYPES = "DAY_TYPES";
    public static final String HAS_PARENT_ACCESS_GROUP = "HAS_PARENT_ACCESS_GROUP";

    public static final String HAS_PERSONALIZED_LOCATION = "HAS_PERSONALIZED_LOCATION";
    public static final String HAS_LOCATION = "HAS_LOCATION";
    public static final String LOCATION_HAS_ADDRESS = "LOCATION_HAS_ADDRESS";
    public static final String HAS_SECTOR = "HAS_SECTOR";
    public static final String HAS_FIELD = "HAS_FIELD";
    public static final String HAS_SUB_MODEL = "HAS_SUB_MODEL";
    public static final String HAS_PERMISSION = "HAS_PERMISSION";

}
