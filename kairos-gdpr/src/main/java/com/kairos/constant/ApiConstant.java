package com.kairos.constant;

public final class ApiConstant {


    public static final String API_V1 = "/api/v1";
    public static final String API_CLAUSES_URL = API_V1 + "/clauses";
<<<<<<< HEAD
=======
    public static final String API_CLAUSES_ADD_CLAUSE = API_CLAUSES_URL + "/add_Clause";
>>>>>>> d97f2521641985b2ef7974bbd2871f4291a34c91

    public static final String API_ORGANIZATION = API_V1 + "/organization";
    public static final String API_ORGANIZATION_TYPE = API_ORGANIZATION + "/type";


// MasterAsset urls

    public static final String API_MASTER_ASSET_URL = API_V1 + "/master_asset";

//Global master processing activity

public static final String API_PROCESSING_ACTIVITY = API_V1 + "/processing_activity";
public static final String API_MASTER_PROCESSING_ACTIVITY = API_V1+"/master_processing_activity";




    //Account type url
    public static final String API_ACCOUNT_TYPE_URL = API_V1 + "/account";


    //Agreement template
    public static final String API_AGREEMENT_TEMPLATE_URl = API_V1 + "/agreement_template";


    //Processing Purpose
    public static final String API_PROCESSING_PURPOSE = API_V1 + "/processing_purpose";

    //transfer method
    public static final String API_TRANSFER_METHOD = API_V1 + "/transfer_method";

    //data source url
    public static final String API_DATASOURCE_URL = API_V1 + "/data_source";


    //data subject url
    public static final String API_DATASUBJECT_URL = API_V1 + "/data_subject";

    //AssetType url
    public static final String API_ASSET_TYPE_URL = API_V1 + "/assetType";

    //Hosting location url
    public static final String API_HOSTING_LOCATION_URL = API_V1 + "/hosting_location";


    //Hosting provider url
    public static final String API_HOSTING_PROVIDER_URL = API_V1 + "/hosting_provider";


    //Organizational Security Measure  url
    public static final String API_ORG_SEC_MEASURE_URL = API_V1 + "/organization_security";

    //Technical Security Measure  url
    public static final String API_STORAGE_FORMAT_URL = API_V1 + "/storage_format";


    //Technical Security Measure  url
    public static final String API_TECH_SEC_MEASURE_URL = API_V1 + "/technical_security";

//clause tag
public static final String API_CLAUSE_TAG_URL = API_V1 + "/clause/tag";

    private ApiConstant() {
    }
}
