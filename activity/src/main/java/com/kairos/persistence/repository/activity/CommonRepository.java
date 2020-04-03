package com.kairos.persistence.repository.activity;

import com.kairos.dto.activity.counter.enums.CounterType;
import com.kairos.persistence.model.counter.Counter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Repository
public class CommonRepository {
    @Inject
    private MongoTemplate mongoTemplate;

    public <T, ID> List<T> findAllByIds(Class clazz, Collection<ID> ids) {
        Query query = new Query(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query,clazz);
    }
}
