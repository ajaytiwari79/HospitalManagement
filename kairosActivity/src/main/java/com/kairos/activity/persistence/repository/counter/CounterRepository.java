package com.kairos.activity.persistence.repository.counter;

import com.kairos.activity.persistence.enums.counter.CounterType;
import com.kairos.activity.persistence.model.counter.Counter;
import com.kairos.activity.persistence.model.counter.UnitRoleWiseCounter;
import com.kairos.activity.persistence.model.counter.ModuleWiseCounter;
import com.kairos.activity.response.dto.counter.*;
import io.jsonwebtoken.lang.Assert;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

@Repository
public class CounterRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    //get counter by type
    public Counter getCounterByType(CounterType type) {
        Query query = new Query(Criteria.where("type").is(type));
        return mongoTemplate.findOne(query, Counter.class);
    }


    //get ModuleWiseCounters List by country
    public List<ModuleWiseCounter> getModulewiseCountersForCountry(BigInteger countryId) {
        Query query = new Query(Criteria.where("countryId").is(countryId));
        return mongoTemplate.find(query, ModuleWiseCounter.class);
    }

    //get modulewise countersIds for a country
    public List<ModulewiseCounterGroupingDTO> getModulewiseCounterDTOsForCountry(BigInteger countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("countryId").is(countryId)),
                Aggregation.group("moduleId").addToSet("counterId").as("counterIds"),
                Aggregation.project("counterIds")
        );
        AggregationResults<ModulewiseCounterGroupingDTO> results = mongoTemplate.aggregate(aggregation, ModuleWiseCounter.class, ModulewiseCounterGroupingDTO.class);
        return results.getMappedResults();
    }

    //get role and moduleCounterId mapping for unit
    public List<RolewiseCounterDTO> getRoleAndModuleCounterIdMapping(BigInteger unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("unitId").is(unitId)),
                Aggregation.group("roleId").addToSet("modulewiseCounterId").as("modulewiseCounterIds"),
                Aggregation.project("modulewiseCounterIds")

        );

        AggregationResults<RolewiseCounterDTO> results = mongoTemplate.aggregate(aggregation, UnitRoleWiseCounter.class, RolewiseCounterDTO.class);
        return results.getMappedResults();
    }


    //public void setCustomCounterSetting


    /// old code

    //getCounterModuleLink
    public ModuleWiseCounter getCounterModuleLink(String moduleId, BigInteger counterDefinitionId) {
        Assert.notNull(moduleId, "Module Id can't be null!");
        Query query = new Query(Criteria.where("moduleId").is(moduleId).and("counterDefinitionId").is(counterDefinitionId));
        ModuleWiseCounter moduleWiseCounter = mongoTemplate.findOne(query, ModuleWiseCounter.class);
        return moduleWiseCounter;
    }

    //deleteModuleWiseCounter
    public void deleteCounterModuleLink(BigInteger moduleId, BigInteger counterDefinitionId) {
        Query query = new Query(Criteria.where("moduleId").is(moduleId).and("counterDefinitionId").is(counterDefinitionId));
        mongoTemplate.findAllAndRemove(query, ModuleWiseCounter.class);
    }

    public void removeAccessiblitiesById(List<BigInteger> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        mongoTemplate.findAllAndRemove(query, UnitRoleWiseCounter.class);
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

    //test cases..
    //getCounterListByType for testcases
    public List getEntityItemList(Class claz){
        return mongoTemplate.findAll(claz);
    }


    public void removeCustomCounterProfiles(List<BigInteger> accessiblityIds) {
        Query query = new Query(Criteria.where("_id").in(accessiblityIds));
    }
}
