package com.kairos.persistence.repository.custom_repository;

import com.kairos.commons.audit_logging.AuditLogging;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.session.Session;
import org.springframework.data.neo4j.repository.support.SimpleNeo4jRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;

@Transactional(readOnly = true)
public class Neo4jBaseRepositoryImpl<T extends UserBaseEntity, ID extends Serializable>
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
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		return session.load(clazz, id);
	}

	@Override
	public T findByIdAndDeletedFalse(ID  id) {
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		String query = "MATCH (n:"+this.clazz.getSimpleName()+"{deleted:false}) where id(n)={id} RETURN n";
		Map<String,Object> queryParam = new HashMap<>();
		queryParam.put("id",id);
		return session.queryForObject(this.clazz,query,queryParam);
	}

	@Override
	public T findOne(ID id, int depth) {
		Assert.notNull(id, ID_MUST_NOT_BE_NULL);
		return session.load(clazz, id,depth);
	}

	@Override
	public List<T> findAllById(List<ID> ids){
		return new ArrayList<>(session.loadAll(clazz, (Collection<ID>) ids, 1));
	}

	@Override
	public List<T> findAllById(List<ID> ids, int depth) {
		return new ArrayList<>(session.loadAll(clazz, (Collection<ID>) ids, depth));
	}

	@Transactional
	@Override
	public <S extends T> S save(S entity) {
		S oldEntity = null;
		boolean validClass = !entity.getClass().isAnnotationPresent(RelationshipEntity.class);
		if(validClass && isNotNull(entity.getId())){
			oldEntity = (S)this.findById((ID)entity.getId()).orElse(null);
		}else {
			if(validClass) {
				try {
					oldEntity = (S) entity.getClass().newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		session.save(entity);
		if(validClass) {
			AuditLogging.doAudit(oldEntity, entity);
		}
		return entity;
	}

	@Transactional
	@Override
	public <S extends T> Iterable<S> saveAll(Iterable<S> entities) {
		for (S entity : entities) {
			save(entity);
		}
		return entities;
	}

	@Transactional
	@Override
	public <S extends T> S save(S entity, int depth) {
		S oldEntity = null;
		if(isNotNull(entity.getId())){
			oldEntity = (S)this.findById((ID)entity.getId(),depth).orElse(null);
		}else {
			try {
				oldEntity = (S)entity.getClass().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		session.save(entity, depth);
		return entity;
	}

	@Transactional
	@Override
	public <S extends T> Iterable<S> save(Iterable<S> ses, int depth) {
		for (S entity : ses) {
			save(entity,depth);
		}
		return ses;
	}


}
