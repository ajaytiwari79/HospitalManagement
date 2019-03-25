package com.kairos.scheduler.constants;

import com.kairos.enums.scheduler.JobSubType;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kairos.enums.scheduler.JobSubType.*;

public class AppConstants {


    public final static String SCHEDULER_PANEL_INTERVAL_STRING = ". Every {0} minutes during selected hours.";
    public final static String SCHEDULER_PANEL_RUN_ONCE_STRING = ". At {0}.";
    public final static String USER_TO_SCHEDULER_JOB_QUEUE_TOPIC = "UserToSchedulerJobQueue";
    public final static String USER_TO_SCHEDULER_LOGS_QUEUE_TOPIC = "UserToSchedulerLogsQueue";
    public final static String SCHEDULER_TO_USER_QUEUE_TOPIC = "SchedulerToUserQueue";
    public final static String SCHEDULER_TO_ACTIVITY_QUEUE_TOPIC = "SchedulerToActivityQueue";
    public static final String ACTIVITY_TO_SCHEDULER_JOB_QUEUE_TOPIC="activityToSchedulerJobQueue";
    public static final String ACTIVITY_TO_SCHEDULER_LOGS_QUEUE_TOPIC="activityToSchedulerLogQueue";

    public static final String JOB_TO_CHECK_SICK_USER = "JOB_TO_CHECK_SICK_USER";
    public final static Set<JobSubType> userSubTypes = Stream.of(INTEGRATION, EMPLOYMENT_END, QUESTIONAIRE_NIGHTWORKER, SENIORITY_LEVEL).collect(Collectors.toSet());
    public final static Set<JobSubType> activitySubTypes = Stream.of(PRIORITYGROUP_FILTER, FLIP_PHASE, SHIFT_REMINDER, UPDATE_USER_ABSENCE,ATTENDANCE_SETTING, ADD_PAYROLL_PERIOD,ADD_PLANNING_PERIOD).collect(Collectors.toSet());

    //Mail constansts
    public static final String HOST = "mail.server.host";
    public static final String PORT = "mail.server.port";
    public static final String PROTOCOL = "mail.server.protocol";
    public static final String MAIL_USERNAME = "mail.server.username";
    public static final String MAIL_AUTH = "mail.server.password";
    public static final String BODY = "Body";
    public static final String TO = "To";
    public static final String FROM = "From";
    public static final String TIMEZONE_UTC = "UTC";


}
