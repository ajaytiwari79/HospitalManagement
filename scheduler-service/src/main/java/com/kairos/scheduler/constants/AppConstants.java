package com.kairos.scheduler.constants;

import com.kairos.enums.scheduler.JobSubType;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.enums.scheduler.JobSubType.*;

public class AppConstants {

    public static final String API_TIME_CARE_SHIFTS = "/api/v1/time_care/getShifts";
    public static final String API_TIME_CARE_ACTIVITIES = "/api/v1/time_care/getWorkPlaces";
    public static final String API_KMD_CARE_CITIZEN = "/api/v1/kmdNexus/citizen/preferences/{unitId}";
    public static final String KMD_CARE_CITIZEN_URL = "/api/v1/kmdNexus/citizen/preferences/";
    public static final String API_KMD_CARE_CITIZEN_GRANTS = "/api/v1/kmdNexus/citizen/grants";
    public static final String API_KMD_CARE_CITIZEN_RELATIVE_DATA = "/api/v1/kmdNexus/citizen/nextToKin";
    public static final String FORWARD_SLASH = "/";

    public static final String API_KMD_CARE_STAFF_SHIFTS = "/api/v1/kmdNexus/citizen/unit/{unitId}/getShifts/{filterId}";
    public static final String API_KMD_CARE_TIME_SLOTS = "/api/v1/kmdNexus/citizen/unit/{unitId}/getTimeSlots";
    public static final String API_KMD_CARE_URL = "/api/v1/kmdNexus/citizen/unit/";
    public static final String API_TIME_SLOTS_NAME = "/api/v1/organization/{organizationId}/unit/{unitId}/time_slot_name";
    //Kettle commands
    public static final String KETTLE_TRANS_STATUS = "/kettle/transStatus/?name=GetAllWorkShiftsByWorkPlaceId&xml=y";
    public static final String KETTLE_EXECUTE_TRANS = "/kettle/executeTrans/?trans=";

    //Transformation Paths
    //development and production
    public static final String IMPORT_TIMECARE_SHIFTS_PATH = "/opt/infra/data-integration/GetAllWorkShiftsByWorkPlaceId.ktr";
    public final static String SCHEDULER_PANEL_INTERVAL_STRING = ". Every {0} minutes during selected hours.";
    public final static String SCHEDULER_PANEL_RUN_ONCE_STRING = ". At {0}.";
    public final static String USER_TO_SCHEDULER_JOB_QUEUE_TOPIC = "UserToSchedulerJobQueue";
    public final static String USER_TO_SCHEDULER_LOGS_QUEUE_TOPIC = "UserToSchedulerLogsQueue";
    public final static String SCHEDULER_TO_USER_QUEUE_TOPIC = "SchedulerToUserQueue";
    public final static String SCHEDULER_TO_ACTIVITY_QUEUE_TOPIC = "SchedulerToActivityQueue";


    public final static Set<JobSubType> userSubTypes = Stream.of(INTEGRATION,EMPLOYMENT_END,QUESTIONAIRE_NIGHTWORKER).collect(Collectors.toSet());
    public final static Set<JobSubType> activitySubTypes = Stream.of(PRIORITYGROUP_FILTER).collect(Collectors.toSet());


}
