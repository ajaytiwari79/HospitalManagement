package com.kairos.activity.persistence.repository.counter;

import com.kairos.activity.persistence.enums.counter.CounterLevel;
import com.kairos.activity.persistence.enums.counter.CounterType;
import com.kairos.activity.persistence.model.counter.CounterAccessiblity;
import com.kairos.activity.persistence.model.counter.CounterDefinition;
import com.kairos.activity.persistence.model.counter.CounterModuleLink;
import com.kairos.activity.persistence.model.counter.CustomCounterSettings;
import com.kairos.activity.response.dto.counter.AvailableCounters;
import com.kairos.activity.response.dto.counter.CounterAccessiblityDTO;
import com.kairos.activity.response.dto.counter.CounterModuleLinkDTO;
import com.kairos.activity.response.dto.counter.CustomCounterSettingDTO;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CounterRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    //get counter by type
    public CounterDefinition getCounterByType(CounterType type){
        Query query = new Query(Criteria.where("type").is(type));
        return mongoTemplate.findOne(query, CounterDefinition.class);
    }

    //getDefaultCountersListForModule
    public List<CounterModuleLinkDTO> getCounterModuleLinks(String moduleId){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("moduleId").is(moduleId)),
                Aggregation.lookup("counterDefinition", "counterDefinitionId", "_id", "counterDefinition"),
                Aggregation.project("moduleId").and("counterDefinition").arrayElementAt(0).as("counterDefinition")
        );
        AggregationResults<CounterModuleLinkDTO> results = mongoTemplate.aggregate(aggregation, CounterModuleLink.class, CounterModuleLinkDTO.class);
        return results.getMappedResults();
    }

    //getCounterModuleLink
    public CounterModuleLink getCounterModuleLink(String moduleId, BigInteger counterDefinitionId){
        Assert.notNull(moduleId, "Module Id can't be null!");
        Query query = new Query(Criteria.where("moduleId").is(moduleId).and("counterDefinitionId").is(counterDefinitionId));
        CounterModuleLink counterModuleLink = mongoTemplate.findOne(query, CounterModuleLink.class);
        return counterModuleLink;
    }

    //deleteCounterModuleLink
    public void deleteCounterModuleLink(BigInteger moduleId, BigInteger counterDefinitionId){
        Query query = new Query(Criteria.where("moduleId").is(moduleId).and("counterDefinitionId").is(counterDefinitionId));
        mongoTemplate.findAllAndRemove(query, CounterModuleLink.class);
    }

    public List<CounterAccessiblityDTO> getCounterAccessiblityList(BigInteger unitId, CounterLevel level){
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("accessLevel").is(level).and("unitId").is(unitId)),
            Aggregation.lookup("counterModuleLink", "counterModuleLinkId", "_id", "counterModule"),
            Aggregation.project("accessLevel").andInclude("unitId").and("counterModule").arrayElementAt(0).as("counterModule")
        );
        AggregationResults<CounterAccessiblityDTO> results = mongoTemplate.aggregate(aggregation, CounterAccessiblity.class, CounterAccessiblityDTO.class);
        return results.getMappedResults();
    }

    public void removeAccessiblitiesById(List<BigInteger> ids){
        Query query = new Query(Criteria.where("_id").in(ids));
        mongoTemplate.findAllAndRemove(query, CounterAccessiblity.class);
    }

    public void removeCustomCounterProfiles(List<BigInteger> accessiblityIds){
        Query query = new Query(Criteria.where("_id").in(accessiblityIds));
    }

    public List<CounterDefinition> getAccessableCountersList(Long moduleId){
        //get list of counter definitions.
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("moduleId").is(moduleId)),
            Aggregation.lookup("counterDefinition", "counterDefinitionId", "_id", "counterDefinition"),
            Aggregation.group("moduleId").addToSet("counterDefinition").as("counterDefinitions"),
            Aggregation.project("moduleId").andInclude("counterDefinitions")
        );
        AggregationResults<AvailableCounters> results = mongoTemplate.aggregate(aggregation, CounterDefinition.class, AvailableCounters.class);
        List<AvailableCounters> availableCounters = results.getMappedResults();
        return (availableCounters.isEmpty())?new ArrayList<CounterDefinition>():availableCounters.get(0).getCounterDefinitions();
    }

    //getList-configuredCounters
    public List<CustomCounterSettingDTO> getConfiguredCounters(BigInteger staffId){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("staffId").is(staffId)),
                Aggregation.lookup("counterAccessiblity", "counterAccessiblityId", "_id", "counterAccessiblity"),
                Aggregation.project("viewDefault").andInclude("level").andInclude("order").andInclude("configured").and("counterAccessiblity").arrayElementAt(0).as("counterAccessiblity")
        );
        AggregationResults<CustomCounterSettingDTO> results = mongoTemplate.aggregate(aggregation, CustomCounterSettings.class, CustomCounterSettingDTO.class);
        return results.getMappedResults();
    }

    //public void setCustomCounterSetting

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

    //test cases..
    //getCounterListByType for testcases
    public List getEntityItemList(Class claz){
        return mongoTemplate.findAll(claz);
    }
}
