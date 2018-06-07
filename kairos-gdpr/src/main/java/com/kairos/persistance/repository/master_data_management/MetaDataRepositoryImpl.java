package com.kairos.persistance.repository.master_data_management;


import com.kairos.service.locale.LocaleService;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Collation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.kairos.constant.AppConstant.COUNTRY_ID;
import static com.kairos.constant.AppConstant.DELETED;


public class MetaDataRepositoryImpl<T, ID extends Serializable> extends SimpleMongoRepository<T, ID> implements MetaDataRepository<T, ID> {


    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;

    public MetaDataRepositoryImpl(MongoEntityInformation<T, ID> entityInformation,
                                  MongoOperations mongoOperations) {
        super(entityInformation, mongoOperations);
        // Keep the EntityManager around to used from the newly introduced methods.
        this.mongoOperations = mongoOperations;
        this.entityInformation = entityInformation;
    }

    private T object;


    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }


    @Override
    public List<T> getMetaDataListByName(Long countryId, Set<String> names) {
        Locale locale = LocaleContextHolder.getLocale();
        Collation collation = Collation.of(locale).strength(1);
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTRY_ID).is(countryId).and(DELETED).is(false).and("name").in(names)).collation(collation);
        return mongoOperations.find(query, entityInformation.getJavaType(), entityInformation.getCollectionName());

    }


}
