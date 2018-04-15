package com.kairos.activity.persistence.repository.kpi;

import com.kairos.activity.persistence.enums.kpi.KpiType;
import com.kairos.activity.persistence.model.kpi.KPI;
import com.kairos.activity.persistence.model.kpi.ModuleWiseKpi;
import com.kairos.activity.persistence.model.kpi.UnitRoleWiseKpi;
import com.kairos.activity.response.dto.kpi.ModulewiseKpiGroupingDTO;
import com.kairos.activity.response.dto.kpi.RolewiseKpiDTO;
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
public class KpiRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    //get Kpi by type
    public KPI getKpiByType(KpiType type) {
        Query query = new Query(Criteria.where("type").is(type));
        return mongoTemplate.findOne(query, KPI.class);
    }


    //get ModuleWiseKpis List by country
    public List<ModuleWiseKpi> getModulewiseKpisForCountry(BigInteger countryId) {
        Query query = new Query(Criteria.where("countryId").is(countryId));
        return mongoTemplate.find(query, ModuleWiseKpi.class);
    }

    //get modulewise KpisIds for a country
    public List<ModulewiseKpiGroupingDTO> getModulewiseKpiDTOsForCountry(BigInteger countryId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("countryId").is(countryId)),
                Aggregation.group("moduleId").addToSet("kpiId").as("kpiIds"),
                Aggregation.project("kpiIds")
        );
        AggregationResults<ModulewiseKpiGroupingDTO> results = mongoTemplate.aggregate(aggregation, ModuleWiseKpi.class, ModulewiseKpiGroupingDTO.class);
        return results.getMappedResults();
    }

    //get role and moduleKpiId mapping for unit
    public List<RolewiseKpiDTO> getRoleAndModuleKpiIdMapping(BigInteger unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("unitId").is(unitId)),
                Aggregation.group("roleId").addToSet("modulewiseKpiId").as("modulewiseKpiIds"),
                Aggregation.project("modulewiseKpiIds")

        );

        AggregationResults<RolewiseKpiDTO> results = mongoTemplate.aggregate(aggregation, UnitRoleWiseKpi.class, RolewiseKpiDTO.class);
        return results.getMappedResults();
    }


    //public void setCustomKpiSetting


    /// old code

    //getKpiModuleLink
    public ModuleWiseKpi getKpiModuleLink(String moduleId, BigInteger KpiDefinitionId) {
        Assert.notNull(moduleId, "Module Id can't be null!");
        Query query = new Query(Criteria.where("moduleId").is(moduleId).and("KpiDefinitionId").is(KpiDefinitionId));
        ModuleWiseKpi moduleWiseKpi = mongoTemplate.findOne(query, ModuleWiseKpi.class);
        return moduleWiseKpi;
    }

    //deleteModuleWiseKpi
    public void deleteKpiModuleLink(BigInteger moduleId, BigInteger kpiId) {
        Query query = new Query(Criteria.where("moduleId").is(moduleId).and("kpiId").is(kpiId));
        mongoTemplate.findAllAndRemove(query, ModuleWiseKpi.class);
    }

    public void removeAccessiblitiesById(List<BigInteger> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        mongoTemplate.findAllAndRemove(query, UnitRoleWiseKpi.class);
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
    //getKpiListByType for testcases
    public List getEntityItemList(Class claz){
        return mongoTemplate.findAll(claz);
    }


    public void removeCustomKpiProfiles(List<BigInteger> accessiblityIds) {
        Query query = new Query(Criteria.where("_id").in(accessiblityIds));
    }
}
