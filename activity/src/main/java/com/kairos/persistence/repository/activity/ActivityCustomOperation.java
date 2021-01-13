package com.kairos.persistence.repository.activity;

import com.kairos.persistence.repository.common.CustomAggregationOperation;

public class ActivityCustomOperation {

    public static CustomAggregationOperation getCustomAggregationOperationForMatchCount() {
        return new CustomAggregationOperation("{\n" +
                "      \"$addFields\": {\n" +
                "        \"mostlyUsedCount\": {\n" +
                "          \"$arrayElemAt\": [\n" +
                "            \"$useActivityCount.useActivityCount\",\n" +
                "            0\n" +
                "          ]\n" +
                "        }\n" +
                "      }\n" +
                "    }");
    }

    public static CustomAggregationOperation getCustomAggregationOperationForStaffActivitySetting() {
        return new CustomAggregationOperation("{\n" +
                "      \"$lookup\": {\n" +
                "        \"from\": \"staffActivityDetails\",\n" +
                "        \"let\": {\n" +
                "          \"staffId\": 2455,\n" +
                "          \"activityId\": \"$_id\"\n" +
                "        },\n" +
                "        \"pipeline\": [\n" +
                "          {\n" +
                "            \"$match\": {\n" +
                "              \"$expr\": {\n" +
                "                \"$and\": [\n" +
                "                  {\n" +
                "                    \"$eq\": [\n" +
                "                      \"$staffId\",\n" +
                "                      \"$$staffId\"\n" +
                "                    ]\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"$gte\": [\n" +
                "                      \"$activityId\",\n" +
                "                      \"$$activityId\"\n" +
                "                    ]\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"$project\": {\n" +
                "              \"useActivityCount\": 1,\n" +
                "              \"_id\": 0\n" +
                "            }\n" +
                "          }\n" +
                "        ],\n" +
                "        \"as\": \"useActivityCount\"\n" +
                "      }\n" +
                "    }");
    }

    public static CustomAggregationOperation getCustomAggregationOperationForReplaceActivity() {
        return new CustomAggregationOperation("{\n" +
                "      \"$replaceRoot\": {\n" +
                "        \"newRoot\": \"$activities\"\n" +
                "      }\n" +
                "    }");
    }

    public static CustomAggregationOperation getCustomAggregationOperationForChildActivitiyIds() {
        return new CustomAggregationOperation("{\n" +
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
                "    }");
    }

    public static CustomAggregationOperation getCustomAggregationOperationForConcatArray() {
        return new CustomAggregationOperation("{\n" +
                "        $project:{\n" +
                "            \"_id\":0,\n" +
                "            \"activityIds\": {\n" +
                "          \"$concatArrays\": [\"$activityIds\",\"$otherActivityIds\"]\n" +
                "        }\n" +
                "            }\n" +
                "        }");
    }

    public static CustomAggregationOperation getCustomAggregationOperationForActivities() {
        return new CustomAggregationOperation("{\n" +
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
                "          }\n" +
                "        ],\n" +
                "        \"as\": \"activities\"\n" +
                "      }\n" +
                "    }");
    }

    public static CustomAggregationOperation getCustomLookUpForActivityAggregationOperation(String activityString,boolean isActivityType,Long unitId) {
        String condition = isActivityType ?  "                { \"$ne\": [ \"$childActivityIds\", [] ] },\n" : "";
        return new CustomAggregationOperation("{\n" +
                "    \"$lookup\": {\n" +
                "      \"from\": \"activities\",\n" +
                "      \"let\": {\n" +
                "        \"activityIds\": \"$activityIds\"\n" +
                "      },\n" +
                "      \"pipeline\": [\n" +
                "        {\n" +
                "          \"$match\": {\n" +
                "            \"$expr\": {\n" +
                "              \"$and\": [\n" +
                condition+
                "                {\n" +
                "                  \"$in\": [\n" +
                "                    \"$_id\",\n" +
                "                    "+activityString+"\n" +
                "                  ]\n" +
                "                },\n" +
                "{ $eq: [ \"$unitId\",  "+unitId+" ] }"+
                "              ]\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"$group\": {\n" +
                "            \"_id\": \"$unitId\",\n" +
                "            \"activityIds\": {\n" +
                "              \"$addToSet\": \"$_id\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ],\n" +
                "      \"as\": \"activities\"\n" +
                "    }\n" +
                "  }");
    }
}
