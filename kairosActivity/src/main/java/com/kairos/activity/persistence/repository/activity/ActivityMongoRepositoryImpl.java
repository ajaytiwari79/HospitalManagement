package com.kairos.activity.persistence.repository.activity;

import com.kairos.activity.persistence.model.activity.Activity;
import com.kairos.activity.response.dto.ActivityDTO;
import com.kairos.activity.response.dto.ActivityWithCompositeDTO;
import com.kairos.activity.response.dto.OrganizationTypeAndSubTypeDTO;
import com.kairos.activity.response.dto.activity.ActivityTagDTO;
import com.kairos.activity.response.dto.activity.ActivityWithCTAWTASettingsDTO;
import com.kairos.activity.response.dto.activity.OrganizationActivityDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class ActivityMongoRepositoryImpl implements CustomActivityMongoRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<ActivityTagDTO> findAllActivityByOrganizationGroupWithCategoryName(Long unitId,boolean deleted) {
        ProjectionOperation projectionOperation = Aggregation.project().and("id").as("id").and("name")
                .as("name")
                .and("activity_type_category.id").as("categoryId").and("activity_type_category.name")
                .as("categoryName");

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(deleted)),
                unwind("generalActivityTab"),
                lookup("activity_category", "generalActivityTab.categoryId", "_id",
                        "activity_type_category"),
                unwind("activity_type_category"),
                projectionOperation
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }

   /*public  List<ActivityDTO> findAllActivitiesByOrganizationType(List<Long> orgTypeIds, List<Long> orgSubTypeIds){


        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("isParentActivity").is(true).and("organizationTypes").in(orgTypeIds).orOperator(Criteria.where("organizationSubTypes").in(orgSubTypeIds))),
                graphLookup("activities").startWith("$compositeActivities").connectFrom("compositeActivities").connectTo("_id").as("compositeActivities"),
                project("name", "generalActivityTab", "compositeActivities"));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();

   }*/

    public List<ActivityTagDTO> findAllActivitiesByOrganizationType(List<Long> orgTypeIds, List<Long> orgSubTypeIds) {

        /*Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("isParentActivity").is(true).and("organizationTypes").in(orgTypeIds).orOperator(Criteria.where("organizationSubTypes").in(orgSubTypeIds))),
                project("name","generalActivityTab"));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();*/

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("isParentActivity").is(true).and("organizationTypes").in(orgTypeIds).orOperator(Criteria.where("organizationSubTypes").in(orgSubTypeIds))),
                unwind("tags", true),
                lookup("tag", "tags", "_id", "tags_data"),
                unwind("tags_data", true),
                group("$id")
                        .first("$name").as("name")
                        .first("$description").as("description")
                        .first("$unitId").as("unitId")
                        .first("$parentId").as("parentId")
                        .first("generalActivityTab").as("generalActivityTab")
                        .push("tags_data").as("tags")

        );

        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();

    }

    public List<ActivityDTO> findAllActivitiesWithDataByIds(Set<BigInteger> activityIds) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").in(activityIds).and("deleted").is(false)),
                project("name", "generalActivityTab"));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();

    }

    // List<ActivityDTO> ;
    /*public  List<ActivityDTO> findAllActivityByCountry(long countryId){

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").in(countryId).and("deleted").is(false).and("isParentActivity").is(true)),
                lookup("tag","tags","_id",
                        "tags")
        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class,ActivityDTO.class);
        return result.getMappedResults();

    }*/


    public List<ActivityTagDTO> findAllActivityByUnitIdAndDeleted(Long unitId, boolean deleted) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").in(unitId).and("deleted").is(deleted)),
                unwind("tags", true),
                lookup("tag", "tags", "_id", "tags_data"),
                unwind("tags_data", true),
                group("$id")
                        .first("$name").as("name")
                        .first("$description").as("description")
                        .first("$unitId").as("unitId")
                        .first("$parentId").as("parentId")
                        .first("generalActivityTab").as("generalActivityTab")
                        .push("tags_data").as("tags")

        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityTagDTO> findAllActivityByCountry(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").in(countryId).and("deleted").is(false).and("isParentActivity").is(true)),
                unwind("tags", true),
                lookup("tag", "tags", "_id", "tags_data"),
                unwind("tags_data", true),
                group("$id")
                        .first("$name").as("name")
                        .first("$description").as("description")
                        .first("$countryId").as("countryId")
                        .first("$unitId").as("unitId")
                        .first("$isParentActivity").as("isParentActivity")
                        .first("generalActivityTab").as("generalActivityTab")
                        .push("tags_data").as("tags")
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByCountry(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").is(countryId).and("deleted").is(false).and("isParentActivity").is(true)),
                project("$id","name","description","ctaAndWtaSettingsActivityTab", "generalActivityTab.categoryId")
        );
        AggregationResults<ActivityWithCTAWTASettingsDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCTAWTASettingsDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("isParentActivity").is(false)),
                project("$id","name","description","ctaAndWtaSettingsActivityTab", "generalActivityTab.categoryId")
        );
        AggregationResults<ActivityWithCTAWTASettingsDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCTAWTASettingsDTO.class);
        return result.getMappedResults();
    }

    public List<OrganizationActivityDTO> findAllActivityOfUnitsByParentActivity(List<BigInteger> parentActivityIds, List<Long> unitIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("isParentActivity").is(false).and("unitId").in(unitIds).and("parentId").in(parentActivityIds)),
                project("$id","unitId", "parentId")
