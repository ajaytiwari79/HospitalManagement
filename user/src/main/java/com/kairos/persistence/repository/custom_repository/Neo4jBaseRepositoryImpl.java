package com.kairos.persistence.repository.custom_repository;

import org.neo4j.ogm.session.Session;
import org.springframework.data.neo4j.repository.support.SimpleNeo4jRepository;
import org.springframework.data.neo4j.util.PagingAndSortingUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class Neo4jBaseRepositoryImpl<T, ID extends Serializable>
extends SimpleNeo4jRepository<T, ID> implements Neo4jBaseRepository<T, ID> {
	private static final int DEFAULT_QUERY_DEPTH = 1;
	private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";

	private final Class<T> clazz;
	private final Session session;

	/**
	 * Creates a new {@link SimpleNeo4jRepository} to manage objects of the given domain type.
	 *
	 * @param domainClass must not be {@literal null}.
	 * @param session must not be {@literal null}.
	 */
	public Neo4jBaseRepositoryImpl(Class<T> domainClass, Session session) {
		super(domainClass,session);
		this.clazz = domainClass;
		this.session = session;
	}

	@Override
	public T findOne(ID  id) {
		Assert.notNull(id, "The given id must not be null!");
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		return session.load(clazz, id);
	}

	@Override
	public T findOne(ID id, int depth) {
		Assert.notNull(id, "The given id must not be null!");
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		return session.load(clazz, id,depth);
	}



}
