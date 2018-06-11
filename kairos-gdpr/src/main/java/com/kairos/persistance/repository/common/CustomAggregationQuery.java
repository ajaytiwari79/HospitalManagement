package com.kairos.persistance.repository.common;

public class CustomAggregationQuery {


    public static String processingActivityWithSubProcessingNonDeletedData()
    {
        return "{ '$project':" +
                " {'subProcessingActivities':" +
                " {'$filter': {'input': '$subProcessingActivities'," +
                "'as': 'subProcessingActivities'," +
                "'cond': { '$eq': [ '$$subProcessingActivities.deleted',"+false+" ] }}}" +
                ",'name':1," +
                "'description':1," +
                "'organizationSubServices':1," +
                "'organizationServices':1," +
                "'organizationSubTypes':1," +
                "'organizationTypes':1," +
                "'countryId':1}}";

    }



    public static String dataSubjectAddNonDeletedDataElementAddFileds()
    {
        return "{  '$addFields ':" +
                "{'dataCategories.dataElements':" +
                "{$filter : { "+
                "'input': '$dataCategories.dataElements',"+
                "as: 'dataElements', "+
                "cond: {$eq: ['$$dataElements.deleted',"+false+"]}" +
                "}}}} ";
    }








}
