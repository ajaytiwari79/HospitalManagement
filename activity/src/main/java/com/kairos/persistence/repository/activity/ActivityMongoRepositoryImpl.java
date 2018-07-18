package com.kairos.persistence.repository.activity;

import com.kairos.activity.activity.ActivityDTO;
import com.kairos.activity.activity.CompositeActivityDTO;
import com.kairos.activity.time_type.TimeTypeAndActivityIdDTO;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.kairos.activity.activity.OrganizationActivityDTO;
import com.kairos.activity.activity.activity_tabs.ActivityWithCTAWTASettingsDTO;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.wrapper.activity.ActivityTagDTO;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.kairos.enums.TimeTypes.WORKING_TYPE;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class ActivityMongoRepositoryImpl implements CustomActivityMongoRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<ActivityTagDTO> findAllActivityByOrganizationGroupWithCategoryName(Long unitId, boolean deleted) {
        ProjectionOperation projectionOperation = Aggregation.project().and("timeCalculationActivityTab.methodForCalculatingTime").as("timeCalculationActivityTab.methodForCalculatingTime")
                .and("timeCalculationActivityTab.fullWeekStart").as("timeCalculationActivityTab.fullWeekStart")
                .and("id").as("id").and("name").as("name")
                .and("activity_type_category.id").as("categoryId").and("activity_type_category.name")
                .as("categoryName");

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(deleted).and("rulesActivityTab.eligibleForStaffingLevel").is(true)),
                unwind("generalActivityTab"),
                lookup("activity_category", "generalActivityTab.categoryId", "_id",
                        "activity_type_category"),
                unwind("activity_type_category"),
                projectionOperation
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityTagDTO> findAllActivitiesByOrganizationType(List<Long> orgTypeIds, List<Long> orgSubTypeIds) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("isParentActivity").is(true)
                        .and("organizationTypes").in(orgTypeIds).orOperator(Criteria.where("organizationSubTypes").in(orgSubTypeIds))
                        .and("state").nin("DRAFT")),
                unwind("tags", true),
                lookup("tag", "tags", "_id", "tags_data"),
                unwind("tags_data", true),
                group("$id")
                        .first("$name").as("name")
                        .first("$description").as("description")
                        .first("$unitId").as("unitId")
                        .first("$parentId").as("parentId")
                        .first("generalActivityTab").as("generalActivityTab")
                        .first("permissionsActivityTab").as("permissionsActivityTab")
                        .push("tags_data").as("tags")

        );

        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();

    }

    public List<CompositeActivityDTO> getCompositeActivities(BigInteger activityId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(activityId).and("deleted").is(false)),
                unwind("compositeActivities", true),
                graphLookup("activities").startWith("$compositeActivities.activityId")
                        .connectFrom("compositeActivities.activityId").connectTo("_id").maxDepth(0).as("compositeActivitiesObject"),
                unwind("$compositeActivitiesObject", true),
                project()
                        .and("compositeActivitiesObject.name").as("name")
                        .andExclude("_id")
                        .and("compositeActivities.activityId").as("compositeId")
                        .and("compositeActivitiesObject.generalActivityTab").as("generalActivityTab")
                        .and("compositeActivities.allowedBefore").as("allowedBefore")
                        .and("compositeActivities.allowedAfter").as("allowedAfter")
        );
        AggregationResults<CompositeActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, CompositeActivityDTO.class);
        return result.getMappedResults();

    }

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
                        .first("permissionsActivityTab").as("permissionsActivityTab")
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
                        .first("$state").as("state")
                        .first("$description").as("description")
                        .first("$countryId").as("countryId")
                        .first("$isParentActivity").as("isParentActivity")
                        .first("generalActivityTab").as("generalActivityTab")
                        .first("permissionsActivityTab").as("permissionsActivityTab")
                        .push("tags_data").as("tags")
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByCountry(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").is(countryId).and("deleted").is(false).and("isParentActivity").is(true)),
                lookup("time_Type", "balanceSettingsActivityTab.timeTypeId", "_id", "timeType"),
                project("$id", "name", "description", "ctaAndWtaSettingsActivityTab", "generalActivityTab.categoryId")
                        .and("timeType").arrayElementAt(0).as("timeType"),
                match(Criteria.where("timeType.timeTypes").is(TimeTypes.WORKING_TYPE))
        );
        AggregationResults<ActivityWithCTAWTASettingsDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCTAWTASettingsDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("isParentActivity").is(false)),
                lookup("time_Type", "balanceSettingsActivityTab.timeTypeId", "_id", "timeType"),
                project("$id", "name", "description", "ctaAndWtaSettingsActivityTab", "generalActivityTab.categoryId").and("timeType").arrayElementAt(0).as("timeType"),
                match(Criteria.where("timeType.timeTypes").is(TimeTypes.WORKING_TYPE))
        );
        AggregationResults<ActivityWithCTAWTASettingsDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCTAWTASettingsDTO.class);
        return result.getMappedResults();
    }

    public List<OrganizationActivityDTO> findAllActivityOfUnitsByParentActivity(List<BigInteger> parentActivityIds, List<Long> unitIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("isParentActivity").is(false).and("unitId").in(unitIds).and("parentId").in(parentActivityIds)),
                project("$id", "unitId", "parentId")
