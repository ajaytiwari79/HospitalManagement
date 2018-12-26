package com.kairos.scheduler.constants;

public class ApiConstants {
    public static final String API_V1 ="/api/v1";
    public final static String UNIT_URL = API_V1+"/unit/{unitId}";
    public final static String API_SCHEDULER_URL = UNIT_URL + "/scheduler_panel";
    public final static String API_UNIT_TIMEZONE_MAPPING_URL = UNIT_URL + "/unit_timezone_mapping";

    public final static String SCHEDULER_EXECUTE_JOB = "/scheduler_execute_job";
    public final static String JOB_DETAILS = "/job_details";


}
