package com.kairos.constants;

public final class ApiConstant {


    public static final String API_V1 = "/api/v1";
    public static final String PARENT_ORGABNIZATION = "/organization/{organizationId}";
    public static final String UNIT_URL = "/unit/{unit}";
    public static final String COUNTRY_URL = "/country/{countryId}";
    public static final String API_ORGANIZATION_URL=API_V1+PARENT_ORGABNIZATION +COUNTRY_URL;
    public static final String API_ACCOUNT_TYPE_URL = API_V1 +PARENT_ORGABNIZATION+COUNTRY_URL+ "/account";
    public static final String API_TEMPLATE_TYPE_URL = API_V1 +COUNTRY_URL+ "/template";
    public static final String API_AGREEMENT_TEMPLATE_URl = API_V1 + PARENT_ORGABNIZATION+COUNTRY_URL+ "/agreement_template";


    private ApiConstant() {
    }
}
