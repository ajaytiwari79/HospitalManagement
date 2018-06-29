package com.kairos.persistence.repository.client_exception;
import com.kairos.persistence.model.client_exception.ClientException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by prabjot on 19/6/17.
 */
@Repository
public class ClientExceptionMongoRepositoryImpl implements CustomClientExceptionRepository{

    @Inject
    private MongoTemplate mongoTemplate;

    public List<ClientException> getExceptionBetweenTaskDates(long citizenId, Date taskStartTime, Date taskEndTime) {


        Query query = Query.query(Criteria.where("clientId").is(citizenId).orOperator(Criteria.where("fromTime").lte(taskEndTime).and("toTime").gte(taskEndTime),
                Criteria.where("fromTime").lte(taskStartTime).and("toTime").gte(taskStartTime)));
        return mongoTemplate.find(query, ClientException.class);

    }

    public boolean isExceptionExistBetweenDate(long citizenId,Date startTime,Date endTime,BigInteger clientExceptionId){

        Query query = Query.query(Criteria.where("clientId").is(citizenId).and("id").ne(clientExceptionId).orOperator(Criteria.where("fromTime").lte(endTime).and("toTime").gte(endTime),
                Criteria.where("fromTime").lte(startTime).and("toTime").gte(startTime)));
        return mongoTemplate.exists(query,ClientException.class);
    }


    public boolean isExceptionExistBetweenDate(List<Long> citizenIds, Date startTime, Date endTime, List<BigInteger> clientExceptionId) {
        Query query = Query.query(Criteria.where("clientId").in(citizenIds).and("id").nin(clientExceptionId).orOperator(Criteria.where("fromTime").lte(endTime).and("toTime").gte(endTime),
                Criteria.where("fromTime").lte(startTime).and("toTime").gte(startTime)));
        return mongoTemplate.exists(query,ClientException.class);
    }

    public boolean isExceptionTypeExistBetweenDate(List<Long> citizenId,Date startTime,Date endTime,BigInteger exceptionTypeId){

        Query query = Query.query(Criteria.where("clientId").in(citizenId).and("exceptionTypeId").is(exceptionTypeId).orOperator(Criteria.where("fromTime").lte(endTime).and("toTime").gte(endTime),
                Criteria.where("fromTime").lte(startTime).and("toTime").gte(startTime)));
        return mongoTemplate.exists(query,ClientException.class);
    }

    public long countSickExceptionsAfterDate(long citizenId,Date fromDate){
        Query query = Query.query(Criteria.where("clientId").is(citizenId).and("exceptionTypeId").is("2").and("fromTime").gte(fromDate));
        return mongoTemplate.count(query,ClientException.class);
    }

    public ClientException getSickExceptionForDate(long citizenId, Date date){
        Query query = Query.query(Criteria.where("clientId").is(citizenId).and("exceptionTypeId").is("2").and("fromTime").is(date));
        return mongoTemplate.findOne(query,ClientException.class);
    }


}
