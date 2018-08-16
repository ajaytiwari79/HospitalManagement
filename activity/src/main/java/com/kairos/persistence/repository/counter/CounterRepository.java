package com.kairos.persistence.repository.counter;

import com.kairos.activity.counter.ApplicableKPIDTO;
import com.kairos.activity.counter.KPICategoryDTO;
import com.kairos.activity.counter.KPIDTO;
import com.kairos.activity.counter.distribution.access_group.AccessGroupMappingDTO;
import com.kairos.activity.counter.distribution.access_group.RoleCounterDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIMappingDTO;
import com.kairos.activity.counter.distribution.category.CategoryKPIsDTO;
import com.kairos.activity.counter.distribution.org_type.OrgTypeMappingDTO;
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
import java.util.Optional;

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
    public Object getEntityById(BigInteger id, Class claz){
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, claz);
    }

    //remove item by Id
    public void removeEntityById(BigInteger id, Class claz){
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
    public List<ApplicableKPIDTO> getCounterListForCountryOrUnitOrStaff(Long refId, ConfLevel level){
        String refQueryField = getRefQueryField(level);
        Aggregation aggregation=Aggregation.newAggregation(
          Aggregation.match(Criteria.where(refQueryField).is(refId)),
          Aggregation.lookup("counter","activeKpiId","_id","kpiIds"),
           Aggregation.project("level").and("kpiIds").arrayElementAt(0).as("kpiIds")
        );
        AggregationResults<ApplicableKPIDTO> results = mongoTemplate.aggregate(aggregation,ApplicableKPI.class,ApplicableKPIDTO.class);
        return results.getMappedResults();
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
    public List<ApplicableKPI> getApplicableKPI(List<BigInteger> kpiIds, ConfLevel level, Long refId){
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("activeKpiId").in(kpiIds).and(refQueryField).is(refId));
        return mongoTemplate.find(query, ApplicableKPI.class);
    }

    //category CRUD

    public List<KPICategory> getKPICategoryByIds(List<BigInteger> categoryIds, ConfLevel level, Long refId){
        String queryField = (ConfLevel.COUNTRY.equals(level)) ? "countryId" : "unitId";
        Query query=new Query(Criteria.where("_id").in(categoryIds).and(queryField).is(refId));
        return mongoTemplate.find(query,KPICategory.class);

    }

    public List<KPICategoryDTO> getKPICategory(List<BigInteger> categoryIds, ConfLevel level, Long refId){
        String queryField = (ConfLevel.COUNTRY.equals(level)) ? "countryId" : "unitId";
        Criteria matchCriteria = categoryIds == null ? Criteria.where(queryField).is(refId) : Criteria.where("_id").in(categoryIds).and(queryField).is(refId);
        Query query=new Query(matchCriteria);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query,KPICategory.class),KPICategoryDTO.class);
    }

    public List<CategoryAssignment> getCategoryAssignment(BigInteger categoryId, ConfLevel level, Long refId){
        String queryField = (ConfLevel.COUNTRY.equals(level))? "countryId":"unitId";
        Query query = new Query(Criteria.where(queryField).is(refId).and("categoryId").is(categoryId));
        return mongoTemplate.find(query, CategoryAssignment.class);
    }

    public List<CategoryAssignment> findCategoryAssignment(List<BigInteger> kpiIds, ConfLevel level, Long refId){
        String queryField = (ConfLevel.COUNTRY.equals(level))? "countryId":"unitId";
        Aggregation aggregation=Aggregation.newAggregation(
          Aggregation.match(Criteria.where("kpiId").in(kpiIds)),
          Aggregation.lookup("categoryAssignment","categoryAssignmentId","_id","categoryAssignment"),
          Aggregation.match(Criteria.where("categoryAssignment."+queryField).is(refId)),
          Aggregation.project().and("categoryAssignment.categoryId").as("categoryId").and("categoryAssignment."+queryField).as(queryField)
                .and("categoryAssignment._id").as("_id")
        );
        AggregationResults<CategoryAssignment> results=mongoTemplate.aggregate(aggregation,CategoryKPIConf.class,CategoryAssignment.class);
        return results.getMappedResults();
    }

    public void removeCategoryKPIEntries(List<BigInteger> categoryAssignmentIds,List<BigInteger> kpiIds){
        Query query=new Query(Criteria.where("categoryId").in(categoryAssignmentIds).and("kpiId").in(kpiIds));
        mongoTemplate.remove(query, CategoryKPIConf.class);
    }

    //CategoryKPI distribution

    public List<CategoryKPIConf> getCategoryKPIConfs(List<BigInteger> kpiIds,List<BigInteger> categoryIds){
        Query query = new Query(Criteria.where("kpiId").in(kpiIds).and("categoryId").in(categoryIds));
        return mongoTemplate.find(query, CategoryKPIConf.class);
    }

    public List<CategoryKPIMappingDTO> getKPIsMappingForCategories(List<BigInteger> categoryIds){
        Aggregation ag = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("categoryId").in(categoryIds))
                , Aggregation.group("categoryId").push("kpiId").as("kpiIds")
                , Aggregation.project().and("_id").as("categoryId").and("kpiIds").as("kpiId")
        );
        AggregationResults<CategoryKPIMappingDTO> results = mongoTemplate.aggregate(ag, CategoryKPIConf.class, CategoryKPIMappingDTO.class);
        return results.getMappedResults();
    }



    //tabKPI distribution crud

    public List<TabKPIMappingDTO> getTabKPIConfigurationByTabIds(List<String> tabIds,List<BigInteger> kpiIds, Long refId, ConfLevel level){
        String refQueryField = getRefQueryField(level);
        Query query=null;
        if(kpiIds.isEmpty()) {
            query=new Query(Criteria.where("tabId").in(tabIds).and(refQueryField).is(refId));
        }else{
           query=new Query(Criteria.where("tabId").in(tabIds).and("kpiId").in(kpiIds).and(refQueryField).is(refId));
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query,TabKPIConf.class),TabKPIMappingDTO.class);
    }

    public List<TabKPIConf> findTabKPIConfigurationByTabIds(List<BigInteger> ids){
        Query query=new Query(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query,TabKPIConf.class);
    }

    public void removeTabKPIConfiguration(TabKPIMappingDTO entry,Long refId,ConfLevel level){
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where("tabId").is(entry.getTabId()).and("kpiId").is(entry.getKpiId()).and(refQueryField).is(refId));
        mongoTemplate.remove(query, TabKPIConf.class);
    }


    //accessGroupKPI distribution crud

    public List<AccessGroupKPIEntry> getAccessGroupKPIByUnitIdAndKpiIds(List<BigInteger> kpiIds,Long unitId){
        Query query = new Query(Criteria.where("kpiId").in(kpiIds).and("unitId").is(unitId));
        return mongoTemplate.find(query, AccessGroupKPIEntry.class);
    }

    public AccessGroupKPIEntry getAccessGroupKPIEntry(AccessGroupMappingDTO entry,Long unitId){
        Query query = new Query(Criteria.where("accessGroupId").is(entry.getAccessGroupId()).and("kpiId").is(entry.getKpiId()).and("unitId").is(unitId));
       return mongoTemplate.findOne(query, AccessGroupKPIEntry.class);
    }

    public List<OrgTypeKPIEntry> getOrgTypeKPIConfigurationByOrgTypeId(List<Long> accessGroupIds){
        Query query = new Query(Criteria.where("orgTypeId").in(accessGroupIds));
        return mongoTemplate.find(query, OrgTypeKPIEntry.class);
    }

    public boolean removeAccessGroupKPIEntry(List<Long> unitIds,BigInteger kpiId){
        Query query = new Query(Criteria.where("unitId").in(unitIds).and("kpiId").is(kpiId));
        mongoTemplate.remove(query, AccessGroupKPIEntry.class);
        return true;
    }

    public boolean removeAccessGroupKPIEntryForCountry(AccessGroupMappingDTO entry,Long refId){
        Query query = new Query(Criteria.where("accessGroupId").is(entry.getAccessGroupId()).and("kpiId").is(entry.getKpiId()).and("countryId").is(refId));
        mongoTemplate.remove(query, AccessGroupKPIEntry.class);
        return true;
    }

    public boolean removeApplicableKPI(List<Long> refIds,BigInteger kpiId,ConfLevel level){
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where(refQueryField).in(refIds).and("activeKpiId").is(kpiId));
        mongoTemplate.remove(query, ApplicableKPI.class);
        return true;
    }

    public boolean removeTabKPIEntry(List<Long> refIds,BigInteger kpiId,ConfLevel level){
        String refQueryField = getRefQueryField(level);
        Query query = new Query(Criteria.where(refQueryField).in(refIds).and("kpiId").is(kpiId));
        mongoTemplate.remove(query, TabKPIConf.class);
        return true;
    }


    public List<ApplicableKPI> getKPIAssignmentsByKPIId(List<BigInteger> kpiIds,Long refId,ConfLevel level){
        String refQueryField = getRefQueryField(level);
        Query query=new Query(Criteria.where(refQueryField).is(refId).and("activeKpiId").in(kpiIds));
        return mongoTemplate.find(query,ApplicableKPI.class);
    }

    public OrgTypeKPIEntry getOrgTypeKPIEntry(OrgTypeMappingDTO entry,Long countryId){
        Query query = new Query(Criteria.where("orgTypeId").is(entry.getOrgTypeId()).and("kpiId").is(entry.getKpiId()).and("countryId").is(countryId));
        return mongoTemplate.findOne(query, OrgTypeKPIEntry.class);
    }


    public List<OrgTypeMappingDTO> getOrgTypeKPIEntryOrgTypeIds(List<Long> orgTypeIds,List<BigInteger> kpiIds ,Long countryId){
        Query query=null;
        if(kpiIds.isEmpty()){
            query = new Query(Criteria.where("orgTypeId").in(orgTypeIds));
        }else{
            query = new Query(Criteria.where("orgTypeId").in(orgTypeIds).and("kpiId").in(kpiIds));
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query,OrgTypeKPIEntry.class),OrgTypeMappingDTO.class);
    }

    public List<AccessGroupMappingDTO> getAccessGroupKPIEntryAccessGroupIds(List<Long> accessGroupIds ,List<BigInteger> kpiIds, ConfLevel level, Long refId){
        String queryField = (ConfLevel.COUNTRY.equals(level)) ? "countryId" : "unitId";
        Query query=null;
        if(kpiIds.isEmpty()){
            query=new Query(Criteria.where("accessGroupId").in(accessGroupIds).and(queryField).is(refId));
        }else{
            query=new Query(Criteria.where("accessGroupId").in(accessGroupIds).and("kpiId").in(kpiIds).and(queryField).is(refId));
        }
        return ObjectMapperUtils.copyPropertiesOfListByMapper(mongoTemplate.find(query,AccessGroupKPIEntry.class),AccessGroupMappingDTO.class);
    }



    public List<ApplicableKPI> getApplicableKPIByUnitOrCountryOrStaffId(Long refId, ConfLevel level){
        String refQueryField = getRefQueryField(level);
        Query query=new Query(Criteria.where(refQueryField).is(refId));
        return mongoTemplate.find(query,ApplicableKPI.class);
    }

    public List<TabKPIConf> findTabKPIIdsByKpiIdAndUnitOrCountry(List<BigInteger> kpiIds,Long refid,ConfLevel level){
        String refQueryField = getRefQueryField(level);
        Query query=new Query(Criteria.where(refQueryField).is(refid).and("KpiId").in(kpiIds));
        return mongoTemplate.find(query,TabKPIConf.class);
    }
}
