package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.model.client_aggregator.ClientAggregator;
import com.kairos.persistence.repository.client_aggregator.CustomClientAggregatorRepository;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.mongodb.*;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

/**
 * Created by oodles on 9/8/17.
 */
@Repository
public class ClientAggregatorMongoRepositoryImpl implements CustomClientAggregatorRepository {

    @Inject
    private MongoTemplate mongoTemplate;
    @Inject
    DB database;

   public List<Map> getCitizenAggregationData(Long unitId){
       String group = "{'$group':{'_id':'$citizenId', 'citizenData':{'$push':\"$$ROOT\"}}}";

       Document groupObject = Document.parse(group);
       Criteria criteria = Criteria.where("unitId").is(unitId);
       Aggregation aggregation = newAggregation(
               match(criteria),
               new CustomAggregationOperation(groupObject)
       );

       // Result
       AggregationResults<Map> finalResult = mongoTemplate.aggregate(aggregation, ClientAggregator.class, Map.class);
       return finalResult.getMappedResults();
    }

    public DBCursor fetchAllClientAggregationData(){
       DBCollection collection= database.getCollection("clientAggregator");
        return collection.find();
    }

    public long getCountOfAggregateData(long unitId){
      Query query = Query.query(Criteria.where("unitId").is(unitId).and("clientExceptionCounts").exists(true));
      return mongoTemplate.count(query,ClientAggregator.class);
    }

    @Override
    public List<ClientAggregator> getAggregateDataByUnit(long unitId,int skip,int limit) {
        Query query = new Query().addCriteria(Criteria.where("unitId").is(unitId).and("clientExceptionCounts").exists(true));
        query.fields().include("citizenId").include("clientExceptionCounts");
        query.skip(skip).limit(limit);
        query.with(new Sort(Sort.DEFAULT_DIRECTION,"citizenId"));
        return mongoTemplate.find(query,ClientAggregator.class,"clientAggregator");
    }


}
