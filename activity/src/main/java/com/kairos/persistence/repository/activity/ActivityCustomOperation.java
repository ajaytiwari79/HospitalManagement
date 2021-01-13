package com.kairos.persistence.repository.activity;

import com.kairos.persistence.repository.common.CustomAggregationOperation;

public class ActivityCustomOperation {

    public static final String NEXT_LINE_SEPERATOR = "}\n";
    public static final String ARRAY_BRACKET_CLOSE = "]\n";
    public static final String BRACKET_CLOSE = "}";
    public static final String BRACKET_CLOSE_WITH_SEPARATOR = "},\n";

    public static CustomAggregationOperation getCustomAggregationOperationForMatchCount() {
        return new CustomAggregationOperation("{\n" +
                "\"$addFields\": {\n" +
                "\"mostlyUsedCount\": {\n" +
                "\"$arrayElemAt\": [\n" +
                "\"$useActivityCount.useActivityCount\",\n" +
                "0\n" +
                ARRAY_BRACKET_CLOSE +
                NEXT_LINE_SEPERATOR +
                NEXT_LINE_SEPERATOR +
                BRACKET_CLOSE);
    }

    public static CustomAggregationOperation getCustomAggregationOperationForStaffActivitySetting() {
        return new CustomAggregationOperation("{\n" +
                "\"$lookup\": {\n" +
                "\"from\": \"staffActivityDetails\",\n" +
                "\"let\": {\n" +
                "\"staffId\": 2455,\n" +
                "\"activityId\": \"$_id\"\n" +
                "},\n" +
                "\"pipeline\": [\n" +
                "{\n" +
                "\"$match\": {\n" +
                "\"$expr\": {\n" +
                "\"$and\": [\n" +
                "{\n" +
                "\"$eq\": [\n" +
                "\"$staffId\",\n" +
                "\"$$staffId\"\n" +
                ARRAY_BRACKET_CLOSE +
                "},\n" +
                "{\n" +
                "\"$gte\": [\n" +
                "\"$activityId\",\n" +
                "\"$$activityId\"\n" +
                ARRAY_BRACKET_CLOSE +
                NEXT_LINE_SEPERATOR +
                ARRAY_BRACKET_CLOSE +
                NEXT_LINE_SEPERATOR +
                NEXT_LINE_SEPERATOR +
                "},\n" +
                "{\n" +
                "\"$project\": {\n" +
                "\"useActivityCount\": 1,\n" +
                "\"_id\": 0\n" +
                NEXT_LINE_SEPERATOR +
                NEXT_LINE_SEPERATOR +
                "],\n" +
                "\"as\": \"useActivityCount\"\n" +
                NEXT_LINE_SEPERATOR +
                BRACKET_CLOSE);
    }

    public static CustomAggregationOperation getCustomAggregationOperationForReplaceActivity() {
        return new CustomAggregationOperation("{\n" +
                "\"$replaceRoot\": {\n" +
                "\"newRoot\": \"$activities\"\n" +
                NEXT_LINE_SEPERATOR +
                BRACKET_CLOSE);
    }

    public static CustomAggregationOperation getCustomAggregationOperationForChildActivitiyIds() {
        return new CustomAggregationOperation("{\n" +
                "\"$project\": {\n" +
                "\"activityIds\": 1,\n" +
                "\"otherActivityIds\": \n" +
                "{\n" +
                "$cond: { if: { $ne: [ \"$activities\", [] ] }, then: {\n" +
                "\"$arrayElemAt\": [\n" +
                "\"$activities.activityIds\",\n" +
                "0\n" +
                ARRAY_BRACKET_CLOSE +
                "}, else: [] }\n" +
                NEXT_LINE_SEPERATOR +
                "\n" +
                NEXT_LINE_SEPERATOR +
                BRACKET_CLOSE);
    }

    public static CustomAggregationOperation getCustomAggregationOperationForConcatArray() {
        return new CustomAggregationOperation("{\n" +
                "$project:{\n" +
                "\"_id\":0,\n" +
                "\"activityIds\": {\n" +
                "\"$concatArrays\": [\"$activityIds\",\"$otherActivityIds"+ARRAY_BRACKET_CLOSE +
                NEXT_LINE_SEPERATOR +
                NEXT_LINE_SEPERATOR +
                "}");
    }

    public static CustomAggregationOperation getCustomAggregationOperationForActivities() {
        return new CustomAggregationOperation("{\n" +
                "\"$lookup\": {\n" +
                "\"from\": \"activities\",\n" +
                "\"let\": {\n" +
                "\"activityIds\": \"$activityIds\"\n" +
                BRACKET_CLOSE_WITH_SEPARATOR +
                "\"pipeline\": [\n" +
                "{\n" +
                "\"$match\": {\n" +
                "\"$expr\": {\n" +
                "\"$and\": [\n" +
                "{\n" +
                "\"$in\": [\n" +
                "\"$_id\",\n" +
                "\"$$activityIds\"\n" +
                ARRAY_BRACKET_CLOSE +
                NEXT_LINE_SEPERATOR +
                ARRAY_BRACKET_CLOSE +
                NEXT_LINE_SEPERATOR +
                NEXT_LINE_SEPERATOR +
                NEXT_LINE_SEPERATOR +
                "],\n" +
                "\"as\": \"activities\"\n" +
                NEXT_LINE_SEPERATOR +
                BRACKET_CLOSE);
    }

    public static CustomAggregationOperation getCustomLookUpForActivityAggregationOperation(String activityString,boolean isActivityType,Long unitId) {
        String condition = isActivityType ?  "{ \"$ne\": [ \"$childActivityIds\", [] ] },\n" : "";
        return new CustomAggregationOperation("{\n" +
                "\"$lookup\": {\n" +
                "\"from\": \"activities\",\n" +
                "\"let\": {\n" +
                "\"activityIds\": \"$activityIds\"\n" +
                "},\n" +
                "\"pipeline\": [\n" +
                "{\n" +
                "\"$match\": {\n" +
                "\"$expr\": {\n" +
                "\"$and\": [\n" +
                condition+
                "{\n" +
                "\"$in\": [\n" +
                "\"$_id\",\n" +
                ""+activityString+"\n" +
                ARRAY_BRACKET_CLOSE +
                "},\n" +
                "{ $eq: [ \"$unitId\",  "+unitId+" ] }"+
                ARRAY_BRACKET_CLOSE +
                NEXT_LINE_SEPERATOR +
                NEXT_LINE_SEPERATOR +
                BRACKET_CLOSE_WITH_SEPARATOR +
                "{\n" +
                "\"$group\": {\n" +
                "\"_id\": \"$unitId\",\n" +
                "\"activityIds\": {\n" +
                "\"$addToSet\": \"$_id\"\n" +
                NEXT_LINE_SEPERATOR +
                NEXT_LINE_SEPERATOR +
                NEXT_LINE_SEPERATOR +
                "],\n" +
                "\"as\": \"activities\"\n" +
                NEXT_LINE_SEPERATOR +
                "}");
    }
}
