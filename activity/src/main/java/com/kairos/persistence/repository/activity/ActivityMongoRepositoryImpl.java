package com.kairos.persistence.repository.activity;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.ActivityCategoryListDTO;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.CompositeActivityDTO;
import com.kairos.dto.activity.activity.OrganizationActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.ActivityPhaseSettings;
import com.kairos.dto.activity.activity.activity_tabs.ActivityWithCTAWTASettingsDTO;
import com.kairos.dto.activity.break_settings.BreakActivitiesDTO;
import com.kairos.dto.activity.time_type.TimeTypeAndActivityIdDTO;
import com.kairos.dto.user.staff.staff_settings.StaffActivitySettingDTO;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.UnityActivitySetting;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.activity.TimeType;
import com.kairos.persistence.model.staff_settings.StaffActivitySetting;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.wrapper.activity.ActivityTagDTO;
import com.kairos.wrapper.activity.ActivityTimeTypeWrapper;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.AppConstants.*;
import static com.kairos.enums.TimeTypeEnum.PAID_BREAK;
import static com.kairos.enums.TimeTypeEnum.UNPAID_BREAK;
import static com.kairos.enums.TimeTypes.WORKING_TYPE;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


public class ActivityMongoRepositoryImpl implements CustomActivityMongoRepository {


    public static final String CHILD_ACTIVITIES_ACTIVITY_PRIORITY_ID = "childActivities.activityPriorityId";
    public static final String CHILD_ACTIVITIES_CATEGORY_ID = "childActivities.categoryId";
    public static final String STAFF_ID = "staffId";
    public static final String ACTIVITY_IDS = "activityIds";
    public static final String ACTIVITY_ID = "activityId";
    public static final String ACTIVITY_RULES_SETTINGS = "activityRulesSettings";
    @Inject
    private MongoTemplate mongoTemplate;

