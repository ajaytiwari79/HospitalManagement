package com.kairos.persistance.repository.master_data_management;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


@NoRepositoryBean
public interface MetaDataRepository<T,ID extends Serializable>  extends MongoRepository<T, ID> {

    List<T> getMetaDataListByName(Long countryId, Set<String> names);
}
