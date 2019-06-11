package com.kairos.persistence.repository.counter;

import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.dto.activity.counter.configuration.CounterDTO;
import com.kairos.dto.activity.counter.configuration.KPIDTO;
import com.kairos.dto.activity.counter.distribution.access_group.AccessGroupMappingDTO;
import com.kairos.dto.activity.counter.distribution.category.CategoryKPIMappingDTO;
import com.kairos.dto.activity.counter.distribution.category.KPICategoryDTO;
import com.kairos.dto.activity.counter.distribution.dashboard.KPIDashboardDTO;
import com.kairos.dto.activity.counter.distribution.org_type.OrgTypeMappingDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIDTO;
import com.kairos.dto.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.dto.activity.counter.enums.KPIValidity;
import com.kairos.dto.activity.counter.enums.ModuleType;
import com.kairos.dto.user.access_page.KPIAccessPageDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.common.MongoSequence;
import com.kairos.persistence.model.counter.*;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepositoryImpl;
import com.kairos.utils.user_context.UserContext;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.client.result.DeleteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.validation.Valid;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.AppConstants.DELETED;
import static com.kairos.dto.activity.counter.enums.ConfLevel.COUNTRY;
import static com.kairos.enums.kpi.KPIRepresentation.INDIVIDUAL_STAFF;
import static com.kairos.persistence.repository.custom_repository.MongoBaseRepositoryImpl.SEQUENCE_POST_FIX;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Repository
public class CounterRepository{

    @Inject
    private MongoTemplate mongoTemplate;

    private final static Logger LOGGER = LoggerFactory.getLogger(MongoBaseRepositoryImpl.class);


    public static String getRefQueryField(ConfLevel level) {
        switch (level) {
            case UNIT:
                return "unitId";
            case STAFF:
                return "staffId";
            default:
                return "countryId";
        }
    }

    //get counter by type
    public Counter getCounterByType(CounterType type) {
        Query query = new Query(Criteria.where("deleted").is(false).and("type").is(type));
        return mongoTemplate.findOne(query, Counter.class);
    }

    public List<Counter> getCounterByTypes(List<CounterType> types) {
        Query query = new Query(Criteria.where("deleted").is(false).and("type").in(types));
        return mongoTemplate.find(query, Counter.class);
    }

    public KPI getKPIByid(BigInteger kpiId) {
        Query query = new Query(Criteria.where("deleted").is(false).and("_id").is(kpiId));
        return mongoTemplate.findOne(query, KPI.class);
    }


