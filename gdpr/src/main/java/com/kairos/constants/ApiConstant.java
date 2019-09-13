package com.kairos.constants;

public final class ApiConstant {


    public static final String API_V1 = "/api/v1";
    public static final String UNIT_URL = "/unit/{unitId}";
    public static final String COUNTRY_URL = "/country/{countryId}";
    public static final String API_ORGANIZATION_URL =API_V1;
    public static final String API_ORGANIZATION_COUNTRY_URL =API_V1 +COUNTRY_URL;
    public static final String API_ORGANIZATION_UNIT_URL =API_V1 +UNIT_URL;
    public static final String GET_ORGANIZATION_IDS_ORGANIZATION_SUB_TYPE_Id = "/organization_type/organizations";


    private ApiConstant() {
    }
}
