package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.model.tag.Tag;
import com.kairos.persistence.repository.tag.CustomTagMongoRepository;
import com.kairos.dto.user.country.tag.TagDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

/**
 * Created by prerna on 7/12/17.
 */
@Repository
public class TagMongoRepositoryImpl implements CustomTagMongoRepository {

    @Inject
    private MongoTemplate mongoTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TagMongoRepositoryImpl.class);


    public List<TagDTO> getTagsById(List<BigInteger> tagIds){
        logger.info("==============> tagIds : "+tagIds);
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("deleted").is(false).and("id").in(tagIds))
//                project("id","name","countryTag")
        );

        AggregationResults<TagDTO> result = mongoTemplate.aggregate(aggregation, Tag.class,TagDTO.class);
        return result.getMappedResults();
    }


}
