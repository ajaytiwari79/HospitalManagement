package com.kairos.persistance.repository.custom_repository;

import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface MongoBaseRepository<T extends MongoBaseEntity, ID extends Serializable> extends MongoRepository<T, ID> {
	// T findOne(ID id);


}
