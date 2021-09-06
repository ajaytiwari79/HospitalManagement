package com.kairos.persistence.repository.activity;

public class ActivityConstants {
    public static final String CHILD_ACTIVITIES_ACTIVITY_PRIORITY_ID = "childActivities.activityPriorityId";
    public static final String CHILD_ACTIVITIES_CATEGORY_ID = "childActivities.categoryId";
    public static final String STAFF_ID = "staffId";
    public static final String ACTIVITY_IDS = "activityIds";
    public static final String ACTIVITY_ID = "activityId";
    public static final String ACTIVITY_RULES_SETTINGS = "activityRulesSettings";
    public static final String CUSTOM_AGGREGATION_OPERATION_FOR_ACTIVITIES = "{\n" +
            "      \"$lookup\": {\n" +
            "        \"from\": \"activities\",\n" +
            "        \"let\": {\n" +
            "          \"activityIds\": \"$activityIds\"\n" +
            "        },\n" +
            "        \"pipeline\": [\n" +
            "          {\n" +
            "            \"$match\": {\n" +
            "              \"$expr\": {\n" +
            "                \"$and\": [\n" +
            "                  {\n" +
            "                    \"$in\": [\n" +
            "                      \"$_id\",\n" +
            "                      \"$$activityIds\"\n" +
            "                    ]\n" +
            "                  }\n" +
            "                ]\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "          \"$project\":{\n" +
            "              \"id\":1,\n" +
            "              \"name\":1,\n" +
            "              \"activityGeneralSettings\":1,\n" +
            "              \"activityTimeCalculationSettings\":1,\n" +
            "              \"activityRulesSettings\":1,\n" +
            "              \"parentActivityId\":1,\n" +
            "              \"activityPriorityName\":1,\n" +
            "              \"activityPhaseSettings\":1,\n" +
            "              \"activitySkillSettings\":1,\n" +
            "              \"expertises\":1,\n" +
            "              }    \n" +
            "              }\n" +
            "        ],\n" +
            "        \"as\": \"activities\"\n" +
            "      }\n" +
            "    }";

