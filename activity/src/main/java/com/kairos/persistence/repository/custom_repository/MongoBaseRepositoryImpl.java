package com.kairos.persistence.repository.custom_repository;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.common.UserInfo;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.common.MongoSequence;
import com.kairos.persistence.model.wta.templates.WTABaseRuleTemplate;
import com.kairos.utils.user_context.UserContext;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.client.MongoDatabase;
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

import javax.validation.Valid;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public class MongoBaseRepositoryImpl<T extends MongoBaseEntity, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements MongoBaseRepository<T, ID> {
	private final MongoOperations mongoOperations;
	private final MongoEntityInformation<T, ID> entityInformation;
	MongoDatabase mongoDatabase;
	private final Logger logger = LoggerFactory.getLogger(MongoBaseRepositoryImpl.class);

	/**
	 *  Sequence collection name prefix
	 * */
	private static final String SEQUENCE_POST_FIX = "Sequence";

	public MongoBaseRepositoryImpl(MongoEntityInformation<T, ID>  entityInformation,
							  MongoOperations mongoOperations) {
		super(entityInformation, mongoOperations);
		mongoDatabase = ((MongoTemplate) mongoOperations).getDb();
		// Keep the EntityManager around to used from the newly introduced methods.
		this.mongoOperations = mongoOperations;
		this.entityInformation=entityInformation;
	}
	@Override
	public T findOne(ID  id) {
		Assert.notNull(id, "The given id must not be null!");
		return mongoOperations.findById(id, entityInformation.getJavaType(), entityInformation.getCollectionName());
	}

	@Override
	public void safeDeleteById(ID id){
		Assert.notNull(id, "The given id must not be null!");
		mongoOperations.findAndModify(new Query(Criteria.where("_id").is(id)),Update.update("deleted",true),entityInformation.getJavaType(),entityInformation.getCollectionName());
	}

	@Override
	public <T extends MongoBaseEntity> void safeDelete(T object){
		Assert.notNull(object.getId(), "The given id must not be null!");
		mongoOperations.updateFirst(new Query(Criteria.where("_id").is(object.getId())),Update.update("deleted",true),entityInformation.getJavaType());
	}


	/**
	 * @decription This method in used to generate mongodb sequence
	 * @param sequenceName
	 * @return sequenceNumber
	 * */
	public BigInteger nextSequence(String sequenceName){
		/**
		 * adding sequence postfix into class name
		 * */
		sequenceName = sequenceName + SEQUENCE_POST_FIX;

		/**
		 *  Find query
		 * */
		String findQuery = "{'sequenceName':'"+sequenceName+"'}";
		/**
		 *  Update query
		 * */
		String updateQuery = "{'$inc':{'sequenceNumber':1}}";
		FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();

		/**
		 *  return updated value
		 * */
		findAndModifyOptions.returnNew(true);

		/**
		 *  create new if not exists
		 * */
		findAndModifyOptions.upsert(true);
		MongoSequence mongoSequence = mongoOperations.findAndModify(new BasicQuery(findQuery), new BasicUpdate(updateQuery), findAndModifyOptions, MongoSequence.class);
		return new BigInteger(mongoSequence.getSequenceNumber()+"");
	}

	@Override
	public <S extends T> S save(@Valid S entity) {
		Assert.notNull(entity, "Entity must not be null!");
		/**
		 *  Get class name for sequence class
		 * */
		String className = entity.getClass().getSimpleName();

		/**
		 *  Set Id if entity don't have Id
		 * */
		if(entity.getId() == null){
			if(entity.getClass().getSuperclass().equals(WTABaseRuleTemplate.class)){
				//Because WTABaseRuleTemplateDTO extends by All RuleTemaplete
				className = entity.getClass().getSuperclass().getSimpleName();
			}
			entity.setCreatedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
			entity.setCreatedAt(DateUtils.getDate());
			entity.setId(nextSequence(className));
		}else {
			entity.setLastModifiedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
		}
		/**
		 *  Set updatedAt time as current time
		 * */
		entity.setUpdatedAt(DateUtils.getDate());
		mongoOperations.save(entity);
		return entity;
	}



	public <T extends MongoBaseEntity> List<T> saveEntities(@Valid List<T> entities){
		Assert.notNull(entities, "Entity must not be null!");
		Assert.notEmpty(entities, "Entity must not be Empty!");

		String collectionName = mongoOperations.getCollectionName(entities.get(0).getClass());

		/**
		 *  Creating BulkWriteOperation object
		 * */

		BulkWriteOperation bulkWriteOperation= ((MongoTemplate) mongoOperations).getMongoDbFactory().getLegacyDb().getCollection(collectionName).initializeUnorderedBulkOperation();

		/**
		 *  Creating MongoConverter object (We need converter to convert Entity Pojo to BasicDbObject)
		 * */
		MongoConverter converter = mongoOperations.getConverter();

		BasicDBObject dbObject;

		/**
		 *  Handling bulk write exceptions
		 * */
		try{

			for (T entity: entities) {
				/**
				 *  Get class name for sequence class
				 * */
				String className = entity.getClass().getSimpleName();
				/**
				 *  Set updatedAt time as current time
				 * */
				entity.setUpdatedAt(DateUtils.getDate());


				if(entity.getId() == null){
					entity.setCreatedAt(DateUtils.getDate());
					/**
					 *  Set Id if entity don't have Id
					 * */
					if(entity.getClass().getSuperclass().equals(WTABaseRuleTemplate.class)){
						//Because WTABaseRuleTemplateDTO extends by All RuleTemaplete
						className = entity.getClass().getSuperclass().getSimpleName();
					}
					entity.setId(nextSequence(className));
					entity.setCreatedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
					dbObject = new BasicDBObject();

                    /*
                    *  Converting entity object to BasicDBObject
                    * */
					converter.write(entity, dbObject);

                    /*
                    *  Adding entity (BasicDBObject)
                    * */
					bulkWriteOperation.insert(dbObject);
				}else {
					entity.setLastModifiedBy(new UserInfo(UserContext.getUserDetails().getId(),UserContext.getUserDetails().getEmail(),UserContext.getUserDetails().getFullName()));
					dbObject = new BasicDBObject();

                    /*
                    *  Converting entity object to BasicDBObject
                    * */
					converter.write(entity, dbObject);

					/**
					 *  Creating BasicDbObject for find query
					 * */
					BasicDBObject query = new BasicDBObject();

					/**
					 *  Adding query (find by ID)
					 * */
					query.put("_id", dbObject.get("_id"));

					/**
					 *  Replacing whole Object
					 * */
					bulkWriteOperation.find(query).replaceOne(dbObject);
				}
			}

			/**
			 * Executing the Operation
			 * */
			bulkWriteOperation.execute();
			return entities;

		} catch(Exception ex){
			logger.error("BulkWriteOperation Exception ::  ", ex);
			return null;
		}
	}

}
