package com.planner.repository.common;

import com.kairos.commons.utils.DateUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.planner.domain.common.MongoBaseEntity;
import com.planner.domain.common.MongoSequence;
import com.planner.domain.constraint.common.Constraint;
import com.planner.domain.solverconfig.common.SolverConfig;
import com.planner.domain.wta.templates.WTABaseRuleTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

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
    private final Logger logger = LoggerFactory.getLogger(MongoBaseRepositoryImpl.class);

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

    /**
     *
     * @param name must not null
     * @param objectIdNotApplicableForCheck
     * @param checkForCountry  must not null
     * @param countryOrUnitId  must not null
     * @return
     */
    @Override
    public boolean isNameExistsById(String name, BigInteger objectIdNotApplicableForCheck, boolean checkForCountry,Long countryOrUnitId) {
        String applicableIdField = checkForCountry ? "countryId" : "unitId";
        Criteria criteria=Criteria.where("name").is(name).regex(Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE)).and(applicableIdField).is(countryOrUnitId);
        if (objectIdNotApplicableForCheck != null) criteria=criteria.and("_id").ne(objectIdNotApplicableForCheck);
        return mongoOperations.exists(new Query(criteria),entityInformation.getJavaType());
    }

    @Override
    public <T1 extends MongoBaseEntity> boolean safeDeleteByObject(T1 o) {
        mongoOperations.findAndModify(new Query(Criteria.where("_id").is(o.getId())), Update.update("deleted", true), o.getClass());
        return true;
    }

    @Override
    @Deprecated
    public List<T> findAllNotDeleted() {
        return mongoOperations.find(new Query(Criteria.where("deleted").exists(false)), entityInformation.getJavaType());
    }

    @Deprecated
    @Override
    public List<T> findAllSolverConfigNotDeletedByType(String solverConfigType) {
        String idType = "country".equalsIgnoreCase(solverConfigType) ? "countryId" : "unitId";
        return mongoOperations.find(new Query(Criteria.where("deleted").exists(false).andOperator(Criteria.where(idType).exists(true))), entityInformation.getJavaType());

    }
    @Override
    public List<T> findAllObjectsNotDeletedById(boolean checkForCountry,Long countryOrUnitId) {
        String applicableIdField = checkForCountry ? "countryId" : "unitId";
        Criteria criteria=Criteria.where("deleted").ne(true).and(applicableIdField).is(countryOrUnitId);
        return mongoOperations.find(new Query(criteria), entityInformation.getJavaType());
    }

     public T findByIdNotDeleted(BigInteger objectId){
        Criteria criteria=Criteria.where("_id").is(objectId).and("deleted").ne(true);
        return mongoOperations.findOne(new Query(criteria), entityInformation.getJavaType());
    }
/**********************************Custom Sequence Generator by this Application******************************************************/
    /**
     * @param sequenceName
     * @return sequenceNumber
     * @description This method in used to generate mongodb sequence
     * by our own Application , not by(default Mongo ObjectId)
     * during all types of save operations
     */
    public BigInteger nextSequence(String sequenceName,Integer sequenceSize) {
        //adding sequence postfix into class name
        sequenceName = sequenceName + SEQUENCE_POST_FIX;
        //Find query
        String findQuery = "{'sequenceName':'" + sequenceName + "'}";
        //Update query
        String updateQuery = "{'$inc':{'sequenceNumber':"+sequenceSize+"}}";
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        //return updated value
        findAndModifyOptions.returnNew(true);
        //create new if not exists
        findAndModifyOptions.upsert(true);

        MongoSequence mongoSequence = mongoOperations.findAndModify(new BasicQuery(findQuery), new BasicUpdate(updateQuery), findAndModifyOptions, MongoSequence.class);
        return new BigInteger(mongoSequence.getSequenceNumber() + "");
    }


    //======================save single object=========================
    public <T extends MongoBaseEntity> T saveObject(T entity) {
        Assert.notNull(entity, "Entity must not be null!");
        //Get class name for sequence class
        String className = entity.getClass().getSimpleName();
        //By Pass, to save both type of solverConfig in same Collection
        if (entity instanceof SolverConfig) className = SolverConfig.class.getSimpleName();
        if(entity instanceof Constraint) className = Constraint.class.getSimpleName();
        //Set Id if entity don't have Id
        if (entity.getId() == null) entity.setId(nextSequence(className,1));
        //Set createdAt if entity don't have createdAt
        if (entity.getCreatedAt() == null) entity.setCreatedAt(DateUtils.getDate());
        //Set updatedAt time as current time
        entity.setUpdatedAt(DateUtils.getDate());
        mongoOperations.save(entity);
        return entity;
    }

    //==========================save List of objects=======================
    public <T extends MongoBaseEntity> List<T> saveObjectList(List<T> objects){
        Assert.notEmpty(objects,"List Can't be empty or null");
        //Get class name for sequence class
        String collectionName = mongoOperations.getCollectionName(objects.get(0).getClass());
        String className = objects.get(0).getClass().getSimpleName();
        //By Pass, to save both type of classes in same Collection
        if (objects.get(0) instanceof SolverConfig) className = SolverConfig.class.getSimpleName();
        if(objects.get(0) instanceof Constraint) className = Constraint.class.getSimpleName();

        // Creating BulkWriteOperation object
        BulkWriteOperation bulkWriteOperation= ((MongoTemplate) mongoOperations).getMongoDbFactory().getLegacyDb().getCollection(collectionName).initializeUnorderedBulkOperation();
        //Creating MongoConverter object (We need converter to convert Entity Pojo to BasicDbObject)
        MongoConverter converter = mongoOperations.getConverter();
        BasicDBObject dbObject;

        //Get last saved sequence id for this class type
        BigInteger lastSequenece=nextSequence(className,0);
        try {
            for (int i = 0; i < objects.size(); i++) {  //Set id's
                T entity = objects.get(i);
                if (entity.getId() == null) {
                    lastSequenece=lastSequenece.add(new BigInteger("1"));
                    entity.setId(lastSequenece);
                    dbObject = new BasicDBObject();
                    converter.write(entity, dbObject);
                    bulkWriteOperation.insert(dbObject);
                } else {
                    dbObject = new BasicDBObject();
                    converter.write(entity, dbObject);
                    BasicDBObject query = new BasicDBObject();
                    query.put("_id", dbObject.get("_id"));
                    bulkWriteOperation.find(query).replaceOne(dbObject);
                }
            }
            bulkWriteOperation.execute();
            nextSequence(className,objects.size());
            return objects;
        }catch(Exception ex){
            logger.error("BulkWriteOperation Exception ::  ", ex);
            return null;
        }
    }

}
