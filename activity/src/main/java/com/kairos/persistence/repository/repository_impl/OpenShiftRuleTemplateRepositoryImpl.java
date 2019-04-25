package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplate;
import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplateDTO;
import com.kairos.persistence.repository.open_shift.CustomOpenShiftRuleTemplateRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class OpenShiftRuleTemplateRepositoryImpl implements CustomOpenShiftRuleTemplateRepository {

    @Inject
    private MongoTemplate mongoTemplate;

 public List<OpenShiftRuleTemplateDTO> findOpenShiftRuleTemplatesWithInterval(Long unitId) {

    Aggregation aggregation =  Aggregation.newAggregation(
             match(Criteria.where("deleted").is(false).and("unitId").is(unitId)),
             lookup("openShiftInterval", "openShiftIntervalId", "_id",
                     "openShiftInterval"),
             project("$id","activitiesPerTimeTypes","selectedSkills").and("openShiftInterval").arrayElementAt(0).as("openShiftInterval"));
     AggregationResults<OpenShiftRuleTemplateDTO> result =  mongoTemplate.aggregate(aggregation, OpenShiftRuleTemplate.class, OpenShiftRuleTemplateDTO.class);


     return result.getMappedResults();
 }
}
