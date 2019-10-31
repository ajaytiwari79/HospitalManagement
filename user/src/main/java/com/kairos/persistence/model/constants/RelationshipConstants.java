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
    public static final  String COUNTRY_HAS_TAG = "COUNTRY_HAS_TAG";
    public static final  String COUNTRY_HAS_FEATURE = "COUNTRY_HAS_FEATURE";
    public static final  String COUNTRY_HAS_EQUIPMENT = "COUNTRY_HAS_EQUIPMENT";
    public static final  String EQUIPMENT_HAS_CATEGORY = "EQUIPMENT_HAS_CATEGORY";
    public static final  String HAS_TAG = "HAS_TAG";

    // Organization
    public static final  String HAS_SETTING = "HAS_SETTING";
    public static final  String TYPE_OF = "TYPE_OF";
    public static final  String SUB_TYPE_OF = "SUB_TYPE_OF";
    public static final  String ORGANISATION_HAS_SKILL = "ORGANISATION_HAS_SKILL";
    public static final  String ORGANIZATION_TIME_SLOT = "ORGANIZATION_TIME_SLOT";
    public static final  String HAS_SUB_ORGANIZATION = "HAS_SUB_ORGANIZATION";
    public static final  String HAS_UNIT = "HAS_UNIT";
    public static final  String PROVIDE_SERVICE = "PROVIDE_SERVICE";
    public static final  String ORGANIZATION_TYPE_HAS_SERVICES = "ORGANIZATION_TYPE_HAS_SERVICES";
    public static final  String HAS_PUBLIC_PHONE_NUMBER = "HAS_PUBLIC_PHONE_NUMBER";
    public static final  String HAS_BILLING_ADDRESS = "HAS_BILLING_ADDRESS";
    public static final  String REQUESTED_BY = "REQUESTED_BY";
    public static final  String APPROVED_BY = "APPROVED_BY";
    public static final  String ORGANIZATION = "ORGANIZATION";
    public static final  String PAYMENT_TYPE = "PAYMENT_TYPE";
    public static final  String CURRENCY = "CURRENCY";
    public static final  String ORGANIZATION_HAS_OFFICE_RESOURCE = "ORGANIZATION_HAS_OFFICE_RESOURCE";
    public static final  String HAS_EMPLOYMENT_TYPE = "HAS_EMPLOYMENT_TYPE";
    public static final  String ORGANIZATION_HAS_TAG = "ORGANIZATION_HAS_TAG";
    public static final  String HAS_CUSTOM_SERVICE_NAME_FOR = "HAS_CUSTOM_SERVICE_NAME_FOR";
    public static final  String HAS_COMPANY_CATEGORY = "HAS_COMPANY_CATEGORY";

    public static final  String EMPLOYMENT_TYPE_SETTINGS = "EMPLOYMENT_TYPE_SETTINGS";

    //expertise
    public static final  String HAS_PROTECTED_DAYS_OFF_SETTINGS = "HAS_PROTECTED_DAYS_OFF_SETTINGS";

    // Group
    public static final  String HAS_TEAMS = "HAS_TEAMS";

    // Team
    public static final  String TEAM_HAS_SKILLS = "TEAM_HAS_SKILLS";
    public static final  String TEAM_HAS_SERVICES = "TEAM_HAS_SERVICES";
    public static final  String TEAM_HAS_LOCATION = "TEAM_HAS_LOCATION";
    public static final  String TEAM_HAS_MEMBER = "TEAM_HAS_MEMBER";

    // Skill ,Skill type, Skill Category , Country


    public static final  String STAFF_HAS_SKILLS = "STAFF_HAS_SKILLS";
    public static final  String HAS_HOLIDAY = "HAS_HOLIDAY";
    public static final  String HAS_CATEGORY = "HAS_CATEGORY";

    // UserRole with User & Role
    public static final  String HAS_PROFILE = "HAS_PROFILE";
    public static final  String HAS_USERROLE = "USER_IS";
    public static final  String HAS_ROLE_OF = "HAS_ROLE_OF";
    public static final  String ADMINS_COUNTRY = "ADMINS_COUNTRY";
    public static final  String MANAGE = "MANAGE";
    public static final  String HAS_STAFF = "HAS_STAFF";
    public static final  String ENGINEER_TYPE = "ENGINEER_TYPE";

    public static final  String ACCESS_GROUP_HAS_ACCESS_TO_PAGE = "ACCESS_GROUP_HAS_ACCESS_TO_PAGE";
    public static final  String STAFF_HAS_ACCESS_GROUP = "STAFF_HAS_ACCESS_GROUP";
    public static final  String ORGANIZATION_HAS_ACCESS_GROUPS = "ORGANIZATION_HAS_ACCESS_GROUPS";
    public static final  String UNIT_HAS_ACCESS_GROUPS = "UNIT_HAS_ACCESS_GROUPS";

    //client
    public static final  String GET_SERVICE_FROM = "GET_SERVICE_FROM";
    public static final  String ADDRESS_ACCESS_DEAILS = "ADDRESS_ACCESS_DEAILS";
    public static final  String HAS_OFFICE_ADDRESS = "HAS_OFFICE_ADDRESS";
    public static final  String HAS_HOME_ADDRESS = "HAS_HOME_ADDRESS";
    public static final  String HAS_SECONDARY_ADDRESS = "HAS_SECONDARY_ADDRESS";
    public static final  String HAS_PARTNER_ADDRESS = "HAS_PARTNER_ADDRESS";
    public static final  String HAS_TEMPORARY_ADDRESS = "HAS_TEMPORARY_ADDRESS";
    public static final  String HAS_CONTACT_DETAIL = "HAS_CONTACT_DETAIL";
    public static final  String HAS_LOCAL_AREA_TAG = "HAS_LOCAL_AREA_TAG";

    public static final  String HAS_DOCTOR = "HAS_DOCTOR";
    public static final  String HAS_DISEASE = "HAS_DISEASE";
    public static final  String HAS_DIAGNOSE = "HAS_DIAGNOSE";
    public static final  String HAS_ALLERGY = "HAS_ALLERGY";

    public static final  String UNIT_HAS_RESOURCE = "UNIT_HAS_RESOURCE";
    public static final  String ORGANIZATION_SUB_SERVICE = "ORGANIZATION_SUB_SERVICE";
    public static final  String LINK_WITH_EXTERNAL_SERVICE = "LINK_WITH_EXTERNAL_SERVICE";

    public static final String BELONGS_TO = "BELONGS_TO";
    public static final String IS_A = "IS_A";

    public static final String HAS_ORGANIZATION_SERVICES = "HAS_ORGANIZATION_SERVICES";
    public static final String CIVILIAN_STATUS = "CIVILIAN_STATUS";
    public static final String HAS_EXPERTISE_IN = "HAS_EXPERTISE_IN";
    public static final String STAFF_HAS_EXPERTISE = "STAFF_HAS_EXPERTISE";

    public static final  String SERVED_BY_STAFF = "SERVED_BY_STAFF";
    public static final  String SERVED_BY_TEAM = "SERVED_BY_TEAM";
    public static final  String SUB_PAGE = "SUB_PAGE";
    public static final  String HAS_POSITIONS = "HAS_POSITIONS";
    public static final  String HAS_LOCAL_AREA_TAGS = "HAS_LOCAL_AREA_TAGS";
    public static final  String LAT_LNG = "LAT_AND_LNG";
    public static final  String HAS_TIME_WINDOW = "HAS_TIME_WINDOW";
    public static final  String APPLICABLE_IN_UNIT = "APPLICABLE_IN_UNIT";
    public static final  String APPLICABLE_IN_ORGANIZATION = "APPLICABLE_IN_ORGANIZATION";
    public static final  String HAS_UNIT_PERMISSIONS = "HAS_UNIT_PERMISSIONS";
    public static final  String HAS_PARTIAL_LEAVES= "HAS_PARTIAL_LEAVES";
    public static final  String HAS_ACCESS_PAGE_PERMISSION = "HAS_ACCESS_PAGE_PERMISSION";
    public static final  String HAS_ACCESS_GROUP = "HAS_ACCESS_GROUP";
    public static final  String HAS_CUSTOMIZED_PERMISSION = "HAS_CUSTOMIZED_PERMISSION";
    public static final  String HAS_ACCESS_PERMISSION = "HAS_ACCESS_PERMISSION";
    public static final  String HAS_SUB_TYPE = "HAS_SUB_TYPE";
    public static final  String HAS_CONTACT_ADDRESS = "HAS_CONTACT_ADDRESS";
    public static final  String DAY_TYPE = "DAY_TYPE";

    //TimeCare
    public static final  String CONTACT_DETAIL = "CONTACT_DETAIL";
    public static final  String CONTACT_ADDRESS = "CONTACT_ADDRESS";
    public static final  String SECONDARY_CONTACT_ADDRESS = "SECONDARY_CONTACT_ADDRESS";
    public static final  String ZIP_CODE = "ZIP_CODE";
    public static final  String BUSINESS_TYPE = "BUSINESS_TYPE";
    public static final  String OWNERSHIP_TYPE = "OWNERSHIP_TYPE";
    public static final  String INDUSTRY_TYPE = "INDUSTRY_TYPE";
    public static final  String EMPLOYEE_LIMIT = "EMPLOYEE_LIMIT";
    public static final  String CONTRACT_TYPE = "CONTRACT_TYPE";
    public static final  String VAT_TYPE = "VAT_TYPE";
    public static final  String EXPERTISE_HAS_SKILLS = "EXPERTISE_HAS_SKILLS";
    public static final  String ORG_TYPE_HAS_SKILL = "ORG_TYPE_HAS_SKILL";
    public static final  String NEXT_TO_KIN = "NEXT_TO_KIN";
    public static final  String PEOPLE_IN_HOUSEHOLD_LIST = "PEOPLE_IN_HOUSEHOLD_LIST";
    public static final  String MUNICIPALITY = "MUNICIPALITY";
    public static final  String PROVINCE = "PROVINCE";
    public static final  String REGION = "REGION";
    public static final  String KAIROS_STATUS = "KAIROS_STATUS";
    public static final  String TYPE_OF_HOUSING = "TYPE_OF_HOUSING";
    public static final  String CLIENT_CONTACT_PERSON_RELATION_TYPE = "CLIENT_CONTACT_PERSON_RELATION_TYPE";
    public static final  String CLIENT_CONTACT_PERSON_STAFF = "CLIENT_CONTACT_PERSON_STAFF";
    public static final  String CLIENT_CONTACT_PERSON_SERVICE = "CLIENT_CONTACT_PERSON_SERVICE";

    // RULE Template
    public static final  String HAS_LEVEL = "HAS_LEVEL";
    public static final  String IN_ORGANIZATION_LEVEL = "IN_ORGANIZATION_LEVEL";
    public static final  String HAS_RELATION_TYPES = "HAS_RELATION_TYPES";
    public static final  String RELATION_TYPE = "RELATION_TYPE";
    public static final  String RELATION_WITH_NEXT_TO_KIN = "RELATION_WITH_NEXT_TO_KIN";
    public static final  String HAS_RELATION_OF = "HAS_RELATION_OF";
    public static final  String HAS_RESOURCES = "HAS_RESOURCES";

    public static final  String HAS_FAVOURITE_FILTERS = "HAS_FAVOURITE_FILTERS";
    public static final  String FILTER_DETAIL = "FILTER_DETAIL";
    public static final  String APPLICABLE_FOR = "APPLICABLE_FOR";
    public static final  String HAS_FILTER_GROUP = "HAS_FILTER_GROUP";

    public static final  String BELONGS_TO_STAFF = "BELONGS_TO_STAFF";
    public static final  String UNAVAILABLE_ON = "UNAVAILABLE_ON";
    public static final  String VEHICLE_HAS_FEATURE = "VEHICLE_HAS_FEATURE";
    public static final  String HAS_TIME_SLOT_SET = "HAS_TIME_SLOT_SET";
    public static final  String HAS_TIME_SLOT = "HAS_TIME_SLOT";

    public static final  String RESOURCE_HAS_FEATURE = "RESOURCE_HAS_FEATURE";
    public static final  String RESOURCE_HAS_EQUIPMENT = "RESOURCE_HAS_EQUIPMENT";
    public static final  String ORGANIZATION_HAS_UNIONS = "ORGANIZATION_HAS_UNIONS";

    public static final  String IN_UNIT = "IN_UNIT";
    public static final  String HAS_ACCESS_FOR_ORG_CATEGORY = "HAS_ACCESS_FOR_ORG_CATEGORY";
    public static final  String HAS_UNION = "HAS_UNION";
    public static final  String HAS_ORGANIZATION_LEVEL = "HAS_ORGANIZATION_LEVEL";

    public static final  String HAS_MUNICIPALITY = "HAS_MUNICIPALITY";
    public static final  String IN_LEVEL = "IN_LEVEL";
    public static final  String HAS_PAY_GRADE = "HAS_PAY_GRADE";
    public static final  String HAS_TEMP_PAY_TABLE = "HAS_TEMP_PAY_TABLE";
    public static final  String HAS_PAY_GROUP_AREA = "HAS_PAY_GROUP_AREA";
    public static final  String FOR_SENIORITY_LEVEL = "FOR_SENIORITY_LEVEL";
    public static final  String SUPPORTED_BY_UNION = "SUPPORTED_BY_UNION";
    public static final  String SUPPORTS_SERVICES = "SUPPORTS_SERVICES";
    public static final  String HAS_FUNCTION = "HAS_FUNCTION";
    public static final  String VERSION_OF = "VERSION_OF";
    public static final  String HAS_BASE_PAY_GRADE = "HAS_BASE_PAY_GRADE";
    public static final  String HAS_SENIORITY_LEVEL = "HAS_SENIORITY_LEVEL";
    public static final  String HAS_REASON_CODE = "HAS_REASON_CODE";
    public static final  String HAS_PAYMENT_SETTINGS = "HAS_PAYMENT_SETTINGS";
    public static final  String HAS_PERSONALIZED_SETTINGS = "HAS_PERSONALIZED_SETTINGS";
    public static final  String HAS_SENIOR_DAYS = "HAS_SENIOR_DAYS";
    public static final  String HAS_CHILD_CARE_DAYS = "HAS_CHILD_CARE_DAYS";
    public static final String APPLICABLE_FOR_EXPERTISE = "APPLICABLE_FOR_EXPERTISE";
    public static final String SENIORITY_LEVEL_FUNCTIONS = "SENIORITY_LEVEL_FUNCTIONS";
    public static final  String HAS_FUNCTIONAL_AMOUNT = "HAS_FUNCTIONAL_AMOUNT";
    public static final  String FUNCTIONAL_PAYMENT_MATRIX = "FUNCTIONAL_PAYMENT_MATRIX";
    public static final  String APPLIED_FUNCTION = "APPLIED_FUNCTION";
    public static final  String APPLICABLE_FUNCTION = "APPLICABLE_FUNCTION";
    public static final  String EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT = "EXPERTISE_HAS_PLANNED_TIME_FOR_EMPLOYMENT";
    public static final  String HAS_PREFERED_TIME_WINDOW = "HAS_PREFERED_TIME_WINDOW";
    public static final  String BELONGS_TO_SECTOR = "BELONGS_TO_SECTOR";
    public static final String HAS_EXPERTISE_LINES="HAS_EXPERTISE_LINES";

// System Langugae

    public static final  String HAS_SYSTEM_LANGUAGE = "HAS_SYSTEM_LANGUAGE";
    public static final  String SELECTED_LANGUAGE = "SELECTED_LANGUAGE";
    public static final  String IN_COUNTRY = "IN_COUNTRY";
    public static final  String HAS_ACCOUNT_TYPE = "HAS_ACCOUNT_TYPE";
    public static final  String HAS_ACCESS_OF_MODULE = "HAS_ACCESS_OF_MODULE";
    public static final  String HAS_UNIT_TYPE = "HAS_UNIT_TYPE";

    public static final  String HAS_EMPLOYMENT_LINES = "HAS_EMPLOYMENT_LINES";

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
