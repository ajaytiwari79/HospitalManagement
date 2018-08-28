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
                "'hasSubProcessingActivity':1," +
                "'organizationSubTypes':1," +
                "'organizationTypes':1," +
                "'countryId':1}}";

    }

    public static String dataSubjectAddNonDeletedDataElementAddFields() {
        return "{  '$addFields':" +
                "{'dataCategories.dataElements':" +
                "{$filter : { " +
                "'input': '$dataCategories.dataElements'," +
                "as: 'dataElements', " +
                "cond: {$eq: ['$$dataElements.deleted'," + false + "]}" +
                "}}}} ";
    }


    public static String questionnaireTemplateAddNonDeletedQuestions() {
        return "{  '$addFields':" +
                "{'sections.questions':" +
                "{$filter : { " +
                "'input': '$questions'," +
                "as: 'questions', " +
                "cond: {$eq: ['$$questions.deleted'," + false + "]}" +
                "}}}} ";
    }


    public static String questionnaireTemplateAddNonDeletedSections() {
        return "{  '$addFields':" +
                "{'sections':" +
                "{$filter : { " +
                "'input': '$sections'," +
                "as: 'sections', " +
                "cond: {$eq: ['$$sections.deleted'," + false + "]}" +
                "}}}} ";
    }


    public static String questionnaireTemplateAddNonDeletedAssetType() {
        return "{  '$addFields':" +
                "{'assetType':" +
                "{$filter : { " +
                "'input': '$assetType'," +
                "as: 'assetType', " +
                "cond: {$eq: ['$$assetType.deleted'," + false + "]}" +
                "}}}} ";
    }


    public static String questionnaireTemplateGroupOperation() {
        return "{'$group':{" +
                "'_id':'$_id','sections':{'$push':{ '$cond': [ { '$eq': [ '$sections.deleted',false ] }, '$sections', {} ] }}," +
                "'name':{$first:'$name'}," +
                "'description':{$first:'$description'}," +
                "'assetType':{$first:'$assetType'}," +
                "'templateType':{$first:'$templateType'}," +
                "}}";
    }

    public static String questionnaireTemplateProjectionBeforeGroupOperationForAssetType() {
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


    public static String agreementTemplateProjectionBeforeGroupOperationForTemplateTypeAtIndexZero() {
        return "{'$project':{" +
                "      'templateType':{'$arrayElemAt':['$templateType',0]}," +
                "      'name':1," +
                "      'agreementSections':1," +
                "      'description':1," +
                "       'accountTypes':1," +
                "             'organizationTypes':1," +
                "             'organizationSubTypes':1," +
                "             'organizationServices':1," +
                "              'organizationSubServices':1," +
                "         }}";


    }


    public static String addNonDeletedTemplateTyeField() {
        return " {  '$addFields':" +
                "                {'templateTypes':" +
                "                {'$filter' : { " +
                "                'input': '$templateTypes'," +
                "                'as': 'templateType', " +
                "                'cond': {'$eq': ['$$templateType.deleted', false ]}" +
                "                }}}}";

    }


    public static String masterAssetProjectionWithAssetType() {
        return " {" +
                "'$project':{" +
                "'assetType':{$arrayElemAt:['$assetType',0]}," +
                "         'assetSubTypes':1," +
                "         'name':1," +
                "       'description':1," +
                "       'organizationSubTypes':1," +
                "       'organizationTypes':1," +
                "       'organizationServices':1," +
                "       'organizationSubServices':1," +

                "            }}";
    }


    public static String assetProjectionWithMetaData() {
        return " {" +
                "'$project':{" +
                "'assetType':{$arrayElemAt:['$assetType',0]}," +
                "'hostingType':{$arrayElemAt:['$hostingType',0]}," +
                "'dataDisposal':{$arrayElemAt:['$dataDisposal',0]}," +
                "'hostingProvider':{$arrayElemAt:['$hostingProvider',0]}," +
                "  'name':1," +
                "'assetSubTypes':1," +
                "  'description':1," +
                "  'hostingLocation':1," +
                "  'managingDepartment':1," +
                "  'assetOwner':1," +
                "  'storageFormats':1," +
                "  'orgSecurityMeasures':1," +
                "  'technicalSecurityMeasures':1," +
                "  'dataRetentionPeriod':1," +
                "  'minDataSubjectVolume':1," +
                "  'maxDataSubjectVolume':1," +
                "  'active':1," +

                "            }}";
    }


    public static String addNonDeletedSubProcessingActivityToProcessingActivity() {

        return "  {'$addFields':" +
                "                {'subProcessingActivities':         " +
                "                    {" +
                "                '$filter' : { " +
                "                'input': '$subProcessingActivities'," +
                "                'as': 'activity', " +
                "                'cond': {'$eq': ['$$activity.deleted', false ]}" +
                "                }}}}";


    }


    public static String metaDataGroupInheritParentOrgMetaDataAndOrganizationMetadata() {
        return "{ $group: {" +
                "    '_id': { 'name': '$name' },  " +
                "    'rootObject': { '$addToSet': '$$ROOT' }," +
                "  } }";
    }

    public static String metaDataProjectionForRemovingDuplicateInheritedMetaData(Long currentOrganizationId) {
        return "{ '$project': {" +
                "          'data':" +
                "              {" +
                "           '$cond': { 'if': { $gt: [ { '$size': '$rootObject' }, 1 ] },'then':{'$filter': {" +
                "           'input': '$rootObject'," +
                "           'as': 'rootObject'," +
                "           'cond': { $eq: [ '$$rootObject.organizationId', " + currentOrganizationId + " ] }" +
                "            }} ,else:'$rootObject' } }}  }";
    }


    public static String metaDataProjectionForAddingFinalDataObject() {
        return "{ '$project': {" +
                "          'data':{'$arrayElemAt':['$data',0]}," +
                "          '_id':0" +
                "   }}";
    }


    public static String metaDataReplaceRoot() {
        return "{ '$replaceRoot' : { 'newRoot' : '$data' } }";
    }


}