//                group("unitId").addToSet("id").as("activityIds")
//                group("unitId").push("$$ROOT").as("activityIds")
        );
        AggregationResults<OrganizationActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, OrganizationActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityTagDTO> findAllActivityByParentOrganization(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                graphLookup("activities").startWith("$compositeActivities").connectFrom("compositeActivities").connectTo("_id").maxDepth(0).as("compositeActivities"),
                project("name", "generalActivityTab", "compositeActivities"));

        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();

    }


    public List<ActivityWithCompositeDTO> findAllActivityByUnitIdWithCompositeActivities(long unitId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                graphLookup("activities").startWith("$compositeActivities").connectFrom("compositeActivities").connectTo("_id").maxDepth(0).as("compositeActivities"),
                project("name", "generalActivityTab","compositeActivities","expertises","employmentTypes","rulesActivityTab","skillActivityTab","timeCalculationActivityTab"));
        AggregationResults<ActivityWithCompositeDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCompositeDTO.class);
        return result.getMappedResults();
    }


    public List<Activity> findAllByTimeTypeId(BigInteger timeTypeId){
        Query query = new Query(Criteria.where("balanceSettingsActivityTab.timeTypeId").is(timeTypeId).and("deleted").is(false));
        return mongoTemplate.find(query,Activity.class);

    }
    public List<ActivityDTO> getAllActivityWithTimeType(Long unitId,List<BigInteger> activityIds){
        Aggregation aggregation = Aggregation.newAggregation(
                //"unitId").is(unitId).and(
                match(Criteria.where("deleted").is(false).and("_id").in(activityIds)),
                lookup("time_Type","balanceSettingsActivityTab.timeTypeId","_id","timeType")
                ,project("unitId")
                        .andInclude("deleted")
                        .andInclude("name")
                        .andInclude("expertises")
                        .andInclude("skillActivityTab")
                        .and("timeType").arrayElementAt(0).as("timeType"));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllActivityByUnitId(Long unitId){
        Aggregation aggregation = Aggregation.newAggregation(
                //"unitId").is(unitId).and(
                match(Criteria.where("deleted").is(false).and("unitId").is(unitId))
                //lookup("time_Type","balanceSettingsActivityTab.timeTypeId","_id","timeType")
                ,project("unitId")
                        .andInclude("deleted")
                        /*.andInclude("name")
                        .andInclude("expertises")
                        .andInclude("skillActivityTab")
                        .and("timeType").arrayElementAt(0).as("timeType")*/);
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    //Ignorecase
    public Activity getActivityByNameAndUnitId(Long unitId,String name){
        Query query = new Query(Criteria.where("deleted").is(false).and("unitId").is(unitId).and("name").regex(Pattern.compile("^"+name+"$",Pattern.CASE_INSENSITIVE)));
        return (Activity) mongoTemplate.findOne(query,Activity.class);
    }

}
