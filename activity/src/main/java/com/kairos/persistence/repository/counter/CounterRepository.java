package com.kairos.persistence.repository.counter;

import com.kairos.activity.counter.CounterOrderDTO;
import com.kairos.activity.counter.KPICategoryDTO;
import com.kairos.activity.counter.ModuleCounterGroupingDTO;
import com.kairos.activity.counter.RoleCounterDTO;
import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.activity.counter.enums.CounterType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.counter.*;
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

    //categoryKPI crud

    public List<KPI> getKPIsByCategory(List<BigInteger> categoryId){
        Query query = new Query(Criteria.where("categoryId").in(categoryId).and("deleted").is(false));
        return mongoTemplate.find(query, KPI.class);
    }

    //tabKPI distribution crud

    public List<TabKPIEntry> getTabKPIConfgiurationByTabId(List<String> tabIds){
        Query query = new Query(Criteria.where("tabId").in(tabIds));
        return mongoTemplate.find(query, TabKPIEntry.class);
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

    public List<KPICategoryDTO> getKPICategoriesByAssignee(Long assigneeId, ConfLevel level){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("levelId").is(assigneeId).and("ownerLevel").is(level))
        );
        return null;
    }
}
