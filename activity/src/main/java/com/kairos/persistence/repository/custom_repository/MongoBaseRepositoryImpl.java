package com.kairos.persistence.repository.custom_repository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.util.Assert;

import java.io.Serializable;

public class MongoBaseRepositoryImpl<T, ID extends Serializable>
extends SimpleMongoRepository<T, ID> implements MongoBaseRepository<T, ID> {
	private final MongoOperations mongoOperations;
	private final MongoEntityInformation<T, ID> entityInformation;
	public MongoBaseRepositoryImpl(MongoEntityInformation<T, ID>  entityInformation,
							  MongoOperations mongoOperations) {
		super(entityInformation, mongoOperations);
		// Keep the EntityManager around to used from the newly introduced methods.
		this.mongoOperations = mongoOperations;
		this.entityInformation=entityInformation;
	}
	@Override
	public T findOne(ID  id) {
		Assert.notNull(id, "The given id must not be null!");

		return mongoOperations.findById(id, entityInformation.getJavaType(), entityInformation.getCollectionName());
	}



}
