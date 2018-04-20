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
	public final static String SAVE_SUCCESS = "save Data successFully";
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
}
