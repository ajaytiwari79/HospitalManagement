package com.kairos.persistence.repository.common;
import com.kairos.persistence.model.common.MongoSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;

/**
 * Created by pankaj on 4/3/17.
 */
@Repository
public class MongoSequenceRepository {

    @Inject
    MongoOperations mongoOperations;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     *  Sequence collection name prefix
     * */
    private static final String SEQUENCE_POST_FIX = "Sequence";

    /**
     * @decription This method in used to generate mongodb sequence
     * @param sequenceName
     * @return sequenceNumber
     * */
    public BigInteger nextSequence(String sequenceName){
        //adding sequence postfix into class name
        sequenceName = sequenceName + SEQUENCE_POST_FIX;

        // Find query
        String findQuery = "{'sequenceName':'"+sequenceName+"'}";

        //Update query
        String updateQuery = "{'$inc':{'sequenceNumber':1}}";
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();

        // return updated value
        findAndModifyOptions.returnNew(true);

        // create new if not exists
        findAndModifyOptions.upsert(true);
        MongoSequence mongoSequence = mongoOperations.findAndModify(new BasicQuery(findQuery), new BasicUpdate(updateQuery), findAndModifyOptions, MongoSequence.class);
        return new BigInteger(mongoSequence.getSequenceNumber()+"");
    }


    public MongoSequenceRepository(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }
}
