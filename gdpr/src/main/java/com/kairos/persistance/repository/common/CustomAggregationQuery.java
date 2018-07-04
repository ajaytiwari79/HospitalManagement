package com.kairos.persistance.repository.common;

public class CustomAggregationQuery {


    public static String processingActivityWithSubProcessingNonDeletedData() {
        return "{ '$project':" +
                " {'subProcessingActivities':" +
                " {'$filter': {'input': '$subProcessingActivities'," +
                "'as': 'subProcessingActivities'," +
                "'cond': { '$eq': [ '$$subProcessingActivities.deleted'," + false + " ] }}}" +
                ",'name':1," +
                "'description':1," +
                "'organizationSubServices':1," +
                "'organizationServices':1," +
                "'hasSubProcess':1," +
                "'organizationSubTypes':1," +
                "'organizationTypes':1," +
                "'countryId':1}}";

    }

    public static String dataSubjectAddNonDeletedDataElementAddFileds() {
        return "{  '$addFields':" +
                "{'dataCategories.dataElements':" +
                "{$filter : { " +
                "'input': '$dataCategories.dataElements'," +
                "as: 'dataElements', " +
                "cond: {$eq: ['$$dataElements.deleted'," + false + "]}" +
                "}}}} ";
    }


   /* public static String questionnnaireTemplateAddNonDeletedQuestions()
    {
        return "{  '$addFields':" +
                "{'questions':" +
                "{$filter : { "+
                "'input': '$questions',"+
                "as: 'questions', "+
                "cond: {$eq: ['$$questions.deleted',"+false+"]}" +
                "}}}} ";
    }*/

    public static String questionnnaireTemplateAddNonDeletedQuestions() {
        return "{  '$addFields':" +
                "{'sections.questions':" +
                "{$filter : { " +
                "'input': '$questions'," +
                "as: 'questions', " +
                "cond: {$eq: ['$$questions.deleted'," + false + "]}" +
                "}}}} ";
    }


    public static String questionnnaireTemplateAddNonDeletedSections() {
        return "{  '$addFields':" +
                "{'sections':" +
                "{$filter : { " +
                "'input': '$sections'," +
                "as: 'sections', " +
                "cond: {$eq: ['$$sections.deleted'," + false + "]}" +
                "}}}} ";
    }


    public static String questionnnaireTemplateAddNonDeletedAssetType() {
        return "{  '$addFields':" +
                "{'assetType':" +
                "{$filter : { " +
                "'input': '$assetType'," +
                "as: 'assetType', " +
                "cond: {$eq: ['$$assetType.deleted'," + false + "]}" +
                "}}}} ";
    }


    public static String questionnnaireTemplateGroupOperation() {
        return "{'$group':{" +
                "'_id':'$_id','sections':{'$push':{ '$cond': [ { '$eq': [ '$sections.deleted',false ] }, '$sections', {} ] }}," +
                "'name':{$first:'$name'}," +
                "'description':{$first:'$description'}," +
                "'assetType':{$first:'$assetType'}," +
                "'templateType':{$first:'$templateType'}," +
                "}}";
    }

    public static String questionnnaireTemplateProjectionBeforeGroupOperationForAssetType() {
        return " {" +
                "'$project':{" +
                "'assetType':{$arrayElemAt:['$assetType',0]}," +
                "         'name':1," +
                "        'sections':1," +
                "      'description':1," +
                "     'templateType':1," +
                "      'countryId':1," +
                "            }}";
    }


    public static String dataCategoryWithDataElementProjectionData() {
        return "{  '$project':" +
                "{'dataElements':" +
                "{'$filter' : { " +
                "'input':'$dataElements'," +
                "as: 'dataElement', " +
                "cond:{$eq:['$$dataElement.deleted',false]}" +
                "}}," +
                "'countryId':1" +
                ", 'name':1" +
                ",'deleted':1 }}";
    }


    public static String assetTypesAddNonDeletedSubAssetTypes() {
        return "{ '$addFields':" +
                "   {'subAssetTypes':" +
                "   {'$filter' :{ " +
                "    'input': '$subAssetTypes'," +
                "   'as':'subAssetTypes', " +
                "  'cond': {'$eq': ['$$subAssetTypes.deleted',false]}" +
                "  }}}}";
    }


}