    public ApplicableKPI getKpiByTitleAndUnitId(String title, Long refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and("title").is(title).regex(Pattern.compile("^" + title + "$", Pattern.CASE_INSENSITIVE)).and(refQueryField).is(refId).and("level").is(level));
        return mongoTemplate.findOne(query, ApplicableKPI.class);
    }

    public List<KPI> getKPIsByIds(List<BigInteger> kpiIds) {
        Query query = new Query(Criteria.where("deleted").is(false).and("id").in(kpiIds));
        return mongoTemplate.find(query, KPI.class);
    }

    //removal of counters
    public void removeAll(String fieldName, List values, Class claz, ConfLevel level) {
        Query query = new Query(Criteria.where("deleted").is(false).and("level").is(level).and(fieldName).in(values));
        mongoTemplate.remove(query, claz);
    }

    //get item by Id
    public Object getEntityById(BigInteger id, Class claz) {
        Query query = new Query(Criteria.where("deleted").is(false).and("_id").is(id));
        return mongoTemplate.findOne(query, claz);
    }

    //remove item by Id
    public void removeEntityById(BigInteger id, Class claz) {
        Query query = new Query(Criteria.where("deleted").is(false).and("id").is(id));
        mongoTemplate.remove(query, claz);
    }


    // order counters list for tab
    public List getMappedValues(List<AggregationOperation> operations, Class fromClassType, Class toClassType) {
        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<Activity> results = mongoTemplate.aggregate(aggregation, fromClassType, toClassType);
        return results.getMappedResults();
    }

    //test cases..
    //getCounterListByType for testcases
    public List<KPIDTO> getCounterListForReferenceId(Long refId, ConfLevel level, boolean copy) {
        String refQueryField = getRefQueryField(level);
        Criteria criteria = Criteria.where("deleted").is(false).and(refQueryField).is(refId).and("level").is(level);
        if (copy) {
            criteria.and("copy").is(copy);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("counter", "activeKpiId", "_id", "kpi"),
                project("title","kpiRepresentation").and("kpi").arrayElementAt(0).as("kpi"),
                project().and("title").as("title").and("kpi._id").as("_id").and("kpi.type").as("type")
                        .and("kpi.calculationFormula").as("calculationFormula").and("kpi.counter").as("counter").and("kpi.multiDimensional").as("multiDimensional").
                        and("kpi.fibonacciKPI").as("fibonacciKPI").and("kpiRepresentation").as("kpiRepresentation")
        );
        AggregationResults<KPIDTO> results = mongoTemplate.aggregate(aggregation, ApplicableKPI.class, KPIDTO.class);
        return results.getMappedResults();
    }

    public List<KPIDTO> getFibonacciKpiForReferenceId(Long refId, ConfLevel level, boolean copy) {
        String refQueryField = getRefQueryField(level);
        Criteria criteria = Criteria.where("deleted").is(false).and(refQueryField).is(refId).and("level").is(level);
        if (copy) {
            criteria.and("copy").is(copy);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("counter", "activeKpiId", "_id", "kpi"),
                project("title","fibonacciKPIConfigs").and("kpi").arrayElementAt(0).as("kpi"),
                match(Criteria.where("kpi.fibonacciKPI").is(true)),
                project("title","fibonacciKPIConfigs").and("kpi._id").as("_id").and("kpi.type").as("type")
                        .and("kpi.calculationFormula").as("calculationFormula").and("kpi.counter").as("counter").
                        and("kpi.fibonacciKPI").as("fibonacciKPI").and("kpi.description").as("kpi.description")
                        .and("kpi.referenceId").as("referenceId")
        );
        AggregationResults<KPIDTO> results = mongoTemplate.aggregate(aggregation, ApplicableKPI.class, KPIDTO.class);
        return results.getMappedResults();
    }


    //KPI  CRUD
    public List<ApplicableKPI> getApplicableKPI(List<BigInteger> kpiIds, ConfLevel level, Long refId) {
        String refQueryField = getRefQueryField(level);
        Criteria criteria = Criteria.where("deleted").is(false).and(refQueryField).is(refId).and("level").is(level);
        if (isCollectionNotEmpty(kpiIds)) {
            criteria.and("activeKpiId").in(kpiIds);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, ApplicableKPI.class);
    }

    public List<ApplicableKPI> getFilterBaseApplicableKPIByKpiIdsOrUnitId(List<BigInteger> kpiIds, List<ConfLevel> level, Long unitId) {
        Criteria criteria = Criteria.where("deleted").is(false).and("baseKpiId").in(kpiIds).and("level").in(level).orOperator(Criteria.where("applicableFilter").exists(false), Criteria.where("applicableFilter.modified").is(false));
        if (isNotNull(unitId)) {
            criteria.and("unitId").is(unitId);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, ApplicableKPI.class);
    }


    //category CRUD

    public List<KPICategory> getKPICategoryByIds(List<BigInteger> categoryIds, ConfLevel level, Long refId) {
        String queryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and("_id").in(categoryIds).and(queryField).is(refId).and("level").is(level));
        return mongoTemplate.find(query, KPICategory.class);

    }

    public List<KPICategoryDTO> getKPICategory(List<BigInteger> categoryIds, ConfLevel level, Long refId) {
        String queryField = getRefQueryField(level);
        Criteria matchCriteria = categoryIds == null ? Criteria.where("deleted").is(false).and(queryField).is(refId) : Criteria.where("deleted").is(false).and("_id").in(categoryIds).and(queryField).is(refId);
        Query query = new Query(matchCriteria);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, KPICategory.class), KPICategoryDTO.class);
    }


    public void removeCategoryKPIEntries(List<BigInteger> categoryIds, List<BigInteger> kpiIds) {
        Query query = new Query(Criteria.where("deleted").is(false).and("categoryId").in(categoryIds).and("kpiId").in(kpiIds));
        mongoTemplate.remove(query, CategoryKPIConf.class);
    }

    //CategoryKPI distribution

    public List<CategoryKPIConf> getCategoryKPIConfs(List<BigInteger> kpiIds, List<BigInteger> categoryIds) {
        Query query = new Query(Criteria.where("deleted").is(false).and("kpiId").in(kpiIds).and("categoryId").in(categoryIds));
        return mongoTemplate.find(query, CategoryKPIConf.class);
    }

    public List<CategoryKPIMappingDTO> getKPIsMappingForCategories(List<BigInteger> categoryIds) {
        Aggregation ag = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("categoryId").in(categoryIds))
                , Aggregation.group("categoryId").push("kpiId").as("kpiIds")
                , project().and("_id").as("categoryId").and("kpiIds").as("kpiId")
        );
        AggregationResults<CategoryKPIMappingDTO> results = mongoTemplate.aggregate(ag, CategoryKPIConf.class, CategoryKPIMappingDTO.class);
        return results.getMappedResults();
    }


    public List<CategoryKPIMappingDTO> getKPIsMappingForCategoriesForStaff(Set<BigInteger> kpiIds, Long refId, ConfLevel level) {
        String queryField = (COUNTRY.equals(level)) ? "countryId" : "unitId";
        Aggregation ag = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and(queryField).is(refId).and("level").is(level)),
                lookup("categoryKPIConf", "_id", "categoryId", "category"),
                match(Criteria.where("category.kpiId").in(kpiIds))
                , Aggregation.group("id", "name").push("category.kpiId").as("kpiId")
                , project().and("id").as("categoryId").and("kpiId").arrayElementAt(0).as("kpiId").and("name").as("name")
        );
        AggregationResults<CategoryKPIMappingDTO> results = mongoTemplate.aggregate(ag, KPICategory.class, CategoryKPIMappingDTO.class);
        return results.getMappedResults();
    }

    //tabKPI distribution crud

    public List<TabKPIMappingDTO> getTabKPIConfigurationByTabIds(List<String> tabIds, List<BigInteger> kpiIds, Long refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = null;
        if (kpiIds.isEmpty()) {
            query = new Query(Criteria.where("deleted").is(false).and("tabId").in(tabIds).and(refQueryField).is(refId).and("level").is(level));
        } else {
            query = new Query(Criteria.where("deleted").is(false).and("tabId").in(tabIds).and("kpiId").in(kpiIds).and(refQueryField).is(refId).and("level").is(level));
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, TabKPIConf.class), TabKPIMappingDTO.class);
    }

    public List<TabKPIDTO> getTabKPIIdsByTabIds(String tabId, Long refId, Long countryId, ConfLevel level) {
        Criteria criteria = null;
        if (COUNTRY.equals(level)) {
            criteria = Criteria.where("deleted").is(false).and("tabId").is(tabId).and("countryId").is(refId).and("level").is(level);
        } else {
            criteria = Criteria.where("deleted").is(false).and("tabId").is(tabId).orOperator(Criteria.where("countryId").is(countryId).and("level")
                    .is(COUNTRY).and("kpiValidity").in(KPIValidity.MANDATORY, KPIValidity.OPTIONAL), Criteria.where("unitId").is(refId).and("level").is(level));
        }
        Aggregation aggregation = Aggregation.newAggregation(match(criteria),
                lookup("counter", "kpiId", "_id", "kpis"),
                project("tabId", "position", "id", "size", "kpiValidity", "locationType", "priority", "level").and("kpis").arrayElementAt(0).as("kpi"),
                Aggregation.sort(Sort.Direction.ASC, "priority"));
        AggregationResults<TabKPIDTO> results = mongoTemplate.aggregate(aggregation, TabKPIConf.class, TabKPIDTO.class);
        return results.getMappedResults();
    }

    public List<TabKPIDTO> getTabKPIForStaffByTabAndStaffId(List<String> tabIds, List<BigInteger> kpiIds, Long staffId, Long unitId, ConfLevel level) {
        Criteria criteria;
        if (kpiIds.isEmpty()) {
            criteria = Criteria.where("deleted").is(false).and("tabId").in(tabIds).and("staffId").is(staffId).and("unitId").is(unitId).and("level").is(level);
        } else {
            criteria = Criteria.where("deleted").is(false).and("tabId").in(tabIds).and("kpiId").in(kpiIds).and("staffId").is(staffId).and("level").is(level);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("counter", "kpiId", "_id", "kpis"),
                project("tabId", "position", "id", "size", "kpiValidity", "locationType", "priority", "level").and("kpis").arrayElementAt(0).as("kpi")
        );
        AggregationResults<TabKPIDTO> aggregationResults = mongoTemplate.aggregate(aggregation, TabKPIConf.class, TabKPIDTO.class);
        return aggregationResults.getMappedResults();
    }

    ;

    public List<TabKPIDTO> getTabKPIForStaffByTabAndStaffIdPriority(String tabId, List<BigInteger> kpiIds, Long staffId, Long countryId, Long unitId, ConfLevel level) {
        Criteria criteria = Criteria.where("deleted").is(false).and("tabId").is(tabId).orOperator(Criteria.where("unitId").is(unitId).and("level").is(ConfLevel.UNIT).
                and("kpiValidity").in(KPIValidity.MANDATORY, KPIValidity.OPTIONAL), Criteria.where("countryId").is(countryId).and("level").is(COUNTRY).and("kpiValidity").
                in(KPIValidity.MANDATORY, KPIValidity.OPTIONAL), Criteria.where("kpiId").in(kpiIds).and("staffId").is(staffId).and("level").is(level));
        if (kpiIds.isEmpty()) {
            criteria = Criteria.where("deleted").is(false).and("tabId").is(tabId).orOperator(Criteria.where("unitId").is(unitId).and("level").is(ConfLevel.UNIT).
                    and("kpiValidity").in(KPIValidity.MANDATORY, KPIValidity.OPTIONAL), Criteria.where("countryId").is(countryId).and("level").is(COUNTRY).and("kpiValidity").
                    in(KPIValidity.MANDATORY, KPIValidity.OPTIONAL), Criteria.where("staffId").is(staffId).and("level").is(level));
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                lookup("counter", "kpiId", "_id", "kpis"),
                project("tabId", "position", "id", "size", "kpiValidity", "locationType", "priority", "level").and("kpis").arrayElementAt(0).as("kpi"),
                Aggregation.sort(Sort.Direction.ASC, "priority")
        );
        AggregationResults<TabKPIDTO> aggregationResults = mongoTemplate.aggregate(aggregation, TabKPIConf.class, TabKPIDTO.class);
        return aggregationResults.getMappedResults();
    }

    ;

