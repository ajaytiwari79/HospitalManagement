package com.kairos.persistence.repository.counter;

import com.kairos.activity.counter.KPICategoryDTO;
import com.kairos.activity.counter.distribution.access_group.RoleCounterDTO;
import com.kairos.activity.counter.distribution.category.CategoryAssignmentDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIMappingDTO;
import com.kairos.activity.counter.distribution.tab.TabKPIMappingDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.activity.counter.enums.CounterType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.counter.*;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

@Repository
public class CounterRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    //get counter by type
    public Counter getCounterByType(CounterType type) {
        Query query = new Query(Criteria.where("type").is(type));
        return mongoTemplate.findOne(query, Counter.class);
    }

    public List<Counter> getCounterByTypes(List<CounterType> types){
        Query query = new Query(Criteria.where("type").in(types));
        return mongoTemplate.find(query, Counter.class);
    }

    //get role and moduleCounterId mapping for unit
    public List<RoleCounterDTO> getRoleAndModuleCounterIdMapping(BigInteger unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("unitId").is(unitId)),
                Aggregation.group("roleId").addToSet("moduleCounterId").as("moduleCounterIds"),
                Aggregation.project("moduleCounterIds")

        );

        AggregationResults<RoleCounterDTO> results = mongoTemplate.aggregate(aggregation, UnitRoleCounter.class, RoleCounterDTO.class);
        return results.getMappedResults();
    }

    //removal of counters
    public void removeAll(String fieldName, List values, Class claz){
        Query query = new Query(Criteria.where(fieldName).in(values));
        mongoTemplate.remove(query, claz);
    }

    //get item by Id
    public Object getItemById(BigInteger id, Class claz){
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, claz);
    }

    //remove item by Id
    public void removeItemById(BigInteger id, Class claz){
        Query query = new Query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, claz);
    }


    // order counters list for tab
    public List getMappedValues(List<AggregationOperation> operations, Class fromClassType, Class toClassType ){
        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<Activity> results = mongoTemplate.aggregate(aggregation, fromClassType, toClassType);
        return results.getMappedResults();
    }

    //test cases..
    //getCounterListByType for testcases
    public List getEntityItemList(Class claz){
        Query query = new Query(Criteria.where("deleted").is(false));
        return mongoTemplate.find(query, claz);
    }

    private String getRefQueryField(ConfLevel level){
        switch(level){
            case COUNTRY: return "countryId";
            case UNIT: return "unitId";
            case STAFF: return "staffId";
            case DEFAULT: return "countryId";
            default: return "countryId";
        }
    }

    //KPI assignment CRUD
    public List<KPIAssignment> getKPIAssignments(List<BigInteger> kpiIds, ConfLevel level, Long refId){
        String refQueryField = getRefQueryField(level);
        Criteria matchCriteria;
        if(kpiIds!=null){
            matchCriteria = Criteria.where("kpiId").in(kpiIds).and(refQueryField).is(refId);
        }else{
            matchCriteria = Criteria.where(refQueryField).is(refId);
        }
        Query query = new Query(matchCriteria);
        return mongoTemplate.find(query, KPIAssignment.class);
    }

    //category CRUD

    public List<KPICategoryDTO> getKPICategory(ConfLevel level, Long refId){
        String queryField = (ConfLevel.COUNTRY.equals(level))? "countryId":"unitId";
        String categoryListField = "categoryList";
        Aggregation ag = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(queryField).is(refId).and("deleted").is(false))
                , Aggregation.lookup("kPICategory", "categoryId", "id", "category")
                , Aggregation.project("categoryId").and("category").arrayElementAt(0).as("category").and(queryField)
                , Aggregation.sort(Sort.Direction.ASC, "categoryId")
                , Aggregation.group(queryField).push("category").as(categoryListField)
                , Aggregation.project(categoryListField)
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(ag, CategoryAssignment.class, Map.class);
        if(!results.getMappedResults().isEmpty() && results.getMappedResults().get(0).get(categoryListField)!=null) {
            return ObjectMapperUtils.copyPropertiesOfListByMapper((List) results.getMappedResults().get(0).get(categoryListField), KPICategoryDTO.class);
        }
        return new ArrayList<>();
    }

    public List<CategoryAssignmentDTO> getCategoryAssignments(List<BigInteger> categoryIds, ConfLevel level, Long refId){
        String queryField = (ConfLevel.COUNTRY.equals(level)) ? "countryId" : "unitId";
        Criteria matchCriteria = categoryIds == null ? Criteria.where(queryField).is(refId) : Criteria.where(queryField).is(refId).and("categoryId").in(categoryIds);
        Aggregation ag = Aggregation.newAggregation(
                Aggregation.match(matchCriteria),
                Aggregation.lookup("kPICategory", "categoryId", "_id", "category"),
                Aggregation.project("countryId", "level", "unitId").and("category").arrayElementAt(0).as("category")
        );

        AggregationResults<CategoryAssignmentDTO> results = mongoTemplate.aggregate(ag, CategoryAssignment.class, CategoryAssignmentDTO.class);
        return results.getMappedResults();
    }

    public CategoryAssignment getCategoryAssignment(BigInteger categoryId, ConfLevel level, Long refId){
        String queryField = (ConfLevel.COUNTRY.equals(level))? "countryId":"unitId";
        Query query = new Query(Criteria.where(queryField).is(refId).and("categoryId").is(refId));
        return mongoTemplate.findOne(query, CategoryAssignment.class);
    }

    //CategoryKPI distribution

    public List<CategoryKPIConf> getCategoryKPIConfs(BigInteger categoryAssignmentId){
        Query query = new Query(Criteria.where("categoryAssignmentId").is(categoryAssignmentId));
        return mongoTemplate.find(query, CategoryKPIConf.class);
    }

    public List<CategoryKPIMappingDTO> getKPIsMappingForCategories(List<BigInteger> categoryAssignmentIds){
        Aggregation ag = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("categoryAssignmentId").in(categoryAssignmentIds))
                , Aggregation.lookup("categoryAssignment", "categoryAssignmentId", "id", "categoryAssignment")
                , Aggregation.lookup("kpiAssignment", "kpiAssignmentId", "id", "kpiAssignment")
                , Aggregation.project().and("kpiAssignment").arrayElementAt(0).as("kpiAssignment").and("categoryAssignment").arrayElementAt(0).as("categoryAssignment")
                , Aggregation.group("categoryAssignment.categoryId").push("kpiAssignment.kpiId").as("kpiIds")
                , Aggregation.project().and("id").as("categoryId").and("kpiIds")
        );
        AggregationResults<CategoryKPIMappingDTO> results = mongoTemplate.aggregate(ag, CategoryKPIConf.class, CategoryKPIMappingDTO.class);
        return results.getMappedResults();
    }

    //tabKPI distribution crud

    public List<TabKPIMappingDTO> getTabKPIConfigurationByTabIds(List<String> tabIds, ConfLevel level, Long refId){
        String refQueryField = getRefQueryField(level);
        Aggregation ag = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("tabId").in(tabIds).and(refQueryField).is(refId))
                , Aggregation.lookup("kpiAssignment", "kpiAssignmentId", "id", "kpiAssignment")
                , Aggregation.project("tabId").and("kpiAssignment").arrayElementAt(0).as("kpiAssignment")
                , Aggregation.group("tabId").push("kpiAssignment.kpiId").as("kpiIds")
                , Aggregation.project().and("id").as("tabId").and("kpiIds")
        );
        AggregationResults<TabKPIMappingDTO> results = mongoTemplate.aggregate(ag, TabKPIConf.class, TabKPIMappingDTO.class);
        return results.getMappedResults();
    }

    public void removeTabKPIConfiguration(TabKPIEntry entry){
        Query query = new Query(Criteria.where("tabId").is(entry.getTabId()).and("kpiId").is(entry.getKpiId()));
        mongoTemplate.remove(query, TabKPIEntry.class);
    }

    //accessGroupKPI distribution crud

    public List<AccessGroupKPIEntry> getAccessGroupKPIConfigurationByAccessGroupId(List<Long> accessGroupIds){
        Query query = new Query(Criteria.where("accessGroupId").in(accessGroupIds));
        return mongoTemplate.find(query, AccessGroupKPIEntry.class);
    }

    public void removeAccessGroupKPIEntry(AccessGroupKPIEntry entry){
        Query query = new Query(Criteria.where("accessGroupId").is(entry.getAccessGroupId()).and("kpiId").is(entry.getKpiId()));
        mongoTemplate.remove(query, AccessGroupKPIEntry.class);
    }

    public List<OrgTypeKPIEntry> getOrgTypeKPIConfigurationByOrgTypeId(List<Long> accessGroupIds){
        Query query = new Query(Criteria.where("orgTypeId").in(accessGroupIds));
        return mongoTemplate.find(query, OrgTypeKPIEntry.class);
    }

    public void removeOrgTypeKPIEntry(OrgTypeKPIEntry entry){
        Query query = new Query(Criteria.where("orgTypeId").is(entry.getOrgTypeId()).and("kpiId").is(entry.getKpiId()));
        mongoTemplate.remove(query, OrgTypeKPIEntry.class);
    }
}
