package com.planner.repository.common;

import com.kairos.commons.utils.DateUtils;
import com.planner.domain.MongoBaseEntity;
import com.planner.domain.common.MongoSequence;
import com.planner.domain.common.solverconfig.SolverConfig;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * This class will be loaded by {@link org.springframework.data.mongodb.repository.config.EnableMongoRepositories}
 * which is configured in planner Main class
 * as an instance of Proxy class into every {@Repository} annotated interface
 *
 * @param <T>
 * @param <ID>
 */
public class MongoBaseRepositoryImpl<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements MongoBaseRepository<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;
    //Sequence collection name prefix
    private static final String SEQUENCE_POST_FIX = "Sequence";

    /*Constructor*/
    public MongoBaseRepositoryImpl(MongoEntityInformation<T, ID> entityInformation, MongoOperations mongoOperations) {
        super(entityInformation, mongoOperations);
        // Keep the EntityManager around to used from the newly introduced methods.
        this.mongoOperations = mongoOperations;
        this.entityInformation = entityInformation;
    }


    /*****************************************Parent class implementation{@link MongoBaseRepository}*****************************************/
    @Override //not required yet
    public Optional<T> findByKairosId(BigInteger kairosId) {
        return Optional.empty();
    }

    @Override
    public boolean safeDeleteById(BigInteger id) {
        mongoOperations.findAndModify(new Query(Criteria.where("_id").is(id)), Update.update("deleted", true), entityInformation.getJavaType());
        return true;
    }

    @Override
    public boolean isNameExists(String name, BigInteger solverConfigIdNotApplicableForCheck) {
        boolean result;
        if (solverConfigIdNotApplicableForCheck != null)
            result = mongoOperations.exists(new Query(Criteria.where("name").is(name).andOperator(Criteria.where("_id").ne(solverConfigIdNotApplicableForCheck))), entityInformation.getJavaType());
        else
            result = mongoOperations.exists(new Query(Criteria.where("name").is(name)), entityInformation.getJavaType());
        return result;
    }

    @Override
    public <T1 extends MongoBaseEntity> boolean safeDeleteByObject(T1 o) {
        mongoOperations.findAndModify(new Query(Criteria.where("_id").is(o.getId())), Update.update("deleted", true), o.getClass());
        return true;
    }

    @Override
    public List<T> findAllNotDeleted() {
        return mongoOperations.find(new Query(Criteria.where("deleted").exists(false)), entityInformation.getJavaType());
    }

    @Override
    public List<T> findAllSolverConfigNotDeletedByType(String solverConfigType) {
        List<T> result;
        if ("country".equalsIgnoreCase(solverConfigType))
            result = mongoOperations.find(new Query(Criteria.where("deleted").exists(false).andOperator(Criteria.where("countryId").exists(true))), entityInformation.getJavaType());
        else
            result = mongoOperations.find(new Query(Criteria.where("deleted").exists(false).andOperator(Criteria.where("unitId").exists(true))), entityInformation.getJavaType());
        return result;
    }
/**********************************Custom Sequence Generator by this Application******************************************************/
    /**
     * @param sequenceName
     * @return sequenceNumber
     * @description This method in used to generate mongodb sequence
     * by our own Application , not by(default Mongo ObjectId)
     * during all types of save operations
     */
    public BigInteger nextSequence(String sequenceName) {
        //adding sequence postfix into class name
        sequenceName = sequenceName + SEQUENCE_POST_FIX;
        //Find query
        String findQuery = "{'sequenceName':'" + sequenceName + "'}";
        //Update query
        String updateQuery = "{'$inc':{'sequenceNumber':1}}";
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        //return updated value
        findAndModifyOptions.returnNew(true);
        //create new if not exists
        findAndModifyOptions.upsert(true);

        MongoSequence mongoSequence = mongoOperations.findAndModify(new BasicQuery(findQuery), new BasicUpdate(updateQuery), findAndModifyOptions, MongoSequence.class);
        return new BigInteger(mongoSequence.getSequenceNumber() + "");
    }

    /**
     * This method will save entity with our own provided Id
     * which we will generate by using Sequene Document in mongo
     *
     * @param entity
     * @param <T>
     * @return
     */

    public <T extends MongoBaseEntity> T saveObject(T entity) {
        Assert.notNull(entity, "Entity must not be null!");
        //Get class name for sequence class
        String className = entity.getClass().getSimpleName();
        //By Pass, to save both type of solverConfig in same Collection
        if (entity instanceof SolverConfig) className = SolverConfig.class.getSimpleName();
        //Set Id if entity don't have Id
        if (entity.getId() == null) entity.setId(nextSequence(className));
        //Set createdAt if entity don't have createdAt
        if (entity.getCreatedAt() == null) entity.setCreatedAt(DateUtils.getDate());
        //Set updatedAt time as current time
        entity.setUpdatedAt(DateUtils.getDate());
        mongoOperations.save(entity);
        return entity;
    }
}
