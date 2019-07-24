package com.kairos.persistence.repository.activity;

import com.kairos.persistence.model.activity.ActivityPriority;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.*;

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
    public boolean existsByNameAndCountryIdAndNotEqualToId(String name,String colorCode, BigInteger id,Long countryId){
        Criteria criteria = Criteria.where("countryId").is(countryId).and("deleted").is(false).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE));
        if(isNotNull(id)){
            criteria.and("_id").ne(id);
        }
        return mongoTemplate.exists(new Query(criteria),ActivityPriority.class);
    }

    @Override
    public boolean existsByNameAndOrganizationIdAndNotEqualToId(String name,String colorCode, BigInteger id,Long organizationId){
        Criteria criteria = Criteria.where("organizationId").is(organizationId).and("deleted").is(false).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE));
        if(isNotNull(id)){
            criteria.and("_id").ne(id);
        }
        return mongoTemplate.exists(new Query(criteria),ActivityPriority.class);
    }

    @Override
    public void updateSequenceOfActivityPriorityOnCountry(int oldSequence,int newSequence,Long countryId){
        Update update = new Update();
        update.set("sequence",newSequence);
        mongoTemplate.updateFirst(new Query(Criteria.where("sequence").is(oldSequence).and("countryId").is(countryId)),update,ActivityPriority.class);
    }

    @Override
    public void updateSequenceOfActivityPriorityOnOrganization(int oldSequence,int newSequence,Long unitId){
        Update update = new Update();
        update.set("sequence",newSequence);
        mongoTemplate.updateFirst(new Query(Criteria.where("sequence").is(oldSequence).and("organizationId").is(unitId)),update,ActivityPriority.class);
    }
}
