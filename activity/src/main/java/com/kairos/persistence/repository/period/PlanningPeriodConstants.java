package com.kairos.persistence.repository.period;

public class PlanningPeriodConstants {
    public static final String ACTIVE = "active";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String UNIT_ID = "unitId";
    public static final String DELETED = "deleted";
    public static final String DURATION = "duration";
    public static final String DURATION_TYPE = "durationType";
    public static final String PHASE_FLIPPING_DATE = "phaseFlippingDate";
    public static final String PUBLISH_EMPLOYMENT_IDS = "publishEmploymentIds";
    public static final String CURRENT_PHASE_DATA_NAME = "current_phase_data.name";
    public static final String CURRENT_PHASE = "currentPhase";
    public static final String NEXT_PHASE_DATA_NAME = "next_phase_data.name";
    public static final String PHASES = "phases";
    public static final String CURRENT_PHASE_ID = "currentPhaseId";
    public static final String CURRENT_PHASE_DATA = "current_phase_data";
    public static final String NEXT_PHASE_ID = "nextPhaseId";
    public static final String NEXT_PHASE_DATA = "next_phase_data";
    public static final String PHASE = "phase";
    public static final String DATE_RANGE = "dateRange";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PHASE_ID = "phaseId";
    public static final String CURRENT_PHASE_NAME = "currentPhaseName";
    public static final String NEXT_PHASE_NAME = "nextPhaseName";
    public static final String ID1 = "_id";
    public static final String NEXT_PHASE = "nextPhase";
    public static final String DATA = "data";
    public static final String PROJECT_OPERATION = "{\"$project\":{\n" +
            "    \"planningPeriod.lastPlanningPeriodEndDate\":{ $arrayElemAt: [ \"$lastPlanningPeriod.endDate\", 0 ] },    \n" +
            "    \"planningPeriod.startDate\" : \"$startDate\",\n" +
            "    \"planningPeriod.endDate\" : \"$endDate\",\n" +
            "    \"planningPeriod.currentPhaseId\" : \"$currentPhaseId\",\n" +
            "    \"planningPeriod.nextPhaseId\" : \"$nextPhaseId\",\n" +
            "    \"planningPeriod.duration\" : \"$duration\",\n" +
            "    \"planningPeriod.durationType\" : \"$durationType\",\n" +
            "    \"_id\":0\n" +
            "    }\n" +
            "    }";
    public static final String PROJECT_FOR_PLANNING = "{$project:{\n" +
            "          \"planningPeriod.startDate\":\"$startDate\",\n" +
            "          \"planningPeriod.endDate\":\"$endDate\",\n" +
            "          \"planningPeriod.id\":\"$_id\",\n" +
            "          \"planningPeriod.unitId\":\"$unitId\",\n" +
            "          \"planningPeriod.currentPhaseId\":\"$currentPhaseId\",\n" +
            "          \"shifts\":1,\n" +
            "          \"activityConfiguration\":1,\n" +
            "          \"staffingLevels\":1,\n" +
            "          \"activities\":1\n" +
            "          }}";
    public static final String ACTIVITY_CONFIGURATION = "{\n" +
            "        $lookup:{\n" +
            "         from: \"activityConfiguration\",\n" +
            "            let: { unitId: \"$unitId\",phaseId:\"$currentPhaseId\" },\n" +
            "         pipeline: [\n" +
            "              { $match:\n" +
            "                 { $expr:\n" +
            "                    { $or:\n" +
            "                       [\n" +
            "                         {$eq:[\"$presencePlannedTime.phaseId\",\"$$phaseId\"]},\n" +
            "                         {$eq:[\"$absencePlannedTime.phaseId\",\"$$phaseId\"]},\n" +
            "                         {$eq:[\"$nonWorkingPlannedTime.phaseId\",\"$$phaseId\"]}\n" +
            "                       ]\n" +
            "                    }\n" +
            "                 }\n" +
            "              },{\n" +
            "                  $project:{\n" +
            "                      \"presencePlannedTime\":1,\n" +
            "                      \"absencePlannedTime\":1,\n" +
            "                      \"nonWorkingPlannedTime\":1,\n" +
            "                      \"_id\":0\n" +
            "                      }\n" +
            "                  }\n" +
            "           ],\n" +
            "            as: \"activityConfiguration\"\n" +
            "            }\n" +
            "        }";
    public static final String STAFFING_LEVEL_LOOKUP = "{\n" +
            "        $lookup:{\n" +
            "            \n" +
            "         from: \"staffing_level\",\n" +
            "            let: { startDate: \"$startDate\", endDate: \"$endDate\",unitId: \"$unitId\" },\n" +
            "         pipeline: [\n" +
            "              { $match:\n" +
            "                 { $expr:\n" +
            "                    { $and:\n" +
            "                       [\n" +
            "                         { $gte: [ \"$currentDate\",  \"$$startDate\" ] },\n" +
            "                         { $lte: [ \"$currentDate\", \"$$endDate\" ] },\n" +
            "                         {$eq:[\"$unitId\",\"$$unitId\"]}\n" +
            "{\n" +
            "                  \"$eq\": [\n" +
            "                    \"$deleted\",\n" +
            "                    false\n" +
            "                  ]\n" +
            "                }" +
            "                       ]\n" +
            "                    }\n" +
            "                 }\n" +
            "              }\n" +
            "           ],\n" +
            "            as: \"staffingLevels\"\n" +
            "            }\n" +
            "        }";
    public static final String ACTIVITIES_LOOKUP = "{\n" +
            "        $lookup:{\n" +
            "            \n" +
            "         from: \"activities\",\n" +
            "            let: { activityIds: \"$activityIds\",unitId:\"$unitId\" },\n" +
            "         pipeline: [\n" +
            "              { $match:{ $expr:\n" +
            "                    { $and:\n" +
            "                       [\n" +
            "                         { $in: [ \"$_id\",  \"$$activityIds\" ] },\n" +
            "                         \n" +
            "                       ]\n" +
            "                    }\n" +
            "                 }\n" +
            "           \n" +
            "              },{\n" +
            "                  \"$project\":{\"name\":1,\"activityBalanceSettings\":1,\"activityRulesSettings\":1,\"activityTimeCalculationSettings\":1,\"activitySkillSettings\":1,\"tags\":1,\"employmentTypes\":1}\n" +
            "                  }\n" +
            "           ],\n" +
            "            as: \"activities\"\n" +
            "            }\n" +
            "        }";
}
