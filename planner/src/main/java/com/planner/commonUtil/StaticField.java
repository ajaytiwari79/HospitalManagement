package com.planner.commonUtil;

public class StaticField {


	/*Planning*/
	public final static String FILEPATH = "src/JsonFile";
	public final static String KIESERVERURL = "http://localhost:8080/kie-server/services/rest/server/containers/";
	public final static String GROUPID = "com.kairos.planner";
	public final static String ARTIFACTID = "task-planner";
	public final static String VERSION = "1.0.2-SNAPSHOT";
	public final static String SERVERCONFIGFILE = "com/kairos/planning/configuration/OutdoorTaskPlanning.solver.xml";
	public final static String AUTHORIZATION = "Basic a2llc2VydmVyOmtpZXNlcnZlcg==";
	public final static String KAIROSURL = "http://localhost:8090/kairos/activity/";
	public final static String SAVE_SUCCESS = "saveEntity Data successFully";
	public final static String FETCH_SUCCESS = "fetch Data successFully";
	public final static String UPDATE_SUCCESS = "Update Data sucessFully";
	public final static String DELETE_SUCCESS = "Delete sucessFully";
	public final static String VERIFY_ADDRESS_SUCCESS = "verify Address successFully";
	public final static String PLANNING = "api/taskPlanning/planner";
	public final static String INTEGRATION = "api/taskPlanning/integration";
	public final static String TASKPLANNING_XMLPATH = "optaplanner/src/test/resources/com/kairos/planner/configuration/OutdoorTaskPlanning.solver.xml";
	public final static String SHIFT_PLANNING_XMLPATH = "optaplanner-shiftplanning/src/main/resources/com/kairos/shiftplanning/configuration/ShiftPlanning_Request.solver.xml";


	/*Graph Hopper*/
	public final static String CAR = "car";
	public final static String RACINGBIKE = "racingbike";


	/*Drools*/
	public final static String DROOLSFILEPATH = "src/main/resources/droolsFile/";
	public final static String BENDABLEIMPORT = "org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScoreHolder;";
	public final static String HARDMEDIUMSOFTIMPORT = "org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;";
	public final static String SHIFT_PLANNING_UTILITY = "com.kairos.shiftplanning.utils.ShiftPlanningUtility;";
	public final static String COM_KAIROS_PLANNING_DOMAIN = "com.kairos.planner.domain.*;";
	public final static String COM_KAIROS_SHIFTPLANNING_DOMAIN= "com.kairos.shiftplanning.domain.*;";
	public final static String UTIL_ARRAYLIST = "java.util.ArrayList;";
	public final static String COM_KAIROS_PLANNING_RULES = "com.kairos.planner.rules";
	public final static String COM_KAIROS_SHIFT_PLANNING_RULES = "com.kairos.shiftplanning.rules;";
	public final static String BENDABLE_LONG_SCORE_HOLDER = "BendableLongScoreHolder";
	public final static String HARD_MEDIUM_SOFT_SCORE_HOLDER = "HardMediumSoftLongScoreHolder";
	public final static String SCORE_HOLDER = "scoreHolder;";
	public final static String HARDCONSTRAINT = "scoreHolder.addHardConstraintMatch(kcontext,";
	public final static String SOFTCONSTRAINT = "scoreHolder.addSoftConstraintMatch(kcontext,";
	public final static String MEDIUMCONSTRAINT = "scoreHolder.addMediumConstraintMatch(kcontext,";
	public final static String DROOLFILEEXTENSION = ".drl";
	public final static String HARD = "Hard";
	public final static String MEDIUM = "Medium";
	public final static String SALIENCE = "salience";
	public final static String DIALECT = "dialect";
	public final static String JAVA = "java";









    public static final String DRL_BASE_SRC = "com/kairos/shiftplanning/rules/";
    public static final String DRL_WTA_BASE_SRC = DRL_BASE_SRC+"wta/";
	public final static String DRL_AVERAGE_SHEDULED_TIME = DRL_WTA_BASE_SRC+"java";
	public final static String DRL_CONSECUTIVE_WORKING_PARTOFDAY = DRL_WTA_BASE_SRC+"java";
	public final static String DRL_DAYS_OFF_IN_PERIOD = DRL_WTA_BASE_SRC+"java";
	public final static String DRL_NUMBER_OF_PARTOFDAY = DRL_WTA_BASE_SRC+"java";
	public final static String DRL_SHIFT_LENGTH = DRL_WTA_BASE_SRC+"java";
	public final static String DRL_NUMBER_OF_SHIFTS_IN_INTERVAL = DRL_WTA_BASE_SRC+"java";
	public final static String DRL_TIME_BANK = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_VETO_PER_PERIOD = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_DAILY_RESTING_TIME = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_DURATION_BETWEEN_SHIFTS = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_REST_IN_CONSECUTIVE_DAYS_AND_NIGHTS = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_WEEKLY_REST_PERIOD = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_NUMBER_OF_WEEKEND_SHIFT_IN_PERIOD = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_SHORTEST_AND_AVERAGE_DAILY_REST = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_SENIOR_DAYS_PER_YEAR = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_CHILD_CARE_DAYS_CHECK = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_DAYS_OFF_AFTER_A_SERIES = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_NO_OF_SEQUENCE_SHIFT = DRL_WTA_BASE_SRC+"java";
    public final static String DRL_EMPLOYEES_WITH_INCREASE_RISK = DRL_WTA_BASE_SRC+"java";
    public static final String BASE_SOLVER_CONFIG_FIRST_PHASE = "com/kairos/shiftplanning/configuration/BASE_SHIFT_PLANNING_PHASE1_SOLVER.xml";
    public static final String SOLVER_CONFIG_DRL_PARENT_TAG="scroreDirectorFactory";

    //Vrp planning
	public static final String VRPPROBLEM_SUBMIT = "Vrp problem Submited";


}
