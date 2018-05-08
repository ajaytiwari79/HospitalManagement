package com.kairos.constant;

public final class ApiConstant {


    public static final String API_V1 ="/api/v1";
    public static final String API_CLAUSES_URL =API_V1+"/clauses";
    public static final String API_CLAUSES_ADD_CLAUSE =API_CLAUSES_URL+"/add_Clause";

    public static final String API_ORGANIZATION=API_V1+"/organization";
    public static final String API_ORGANIZATION_TYPE=API_ORGANIZATION+"/type";



// Asset urls
    public static final String API_ASSET_URL=API_V1+"/asset";



    //Account type url
    public static final String API_ACCOUNT_TYPE_URL=API_V1+"/account";


    //Agreement template
    public static final String API_AGREEMENT_TEMPLATE_URl=API_V1+"/agreement/template";



    private ApiConstant() {
    }
}
