package com.kairos.persistence.repository.custom_repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface Neo4jBaseRepository<T, ID extends Serializable> extends Neo4jRepository<T, ID> {
	 T findOne(ID id);
	 T findOne(ID id,int depth);
	 List<T> findAllById(List<ID> ids);

}
