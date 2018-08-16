package com.kairos.persistence.repository.counter;

import com.kairos.ApplicableFor;
import com.kairos.activity.counter.*;
import com.kairos.enums.CounterType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.counter.*;
import com.kairos.util.ObjectMapperUtils;
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
import java.util.Set;

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

    //get ModuleWiseCounters List by country
    public List<ModuleCounter> getModuleCountersForCountry(BigInteger countryId) {
        Query query = new Query(Criteria.where("countryId").is(countryId));
        return mongoTemplate.find(query, ModuleCounter.class);
    }

    //get modulewise countersIds for a country
    public List<ModuleCounterGroupingDTO> getModuleCounterDTOsForCountry(BigInteger countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("countryId").is(countryId)),
                Aggregation.group("moduleId").addToSet("counterId").as("counterIds"),
                Aggregation.project("counterIds")
        );
        AggregationResults<ModuleCounterGroupingDTO> results = mongoTemplate.aggregate(aggregation, ModuleCounter.class, ModuleCounterGroupingDTO.class);
        return results.getMappedResults();
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

    public List<BigInteger> getModuleCountersIds(List<BigInteger> refCounterIds, String moduleId, BigInteger countryId){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("countryId").is(countryId).and("moduleId").is(moduleId).and("counterId").in(refCounterIds)),
                Aggregation.group("moduleId").addToSet("_id").as("ids"),
                Aggregation.project("ids")
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, ModuleCounter.class, Map.class);
        List<Map> resultData = results.getMappedResults();
        List ids = new ArrayList();
        if(!resultData.isEmpty() && ((Map)resultData.get(0)).get("ids") != null) {
            ids = (List)((Map)resultData.get(0)).get("ids");
        }
        return ids;
    }

    //removal of counters

    public void removeAll(String fieldName, List values, Class claz){
        Query query = new Query(Criteria.where(fieldName).in(values));
        mongoTemplate.remove(query, claz);
    }

    public void removeRoleCounters(BigInteger roleId, List<BigInteger> refCounterIds){
        Query query = new Query(Criteria.where("roleId").is(roleId).and("refCounterId").in(refCounterIds));
        mongoTemplate.remove(query, UnitRoleCounter.class);
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


    //counterRef and counter type mapping
    public List<RefCounterDefDTO> getModuleCounterDetails(String moduleId, BigInteger countryId){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("moduleId").is(moduleId).and("countryId").is(countryId)),
                Aggregation.lookup("counter", "counterId","_id","counterType"),
                Aggregation.project().and("counterType").arrayElementAt(0).as("counterType")
        );

        AggregationResults<RefCounterDefDTO> results = mongoTemplate.aggregate(aggregation, ModuleCounter.class, RefCounterDefDTO.class);
        return results.getMappedResults();
    }

    public List<RefCounterDefDTO> getRoleCounterTypeDetails(BigInteger roleId, BigInteger unitId, String moduleId){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("roleId").is(roleId).and("unitId").is(unitId)),
                Aggregation.lookup("moduleCounter","refCounterId", "_id", "refCounter"),
                Aggregation.project().and("refCounter").arrayElementAt(0).as("refCounter"),
                Aggregation.match(Criteria.where("refCounter.moduleId").is(moduleId)),
                Aggregation.lookup("counter", "refCounter.counterId", "_id","counterDef" ),
                Aggregation.project().and("counterDef").arrayElementAt(0).as("counterDef"),
                Aggregation.project().and("counterDef.type").as("counterType")
        );

        AggregationResults<RefCounterDefDTO> results = mongoTemplate.aggregate(aggregation, UnitRoleCounter.class, RefCounterDefDTO.class);
        return results.getMappedResults();
    }


    // order counters list for tab
    public List<CounterOrderDTO> getOrderedCountersListForCountry(BigInteger countryId, String moduleId){
        Criteria criteria = Criteria.where("countryId").is(countryId).and("moduleId").is(moduleId);
        if(moduleId == null)
            criteria = Criteria.where("countryId").is(countryId);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria)
        );
        AggregationResults<CounterOrderDTO> results = mongoTemplate.aggregate(aggregation, DefaultCounterOrder.class, CounterOrderDTO.class);
        return results.getMappedResults();
    }

    public List<CounterOrderDTO> getOrderedCountersListForUnit(BigInteger unitId, String moduleId){
        Criteria criteria = Criteria.where("unitId").is(unitId).and("moduleId").is(moduleId);
        if(moduleId == null)
            criteria = Criteria.where("unitId").is(unitId);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("unitId").is(unitId).and("moduleId").is(moduleId))
        );
        AggregationResults<CounterOrderDTO> results = mongoTemplate.aggregate(aggregation, UnitCounterOrder.class, CounterOrderDTO.class);
        return results.getMappedResults();
    }

    public List<CounterOrderDTO> getOrderedCountersListForUser(BigInteger unitId, BigInteger staffId, String moduleId){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("unitId").is(unitId).and("staffId").is(staffId).and("moduleId").is(moduleId))
        );
        AggregationResults<CounterOrderDTO> results = mongoTemplate.aggregate(aggregation, UserCounterOrder.class, CounterOrderDTO.class);
        return results.getMappedResults();
    }

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

    //CounterCategories crud

    public List<KPICategory> getCategoriesByNames(List<String> names){
        Query query = new Query();
        query.addCriteria(Criteria.where("name").in(names).and("deleted").is(false));
        return mongoTemplate.find(query, KPICategory.class);
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

    public List<CounterDTO> getAllCounterApplicableFor(ApplicableFor applicableFor){
        Query query = new Query(Criteria.where("applicableFor").in(applicableFor).and("deleted").is(false));
        query.fields().include("id").include("title");
        return ObjectMapperUtils.copyProperties(mongoTemplate.find(query,Counter.class),CounterDTO.class);
    }
}
