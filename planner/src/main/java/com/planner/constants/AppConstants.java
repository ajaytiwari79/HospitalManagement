package com.planner.constants;



public class AppConstants {

    /**
     * @author pradeep
     * @date - 28/6/18
     */
    public static final String DROOL_FILE_EXTENTION = ".drl";
    public static final String DROOL_BASE_FILE = "Base_vrp_task_rules";

    /**
     * Neo4j Configuration constants
     *
     * @author mohit
     */
    public static final String NEO4J_URI = "spring.data.neo4j.uri";
    public static final String NEO4J_USER_NAME = "spring.data.neo4j.username";
    public static final String NEO4J_PASSWORD = "spring.data.neo4j.password";
    public static final String CONNECTION_POOL_SIZE = "spring.data.neo4j.connection.pool.size";

    /**
     *MongoDb {Collections names} Constants for Activity micro-service
     * @author mohit
     * @date 31-8-17
     */
    public static final String STAFFING_LEVEL="staffing_level";
    public static final String ACTIVITYIES="activities";
    public static final String COST_TIME_AGGREMENET="costTimeAgreement";
    public static final String CTA_RULE_TEMPLATE="cTARuleTemplate";
    public static final String Working_Time_AGREEMENT="workingTimeAgreement";
    public static final String WTABASE_TEMPLATE="wtaBaseRuleTemplate";
    public static final String SHIFTS="shifts";

    /**
     * Neo4j RelationShip constants
     */
    public static final String STAFF_HAS_SKILLS="STAFF_HAS_SKILLS";
    public static final String BELONGS_TO_STAFF="BELONGS_TO_STAFF";
    public static final String HAS_EXPERTISE_IN="HAS_EXPERTISE_IN";
    public static final String IN_UNIT="IN_UNIT";
    public static final String HAS_ORGANIZATION_SERVICES="HAS_ORGANIZATION_SERVICES";
    public static final String ORGANIZATION_SUB_SERVICE="ORGANIZATION_SUB_SERVICE";
}
