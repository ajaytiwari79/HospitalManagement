package com.planner.repository.activity;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;

@Repository
public class ActivityKairosRepository {

    @Inject
    @Qualifier("Activity")
    private MongoTemplate mongoTemplateActivity;

    public boolean m1()
    {

       return mongoTemplateActivity.exists(new Query(Criteria.where("_id").is(new BigInteger("1002"))),"activities");
       /* System.out.println(exists);*/
    }
}
