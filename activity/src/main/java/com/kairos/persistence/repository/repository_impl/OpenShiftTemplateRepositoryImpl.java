package com.kairos.persistence.repository.repository_impl;

import com.kairos.activity.activity.OrganizationActivityDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplate;
import com.kairos.persistence.model.open_shift.OpenShiftRuleTemplateDTO;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.List;

import static com.kairos.enums.TimeTypes.WORKING_TYPE;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

public class OpenShiftTemplateRepositoryImpl {

    @Inject
    private MongoTemplate mongoTemplate;

 public List<OpenShiftRuleTemplateDTO> findOpenShiftRuleTemplatesWithIntervalByUnitId(Long unitId) {

    Aggregation aggregation =  Aggregation.newAggregation(
             match(Criteria.where("unitId").is(unitId).and("deleted").is(false)),
             lookup("opneShiftInterval", "openShiftIntervalId", "_id",
                     "openShiftInterval"),
             project("$id","activitiesPerTimeTypes","selectedSkills").and("openShiftInterval").arrayElementAt(0).as("openShiftInterval"));
     AggregationResults<OpenShiftRuleTemplateDTO> result =  mongoTemplate.aggregate(aggregation, OpenShiftRuleTemplate.class, OpenShiftRuleTemplateDTO.class);


     return result.getMappedResults();
 }
}
