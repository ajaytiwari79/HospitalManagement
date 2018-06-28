package com.planner.constants;

public class ApiConstants {
    public static final String API_V1 ="/api/v1";
    public static final String PARENT_ORGANIZATION_URL = "/organization/{organizationId}";
    public static final String UNIT_URL = "/unit/{unitId}";
    public static final String COUNTRY_URL = "/basic_details/{countryId}";
    public static final String API_ORGANIZATION_URL =  API_V1 + PARENT_ORGANIZATION_URL;
    public static final String API_ORGANIZATION_UNIT_URL = API_ORGANIZATION_URL + UNIT_URL;
    public static final String API_UNIT_URL = API_V1  + UNIT_URL;
}
