package com.kairos.persistance.repository.master_data_management;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;


@Repository
public interface MetaDataRepository<T, ID extends Serializable> extends MongoRepository<T,ID> {

    T getMetedataByIdAndDeletedIsFalse(Long countryId, ID id);


    T getMetaDataByName(Long countryId, String name);


    List<T> getMetaDataListByCountryId(Long countryId);


    List<T> getMetaDataListByCountryIdAndName(Long countryId, Set<String> name);


}
