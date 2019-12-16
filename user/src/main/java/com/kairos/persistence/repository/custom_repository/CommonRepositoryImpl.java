package com.kairos.persistence.repository.custom_repository;

import com.kairos.service.kpermissions.PermissionService;
import org.neo4j.ogm.context.GraphEntityMapper;
import org.neo4j.ogm.context.MappingContext;
import org.neo4j.ogm.cypher.query.DefaultGraphModelRequest;
import org.neo4j.ogm.cypher.query.Pagination;
import org.neo4j.ogm.cypher.query.PagingAndSortingQuery;
import org.neo4j.ogm.cypher.query.SortOrder;
import org.neo4j.ogm.metadata.ClassInfo;
import org.neo4j.ogm.metadata.FieldInfo;
import org.neo4j.ogm.metadata.reflect.ReflectionEntityInstantiator;
import org.neo4j.ogm.model.GraphModel;
import org.neo4j.ogm.request.GraphModelRequest;
import org.neo4j.ogm.response.Response;
import org.neo4j.ogm.session.LoadStrategy;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.session.request.strategy.LoadClauseBuilder;
import org.neo4j.ogm.session.request.strategy.QueryStatements;
import org.neo4j.ogm.session.request.strategy.impl.*;
import org.neo4j.ogm.utils.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Pattern;
@Repository
public class CommonRepositoryImpl implements CommonRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRepositoryImpl.class);

    @Inject
    private SessionFactory sessionFactory;

    private static final Pattern WRITE_CYPHER_KEYWORDS = Pattern.compile("\\b(CREATE|MERGE|SET|DELETE|REMOVE|DROP)\\b");

    /***
     * This method is used for Query without transaction
     * so please don't use this method for any database action
     ***/
    @Override
    public <T, ID extends Serializable> Collection<T> findByIds(Class<T> type, Collection<ID> ids,int depth){
        return loadAll(type,ids,new SortOrder(),null,depth);
    }


    public <T, ID extends Serializable> Collection<T> loadAll(Class<T> type, Collection<ID> ids, SortOrder sortOrder,
                                                              Pagination pagination, int depth) {

        String entityLabel = sessionFactory.metaData().entityType(type.getName());
        if (entityLabel == null) {
            LOGGER.warn("Unable to find database label for entity " + type.getName()
                    + " : no results will be returned. Make sure the class is registered, "
                    + "and not abstract without @NodeEntity annotation");
        }
        QueryStatements<ID> queryStatements = queryStatementsFor(type, depth);

        PagingAndSortingQuery qry = queryStatements.findAllByType(entityLabel, ids, depth)
                .setSortOrder(sortOrder)
                .setPagination(pagination);

        GraphModelRequest request = new DefaultGraphModelRequest(qry.getStatement(), qry.getParameters());
        try (Response<GraphModel> response = sessionFactory.getDriver().request().execute(request)) {
            Iterable<T> mapped = new GraphEntityMapper(sessionFactory.metaData(), new MappingContext(sessionFactory.metaData()), new ReflectionEntityInstantiator(sessionFactory.metaData())).map(type, response);

            if (sortOrder.sortClauses().isEmpty()) {
                return sortResultsByIds(type, ids, mapped);
            }
            Set<T> results = new LinkedHashSet<>();
            for (T entity : mapped) {
                if (includeMappedEntity(ids, entity)) {
                    results.add(entity);
                }
            }
            return results;
        }
    }

    private <T, ID extends Serializable> boolean includeMappedEntity(Collection<ID> ids, T mapped) {

        final ClassInfo classInfo = sessionFactory.metaData().classInfo(mapped);
        final FieldInfo primaryIndexField = classInfo.primaryIndexField();

        if (primaryIndexField != null) {
            final Object primaryIndexValue = primaryIndexField.read(mapped);
            if (ids.contains(primaryIndexValue)) {
                return true;
            }
        }
        Object id = EntityUtils.identity(mapped, sessionFactory.metaData());
        return ids.contains(id);
    }

    private <T, ID extends Serializable> Set<T> sortResultsByIds(Class<T> type, Collection<ID> ids,
                                                                 Iterable<T> mapped) {
        Map<ID, T> items = new HashMap<>();
        ClassInfo classInfo = sessionFactory.metaData().classInfo(type.getName());

        FieldInfo idField = classInfo.primaryIndexField();
        if (idField == null) {
            idField = classInfo.identityField();
        }

        for (T t : mapped) {
            Object id = idField.read(t);
            if (id != null) {
                items.put((ID) id, t);
            }
        }

        Set<T> results = new LinkedHashSet<>();
        for (ID id : ids) {
            T item = items.get(id);
            if (item != null) {
                results.add(item);
            }
        }
        return results;
    }

    public <T, ID extends Serializable> QueryStatements<ID> queryStatementsFor(Class<T> type, int depth) {
        final FieldInfo fieldInfo = sessionFactory.metaData().classInfo(type.getName()).primaryIndexField();
        String primaryIdName = fieldInfo != null ? fieldInfo.property() : null;
        if (sessionFactory.metaData().isRelationshipEntity(type.getName())) {
            return new RelationshipQueryStatements<>(primaryIdName, loadRelationshipClauseBuilder(depth, LoadStrategy.PATH_LOAD_STRATEGY));
        } else {
            return new NodeQueryStatements<>(primaryIdName, loadNodeClauseBuilder(depth,LoadStrategy.PATH_LOAD_STRATEGY));
        }
    }

    private LoadClauseBuilder loadRelationshipClauseBuilder(int depth, LoadStrategy loadStrategy) {
        if (depth < 0) {
            throw new IllegalArgumentException("Can't load unlimited depth for relationships");
        }

        switch (loadStrategy) {
            case PATH_LOAD_STRATEGY:
                return new PathRelationshipLoadClauseBuilder();

            case SCHEMA_LOAD_STRATEGY:
                return new SchemaRelationshipLoadClauseBuilder(sessionFactory.metaData().getSchema());

            default:
                throw new IllegalStateException("Unknown loadStrategy " + loadStrategy);
        }
    }

    private LoadClauseBuilder loadNodeClauseBuilder(int depth,LoadStrategy loadStrategy) {
        if (depth < 0) {
            return new PathNodeLoadClauseBuilder();
        }

        switch (loadStrategy) {
            case PATH_LOAD_STRATEGY:
                return new PathNodeLoadClauseBuilder();

            case SCHEMA_LOAD_STRATEGY:
                return new SchemaNodeLoadClauseBuilder(sessionFactory.metaData().getSchema());

            default:
                throw new IllegalStateException("Unknown loadStrategy " + loadStrategy);
        }
    }
}