    public List<ActivityCategoryListDTO> findAllActivityByOrganizationGroupWithCategoryName(Long unitId, boolean deleted) {
        List<AggregationOperation> customAgregationForCompositeActivity = new ArrayList<>();
        customAgregationForCompositeActivity.add(match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(deleted).and("activityRulesSettings.eligibleForStaffingLevel").is(true)));
        customAgregationForCompositeActivity.add(lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE_INFO));
        customAgregationForCompositeActivity.add(lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, UNDERSCORE_ID, ACTIVITY_PRIORITY));
        customAgregationForCompositeActivity.addAll(getCustomAgregationForCompositeActivityWithCategory(true,true));
        customAgregationForCompositeActivity.add(new CustomAggregationOperation("{\n" +
                "     $group : { _id : \"$activityCategory\", activities: { $push: \"$$ROOT\" } }\n" +
                "   }"));
        customAgregationForCompositeActivity.add(new CustomAggregationOperation("{$project:{\n" +
                "       \"activityCategory._id\": \"$_id.categoryId\",\n" +
                "        \"activityCategory.name\": \"$_id.categoryName\",\n" +
                "       \"activities\":1,\n" +
                "       \"_id\":0\n" +
                "       }}"));
        Aggregation aggregation = Aggregation.newAggregation(customAgregationForCompositeActivity);

        AggregationResults<ActivityCategoryListDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityCategoryListDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityTagDTO> findAllActivitiesByOrganizationType(List<Long> orgTypeIds, List<Long> orgSubTypeIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(true)
                        .and(ORGANIZATION_TYPES).in(orgTypeIds).orOperator(Criteria.where(ORGANIZATION_SUB_TYPES).in(orgSubTypeIds))
                        .and(STATE).nin("DRAFT")),
                unwind("tags", true),
                lookup("tag", "tags", UNDERSCORE_ID, TAGS_DATA),
                unwind(TAGS_DATA, true),
                group(DOLLAR_ID)
                        .first("$name").as(NAME)
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
                match(Criteria.where(UNDERSCORE_ID).is(activityId).and(DELETED).is(false))
        );
        AggregationResults<CompositeActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, CompositeActivityDTO.class);
        return result.getMappedResults();

    }

    public List<ActivityTagDTO> findAllowChildActivityByUnitIdAndDeleted(Long unitId, boolean deleted) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(deleted)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                project(NAME, DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB, ACTIVITY_PRIORITY_ID, CHILD_ACTIVITY_IDS).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).as(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID)
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
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                lookup("tag", "tags", UNDERSCORE_ID, "tags"),
                project(NAME, DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB, "tags", ACTIVITY_PRIORITY_ID,TRANSLATIONS).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).as(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).and(TIME_CALCULATION_ACTIVITY_TAB).as(TIME_CALCULATION_ACTIVITY_TAB)
                        .and(TIME_CALCULATION_ACTIVITY_TAB_METHOD_FOR_CALCULATING_TIME).as("methodForCalculatingTime")
                        .and("timeType.activityCanBeCopiedForOrganizationHierarchy").arrayElementAt(0).as("activityCanBeCopiedForOrganizationHierarchy")
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(APPLICABLE_FOR_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_SICKNESS_SETTING).arrayElementAt(0).as(SICKNESS_SETTING)
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityTagDTO> findAllActivityByCountry(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(true)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                lookup("tag", "tags", UNDERSCORE_ID, "tags"),
                project(NAME, STATE, DESCRIPTION, COUNTRY_ID, IS_PARENT_ACTIVITY, GENERAL_ACTIVITY_TAB,TRANSLATIONS, "tags", ACTIVITY_PRIORITY_ID, CHILD_ACTIVITY_IDS).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).as(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID)
                        .and(TIME_CALCULATION_ACTIVITY_TAB_METHOD_FOR_CALCULATING_TIME).as("methodForCalculatingTime")

                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(APPLICABLE_FOR_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_SICKNESS_SETTING).arrayElementAt(0).as(SICKNESS_SETTING)
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityTagDTO> findAllowChildActivityByCountryId(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(true)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                project(NAME, COUNTRY_ID, IS_PARENT_ACTIVITY, GENERAL_ACTIVITY_TAB, CHILD_ACTIVITY_IDS).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).as(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(APPLICABLE_FOR_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_SICKNESS_SETTING).arrayElementAt(0).as(SICKNESS_SETTING),
                match(Criteria.where(APPLICABLE_FOR_CHILD_ACTIVITIES).is(true))
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }

    public ActivityWithCompositeDTO findActivityByActivityId(BigInteger activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNDERSCORE_ID).is(activityId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                lookup(ACTIVITIES, UNDERSCORE_ID, CHILD_ACTIVITY_IDS, "parentActivity"),
                project(NAME, STATE, DESCRIPTION, COUNTRY_ID, IS_PARENT_ACTIVITY, GENERAL_ACTIVITY_TAB, ACTIVITY_PRIORITY_ID, CHILD_ACTIVITY_IDS).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).as(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID)
                        .and("parentActivity._id").as(PARENT_ACTIVITY_ID)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(APPLICABLE_FOR_CHILD_ACTIVITIES)
                        .and(TIME_TYPE_SICKNESS_SETTING).arrayElementAt(0).as(SICKNESS_SETTING)
        );
        AggregationResults<ActivityWithCompositeDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCompositeDTO.class);
        return isCollectionNotEmpty(result.getMappedResults()) ? result.getMappedResults().get(0) : null;
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByCountry(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(true).and(STATE).is(ActivityStateEnum.PUBLISHED)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                project(DOLLAR_ID, NAME, DESCRIPTION, CTA_AND_WTA_SETTINGS_ACTIVITY_TAB, GENERAL_ACTIVITY_TAB_CATEGORY_ID)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1),
                match(Criteria.where(TIME_TYPE_TIME_TYPES).is(TimeTypes.WORKING_TYPE))
        );
        AggregationResults<ActivityWithCTAWTASettingsDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCTAWTASettingsDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityWithCTAWTASettingsDTO> findAllActivityWithCtaWtaSettingByUnit(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                project(DOLLAR_ID, NAME, DESCRIPTION, CTA_AND_WTA_SETTINGS_ACTIVITY_TAB, GENERAL_ACTIVITY_TAB_CATEGORY_ID).and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1),
                match(Criteria.where(TIME_TYPE_TIME_TYPES).is(TimeTypes.WORKING_TYPE))
        );
        AggregationResults<ActivityWithCTAWTASettingsDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCTAWTASettingsDTO.class);
        return result.getMappedResults();
    }

    public List<OrganizationActivityDTO> findAllActivityOfUnitsByParentActivity(List<BigInteger> parentActivityIds, List<Long> unitIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(IS_PARENT_ACTIVITY).is(false).and(UNIT_ID).in(unitIds).and(PARENT_ID).in(parentActivityIds)),
                project(DOLLAR_ID, UNIT_ID, PARENT_ID)
        );
        AggregationResults<OrganizationActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, OrganizationActivityDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityTagDTO> findAllActivityByParentOrganization(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                project(NAME, GENERAL_ACTIVITY_TAB, BALANCE_SETTINGS_ACTIVITY_TAB));

        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();

    }

    public List<ActivityWithCompositeDTO> findAllActivityByUnitIdWithCompositeActivities(List<BigInteger> activityIds) {
        Aggregation aggregation = getParentActivityAggregation(Criteria.where(UNDERSCORE_ID).in(activityIds).and(DELETED).is(false));
        AggregationResults<ActivityWithCompositeDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWithCompositeDTO.class);
        return result.getMappedResults();
    }


    public List<ActivityDTO> getAllActivityWithTimeType(List<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                //"unitId").is(unitId).and(
                match(Criteria.where(DELETED).is(false).and(UNDERSCORE_ID).in(activityIds)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE_INFO)
                , project(UNIT_ID)
                        .andInclude(DELETED)
                        .andInclude(NAME)
                        .andInclude(EXPERTISES)
                        .andInclude(SKILL_ACTIVITY_TAB)
                        .and(TIME_TYPE_INFO).arrayElementAt(0).as(TIME_TYPE1));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllActivityByUnitId(Long unitId, boolean deleted) {
        List<AggregationOperation> customAgregationForCompositeActivity = new ArrayList<>();
        customAgregationForCompositeActivity.add(match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(deleted)));
        customAgregationForCompositeActivity.add(lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE_INFO));
        customAgregationForCompositeActivity.add(lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, UNDERSCORE_ID, ACTIVITY_PRIORITY));
        customAgregationForCompositeActivity.addAll(getCustomAgregationForCompositeActivityWithCategory(false,false));
        customAgregationForCompositeActivity.add(match(Criteria.where("timeTypeInfo.partOfTeam").is(true)));
        Aggregation aggregation = Aggregation.newAggregation(customAgregationForCompositeActivity);
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List[] findAllNonProductiveTypeActivityIdsAndAssignedStaffIds(Collection<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ID).in(activityIds).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE_INFO),
                match(Criteria.where("timeTypeInfo.partOfTeam").is(false)),
                group().push("id").as(ACTIVITY_IDS),
                lookup("staffActivitySetting", ACTIVITY_IDS, ACTIVITY_ID,"staff"),
                unwind("staff",true),
                group(ACTIVITY_IDS).push("staff.staffId").as("staffIds")
        );
        List<Map> result = mongoTemplate.aggregate(aggregation, Activity.class, Map.class).getMappedResults();
        List<BigInteger> nonProductiveTypeActivityIds = null;
        List<Long> staffIds = null;
        if(isCollectionNotEmpty(result)){
            Map<String,List> stringListMap = result.get(0);
             nonProductiveTypeActivityIds = (List<BigInteger>)ObjectMapperUtils.copyCollectionPropertiesByMapper(stringListMap.get("_id"),BigInteger.class);
            staffIds = stringListMap.get("staffIds");
        }
        return new List[]{nonProductiveTypeActivityIds,staffIds};
    }

    //Ignorecase

    public Activity getActivityByNameAndUnitId(Long unitId, String name) {
        Query query = new Query(Criteria.where(DELETED).is(false).and(UNIT_ID).is(unitId).and(NAME).regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)));
        return mongoTemplate.findOne(query, Activity.class);
    }


    public List<ActivityDTO> findAllActivitiesWithBalanceSettings(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID,
                        BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO),
                project(BALANCE_SETTINGS_ACTIVITY_TAB, NAME, EXPERTISES)
                        .and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO).arrayElementAt(0).as(TIME_TYPE1).andInclude(TIME_TYPE_INFO_LABEL)

        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllActivitiesWithTimeTypes(long countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID,
                        BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO),
                match(Criteria.where("activityBalanceSettings.timeTypeInfo.timeTypes").is(WORKING_TYPE)),
                project(BALANCE_SETTINGS_ACTIVITY_TAB, NAME).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO).arrayElementAt(0).as(TIME_TYPE1).andInclude(TIME_TYPE_INFO_LABEL)

        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllActivitiesWithTimeTypesByUnit(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID,
                        BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO),
                match(Criteria.where("activityBalanceSettings.timeTypeInfo.timeTypes").is(WORKING_TYPE)),
                project(BALANCE_SETTINGS_ACTIVITY_TAB, NAME).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_INFO).arrayElementAt(0).as(TIME_TYPE1).andInclude(TIME_TYPE_INFO_LABEL)

        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }


    public Activity findByNameExcludingCurrentInCountryAndDate(String name, BigInteger activityId, Long countryId, LocalDate startDate, LocalDate endDate) {
        Criteria criteria = Criteria.where(ID).ne(activityId).and(NAME).regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false).and(COUNTRY_ID).is(countryId);
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
        Criteria criteria = Criteria.where(ID).ne(activityId).and(NAME).regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false).and(UNIT_ID).is(unitId);
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
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID,
                        BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE),
                match(Criteria.where(DELETED).is(false).and("activityBalanceSettings.timeType.timeTypes").is("NON_WORKING_TYPE")),
                // match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("activityBalanceSettings.timeType.timeTypes").is("NON_WORKING_TYPE")),
                //group("unitId").addToSet("id").as("ids"),
                project(ID)

        );
        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, Activity.class, Map.class);
        List<Map> activityIdMap = result.getMappedResults();
        Set<BigInteger> activityIds = new HashSet<>();
        for (Map activityMap : activityIdMap) {
            activityIds.add(new BigInteger(activityMap.get(UNDERSCORE_ID).toString()));
        }
        return activityIds;
    }

    public Activity findByNameIgnoreCaseAndCountryIdAndByDate(String name, Long countryId, LocalDate startDate, LocalDate endDate) {
        Criteria criteria = Criteria.where(NAME).regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false).and(COUNTRY_ID).is(countryId);
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
        Criteria criteria = Criteria.where(NAME).regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(DELETED).is(false).and(UNIT_ID).is(unitId);
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
                match(Criteria.where(ID).is(activityId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID,
                        TIME_TYPE1),
                getProject()
        );
        AggregationResults<ActivityWrapper> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWrapper.class);
        return (result.getMappedResults().isEmpty()) ? null : result.getMappedResults().get(0);
    }

    private ProjectionOperation getProject() {
        return project().and(ID).as(AppConstants.ACTIVITY_ID).and(NAME).as(ACTIVITY_NAME).and(DESCRIPTION).as("activity.description")
                .and(COUNTRY_ID).as(ACTIVITY_COUNTRY_ID).and(EXPERTISES).as(ACTIVITY_EXPERTISES)
                .and(ID).as(AppConstants.ACTIVITY_ID)
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
                .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE_INFO);
    }

    @Override
    public List<ActivityWrapper> findActivitiesAndTimeTypeByActivityId(Collection<BigInteger> activityIds) {
        return getActivityWrappersByCriteria(Criteria.where("id").in(activityIds).and(DELETED).is(false));
    }

    @Override
    public List<ActivityWrapper> findParentActivitiesAndTimeTypeByActivityId(Collection<BigInteger> activityIds) {
        return getParentActivityWrappersByCriteria(Criteria.where("id").in(activityIds).and(DELETED).is(false));
    }

    private List<ActivityWrapper> getParentActivityWrappersByCriteria(Criteria criteria) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        TIME_TYPE1),
                lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, UNDERSCORE_ID,
                        ACTIVITY_PRIORITY),
                getProjectForParentActivityWrapper()
        );
        AggregationResults<ActivityWrapper> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWrapper.class);
        return result.getMappedResults();
    }

    private ProjectionOperation getProjectForParentActivityWrapper() {
        return project().and(ID).as(AppConstants.ACTIVITY_ID).and(NAME).as(ACTIVITY_NAME).and(CHILD_ACTIVITY_IDS).as("activity.childActivityIds")
                .and(ID).as(AppConstants.ACTIVITY_ID).and(ACTIVITY_PRIORITY_ID).as("activity.activityPriorityId").and(UNIT_ID).as(ACTIVITY_UNIT_ID)
                .and(BALANCE_SETTINGS_ACTIVITY_TAB).as(ACTIVITY_BALANCE_SETTINGS_ACTIVITY_TAB).and(RULES_ACTIVITY_TAB).as(ACTIVITY_RULES_ACTIVITY_TAB)
                .and(TIME_CALCULATION_ACTIVITY_TAB).as(ACTIVITY_TIME_CALCULATION_ACTIVITY_TAB).and(BONUS_ACTIVITY_TAB).as(ACTIVITY_BONUS_ACTIVITY_TAB)
                .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1).and(TIME_TYPE_TIME_TYPES).as(TIME_TYPE1).and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE_INFO)
                .and(ACTIVITY_PRIORITY).arrayElementAt(0).as(ACTIVITY_PRIORITY);
    }

    @Override
    public List<Activity> findActivitiesSickSettingByActivityIds(Collection<BigInteger> activityIds){
        Query query = new Query(Criteria.where("id").in(activityIds).and(DELETED).is(false).and("activityRulesSettings.sicknessSettingValid").is(true));
        query.fields().include(ACTIVITY_RULES_SETTINGS);
        return mongoTemplate.find(query,Activity.class);
    }

    private List<ActivityWrapper> getActivityWrappersByCriteria(Criteria criteria) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        TIME_TYPE1),
                lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, UNDERSCORE_ID,
                        ACTIVITY_PRIORITY),
                getProjectForActivityWrapper()
        );
        AggregationResults<ActivityWrapper> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityWrapper.class);
        return result.getMappedResults();
    }



    private ProjectionOperation getProjectForActivityWrapper() {
        return project().and(ID).as(AppConstants.ACTIVITY_ID).and(NAME).as(ACTIVITY_NAME).and(DESCRIPTION).as("activity.description")
                .and(COUNTRY_ID).as(ACTIVITY_COUNTRY_ID).and(EXPERTISES).as(ACTIVITY_EXPERTISES).and(CHILD_ACTIVITY_IDS).as("activity.childActivityIds")
                .and(ID).as(AppConstants.ACTIVITY_ID).and(ACTIVITY_PRIORITY_ID).as("activity.activityPriorityId")
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
                .and(ACTIVITY_PRIORITY).arrayElementAt(0).as(ACTIVITY_PRIORITY);
    }
    @Override
    public List<ActivityWrapper> getAllActivityWrapperBySecondLevelTimeType(TimeTypeEnum secondLevelTimeType,Long unitId){
        return getActivityWrappersByCriteria(Criteria.where(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE).is(secondLevelTimeType).and(UNIT_ID).is(unitId).and(DELETED).is(false));
    }

    public List<TimeTypeAndActivityIdDTO> findAllTimeTypeByActivityIds(Set<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ID).in(activityIds).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID,
                        TIME_TYPE1), project().and(ID).as(ACTIVITY_ID)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1).and(TIME_TYPE_TIME_TYPES).as(TIME_TYPE1));
        AggregationResults<TimeTypeAndActivityIdDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, TimeTypeAndActivityIdDTO.class);
        return result.getMappedResults();
    }

    public StaffActivitySettingDTO findStaffPersonalizedSettings(Long unitId, BigInteger activityId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(UNDERSCORE_ID).is(activityId)),
                project("activityRulesSettings.shortestTime", "activityRulesSettings.longestTime", "activityRulesSettings.earliestStartTime", "activityRulesSettings.latestStartTime", "activityRulesSettings.maximumEndTime", "activityOptaPlannerSetting.maxThisActivityPerShift", "activityOptaPlannerSetting.minLength", "activityOptaPlannerSetting.eligibleForMove", "activityTimeCalculationSettings.defaultStartTime").and("activityTimeCalculationSettings.dayTypes").as("dayTypeIds")
        );
        AggregationResults<StaffActivitySettingDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, StaffActivitySettingDTO.class);
        return (result.getMappedResults().isEmpty()) ? null : result.getMappedResults().get(0);
    }

    public List<BreakActivitiesDTO> getAllActivitiesGroupedByTimeType(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                group("$timeType.timeTypes").push(new BasicDBObject(UNDERSCORE_ID, "$_id").append(NAME, "$name")).as(ACTIVITIES),
                project(ACTIVITIES).and(UNDERSCORE_ID).as(TIME_TYPE1));
        AggregationResults<BreakActivitiesDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, BreakActivitiesDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityDTO> findAllByTimeTypeIdAndUnitId(Set<BigInteger> timeTypeIds, Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).in(timeTypeIds).and("activityRulesSettings.allowedAutoAbsence").is(true).and(DELETED).is(false).and(UNIT_ID).is(unitId)),
                project().and(ID).as(ID).and(NAME).as(NAME).and(TIME_CALCULATION_ACTIVITY_TAB).as(TIME_CALCULATION_ACTIVITY_TAB));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<ActivityWrapper> findActivitiesAndTimeTypeByParentIdsAndUnitId(List<BigInteger> activityIds, Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(DELETED).is(false).and(UNIT_ID).is(unitId).orOperator(Criteria.where(COUNTRY_PARENT_ID).in(activityIds), Criteria.where(ID).in(activityIds))),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                project().and(ID).as(AppConstants.ACTIVITY_ID).and(NAME).as(ACTIVITY_NAME)
                        .and(COUNTRY_ID).as(ACTIVITY_COUNTRY_ID).and(EXPERTISES).as(ACTIVITY_EXPERTISES)
                        .and(PARENT_ID).as(ACTIVITY_PARENT_ID)
                        .and(COUNTRY_PARENT_ID).as("activity.countryParentId")
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
        Criteria criteria =isNull(countryId)?Criteria.where(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).in(timeTypeIds):Criteria.where(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).in(timeTypeIds).and(DELETED).is(false).and(COUNTRY_ID).is(countryId);
        Aggregation aggregation = Aggregation.newAggregation(match(criteria)
                , project().and(ID).as(ID).and(NAME).as(NAME).and(ACTIVITY_PRIORITY_ID));
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();

    }

    @Override
    public List<Activity> findAllActivitiesByOrganizationTypeOrSubTypeOrBreakTypes(Long orgTypeIds, List<Long> orgSubTypeIds) {
        List<TimeTypeEnum> breakTypes = new ArrayList<>();
        breakTypes.add(PAID_BREAK);
        breakTypes.add(UNPAID_BREAK);
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where(IS_PARENT_ACTIVITY).is(true).and(DELETED).is(false))
                , lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                match(Criteria.where(STATE).nin("DRAFT").orOperator(Criteria.where(ORGANIZATION_SUB_TYPES).in(orgSubTypeIds), Criteria.where("timeType.secondLevelType").in(breakTypes))));
        AggregationResults<Activity> result = mongoTemplate.aggregate(aggregation, Activity.class, Activity.class);
        return result.getMappedResults();
    }

    @Override
    public List<Activity> findAllBreakActivitiesByOrganizationId(Long unitId) {
        List<TimeTypeEnum> breakTypes = new ArrayList<>();
        breakTypes.add(PAID_BREAK);
        breakTypes.add(UNPAID_BREAK);
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where(DELETED).is(false).and(UNIT_ID).is(unitId).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID).in(breakTypes)),
                project("id")
                );
        AggregationResults<Activity> result = mongoTemplate.aggregate(aggregation, Activity.class, Activity.class);
        return result.getMappedResults();
    }

    @Override
    public List<ActivityWrapper> findActivityAndTimeTypeByActivityIdsAndNotFullDayAndFullWeek(Set<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ID).in(activityIds).and(DELETED).is(false).and(TIME_CALCULATION_ACTIVITY_TAB_METHOD_FOR_CALCULATING_TIME).nin(CommonConstants.FULL_DAY_CALCULATION, CommonConstants.FULL_WEEK)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID,
                        TIME_TYPE1),
                project().and(ID).as(AppConstants.ACTIVITY_ID).and(NAME).as(ACTIVITY_NAME)
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
                match(Criteria.where(ID).in(activityIds).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                project(NAME, ID, CHILD_ACTIVITY_IDS)
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
        Query query = new Query(Criteria.where(UNDERSCORE_ID).ne(activityId).and(CHILD_ACTIVITY_IDS).in(allowedActivityIds).and(DELETED).is(false).and(STATE).is(ActivityStateEnum.PUBLISHED));
        return mongoTemplate.find(query, Activity.class);
    }

    private List<AggregationOperation> getCustomAgregationForCompositeActivityWithCategory(boolean isChildActivityEligibleForStaffingLevel,boolean groupByCategory) {
        String group = getGroup();
        String projection = getProjection(isChildActivityEligibleForStaffingLevel,groupByCategory);

        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations.add(lookup("activity_category", GENERAL_ACTIVITY_TAB_CATEGORY_ID, UNDERSCORE_ID,
                CATEGORY));
        aggregationOperations.add(project(CHILD_ACTIVITY_IDS, TIME_CALCULATION_ACTIVITY_TAB,TRANSLATIONS, BALANCE_SETTINGS_ACTIVITY_TAB, NAME, ACTIVITY_PRIORITY_ID).and(CATEGORY).arrayElementAt(0).as(CATEGORY).and(TIME_TYPE_INFO).arrayElementAt(0).as(TIME_TYPE_INFO).and(ACTIVITY_PRIORITY).arrayElementAt(0).as(ACTIVITY_PRIORITY));
        aggregationOperations.add(project(CHILD_ACTIVITY_IDS, TIME_CALCULATION_ACTIVITY_TAB,TRANSLATIONS, BALANCE_SETTINGS_ACTIVITY_TAB, NAME, ACTIVITY_PRIORITY_ID, TIME_TYPE_INFO,ACTIVITY_PRIORITY).and("category._id").as(CATEGORY_ID).and("category.name").as(CATEGORY_NAME));
        aggregationOperations.add(unwind(CHILD_ACTIVITY_IDS, true));
        aggregationOperations.add(lookup(ACTIVITIES, CHILD_ACTIVITY_IDS, UNDERSCORE_ID,
                CHILD_ACTIVITIES));
        aggregationOperations.add(lookup(TIME_TYPE, "childActivities.activityBalanceSettings.timeTypeId", UNDERSCORE_ID,
                COMPOSITE_TIME_TYPE_INFO));
        aggregationOperations.add(lookup(ACTIVITY_PRIORITY, CHILD_ACTIVITIES_ACTIVITY_PRIORITY_ID, UNDERSCORE_ID, CHILD_ACTIVITY_PRIORITY));

        aggregationOperations.add(project(CHILD_ACTIVITIES, TIME_CALCULATION_ACTIVITY_TAB,TRANSLATIONS, TIME_TYPE_INFO, ACTIVITY_PRIORITY, BALANCE_SETTINGS_ACTIVITY_TAB, NAME, ACTIVITY_PRIORITY_ID, CATEGORY_ID, CATEGORY_NAME).and(CHILD_ACTIVITIES).arrayElementAt(0).as(CHILD_ACTIVITIES).and(COMPOSITE_TIME_TYPE_INFO).arrayElementAt(0).as(COMPOSITE_TIME_TYPE_INFO).and(CHILD_ACTIVITY_PRIORITY).arrayElementAt(0).as(CHILD_ACTIVITY_PRIORITY));
        aggregationOperations.add(project(TIME_CALCULATION_ACTIVITY_TAB,TRANSLATIONS, COMPOSITE_TIME_TYPE_INFO, TIME_TYPE_INFO, ACTIVITY_PRIORITY, CHILD_ACTIVITY_PRIORITY, BALANCE_SETTINGS_ACTIVITY_TAB, NAME, ACTIVITY_PRIORITY_ID, CATEGORY_ID, CATEGORY_NAME, CHILD_ACTIVITIES));
        aggregationOperations.add(project(TIME_CALCULATION_ACTIVITY_TAB,TRANSLATIONS, TIME_TYPE_INFO, ACTIVITY_PRIORITY, BALANCE_SETTINGS_ACTIVITY_TAB, NAME, ACTIVITY_PRIORITY_ID, CATEGORY_ID, CATEGORY_NAME).and("compositeTimeTypeInfo.allowChildActivities").as("compositeActivities.allowChildActivities")
                .and("childActivities.activityTimeCalculationSettings").as("childActivities.activityTimeCalculationSettings")
                .and("childActivities.activityBalanceSettings").as("childActivities.activityBalanceSettings")
                .and("childActivities.activityRulesSettings").as("childActivities.activityRulesSettings")
                .and(CHILD_ACTIVITIES_ID).as(CHILD_ACTIVITIES_ID)
                .and("childActivities.name").as("childActivities.name")
                .and(CHILD_ACTIVITIES_ACTIVITY_PRIORITY_ID).as(CHILD_ACTIVITIES_ACTIVITY_PRIORITY_ID)
                .and(CHILD_ACTIVITY_PRIORITY).as("childActivities.activityPriority")
                .and(CHILD_ACTIVITIES_CATEGORY_ID).as(CHILD_ACTIVITIES_CATEGORY_ID)
                .and(CHILD_ACTIVITIES_CATEGORY_ID).as(CHILD_ACTIVITIES_CATEGORY_ID)
                .and("childActivities.translations").as("childActivities.translations"));
        aggregationOperations.add(new CustomAggregationOperation(Document.parse(group)));
        aggregationOperations.add(new CustomAggregationOperation(Document.parse(projection)));
        return aggregationOperations;
    }

    private String getProjection(boolean isChildActivityEligibleForStaffingLevel, boolean groupByCategory) {
        if(groupByCategory){
            return new StringBuffer("{'$project':{'childActivities':").append(isChildActivityEligibleForStaffingLevel ? "{'$filter':{  'input':'$childActivities','as':'childActivity','cond':{'$eq':['$$childActivity.activityRulesSettings.eligibleForStaffingLevel',true]} }}" : "'$childActivities'").append(",'activityTimeCalculationSettings':'$_id.activityTimeCalculationSettings','activityBalanceSettings':'$_id.activityBalanceSettings','_id':'$_id.id','name':'$_id.name','activityPriorityId':'$_id.activityPriorityId','activityPriority':'$_id.activityPriority','timeTypeInfo':'$_id.timeTypeInfo','allowChildActivities':'$_id.timeTypeInfo.allowChildActivities','activityCategory.categoryId':'$_id.categoryId','activityCategory.categoryName':'$_id.categoryName','translations':'$_id.translations'}}").toString();
        }
        return new StringBuffer("{'$project':{'childActivities':").append(isChildActivityEligibleForStaffingLevel ? "{'$filter':{  'input':'$childActivities','as':'childActivity','cond':{'$eq':['$$childActivity.activityRulesSettings.eligibleForStaffingLevel',true]} }}" : "'$childActivities'").append(",'activityTimeCalculationSettings':'$_id.activityTimeCalculationSettings','activityBalanceSettings':'$_id.activityBalanceSettings','_id':'$_id.id','name':'$_id.name','activityPriorityId':'$_id.activityPriorityId','activityPriority':'$_id.activityPriority','timeTypeInfo':'$_id.timeTypeInfo','allowChildActivities':'$_id.timeTypeInfo.allowChildActivities','categoryId':'$_id.categoryId','categoryName':'$_id.categoryName','translations':'$_id.translations'}}").toString();
    }

    private String getGroup() {
        return "{  \n" +
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
                    lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                    lookup(ACTIVITIES, UNDERSCORE_ID, CHILD_ACTIVITY_IDS, "parentActivity"),
                    lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, UNDERSCORE_ID,
                        ACTIVITY_PRIORITY),
                    project(ID, NAME, GENERAL_ACTIVITY_TAB, TIME_CALCULATION_ACTIVITY_TAB, EXPERTISES, EMPLOYMENT_TYPES, RULES_ACTIVITY_TAB, SKILL_ACTIVITY_TAB,
                            PHASE_SETTINGS_ACTIVITY_TAB,
                            BALANCE_SETTINGS_ACTIVITY_TAB,
                            UNIT_ID,
                            CHILD_ACTIVITY_IDS).and("parentActivity._id").as(PARENT_ACTIVITY_ID).and(TIME_TYPE_ALLOW_CHILD_ACTIVITIES).arrayElementAt(0).as(ALLOW_CHILD_ACTIVITIES).and("activityPriority.name").arrayElementAt(0).as("activityPriorityName")
                    );
    }

    @Override
    public List<ActivityPhaseSettings> findActivityIdAndStatusByUnitAndAccessGroupIds(Long unitId, List<Long> accessGroupIds) {
        String group = getGroupString();
        String project = "{  \n" +
                "      \"$project\":{  \n" +
                "         \"activityId\":\"$_id._id\",\n" +
                "         \"phaseTemplateValues\":\"$phaseTemplateValues\"\n" +
                "      }\n" +
                "   }";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                project(ID).and("$activityPhaseSettings.phaseTemplateValues").as("phaseTemplateValues"),
                unwind("phaseTemplateValues"),
                match(Criteria.where("phaseTemplateValues.activityShiftStatusSettings.accessGroupIds").in(accessGroupIds)),
                unwind(PHASE_TEMPLATE_VALUES_ACTIVITY_SHIFT_STATUS_SETTINGS),
                match(Criteria.where("phaseTemplateValues.activityShiftStatusSettings.accessGroupIds").in(accessGroupIds)),
                project(ID).and(PHASE_TEMPLATE_VALUES_ACTIVITY_SHIFT_STATUS_SETTINGS).as(ACTIVITY_SHIFT_STATUS_SETTINGS).and("phaseTemplateValues.phaseId").as(PHASE_ID),
                group(ID, PHASE_ID).addToSet(ACTIVITY_SHIFT_STATUS_SETTINGS).as(ACTIVITY_SHIFT_STATUS_SETTINGS),
                project().and(ID).as(UNDERSCORE_ID).and(PHASE_ID).as("phaseTemplateValues.phaseId").and(ACTIVITY_SHIFT_STATUS_SETTINGS).as(PHASE_TEMPLATE_VALUES_ACTIVITY_SHIFT_STATUS_SETTINGS),
                new CustomAggregationOperation(Document.parse(group)),
                new CustomAggregationOperation(Document.parse(project))


        );
        AggregationResults<ActivityPhaseSettings> results = mongoTemplate.aggregate(aggregation, Activity.class, ActivityPhaseSettings.class);
        return results.getMappedResults();
    }

    private String getGroupString() {
        return " {  \n" +
                    "      \"$group\":{  \n" +
                    "         \"_id\":{  \n" +
                    "            \"_id\":\"$_id\"\n" +
                    "         },\n" +
                    "         \"phaseTemplateValues\":{  \n" +
                    "            \"$addToSet\":\"$phaseTemplateValues\"\n" +
                    "         }\n" +
                    "      }\n" +
                    "   }";
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
        String project = "{'$project':{'_id':1,'childActivities':{'$filter':{  'input':'$childActivities','as':'childActivity','cond':{'$eq':['$$childActivity.activityRulesSettings.eligibleForStaffingLevel',true]} }} }}";
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNDERSCORE_ID).is(activityId).and(DELETED).is(false)),
                lookup(ACTIVITIES, CHILD_ACTIVITY_IDS, UNDERSCORE_ID, CHILD_ACTIVITIES),
                unwind(CHILD_ACTIVITIES),
                match(Criteria.where("childActivities.activityRulesSettings.eligibleForStaffingLevel").is(true)),
                group(DOLLAR_ID)
                        .addToSet(CHILD_ACTIVITIES).as(CHILD_ACTIVITIES),
                new CustomAggregationOperation(Document.parse(project)),
                project(ID, CHILD_ACTIVITIES)
                        .and(CHILD_ACTIVITIES_ID).as(CHILD_ACTIVITY_IDS)
        );

        return mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class).getUniqueMappedResult();
    }

    @Override
    public List<ActivityTagDTO> findAllActivityByUnitIdAndNotPartOfTeam(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                project(NAME, DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB,TRANSLATIONS).and(TIME_CALCULATION_ACTIVITY_TAB).as(TIME_CALCULATION_ACTIVITY_TAB)
                        .and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1),
                match(Criteria.where("timeType.partOfTeam").is(false))
        );
        AggregationResults<ActivityTagDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityTagDTO.class);
        return result.getMappedResults();
    }

    @Override
    public TimeTypeEnum findTimeTypeByActivityId(BigInteger activityId){
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNDERSCORE_ID).is(activityId).and(DELETED).is(false)),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                project().andExclude(UNDERSCORE_ID).and(TIME_TYPE1).arrayElementAt(0).as(TIME_TYPE1),
                project().and("timeType.secondLevelType").as("secondLevelType")
        );
        AggregationResults<TimeType> result = mongoTemplate.aggregate(aggregation, Activity.class, TimeType.class);
        return result.getMappedResults().get(0).getSecondLevelType();
    }

    @Override
    public List<ActivityDTO> findAbsenceActivityByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE).is(TimeTypeEnum.ABSENCE)),
                lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, UNDERSCORE_ID, ACTIVITY_PRIORITY),
                project(NAME, DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB)
                        .and(ACTIVITY_PRIORITY).arrayElementAt(0).as(ACTIVITY_PRIORITY),
                project(NAME, DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB)
                        .and(ACTIVITY_PRIORITY_SEQUENCE).as(ACTIVITY_SEQUENCE)
                );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<ActivityDTO> getActivityRankWithRankByUnitId(Long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, UNDERSCORE_ID, ACTIVITY_PRIORITY),
                project(NAME, DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB)
                        .and(ACTIVITY_PRIORITY).arrayElementAt(0).as(ACTIVITY_PRIORITY),
                project()
                        .and(ACTIVITY_PRIORITY_SEQUENCE).as(ACTIVITY_SEQUENCE)
        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<ActivityDTO> findActivitiesByUnitId(Long unitId,Collection<BigInteger> activityIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(UNDERSCORE_ID).in(activityIds)),
                lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, UNDERSCORE_ID, ACTIVITY_PRIORITY),
                project(NAME, DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB)
                        .and(ACTIVITY_PRIORITY).arrayElementAt(0).as(ACTIVITY_PRIORITY),
                project(NAME, DESCRIPTION, UNIT_ID, RULES_ACTIVITY_TAB, PARENT_ID, GENERAL_ACTIVITY_TAB)
                        .and(ACTIVITY_PRIORITY_SEQUENCE).as(ACTIVITY_SEQUENCE)
        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    public List<ActivityTimeTypeWrapper> getActivityPath(final String activityId) {
        Document groupDocument = Document.parse("{\n" +
                "    \t$group: { _id : \"$_id\" , timeTypeHierarchyList: { $push: \"$patharray\" } }\n" +
                "      }");
        CustomAggregationOperation customGroupAggregationOperation = new CustomAggregationOperation(groupDocument);
        Document projectionDocument = Document.parse("{\n" +
                "    \t$project: {\"_id\":\"$_id\",\"name\":\"$_id.name\",\"timeTypeHierarchyList\":1}\n" +
                "      }");
        CustomAggregationOperation customProjectAggregationOperation = new CustomAggregationOperation(projectionDocument);

        Document pathArrayProject = Document.parse("{\n" +
                "$project : { \"_id\":1,\"name\":1,\"depthField\":1,\"patharray._id\":1,\"patharray.label\":1,\"patharray.upperLevelTimeTypeId\":1,\"patharray.timeTypes\":1 }\n" +
                "}");
        CustomAggregationOperation pathArrayProjection = new CustomAggregationOperation(pathArrayProject);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNDERSCORE_ID).is(activityId)),
                graphLookup(TIME_TYPE).
                        startWith("$activityBalanceSettings.timeTypeId")
                        .connectFrom("upperLevelTimeTypeId")
                        .connectTo("_id")
                        .maxDepth(3)
                        .depthField("numofchild")
                        .as("patharray"),
                unwind("$patharray"),
                sort(Sort.Direction.ASC, "patharray._id"),
                pathArrayProjection,
                customGroupAggregationOperation,
                customProjectAggregationOperation

        ).withOptions(new AggregationOptions(true, false, null, null));

        return mongoTemplate.aggregate(aggregation, Activity.class, ActivityTimeTypeWrapper.class).getMappedResults();
    }

    @Override
    public List<ActivityDTO> getActivityDetailsWithRankByUnitId(Long unitId){
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false)),
                lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, UNDERSCORE_ID, ACTIVITY_PRIORITY),
                project(NAME, GENERAL_ACTIVITY_TAB,TRANSLATIONS)
                        .and(ACTIVITY_PRIORITY).arrayElementAt(0).as(ACTIVITY_PRIORITY),
                project(NAME, GENERAL_ACTIVITY_TAB,TRANSLATIONS)
                        .and(ACTIVITY_PRIORITY_SEQUENCE).as(ACTIVITY_SEQUENCE)
        );
        AggregationResults<ActivityDTO> result = mongoTemplate.aggregate(aggregation, Activity.class, ActivityDTO.class);
        return result.getMappedResults();
    }

    @Override
    public  Set<BigInteger> findAllShowOnCallAndStandByActivitiesByUnitId(Long unitId, UnityActivitySetting unityActivitySetting){
        Criteria criteriaDefinition = Criteria.where(UNIT_ID).is(unitId).and(DELETED).is(false).and(TIME_TYPE1+".unityActivitySetting").is(unityActivitySetting);
        Aggregation aggregation = Aggregation.newAggregation(
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, UNDERSCORE_ID, TIME_TYPE1),
                unwind(TIME_TYPE1),
                match(criteriaDefinition),
                project(ID,NAME)
        );
        return mongoTemplate.aggregate(aggregation,Activity.class,Activity.class).getMappedResults().stream().map(activity -> activity.getId()).collect(Collectors.toSet());
    }

    @Override
    public List<ActivityWithCompositeDTO> findAllActivityByIdsAndIncludeChildActivitiesWithMostUsedCountOfActivity(Collection<BigInteger> activityIds,Long unitId, Long staffId, boolean isActivityType) {
        String activityIdString = getBigIntegerString(activityIds.iterator());
        AggregationOperation[] aggregations = new AggregationOperation[10];
        int i=0;
        if(!mongoTemplate.exists(new Query(Criteria.where(STAFF_ID).is(staffId).and(DELETED).is(false)),
                StaffActivitySetting.class)){
            staffId = 0l;
        }
        aggregations[i++] = match(Criteria.where(STAFF_ID).in(staffId).and(DELETED).is(false));
        aggregations[i++] = group(STAFF_ID).addToSet(ACTIVITY_ID).as(ACTIVITY_IDS);
        aggregations[i++] = getCustomLookUpForActivityAggregationOperation(activityIdString,isActivityType,unitId);
        aggregations[i++] = getCustomAggregationOperationForChildActivitiyIds();
        aggregations[i++] = getCustomAggregationOperationForConcatArray();
        aggregations[i++] = getCustomAggregationOperationForActivities();
        aggregations[i++] = new CustomAggregationOperation("{\"$unwind\": \"$activities\"}");
        aggregations[i++] = getCustomAggregationOperationForReplaceActivity();
        aggregations[i++] = getCustomAggregationOperationForStaffActivitySetting();
        aggregations[i++] = getCustomAggregationOperationForMatchCount();
        return mongoTemplate.aggregate(Aggregation.newAggregation(aggregations), "staffActivitySetting", ActivityWithCompositeDTO.class).getMappedResults();
    }

    @Override
    public List<ActivityDTO> findActivitiesWithTimeTypeByActivityId(Collection<BigInteger> activityIds) {
        return getActivityDTOS(Criteria.where("id").in(activityIds).and(DELETED).is(false));
    }

    private List<ActivityDTO> getActivityDTOS(Criteria criteria) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup(TIME_TYPE, BALANCE_SETTINGS_ACTIVITY_TAB_TIME_TYPE_ID, "_id",
                        TIME_TYPE1),
                lookup(ACTIVITY_PRIORITY, ACTIVITY_PRIORITY_ID, UNDERSCORE_ID,
                        ACTIVITY_PRIORITY),
                unwind(ACTIVITY_PRIORITY,true),
                unwind(TIME_TYPE1)
        );
        return mongoTemplate.aggregate(aggregation, Activity.class,ActivityDTO.class).getMappedResults();
    }

    private CustomAggregationOperation getCustomAggregationOperationForMatchCount() {
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

    private CustomAggregationOperation getCustomAggregationOperationForStaffActivitySetting() {
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

    private CustomAggregationOperation getCustomAggregationOperationForReplaceActivity() {
        return new CustomAggregationOperation("{\n" +
                "      \"$replaceRoot\": {\n" +
                "        \"newRoot\": \"$activities\"\n" +
                "      }\n" +
                "    }");
    }

    private CustomAggregationOperation getCustomAggregationOperationForChildActivitiyIds() {
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

    private CustomAggregationOperation getCustomAggregationOperationForConcatArray() {
        return new CustomAggregationOperation("{\n" +
                "        $project:{\n" +
                "            \"_id\":0,\n" +
                "            \"activityIds\": {\n" +
                "          \"$concatArrays\": [\"$activityIds\",\"$otherActivityIds\"]\n" +
                "        }\n" +
                "            }\n" +
                "        }");
    }

    private CustomAggregationOperation getCustomAggregationOperationForActivities() {
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

    private CustomAggregationOperation getCustomLookUpForActivityAggregationOperation(String activityString,boolean isActivityType,Long unitId) {
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