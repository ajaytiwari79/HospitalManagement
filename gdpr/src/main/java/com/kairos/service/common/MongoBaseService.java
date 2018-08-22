package com.kairos.service.common;

import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.repository.common.MongoSequenceRepository;
import com.kairos.utils.DateUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.AppConstant.ORGANIZATION_ID;
import static com.kairos.constants.AppConstant.COUNTRY_ID;

/**
 * Created by Pankaj on 12/4/17.
 */
@Service
public class MongoBaseService {

    @Inject
    private MongoSequenceRepository mongoSequenceRepository;

    @Inject
    private MongoTemplate mongoTemplate;

    @Inject
    DB database;


    private static final Logger logger = LoggerFactory.getLogger(MongoBaseService.class);

    public <T extends MongoBaseEntity> List<T> getNextSequence(List<T> entities) {
        Assert.notNull(entities, "Entity must not be null!");
        Assert.notEmpty(entities, "Entity must not be Empty!");

        String collectionName = mongoTemplate.getCollectionName(entities.get(0).getClass());

        // Creating BulkWriteOperation object
        BulkWriteOperation bulkWriteOperation = database.getCollection(collectionName).initializeUnorderedBulkOperation();

        // Creating MongoConverter object (We need converter to convert Entity Pojo to BasicDbObjectCode)
        MongoConverter converter = mongoTemplate.getConverter();

        BasicDBObject dbObject;

        // Handling bulk write exceptions
        try {

            for (T entity : entities) {
                //  Get class name for sequence class
                String className = entity.getClass().getSimpleName();

                //  Set createdAt if entity don't have createdAt
                if (entity.getCreatedAt() == null) {
                    entity.setCreatedAt(DateUtils.getDate());
                }
                //  Set updatedAt time as current time
                entity.setUpdatedAt(DateUtils.getDate());


                if (entity.getId() == null) {
                    //  Set Id if entity don't have Id
                    entity.setId(mongoSequenceRepository.nextSequence(className));

                    dbObject = new BasicDBObject();

                    //  Converting entity object to BasicDBObject
                    converter.write(entity, dbObject);

                    //  Adding entity (BasicDBObject)
                    bulkWriteOperation.insert(dbObject);
                } else {

                    dbObject = new BasicDBObject();

                    //  Converting entity object to BasicDBObject
                    converter.write(entity, dbObject);

                    //  Creating BasicDbObjectCode for find query
                    BasicDBObject query = new BasicDBObject();

                    //  Adding query (find by ID)
                    query.put("_id", dbObject.get("_id"));

                    //  Replacing whole Object
                    bulkWriteOperation.find(query).replaceOne(dbObject);
                }
            }

            // Executing the Operation
            // bulkWriteOperation.execute();
            return entities;

        } catch (Exception ex) {
            logger.error("BulkWriteOperation Exception ::  ", ex);
            return null;
        }
    }


    public <T extends MongoBaseEntity> List<T> findMetaDataByNamesAndCountryId(Long countryId, Set<String> namesList, Class entity) {


        Assert.notNull(entity, "Entity must not be null!");
        Assert.notEmpty(namesList, "Entity must not be Empty!");
        Assert.notNull(countryId, "countryId must not be null");

        // collection name get collection name
        String collectionName = entity.getClass().getSimpleName();

        if (namesList.size() == 0) {
            throw new InvalidRequestException("list can't be empty");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and("deleted").is(false).and("name").in(namesList));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, entity);

    }


    public <T extends MongoBaseEntity> List<T> findMetaDataByNameAndUnitId(Long organizationId, Set<String> namesList, Class entity) {


        Assert.notNull(entity, "Entity must not be null!");
        Assert.notEmpty(namesList, "Entity must not be Empty!");
        Assert.notNull(organizationId, "organization Id must not be Null");

        // collection name get collection name
        String collectionName = entity.getClass().getSimpleName();

        if (namesList.size() == 0) {
            throw new InvalidRequestException("list can't be empty");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where(ORGANIZATION_ID).is(organizationId).and("deleted").is(false).and("name").in(namesList));
        query.collation(Collation.of("en").
                strength(Collation.ComparisonLevel.secondary()));
        return mongoTemplate.find(query, entity);

    }


    public <T extends MongoBaseEntity> T delete(T entity) {

        Assert.notNull(entity, "Entity must not be null!");
        //  Get class name for sequence class

        entity.setDeleted(true);
        entity.setUpdatedAt(DateUtils.getDate());
        mongoTemplate.save(entity);
        return entity;
    }


    public <T extends MongoBaseEntity> List<T> deleteAll(List<T> entities) {

        Assert.notNull(entities, "Entity must not be null!");
        //  Get class name for sequence class

        entities.forEach(entity -> {
            entity.setDeleted(true);
        });
        mongoTemplate.save(entities);
        return entities;
    }


    public Boolean remove(List<BigInteger> ids, Class entity) {

        Assert.notNull(entity, "Entity must not be null!");
        Assert.notEmpty(ids, "List cannot be empty");

        // Get class name for sequence class
        String className = entity.getClass().getSimpleName();

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(ids));


        mongoTemplate.remove(query, className);
        return true;

    }
}
