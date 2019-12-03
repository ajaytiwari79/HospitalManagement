package com.kairos.persistence.repository.activity;

import com.kairos.persistence.model.activity.ActivityPriority;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Pattern;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class ActivityPriorityMongoRepositoryImpl implements CustomActivityPriorityMongoRepository{

    public static final String ORGANIZATION_ID = "organizationId";
    public static final String DELETED = "deleted";
    public static final String SEQUENCE = "sequence";
    @Inject private MongoTemplate mongoTemplate;

    @Override
    public List<ActivityPriority> findLastSeqenceByOrganizationIds(List<Long> organizationIds){
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where(ORGANIZATION_ID).in(organizationIds).and(DELETED).is(false)),
                group(ORGANIZATION_ID).last(SEQUENCE).as(SEQUENCE),
                project(SEQUENCE).and("_id").as(ORGANIZATION_ID)

        );
        return mongoTemplate.aggregate(aggregation,ActivityPriority.class,ActivityPriority.class).getMappedResults();
    }


    @Override
    public boolean existsByNameAndCountryIdAndNotEqualToId(String name,String colorCode, BigInteger id,Long countryId){
        Criteria criteria = Criteria.where("countryId").is(countryId).and(DELETED).is(false).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE));
        if(isNotNull(id)){
            criteria.and("_id").ne(id);
        }
        return mongoTemplate.exists(new Query(criteria),ActivityPriority.class);
    }

    @Override
    public boolean existsByNameAndOrganizationIdAndNotEqualToId(String name,String colorCode, BigInteger id,Long organizationId){
        Criteria criteria = Criteria.where(ORGANIZATION_ID).is(organizationId).and(DELETED).is(false).and("name").regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE));
        if(isNotNull(id)){
            criteria.and("_id").ne(id);
        }
        return mongoTemplate.exists(new Query(criteria),ActivityPriority.class);
    }

    @Override
    public void updateSequenceOfActivityPriorityOnCountry(int oldSequence,int newSequence,Long countryId){
        Update update = new Update();
        update.set(SEQUENCE,newSequence);
        mongoTemplate.updateFirst(new Query(Criteria.where(SEQUENCE).is(oldSequence).and("countryId").is(countryId)),update,ActivityPriority.class);
    }

    @Override
    public void updateSequenceOfActivityPriorityOnOrganization(int oldSequence,int newSequence,Long unitId){
        Update update = new Update();
        update.set(SEQUENCE,newSequence);
        mongoTemplate.updateFirst(new Query(Criteria.where(SEQUENCE).is(oldSequence).and(ORGANIZATION_ID).is(unitId)),update,ActivityPriority.class);
    }
}