    public static final String GROUP_BY_ACTIVITY_CATEGORY = "{\n" +
            "     $group : { _id : \"$activityCategory\", activities: { $push: \"$$ROOT\" } }\n" +
            "   }";
    public static final String PROJECT_ACTIVITY_CATEGORY = "{$project:{\n" +
            "       \"activityCategory._id\": \"$_id.categoryId\",\n" +
            "        \"activityCategory.name\": \"$_id.categoryName\",\n" +
            "       \"activities\":1,\n" +
            "       \"_id\":0\n" +
            "       }}";
    public static final String NAME = "$name";
    public static final String DESCRIPTION = "$description";
    public static final String UNIT_ID = "$unitId";
    public static final String PARENT_ID = "$parentId";
    public static final String ACTIVITY_PRIORITY_ID = "$activityPriorityId";
    public static final String TAGS = "tags";
    public static final String TAG = "tag";
    public static final String TIME_TYPE_ACTIVITY_CAN_BE_COPIED_FOR_ORGANIZATION_HIERARCHY = "timeType.activityCanBeCopiedForOrganizationHierarchy";
    public static final String METHOD_FOR_CALCULATING_TIME = "methodForCalculatingTime";
    public static final String ACTIVITY_CAN_BE_COPIED_FOR_ORGANIZATION_HIERARCHY = "activityCanBeCopiedForOrganizationHierarchy";
    public static final String PARENT_ACTIVITY = "parentActivity";
    public static final String PARENT_ACTIVITY_ID = "parentActivity._id";
    public static final String TIME_TYPE_INFO_PART_OF_TEAM = "timeTypeInfo.partOfTeam";
    public static final String ID = "id";
    public static final String STAFF_ACTIVITY_SETTING = "staffActivitySetting";
    public static final String STAFF = "staff";
    public static final String STAFF_IDS = "staffIds";
    public static final String STAFF_STAFF_ID = "staff.staffId";
    public static final String ACTIVITY_BALANCE_SETTINGS_TIME_TYPE_TIME_TYPES = "activityBalanceSettings.timeType.timeTypes";
    public static final String NON_WORKING_TYPE = "NON_WORKING_TYPE";
    public static final String ACTIVITY_ORGANIZATION_TYPES = "activity.organizationTypes";
    public static final String ACTIVITY_REGIONS = "activity.regions";
    public static final String REGIONS = "regions";
    public static final String LEVELS = "levels";
    public static final String ACTIVITY_LEVELS = "activity.levels";
    public static final String ACTIVITY_TAGS = "activity.tags";
    public static final String ACTIVITY_ORGANIZATION_SUB_TYPES = "activity.organizationSubTypes";
    public static final String ACTIVITY_RULES_SETTINGS_SICKNESS_SETTING_VALID = "activityRulesSettings.sicknessSettingValid";
    public static final String ACTIVITY_CHILD_ACTIVITY_IDS = "activity.childActivityIds";
    public static final String ACTIVITY_ACTIVITY_PRIORITY_ID = "activity.activityPriorityId";
    public static final String ID1 = "_id";
    public static final String ACTIVITY_DESCRIPTION = "activity.description";
    public static final String ACTIVITY_COUNTRY_PARENT_ID = "activity.countryParentId";
    public static final String GROUP = "{  \n" +
            "      \"$group\":{  \n" +
            "         \"_id\":{  \n" +
            "            \"activityTimeCalculationSettings\":\"$activityTimeCalculationSettings\",\n" +
            "            \"activityBalanceSettings\":\"$activityBalanceSettings\",\n" +
            "            \"name\":\"$name\",\n" +
            "            \"activityPriorityId\":\"$activityPriorityId\",\n" +
            "            'timeTypeInfo':'$timeTypeInfo',\n" +
            "            'activityPriority':'$activityPriority',\n" +
            "            'compositeTimeTypeInfo':'$compositeTimeTypeInfo',\n" +
            "             \"id\":\"$_id\",\n" +
            "            \"categoryId\":\"$categoryId\",\n" +
            "             \"translations\":\"$translations\",\n" +
            "            \"categoryName\":\"$categoryName\"\n" +
            "         },\n" +
            "         \"childActivities\":{  \n" +
            "            \"$addToSet\":\"$childActivities\"\n" +
            "         }\n" +
            "      }\n" +
            "   }";
    public static final String PROJECT = "{  \n" +
            "      \"$project\":{  \n" +
            "         \"activityId\":\"$_id._id\",\n" +
            "         \"phaseTemplateValues\":\"$phaseTemplateValues\"\n" +
            "      }\n" +
            "   }";
    public static final String PHASE_TEMPLATE_VALUES = "phaseTemplateValues";
    public static final String PHASE_TEMPLATE_VALUES_ACTIVITY_SHIFT_STATUS_SETTINGS_ACCESS_GROUP_IDS = "phaseTemplateValues.activityShiftStatusSettings.accessGroupIds";
    public static final String PHASE_TEMPLATE_VALUES_PHASE_ID = "phaseTemplateValues.phaseId";
    public static final String GROUP_BY_PHASETEMPLATES = " {  \n" +
            "      \"$group\":{  \n" +
            "         \"_id\":{  \n" +
            "            \"_id\":\"$_id\"\n" +
            "         },\n" +
            "         \"phaseTemplateValues\":{  \n" +
            "            \"$addToSet\":\"$phaseTemplateValues\"\n" +
            "         }\n" +
            "      }\n" +
            "   }";
    public static final String TIME_TYPE_PART_OF_TEAM = "timeType.partOfTeam";
    public static final String TIME_TYPE_SECOND_LEVEL_TYPE = "timeType.secondLevelType";
    public static final String SECOND_LEVEL_TYPE = "secondLevelType";
    public static final String CONCATE_ARRAY = "{\n" +
            "        $project:{\n" +
            "            \"_id\":0,\n" +
            "            \"activityIds\": {\n" +
            "          \"$concatArrays\": [\"$activityIds\",\"$otherActivityIds\"]\n" +
            "        }\n" +
            "            }\n" +
            "        }";
    public static final String $_NE_$_CHILD_ACTIVITY_IDS = "                { \"$ne\": [ \"$childActivityIds\", [] ] },\n";
    public static final String ARRAY_ASSIGNMENT = "{\n" +
            "      \"$project\": {\n" +
            "        \"activityIds\": 1,\n" +
            "        \"otherActivityIds\": \n" +
            "               {\n" +
            "                 $cond: { if: { $ne: [ \"$activities\", [] ] }, then: {\n" +
            "          \"$arrayElemAt\": [\n" +
            "            \"$activities.activityIds\",\n" +
            "            0\n" +
            "          ]\n" +
            "        }, else: [] }\n" +
            "               }\n" +
            "          \n" +
            "      }\n" +
            "    }";
    public static final String REPLACE_ROOT = "{\n" +
            "      \"$replaceRoot\": {\n" +
            "        \"newRoot\": \"$activities\"\n" +
            "      }\n" +
            "    }";

