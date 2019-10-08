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

    public static final String OPEN_SHIFT_INTERVAL = "openShiftInterval";
    @Inject
    private MongoTemplate mongoTemplate;

 public List<OpenShiftRuleTemplateDTO> findOpenShiftRuleTemplatesWithInterval(Long unitId) {

    Aggregation aggregation =  Aggregation.newAggregation(
             match(Criteria.where("deleted").is(false).and("unitId").is(unitId)),
             lookup(OPEN_SHIFT_INTERVAL, "openShiftIntervalId", "_id",
                     OPEN_SHIFT_INTERVAL),
             project("$id","activitiesPerTimeTypes","selectedSkills").and(OPEN_SHIFT_INTERVAL).arrayElementAt(0).as(OPEN_SHIFT_INTERVAL));
     AggregationResults<OpenShiftRuleTemplateDTO> result =  mongoTemplate.aggregate(aggregation, OpenShiftRuleTemplate.class, OpenShiftRuleTemplateDTO.class);


     return result.getMappedResults();
 }
}
