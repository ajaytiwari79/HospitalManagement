package com.planner.constants;

public class ApiConstants {
    public static final String API_V1 ="/api/v1";
    //public static final String PARENT_ORGANIZATION_URL = "/organization/{organizationId}";
    public static final String UNIT_URL = "/unit/{unitId}";
    public static final String COUNTRY_URL = "/country/{countryId}";
    public static final String SOLVER_CONFIG_URL ="/solver_config";
    public static final String PLANNING_PROBLEM_URL ="/planning_problem";
    public static final String CONSTRAINT_URL ="/constraint";
    public static final String API_UNIT_URL = API_V1  + UNIT_URL;
    public static final String SHIFTPLANNING = "/shift_planning";
    public static final String API_PARENT_ORGANIZATION_COUNTRY_URL=API_V1+COUNTRY_URL;
    public static final String API_PARENT_ORGANIZATION_UNIT_URL=API_V1+UNIT_URL;
    public static final String API_PARENT_ORGANIZATION_COUNTRY_SOLVER_CONFIG_URL=API_PARENT_ORGANIZATION_COUNTRY_URL+SOLVER_CONFIG_URL;
    public static final String API_PARENT_ORGANIZATION_UNIT_SOLVER_CONFIG_URL=API_PARENT_ORGANIZATION_UNIT_URL+SOLVER_CONFIG_URL;
    public static final String API_PARENT_ORGANIZATION_COUNTRY_PLANNING_PROBLEM_URL=API_PARENT_ORGANIZATION_COUNTRY_URL+PLANNING_PROBLEM_URL;
    public static final String API_PARENT_ORGANIZATION_COUNTRY_CONSTRAINT_URL =API_PARENT_ORGANIZATION_COUNTRY_URL+CONSTRAINT_URL;
    public static final String API_PARENT_ORGANIZATION_UNIT_CONSTRAINT_URL =API_PARENT_ORGANIZATION_UNIT_URL+CONSTRAINT_URL;
}