    public static final String USED_COUNT = "{\n" +
            "      \"$addFields\": {\n" +
            "        \"mostlyUsedCount\": {\n" +
            "          \"$arrayElemAt\": [\n" +
            "            \"$useActivityCount.useActivityCount\",\n" +
            "            0\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    }";
    public static final String TIMETYPE_HIERACHY = "{\n" +
            "    \t$project: {\"_id\":\"$_id\",\"name\":\"$_id.name\",\"timeTypeHierarchyList\":1}\n" +
            "      }";
    public static final String TIMETYPE_HIERACHY_GROUP = "{\n" +
            "    \t$group: { _id : \"$_id\" , timeTypeHierarchyList: { $push: \"$patharray\" } }\n" +
            "      }";
    public static final String PATH_ARRAY = "{\n" +
            "$project : { \"_id\":1,\"name\":1,\"depthField\":1,\"patharray._id\":1,\"patharray.label\":1,\"patharray.upperLevelTimeTypeId\":1,\"patharray.timeTypes\":1 }\n" +
            "}";
    public static final String CHILD_ACTIVITIES_ACTIVITY_RULES_SETTINGS_ELIGIBLE_FOR_STAFFING_LEVEL = "childActivities.activityRulesSettings.eligibleForStaffingLevel";
    public static final String PROJECT_ID_1_CHILD_ACTIVITIES_$_FILTER_INPUT_$_CHILD_ACTIVITIES_AS_CHILD_ACTIVITY_COND_$_EQ_$$_CHILD_ACTIVITY_ACTIVITY_RULES_SETTINGS_ELIGIBLE_FOR_STAFFING_LEVEL_TRUE = "{'$project':{'_id':1,'childActivities':{'$filter':{  'input':'$childActivities','as':'childActivity','cond':{'$eq':['$$childActivity.activityRulesSettings.eligibleForStaffingLevel',true]} }} }}";
    public static final String ACTIVITY_PHASE_SETTINGS_PHASE_TEMPLATE_VALUES = "$activityPhaseSettings.phaseTemplateValues";
    public static final String CHILD_ACTIVITIES_ACTIVITY_PRIORITY = "childActivities.activityPriority";
    public static final String CHILD_ACTIVITIES_TRANSLATIONS = "childActivities.translations";
    public static final String CHILD_ACTIVITIES_NAME = "childActivities.name";
    public static final String CHILD_ACTIVITIES_ACTIVITY_RULES_SETTINGS = "childActivities.activityRulesSettings";
    public static final String CHILD_ACTIVITIES_ACTIVITY_BALANCE_SETTINGS = "childActivities.activityBalanceSettings";
    public static final String CHILD_ACTIVITIES_ACTIVITY_TIME_CALCULATION_SETTINGS = "childActivities.activityTimeCalculationSettings";
    public static final String CHILD_ACTIVITIES_ACTIVITY_BALANCE_SETTINGS_TIME_TYPE_ID = "childActivities.activityBalanceSettings.timeTypeId";
    public static final String ACTIVITY_CATEGORY = "activity_category";
    public static final String ACTIVITY_RULES_SETTINGS_SHORTEST_TIME = "activityRulesSettings.shortestTime";
    public static final String ACTIVITY_RULES_SETTINGS_LONGEST_TIME = "activityRulesSettings.longestTime";
    public static final String ACTIVITY_RULES_SETTINGS_EARLIEST_START_TIME = "activityRulesSettings.earliestStartTime";
    public static final String ACTIVITY_RULES_SETTINGS_LATEST_START_TIME = "activityRulesSettings.latestStartTime";
    public static final String ACTIVITY_RULES_SETTINGS_ALLOWED_AUTO_ABSENCE = "activityRulesSettings.allowedAutoAbsence";
    public static final String ACTIVITY_BALANCE_SETTINGS_TIME_TYPE_INFO_TIME_TYPES = "activityBalanceSettings.timeTypeInfo.timeTypes";
    public static final Long INVALID_ID = -2L;
}
