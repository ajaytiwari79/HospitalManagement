package com.planner.repository.common;

import com.planner.domain.MongoBaseEntity;
import com.planner.domain.common.MongoSequence;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Optional;

public class MongoBaseRepositoryImpl <T, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements MongoBaseRepository<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;
    /**
     *  Sequence collection name prefix
     * */
    private static final String SEQUENCE_POST_FIX = "Sequence";



    /*****************************************Constructor*****************************************/
    public MongoBaseRepositoryImpl(MongoEntityInformation<T, ID>  entityInformation, MongoOperations mongoOperations) {
        super(entityInformation, mongoOperations);
        // Keep the EntityManager around to used from the newly introduced methods.
        this.mongoOperations = mongoOperations;
        this.entityInformation=entityInformation;
    }


    /*****************************************Parent class implementation*****************************************/
    @Override
    public Optional<T> findByKairosId(BigInteger kairosId) {
        return Optional.empty();
    }

    @Override
    public boolean safeDeleteById(BigInteger id,Class clasName) {
        Criteria  criteria=Criteria.where("_id").is(id);
        Query query = new Query(criteria);
        Update update = Update.update("deleted",true);
        mongoOperations.findAndModify(query,update,clasName);
        return true;
    }

    @Override
    public <T1 extends MongoBaseEntity> boolean safeDeleteByObject(T1 o) {
        Criteria  criteria=Criteria.where("_id").is(o.getId());
        Query query = new Query(criteria);
        Update update = Update.update("deleted",true);
        mongoOperations.findAndModify(query,update,o.getClass());
        return true;
    }


/**********************************Custom Sequence Generator******************************************************/
    /**
     * @decription This method in used to generate mongodb sequence
     * @param sequenceName
     * @return sequenceNumber
     * */
    public BigInteger nextSequence(String sequenceName){
        //adding sequence postfix into class name
        sequenceName = sequenceName + SEQUENCE_POST_FIX;
        //Find query
        String findQuery = "{'sequenceName':'"+sequenceName+"'}";
        //Update query
        String updateQuery = "{'$inc':{'sequenceNumber':1}}";
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        //return updated value
        findAndModifyOptions.returnNew(true);
         //create new if not exists
        findAndModifyOptions.upsert(true);

        MongoSequence mongoSequence = mongoOperations.findAndModify(new BasicQuery(findQuery), new BasicUpdate(updateQuery), findAndModifyOptions, MongoSequence.class);
        return new BigInteger(mongoSequence.getSequenceNumber()+"");
    }
}
