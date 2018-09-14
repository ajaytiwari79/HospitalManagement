package com.kairos.persistence.repository.common;
import com.kairos.commons.utils.DateUtils;

import java.util.Date;

/**
 * Created by Jasgeet on 31/1/17.
 */
public class CustomAggregationQuery {

    public static String absencePlanningDataGroupingQuery(){
        return "  { '$group':{'_id':null,'taskLists':{'$push':'$taskList'}," +
                " 'taskTypeList' : { '$addToSet':{'_id':'$_id', 'title':'$title' }}," +
                " 'staffList': { '$push':'$taskList.staffId'}," +
                " 'staffAnonymousList': { '$addToSet':'$taskList.staffAnonymousId'}" +
                " }}";
    }

    public static String absencePlanningDataUnwindQuery(){
        return "{'$unwind' : '$taskList'}";
    }

    public static String absencePlanningProjectionQuery(){
        return "{" +
                "       $project:" +
                "         {" +
                "           'grouping':{" +
                "               '$ifNull':['$staffId','$staffAnonymousId']}," +
                " 'startDate':'$startDate', 'endDate':'$endDate', 'taskTypeId':'$taskTypeId', 'info1':'$info1'," +
                " 'info2':'$info2', 'absencePlanningStatus':'$absencePlanningStatus', 'startAddress':'$startAddress'," +
                " 'endAddress':'$endAddress', 'duration':'$duration', 'priority':'$priority', 'isActive':'$isActive'}}";
    }

    public static String groupingByTaskTypesQuery(){
        return "{" +
                "       $project:" +
                "         {" +
                "           'grouping':taskTypeId," +
                " 'staffList': { '$push':'$staffId'}}}" ;
    }

    public static String absencePlanningDataMatchUnitQuery(Long unitId, Boolean isDaily){
        String matchQuery = "{ '$match' : { 'taskList' : {'$ne':[]} ";
        if(unitId != null) matchQuery +=  ", 'taskList.unitId' : "+unitId;
        if(isDaily == true){
            Date date = DateUtils.getDate();
            Date startTimeOfDay = DateUtils.getStartOfDay(date);
            Date endTimeOfDay = DateUtils.getEndOfDay(date);
            matchQuery +=  ", 'taskList.startDate' : { '$gte' : { '$date' : '"+startTimeOfDay+"'} , '$lt' : { '$date' : '"+endTimeOfDay+"'}} ";
        }
        matchQuery += "}} ";
        System.out.println("matchQuery-----> "+matchQuery);
        return matchQuery;
    }

    public static String sortingTaskByDate(){
       return "{'$sort':{'startDate':1}}";
    }
}