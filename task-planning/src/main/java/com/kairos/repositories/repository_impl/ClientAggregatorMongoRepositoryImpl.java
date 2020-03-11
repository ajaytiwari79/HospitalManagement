package com.kairos.repositories.repository_impl;

import com.kairos.persistence.model.client_aggregator.ClientAggregator;
import com.kairos.repositories.client_aggregator.CustomClientAggregatorRepository;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
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

/**
 * Created by oodles on 9/8/17.
 */
@Repository
public class ClientAggregatorMongoRepositoryImpl implements CustomClientAggregatorRepository {

    public static final String UNIT_ID = "unitId";
    public static final String CLIENT_EXCEPTION_COUNTS = "clientExceptionCounts";
    @Inject
    private MongoTemplate mongoTemplate;
    @Inject
    DB database;

   public List<Map> getCitizenAggregationData(Long unitId){
       String group = "{'$group':{'_id':'$citizenId', 'citizenData':{'$push':\"$$ROOT\"}}}";

       Document groupObject = Document.parse(group);
       Criteria criteria = Criteria.where(UNIT_ID).is(unitId);
       Aggregation aggregation = Aggregation.newAggregation(
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
      Query query = Query.query(Criteria.where(UNIT_ID).is(unitId).and(CLIENT_EXCEPTION_COUNTS).exists(true));
      return mongoTemplate.count(query,ClientAggregator.class);
    }

    @Override
    public List<ClientAggregator> getAggregateDataByUnit(long unitId,int skip,int limit) {
        Query query = new Query().addCriteria(Criteria.where(UNIT_ID).is(unitId).and(CLIENT_EXCEPTION_COUNTS).exists(true));
        query.fields().include("citizenId").include(CLIENT_EXCEPTION_COUNTS);
        query.skip(skip).limit(limit);
        query.with(Sort.by(Sort.DEFAULT_DIRECTION,"citizenId"));
        return mongoTemplate.find(query,ClientAggregator.class,"clientAggregator");
    }


}
