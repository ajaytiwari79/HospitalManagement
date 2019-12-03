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

    public static final String CLIENT_ID = "clientId";
    public static final String FROM_TIME = "fromTime";
    public static final String TO_TIME = "toTime";
    public static final String EXCEPTION_TYPE_ID = "exceptionTypeId";
    @Inject
    private MongoTemplate mongoTemplate;

    public List<ClientException> getExceptionBetweenTaskDates(long citizenId, Date taskStartTime, Date taskEndTime) {


        Query query = Query.query(Criteria.where(CLIENT_ID).is(citizenId).orOperator(Criteria.where(FROM_TIME).lte(taskEndTime).and(TO_TIME).gte(taskEndTime),
                Criteria.where(FROM_TIME).lte(taskStartTime).and(TO_TIME).gte(taskStartTime)));
        return mongoTemplate.find(query, ClientException.class);

    }

    public boolean isExceptionExistBetweenDate(long citizenId,Date startTime,Date endTime,BigInteger clientExceptionId){

        Query query = Query.query(Criteria.where(CLIENT_ID).is(citizenId).and("id").ne(clientExceptionId).orOperator(Criteria.where(FROM_TIME).lte(endTime).and(TO_TIME).gte(endTime),
                Criteria.where(FROM_TIME).lte(startTime).and(TO_TIME).gte(startTime)));
        return mongoTemplate.exists(query,ClientException.class);
    }


    public boolean isExceptionExistBetweenDate(List<Long> citizenIds, Date startTime, Date endTime, List<BigInteger> clientExceptionId) {
        Query query = Query.query(Criteria.where(CLIENT_ID).in(citizenIds).and("id").nin(clientExceptionId).orOperator(Criteria.where(FROM_TIME).lte(endTime).and(TO_TIME).gte(endTime),
                Criteria.where(FROM_TIME).lte(startTime).and(TO_TIME).gte(startTime)));
        return mongoTemplate.exists(query,ClientException.class);
    }

    public boolean isExceptionTypeExistBetweenDate(List<Long> citizenId,Date startTime,Date endTime,BigInteger exceptionTypeId){

        Query query = Query.query(Criteria.where(CLIENT_ID).in(citizenId).and(EXCEPTION_TYPE_ID).is(exceptionTypeId).orOperator(Criteria.where(FROM_TIME).lte(endTime).and(TO_TIME).gte(endTime),
                Criteria.where(FROM_TIME).lte(startTime).and(TO_TIME).gte(startTime)));
        return mongoTemplate.exists(query,ClientException.class);
    }

    public long countSickExceptionsAfterDate(long citizenId,Date fromDate){
        Query query = Query.query(Criteria.where(CLIENT_ID).is(citizenId).and(EXCEPTION_TYPE_ID).is("2").and(FROM_TIME).gte(fromDate));
        return mongoTemplate.count(query,ClientException.class);
    }

    public ClientException getSickExceptionForDate(long citizenId, Date date){
        Query query = Query.query(Criteria.where(CLIENT_ID).is(citizenId).and(EXCEPTION_TYPE_ID).is("2").and(FROM_TIME).is(date));
        return mongoTemplate.findOne(query,ClientException.class);
    }


}