//                group("unitId").addToSet("id").as("activityIds")
//                group("unitId").push("$$ROOT").as("activityIds")
        );
        AggregationResults<OrganizationActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, OrganizationActivityDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityTagDTO> findAllActivityByParentOrganization(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                //  graphLookup("activities").startWith("$compositeActivities").connectFrom("compositeActivities").connectTo("_id").maxDepth(0).as("compositeActivities"),
                project("name", "generalActivityTab", "compositeActivities", "permissionsActivityTab"));

        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();

    }

    public List<ActivityWithCompositeDTO> findAllActivityByUnitIdWithCompositeActivities(long unitId) {

        String groupString = "{'$group':{'_id':{id:'$_id','compositeActivities': { '$mergeObjects': [ { '$arrayElemAt': [ '$compositeActivitiesObject', 0 ] }, '$compositeActivities' ] },'compositeActivi': { '$mergeObjects': [ { '$arrayElemAt': [ '$compositeActivitiesObject', 0 ] }, '$compositeActivities' ] },'name':'$name', generalActivityTab:'$generalActivityTab',  expertises:'$expertises', employmentTypes:'$employmentTypes', rulesActivityTab:'$rulesActivityTab', skillActivityTab:'$skillActivityTab', timeCalculationActivityTab:'$timeCalculationActivityTab'}}}";
        String groupCompositeActivity = "{'$group':{'_id':{id:'$_id.id','name':'$_id.name', generalActivityTab:'$_id.generalActivityTab',  expertises:'$_id.expertises', employmentTypes:'$_id.employmentTypes', rulesActivityTab:'$_id.rulesActivityTab', skillActivityTab:'$_id.skillActivityTab', timeCalculationActivityTab:'$_id.timeCalculationActivityTab'},compositeActivities:{$push:'$_id.compositeActivities'}}}";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                unwind("compositeActivities", true),
                graphLookup("activities").startWith("$compositeActivities.activityId")
                        .connectFrom("compositeActivities.activityId").connectTo("_id").maxDepth(0).as("compositeActivitiesObject"),
                new CustomAggregationOperation(Document.parse(groupString)),
                new CustomAggregationOperation(Document.parse(groupCompositeActivity)));
        AggregationResults<ActivityWithCompositeDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCompositeDTO.class);
        return result.getMappedResults();
    }


    public List<Activity> findAllByTimeTypeId(BigInteger timeTypeId) {
        Query query = new Query(Criteria.where("balanceSettingsActivityTab.timeTypeId").is(timeTypeId).and("deleted").is(false));
        return mongoTemplate.find(query, Activity.class);

    }

    public List<ActivityDTO> getAllActivityWithTimeType(Long unitId, List<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                //"unitId").is(unitId).and(
                match(Criteria.where("deleted").is(false).and("_id").in(activityIds)),
                lookup("time_Type", "balanceSettingsActivityTab.timeTypeId", "_id", "timeType")
                , project("unitId")
                        .andInclude("deleted")
                        .andInclude("name")
                        .andInclude("expertises")
                        .andInclude("skillActivityTab")
                        .and("timeType").arrayElementAt(0).as("timeType"));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllActivityByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                //"unitId").is(unitId).and(
                match(Criteria.where("deleted").is(false).and("unitId").is(unitId))
                //lookup("time_Type","balanceSettingsActivityTab.timeTypeId","_id","timeType")
                , project("unitId")
                        .andInclude("deleted")
                        /*.andInclude("name")
                        .andInclude("expertises")
                        .andInclude("skillActivityTab")
                        .and("timeType").arrayElementAt(0).as("timeType")*/);
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    //Ignorecase
    public Activity getActivityByNameAndUnitId(Long unitId, String name) {
        Query query = new Query(Criteria.where("deleted").is(false).and("unitId").is(unitId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)));
        return (Activity) mongoTemplate.findOne(query, Activity.class);
    }


    public List<ActivityDTO> findAllActivitiesWithBalanceSettings(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                lookup("time_Type", "balanceSettingsActivityTab.timeTypeId", "_id",
                        "balanceSettingsActivityTab.timeType"),
                project("balanceSettingsActivityTab", "name", "expertises").and("balanceSettingsActivityTab.timeType").arrayElementAt(0).as("timeType").andInclude("timeType.label")

        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllActivitiesWithTimeTypes(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("countryId").is(countryId).and("deleted").is(false)),
                lookup("time_Type", "balanceSettingsActivityTab.timeTypeId", "_id",
                        "balanceSettingsActivityTab.timeType"),
                match(Criteria.where("balanceSettingsActivityTab.timeType.timeTypes").is(WORKING_TYPE)),
                project("balanceSettingsActivityTab", "name").and("balanceSettingsActivityTab.timeType").arrayElementAt(0).as("timeType").andInclude("timeType.label")

        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllActivitiesWithTimeTypesByUnit(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
                lookup("time_Type", "balanceSettingsActivityTab.timeTypeId", "_id",
                        "balanceSettingsActivityTab.timeType"),
                match(Criteria.where("balanceSettingsActivityTab.timeType.timeTypes").is(WORKING_TYPE)),
                project("balanceSettingsActivityTab", "name").and("balanceSettingsActivityTab.timeType").arrayElementAt(0).as("timeType").andInclude("timeType.label")

        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }


    public Activity findByNameExcludingCurrentInCountryAndDate(String name, BigInteger activityId, Long countryId, LocalDate startDate, LocalDate endDate) {
        Criteria criteria = Criteria.where("id").ne(activityId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and("deleted").is(false).and("countryId").is(countryId);
        if (endDate == null) {
            Criteria startDateCriteria = new Criteria().orOperator(Criteria.where("generalActivityTab.startDate").gte(startDate), Criteria.where("generalActivityTab.startDate").lte(startDate));
            Criteria endDateCriteria = new Criteria().orOperator(Criteria.where("generalActivityTab.endDate").exists(false), Criteria.where("generalActivityTab.endDate").gte(startDate));
            criteria.andOperator(startDateCriteria, endDateCriteria);
        } else {
            criteria.and("generalActivityTab.startDate").lte(endDate).orOperator(Criteria.where("generalActivityTab.endDate").exists(false), Criteria.where("generalActivityTab.endDate").gte(startDate));
        }
        Query query = new Query(criteria);
        return (Activity) mongoTemplate.findOne(query, Activity.class);
    }

    public Activity findByNameExcludingCurrentInUnitAndDate(String name, BigInteger activityId, Long unitId, LocalDate startDate, LocalDate endDate) {
        Criteria criteria = Criteria.where("id").ne(activityId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and("deleted").is(false).and("unitId").is(unitId);
        if (endDate == null) {
            Criteria startDateCriteria = new Criteria().orOperator(Criteria.where("generalActivityTab.startDate").gte(startDate), Criteria.where("generalActivityTab.startDate").lte(startDate));
            Criteria endDateCriteria = new Criteria().orOperator(Criteria.where("generalActivityTab.endDate").exists(false), Criteria.where("generalActivityTab.endDate").gte(startDate));
            criteria.andOperator(startDateCriteria, endDateCriteria);
        } else {
            criteria.and("generalActivityTab.startDate").lte(endDate).orOperator(Criteria.where("generalActivityTab.endDate").exists(false), Criteria.where("generalActivityTab.endDate").gte(startDate));
        }
        Query query = new Query(criteria);
        return (Activity) mongoTemplate.findOne(query, Activity.class);
    }

    public Set<BigInteger> findAllActivitiesByUnitIdAndUnavailableTimeType(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                lookup("time_Type", "balanceSettingsActivityTab.timeTypeId", "_id",
                        "balanceSettingsActivityTab.timeType"),
                match(Criteria.where("deleted").is(false).and("balanceSettingsActivityTab.timeType.timeTypes").is("NON_WORKING_TYPE")),
                // match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("balanceSettingsActivityTab.timeType.timeTypes").is("NON_WORKING_TYPE")),
                //group("unitId").addToSet("id").as("ids"),
                project("id")

        );
        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, Activity.class, Map.class);
        List<Map> activityIdMap = result.getMappedResults();
        //List<BigInteger> activityIds = activityIdMap.stream().map(Map:: get("_id")).collect(Collectors.toList());
        Set<BigInteger> activityIds = new HashSet<>();
        for (Map activityMap : activityIdMap) {
            activityIds.add(new BigInteger(activityMap.get("_id").toString()));
        }
        //List<BigInteger> activityIds1 = activityIdMap.stream().map(Map::get("_id"))
        return activityIds;//new HashSet<Long>(result.getMappedResults());
    }

    public Activity findByNameIgnoreCaseAndCountryIdAndByDate(String name, Long countryId, LocalDate startDate, LocalDate endDate) {
        Criteria criteria = Criteria.where("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and("deleted").is(false).and("countryId").is(countryId);
        if (endDate == null) {
            Criteria startDateCriteria = new Criteria().orOperator(Criteria.where("generalActivityTab.startDate").gte(startDate), Criteria.where("generalActivityTab.startDate").lte(startDate));
            Criteria endDateCriteria = new Criteria().orOperator(Criteria.where("generalActivityTab.endDate").exists(false), Criteria.where("generalActivityTab.endDate").gte(startDate));
            criteria.andOperator(startDateCriteria, endDateCriteria);
        } else {
            criteria.and("generalActivityTab.startDate").lte(endDate).orOperator(Criteria.where("generalActivityTab.endDate").exists(false), Criteria.where("generalActivityTab.endDate").gte(startDate));
        }
        Query query = new Query(criteria);
        Activity activity = mongoTemplate.findOne(query, Activity.class);
        return activity;
    }


    public Activity findByNameIgnoreCaseAndUnitIdAndByDate(String name, Long unitId, LocalDate startDate, LocalDate endDate) {
        Criteria criteria = Criteria.where("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and("deleted").is(false).and("unitId").is(unitId);
        if (endDate == null) {
            Criteria startDateCriteria = new Criteria().orOperator(Criteria.where("generalActivityTab.startDate").gte(startDate), Criteria.where("generalActivityTab.startDate").lte(startDate));
            Criteria endDateCriteria = new Criteria().orOperator(Criteria.where("generalActivityTab.endDate").exists(false), Criteria.where("generalActivityTab.endDate").gte(startDate));
            criteria.andOperator(startDateCriteria, endDateCriteria);
        } else {
            criteria.and("generalActivityTab.startDate").lte(endDate).orOperator(Criteria.where("generalActivityTab.endDate").exists(false), Criteria.where("generalActivityTab.endDate").gte(startDate));
        }
        Query query = new Query(criteria);
        Activity activity = mongoTemplate.findOne(query, Activity.class);
        return activity;
    }

    public ActivityWrapper findActivityAndTimeTypeByActivityId(BigInteger activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("id").is(activityId).and("deleted").is(false)),
                lookup("time_Type", "balanceSettingsActivityTab.timeTypeId", "_id","timeType"),
                project().and("name").as("activity.name").and("description").as("activity.description")
                        .and("countryId").as("activity.countryId").and("expertises").as("activity.expertises")
                        .and("id").as("activity.id")
                        .and("organizationTypes").as("activity.organizationTypes").and("organizationSubTypes").as("activity.organizationSubTypes")
                        .and("regions").as("activity.regions").and("levels").as("activity.levels")
                        .and("employmentTypes").as("activity.employmentTypes").and("tags").as("activity.tags")
                        .and("state").as("activity.state").and("unitId").as("activity.unitId").
                        and("parentId").as("activity.parentId").and("isParentActivity").as("activity.isParentActivity").and("generalActivityTab").as("activity.generalActivityTab")
                        .and("balanceSettingsActivityTab").as("activity.balanceSettingsActivityTab")
                        .and("rulesActivityTab").as("activity.rulesActivityTab").and("individualPointsActivityTab").as("activity.individualPointsActivityTab")
                        .and("timeCalculationActivityTab").as("activity.timeCalculationActivityTab").
                        and("compositeActivities").as("activity.compositeActivities")
                        .and("notesActivityTab").as("activity.notesActivityTab")
                        .and("communicationActivityTab").as("activity.communicationActivityTab")
                        .and("bonusActivityTab").as("activity.bonusActivityTab")
                        .and("skillActivityTab").as("activity.skillActivityTab")
                        .and("optaPlannerSettingActivityTab").as("activity.optaPlannerSettingActivityTab")
                        .and("ctaAndWtaSettingsActivityTab").as("activity.ctaAndWtaSettingsActivityTab")
                        .and("locationActivityTab").as("activity.locationActivityTab")
                        .and("permissionsActivityTab").as("activity.permissionsActivityTab")
                        .and("timeType").arrayElementAt(0).as("timeType").and("timeType.timeTypes").as("timeType")
        );
        AggregationResults<ActivityWrapper> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWrapper.class);
        return (result.getMappedResults().isEmpty()) ? null : result.getMappedResults().get(0);
    }

    public List<TimeTypeAndActivityIdDTO> findAllTimeTypeByActivityIds(Set<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("id").in(activityIds).and("deleted").is(false)),
                lookup("time_Type", "balanceSettingsActivityTab.timeTypeId", "_id",
                        "timeType"), project().and("id").as("activityId")
                        .and("timeType").arrayElementAt(0).as("timeType").and("timeType.timeTypes").as("timeType"));
        AggregationResults<TimeTypeAndActivityIdDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, TimeTypeAndActivityIdDTO.class);
        return result.getMappedResults();
    }

    public StaffActivitySettingDTO findStaffPersonalizedSettings(Long unitId, BigInteger activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("_id").is(activityId)),
                project("rulesActivityTab.shortestTime", "rulesActivityTab.longestTime", "optaPlannerSettingActivityTab.maxThisActivityPerShift", "optaPlannerSettingActivityTab.minLength", "optaPlannerSettingActivityTab.eligibleForMove")
        );
        AggregationResults<StaffActivitySettingDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, StaffActivitySettingDTO.class);
        return (result.getMappedResults().isEmpty()) ? null : result.getMappedResults().get(0);
    }


}