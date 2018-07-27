package com.kairos.persistence.repository.custom_repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
@NoRepositoryBean
public interface MongoBaseRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {
	 T findOne(ID id);

}