/*
Criteria.where("level").is(ConfLevel.COUNTRY.toString()),Criteria.where("level").is()
               ,Criteria.where("kpiValidity").is(KPIValidity.MANDATORY.toString()),Criteria.where("kpiValidity").is(KPIValidity.OPTIONAL.toString()));
 */

    public List<TabKPIConf> findTabKPIConfigurationByTabIds(List<String> tabIds, List<BigInteger> kpiIds, Long refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and("tabId").in(tabIds).and("kpiId").in(kpiIds).and(refQueryField).is(refId).and("level").is(level).and("kpiValidity").nin(KPIValidity.MANDATORY, KPIValidity.OPTIONAL));
        return mongoTemplate.find(query, TabKPIConf.class);
    }

    public TabKPIConf findTabKPIConfigurationByTabId(String tabId, List<BigInteger> kpiIds, Long refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and("tabId").is(tabId).and("kpiId").in(kpiIds).and(refQueryField).is(refId).and("level").is(level));
        return mongoTemplate.findOne(query, TabKPIConf.class);
    }

    public DeleteResult removeTabKPIConfiguration(TabKPIMappingDTO entry, Long refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and("tabId").is(entry.getTabId()).and("kpiId").is(entry.getKpiId()).and(refQueryField).is(refId).and("level").is(level));
        return mongoTemplate.remove(query, TabKPIConf.class);
    }


    //accessGroupKPI distribution crud

    public List<AccessGroupKPIEntry> getAccessGroupKPIByUnitId(AccessGroupMappingDTO entry, Long unitId, ConfLevel level) {
        Query query = new Query(Criteria.where("deleted").is(false).and("unitId").is(unitId).and("accessGroupId").nin(entry.getAccessGroupId()).and("level").is(level));
        return mongoTemplate.find(query, AccessGroupKPIEntry.class);
    }

    public AccessGroupKPIEntry getAccessGroupKPIEntry(AccessGroupMappingDTO entry, Long unitId, ConfLevel level) {
        Query query = new Query(Criteria.where("deleted").is(false).and("accessGroupId").is(entry.getAccessGroupId()).and("kpiId").is(entry.getKpiId()).and("unitId").is(unitId).and("level").is(level));
        return mongoTemplate.findOne(query, AccessGroupKPIEntry.class);
    }

    public void removeAccessGroupKPIEntry(List<Long> unitIds, BigInteger kpiId) {
        Query query = new Query(Criteria.where("deleted").is(false).and("unitId").in(unitIds).and("kpiId").is(kpiId));
        mongoTemplate.remove(query, AccessGroupKPIEntry.class);
    }

    public void removeCategoryKPIEntry(List<Long> unitIds, BigInteger kpiId) {
        Query query = new Query(Criteria.where("deleted").is(false).and("unitId").in(unitIds).and("kpiId").is(kpiId));
        mongoTemplate.remove(query, CategoryKPIConf.class);
    }

    public void removeCategoryKPIEntryOfStaff(Long unitId, List<BigInteger> kpiId, ConfLevel level) {
        Query query = new Query(Criteria.where("deleted").is(false).and("unitId").is(unitId).and("kpiId").nin(kpiId).and("level").is(level));
        mongoTemplate.remove(query, CategoryKPIConf.class);
    }


    public void removeAccessGroupKPIEntryForCountry(AccessGroupMappingDTO entry, Long refId) {
        Query query = new Query(Criteria.where("deleted").is(false).and("accessGroupId").is(entry.getAccessGroupId()).and("kpiId").is(entry.getKpiId()).and("countryId").is(refId));
        mongoTemplate.remove(query, AccessGroupKPIEntry.class);
    }

    public void removeApplicableKPI(List<Long> refIds, List<BigInteger> kpiIds, Long unitId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = ConfLevel.STAFF.equals(level) ? new Query(Criteria.where("deleted").is(false).and(refQueryField).in(refIds).and("baseKpiId").in(kpiIds).and("level").in(level)) :
                new Query(Criteria.where("deleted").is(false).and(refQueryField).in(refIds).and("baseKpiId").in(kpiIds));
        mongoTemplate.remove(query, ApplicableKPI.class);
    }

    public void removeTabKPIEntry(List<Long> refIds, List<BigInteger> kpiId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and(refQueryField).in(refIds).and("kpiId").in(kpiId));
        mongoTemplate.remove(query, TabKPIConf.class);
    }

    public void removeDashboardTabOfStaff(Long staffId, Long unitId, ConfLevel level) {
        Query query = new Query(Criteria.where("deleted").is(false).and("staffId").in(staffId).and("unitId").is(unitId).and("level").is(level));
        mongoTemplate.remove(query, KPIDashboard.class);
    }

    public List<ApplicableKPI> getKPIByKPIIds(List<BigInteger> kpiIds, Long refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and(refQueryField).is(refId).and("activeKpiId").in(kpiIds).and("level").is(level));
        return mongoTemplate.find(query, ApplicableKPI.class);
    }

    public ApplicableKPI getKPIByKPIId(BigInteger kpiId, Long refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and(refQueryField).is(refId).and("activeKpiId").is(kpiId).and("level").is(level));
        return mongoTemplate.findOne(query, ApplicableKPI.class);
    }


    public OrgTypeKPIEntry getOrgTypeKPIEntry(OrgTypeMappingDTO entry, Long countryId) {
        Query query = new Query(Criteria.where("deleted").is(false).and("orgTypeId").is(entry.getOrgTypeId()).and("kpiId").is(entry.getKpiId()).and("countryId").is(countryId));
        return mongoTemplate.findOne(query, OrgTypeKPIEntry.class);
    }

    public List<BigInteger> getKPISOfAccessGroup(List<Long> accessGroupId, Long unitId, ConfLevel level) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("accessGroupId").in(accessGroupId).and("unitId").is(unitId).and("level").is(level)),
                project().and("kpiId").as("kpiId").andExclude("_id")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, AccessGroupKPIEntry.class, Map.class);
        List<BigInteger> kpiIds = new ArrayList<>();
        for (Map kpi : results) {
            kpiIds.add(new BigInteger(kpi.get("kpiId").toString()));
        }
        return kpiIds;
    }

    public List<OrgTypeMappingDTO> getOrgTypeKPIEntryOrgTypeIds(List<Long> orgTypeIds, List<BigInteger> kpiIds) {
        Query query = null;
        if (kpiIds.isEmpty()) {
            query = new Query(Criteria.where("deleted").is(false).and("orgTypeId").in(orgTypeIds));
        } else {
            query = new Query(Criteria.where("deleted").is(false).and("orgTypeId").in(orgTypeIds).and("kpiId").in(kpiIds));
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, OrgTypeKPIEntry.class), OrgTypeMappingDTO.class);
    }


    public List<BigInteger> getOrgTypeKPIIdsOrgTypeIds(List<Long> orgTypeIds, List<BigInteger> kpiIds) {
        Criteria criteria = Criteria.where("deleted").is(false).and("orgTypeId").in(orgTypeIds);
        if (!kpiIds.isEmpty()) {
            criteria = criteria.and("kpiId").in(kpiIds);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                project("kpiId")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, OrgTypeKPIEntry.class, Map.class);
        return results.getMappedResults().stream().map(s -> new BigInteger(s.get("kpiId").toString())).collect(Collectors.toList());
    }


    public List<AccessGroupMappingDTO> getAccessGroupKPIEntryAccessGroupIds(List<Long> accessGroupIds, List<BigInteger> kpiIds, ConfLevel level, Long refId) {
        String queryField = getRefQueryField(level);
        Query query = null;
        if (kpiIds.isEmpty()) {
            query = new Query(Criteria.where("deleted").is(false).and("accessGroupId").in(accessGroupIds).and(queryField).is(refId));
        } else if (accessGroupIds.isEmpty()) {
            query = new Query(Criteria.where("deleted").is(false).and("kpiId").in(kpiIds).and(queryField).is(refId));
        } else {
            query = new Query(Criteria.where("deleted").is(false).and("accessGroupId").in(accessGroupIds).and("kpiId").in(kpiIds).and(queryField).is(refId));
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, AccessGroupKPIEntry.class), AccessGroupMappingDTO.class);
    }

    public List<BigInteger> getAccessGroupKPIIdsAccessGroupIds(List<Long> accessGroupIds, List<BigInteger> kpiIds, ConfLevel level, Long refId) {
        String queryField = getRefQueryField(level);
        Criteria criteria = Criteria.where("deleted").is(false).and("accessGroupId").in(accessGroupIds).and(queryField).is(refId);
        if (!kpiIds.isEmpty()) {
            criteria = criteria.and("kpiId").in(kpiIds);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                project("kpiId")
        );
        AggregationResults<Map> result = mongoTemplate.aggregate(aggregation, AccessGroupKPIEntry.class, Map.class);
        return result.getMappedResults().stream().map(o -> new BigInteger(o.get("kpiId").toString())).collect(Collectors.toList());
    }

    public List<KPIDTO> getCopyKpiOfUnit(ConfLevel level, Long refId, boolean copy) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("staffId").is(refId).and("level").is(level).and("copy").is(copy)),
                lookup("counter", "activeKpiId", "_id", "kpi"),
                project().and("kpi").arrayElementAt(0).as("kpi"),
                Aggregation.group("kpi._id", "kpi.title", "kpi.counter", "kpi.type"),
                project().and("_id").as("_id").and("kpi.type").as("type")
                        .and("kpi.title").as("title").and("kpi.counter").as("counter")
        );
        AggregationResults<KPIDTO> results = mongoTemplate.aggregate(aggregation, ApplicableKPI.class, KPIDTO.class);
        return results.getMappedResults();
    }

    public List<KPIDTO> getAccessGroupKPIDto(List<Long> accessGroupIds, ConfLevel level, Long refId, Long staffId) {
        String refQueryField = getRefQueryField(level);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("accessGroupId").in(accessGroupIds).and(refQueryField).is(refId).and("level").is(level)),
                lookup("applicableKPI", "kpiId", "activeKpiId", "applicableKpi"),
                match(Criteria.where("applicableKpi.staffId").is(staffId)),
                lookup("counter", "applicableKpi.activeKpiId", "_id", "kpi"),
                project().and("kpi").arrayElementAt(0).as("kpi"),
                Aggregation.group("kpi._id", "kpi.title", "kpi.counter", "kpi.type"),
                project().and("_id").as("_id").and("kpi.type").as("type")
                        .and("kpi.title").as("title").and("kpi.counter").as("counter")
        );
        AggregationResults<KPIDTO> results = mongoTemplate.aggregate(aggregation, AccessGroupKPIEntry.class, KPIDTO.class);
        return results.getMappedResults();
    }

    public List<BigInteger> getAccessGroupKPIIds(List<Long> accessGroupIds, ConfLevel level, Long refId, Long staffId) {
        String refQueryField = getRefQueryField(level);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("accessGroupId").in(accessGroupIds).and(refQueryField).is(refId).and("level").is(level)),
                lookup("applicableKPI", "kpiId", "activeKpiId", "applicableKpi"),
                match(Criteria.where("applicableKpi.staffId").is(staffId)),
                lookup("counter", "applicableKpi.activeKpiId", "_id", "kpi"),
                project().and("kpi").arrayElementAt(0).as("kpi"),
                project("kpi._id")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, AccessGroupKPIEntry.class, Map.class);
        return results.getMappedResults().stream().filter(s-> isNotNull(s.get("_id"))).map(s -> new BigInteger(s.get("_id").toString())).collect(Collectors.toList());
    }

    public List<AccessGroupMappingDTO> getAccessGroupAndKpiId(Set<Long> accessGroupIds, ConfLevel level, Long refId) {
        String refQueryField = getRefQueryField(level);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("accessGroupId").in(accessGroupIds).and(refQueryField).is(refId).and("level").is(level)),
                Aggregation.group("accessGroupId").push("kpiId").as("kpiIds"),
                project().and("_id").as("accessGroupId").and("kpiIds").as("kpiIds")
        );
        AggregationResults<AccessGroupMappingDTO> results = mongoTemplate.aggregate(aggregation, AccessGroupKPIEntry.class, AccessGroupMappingDTO.class);
        return results.getMappedResults();

    }

    public List<ApplicableKPI> getApplicableKPIByReferenceId(List<BigInteger> kpiIds, List<Long> refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = null;
        if (kpiIds.isEmpty()) {
            query = new Query(Criteria.where("deleted").is(false).and(refQueryField).in(refId).and("level").is(level));
        } else {
            query = new Query(Criteria.where("deleted").is(false).and(refQueryField).in(refId).and("level").is(level).and("activeKpiId").in(kpiIds));
        }
        return mongoTemplate.find(query, ApplicableKPI.class);
    }


    public List<BigInteger> getApplicableKPIIdsByReferenceId(List<BigInteger> kpiIds, List<Long> refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Criteria criteria = Criteria.where("deleted").is(false).and(refQueryField).in(refId).and("level").is(level);
        if (!kpiIds.isEmpty()) {
            criteria = criteria.and("activeKpiId").in(kpiIds);
        }
        Aggregation aggregation = Aggregation.newAggregation(
                match(criteria),
                project("activeKpiId"));
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, ApplicableKPI.class, Map.class);
        List<BigInteger> applicableKpis = results.getMappedResults().stream().map(s -> new BigInteger(s.get("activeKpiId").toString())).collect(Collectors.toList());
        ;
        return applicableKpis;
    }


    public List<TabKPIConf> findTabKPIIdsByKpiIdAndUnitOrCountry(List<BigInteger> kpiIds, Long refid, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and(refQueryField).is(refid).and("KpiId").in(kpiIds).and("level").is(level).and("kpiValidity").nin(KPIValidity.MANDATORY, KPIValidity.OPTIONAL));
        return mongoTemplate.find(query, TabKPIConf.class);
    }

    public List<CounterDTO> getAllCounterBySupportedModule(ModuleType supportedModuleType) {
        Query query = new Query(Criteria.where("deleted").is(false).and("supportedModuleTypes").in(supportedModuleType));
        query.fields().include("id").include("title");
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, Counter.class), CounterDTO.class);
    }

    //dashboard tab
    public List<KPIDashboard> getKPIDashboardByIds(List<String> dashboardIds, ConfLevel level, Long refId) {
        String queryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and("moduleId").in(dashboardIds).and(queryField).is(refId).and("level").is(level));
        return mongoTemplate.find(query, KPIDashboard.class);
    }

    public List<KPIDashboardDTO> getKPIDashboard(List<String> dashBoardIds, ConfLevel level, Long refId) {
        String refQueryField = getRefQueryField(level);
        Criteria matchCriteria = dashBoardIds == null ? Criteria.where("deleted").is(false).and(refQueryField).is(refId).and("level").is(level) : Criteria.where("deleted").is(false).and("moduleId").in(dashBoardIds).and(refQueryField).is(refId).and("level").is(level);
        Query query = new Query(matchCriteria);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, KPIDashboard.class), KPIDashboardDTO.class);
    }

    public List<KPIAccessPageDTO> getKPIAcceccPage(Long refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and(refQueryField).is(refId).and("level").is(level)),
                Aggregation.group("parentModuleId").push("$$ROOT").as("child"),
                project().and("$_id").as("moduleId").and("child").as("child").and("child.enable").as("enable").and("child.defaultTab").as("defaultTab")
        );
        AggregationResults<KPIAccessPageDTO> results = mongoTemplate.aggregate(aggregation, KPIDashboard.class, KPIAccessPageDTO.class);
        return results.getMappedResults();
    }

    public List<DashboardKPIConf> getDashboardKPIConfs(List<BigInteger> kpiIds, List<String> moduleIds, Long refId, ConfLevel level) {
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("deleted").is(false).and(refQueryField).is(refId).and("_id").in(kpiIds).and("level").is(level).and("moduleId").in(moduleIds));
        return mongoTemplate.find(query, DashboardKPIConf.class);
    }

    public List<KPIDashboardDTO> getKPIDashboard(long unitId, ConfLevel level, Long staffId) {
        Criteria matchCriteria = Criteria.where("deleted").is(false).and("unitId").is(unitId).and("staffId").is(staffId).and("level").is(level);
        Query query = new Query(matchCriteria);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, KPIDashboard.class), KPIDashboardDTO.class);
    }

    public List<KPIDashboardDTO> getKPIDashboardsOfStaffs(Long unitId, ConfLevel level, List<Long> staffIds) {
        Criteria matchCriteria = Criteria.where("deleted").is(false).and("unitId").is(unitId).and("staffId").in(staffIds).and("level").is(level);
        Query query = new Query(matchCriteria);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, KPIDashboard.class), KPIDashboardDTO.class);
    }

    public List<KPIDashboard> getKPIDashboardsOfStaffAndUnits(List<Long> unitIds, ConfLevel level, List<Long> staffIds) {
        Criteria matchCriteria = Criteria.where("deleted").is(false).and("unitId").in(unitIds).and("staffId").in(staffIds).and("level").is(level);
        Query query = new Query(matchCriteria);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query, KPIDashboard.class), KPIDashboard.class);
    }

    public boolean allKPIsBelongsToIndividualType(Set<BigInteger> kpiIds, ConfLevel confLevel, Long referenceId) {
        String queryField = getRefQueryField(confLevel);
        Criteria matchCriteria = Criteria.where("activeKpiId").in(kpiIds).and("kpiRepresentation").is(INDIVIDUAL_STAFF).and(DELETED).is(false).and(queryField).is(referenceId).and("level").is(confLevel);
        Query query = new Query(matchCriteria);
        return mongoTemplate.find(query, ApplicableKPI.class).size() == kpiIds.size();
    }

    public <S extends MongoBaseEntity> S save(@Valid S entity) {
        Assert.notNull(entity, "Entity must not be null!");
        /**
         *  Get class name for sequence class
         * */
        String className = entity.getClass().getSimpleName();

        /**
         *  Set Id if entity don't have Id
         * */
        if(entity.getId() == null){
            if(entity.getClass().getSuperclass().equals(WTABaseRuleTemplate.class)){
                //Because WTABaseRuleTemplateDTO extends by All RuleTemaplete
                className = entity.getClass().getSuperclass().getSimpleName();
            }
            if(entity.getClass().equals(FibonacciKPI.class)){
                className = KPI.class.getSimpleName();
            }
            entity.setCreatedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
            entity.setCreatedAt(DateUtils.getDate());
            entity.setId(nextSequence(className));
        }else {
            entity.setLastModifiedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
        }
        /**
         *  Set updatedAt time as current time
         * */
        entity.setUpdatedAt(DateUtils.getDate());
        mongoTemplate.save(entity);
        return entity;
    }

    public <T extends MongoBaseEntity> List<T> saveEntities(@Valid List<T> entities){
        Assert.notNull(entities, "Entity must not be null!");
        Assert.notEmpty(entities, "Entity must not be Empty!");

        String collectionName = mongoTemplate.getCollectionName(entities.get(0).getClass());

        /**
         *  Creating BulkWriteOperation object
         * */

        BulkWriteOperation bulkWriteOperation= mongoTemplate.getMongoDbFactory().getLegacyDb().getCollection(collectionName).initializeUnorderedBulkOperation();

        /**
         *  Creating MongoConverter object (We need converter to convert Entity Pojo to BasicDbObject)
         * */
        MongoConverter converter = mongoTemplate.getConverter();

        BasicDBObject dbObject;

        /**
         *  Handling bulk write exceptions
         * */
        try{

            for (T entity: entities) {
                /**
                 *  Get class name for sequence class
                 * */
                String className = entity.getClass().getSimpleName();
                /**
                 *  Set updatedAt time as current time
                 * */
                entity.setUpdatedAt(DateUtils.getDate());


                if(entity.getId() == null){
                    entity.setCreatedAt(DateUtils.getDate());
                    /**
                     *  Set Id if entity don't have Id
                     * */
                    if(entity.getClass().getSuperclass().equals(WTABaseRuleTemplate.class)){
                        //Because WTABaseRuleTemplateDTO extends by All RuleTemaplete
                        className = entity.getClass().getSuperclass().getSimpleName();
                    }
                    entity.setId(nextSequence(className));
                    entity.setCreatedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
                    dbObject = new BasicDBObject();

                    /*
                     *  Converting entity object to BasicDBObject
                     * */
                    converter.write(entity, dbObject);

                    /*
                     *  Adding entity (BasicDBObject)
                     * */
                    bulkWriteOperation.insert(dbObject);
                }else {
                    entity.setLastModifiedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
                    dbObject = new BasicDBObject();

                    /*
                     *  Converting entity object to BasicDBObject
                     * */
                    converter.write(entity, dbObject);

                    /**
                     *  Creating BasicDbObject for find query
                     * */
                    BasicDBObject query = new BasicDBObject();

                    /**
                     *  Adding query (find by ID)
                     * */
                    query.put("_id", dbObject.get("_id"));

                    /**
                     *  Replacing whole Object
                     * */
                    bulkWriteOperation.find(query).replaceOne(dbObject);
                }
            }

            /**
             * Executing the Operation
             * */
            bulkWriteOperation.execute();
            return entities;

        } catch(Exception ex){
            LOGGER.error("BulkWriteOperation Exception ::  ", ex);
            return null;
        }
    }

    public BigInteger nextSequence(String sequenceName){
        /**
         * adding sequence postfix into class name
         * */
        sequenceName = sequenceName + SEQUENCE_POST_FIX;

        /**
         *  Find query
         * */
        String findQuery = "{'sequenceName':'"+sequenceName+"'}";
        /**
         *  Update query
         * */
        String updateQuery = "{'$inc':{'sequenceNumber':1}}";
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();

        /**
         *  return updated value
         * */
        findAndModifyOptions.returnNew(true);

        /**
         *  create new if not exists
         * */
        findAndModifyOptions.upsert(true);
        MongoSequence mongoSequence = mongoTemplate.findAndModify(new BasicQuery(findQuery), new BasicUpdate(updateQuery), findAndModifyOptions, MongoSequence.class);
        return new BigInteger(mongoSequence.getSequenceNumber()+"");
    }


}
