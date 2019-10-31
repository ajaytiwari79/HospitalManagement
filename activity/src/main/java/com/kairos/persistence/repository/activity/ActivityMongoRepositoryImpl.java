package com.kairos.persistence.repository.activity;

import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.CompositeActivityDTO;
import com.kairos.dto.activity.activity.OrganizationActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityWithCTAWTASettingsDTO;
import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.break_settings.BreakActivitiesDTO;
import com.kairos.dto.activity.counter.data.FilterCriteria;
import com.kairos.dto.activity.time_type.TimeTypeAndActivityIdDTO;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.TimeTypes;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.service.counter.ActivityFilterCriteria;
import com.kairos.wrapper.activity.ActivityTagDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.enums.TimeTypeEnum.PAID_BREAK;
import static com.kairos.enums.TimeTypeEnum.UNPAID_BREAK;
import static com.kairos.enums.TimeTypes.WORKING_TYPE;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class ActivityMongoRepositoryImpl implements CustomActivityMongoRepository {
    public static final String ACTIVITY_RULES_ACTIVITY_TAB = "activity.rulesActivityTab";
    public static final String ACTIVITY_INDIVIDUAL_POINTS_ACTIVITY_TAB = "activity.individualPointsActivityTab";
    public static final String UNIT_ID = "unitId";
    public static final String TIME_TYPE = "time_Type";
    public static final String DELETED = "deleted";
    public static final String BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID = "balanceSettingsActivityTab.timeTypeId";
    public static final String TIME_TYPE_INFO = "timeTypeInfo";
    public static final String IS_PARENT_ACTIVITY = "isParentActivity";
    public static final String ORGANIZATION_TYPES = "organizationTypes";
    public static final String ORGANIZATION_SUB_TYPES = "organizationSubTypes";
    public static final String STATE = "state";
    public static final String TAGS_DATA = "tags_data";
    public static final String DESCRIPTION = "description";
    public static final String PARENT_ID = "parentId";
    public static final String GENERAL_ACTIVITY_TAB = "generalActivityTab";
    public static final String ACTIVITY_PRIORITY_ID = "activityPriorityId";
    public static final String TIME_TYPE1 = "timeType";
    public static final String RULES_ACTIVITY_TAB = "rulesActivityTab";
    public static final String TIME_TYPE_ALLOW_CHILD_ACTIVITIES = "timeType.allowChildActivities";
    public static final String ALLOW_CHILD_ACTIVITIES = "allowChildActivities";
    public static final String CHILD_ACTIVITY_IDS = "childActivityIds";
    public static final String APPLICABLE_FOR_CHILD_ACTIVITIES = "applicableForChildActivities";
    public static final String TIME_CALCULATION_ACTIVITY_TAB_METHOD_FOR_CALCULATING_TIME = "timeCalculationActivityTab.methodForCalculatingTime";
    public static final String ACTIVITIES = "activities";
    public static final String CTA_AND_WTA_SETTINGS_ACTIVITY_TAB = "ctaAndWtaSettingsActivityTab";
    public static final String GENERAL_ACTIVITY_TAB_CATEGORY_ID = "generalActivityTab.categoryId";
    public static final String TIME_TYPE_TIME_TYPES = "timeType.timeTypes";
    public static final String BALANCE_SETTINGS_ACTIVITY_TAB = "balanceSettingsActivityTab";
    public static final String EXPERTISES = "expertises";
    public static final String SKILL_ACTIVITY_TAB = "skillActivityTab";
    public static final String BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO = "balanceSettingsActivityTab.timeTypeInfo";
    public static final String TIME_TYPE_INFO_LABEL = "timeTypeInfo.label";
    public static final String GENERAL_ACTIVITY_TAB_START_DATE = "generalActivityTab.startDate";
    public static final String GENERAL_ACTIVITY_TAB_END_DATE = "generalActivityTab.endDate";
    public static final String ACTIVITY_ID = "activity._id";
    public static final String ACTIVITY_NAME = "activity.name";
    public static final String ACTIVITY_COUNTRY_ID = "activity.countryId";
    public static final String ACTIVITY_EXPERTISES = "activity.expertises";
    public static final String EMPLOYMENT_TYPES = "employmentTypes";
    public static final String ACTIVITY_EMPLOYMENT_TYPES = "activity.employmentTypes";
    public static final String ACTIVITY_STATE = "activity.state";
    public static final String ACTIVITY_UNIT_ID = "activity.unitId";
    public static final String ACTIVITY_PARENT_ID = "activity.parentId";
    public static final String ACTIVITY_IS_PARENT_ACTIVITY = "activity.isParentActivity";
    public static final String ACTIVITY_GENERAL_ACTIVITY_TAB = "activity.generalActivityTab";
    public static final String ACTIVITY_BALANCE_SETTINGS_ACTIVITY_TAB = "activity.balanceSettingsActivityTab";
    public static final String INDIVIDUAL_POINTS_ACTIVITY_TAB = "individualPointsActivityTab";
    public static final String TIME_CALCULATION_ACTIVITY_TAB = "timeCalculationActivityTab";
    public static final String ACTIVITY_TIME_CALCULATION_ACTIVITY_TAB = "activity.timeCalculationActivityTab";
    public static final String NOTES_ACTIVITY_TAB = "notesActivityTab";
    public static final String ACTIVITY_NOTES_ACTIVITY_TAB = "activity.notesActivityTab";
    public static final String COMMUNICATION_ACTIVITY_TAB = "communicationActivityTab";
    public static final String ACTIVITY_COMMUNICATION_ACTIVITY_TAB = "activity.communicationActivityTab";
    public static final String BONUS_ACTIVITY_TAB = "bonusActivityTab";
    public static final String ACTIVITY_BONUS_ACTIVITY_TAB = "activity.bonusActivityTab";
    public static final String ACTIVITY_SKILL_ACTIVITY_TAB = "activity.skillActivityTab";
    public static final String OPTA_PLANNER_SETTING_ACTIVITY_TAB = "optaPlannerSettingActivityTab";
    public static final String ACTIVITY_OPTA_PLANNER_SETTING_ACTIVITY_TAB = "activity.optaPlannerSettingActivityTab";
    public static final String LOCATION_ACTIVITY_TAB = "locationActivityTab";
    public static final String ACTIVITY_CTA_AND_WTA_SETTINGS_ACTIVITY_TAB = "activity.ctaAndWtaSettingsActivityTab";
    public static final String ACTIVITY_LOCATION_ACTIVITY_TAB = "activity.locationActivityTab";
    public static final String PHASE_SETTINGS_ACTIVITY_TAB = "phaseSettingsActivityTab";
    public static final String ACTIVITY_PHASE_SETTINGS_ACTIVITY_TAB = "activity.phaseSettingsActivityTab";
    public static final String CATEGORY = "category";
    public static final String CATEGORY_ID = "categoryId";
    public static final String CATEGORY_NAME = "categoryName";
    public static final String CHILD_ACTIVITIES = "childActivities";
    public static final String COMPOSITE_TIME_TYPE_INFO = "compositeTimeTypeInfo";
    public static final String CHILD_ACTIVITIES_ID = "childActivities._id";
    public static final String PHASE_TEMPLATE_VALUES_ACTIVITY_SHIFT_STATUS_SETTINGS = "phaseTemplateValues.activityShiftStatusSettings";
    public static final String ACTIVITY_SHIFT_STATUS_SETTINGS = "activityShiftStatusSettings";
    public static final String PHASE_ID = "phaseId";
    public static final String ACTIVITY_PRIORITY = "activityPriority";
    public static final String COUNTRY_ID = "countryId";
    @Inject
    private MongoTemplate mongoTemplate;

    public List<ActivityDTO> findAllActivityByOrganizationGroupWithCategoryName(Long unitId, boolean deleted) {
        List<AggregationOperation> customAgregationForCompositeActivity = new ArrayList<>();
        customAgregationForCompositeActivity.add(match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(deleted).and("rulesActivityTab.eligibleForStaffingLevel").is(true)));
        customAgregationForCompositeActivity.add(lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE_INFO));
        customAgregationForCompositeActivity.addAll(getCustomAgregationForCompositeActivityWithCategory(true));
        Aggregation aggregation = Aggregation.newAggregation(customAgregationForCompositeActivity);
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityTagDTO> findAllActivitiesByOrganizationType(List<Long> orgTypeIds, List<Long> orgSubTypeIds) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(true)
                        .and(ORGANIZATION_TYPES).in(orgTypeIds).orOperator(Criteria.where(ORGANIZATION_SUB_TYPES).in(orgSubTypeIds))
                        .and(STATE).nin("DRAFT")),
                unwind("tags", true),
                lookup("tag", "tags", "_id", TAGS_DATA),
                unwind(TAGS_DATA, true),
                group("$id")
                        .first("$name").as("name")
                        .first("$description").as(DESCRIPTION)
                        .first("$unitId").as(UNIT_ID)
                        .first("$parentId").as(PARENT_ID)
                        .first(GENERAL_ACTIVITY_TAB).as(GENERAL_ACTIVITY_TAB)
                        .first("$activityPriorityId").as(ACTIVITY_PRIORITY_ID)
                        .push(TAGS_DATA).as("tags")

        );

        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();

    }

    public List<CompositeActivityDTO> getCompositeActivities(BigInteger activityId) {

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(activityId).and(DELETED).is(false))
        );
        AggregationResults<CompositeActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, CompositeActivityDTO.class);
        return result.getMappedResults();

    }

    public List<ActivityTagDTO> findAllowChildActivityByUnitIdAndDeleted(Long unitId, boolean deleted) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(deleted)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                project("name", DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB, ACTIVITY_PRIORITY_ID, CHILD_ACTIVITY_IDS).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).as(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID)
                        .and("timeType.activityCanBeCopiedForOrganizationHierarchy").arrayElementAt(0).as("activityCanBeCopiedForOrganizationHierarchy")
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(APPLICABLE_FOR_CHILD_ACTIVITIES),
                match(Criteria.where(APPLICABLE_FOR_CHILD_ACTIVITIES).is(true))
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();

    }

    public List<ActivityTagDTO> findAllActivityByUnitIdAndDeleted(Long unitId, boolean deleted) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(deleted)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                lookup("tag", "tags", "_id", "tags"),
                project("name", DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB, "tags", ACTIVITY_PRIORITY_ID).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).as(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID)
                        .and(TIME_CALCULATION_ACTIVITY_TAB_METHOD_FOR_CALCULATING_TIME).as("methodForCalculatingTime")
                        .and("timeType.activityCanBeCopiedForOrganizationHierarchy").arrayElementAt(0).as("activityCanBeCopiedForOrganizationHierarchy")
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(APPLICABLE_FOR_CHILD_ACTIVITIES)
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityTagDTO> findAllActivityByCountry(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(true)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                lookup("tag", "tags", "_id", "tags"),
                project("name", STATE, DESCRIPTION, COUNTRY_ID, IS_PARENT_ACTIVITY, GENERAL_ACTIVITY_TAB, "tags", ACTIVITY_PRIORITY_ID, CHILD_ACTIVITY_IDS).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).as(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID)
                        .and(TIME_CALCULATION_ACTIVITY_TAB_METHOD_FOR_CALCULATING_TIME).as("methodForCalculatingTime")

                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(APPLICABLE_FOR_CHILD_ACTIVITIES)
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityTagDTO> findAllowChildActivityByCountryId(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(true)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                project("name", COUNTRY_ID, IS_PARENT_ACTIVITY, GENERAL_ACTIVITY_TAB, CHILD_ACTIVITY_IDS).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).as(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(APPLICABLE_FOR_CHILD_ACTIVITIES),
                match(Criteria.where(APPLICABLE_FOR_CHILD_ACTIVITIES).is(true))
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }

    public ActivityWithCompositeDTO findActivityByActivityId(BigInteger activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(activityId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                lookup(ACTIVITIES, "_id", CHILD_ACTIVITY_IDS, "parentActivity"),
                project("name", STATE, DESCRIPTION, COUNTRY_ID, IS_PARENT_ACTIVITY, GENERAL_ACTIVITY_TAB, ACTIVITY_PRIORITY_ID, CHILD_ACTIVITY_IDS).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).as(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID)
                        .and("parentActivity._id").as("parentActivityId")
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(APPLICABLE_FOR_CHILD_ACTIVITIES)
        );
        AggregationResults<ActivityWithCompositeDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCompositeDTO.class);
        return isCollectionNotEmpty(result.getMappedResults()) ? result.getMappedResults().get(0) : null;
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByCountry(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(true).and(STATE).is(ActivityStateEnum.PUBLISHED)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                project("$id", "name", DESCRIPTION, CTA_AND_WTA_SETTINGS_ACTIVITY_TAB, GENERAL_ACTIVITY_TAB_CATEGORY_ID)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1),
                match(Criteria.where(TIME_TYPE_TIME_TYPES).is(TimeTypes.WORKING_TYPE))
        );
        AggregationResults<ActivityWithCTAWTASettingsDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCTAWTASettingsDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                project("$id", "name", DESCRIPTION, CTA_AND_WTA_SETTINGS_ACTIVITY_TAB, GENERAL_ACTIVITY_TAB_CATEGORY_ID).and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1),
                match(Criteria.where(TIME_TYPE_TIME_TYPES).is(TimeTypes.WORKING_TYPE))
        );
        AggregationResults<ActivityWithCTAWTASettingsDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCTAWTASettingsDTO.class);
        return result.getMappedResults();
    }

    public List<OrganizationActivityDTO> findAllActivityOfUnitsByParentActivity(List<BigInteger> parentActivityIds, List<Long> unitIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(false).and(UNIT_ID).in(unitIds).and(PARENT_ID).in(parentActivityIds)),
                project("$id", UNIT_ID, PARENT_ID)
        );
        AggregationResults<OrganizationActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, OrganizationActivityDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityTagDTO> findAllActivityByParentOrganization(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                project("name", GENERAL_ACTIVITY_TAB, BALANCE_SETTINGS_ACTIVITY_TAB));

        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();

    }

    public List<ActivityWithCompositeDTO> findAllActivityByUnitIdWithCompositeActivities(List<BigInteger> activityIds) {
        Aggregation aggregation = getParentActivityAggregation(Criteria.where("_id").in(activityIds).and(DELETED).is(false));
        AggregationResults<ActivityWithCompositeDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCompositeDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityDTO> getAllActivityWithTimeType(Long unitId, List<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                //"unitId").is(unitId).and(
                match(Criteria.where(DELETED).is(false).and("_id").in(activityIds)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE_INFO)
                , project(UNIT_ID)
                        .andInclude(DELETED)
                        .andInclude("name")
                        .andInclude(EXPERTISES)
                        .andInclude(SKILL_ACTIVITY_TAB)
                        .and(TIME_TYPE_INFO).arrayElementAt(0).as(TIME_TYPE1));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllActivityByUnitId(Long unitId, boolean deleted) {
        List<AggregationOperation> customAgregationForCompositeActivity = new ArrayList<>();
        customAgregationForCompositeActivity.add(match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(deleted)));
        customAgregationForCompositeActivity.add(lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE_INFO));
        customAgregationForCompositeActivity.addAll(getCustomAgregationForCompositeActivityWithCategory(false));
        //customAgregationForCompositeActivity.add(project("name", "categoryId", "categoryName").and("timeTypeInfo").as("timeTypeInfo").and("childActivities").as("childActivities"));
        customAgregationForCompositeActivity.add(match(Criteria.where("timeTypeInfo.partOfTeam").is(true)));
        Aggregation aggregation = Aggregation.newAggregation(customAgregationForCompositeActivity);
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    //Ignorecase

    public Activity getActivityByNameAndUnitId(Long unitId, String name) {
        Query query = new Query(Criteria.where(DELETED).is(false).and(UNIT_ID).is(unitId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)));
        return mongoTemplate.findOne(query, Activity.class);
    }


    public List<ActivityDTO> findAllActivitiesWithBalanceSettings(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO),
                project(BALANCE_SETTINGS_ACTIVITY_TAB, "name", EXPERTISES)
                        .and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO).arrayElementAt(0).as(TIME_TYPE1).andInclude(TIME_TYPE_INFO_LABEL)

        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllActivitiesWithTimeTypes(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO),
                match(Criteria.where("balanceSettingsActivityTab.timeTypeInfo.timeTypes").is(WORKING_TYPE)),
                project(BALANCE_SETTINGS_ACTIVITY_TAB, "name").and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO).arrayElementAt(0).as(TIME_TYPE1).andInclude(TIME_TYPE_INFO_LABEL)

        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllActivitiesWithTimeTypesByUnit(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO),
                match(Criteria.where("balanceSettingsActivityTab.timeTypeInfo.timeTypes").is(WORKING_TYPE)),
                project(BALANCE_SETTINGS_ACTIVITY_TAB, "name").and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO).arrayElementAt(0).as(TIME_TYPE1).andInclude(TIME_TYPE_INFO_LABEL)

        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }


    public Activity findByNameExcludingCurrentInCountryAndDate(String name, BigInteger activityId, Long countryId, LocalDate startDate, LocalDate endDate) {
        Criteria criteria = Criteria.where("id").ne(activityId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false).and(COUNTRY_ID).is(countryId);
        if (endDate == null) {
            Criteria startDateCriteria = new Criteria().orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_START_DATE).gte(startDate), Criteria.where(GENERAL_ACTIVITY_TAB_START_DATE).lte(startDate));
            Criteria endDateCriteria = new Criteria().orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).exists(false), Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).gte(startDate));
            criteria.andOperator(startDateCriteria, endDateCriteria);
        } else {
            criteria.and(GENERAL_ACTIVITY_TAB_START_DATE).lte(endDate).orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).exists(false), Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).gte(startDate));
        }
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Activity.class);
    }

    public Activity findByNameExcludingCurrentInUnitAndDate(String name, BigInteger activityId, Long unitId, LocalDate startDate, LocalDate endDate) {
        Criteria criteria = Criteria.where("id").ne(activityId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false).and(UNIT_ID).is(unitId);
        if (endDate == null) {
            Criteria startDateCriteria = new Criteria().orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_START_DATE).gte(startDate), Criteria.where(GENERAL_ACTIVITY_TAB_START_DATE).lte(startDate));
            Criteria endDateCriteria = new Criteria().orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).exists(false), Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).gte(startDate));
            criteria.andOperator(startDateCriteria, endDateCriteria);
        } else {
            criteria.and(GENERAL_ACTIVITY_TAB_START_DATE).lte(endDate).orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).exists(false), Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).gte(startDate));
        }
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Activity.class);
    }

    public Set<BigInteger> findAllActivitiesByUnitIdAndUnavailableTimeType(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        "balanceSettingsActivityTab.timeType"),
                match(Criteria.where(DELETED).is(false).and("balanceSettingsActivityTab.timeType.timeTypes").is("NON_WORKING_TYPE")),
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
        Criteria criteria = Criteria.where("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false).and(COUNTRY_ID).is(countryId);
        if (endDate == null) {
            Criteria startDateCriteria = new Criteria().orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_START_DATE).gte(startDate), Criteria.where(GENERAL_ACTIVITY_TAB_START_DATE).lte(startDate));
            Criteria endDateCriteria = new Criteria().orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).exists(false), Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).gte(startDate));
            criteria.andOperator(startDateCriteria, endDateCriteria);
        } else {
            criteria.and(GENERAL_ACTIVITY_TAB_START_DATE).lte(endDate).orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).exists(false), Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).gte(startDate));
        }
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Activity.class);
    }


    public Activity findByNameIgnoreCaseAndUnitIdAndByDate(String name, Long unitId, LocalDate startDate, LocalDate endDate) {
        Criteria criteria = Criteria.where("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false).and(UNIT_ID).is(unitId);
        if (endDate == null) {
            Criteria startDateCriteria = new Criteria().orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_START_DATE).gte(startDate), Criteria.where(GENERAL_ACTIVITY_TAB_START_DATE).lte(startDate));
            Criteria endDateCriteria = new Criteria().orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).exists(false), Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).gte(startDate));
            criteria.andOperator(startDateCriteria, endDateCriteria);
        } else {
            criteria.and(GENERAL_ACTIVITY_TAB_START_DATE).lte(endDate).orOperator(Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).exists(false), Criteria.where(GENERAL_ACTIVITY_TAB_END_DATE).gte(startDate));
        }
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Activity.class);
    }

    public ActivityWrapper findActivityAndTimeTypeByActivityId(BigInteger activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("id").is(activityId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        TIME_TYPE1),
                project().and("id").as(ACTIVITY_ID).and("name").as(ACTIVITY_NAME).and(DESCRIPTION).as("activity.description")
                        .and(COUNTRY_ID).as(ACTIVITY_COUNTRY_ID).and(EXPERTISES).as(ACTIVITY_EXPERTISES)
                        .and("id").as("activity.id")
                        .and(ORGANIZATION_TYPES).as("activity.organizationTypes").and(ORGANIZATION_SUB_TYPES).as("activity.organizationSubTypes")
                        .and("regions").as("activity.regions").and("levels").as("activity.levels")
                        .and(EMPLOYMENT_TYPES).as(ACTIVITY_EMPLOYMENT_TYPES).and("tags").as("activity.tags")
                        .and(STATE).as(ACTIVITY_STATE).and(UNIT_ID).as(ACTIVITY_UNIT_ID)
                        .and(PARENT_ID).as(ACTIVITY_PARENT_ID).and(IS_PARENT_ACTIVITY).as(ACTIVITY_IS_PARENT_ACTIVITY).and(GENERAL_ACTIVITY_TAB).as(ACTIVITY_GENERAL_ACTIVITY_TAB)
                        .and(BALANCE_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_BALANCE_SETTINGS_ACTIVITY_TAB)
                        .and(RULES_ACTIVITY_TAB).as(ACTIVITY_RULES_ACTIVITY_TAB).and(INDIVIDUAL_POINTS_ACTIVITY_TAB).as(ACTIVITY_INDIVIDUAL_POINTS_ACTIVITY_TAB)
                        .and(TIME_CALCULATION_ACTIVITY_TAB).as(ACTIVITY_TIME_CALCULATION_ACTIVITY_TAB)
                        .and(NOTES_ACTIVITY_TAB).as(ACTIVITY_NOTES_ACTIVITY_TAB)
                        .and(COMMUNICATION_ACTIVITY_TAB).as(ACTIVITY_COMMUNICATION_ACTIVITY_TAB)
                        .and(BONUS_ACTIVITY_TAB).as(ACTIVITY_BONUS_ACTIVITY_TAB)
                        .and(SKILL_ACTIVITY_TAB).as(ACTIVITY_SKILL_ACTIVITY_TAB)
                        .and(OPTA_PLANNER_SETTING_ACTIVITY_TAB).as(ACTIVITY_OPTA_PLANNER_SETTING_ACTIVITY_TAB)
                        .and(CTA_AND_WTA_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_CTA_AND_WTA_SETTINGS_ACTIVITY_TAB)
                        .and(LOCATION_ACTIVITY_TAB).as(ACTIVITY_LOCATION_ACTIVITY_TAB)
                        .and(PHASE_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_PHASE_SETTINGS_ACTIVITY_TAB)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1).and(TIME_TYPE_TIME_TYPES).as(TIME_TYPE1)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE_INFO)
        );
        AggregationResults<ActivityWrapper> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWrapper.class);
        return (result.getMappedResults().isEmpty()) ? null : result.getMappedResults().get(0);
    }

    @Override
    public List<ActivityWrapper> findActivitiesAndTimeTypeByActivityId(Collection<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("id").in(activityIds).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        TIME_TYPE1),
                lookup("activityPriority", "activityPriorityId", "_id",
                        "activityPriority"),
                project().and("id").as(ACTIVITY_ID).and("name").as(ACTIVITY_NAME).and(DESCRIPTION).as("activity.description")
                        .and(COUNTRY_ID).as(ACTIVITY_COUNTRY_ID).and(EXPERTISES).as(ACTIVITY_EXPERTISES)
                        .and("id").as("activity.id").and("activityPriorityId").as("activity.activityPriorityId")
                        .and(ORGANIZATION_TYPES).as("activity.organizationTypes").and(ORGANIZATION_SUB_TYPES).as("activity.organizationSubTypes")
                        .and("regions").as("activity.regions").and("levels").as("activity.levels")
                        .and(EMPLOYMENT_TYPES).as(ACTIVITY_EMPLOYMENT_TYPES).and("tags").as("activity.tags")
                        .and(STATE).as(ACTIVITY_STATE).and(UNIT_ID).as(ACTIVITY_UNIT_ID)
                        .and(PARENT_ID).as(ACTIVITY_PARENT_ID).and(IS_PARENT_ACTIVITY).as(ACTIVITY_IS_PARENT_ACTIVITY).and(GENERAL_ACTIVITY_TAB).as(ACTIVITY_GENERAL_ACTIVITY_TAB)
                        .and(BALANCE_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_BALANCE_SETTINGS_ACTIVITY_TAB)
                        .and(RULES_ACTIVITY_TAB).as(ACTIVITY_RULES_ACTIVITY_TAB).and(INDIVIDUAL_POINTS_ACTIVITY_TAB).as(ACTIVITY_INDIVIDUAL_POINTS_ACTIVITY_TAB)
                        .and(TIME_CALCULATION_ACTIVITY_TAB).as(ACTIVITY_TIME_CALCULATION_ACTIVITY_TAB)
                        .and(NOTES_ACTIVITY_TAB).as(ACTIVITY_NOTES_ACTIVITY_TAB)
                        .and(COMMUNICATION_ACTIVITY_TAB).as(ACTIVITY_COMMUNICATION_ACTIVITY_TAB)
                        .and(BONUS_ACTIVITY_TAB).as(ACTIVITY_BONUS_ACTIVITY_TAB)
                        .and(SKILL_ACTIVITY_TAB).as(ACTIVITY_SKILL_ACTIVITY_TAB)
                        .and(OPTA_PLANNER_SETTING_ACTIVITY_TAB).as(ACTIVITY_OPTA_PLANNER_SETTING_ACTIVITY_TAB)
                        .and(CTA_AND_WTA_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_CTA_AND_WTA_SETTINGS_ACTIVITY_TAB)
                        .and(LOCATION_ACTIVITY_TAB).as(ACTIVITY_LOCATION_ACTIVITY_TAB)
                        .and(PHASE_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_PHASE_SETTINGS_ACTIVITY_TAB)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1).and(TIME_TYPE_TIME_TYPES).as(TIME_TYPE1)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE_INFO)
                        .and("activityPriority").arrayElementAt(0).as("activityPriority")
        );
        AggregationResults<ActivityWrapper> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWrapper.class);
        return result.getMappedResults();
    }

    public List<TimeTypeAndActivityIdDTO> findAllTimeTypeByActivityIds(Set<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("id").in(activityIds).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        TIME_TYPE1), project().and("id").as("activityId")
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1).and(TIME_TYPE_TIME_TYPES).as(TIME_TYPE1));
        AggregationResults<TimeTypeAndActivityIdDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, TimeTypeAndActivityIdDTO.class);
        return result.getMappedResults();
    }

    public StaffActivitySettingDTO findStaffPersonalizedSettings(Long unitId, BigInteger activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and("_id").is(activityId)),
                project("rulesActivityTab.shortestTime", "rulesActivityTab.longestTime", "rulesActivityTab.earliestStartTime", "rulesActivityTab.latestStartTime", "rulesActivityTab.maximumEndTime", "optaPlannerSettingActivityTab.maxThisActivityPerShift", "optaPlannerSettingActivityTab.minLength", "optaPlannerSettingActivityTab.eligibleForMove")
        );
        AggregationResults<StaffActivitySettingDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, StaffActivitySettingDTO.class);
        return (result.getMappedResults().isEmpty()) ? null : result.getMappedResults().get(0);
    }

    public List<BreakActivitiesDTO> getAllActivitiesGroupedByTimeType(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                group("$timeType.timeTypes").push(new BasicDBObject("_id", "$_id").append("name", "$name")).as(ACTIVITIES),
                project(ACTIVITIES).and("_id").as(TIME_TYPE1));
        AggregationResults<BreakActivitiesDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, BreakActivitiesDTO.class);
        return result.getMappedResults();
    }

    public List<BigInteger> getActivityIdsByFilter(List<FilterCriteria> filters) {
        ActivityFilterCriteria activityCriteria = ActivityFilterCriteria.getInstance();
        for (FilterCriteria criteria : filters) {
            switch (criteria.getType()) {
                case ACTIVITY_IDS:
                    activityCriteria.setActivityIds(criteria.getValues());
                    break;
                case UNIT_IDS:
                    activityCriteria.setUnitId(criteria.getValues());
                    break;
                case ACTIVITY_CATEGORY_TYPE:
                    activityCriteria.setCategoryId(criteria.getValues());
                    break;
                case EMPLOYMENT_TYPE:
                    activityCriteria.setEmploymentTypes(criteria.getValues());
                    break;
                case EXPERTISE:
                    activityCriteria.setExpertiseCriteria(criteria.getValues());
                    break;
                case TIME_TYPE:
                    activityCriteria.setTimeTypeList(criteria.getValues());
                    break;
                case PLANNED_TIME_TYPE:
                    activityCriteria.setPlanneTimeType(criteria.getValues());
                    break;
                case ORGANIZATION_TYPE:
                    activityCriteria.setOrganizationTypes(criteria.getValues());
                    break;
                default:
                    break;
            }
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(activityCriteria.getFilterCriteria()),
                group("0").push("$_id").as("activityIds"),
                project(ACTIVITIES)
        );
        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, Activity.class, Map.class);
        if (result.getMappedResults().isEmpty()) return new ArrayList<>();
        return (List<BigInteger>) result.getMappedResults().get(0).get("activityIds");
    }

    public List<ActivityDTO> findAllByTimeTypeIdAndUnitId(Set<BigInteger> timeTypeIds, Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).in(timeTypeIds).and("rulesActivityTab.allowedAutoAbsence").is(true).and(DELETED).is(false).and(UNIT_ID).is(unitId)),
                project().and("id").as("id").and("name").as("name").and(TIME_CALCULATION_ACTIVITY_TAB).as(TIME_CALCULATION_ACTIVITY_TAB));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<ActivityWrapper> findActivitiesAndTimeTypeByParentIdsAndUnitId(List<BigInteger> activityIds, Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(UNIT_ID).is(unitId).orOperator(Criteria.where("countryParentId").in(activityIds), Criteria.where("id").in(activityIds))),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                project().and("id").as(ACTIVITY_ID).and("name").as(ACTIVITY_NAME)
                        .and(COUNTRY_ID).as(ACTIVITY_COUNTRY_ID).and(EXPERTISES).as(ACTIVITY_EXPERTISES)
                        .and(PARENT_ID).as(ACTIVITY_PARENT_ID)
                        .and("countryParentId").as("activity.countryParentId")
                        .and(EMPLOYMENT_TYPES).as(ACTIVITY_EMPLOYMENT_TYPES)
                        .and(STATE).as(ACTIVITY_STATE).and(UNIT_ID).as(ACTIVITY_UNIT_ID)
                        .and(IS_PARENT_ACTIVITY).as(ACTIVITY_IS_PARENT_ACTIVITY).and(GENERAL_ACTIVITY_TAB).as(ACTIVITY_GENERAL_ACTIVITY_TAB)
                        .and(BALANCE_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_BALANCE_SETTINGS_ACTIVITY_TAB)
                        .and(RULES_ACTIVITY_TAB).as(ACTIVITY_RULES_ACTIVITY_TAB).and(INDIVIDUAL_POINTS_ACTIVITY_TAB).as(ACTIVITY_INDIVIDUAL_POINTS_ACTIVITY_TAB)
                        .and(TIME_CALCULATION_ACTIVITY_TAB).as(ACTIVITY_TIME_CALCULATION_ACTIVITY_TAB)
                        .and(NOTES_ACTIVITY_TAB).as(ACTIVITY_NOTES_ACTIVITY_TAB)
                        .and(COMMUNICATION_ACTIVITY_TAB).as(ACTIVITY_COMMUNICATION_ACTIVITY_TAB)
                        .and(BONUS_ACTIVITY_TAB).as(ACTIVITY_BONUS_ACTIVITY_TAB)
                        .and(SKILL_ACTIVITY_TAB).as(ACTIVITY_SKILL_ACTIVITY_TAB)
                        .and(OPTA_PLANNER_SETTING_ACTIVITY_TAB).as(ACTIVITY_OPTA_PLANNER_SETTING_ACTIVITY_TAB)
                        .and(CTA_AND_WTA_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_CTA_AND_WTA_SETTINGS_ACTIVITY_TAB)
                        .and(LOCATION_ACTIVITY_TAB).as(ACTIVITY_LOCATION_ACTIVITY_TAB)
                        .and(PHASE_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_PHASE_SETTINGS_ACTIVITY_TAB)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1).and(TIME_TYPE_TIME_TYPES).as(TIME_TYPE1)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE_INFO)
        );
        AggregationResults<ActivityWrapper> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWrapper.class);
        return result.getMappedResults();
    }

    @Override
    public List<ActivityDTO> findAllActivitiesByCountryIdAndTimeTypes(Long countryId, List<BigInteger> timeTypeIds) {
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).in(timeTypeIds).and(DELETED).is(false).and(COUNTRY_ID).is(countryId))
                , project().and("id").as("id").and("name").as("name"));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();

    }

    @Override
    public List<Activity> findAllActivitiesByOrganizationTypeOrSubTypeOrBreakTypes(Long orgTypeIds, List<Long> orgSubTypeIds) {
        List<TimeTypeEnum> breakTypes = new ArrayList<>();
        breakTypes.add(PAID_BREAK);
        breakTypes.add(UNPAID_BREAK);
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where(IS_PARENT_ACTIVITY).is(true).and(DELETED).is(false))
                , lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                match(Criteria.where(STATE).nin("DRAFT").orOperator(Criteria.where(ORGANIZATION_SUB_TYPES).in(orgSubTypeIds), Criteria.where("timeType.secondLevelType").in(breakTypes))));
        AggregationResults<Activity> result = mongoTemplate.aggregate(aggregation, Activity.class, Activity.class);
        return result.getMappedResults();
    }

    @Override
    public List<ActivityWrapper> findActivityAndTimeTypeByActivityIdsAndNotFullDayAndFullWeek(Set<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("id").in(activityIds).and(DELETED).is(false).and(TIME_CALCULATION_ACTIVITY_TAB_METHOD_FOR_CALCULATING_TIME).nin(CommonConstants.FULL_DAY_CALCULATION, CommonConstants.FULL_WEEK)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        TIME_TYPE1),
                project().and("id").as(ACTIVITY_ID).and("name").as(ACTIVITY_NAME)
                        .and(BALANCE_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_BALANCE_SETTINGS_ACTIVITY_TAB)
                        .and(RULES_ACTIVITY_TAB).as(ACTIVITY_RULES_ACTIVITY_TAB)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE_INFO)
        );
        AggregationResults<ActivityWrapper> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWrapper.class);
        return result.getMappedResults();
    }

    @Override
    public List<ActivityDTO> findChildActivityActivityIds(Set<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("id").in(activityIds).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                project("name", "id", CHILD_ACTIVITY_IDS)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(APPLICABLE_FOR_CHILD_ACTIVITIES),
                match(Criteria.where(APPLICABLE_FOR_CHILD_ACTIVITIES).is(true))
        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public boolean existsByActivityIdInChildActivities(BigInteger activityId) {
        Query query = new Query(Criteria.where(CHILD_ACTIVITY_IDS).is(activityId).and(DELETED).is(false).and(STATE).is(ActivityStateEnum.PUBLISHED));
        return isNotNull(mongoTemplate.findOne(query, Activity.class));
    }

    public List<Activity> findByActivityIdInChildActivities(BigInteger activityId, List<BigInteger> allowedActivityIds) {
        Query query = new Query(Criteria.where("_id").ne(activityId).and(CHILD_ACTIVITY_IDS).in(allowedActivityIds).and(DELETED).is(false).and(STATE).is(ActivityStateEnum.PUBLISHED));
        return mongoTemplate.find(query, Activity.class);
    }

    private List<AggregationOperation> getCustomAgregationForCompositeActivityWithCategory(boolean isChildActivityEligibleForStaffingLevel) {
        if (isChildActivityEligibleForStaffingLevel) {

        }
        String group = "{  \n" +
                "      \"$group\":{  \n" +
                "         \"_id\":{  \n" +
                "            \"timeCalculationActivityTab\":\"$timeCalculationActivityTab\",\n" +
                "            \"balanceSettingsActivityTab\":\"$balanceSettingsActivityTab\",\n" +
                "            \"name\":\"$name\",\n" +
                "            'timeTypeInfo':'$timeTypeInfo',\n" +
                "            'compositeTimeTypeInfo':'$compositeTimeTypeInfo',\n" +
                "             \"id\":\"$_id\",\n" +
                "            \"categoryId\":\"$categoryId\",\n" +
                "            \"categoryName\":\"$categoryName\"\n" +
                "         },\n" +
                "         \"childActivities\":{  \n" +
                "            \"$addToSet\":\"$childActivities\"\n" +
                "         }\n" +
                "      }\n" +
                "   }";
        String projection = new StringBuffer("{'$project':{'childActivities':").append(isChildActivityEligibleForStaffingLevel ? "{'$filter':{  'input':'$childActivities','as':'childActivity','cond':{'$eq':['$$childActivity.rulesActivityTab.eligibleForStaffingLevel',true]} }}" : "'$childActivities'").append(",'timeCalculationActivityTab':'$_id.timeCalculationActivityTab','balanceSettingsActivityTab':'$_id.balanceSettingsActivityTab','_id':'$_id.id','name':'$_id.name','timeTypeInfo':'$_id.timeTypeInfo','allowChildActivities':'$_id.timeTypeInfo.allowChildActivities','categoryId':'$_id.categoryId','categoryName':'$_id.categoryName'}}").toString();

        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations.add(lookup("activity_category", GENERAL_ACTIVITY_TAB_CATEGORY_ID, "_id",
                CATEGORY));
        aggregationOperations.add(project(CHILD_ACTIVITY_IDS, TIME_CALCULATION_ACTIVITY_TAB, BALANCE_SETTINGS_ACTIVITY_TAB, "name").and(CATEGORY).arrayElementAt(0).as(CATEGORY).and(TIME_TYPE_INFO).arrayElementAt(0).as(TIME_TYPE_INFO));
        aggregationOperations.add(project(CHILD_ACTIVITY_IDS, TIME_CALCULATION_ACTIVITY_TAB, BALANCE_SETTINGS_ACTIVITY_TAB, "name", TIME_TYPE_INFO).and("category._id").as(CATEGORY_ID).and("category.name").as(CATEGORY_NAME));
        aggregationOperations.add(unwind(CHILD_ACTIVITY_IDS, true));
        aggregationOperations.add(lookup(ACTIVITIES, CHILD_ACTIVITY_IDS, "_id",
                CHILD_ACTIVITIES));
        aggregationOperations.add(lookup(TIME_TYPE, "childActivities.balanceSettingsActivityTab.timeTypeId", "_id",
                COMPOSITE_TIME_TYPE_INFO));
        aggregationOperations.add(project(CHILD_ACTIVITIES, TIME_CALCULATION_ACTIVITY_TAB, TIME_TYPE_INFO, BALANCE_SETTINGS_ACTIVITY_TAB, "name", CATEGORY_ID, CATEGORY_NAME).and(CHILD_ACTIVITIES).arrayElementAt(0).as(CHILD_ACTIVITIES).and(COMPOSITE_TIME_TYPE_INFO).arrayElementAt(0).as(COMPOSITE_TIME_TYPE_INFO));
        aggregationOperations.add(project(TIME_CALCULATION_ACTIVITY_TAB, COMPOSITE_TIME_TYPE_INFO, TIME_TYPE_INFO, BALANCE_SETTINGS_ACTIVITY_TAB, "name", CATEGORY_ID, CATEGORY_NAME, CHILD_ACTIVITIES));
        aggregationOperations.add(project(TIME_CALCULATION_ACTIVITY_TAB, TIME_TYPE_INFO, BALANCE_SETTINGS_ACTIVITY_TAB, "name", CATEGORY_ID, CATEGORY_NAME).and("compositeTimeTypeInfo.allowChildActivities").as("compositeActivities.allowChildActivities").
                and("childActivities.timeCalculationActivityTab").as("childActivities.timeCalculationActivityTab")
                .and("childActivities.balanceSettingsActivityTab").as("childActivities.balanceSettingsActivityTab")
                .and("childActivities.rulesActivityTab").as("childActivities.rulesActivityTab")
                .and(CHILD_ACTIVITIES_ID).as(CHILD_ACTIVITIES_ID)
                .and("childActivities.name").as("childActivities.name")
                .and("childActivities.categoryId").as("childActivities.categoryId")
                .and("childActivities.categoryName").as("childActivities.categoryName"));
        aggregationOperations.add(new CustomAggregationOperation(Document.parse(group)));
        aggregationOperations.add(new CustomAggregationOperation(Document.parse(projection)));
        return aggregationOperations;
    }

    public List<ActivityWithCompositeDTO> findAllActivityByUnitIdWithCompositeActivities(Long unitId) {
        Criteria criteria = Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false);
        Aggregation aggregation = getParentActivityAggregation(criteria);
        AggregationResults<ActivityWithCompositeDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCompositeDTO.class);
        return result.getMappedResults();
    }

    private Aggregation getParentActivityAggregation(Criteria criteria) {
        return Aggregation.newAggregation(
                    match(criteria),
                    lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                    lookup(ACTIVITIES, "_id", CHILD_ACTIVITY_IDS, "parentActivity"),
                    project("id","name", GENERAL_ACTIVITY_TAB, TIME_CALCULATION_ACTIVITY_TAB, EXPERTISES, EMPLOYMENT_TYPES, RULES_ACTIVITY_TAB, SKILL_ACTIVITY_TAB,
                            PHASE_SETTINGS_ACTIVITY_TAB,
                            BALANCE_SETTINGS_ACTIVITY_TAB,
                            UNIT_ID,
                            CHILD_ACTIVITY_IDS).and("parentActivity._id").as("parentActivityId").and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                    );
    }

    @Override
    public List<PhaseSettingsActivityTab> findActivityIdAndStatusByUnitAndAccessGroupIds(Long unitId, List<Long> accessGroupIds) {
        String group = " {  \n" +
                "      \"$group\":{  \n" +
                "         \"_id\":{  \n" +
                "            \"_id\":\"$_id\"\n" +
                "         },\n" +
                "         \"phaseTemplateValues\":{  \n" +
                "            \"$addToSet\":\"$phaseTemplateValues\"\n" +
                "         }\n" +
                "      }\n" +
                "   }";
        String project = "{  \n" +
                "      \"$project\":{  \n" +
                "         \"activityId\":\"$_id._id\",\n" +
                "         \"phaseTemplateValues\":\"$phaseTemplateValues\"\n" +
                "      }\n" +
                "   }";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                project("id").and("$phaseSettingsActivityTab.phaseTemplateValues").as("phaseTemplateValues"),
                unwind("phaseTemplateValues"),
                match(Criteria.where("phaseTemplateValues.activityShiftStatusSettings.accessGroupIds").in(accessGroupIds)),
                unwind(PHASE_TEMPLATE_VALUES_ACTIVITY_SHIFT_STATUS_SETTINGS),
                match(Criteria.where("phaseTemplateValues.activityShiftStatusSettings.accessGroupIds").in(accessGroupIds)),
                project("id").and(PHASE_TEMPLATE_VALUES_ACTIVITY_SHIFT_STATUS_SETTINGS).as(ACTIVITY_SHIFT_STATUS_SETTINGS).and("phaseTemplateValues.phaseId").as(PHASE_ID),
                group("id", PHASE_ID).addToSet(ACTIVITY_SHIFT_STATUS_SETTINGS).as(ACTIVITY_SHIFT_STATUS_SETTINGS),
                project().and("id").as("_id").and(PHASE_ID).as("phaseTemplateValues.phaseId").and(ACTIVITY_SHIFT_STATUS_SETTINGS).as(PHASE_TEMPLATE_VALUES_ACTIVITY_SHIFT_STATUS_SETTINGS),
                new CustomAggregationOperation(Document.parse(group)),
                new CustomAggregationOperation(Document.parse(project))


        );
        AggregationResults<PhaseSettingsActivityTab> results = mongoTemplate.aggregate(aggregation, Activity.class, PhaseSettingsActivityTab.class);
        return results.getMappedResults();
    }

    @Override
    public boolean unassignExpertiseFromActivitiesByExpertiesId(Long expertiseId) {
        Update update = new Update().pull(EXPERTISES, expertiseId);
        return mongoTemplate.updateMulti(new Query(), update, Activity.class).wasAcknowledged();
    }

    @Override
    public boolean unassignCompositeActivityFromActivitiesByactivityId(BigInteger activityId) {
        Update update = new Update();
        return mongoTemplate.updateMulti(new Query(), update, Activity.class).wasAcknowledged();
    }

    @Override
    public ActivityDTO findByIdAndChildActivityEligibleForStaffingLevelTrue(BigInteger activityId) {
        String project = "{'$project':{'_id':1,'childActivities':{'$filter':{  'input':'$childActivities','as':'childActivity','cond':{'$eq':['$$childActivity.rulesActivityTab.eligibleForStaffingLevel',true]} }} }}";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(activityId).and(DELETED).is(false)),
                lookup(ACTIVITIES, CHILD_ACTIVITY_IDS, "_id", CHILD_ACTIVITIES),
                unwind(CHILD_ACTIVITIES),
                match(Criteria.where("childActivities.rulesActivityTab.eligibleForStaffingLevel").is(true)),
                group("$id")
                        .addToSet(CHILD_ACTIVITIES).as(CHILD_ACTIVITIES),
                new CustomAggregationOperation(Document.parse(project)),
                project("id", CHILD_ACTIVITIES)
                        .and(CHILD_ACTIVITIES_ID).as(CHILD_ACTIVITY_IDS)
        );

        return mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class).getUniqueMappedResult();
    }

    @Override
    public List<ActivityTagDTO> findAllActivityByUnitIdAndNotPartOfTeam(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                project("name", DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1),
                match(Criteria.where("timeType.partOfTeam").is(false))
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }

    @Override
    public TimeTypeEnum findTimeTypeByActivityId(BigInteger activityId){
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("_id").is(activityId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id", TIME_TYPE1),
                project().andExclude("_id").and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1),
                project().and("timeType.secondLevelType").as("secondLevelType")
        );
        AggregationResults<TimeType> result = mongoTemplate.aggregate(aggregation, Activity.class, TimeType.class);
        return result.getMappedResults().get(0).getSecondLevelType();
    }

    @Override
    public List<ActivityDTO> findAbsenceActivityByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and("balanceSettingsActivityTab.timeType").is(TimeTypeEnum.ABSENCE)),
                lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, "_id", ACTIVITY_PRIORITY),
                project("name", DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB)
                        .and(ACTIVITY_PRIORITY).arrayElementAt(0).as(ACTIVITY_PRIORITY),
                project("name", DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB)
                        .and("activityPriority.sequence").as("activitySequence")
                );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }
}