package com.kairos.persistence.repository.activity;

import com.kairos.persistence.model.activity.ActivityPriority;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class ActivityPriorityMongoRepositoryImpl implements CustomActivityPriorityMongoRepository{

    @Inject private MongoTemplate mongoTemplate;

    @Override
    public List<ActivityPriority> findLastSeqenceByOrganizationIds(List<Long> organizationIds){
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("organizationId").in(organizationIds).and("deleted").is(false)),
                group("organizationId").last("sequence").as("sequence"),
                project("sequence").and("_id").as("organizationId")

        );
        return mongoTemplate.aggregate(aggregation,ActivityPriority.class,ActivityPriority.class).getMappedResults();
    }


    @Override
    public boolean existsByNameAndCountryIdAndNotEqualToId(String name, BigInteger id,Long countryId){
        Assert.notNull(name, "The given name must not be null!");
        Assert.notNull(id, "The given id must not be null!");
        Criteria criteria = Criteria.where("countryId").is(countryId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and("deleted").is(false);
        if(isNotNull(id)){
            criteria.and("_id").ne(id);
        }
        return mongoTemplate.exists(new Query(criteria),ActivityPriority.class);
    }

    @Override
    public boolean existsByNameAndOrganizationIdAndNotEqualToId(String name, BigInteger id,Long organizationId){
        Assert.notNull(name, "The given name must not be null!");
        Assert.notNull(id, "The given id must not be null!");
        Criteria criteria = Criteria.where("organizationId").is(organizationId).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and("deleted").is(false);
        if(isNotNull(id)){
            criteria.and("_id").ne(id);
        }
        return mongoTemplate.exists(new Query(criteria),ActivityPriority.class);
    }
}
