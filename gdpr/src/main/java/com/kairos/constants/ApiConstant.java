package com.kairos.constants;

public final class ApiConstant {


    public static final String API_V1 = "/api/v1";
    public static final String PARENT_ORGABNIZATION = "/organization/{organizationId}";
    public static final String COUNTRY_URL = "/basic_details/{countryId}";
    public static final String API_CLAUSES_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+"/clause";
    public static final String API_CLAUSE_TAG_URL = API_V1  + PARENT_ORGABNIZATION+COUNTRY_URL+"/clause_tag";
    public static final String API_ACCOUNT_TYPE_URL = API_V1 +PARENT_ORGABNIZATION+COUNTRY_URL+ "/account";
    public static final String API_TEMPLATE_TYPE_URL = API_V1 +COUNTRY_URL+ "/template";


    //master asset data
    public static final String API_MASTER_ASSET_URL = API_V1+ PARENT_ORGABNIZATION+COUNTRY_URL+ "/master_asset";
    public static final String API_STORAGE_FORMAT_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/storage_format";
    public static final String API_STORAGE_TYPE_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/storage_type";
    public static final String API_HOSTING_PROVIDER_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/hosting_provider";
    public static final String API_HOSTING_TYPE_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/hosting_type";
    public static final String API_ORG_SEC_MEASURE_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/organization_security";
    public static final String API_DATA_DISPOSAL_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/data_disposal";
    public static final String API_TECH_SECURITY_MEASURE_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/technical_security";

    //master processing activities
    public static final String API_MASTER_PROCESSING_ACTIVITY = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/master_processing_activity";
    public static final String API_PROCESSING_PURPOSE = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/processing_purpose";
    public static final String API_TRANSFER_METHOD = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/transfer_method";
    public static final String API_DATASOURCE_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/data_source";
    public static final String API_DATASUBJECT_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/data_subject";
    public static final String API_RESPONSIBILITY_TYPE= API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/responsibility_type";
    public static final String API_DESTINATION= API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+"/destination";
    public static final String API_PROCESSING_LEGAL_BASIS= API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/legal_basis";
    public static final String API_ACCESSOR_PARTY_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/accessor_party";

    //agreement template
    public static final String API_AGREEMENT_TEMPLATE_URl = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/agreement_template";
    public static final String API_AGREEMENT_SECTION_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/agreement_section";

    //filter
    public static final String API_FILTER= API_V1+ PARENT_ORGABNIZATION+COUNTRY_URL+ "/filter";

    //Master Questionnaire
    public static final String API_MASTER_QUESTION_SECTION= API_V1+ PARENT_ORGABNIZATION+COUNTRY_URL+ "/question_section";
    public static final String API_MASTER_QUESTIONNAIRE_TEMPLATE= API_V1+ PARENT_ORGABNIZATION+COUNTRY_URL+ "/questionnaire_template";



    //Data category & element and data subject Mapping
    public static final String API_DATA_CATEGORY_URL= API_V1+ PARENT_ORGABNIZATION+COUNTRY_URL+ "/data_category";
    public static final String API_DATA_ELEMENT_URL= API_V1+ PARENT_ORGABNIZATION+COUNTRY_URL+ "/data_element";
    public static final String API_DATA_SUBJECT_AND_MAPPING_URL= API_V1+ PARENT_ORGABNIZATION+COUNTRY_URL+ "/dataSubject_mapping";




    public static final String API_PROCESSING_ACTIVITY = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/processing_activity";
    public static final String API_ASSET_TYPE_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/asset_type";
    public static final String API_HOSTING_LOCATION_URL = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/hosting_location";



    private ApiConstant() {
    }
}
